package io.quarkus.devtools.project.codegen.codestarts;

import java.util.Map;
import java.util.stream.Stream;

import static io.quarkus.devtools.project.codegen.codestarts.CodestartData.mergeMaps;

final class Codestart {
    public static final String BASE_LANGUAGE = "base";
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
        return mergeMaps(Stream.of(getBaseLanguageSpec().getLocalData(), getLanguageSpec(languageName).getLocalData()));
    }

    public Map<String, Object> getSharedData(String languageName) {
        return mergeMaps(Stream.of(getBaseLanguageSpec().getSharedData(), getLanguageSpec(languageName).getSharedData()));
    }

    public CodestartSpec.LanguageSpec getBaseLanguageSpec() {
        return getSpec().getLanguagesSpec().getOrDefault(BASE_LANGUAGE, new CodestartSpec.LanguageSpec());
    }

    public CodestartSpec.LanguageSpec getLanguageSpec(String languageName) {
        return getSpec().getLanguagesSpec().getOrDefault(languageName, new CodestartSpec.LanguageSpec());
    }

}
