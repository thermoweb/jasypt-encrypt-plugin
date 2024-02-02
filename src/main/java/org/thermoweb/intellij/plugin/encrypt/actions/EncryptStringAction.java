package org.thermoweb.intellij.plugin.encrypt.actions;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.thermoweb.intellij.plugin.encrypt.Algorithms;
import org.thermoweb.intellij.plugin.encrypt.cipher.CipherUtils;
import org.thermoweb.intellij.plugin.encrypt.vault.CipherConfiguration;
import org.thermoweb.intellij.plugin.encrypt.vault.SecretVault;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.impl.text.TextEditorImpl;
import com.intellij.openapi.vfs.VirtualFile;

import static org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog.*;

public class EncryptStringAction extends JasyptAction {
    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        super.actionPerformed(event);
        Optional<String> filePath = Optional.ofNullable((TextEditorImpl) event.getDataContext().getData("fileEditor"))
                .map(TextEditorImpl::getFile)
                .map(VirtualFile::getPath);
        filePath.flatMap(SecretVault::getSecrets)
                .ifPresent(storedConfiguration -> {
                    dialog.setAlgorithm(storedConfiguration.algorithm().getCode());
                    dialog.setPassword(storedConfiguration.password());
                });


        if (!dialog.showAndGet()) {
            return;
        }

        Map<String, String> values = dialog.getValues();

        String password = values.get(PASSWORD_FIELD_NAME);
        String algorithm = values.get(ALGORITHM_FIELD_NAME);
        String newValue = CipherUtils.encrypt(primaryCaret.getSelectedText(), password, algorithm);
        final String cipheredString = "true".equals(values.get(ENCAPSULATE_FIELD_NAME)) ? "ENC(" + newValue + ")" : newValue;
        WriteCommandAction.runWriteCommandAction(project,
                () -> document.replaceString(primaryCaret.getSelectionStart(), primaryCaret.getSelectionEnd(), cipheredString));
        updateSettings();
        if ("true".equals(values.get(REMEMBER_PASSWORD))) {
            filePath.ifPresent(path -> SecretVault.storeSecret(path, new CipherConfiguration(Algorithms.fromCode(algorithm), password)));
        }
        primaryCaret.removeSelection();
    }
}
