package io.quarkus.devtools.project.codegen.codestarts;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class CodestartData {

    private CodestartData() {}


    static Map<String, Object> mergeData(final Codestart codestart, final String languageName, final Map<String, Object> data) {
        return mergeMaps(Stream.of(codestart.getLocalData(languageName), data));
    }

    static Map<String, Object> mergeMaps(final Stream<Map<String, Object>> stream) {
        return stream
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


}
