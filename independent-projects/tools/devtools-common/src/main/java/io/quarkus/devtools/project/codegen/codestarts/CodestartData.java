package io.quarkus.devtools.project.codegen.codestarts;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CodestartData {

    private CodestartData() {
    }

    public enum DefaultKeys {
        /**
         * BOM_GROUP_ID("bom_groupId"),
         * BOM_ARTIFACT_ID("bom_artifactId"),
         * BOM_VERSION("bom_version"),
         * PROJECT_GROUP_ID("project_groupId"),
         * PROJECT_ARTIFACT_ID("project.artifactId", "project_artifactId"),
         * PROJECT_VERSION("project.version", "project_version"),
         * QUARKUS_PLUGIN_VERSION("plugin_version"),
         * QUARKUS_VERSION("quarkus_version"),
         * PACKAGE_NAME("package_name"),
         * MAVEN_REPOSITORIES("maven_repositories"),
         * MAVEN_PLUGIN_REPOSITORIES("maven_plugin_repositories"),
         * SOURCE_TYPE("source_type"),
         * BUILD_FILE("build_file"),
         * BUILD_DIRECTORY("build_dir"),
         * ADDITIONAL_GITIGNORE_ENTRIES("additional_gitignore_entries"),
         * CLASS_NAME("class_name"),
         * EXTENSIONS("extensions"),
         * IS_SPRING("is_spring"),
         * RESOURCE_PATH("path"),
         * JAVA_TARGET("java_target");
         * 
         * private final String key;
         * private final String oldKey;
         * 
         * DefaultKeys(String key, String oldKey) {
         * this.key = key;
         * this.oldKey = oldKey;
         * }
         **/
    }

    static Map<String, Object> mergeData(final Codestart codestart, final String languageName, final Map<String, Object> data) {
        return mergeMaps(Stream.of(codestart.getLocalData(languageName), data));
    }

    static Map<String, Object> mergeMaps(final Stream<Map<String, Object>> stream) {
        return stream
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    static Map<String, String> mergePartials(final Stream<Map<String, String>> stream) {
        return stream
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
