package org.thermoweb.intellij.plugin.encrypt;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

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
        if (textToUncrypt.isEmpty()) {
            return;
        }

        WriteCommandAction.runWriteCommandAction(project,
                () -> document.replaceString(primaryCaret.getSelectionStart(), primaryCaret.getSelectionEnd(),
                        CipherUtils.decrypt(textToUncrypt.get(), values.get(PASSWORD_FIELD_NAME), values.get(ALGORITHM_FIELD_NAME))));
        updateSettings();
        primaryCaret.removeSelection();
    }
}
