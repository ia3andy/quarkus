package io.quarkus.devtools.project.codegen.codestarts;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Expression;
import io.quarkus.qute.ResultMapper;
import io.quarkus.qute.Results;
import io.quarkus.qute.TemplateLocator;
import io.quarkus.qute.TemplateNode;
import io.quarkus.qute.Variant;
import org.apache.commons.lang3.StringUtils;

final class CodestartQute {

    private CodestartQute() {}

    public static Engine newEngine() {
        return Engine.builder().addDefaults()
            .addResultMapper(new MissingValueMapper())
            .build();
    }

    public static String processQuteContent(Path path, String languageName, Map<String, Object> data) throws IOException {
        final String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        final Object preparedData = prepareData(data);
        final Engine engine = Engine.builder().addDefaults()
            .addResultMapper(new MissingValueMapper())
            .addLocator(id -> findIncludeTemplate(path, languageName, id).map(IncludeTemplateLocation::new))
            .build();
        return engine.parse(content).render(preparedData);
    }

    private static Optional<Path> findIncludeTemplate(Path path, String languageName, String name) {
        // FIXME looking at the parent dir is a bit random
        final Path codestartPath = path.getParent().getParent();
        final String includeFileName = name + ".include.qute";
        final Path languageIncludeTemplate = codestartPath.resolve(languageName + "/" + includeFileName);
        if(Files.isRegularFile(languageIncludeTemplate)) {
            return Optional.of(languageIncludeTemplate);
        }
        final Path baseIncludeTemplate = codestartPath.resolve("base/" + includeFileName);
        if(Files.isRegularFile(baseIncludeTemplate)) {
            return Optional.of(baseIncludeTemplate);
        }
        return Optional.empty();
    }

    private static Object prepareData(Map<String, Object> data) {
        return unflatten(data);
    }

    private static Map<String, Object> unflatten(Map<String, Object> flattened) {
        Map<String, Object> unflattened = new HashMap<>();
        for (String key : flattened.keySet()) {
            doUnflatten(unflattened, key, flattened.get(key));
        }
        return unflattened;
    }

    private static void doUnflatten(Map<String, Object> current, String key, Object originalValue) {
        String[] parts = StringUtils.split(key, ".");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == (parts.length - 1)) {
                current.put(part, originalValue);
                return;
            }


            final Object value = current.get(part);
            if (value == null) {
                final HashMap<String, Object> map = new HashMap<>();
                current.put(part, map);
                current = map;
            } else if(value instanceof Map) {
                current = (Map<String, Object>) value;
            } else {
                throw new IllegalStateException("Conflicting data types for key '" + key + "'");
            }
        }
    }

    private static class IncludeTemplateLocation implements TemplateLocator.TemplateLocation {

        private final Path path;

        private IncludeTemplateLocation(Path path) {
            this.path = path;
        }

        @Override
        public Reader read() {
            try {
                return Files.newBufferedReader(path);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        public Optional<Variant> getVariant() {
            return Optional.empty();
        }
    }

    static class MissingValueMapper implements ResultMapper {

        public boolean appliesTo(TemplateNode.Origin origin, Object result) {
            return Results.Result.NOT_FOUND.equals(result);
        }

        public String map(Object result, Expression expression) {
            throw new IllegalStateException("Missing required data: {" + expression.toOriginalString() + "}");
        }
    }

}
