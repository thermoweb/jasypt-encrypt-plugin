package org.thermoweb.intellij.plugin.encrypt;

import org.junit.Test;
import org.thermoweb.intellij.plugin.encrypt.cipher.CipherUtils;
import org.thermoweb.intellij.plugin.encrypt.vault.CipherConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CipherUtilsTest {

	@Test
	public void should_encrypt_and_decrypt_string_correctly() {
		String secretString = "secretString";
		String password = "password";
		CipherConfiguration cipherConfiguration = new CipherConfiguration(Algorithms.PBEWITHHMACSHA1ANDAES_128, IvGenerators.RANDOMIVGENERATOR, password);
		Result<String, CipherUtils.CipherError> encryptionResult = CipherUtils.encrypt(secretString, cipherConfiguration);
		assertTrue(encryptionResult.isSuccess());
		String encryptedText = ((Result.Ok<String, CipherUtils.CipherError>) encryptionResult).value();
		Result<String, CipherUtils.CipherError> decryptionResult = CipherUtils.decrypt(encryptedText, cipherConfiguration);
		assertTrue(decryptionResult.isSuccess());
		String decryptedtext = ((Result.Ok<String, CipherUtils.CipherError>) decryptionResult).value();
		assertEquals(secretString, decryptedtext);
	}

}