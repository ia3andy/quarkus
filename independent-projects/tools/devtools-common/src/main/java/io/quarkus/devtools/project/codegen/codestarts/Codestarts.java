package io.quarkus.devtools.project.codegen.codestarts;

import static io.quarkus.platform.descriptor.loader.json.ResourceLoaders.toResourceNameWalker;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.maven.Maven;
import io.fabric8.maven.merge.SmartModelMerger;
import io.quarkus.dependencies.Extension;
import io.quarkus.devtools.project.extensions.Extensions;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import io.quarkus.qute.Engine;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.model.Model;

public class Codestarts {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
            .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    private static final String CODESTARTS_DIR_DEFAULT = "codestarts/default";

    public static CodestartProject resolveCodestartProject(final QuarkusPlatformDescriptor descriptor,
            final CodestartInput input) throws IOException {
        final String buildToolCodeStart = "buildtool-" + input.getData().getOrDefault("buildtool", "maven");
        final Set<String> enabledCodestarts = Stream.concat(descriptor.getExtensions().stream()
                .filter(e -> input.getExtensions().contains(Extensions.toKey(e)))
                .map(Extension::getCodestart), Stream.of(buildToolCodeStart))
                .collect(Collectors.toSet());

        final Collection<Codestart> defaultCodestarts = loadDefaultCodestarts(descriptor);
        final Codestart project = findEnabledCodestartOfTypeOrDefault(defaultCodestarts, enabledCodestarts,
                CodestartSpec.Type.PROJECT);
        final Codestart buildTool = findEnabledCodestartOfTypeOrDefault(defaultCodestarts, enabledCodestarts,
                CodestartSpec.Type.BUILDTOOL);
        final Codestart language = findEnabledCodestartOfTypeOrDefault(defaultCodestarts, enabledCodestarts,
                CodestartSpec.Type.LANGUAGE);
        final Codestart config = findEnabledCodestartOfTypeOrDefault(defaultCodestarts, enabledCodestarts,
                CodestartSpec.Type.CONFIG);
        final List<Codestart> extensions = defaultCodestarts.stream()
                .filter(c -> CodestartSpec.Type.CODESTART.equals(c.getSpec().getType())
                        && (c.getSpec().isDefault() || enabledCodestarts.contains(c.getSpec().getName())))
                .collect(Collectors.toList());

        return new CodestartProject(project, buildTool, language, config, extensions, input.getData());
    }

    public static Collection<Codestart> loadDefaultCodestarts(final QuarkusPlatformDescriptor descriptor) throws IOException {
        return descriptor.loadResourcePath(CODESTARTS_DIR_DEFAULT, path -> toResourceNameWalker(CODESTARTS_DIR_DEFAULT, path))
                .filter(n -> n.matches(".*/codestart\\.ya?ml$"))
                .map(n -> {
                    try {
                        final CodestartSpec spec = mapper.readerFor(CodestartSpec.class)
                                .readValue(descriptor.getTemplate(n));
                        return new Codestart(n.replaceAll("/?codestart\\.ya?ml", ""), spec);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }).collect(Collectors.toList());
    }

    static void processCodestartProject(final QuarkusPlatformDescriptor descriptor, final Engine engine,
            final CodestartProject codestartProject, final Path targetDirectory) throws IOException {
        final String languageName = codestartProject.getLanguageName();
        final Map<String, Object> sharedData = codestartProject.getSharedData();
        codestartProject.getAllCodestartsStream()
                .forEach(c -> processCodestart(descriptor, engine, c, languageName, targetDirectory, sharedData));
    }

    static void processCodestart(final QuarkusPlatformDescriptor descriptor, final Engine engine, final Codestart codestart,
            final String language, final Path targetDirectory, final Map<String, Object> sharedData) {
        try {
            descriptor.loadResourcePath(codestart.getResourceName(), p -> resolveDirectoriesToProcessAsStream(p, language))
                    .forEach(p -> processCodestartCodeDirectory(engine, p, targetDirectory, mergeData(codestart, sharedData)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    static Stream<Path> resolveDirectoriesToProcessAsStream(final Path sourceDirectory, final String languageName)
            throws IOException {
        if (!Files.isDirectory(sourceDirectory)) {
            throw new IllegalStateException("Codestart sourceDirectory is not a directory: " + sourceDirectory);
        }
        return Stream.of("core", "example")
                .map(sourceDirectory::resolve)
                .filter(Files::isDirectory)
                .flatMap(p -> Stream.of(p.resolve("base"), p.resolve(languageName)))
                .filter(Files::isDirectory);

    }

    static void processCodestartCodeDirectory(final Engine engine, final Path sourceDirectory,
            final Path targetProjectDirectory,
            final Object data) {
        try {
            Files.createDirectories(targetProjectDirectory);
            Files.walk(sourceDirectory)
                    .filter(path -> !path.equals(sourceDirectory))
                    .forEach(path -> {
                        try {
                            final Path relativePath = sourceDirectory.relativize(path);
                            if (Files.isDirectory(path)) {
                                Files.createDirectories(relativePath);
                            } else {
                                final String fileName = relativePath.getFileName().toString();
                                final Path targetPath = targetProjectDirectory.resolve(relativePath);
                                if (fileName.contains(".part")) {
                                    // TODO we need some kind of PartCombiner interface with a "matcher"
                                    if (fileName.equals("pom.part.qute.xml")) {
                                        processMavenPart(engine, path, data, targetPath.getParent().resolve("pom.xml"));
                                    } else if (fileName.equals("README.part.qute.md")) {
                                        processReadmePart(engine, path, data, targetPath.getParent().resolve("README.md"));
                                    } else {
                                        throw new IllegalStateException("Unsupported part file: " + path);
                                    }
                                } else if (fileName.contains(".qute")) {
                                    // Template file
                                    processQuteFile(engine, path, data,
                                            targetPath.getParent().resolve(fileName.replace(".qute", "")));
                                } else {
                                    // Static file
                                    processStaticFile(path, targetPath);
                                }
                            }
                        } catch (final IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    private static void processReadmePart(Engine engine, Path path, Object data, Path targetPath) throws IOException {
        if (!Files.exists(targetPath)) {
            throw new IllegalStateException(
                    "Using .part is not possible when the target file does not exist already: " + path + " -> " + targetPath);
        }
        final String renderedContent = processQuteContent(engine, path, data);
        Files.write(targetPath, renderedContent.getBytes(), StandardOpenOption.APPEND);
    }

    private static void processMavenPart(Engine engine, Path path, Object data, Path targetPath) throws IOException {
        if (!Files.exists(targetPath)) {
            throw new IllegalStateException(
                    "Using .part is not possible when the target file does not exist already: " + path + " -> " + targetPath);
        }
        final Model targetModel = Maven.readModel(targetPath);
        final SmartModelMerger merger = new SmartModelMerger();
        final String content = processQuteContent(engine, path, data);
        final Model sourceModel = Maven.readModel(new StringReader(content));
        merger.merge(targetModel, sourceModel, true, null);
        Maven.writeModel(targetModel);
    }

    private static void processStaticFile(Path path, Path targetPath) throws IOException {
        Files.createDirectories(targetPath.getParent());
        Files.copy(path, targetPath);
    }

    private static void processQuteFile(Engine engine, Path path, Object data, Path targetPath) throws IOException {
        final String renderedContent = processQuteContent(engine, path, data);
        Files.createDirectories(targetPath.getParent());
        Files.write(targetPath, renderedContent.getBytes(), StandardOpenOption.CREATE_NEW);
    }

    private static String processQuteContent(Engine engine, Path path, Object data) throws IOException {
        final String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        return engine.parse(content).data(data).render();
    }

    private static Codestart findEnabledCodestartOfTypeOrDefault(final Collection<Codestart> codestarts,
            final Set<String> enabledCodestarts, final CodestartSpec.Type type) {
        return codestarts.stream()
                .filter(c -> c.getSpec().getType().equals(type)
                        && (c.getSpec().isDefault() || enabledCodestarts.contains(c.getSpec().getName())))
                .min(Comparator.comparing(c -> c.getSpec().isDefault()))
                .orElseThrow(() -> new IllegalStateException("No matching codestart of type " + type + " has been found"));
    }

    private static Map<String, Object> mergeData(final Codestart codestart, final Map<String, Object> sharedData) {
        return mergeMaps(Stream.of(codestart.getSpec().getData().getLocal(), sharedData));
    }

    static Map<String, Object> mergeMaps(final Stream<Map<String, Object>> stream) {

        // TODO we will need a deep merge here
        return stream
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
