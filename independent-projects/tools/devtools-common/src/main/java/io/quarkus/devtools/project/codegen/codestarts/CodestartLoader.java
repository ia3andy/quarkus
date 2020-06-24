package io.quarkus.devtools.project.codegen.codestarts;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.devtools.project.codegen.codestarts.Codestart;
import io.quarkus.devtools.project.codegen.codestarts.CodestartSpec;
import io.quarkus.devtools.project.codegen.codestarts.Codestarts;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.quarkus.platform.descriptor.loader.json.ResourceLoaders.toResourceNameWalker;

public final class CodestartLoader {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
        .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

    private static final String CODESTARTS_DIR_DEFAULT = "codestarts/default";
    private static final String CODESTARTS_DIR_EXTENSIONS = "codestarts/extensions";

    private CodestartLoader() {}

    public static List<Codestart> loadAddCodestarts(CodestartInput input) throws IOException {
        return Stream.concat(CodestartLoader.loadDefaultCodestarts(input.getDescriptor()).stream(), CodestartLoader.loadExtensionsCodestarts(input.getDescriptor()).stream()).collect(Collectors.toList());
    }

    public static Collection<Codestart> loadDefaultCodestarts(final QuarkusPlatformDescriptor descriptor) throws IOException {
        return loadCodestarts(descriptor, CODESTARTS_DIR_DEFAULT);
    }

    public static Collection<Codestart> loadExtensionsCodestarts(final QuarkusPlatformDescriptor descriptor) throws IOException {
        return loadCodestarts(descriptor, CODESTARTS_DIR_EXTENSIONS);
    }

    static Collection<Codestart> loadCodestarts(final QuarkusPlatformDescriptor descriptor, final String directoryName) throws IOException {
        return descriptor.loadResourcePath(directoryName, path -> toResourceNameWalker(directoryName, path))
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
}
