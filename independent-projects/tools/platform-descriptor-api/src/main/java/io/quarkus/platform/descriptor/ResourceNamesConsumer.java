package io.quarkus.platform.descriptor;

import java.io.IOException;
import java.util.stream.Stream;

public interface ResourceNamesConsumer<T> {
    T consume(Stream<String> names) throws IOException;
}
