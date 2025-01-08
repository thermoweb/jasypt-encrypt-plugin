package org.thermoweb.intellij.plugin.encrypt.actions;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.thermoweb.intellij.plugin.encrypt.Notifier;
import org.thermoweb.intellij.plugin.encrypt.cipher.CipherUtils;
import org.thermoweb.intellij.plugin.encrypt.vault.CipherConfiguration;
import org.thermoweb.intellij.plugin.encrypt.vault.SecretVault;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;

public class EncryptStringAction extends JasyptAction {
	@Override
	public void actionPerformed(@NotNull final AnActionEvent event) {
		super.actionPerformed(event);
		Optional<String> filePath = getCurrentFilePath();
		filePath.flatMap(SecretVault::getSecrets)
				.ifPresent(storedConfiguration -> {
					dialog.setAlgorithm(storedConfiguration.algorithm().getCode());
					dialog.setPassword(storedConfiguration.password());
				});

		if (!dialog.showAndGet()) {
			return;
		}
		CipherConfiguration cipherConfiguration = dialog.getCipherConfiguration();
		CipherUtils.encrypt(primaryCaret.getSelectedText(), cipherConfiguration)
				.ifSuccessOrElse(newValue -> {
							final String cipheredString = dialog.isEncapsulated() ? "ENC(" + newValue + ")" : newValue;
							WriteCommandAction.runWriteCommandAction(project,
									() -> document.replaceString(primaryCaret.getSelectionStart(), primaryCaret.getSelectionEnd(), cipheredString));
							updateSettings();
							if (dialog.isRememberPassword()) {
								filePath.ifPresent(path -> SecretVault.storeSecret(path, cipherConfiguration));
							}
						},
						this::handleError
				);

		primaryCaret.removeSelection();
	}

	private void handleError(CipherUtils.CipherError error) {
		if (error == CipherUtils.CipherError.CANT_USE_NO_IV_WITH_AES) {
			Notifier.notifyError(project, "You can't use NoIvGenerator with AES algorithms");
		} else {
			Notifier.notifyError(project, "unknown error during encryption");
		}
	}
}
