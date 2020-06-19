package io.quarkus.platform.descriptor.loader.json;

import io.quarkus.platform.descriptor.ResourceInputStreamConsumer;
import io.quarkus.platform.descriptor.ResourceNamesConsumer;
import java.io.IOException;

public interface ResourceLoader {
    <T> T loadResource(String name, ResourceInputStreamConsumer<T> consumer) throws IOException;

    <T> T walkDir(String name, ResourceNamesConsumer<T> consumer) throws IOException;

}
