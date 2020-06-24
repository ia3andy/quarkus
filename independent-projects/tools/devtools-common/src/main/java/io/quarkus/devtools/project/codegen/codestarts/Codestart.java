package io.quarkus.devtools.project.codegen.codestarts;

import java.util.Map;
import java.util.stream.Stream;

import static io.quarkus.devtools.project.codegen.codestarts.Codestarts.mergeMaps;

public final class Codestart {
    private final String resourceName;
    private final CodestartSpec spec;

    public Codestart(final String resourceName, final CodestartSpec spec) {
        this.resourceName = resourceName;
        this.spec = spec;
    }

    public String getResourceName() {
        return resourceName;
    }

    public CodestartSpec getSpec() {
        return spec;
    }

    public Map<String, Object> getLocalData(String languageName) {
        return mergeMaps(Stream.of(getSpec().getBaseSpec().getData().getLocal(), getSpec().getLanguageSpec(languageName).getData().getLocal()));
    }

    public Map<String, Object> getSharedData(String languageName) {
        return mergeMaps(Stream.of(getSpec().getBaseSpec().getData().getShared(), getSpec().getLanguageSpec(languageName).getData().getShared()));
    }

}
