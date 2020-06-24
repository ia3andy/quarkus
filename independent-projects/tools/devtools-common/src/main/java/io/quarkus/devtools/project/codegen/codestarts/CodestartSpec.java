package io.quarkus.devtools.project.codegen.codestarts;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CodestartSpec {

    enum Type {
        PROJECT,
        BUILDTOOL,
        LANGUAGE,
        CONFIG,
        CODESTART
    }

    private final String name;
    private final String ref;
    private final Type type;
    private final boolean isDefault;
    private final boolean isExample;
    private final Map<String, LanguageSpec> languagesSpec;

    @JsonCreator
    public CodestartSpec(@JsonProperty(value = "name", required = true) String name,
                         @JsonProperty(value = "ref") String ref,
                         @JsonProperty(value = "type") Type type,
                         @JsonProperty("default") boolean isDefault,
                         @JsonProperty("example") boolean isExample,
                         @JsonProperty("spec") Map<String, LanguageSpec> languagesSpec) {
        this.name = requireNonNull(name, "name is required");
        this.ref = ref != null ? ref : name;
        this.type = type != null ? type : Type.CODESTART;
        this.isDefault = isDefault;
        this.isExample = isExample;
        this.languagesSpec = languagesSpec != null ? languagesSpec : Collections.emptyMap();
    }

    public String getName() {
        return name;
    }

    public String getRef() {
        return ref;
    }

    public Type getType() {
        return type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isExample() {
        return isExample;
    }

    public LanguageSpec getBaseSpec() {
        return languagesSpec.getOrDefault("base", new LanguageSpec());
    }

    public LanguageSpec getLanguageSpec(String languageName) {
        return languagesSpec.getOrDefault("language-" + languageName, new LanguageSpec());
    }

    public static final class CodeStartData {
        private final Map<String, Object> local;
        private final Map<String, Object> shared;

        public CodeStartData() {
           this(null, null);
        }

        @JsonCreator
        public CodeStartData(@JsonProperty("local") Map<String, Object> local,
                @JsonProperty("shared") Map<String, Object> shared) {
            this.local = local != null ? local : Collections.emptyMap();
            this.shared = shared != null ? shared : Collections.emptyMap();
        }

        public Map<String, Object> getLocal() {
            return local;
        }

        public Map<String, Object> getShared() {
            return shared;
        }

    }

    public static final class LanguageSpec {
        private final CodeStartData data;
        private final List<CodestartDep> dependencies;
        private final List<CodestartDep> testDependencies;

        public LanguageSpec() {
            this(null, null, null);
        }

        @JsonCreator
        public LanguageSpec(@JsonProperty("data") CodeStartData data,
                            @JsonProperty("dependencies") List<CodestartDep> dependencies,
                            @JsonProperty("testDependencies") List<CodestartDep> testDependencies) {
            this.data = data != null ? data : new CodeStartData();
            this.dependencies = dependencies != null ? dependencies : Collections.emptyList();
            this.testDependencies = testDependencies != null ? testDependencies : Collections.emptyList();
        }

        public CodeStartData getData() {
            return data;
        }

        public List<CodestartDep> getDependencies() {
            return dependencies;
        }

        public List<CodestartDep> getTestDependencies() {
            return testDependencies;
        }
    }
    public static class CodestartDep extends HashMap<String, String> {
        public CodestartDep() {
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public CodestartDep(final String expression) {
            final String[] split = expression.split(":");
            if(split.length < 2 || split.length > 3) {
                throw new IllegalArgumentException("Invalid CodestartDep expression: " + expression);
            }
            this.put("groupId", split[0]);
            this.put("artifactId", split[1]);
            if(split.length == 3) {
                this.put("version", split[2]);
            }
        }

        public String getGroupId() {
            return this.get("groupId");
        }

        public String getArtifactId() {
            return this.get("artifactId");
        }

        public String getVersion() {
            return this.get("version");
        }

        @Override
        public String toString() {
            final String version = Optional.ofNullable(getVersion()).map(v -> ":" + v).orElse("");
            return getGroupId() + ":" + getArtifactId() + version;
        }


    }
}
