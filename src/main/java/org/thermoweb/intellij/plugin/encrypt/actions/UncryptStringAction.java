package org.thermoweb.intellij.plugin.encrypt.actions;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.thermoweb.intellij.plugin.encrypt.Notifier;
import org.thermoweb.intellij.plugin.encrypt.cipher.CipherUtils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;

public class UncryptStringAction extends JasyptAction {

	@Override
	public void actionPerformed(@NotNull final AnActionEvent event) {
		super.actionPerformed(event);
		getSecretsFromCurrentFilePath()
				.ifPresentOrElse(cipherConfiguration -> {
							Optional<String> selectedText = getSelectedText();
							CipherUtils.decrypt(selectedText.orElseThrow(), cipherConfiguration)
									.ifSuccessOrElse(
											this::setClearText,
											error -> askAndDecrypt()
									);
						},
						this::askAndDecrypt);
	}

	private void setClearText(String clearText) {
		WriteCommandAction.runWriteCommandAction(project,
				() -> document.replaceString(primaryCaret.getSelectionStart(), primaryCaret.getSelectionEnd(), clearText));
	}

	private void askAndDecrypt() {
		if (!dialog.showAndGet()) {
			return;
		}

		final Optional<String> textToUncrypt = getSelectedText();
		textToUncrypt.ifPresent(text -> {
			WriteCommandAction.runWriteCommandAction(project,
					() -> CipherUtils.decrypt(text, dialog.getCipherConfiguration())
							.ifSuccessOrElse(
									this::setClearText,
									this::handleError
							)
			);
			updateSettings();
			primaryCaret.removeSelection();
		});
	}

	private void handleError(CipherUtils.CipherError cipherError) {
		if (cipherError == CipherUtils.CipherError.CANT_USE_NO_IV_WITH_AES) {
			Notifier.notifyError(project, "NoIvGenerator is not supported for AES algorithms");
		} else {
			Notifier.notifyError(project, "Failed to decrypt string, please verify provided password or algorithm.");
		}
	}
}
