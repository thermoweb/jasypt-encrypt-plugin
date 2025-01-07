package org.thermoweb.intellij.plugin.encrypt.cipher;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.thermoweb.intellij.plugin.encrypt.IvGenerators;
import org.thermoweb.intellij.plugin.encrypt.Result;
import org.thermoweb.intellij.plugin.encrypt.exceptions.JasyptPluginException;
import org.thermoweb.intellij.plugin.encrypt.vault.CipherConfiguration;

public class CipherUtils {
    private CipherUtils() {
    }

    public static Result<String, CipherError> encrypt(final String value, final CipherConfiguration cipherConfiguration) {
		try {
            return Result.ok(stringEncryptor(cipherConfiguration).encrypt(value));
        } catch (JasyptPluginException e) {
            return Result.error(CipherError.CANT_USE_NO_IV_WITH_AES, e.getMessage());
        } catch (RuntimeException e) {
            return Result.error(CipherError.UNKNOWN_ERROR, "Unknown error");
        }
    }

    public static Result<String, CipherError> decrypt(final String value, final CipherConfiguration cipherConfiguration) {
        try {
            return Result.ok(stringEncryptor(cipherConfiguration).decrypt(value));
        } catch (JasyptPluginException e) {
            return Result.error(CipherError.CANT_USE_NO_IV_WITH_AES, e.getMessage());
        } catch (RuntimeException e) {
            return Result.error(CipherError.UNKNOWN_ERROR, "Unknown error");
        }
    }

    private static StringEncryptor stringEncryptor(final CipherConfiguration cipherConfiguration) throws JasyptPluginException {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(cipherConfiguration.password());
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        String algorithm = cipherConfiguration.algorithm().getCode();
        encryptor.setAlgorithm(algorithm);

        String ivGenerator = cipherConfiguration.ivGenerator().getCode();
        if (algorithm.contains("AES") && ivGenerator.equals(IvGenerators.NOIVGENERATOR.getCode())) {
            throw new JasyptPluginException("NoIvGenerator is not supported for AES algorithms");
        }
        encryptor.setIvGenerator(IvGenerators.fromCode(ivGenerator).orElse(IvGenerators.RANDOMIVGENERATOR).getGenerator());
        encryptor.setConfig(config);

        return encryptor;
    }

    public enum CipherError {
        CANT_USE_NO_IV_WITH_AES,
        UNKNOWN_ERROR,
    }
}
