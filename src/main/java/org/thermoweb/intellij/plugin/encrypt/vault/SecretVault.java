package org.thermoweb.intellij.plugin.encrypt.vault;

import java.util.Optional;

import org.thermoweb.intellij.plugin.encrypt.exceptions.JasyptPluginException;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

public class SecretVault {

    private SecretVault() {
    }

    public static Optional<CipherConfiguration> getSecrets(String key) {
        return getCredentials(key).flatMap(credentials -> {
            try {
                return Optional.of(CipherConfiguration.deserialize(credentials.getPasswordAsString()));
            } catch (Exception e) {
                if (credentials.getUserName() != null && credentials.getPassword() != null) {
                    return Optional.of(new CipherConfiguration(credentials));
                }
                return Optional.empty();
            }
        });
    }

    private static Optional<Credentials> getCredentials(String key) {
        CredentialAttributes attributes = createCredentialAttributes(key);
        PasswordSafe passwordSafe = PasswordSafe.getInstance();
        return Optional.ofNullable(passwordSafe.get(attributes));
    }

    public static void storeSecret(String key, CipherConfiguration configuration) {
        CredentialAttributes attributes = createCredentialAttributes(key);
		try {
			String serializedConfig = CipherConfiguration.serialize(configuration);
            Credentials credentials = new Credentials(null, serializedConfig);
            PasswordSafe.getInstance().set(attributes, credentials);
        } catch (JasyptPluginException e) {
            // do nothing
        }
    }

    private static CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName("JasyptPlugin", key));
    }
}
