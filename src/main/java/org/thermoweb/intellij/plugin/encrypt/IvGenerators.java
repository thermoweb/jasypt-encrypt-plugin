package org.thermoweb.intellij.plugin.encrypt;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import org.jasypt.iv.IvGenerator;
import org.jasypt.iv.NoIvGenerator;
import org.jasypt.iv.RandomIvGenerator;

public  enum IvGenerators {

    RANDOMIVGENERATOR("RandomIvGenerator", RandomIvGenerator::new),
    NOIVGENERATOR("NoIvGenerator", NoIvGenerator::new);

    private final String code;
    private final Supplier<IvGenerator> generatorSupplier;

    IvGenerators(String randomIvGenerator, Supplier<IvGenerator> generatorSupplier) {
        this.code = randomIvGenerator;
        this.generatorSupplier = generatorSupplier;
    }

    public IvGenerator getGenerator() {
        return this.generatorSupplier.get();
    }

    public String getCode() {
        return this.code;
    }

    public static Optional<IvGenerators> fromCode(String code) {
        return Arrays.stream(values())
                .filter(a -> a.getCode().equals(code))
                .findFirst();
    }

}
