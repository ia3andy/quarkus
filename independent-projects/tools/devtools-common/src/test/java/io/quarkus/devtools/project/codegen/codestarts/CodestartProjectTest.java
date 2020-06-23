package io.quarkus.devtools.project.codegen.codestarts;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.devtools.ProjectTestUtil;
import io.quarkus.devtools.commands.PlatformAwareTestBase;
import io.quarkus.qute.Engine;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CodestartProjectTest extends PlatformAwareTestBase {

    private final Path projectPath = Paths.get("target/codestarts-test");

    @BeforeEach
    void setUp() throws IOException {
        ProjectTestUtil.delete(projectPath.toFile());
    }

    @Test
    void loadDefaultCodestartsTest() throws IOException {
        final Collection<Codestart> codestarts = Codestarts.loadDefaultCodestarts(getPlatformDescriptor());
        assertThat(codestarts).hasSize(10);
    }

    @Test
    void processCodestartProjectEmpty() throws IOException {
        final CodestartProject codestartProject = Codestarts.resolveCodestartProject(getPlatformDescriptor(),
                new CodestartInput(Collections.emptyList(), Collections.emptyMap()));
        Codestarts.processCodestartProject(getPlatformDescriptor(), Engine.builder().addDefaults().build(), codestartProject,
                projectPath.resolve("empty"));
    }

    @Test
    void processCodestartProjectResteasy() throws IOException {
        final CodestartProject codestartProject = Codestarts.resolveCodestartProject(getPlatformDescriptor(),
                new CodestartInput(Collections.singletonList(AppArtifactKey.fromString("io.quarkus:quarkus-resteasy")),
                        Collections.emptyMap()));
        Codestarts.processCodestartProject(getPlatformDescriptor(), Engine.builder().addDefaults().build(), codestartProject,
                projectPath.resolve("resteasy"));
    }
}
