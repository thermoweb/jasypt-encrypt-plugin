package org.thermoweb.intellij.plugin.encrypt;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

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
		final String cipheredPassword = "true".equals(values.get(ENCAPSULATE_FIELD_NAME)) ? "ENC(" + newValue + ")" : newValue;
		WriteCommandAction.runWriteCommandAction(project,
				() -> document.replaceString(primaryCaret.getSelectionStart(), primaryCaret.getSelectionEnd(), cipheredPassword));
		updateSettings();
		primaryCaret.removeSelection();
	}
}
