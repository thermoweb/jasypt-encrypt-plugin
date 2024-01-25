package org.thermoweb.intellij.plugin.encrypt.actions;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.thermoweb.intellij.plugin.encrypt.Notifier;
import org.thermoweb.intellij.plugin.encrypt.cipher.CipherUtils;
import org.thermoweb.intellij.plugin.encrypt.exceptions.JasyptPluginException;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;

import static org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog.ALGORITHM_FIELD_NAME;
import static org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog.PASSWORD_FIELD_NAME;

public class UncryptStringAction extends JasyptAction {

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        super.actionPerformed(event);
        if (!dialog.showAndGet()) {
            return;
        }

        Map<String, String> values = dialog.getValues();
        final Optional<String> textToUncrypt = getSelectedText();
        textToUncrypt.ifPresent(text -> {
            WriteCommandAction.runWriteCommandAction(project,
                    () -> {
                        try {
                            document.replaceString(primaryCaret.getSelectionStart(), primaryCaret.getSelectionEnd(),
                                    CipherUtils.decrypt(text, values.get(PASSWORD_FIELD_NAME), values.get(ALGORITHM_FIELD_NAME)));
                        } catch (JasyptPluginException e) {
                            Notifier.notifyError(project, "Failed to decrypt string, please verify provided password or algorithm.");
                        }
                    });
            updateSettings();
            primaryCaret.removeSelection();
        });


    }
}
