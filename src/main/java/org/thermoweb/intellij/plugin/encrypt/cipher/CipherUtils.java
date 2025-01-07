package org.thermoweb.intellij.plugin.encrypt.cipher;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.iv.*;
import org.jasypt.salt.RandomSaltGenerator;
import org.thermoweb.intellij.plugin.encrypt.IvGenerators;
import org.thermoweb.intellij.plugin.encrypt.exceptions.JasyptPluginException;

import java.util.Random;

import static org.thermoweb.intellij.plugin.encrypt.IvGenerators.*;

public class CipherUtils {
    private CipherUtils() {
    }

    public static String encrypt(final String value, final String password, final String algorithm, final String ivGenerator) {
        return stringEncryptor(password, algorithm, ivGenerator).encrypt(value);
    }

    public static String decrypt(final String value, final String password, final String algorithm, final String ivGenerator) throws JasyptPluginException {
        try {
            return stringEncryptor(password, algorithm, ivGenerator).decrypt(value);
        } catch (EncryptionOperationNotPossibleException e) {
            throw new JasyptPluginException(e);
        }
    }

    private static StringEncryptor stringEncryptor(final String password, final String algorithm, final String ivGenerator) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm(algorithm);

        IvGenerators generator = IvGenerators.fromCode(ivGenerator);
        switch (generator) {
            case RANDOMIVGENERATOR:
                encryptor.setIvGenerator(new RandomIvGenerator());
                break;
            case NOIVGENERATOR:
                encryptor.setIvGenerator(new NoIvGenerator());
                break;
            default:
                break;
        }

        if (algorithm.contains("AES")) {
            config.setIvGenerator(new org.jasypt.iv.RandomIvGenerator());
        } else {
            config.setIvGenerator(new org.jasypt.iv.NoIvGenerator());
        }

        encryptor.setConfig(config);
        return encryptor;
    }
}
