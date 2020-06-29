package io.quarkus.devtools.project.codegen.codestarts;

import static io.quarkus.devtools.project.codegen.codestarts.Codestart.BASE_LANGUAGE;

import io.fabric8.maven.Maven;
import io.fabric8.maven.merge.SmartModelMerger;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.maven.model.Model;

final class CodestartProcessor {

    private CodestartProcessor() {
    }

    static void processCodestart(final QuarkusPlatformDescriptor descriptor, final Codestart codestart,
            final String languageName, final Path targetDirectory, final Map<String, Object> data) {
        try {
            descriptor.loadResourcePath(codestart.getResourceName(), p -> resolveDirectoriesToProcessAsStream(p, languageName))
                    .forEach(p -> processCodestartDir(languageName, p, targetDirectory,
                            CodestartData.mergeData(codestart, languageName, data)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    static Stream<Path> resolveDirectoriesToProcessAsStream(final Path sourceDirectory, final String languageName)
            throws IOException {
        if (!Files.isDirectory(sourceDirectory)) {
            throw new IllegalStateException("Codestart sourceDirectory is not a directory: " + sourceDirectory);
        }
        return Stream.of(BASE_LANGUAGE, languageName)
                .map(sourceDirectory::resolve)
                .filter(Files::isDirectory);
    }

    static void processCodestartDir(final String languageName,
            final Path sourceDirectory,
            final Path targetProjectDirectory,
            final Map<String, Object> data) {
        try {
            Files.walk(sourceDirectory)
                    .filter(path -> !path.equals(sourceDirectory))
                    .forEach(path -> {
                        try {
                            final Path relativePath = sourceDirectory.relativize(path);
                            if (Files.isDirectory(path)) {
                                return;
                            } else {
                                final String fileName = relativePath.getFileName().toString();
                                final Path targetPath = targetProjectDirectory.resolve(relativePath);
                                if (fileName.contains(".part")) {
                                    // TODO we need some kind of PartCombiner interface with a "matcher"
                                    if (fileName.equals("pom.part.qute.xml")) {
                                        processMavenPart(path, languageName, data, targetPath.getParent().resolve("pom.xml"));
                                    } else if (fileName.equals("README.part.qute.md")) {
                                        processReadmePart(path, languageName, data,
                                                targetPath.getParent().resolve("README.md"));
                                    } else {
                                        throw new IllegalStateException("Unsupported part file: " + path);
                                    }
                                } else if (fileName.contains(".qute")) {
                                    if (fileName.endsWith(".include.qute")) {
                                        //ignore includes
                                        return;
                                    }
                                    // Template file
                                    processQuteFile(path, languageName, data,
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

    private static void processReadmePart(Path path, String languageName, Map<String, Object> data, Path targetPath)
            throws IOException {
        if (!Files.exists(targetPath)) {
            throw new IllegalStateException(
                    "Using .part is not possible when the target file does not exist already: " + path + " -> " + targetPath);
        }
        final String renderedContent = "\n" + CodestartQute.processQuteContent(path, languageName, data);
        Files.write(targetPath, renderedContent.getBytes(), StandardOpenOption.APPEND);
    }

    private static void processMavenPart(Path path, String languageName, Map<String, Object> data, Path targetPath)
            throws IOException {
        if (!Files.exists(targetPath)) {
            throw new IllegalStateException(
                    "Using .part is not possible when the target file does not exist already: " + path + " -> " + targetPath);
        }
        final Model targetModel = Maven.readModel(targetPath);
        final SmartModelMerger merger = new SmartModelMerger();
        final String content = CodestartQute.processQuteContent(path, languageName, data);
        final Model sourceModel = Maven.readModel(new StringReader(content));
        merger.merge(targetModel, sourceModel, true, null);
        Maven.writeModel(targetModel);
    }

    private static void processStaticFile(Path path, Path targetPath) throws IOException {
        Files.createDirectories(targetPath.getParent());
        Files.copy(path, targetPath);
    }

    private static void processQuteFile(Path path, String languageName, Map<String, Object> data, Path targetPath)
            throws IOException {
        final String renderedContent = CodestartQute.processQuteContent(path, languageName, data);
        Files.createDirectories(targetPath.getParent());
        Files.write(targetPath, renderedContent.getBytes(), StandardOpenOption.CREATE_NEW);
    }

    static void checkTargetDir(Path targetDirectory) throws IOException {
        if (!Files.exists(targetDirectory)) {
            boolean mkdirStatus = targetDirectory.toFile().mkdirs();
            if (!mkdirStatus) {
                throw new IOException("Failed to create the project directory: " + targetDirectory);
            }
            return;
        }
        if (!Files.isDirectory(targetDirectory)) {
            throw new IOException("Project path needs to point to a directory: " + targetDirectory);
        }
        final String[] files = targetDirectory.toFile().list();
        if (files != null && files.length > 0) {
            throw new IOException("You can't create a project when the directory is not empty: " + targetDirectory);
        }
    }
}
