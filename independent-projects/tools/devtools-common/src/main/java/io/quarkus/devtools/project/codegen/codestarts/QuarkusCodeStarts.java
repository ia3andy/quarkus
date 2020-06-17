package io.quarkus.devtools.project.codegen.codestarts;

import io.quarkus.devtools.project.QuarkusProject;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuarkusCodeStarts {





    static void processCodeStartDirectory(final Engine engine, final Path sourceDirectory, final Path targetDirectory, final Object data) throws IOException {
        Files.walk(sourceDirectory)
            .filter(path -> !path.equals(sourceDirectory))
            .forEach(path -> {
                try {
                    final Path relativePath = sourceDirectory.relativize(path);
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(relativePath);
                    } else  {
                        final String fileName = relativePath.getFileName().toString();
                        final Path targetPath = targetDirectory.resolve(relativePath);
                        if(fileName.contains(".qute")) {
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




    /**
    Map<String, Template> loadTemplates(QuarkusProject quarkusProject, List<String> names) {
        return names.stream().collect(Collectors.toMap(Function.identity(), name -> {
            final String content = descriptor.getTemplate("codestarts/" + name);
            return engine.parse(content);
        }));
    }**/


}
