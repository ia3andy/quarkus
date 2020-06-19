package io.quarkus.devtools.project.codegen.codestarts;

import java.util.List;
import java.util.Map;

class CodestartProject {

    private final Codestart project;
    private final Codestart buildTool;
    private final Codestart language;
    private final Codestart config;
    private final List<Codestart> codestarts;
    private final Map<String, Object> inputData;

    CodestartProject(Codestart project, Codestart buildTool, Codestart language, Codestart config, List<Codestart> codestarts,
            Map<String, Object> inputData) {
        this.project = project;
        this.buildTool = buildTool;
        this.language = language;
        this.config = config;
        this.codestarts = codestarts;
        this.inputData = inputData;
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

    public Map<String, Object> getInputData() {
        return inputData;
    }
}
