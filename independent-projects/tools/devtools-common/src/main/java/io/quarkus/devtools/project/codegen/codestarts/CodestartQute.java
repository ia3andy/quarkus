package io.quarkus.devtools.project.codegen.codestarts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Expression;
import io.quarkus.qute.ResultMapper;
import io.quarkus.qute.Results;
import io.quarkus.qute.TemplateNode;

public final class CodestartQute {

    private CodestartQute() {}

    public static Engine newEngine() {
        return Engine.builder().addDefaults()
            .addResultMapper(new MissingValueMapper())
            .build();
    }

    public static String processQuteContent(Engine engine, Path path, Object data) throws IOException {
        final String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        return engine.parse(content).render(data);
    }

    static class MissingValueMapper implements ResultMapper {

        public boolean appliesTo(TemplateNode.Origin origin, Object result) {
            return Results.Result.NOT_FOUND.equals(result);
        }

        public String map(Object result, Expression expression) {
            return "{missing:" + expression.toOriginalString() + "}";
        }
    }
}
