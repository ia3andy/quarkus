package io.quarkus.devtools.project.codegen.codestarts;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.devtools.ProjectTestUtil;
import io.quarkus.devtools.commands.PlatformAwareTestBase;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CodestartsTest extends PlatformAwareTestBase {

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
    void resolveCodestartProjectTestEmpty() throws IOException {
        final CodestartProject codestartProject = Codestarts.resolveCodestartProject(getPlatformDescriptor(),
                new CodestartInput(Collections.emptyList(), Collections.emptyMap()));
        assertThat(codestartProject.getProject()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/project-quarkus");
        assertThat(codestartProject.getBuildTool()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/buildtool-maven");
        assertThat(codestartProject.getConfig()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/config-properties");
        assertThat(codestartProject.getLanguage()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/language-java");
        assertThat(codestartProject.getCodestarts()).extracting(Codestart::getResourceName)
                .containsExactly("codestarts/default/codestart-docker");
    }

    @Test
    void resolveCodestartProjectTestGradle() throws IOException {
        final CodestartProject codestartProject = Codestarts.resolveCodestartProject(getPlatformDescriptor(),
                new CodestartInput(Collections.emptyList(), Collections.singletonMap("buildtool", "gradle")));
        assertThat(codestartProject.getBuildTool()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/buildtool-gradle");
    }

    @Test
    void resolveCodestartProjectTestKotlin() throws IOException {
        final CodestartProject codestartProject = Codestarts.resolveCodestartProject(getPlatformDescriptor(),
                new CodestartInput(Collections.singletonList(AppArtifactKey.fromString("io.quarkus:quarkus-kotlin")),
                        Collections.emptyMap()));
        assertThat(codestartProject.getLanguage()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/language-kotlin");
    }

    @Test
    void resolveCodestartProjectTestScala() throws IOException {
        final CodestartProject codestartProject = Codestarts.resolveCodestartProject(getPlatformDescriptor(),
                new CodestartInput(Collections.singletonList(AppArtifactKey.fromString("io.quarkus:quarkus-scala")),
                        Collections.emptyMap()));
        assertThat(codestartProject.getLanguage()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/language-scala");
    }

    @Test
    void resolveCodestartProjectTestConfigYaml() throws IOException {
        final CodestartProject codestartProject = Codestarts.resolveCodestartProject(getPlatformDescriptor(),
                new CodestartInput(Collections.singletonList(AppArtifactKey.fromString("io.quarkus:quarkus-config-yaml")),
                        Collections.emptyMap()));
        assertThat(codestartProject.getConfig()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/config-yaml");
    }

    @Test
    void resolveCodestartProjectTestResteasy() throws IOException {
        final CodestartProject codestartProject = Codestarts.resolveCodestartProject(getPlatformDescriptor(),
                new CodestartInput(Collections.singletonList(AppArtifactKey.fromString("io.quarkus:quarkus-resteasy")),
                        Collections.emptyMap()));
        assertThat(codestartProject.getCodestarts()).extracting(Codestart::getResourceName)
                .containsExactlyInAnyOrder("codestarts/default/codestart-docker", "codestarts/default/codestart-resteasy");
    }
}
