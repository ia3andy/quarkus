package io.quarkus.devtools.project.codegen.codestarts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import io.quarkus.qute.Engine;
import io.quarkus.qute.EvalContext;
import io.quarkus.qute.Expression;
import io.quarkus.qute.ResultMapper;
import io.quarkus.qute.Results;
import io.quarkus.qute.TemplateNode;
import io.quarkus.qute.ValueResolver;
import org.apache.commons.lang3.StringUtils;

final class CodestartQute {

    private CodestartQute() {}

    public static Engine newEngine() {
        return Engine.builder().addDefaults()
            .addResultMapper(new MissingValueMapper())
            .build();
    }

    public static String processQuteContent(Engine engine, Path path, Map<String, Object> data) throws IOException {
        final String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        final Object preparedData = prepareData(data);
        return engine.parse(content).render(preparedData);
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

    private static void doUnflatten(Map<String, Object> current, String key,
                                    Object originalValue) {
        String[] parts = StringUtils.split(key, ".");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == (parts.length - 1)) {
                current.put(part, originalValue);
                return;
            }

            Map<String, Object> nestedMap = (Map<String, Object>) current.get(part);
            if (nestedMap == null) {
                nestedMap = new HashMap<>();
                current.put(part, nestedMap);
            }

            current = nestedMap;
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
