package org.thermoweb.intellij.plugin.encrypt;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.iv.RandomIvGenerator;

public class CipherUtils {
	private CipherUtils() {

	}

	public static String encrypt(final String value, final String password, final String algorithm) {
		return stringEncryptor(password, algorithm).encrypt(value);
	}

	public static String decrypt(final String value, final String password, final String algorithm) {
		return stringEncryptor(password, algorithm).decrypt(value);
	}

	private static StringEncryptor stringEncryptor(final String password, final String algorithm) {
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(password);
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm(algorithm);
		encryptor.setIvGenerator(new RandomIvGenerator());
		encryptor.setConfig(config);
		return encryptor;
	}
}
