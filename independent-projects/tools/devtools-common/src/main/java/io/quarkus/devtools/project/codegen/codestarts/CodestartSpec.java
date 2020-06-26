package io.quarkus.devtools.project.codegen.codestarts;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class CodestartSpec {

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

    public Map<String, LanguageSpec> getLanguagesSpec() {
        return languagesSpec;
    }

    public static final class LanguageSpec {
        private final Map<String, Object> localData;
        private final Map<String, Object> sharedData;
        private final Map<String, String> qutePartials;
        private final List<CodestartDep> dependencies;
        private final List<CodestartDep> testDependencies;

        public LanguageSpec() {
            this(null, null, null, null, null);
        }

        @JsonCreator
        public LanguageSpec(@JsonProperty("localData") Map<String, Object> localData,
                            @JsonProperty("sharedData") Map<String, Object> sharedData,
                            @JsonProperty("qutePartials") Map<String, String> qutePartials,
                            @JsonProperty("dependencies") List<CodestartDep> dependencies,
                            @JsonProperty("testDependencies") List<CodestartDep> testDependencies) {
            this.localData = localData != null ? localData : Collections.emptyMap();
            this.sharedData = sharedData != null ? sharedData : Collections.emptyMap();
            this.qutePartials = qutePartials != null ? qutePartials : Collections.emptyMap();;
            this.dependencies = dependencies != null ? dependencies : Collections.emptyList();
            this.testDependencies = testDependencies != null ? testDependencies : Collections.emptyList();
        }

        public Map<String, Object> getLocalData() {
            return localData;
        }

        public Map<String, Object> getSharedData() {
            return sharedData;
        }

        public Map<String, String> getQutePartials() {
            return qutePartials;
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
