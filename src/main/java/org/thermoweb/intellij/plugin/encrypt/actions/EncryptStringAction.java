package org.thermoweb.intellij.plugin.encrypt.actions;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.thermoweb.intellij.plugin.encrypt.cipher.CipherUtils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;

import static org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog.*;

public class EncryptStringAction extends JasyptAction {
	@Override
	public void actionPerformed(@NotNull final AnActionEvent event) {
		super.actionPerformed(event);
		if (!dialog.showAndGet()) {
			return;
		}

		Map<String, String> values = dialog.getValues();

		String newValue = CipherUtils.encrypt(primaryCaret.getSelectedText(), values.get(PASSWORD_FIELD_NAME), values.get(ALGORITHM_FIELD_NAME));
		final String cipheredString = "true".equals(values.get(ENCAPSULATE_FIELD_NAME)) ? "ENC(" + newValue + ")" : newValue;
		WriteCommandAction.runWriteCommandAction(project,
				() -> document.replaceString(primaryCaret.getSelectionStart(), primaryCaret.getSelectionEnd(), cipheredString));
		updateSettings();
		primaryCaret.removeSelection();
	}
}
