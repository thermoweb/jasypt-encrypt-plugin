package org.thermoweb.intellij.plugin.encrypt;

import org.junit.Test;
import org.thermoweb.intellij.plugin.encrypt.cipher.CipherUtils;
import org.thermoweb.intellij.plugin.encrypt.exceptions.JasyptPluginException;

import static org.junit.Assert.assertEquals;

public class CipherUtilsTest {

	@Test
	public void should_encrypt_and_decrypt_string_correctly() throws JasyptPluginException {
		String secretString = "secretString";
		String password = "password";
		String encryptedText = CipherUtils.encrypt(secretString, password, Algorithms.PBEWITHHMACSHA1ANDAES_128.getCode());
		String decryptedtext = CipherUtils.decrypt(encryptedText, password, Algorithms.PBEWITHHMACSHA1ANDAES_128.getCode());
		assertEquals(secretString, decryptedtext);
	}

}