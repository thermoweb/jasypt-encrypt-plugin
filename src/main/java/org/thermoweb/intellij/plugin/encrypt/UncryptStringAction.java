package org.thermoweb.intellij.plugin.encrypt;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import static org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog.ALGORITHM_FIELD_NAME;
import static org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog.ENCAPSULATE_FIELD_NAME;
import static org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog.PASSWORD_FIELD_NAME;

public class UncryptStringAction extends AnAction {
	private final Pattern pattern = Pattern.compile("ENC\\((.*)\\)");

	@Override
	public void actionPerformed(@NotNull final AnActionEvent event) {
		CipherInformationsDialog dialog = new CipherInformationsDialog();
		if (!dialog.showAndGet()) {
			return;
		}

		Map<String, String> values = dialog.getValues();

		Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
		Project project = event.getRequiredData(CommonDataKeys.PROJECT);
		Document document = editor.getDocument();
		Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
		if (primaryCaret.getSelectedText() == null) {
			return;
		}

		Matcher matches = pattern.matcher(primaryCaret.getSelectedText());
		final String selectedText = matches.find() && "true".equals(values.get(ENCAPSULATE_FIELD_NAME)) ?
				matches.group(1) :
				primaryCaret.getSelectedText();

		WriteCommandAction.runWriteCommandAction(project,
				() -> document.replaceString(primaryCaret.getSelectionStart(), primaryCaret.getSelectionEnd(),
						CipherUtils.decrypt(selectedText, values.get(PASSWORD_FIELD_NAME), values.get(ALGORITHM_FIELD_NAME))));

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
