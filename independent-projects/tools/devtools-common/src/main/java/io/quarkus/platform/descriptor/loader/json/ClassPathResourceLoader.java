package io.quarkus.platform.descriptor.loader.json;

import static io.quarkus.platform.descriptor.loader.json.ResourceLoaders.getResourceNameWalker;

import io.quarkus.platform.descriptor.ResourceInputStreamConsumer;
import io.quarkus.platform.descriptor.ResourceNamesConsumer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public class ClassPathResourceLoader implements ResourceLoader {

    private final ClassLoader cl;

    public ClassPathResourceLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ClassPathResourceLoader(ClassLoader cl) {
        this.cl = cl;
    }

    @Override
    public <T> T loadResource(String name, ResourceInputStreamConsumer<T> consumer) throws IOException {
        final InputStream stream = cl.getResourceAsStream(name);
        if (stream == null) {
            throw new IOException("Failed to locate " + name + " on the classpath");
        }
        return consumer.consume(stream);
    }

    @Override
    public <T> T walkDir(String name, ResourceNamesConsumer<T> consumer) throws IOException {
        final URL url = cl.getResource(name);
        final File file = ResourceLoaders.getResourceFile(url, name);
        if (!file.isDirectory()) {
            throw new IOException("Resource " + name + " is not a directory on the classpath");
        }
        final Path dirPath = file.toPath();
        return consumer.consume(getResourceNameWalker(name, dirPath));
    }
}
