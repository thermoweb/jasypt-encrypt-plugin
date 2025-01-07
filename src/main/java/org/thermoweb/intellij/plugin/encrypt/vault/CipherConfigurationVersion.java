package org.thermoweb.intellij.plugin.encrypt.vault;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.function.Function;

public enum CipherConfigurationVersion {
	LEGACY(0, CipherConfigurationVersion::readLegacy),
	VERSION_1(1, CipherConfigurationVersion::readVersion1);

	private final int versionNumber;
	private final Function<ObjectInputStream, CipherConfiguration> reader;

	CipherConfigurationVersion(int versionNumber, Function<ObjectInputStream, CipherConfiguration> reader) {
		this.versionNumber = versionNumber;
		this.reader = reader;
	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public Function<ObjectInputStream, CipherConfiguration> getReader() {
		return reader;
	}

	public static CipherConfigurationVersion fromVersionNumber(int versionNumber) {
		for (CipherConfigurationVersion version : CipherConfigurationVersion.values()) {
			if (version.getVersionNumber() == versionNumber) {
				return version;
			}
		}
		throw new IllegalArgumentException("Unknown version number: " + versionNumber);
	}

	private static CipherConfiguration readVersion1(ObjectInputStream in) {
		try {
			return (CipherConfiguration) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Error during deserialization of configuration", e);
		}
	}

	private static CipherConfiguration readLegacy(ObjectInputStream in) {
		throw new UnsupportedOperationException("Can not deserialize configuration for legacy versions");
	}
}
