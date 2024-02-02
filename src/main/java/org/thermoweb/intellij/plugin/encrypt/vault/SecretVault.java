package org.thermoweb.intellij.plugin.encrypt.vault;

import java.util.Optional;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

public class SecretVault {

    private SecretVault() {
    }

    public static Optional<String> getSecret(String key) {
        return getCredentials(key).map(Credentials::getPasswordAsString);
    }

    public static Optional<CipherConfiguration> getSecrets(String key) {
        return getCredentials(key).map(CipherConfiguration::new);
    }

    private static Optional<Credentials> getCredentials(String key) {
        CredentialAttributes attributes = createCredentialAttributes(key);
        PasswordSafe passwordSafe = PasswordSafe.getInstance();
        return Optional.ofNullable(passwordSafe.get(attributes));
    }

    public static void storeSecret(String key, String secret) {
        CredentialAttributes attributes = createCredentialAttributes(key);
        Credentials credentials = new Credentials("jasypt-plugin", secret);
        PasswordSafe.getInstance().set(attributes, credentials);
    }

    public static void storeSecret(String key, CipherConfiguration configuration) {
        CredentialAttributes attributes = createCredentialAttributes(key);
        Credentials credentials = new Credentials(configuration.algorithm().getCode(), configuration.password());
        PasswordSafe.getInstance().set(attributes, credentials);
    }

    private static CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName("JasyptPlugin", key));
    }
}
