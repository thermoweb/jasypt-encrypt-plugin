package org.thermoweb.intellij.plugin.encrypt.cipher;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thermoweb.intellij.plugin.encrypt.Algorithms;
import org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog;
import org.thermoweb.intellij.plugin.encrypt.JasyptPluginSettings;
import org.thermoweb.intellij.plugin.encrypt.Notifier;
import org.thermoweb.intellij.plugin.encrypt.exceptions.JasyptPluginException;
import org.thermoweb.intellij.plugin.encrypt.vault.CipherConfiguration;
import org.thermoweb.intellij.plugin.encrypt.vault.SecretVault;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.impl.source.tree.LeafPsiElement;

import static org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog.*;

public class CipherDecryptCommand {
    private final Pattern pattern = Pattern.compile("ENC\\((.*)\\)");
    private final LeafPsiElement property;

    private CipherDecryptCommand(LeafPsiElement property) {
        this.property = Objects.requireNonNull(property);
    }

    public static CipherDecryptCommand of(LeafPsiElement property) {
        return new CipherDecryptCommand(property);
    }

    public boolean check() {
        boolean isTextEncapsulated = property.getText().trim().startsWith("ENC(");
        Optional<CipherConfiguration> storedConfiguration = SecretVault.getSecrets(property.getContainingFile().getVirtualFile().getPath());
        if (storedConfiguration.isEmpty()) {
            return false;
        }

        CipherConfiguration configuration = storedConfiguration.get();
        String textToUncrypt = getTextToUncrypt(property.getText(), isTextEncapsulated);
        try {
            CipherUtils.decrypt(textToUncrypt, configuration.password(), configuration.algorithm().getCode());
            return true;
        } catch (JasyptPluginException e) {
            return false;
        }
    }

    public void execute() {

        boolean isTextEncapsulated = property.getText().trim().startsWith("ENC(");
        Optional<CipherConfiguration> storedConfiguration = SecretVault.getSecrets(property.getContainingFile().getVirtualFile().getPath());
        storedConfiguration.ifPresentOrElse(configuration -> {
                    String textToUncrypt = getTextToUncrypt(property.getText(), isTextEncapsulated);
                    try {
                        String clearText = CipherUtils.decrypt(textToUncrypt, configuration.password(), configuration.algorithm().getCode());
                        setClearText(clearText);
                    } catch (JasyptPluginException e) {
                        askAndDecrypt(isTextEncapsulated);
                    }
                },
                () -> askAndDecrypt(isTextEncapsulated));
    }

    private void askAndDecrypt(boolean isTextEncapsulated) {
        CipherInformationsDialog dialog = new CipherInformationsDialog(JasyptPluginSettings.getInstance(property.getProject()), isTextEncapsulated);
        if (!dialog.showAndGet()) {
            return;
        }
        Map<String, String> values = dialog.getValues();
        String textToUncrypt = getTextToUncrypt(property.getText(), values.get(ENCAPSULATE_FIELD_NAME));
        String password = values.get(PASSWORD_FIELD_NAME);
        String algorithm = values.get(ALGORITHM_FIELD_NAME);
        try {
            String clearText = CipherUtils.decrypt(textToUncrypt, password, algorithm);
            if ("true".equals(values.get(REMEMBER_PASSWORD))) {
                SecretVault.storeSecret(property.getContainingFile().getVirtualFile().getPath(), new CipherConfiguration(Algorithms.fromCode(algorithm), password));
            }
            JasyptPluginSettings.updateSettings(property.getProject(), dialog.getValues());
            setClearText(clearText);
        } catch (JasyptPluginException e) {
            Notifier.notifyError(property.getProject(), "Failed to decrypt string, please verify provided password or algorithm.");
        }
    }

    private String getTextToUncrypt(String text, String isEncapsulated) {
        return getTextToUncrypt(text, "true".equals(isEncapsulated));
    }

    private String getTextToUncrypt(String text, boolean isEncapsulated) {
        Matcher matches = pattern.matcher(text);
        return matches.find() && isEncapsulated ?
                matches.group(1) :
                text;
    }

    private void setClearText(String clearText) {
        WriteCommandAction.runWriteCommandAction(property.getProject(), () -> {
            property.replaceWithText(clearText);
        });
    }
}
