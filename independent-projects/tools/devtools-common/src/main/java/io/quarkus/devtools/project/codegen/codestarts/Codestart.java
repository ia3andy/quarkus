package io.quarkus.devtools.project.codegen.codestarts;

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
}
