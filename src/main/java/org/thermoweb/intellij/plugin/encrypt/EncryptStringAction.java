package org.thermoweb.intellij.plugin.encrypt;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class EncryptStringAction extends AnAction {
	private final CipherUtils cipherUtils = new CipherUtils();

	@Override
	public void actionPerformed(@NotNull final AnActionEvent event) {
		String password = Messages.showPasswordDialog("Password :", "Encrypt String");
		if (StringUtils.isEmpty(password)) {
			return;
		}

		Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
		Project project = event.getRequiredData(CommonDataKeys.PROJECT);
		Document document = editor.getDocument();
		Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();

		WriteCommandAction.runWriteCommandAction(project,
				() -> document.replaceString(primaryCaret.getSelectionStart(), primaryCaret.getSelectionEnd(),
						cipherUtils.encrypt(primaryCaret.getSelectedText(), password)));

		primaryCaret.removeSelection();
	}

	@Override
	public void update(@NotNull final AnActionEvent event) {
		// Get required data keys
		Project project = event.getProject();
		Editor editor = event.getData(CommonDataKeys.EDITOR);

		// Set visibility only in the case of
		// existing project editor, and selection
		event.getPresentation().setEnabledAndVisible(project != null && editor != null && editor.getSelectionModel().hasSelection());
	}
}
