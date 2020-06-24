package io.quarkus.devtools.project.codegen.codestarts;

import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

import java.util.Collection;
import java.util.Map;

public class CodestartInput {
    private final QuarkusPlatformDescriptor descriptor;
    private final Collection<AppArtifactKey> extensions;
    private final boolean includeExample;
    private final Map<String, Object> data;

    public CodestartInput(QuarkusPlatformDescriptor descriptor, Collection<AppArtifactKey> extensions, boolean includeExample, Map<String, Object> data) {
        this.descriptor = descriptor;
        this.extensions = extensions;
        this.includeExample = includeExample;
        this.data = data;
    }

    public QuarkusPlatformDescriptor getDescriptor() {
        return descriptor;
    }

    public Collection<AppArtifactKey> getExtensions() {
        return extensions;
    }

    public boolean includeExample() {
        return includeExample;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
