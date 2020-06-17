package io.quarkus.devtools.project.codegen.codestarts;

import java.util.Map;

class ProjectDefinition {

    private final String buildTool;
    private final List<AppArtifactCoords> extensions;
    private final Map<String, Object> data;

    ProjectDefinition(BuildToolVariant buildToolVariant, QuarkusConfigVariant quarkusConfigVariant, String sourceVariant, Map<String, Object> data) {
        this.buildToolVariant = buildToolVariant;
        this.quarkusConfigVariant = quarkusConfigVariant;
        this.sourceVariant = sourceVariant;
        this.data = data;
    }

    public BuildToolVariant getBuildToolVariant() {
        return buildToolVariant;
    }

    public QuarkusConfigVariant getQuarkusConfigVariant() {
        return quarkusConfigVariant;
    }

    public String getSourceVariant() {
        return sourceVariant;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
