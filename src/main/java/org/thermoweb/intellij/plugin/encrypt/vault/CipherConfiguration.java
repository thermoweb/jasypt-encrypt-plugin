package org.thermoweb.intellij.plugin.encrypt.vault;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import org.thermoweb.intellij.plugin.encrypt.Algorithms;
import org.thermoweb.intellij.plugin.encrypt.IvGenerators;
import org.thermoweb.intellij.plugin.encrypt.exceptions.JasyptPluginException;

import com.intellij.credentialStore.Credentials;

public record CipherConfiguration(int version, Algorithms algorithm, IvGenerators ivGenerator,
								  String password) implements Serializable {

	public CipherConfiguration(Credentials credentials) {
		this(1, Algorithms.fromCode(credentials.getUserName()),
				IvGenerators.RANDOMIVGENERATOR,
				credentials.getPasswordAsString());
	}

	public CipherConfiguration(Algorithms algorithm, IvGenerators ivGenerator, String password) {
		this(1, algorithm, ivGenerator, password);
	}

	public static String serialize(CipherConfiguration cipherConfiguration) throws JasyptPluginException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 ObjectOutputStream out = new ObjectOutputStream(baos)) {
			out.writeInt(cipherConfiguration.version());
			out.writeObject(cipherConfiguration);
			out.flush();
			return Base64.getEncoder().encodeToString(baos.toByteArray());
		} catch (IOException e) {
			throw new JasyptPluginException("Error during serialization", e);
		}
	}

	public static CipherConfiguration deserialize(String serializedCipherConfiguration) throws JasyptPluginException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(serializedCipherConfiguration));
			 ObjectInputStream in = new ObjectInputStream(bais)) {
			int versionNumber = in.readInt();
			CipherConfigurationVersion cipherConfigurationVersion = CipherConfigurationVersion.fromVersionNumber(versionNumber);
			return cipherConfigurationVersion.getReader().apply(in);
		} catch (IOException e) {
			throw new JasyptPluginException("Error during deserialization", e);
		}
	}
}
