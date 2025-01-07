package org.thermoweb.intellij.plugin.encrypt.cipher;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog;
import org.thermoweb.intellij.plugin.encrypt.JasyptPluginSettings;
import org.thermoweb.intellij.plugin.encrypt.Notifier;
import org.thermoweb.intellij.plugin.encrypt.vault.CipherConfiguration;
import org.thermoweb.intellij.plugin.encrypt.vault.SecretVault;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.impl.source.tree.LeafPsiElement;

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

		String textToUncrypt = getTextToUncrypt(property.getText(), isTextEncapsulated);
		return CipherUtils.decrypt(textToUncrypt, storedConfiguration.get()).isSuccess();
	}

	public void execute() {

		boolean isTextEncapsulated = property.getText().trim().startsWith("ENC(");
		Optional<CipherConfiguration> storedConfiguration = SecretVault.getSecrets(property.getContainingFile().getVirtualFile().getPath());
		storedConfiguration.ifPresentOrElse(configuration -> {
					String textToUncrypt = getTextToUncrypt(property.getText(), isTextEncapsulated);
					CipherUtils.decrypt(textToUncrypt, configuration)
							.ifSuccessOrElse(this::setClearText,
									error -> askAndDecrypt(isTextEncapsulated));
				},
				() -> askAndDecrypt(isTextEncapsulated));
	}

	private void askAndDecrypt(boolean isTextEncapsulated) {
		CipherInformationsDialog dialog = new CipherInformationsDialog(JasyptPluginSettings.getInstance(property.getProject()), isTextEncapsulated);
		if (!dialog.showAndGet()) {
			return;
		}
		String textToUncrypt = getTextToUncrypt(property.getText(), dialog.isEncapsulated());
		CipherConfiguration cipherConfiguration = dialog.getCipherConfiguration();
		CipherUtils.decrypt(textToUncrypt, cipherConfiguration)
				.ifSuccessOrElse(clearText -> {
							if (dialog.isRememberPassword()) {
								SecretVault.storeSecret(property.getContainingFile().getVirtualFile().getPath(),
										cipherConfiguration);
							}
							JasyptPluginSettings.updateSettings(property.getProject(), dialog.getValues());
							setClearText(clearText);
						},
						error -> Notifier.notifyError(property.getProject(), "Failed to decrypt string, please verify provided password or algorithm."));
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
