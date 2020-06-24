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
        final Collection<Codestart> codestarts = CodestartLoader.loadDefaultCodestarts(getPlatformDescriptor());
        assertThat(codestarts).hasSize(10);
    }

    @Test
    void prepareProjectTestEmpty() throws IOException {
        final CodestartProject codestartProject = Codestarts.prepareProject(new CodestartInput(getPlatformDescriptor(), Collections.emptyList(), false, Collections.emptyMap()));
        assertThat(codestartProject.getProject()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/project/quarkus");
        assertThat(codestartProject.getBuildTool()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/buildtool/maven");
        assertThat(codestartProject.getConfig()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/config/config-properties");
        assertThat(codestartProject.getLanguage()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/language/java");
        assertThat(codestartProject.getCodestarts()).extracting(Codestart::getResourceName)
                .containsExactly("codestarts/default/codestart/docker");
    }

    @Test
    void prepareProjectTestGradle() throws IOException {
        final CodestartProject codestartProject = Codestarts.prepareProject(new CodestartInput(getPlatformDescriptor(), Collections.emptyList(), false, Collections.singletonMap("buildtool", "gradle")));
        assertThat(codestartProject.getBuildTool()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/buildtool/gradle");
    }

    @Test
    void prepareProjectTestKotlin() throws IOException {
        final CodestartProject codestartProject = Codestarts.prepareProject(new CodestartInput(getPlatformDescriptor(), Collections.singletonList(AppArtifactKey.fromString("io.quarkus:quarkus-kotlin")),
            false, Collections.emptyMap()));
        assertThat(codestartProject.getLanguage()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/language/kotlin");
    }

    @Test
    void prepareProjectTestScala() throws IOException {
        final CodestartProject codestartProject = Codestarts.prepareProject(new CodestartInput(getPlatformDescriptor(), Collections.singletonList(AppArtifactKey.fromString("io.quarkus:quarkus-scala")),
            false, Collections.emptyMap()));
        assertThat(codestartProject.getLanguage()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/language/scala");
    }

    @Test
    void prepareProjectTestConfigYaml() throws IOException {
        final CodestartProject codestartProject = Codestarts.prepareProject(new CodestartInput(getPlatformDescriptor(), Collections.singletonList(AppArtifactKey.fromString("io.quarkus:quarkus-config-yaml")),
            false, Collections.emptyMap()));
        assertThat(codestartProject.getConfig()).extracting(Codestart::getResourceName)
                .isEqualTo("codestarts/default/config/config-yaml");
    }

    @Test
    void prepareProjectTestResteasy() throws IOException {
        final CodestartProject codestartProject = Codestarts.prepareProject(new CodestartInput(getPlatformDescriptor(), Collections.singletonList(AppArtifactKey.fromString("io.quarkus:quarkus-resteasy")),
            true, Collections.emptyMap()));
        assertThat(codestartProject.getCodestarts()).extracting(Codestart::getResourceName)
                .containsExactlyInAnyOrder("codestarts/default/codestart/docker", "codestarts/extensions/resteasy-example");
    }
}
