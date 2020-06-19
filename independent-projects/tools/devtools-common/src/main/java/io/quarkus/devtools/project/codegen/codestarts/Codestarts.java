package io.quarkus.devtools.project.codegen.codestarts;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.dependencies.Extension;
import io.quarkus.devtools.project.extensions.Extensions;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import io.quarkus.qute.Engine;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Codestarts {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
            .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

    public static CodestartProject resolveCodestartProject(final QuarkusPlatformDescriptor descriptor, final CodestartInput input) throws IOException {
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
        return descriptor.walkDir("codestarts/default", ps -> ps.filter(n -> n.matches(".*/codestart\\.ya?ml$"))
                .map(n -> {
                    try {
                        final CodestartSpec spec = mapper.readerFor(CodestartSpec.class)
                                .readValue(descriptor.getTemplate(n));
                        return new Codestart(n.replaceAll("/?codestart\\.ya?ml", ""), spec);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }).collect(Collectors.toList()));
    }

    static void processCodestartDirectory(final Engine engine, final Path sourceDirectory, final Path targetDirectory,
            final Object data) throws IOException {
        Files.walk(sourceDirectory)
                .filter(path -> !path.equals(sourceDirectory))
                .forEach(path -> {
                    try {
                        final Path relativePath = sourceDirectory.relativize(path);
                        if (Files.isDirectory(path)) {
                            Files.createDirectories(relativePath);
                        } else {
                            final String fileName = relativePath.getFileName().toString();
                            final Path targetPath = targetDirectory.resolve(relativePath);
                            if (fileName.contains(".qute")) {
                                // Template file
                                final String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                                final String renderedContent = engine.parse(content).data(data).render();
                                Files.write(targetPath, renderedContent.getBytes());
                            } else {
                                // Static file
                                Files.copy(path, targetPath);
                            }
                        }
                    } catch (final IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });

    }

    private static Codestart findEnabledCodestartOfTypeOrDefault(final Collection<Codestart> codestarts,
            final Set<String> enabledCodestarts, final CodestartSpec.Type type) {
        return codestarts.stream()
                .filter(c -> c.getSpec().getType().equals(type)
                        && (c.getSpec().isDefault() || enabledCodestarts.contains(c.getSpec().getName())))
                .min(Comparator.comparing(c -> c.getSpec().isDefault()))
                .orElseThrow(() -> new IllegalStateException("No matching codestart of type " + type + " has been found"));
    }

    /**
     * Map<String, Template> loadTemplates(QuarkusProject quarkusProject, List<String> names) {
     * return names.stream().collect(Collectors.toMap(Function.identity(), name -> {
     * final String content = descriptor.getTemplate("codestarts/" + name);
     * return engine.parse(content);
     * }));
     * }
     **/

}
