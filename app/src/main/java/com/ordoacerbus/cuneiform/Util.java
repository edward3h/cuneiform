package com.ordoacerbus.cuneiform;

import java.util.Collection;
import java.util.stream.Collectors;

public class Util {
    private Util(){}

    public static String safeName(String name) {
        return name.replaceAll("\\W+", "_");
    }

    public static String keywordsClasses(Collection<Keyword> keywords) {
        return keywords.stream()
                .map(Util::keywordClass)
                .collect(Collectors.joining(" "));
    }

    public static String keywordClass(Keyword keyword) {
        return "kw-" + safeName(keyword.keyword().toLowerCase());
    }
}
