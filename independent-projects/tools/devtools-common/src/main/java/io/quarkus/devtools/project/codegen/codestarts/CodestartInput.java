package io.quarkus.devtools.project.codegen.codestarts;

import io.quarkus.bootstrap.model.AppArtifactKey;

import java.util.Collection;
import java.util.Map;

public class CodestartInput {
    private final Collection<AppArtifactKey> extensions;
    private final Map<String, Object> data;

    public CodestartInput(Collection<AppArtifactKey> extensions, Map<String, Object> data) {
        this.extensions = extensions;
        this.data = data;
    }

    public Collection<AppArtifactKey> getExtensions() {
        return extensions;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
