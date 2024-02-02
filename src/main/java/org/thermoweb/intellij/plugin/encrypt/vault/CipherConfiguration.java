package org.thermoweb.intellij.plugin.encrypt.vault;

import org.thermoweb.intellij.plugin.encrypt.Algorithms;

import com.intellij.credentialStore.Credentials;

public record CipherConfiguration(Algorithms algorithm, String password) {

    public CipherConfiguration(Credentials credentials) {
        this(Algorithms.fromCode(credentials.getUserName()), credentials.getPasswordAsString());
    }
}
