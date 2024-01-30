package org.thermoweb.intellij.plugin.encrypt.cipher;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog;
import org.thermoweb.intellij.plugin.encrypt.JasyptPluginSettings;
import org.thermoweb.intellij.plugin.encrypt.Notifier;
import org.thermoweb.intellij.plugin.encrypt.exceptions.JasyptPluginException;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiLanguageInjectionHost;

import static org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog.*;

public class CipherDecryptCommand {
    private final Pattern pattern = Pattern.compile("ENC\\((.*)\\)");
    private final PsiLanguageInjectionHost property;

    private CipherDecryptCommand(PsiLanguageInjectionHost property) {
        this.property = Objects.requireNonNull(property);
    }

    public static CipherDecryptCommand of(PsiLanguageInjectionHost property) {
        return new CipherDecryptCommand(property);
    }

    public void execute() {
        boolean isTextEncapsulated = Optional.ofNullable(property.getText())
                .map(String::trim)
                .map(s -> s.startsWith("ENC("))
                .orElse(false);
        CipherInformationsDialog dialog = new CipherInformationsDialog(JasyptPluginSettings.getInstance(property.getProject()), isTextEncapsulated);
        if (!dialog.showAndGet()) {
            return;
        }
        Map<String, String> values = dialog.getValues();
        try {
            String textToUncrypt = getTextToUncrypt(property.getText(), values.get(ENCAPSULATE_FIELD_NAME));
            String clearText = CipherUtils.decrypt(textToUncrypt, values.get(PASSWORD_FIELD_NAME), values.get(ALGORITHM_FIELD_NAME));
            setClearText(clearText);
        } catch (JasyptPluginException e) {
            Notifier.notifyError(property.getProject(), "Failed to decrypt string, please verify provided password or algorithm.");
        }
    }

    private String getTextToUncrypt(String text, String isEncapsulated) {
        Matcher matches = pattern.matcher(text);
        return matches.find() && "true".equals(isEncapsulated) ?
                matches.group(1) :
                text;
    }

    private void setClearText(String clearText) {
        WriteCommandAction.runWriteCommandAction(property.getProject(), () -> {
            property.updateText(clearText);
        });
    }
}
