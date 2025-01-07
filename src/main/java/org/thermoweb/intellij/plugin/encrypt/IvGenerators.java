package org.thermoweb.intellij.plugin.encrypt;

import java.util.Arrays;

public  enum IvGenerators {

    RANDOMIVGENERATOR("RandomIvGenerator"),
    NOIVGENERATOR("NoIvGenerator");

    private final String code;

    IvGenerators(final String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static IvGenerators fromCode(String code) {
        return Arrays.stream(values())
                .filter(a -> a.getCode().equals(code))
                .findFirst()
                .orElse(IvGenerators.RANDOMIVGENERATOR);
    }

}
