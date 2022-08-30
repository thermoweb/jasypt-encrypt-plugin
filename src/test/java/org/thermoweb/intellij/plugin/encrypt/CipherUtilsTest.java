package org.thermoweb.intellij.plugin.encrypt;

import org.junit.Test;

import static org.junit.Assert.*;

public class CipherUtilsTest {

	@Test
	public void should_encrypt_and_decrypt_string_correctly() {
		String secretString = "secretString";
		String password = "password";
		String encryptedText = CipherUtils.encrypt(secretString, password, Algorithm.PBE.getCode());
		String decryptedtext = CipherUtils.decrypt(encryptedText, password, Algorithm.PBE.getCode());
		assertEquals(secretString, decryptedtext);
	}

}