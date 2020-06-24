package io.quarkus.devtools.project.codegen.codestarts;

import io.quarkus.dependencies.Extension;
import io.quarkus.devtools.project.extensions.Extensions;
import io.quarkus.qute.Engine;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.quarkus.devtools.project.codegen.codestarts.CodestartLoader.loadAddCodestarts;

public class Codestarts {


    public static CodestartProject prepareProject(final CodestartInput input) throws IOException {
        final String buildToolCodeStart = "buildtool-" + input.getData().getOrDefault("buildtool", "maven");
        final Set<String> enabledCodestarts = Stream.concat(input.getDescriptor().getExtensions().stream()
                .filter(e -> input.getExtensions().contains(Extensions.toKey(e)))
                .map(Extension::getCodestart), Stream.of(buildToolCodeStart))
                .collect(Collectors.toSet());

        final Collection<Codestart> allCodestarts = loadAddCodestarts(input);
        final Codestart project = findEnabledCodestartOfTypeOrDefault(allCodestarts, enabledCodestarts,
                CodestartSpec.Type.PROJECT);
        final Codestart buildTool = findEnabledCodestartOfTypeOrDefault(allCodestarts, enabledCodestarts,
                CodestartSpec.Type.BUILDTOOL);
        final Codestart language = findEnabledCodestartOfTypeOrDefault(allCodestarts, enabledCodestarts,
                CodestartSpec.Type.LANGUAGE);
        final Codestart config = findEnabledCodestartOfTypeOrDefault(allCodestarts, enabledCodestarts,
                CodestartSpec.Type.CONFIG);
        final List<Codestart> codestarts = allCodestarts.stream()
                .filter(c -> CodestartSpec.Type.CODESTART.equals(c.getSpec().getType()))
                .filter(c -> c.getSpec().isDefault() || enabledCodestarts.contains(c.getSpec().getRef()))
                .filter(c -> !c.getSpec().isExample() || input.includeExample())
                .collect(Collectors.toList());

        return new CodestartProject(project, buildTool, language, config, codestarts, input);
    }

    public static void generateProject(final CodestartProject codestartProject, final Path targetDirectory) throws IOException {
        CodestartProcessor.checkTargetDir(targetDirectory);
        final String languageName = codestartProject.getLanguageName();
        final Map<String, Object> sharedData = codestartProject.getSharedData();
        final Engine engine = CodestartQute.newEngine();
        final Map<String, Object> data = mergeMaps(Stream.of(sharedData, codestartProject.getDepsData()));
        // TODO support yaml config
        codestartProject.getAllCodestartsStream()
            .forEach(c -> CodestartProcessor.processCodestart(codestartProject.getCodestartInput().getDescriptor(), engine, c, languageName, targetDirectory, data));
    }


    private static Codestart findEnabledCodestartOfTypeOrDefault(final Collection<Codestart> codestarts,
            final Set<String> enabledCodestarts, final CodestartSpec.Type type) {
        return codestarts.stream()
                .filter(c -> c.getSpec().getType().equals(type)
                        && (c.getSpec().isDefault() || enabledCodestarts.contains(c.getSpec().getName())))
                .min(Comparator.comparing(c -> c.getSpec().isDefault()))
                .orElseThrow(() -> new IllegalStateException("No matching codestart of type " + type + " has been found"));
    }

    static Map<String, Object> mergeData(final Codestart codestart, final String languageName, final Map<String, Object> data) {
        return mergeMaps(Stream.of(codestart.getLocalData(languageName), data));
    }

    static Map<String, Object> mergeMaps(final Stream<Map<String, Object>> stream) {
        // TODO we will need a deep merge here
        return stream
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
