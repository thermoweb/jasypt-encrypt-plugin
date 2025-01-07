package org.thermoweb.intellij.plugin.encrypt.vault;

import org.thermoweb.intellij.plugin.encrypt.Algorithms;

import com.intellij.credentialStore.Credentials;
import org.thermoweb.intellij.plugin.encrypt.IvGenerators;

public record CipherConfiguration(Algorithms algorithm, IvGenerators ivGenerator, String password) {

    public CipherConfiguration(Credentials credentials) {
        this(Algorithms.fromCode(credentials.getUserName()), IvGenerators.fromCode(credentials.getUserName()),  credentials.getPasswordAsString());
    }
}
