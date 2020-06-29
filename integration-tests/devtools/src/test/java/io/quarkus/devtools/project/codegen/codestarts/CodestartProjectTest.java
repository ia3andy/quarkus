package io.quarkus.devtools.project.codegen.codestarts;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.devtools.PlatformAwareTestBase;
import io.quarkus.devtools.ProjectTestUtil;

class CodestartProjectTest extends PlatformAwareTestBase {

    private static final Path projectPath = Paths.get("target/codestarts-test");

    @BeforeAll
    static void setUp() throws IOException {
        ProjectTestUtil.delete(projectPath.toFile());
    }

    private Map<String, Object> getTestInputData() {
        return getTestInputData(null);
    }

    private Map<String, Object> getTestInputData(final Map<String, Object> override) {
        final HashMap<String, Object> data = new HashMap<>();
        data.put("project.version", "1.0.0-codestart");
        data.put("quarkus.platform.groupId", getPlatformDescriptor().getBomGroupId());
        data.put("quarkus.platform.artifactId", getPlatformDescriptor().getBomArtifactId());
        data.put("quarkus.platform.version", "1.5.2.Final");
        data.put("quarkus.plugin.groupId", "io.quarkus");
        data.put("quarkus.plugin.artifactId", "quarkus-maven-plugin");
        data.put("quarkus.plugin.version", "1.5.2.Final");
        if (override != null)
            data.putAll(override);
        return data;
    }

    @Test
    void loadDefaultCodestartsTest() throws IOException {
        final Collection<Codestart> codestarts = CodestartLoader.loadBaseCodestarts(getPlatformDescriptor());
        assertThat(codestarts).hasSize(10);
    }

    @Test
    void generateCodestartProjectEmpty() throws IOException {
        final CodestartProject codestartProject = Codestarts.prepareProject(
                new CodestartInput(getPlatformDescriptor(), Collections.emptyList(), false, getTestInputData()));
        Codestarts.generateProject(codestartProject, projectPath.resolve("empty"));
    }

    @Test
    void generateCodestartProjectMavenResteasyJava() throws IOException {
        final List<AppArtifactKey> extensions = Arrays.asList(AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"));
        final CodestartProject codestartProject = Codestarts
                .prepareProject(new CodestartInput(getPlatformDescriptor(), extensions,
                        true, getTestInputData()));
        Codestarts.generateProject(codestartProject, projectPath.resolve("maven-resteasy-java"));
    }

    @Test
    void generateCodestartProjectMavenResteasyKotlin() throws IOException {
        final List<AppArtifactKey> extensions = Arrays.asList(
                AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"),
                AppArtifactKey.fromString("io.quarkus:quarkus-kotlin"));
        final CodestartProject codestartProject = Codestarts
                .prepareProject(new CodestartInput(getPlatformDescriptor(), extensions,
                        true, getTestInputData()));
        Codestarts.generateProject(codestartProject, projectPath.resolve("maven-resteasy-kotlin"));
    }

    @Test
    void generateCodestartProjectMavenResteasyScala() throws IOException {
        final List<AppArtifactKey> extensions = Arrays.asList(
                AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"),
                AppArtifactKey.fromString("io.quarkus:quarkus-scala"));
        final CodestartProject codestartProject = Codestarts
                .prepareProject(new CodestartInput(getPlatformDescriptor(), extensions,
                        true, getTestInputData()));
        Codestarts.generateProject(codestartProject, projectPath.resolve("maven-resteasy-scala"));
    }

    @Test
    void generateCodestartProjectGradleResteasyJava() throws IOException {
        final List<AppArtifactKey> extensions = Arrays.asList(AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"));
        final CodestartProject codestartProject = Codestarts
                .prepareProject(new CodestartInput(getPlatformDescriptor(), extensions,
                        true, getTestInputData(Collections.singletonMap("buildtool.name", "gradle"))));
        Codestarts.generateProject(codestartProject, projectPath.resolve("gradle-resteasy-java"));
    }

    @Test
    void generateCodestartProjectGradleResteasyKotlin() throws IOException {
        final List<AppArtifactKey> extensions = Arrays.asList(
                AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"),
                AppArtifactKey.fromString("io.quarkus:quarkus-kotlin"));
        final CodestartProject codestartProject = Codestarts
                .prepareProject(new CodestartInput(getPlatformDescriptor(), extensions,
                        true, getTestInputData(Collections.singletonMap("buildtool.name", "gradle"))));
        Codestarts.generateProject(codestartProject, projectPath.resolve("gradle-resteasy-kotlin"));
    }

    @Test
    void generateCodestartProjectGradleResteasyScala() throws IOException {
        final List<AppArtifactKey> extensions = Arrays.asList(
                AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"),
                AppArtifactKey.fromString("io.quarkus:quarkus-scala"));
        final CodestartProject codestartProject = Codestarts
                .prepareProject(new CodestartInput(getPlatformDescriptor(), extensions,
                        true, getTestInputData(Collections.singletonMap("buildtool.name", "gradle"))));
        Codestarts.generateProject(codestartProject, projectPath.resolve("gradle-resteasy-scala"));
    }
}
