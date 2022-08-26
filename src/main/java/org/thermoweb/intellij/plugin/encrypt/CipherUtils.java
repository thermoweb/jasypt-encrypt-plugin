package org.thermoweb.intellij.plugin.encrypt;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.iv.RandomIvGenerator;

public class CipherUtils {

	public String encrypt(final String value, final String password) {
		return stringEncryptor(password).encrypt(value);
	}

	public String decrypt(final String value, final String password) {
		return stringEncryptor(password).decrypt(value);
	}

	private StringEncryptor stringEncryptor(final String password) {
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(password);
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm("PBEWithHmacSHA512AndAES_128");
		encryptor.setIvGenerator(new RandomIvGenerator());
		encryptor.setConfig(config);
		return encryptor;
	}
}
