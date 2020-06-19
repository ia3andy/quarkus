package io.quarkus.platform.descriptor.loader.json;

import static io.quarkus.platform.descriptor.loader.json.ResourceLoaders.getResourceNameWalker;

import io.quarkus.platform.descriptor.ResourceInputStreamConsumer;
import io.quarkus.platform.descriptor.ResourceNamesConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZipResourceLoader implements ResourceLoader {

    private final Path zip;

    public ZipResourceLoader(Path zip) {
        this.zip = zip;
    }

    @Override
    public <T> T loadResource(String name, ResourceInputStreamConsumer<T> consumer) throws IOException {
        try (FileSystem fs = FileSystems.newFileSystem(zip, (ClassLoader) null)) {
            final Path p = fs.getPath("/", name);
            if (!Files.exists(p)) {
                throw new IOException("Failed to locate " + name + " in " + zip);
            }
            try (InputStream is = Files.newInputStream(p)) {
                return consumer.consume(is);
            }
        }
    }

    @Override
    public <T> T walkDir(String name, ResourceNamesConsumer<T> consumer) throws IOException {
        try (FileSystem fs = FileSystems.newFileSystem(zip, (ClassLoader) null)) {
            final Path dirPath = fs.getPath("/", name);
            if (!Files.exists(dirPath)) {
                throw new IOException("Failed to locate " + name + " in " + zip);
            }
            if (!Files.isDirectory(dirPath)) {
                throw new IOException("Resource " + name + " is not a directory on the classpath");
            }
            return consumer.consume(getResourceNameWalker(name, dirPath));
        }
    }
}
