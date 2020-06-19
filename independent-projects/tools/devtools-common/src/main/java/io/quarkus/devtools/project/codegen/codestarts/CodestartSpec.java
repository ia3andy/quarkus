package io.quarkus.devtools.project.codegen.codestarts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class CodestartSpec {

    enum Type {
        PROJECT,
        BUILDTOOL,
        LANGUAGE,
        CONFIG,
        CODESTART
    }

    private final String name;
    private final Type type;
    private final CodeStartData data;
    private final boolean isDefault;
    private final CodeStartDeps core;
    private final CodeStartDeps example;

    @JsonCreator
    public CodestartSpec(@JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "type") Type type,
            @JsonProperty("data") CodeStartData data,
            @JsonProperty("default") boolean isDefault,
            @JsonProperty("core") CodeStartDeps core,
            @JsonProperty("example") CodeStartDeps example) {
        this.name = requireNonNull(name, "name is required");
        this.type = type != null ? type : Type.CODESTART;
        this.data = data;
        this.isDefault = isDefault;
        this.core = core;
        this.example = example;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public CodeStartData getData() {
        return data;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public CodeStartDeps getCore() {
        return core;
    }

    public CodeStartDeps getExample() {
        return example;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CodestartSpec that = (CodestartSpec) o;
        return isDefault == that.isDefault &&
                Objects.equals(name, that.name) &&
                type == that.type &&
                Objects.equals(data, that.data) &&
                Objects.equals(core, that.core) &&
                Objects.equals(example, that.example);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, data, isDefault, core, example);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CodestartSpec{");
        sb.append("name='").append(name).append('\'');
        sb.append(", type=").append(type);
        sb.append(", data=").append(data);
        sb.append(", isDefault=").append(isDefault);
        sb.append(", core=").append(core);
        sb.append(", example=").append(example);
        sb.append('}');
        return sb.toString();
    }

    public static final class CodeStartData {
        private final Map<String, Object> local;
        private final Map<String, Object> global;

        @JsonCreator
        public CodeStartData(@JsonProperty("local") Map<String, Object> local,
                @JsonProperty("global") Map<String, Object> global) {
            this.local = local;
            this.global = global;
        }

        public Map<String, Object> getLocal() {
            return local;
        }

        public Map<String, Object> getGlobal() {
            return global;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            CodeStartData that = (CodeStartData) o;
            return Objects.equals(local, that.local) &&
                    Objects.equals(global, that.global);
        }

        @Override
        public int hashCode() {
            return Objects.hash(local, global);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CodeStartData{");
            sb.append("local=").append(local);
            sb.append(", global=").append(global);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final class CodeStartDeps {
        private final List<String> dependencies;
        private final List<String> testDependencies;

        @JsonCreator
        public CodeStartDeps(@JsonProperty("dependencies") List<String> dependencies,
                @JsonProperty("testDependencies") List<String> testDependencies) {
            this.dependencies = dependencies;
            this.testDependencies = testDependencies;
        }

        public List<String> getDependencies() {
            return dependencies;
        }

        public List<String> getTestDependencies() {
            return testDependencies;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            CodeStartDeps that = (CodeStartDeps) o;
            return Objects.equals(dependencies, that.dependencies) &&
                    Objects.equals(testDependencies, that.testDependencies);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dependencies, testDependencies);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CodeStartDeps{");
            sb.append("dependencies=").append(dependencies);
            sb.append(", testDependencies=").append(testDependencies);
            sb.append('}');
            return sb.toString();
        }
    }
}
