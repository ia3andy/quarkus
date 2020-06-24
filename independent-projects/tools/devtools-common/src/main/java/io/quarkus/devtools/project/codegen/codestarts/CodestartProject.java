package io.quarkus.devtools.project.codegen.codestarts;

import static io.quarkus.devtools.project.codegen.codestarts.Codestarts.mergeMaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CodestartProject {

    private final Codestart project;
    private final Codestart buildTool;
    private final Codestart language;
    private final Codestart config;
    private final List<Codestart> codestarts;
    private final CodestartInput codestartInput;

    CodestartProject(Codestart project, Codestart buildTool, Codestart language, Codestart config, List<Codestart> codestarts,
                     CodestartInput codestartInput) {
        this.project = project;
        this.buildTool = buildTool;
        this.language = language;
        this.config = config;
        this.codestarts = codestarts;
        this.codestartInput = codestartInput;
    }

    public Codestart getProject() {
        return project;
    }

    public Codestart getBuildTool() {
        return buildTool;
    }

    public Codestart getLanguage() {
        return language;
    }

    public Codestart getConfig() {
        return config;
    }

    public List<Codestart> getCodestarts() {
        return codestarts;
    }

    public CodestartInput getCodestartInput() {
        return codestartInput;
    }

    public List<Codestart> getDefaultCodestart() {
        return Arrays.asList(
                this.getProject(),
                this.getBuildTool(),
                this.getLanguage(),
                this.getConfig());
    }

    public Stream<Codestart> getAllCodestartsStream() {
        return Stream.concat(getDefaultCodestart().stream(), getCodestarts().stream());
    }

    public String getLanguageName() {
        return language.getSpec().getName();
    }

    public Map<String, Object> getSharedData() {
        final Stream<Map<String, Object>> codestartsGlobal = getAllCodestartsStream()
                .map(c -> c.getSharedData(getLanguageName()));
        return mergeMaps(Stream.concat(codestartsGlobal, Stream.of(getCodestartInput().getData())));
    }

    public Map<String, Object> getDepsData() {
        final Map<String, List<CodestartSpec.CodestartDep>> depsData = new HashMap<>();
        final List<CodestartSpec.CodestartDep> extensionsAsDeps = codestartInput.getExtensions().stream().map(k -> k.getGroupId() + ":" + k.getArtifactId()).map(CodestartSpec.CodestartDep::new).collect(Collectors.toList());
        depsData.put("dependencies", new ArrayList<>(extensionsAsDeps));
        depsData.put("testDependencies", new ArrayList<>());
        getAllCodestartsStream()
            .map(Codestart::getSpec)
            .flatMap(s -> Stream.of(s.getBaseSpec(), s.getLanguageSpec(getLanguageName())))
            .forEach(d -> {
                depsData.get("dependencies").addAll(d.getDependencies());
                depsData.get("testDependencies").addAll(d.getTestDependencies());
            });
        return Collections.unmodifiableMap(depsData);
    }
}
