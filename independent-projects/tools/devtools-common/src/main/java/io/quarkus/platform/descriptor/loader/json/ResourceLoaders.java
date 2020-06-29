package io.quarkus.platform.descriptor.loader.json;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;

public final class ResourceLoaders {

    private ResourceLoaders() {
    }

    public static File getResourceFile(final URL url, final String name) throws IOException {
        if (url == null) {
            throw new IOException("Failed to locate resource " + name + " on the classpath");
        }
        try {
            return new File(url.toURI());
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new IOException(
                    "There were a problem while reading the resource dir '" + name + "' on the classpath with url: '"
                            + url.toString() + "'");
        }
    }

    public static Stream<String> toResourceNameWalker(final String dirName, final Path dirPath) throws IOException {
        return Files.walk(dirPath).map(p -> resolveResourceName(dirName, dirPath, p));
    }

    private static String resolveResourceName(final String dirName, final Path dirPath, final Path resourcePath) {
        return FilenameUtils.concat(dirName, dirPath.relativize(resourcePath).toString().replace('\\', '/'));
    }
}
