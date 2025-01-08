package org.thermoweb.intellij.plugin.encrypt.actions;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.thermoweb.intellij.plugin.encrypt.CipherInformationsDialog;
import org.thermoweb.intellij.plugin.encrypt.JasyptPluginSettings;
import org.thermoweb.intellij.plugin.encrypt.vault.CipherConfiguration;
import org.thermoweb.intellij.plugin.encrypt.vault.SecretVault;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

public class JasyptAction extends AnAction {
    private final Pattern pattern = Pattern.compile("ENC\\((.*)\\)");

    protected CipherInformationsDialog dialog;
    protected Editor editor;
    protected Project project;
    protected Document document;
    protected Caret primaryCaret;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        editor = event.getData(CommonDataKeys.EDITOR);
        project = event.getProject();
        if (editor == null || project == null) {
            return;
        }
        document = editor.getDocument();
        primaryCaret = editor.getCaretModel().getPrimaryCaret();

        JasyptPluginSettings settings = JasyptPluginSettings.getInstance(project);
        boolean isEncapsulated = getSelectedText().map(String::trim).map(s -> s.startsWith("ENC(")).orElse(false);
        dialog = new CipherInformationsDialog(settings, isEncapsulated);
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

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    protected Optional<CipherConfiguration> getSecretsFromCurrentFilePath() {
        return getCurrentFilePath().flatMap(SecretVault::getSecrets);
    }

    protected Optional<String> getCurrentFilePath() {
        return Optional.ofNullable(PsiDocumentManager.getInstance(project))
                .map(psiDocumentManager -> psiDocumentManager.getPsiFile(document))
                .map(PsiFile::getOriginalFile)
                .map(PsiFile::getVirtualFile)
                .map(VirtualFile::getPath);
    }

    protected Optional<String> getSelectedText() {
        return Optional.ofNullable(primaryCaret.getSelectedText())
                .map(this::matchSelected);
    }

    private String matchSelected(String text) {
        Matcher matches = pattern.matcher(text);
        return matches.find() && Optional.ofNullable(dialog).map(CipherInformationsDialog::isEncapsulated).orElse(false) ?
                matches.group(1) :
                text;
    }

    protected void updateSettings() {
        Map<String, String> values = dialog.getValues();
        JasyptPluginSettings.updateSettings(project, values);
    }
}
