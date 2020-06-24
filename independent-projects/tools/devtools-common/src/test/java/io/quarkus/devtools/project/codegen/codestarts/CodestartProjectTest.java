package io.quarkus.devtools.project.codegen.codestarts;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.devtools.ProjectTestUtil;
import io.quarkus.devtools.commands.PlatformAwareTestBase;

class CodestartProjectTest extends PlatformAwareTestBase {

    private final Path projectPath = Paths.get("target/codestarts-test");

    @BeforeEach
    void setUp() throws IOException {
        ProjectTestUtil.delete(projectPath.toFile());
    }

    @Test
    void loadDefaultCodestartsTest() throws IOException {
        final Collection<Codestart> codestarts = CodestartLoader.loadDefaultCodestarts(getPlatformDescriptor());
        assertThat(codestarts).hasSize(10);
    }

    @Test
    void generateCodestartProjectEmpty() throws IOException {
        final CodestartProject codestartProject = Codestarts.prepareProject(new CodestartInput(getPlatformDescriptor(), Collections.emptyList(), false, Collections.emptyMap()));
        Codestarts.generateProject(codestartProject, projectPath.resolve("empty"));
    }

    @Test
    void generateCodestartProjectResteasy() throws IOException {
        final CodestartProject codestartProject = Codestarts.prepareProject(new CodestartInput(getPlatformDescriptor(), Collections.singletonList(AppArtifactKey.fromString("io.quarkus:quarkus-resteasy")),
            true, Collections.emptyMap()));
        Codestarts.generateProject(codestartProject, projectPath.resolve("resteasy"));
    }
}
