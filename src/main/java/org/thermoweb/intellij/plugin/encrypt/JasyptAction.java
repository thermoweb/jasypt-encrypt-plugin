package org.thermoweb.intellij.plugin.encrypt;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import static org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog.ENCAPSULATE_FIELD_NAME;

public class JasyptAction extends AnAction {
    private final Pattern pattern = Pattern.compile("ENC\\((.*)\\)");

    protected CipherInformationsDialog dialog;
    protected Editor editor;
    protected Project project;
    protected Document document;
    protected Caret primaryCaret;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        dialog = new CipherInformationsDialog();

        editor = event.getRequiredData(CommonDataKeys.EDITOR);
        project = event.getRequiredData(CommonDataKeys.PROJECT);
        document = editor.getDocument();
        primaryCaret = editor.getCaretModel().getPrimaryCaret();
    }

    @Override
    public void update(@NotNull final AnActionEvent event) {
        // Get required data keys
        project = event.getProject();
        editor = event.getData(CommonDataKeys.EDITOR);

        // Set visibility only in the case of
        // existing project editor, and selection
        event.getPresentation().setEnabledAndVisible(project != null && editor != null && editor.getSelectionModel().hasSelection());
    }

    protected Optional<String> getSelectedText() {
        return Optional.ofNullable(primaryCaret.getSelectedText())
                .map(this::matchSelected);
    }

    private String matchSelected(String text) {
        Matcher matches = pattern.matcher(text);
        return matches.find() && "true".equals(dialog.getValues().get(ENCAPSULATE_FIELD_NAME)) ?
                matches.group(1) :
                text;
    }
}
