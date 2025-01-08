package org.thermoweb.intellij.plugin.encrypt;

import java.awt.event.MouseEvent;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.thermoweb.intellij.plugin.encrypt.cipher.CipherDecryptCommand;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.LeafPsiElement;

public class DecryptLineMarkerProvider implements LineMarkerProvider {

    @Override
    public LineMarkerInfo<PsiElement> getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof LeafPsiElement
                && element.getText().startsWith("ENC(")
                && element.getParent() instanceof PsiLanguageInjectionHost
        ) {
            Icon cryptedIcon = AllIcons.Nodes.Private;
            if (checkStoredPassword(element)) {
                cryptedIcon = AllIcons.Nodes.Public;
            }
            return new LineMarkerInfo<>(element,
                    element.getTextRange(),
                    cryptedIcon,
                    elem -> "Decrypt string with jasypt",
                    new DecryptNavigationHandler(),
                    GutterIconRenderer.Alignment.CENTER,
                    () -> "Decrypt string with jasypt");
        }
        return null;
    }

    private boolean checkStoredPassword(PsiElement element) {
        if (element instanceof LeafPsiElement property) {
            return CipherDecryptCommand.of(property).check();
        }

        return false;
    }

    static class DecryptNavigationHandler implements GutterIconNavigationHandler<PsiElement> {

        @Override
        public void navigate(MouseEvent e, PsiElement elt) {
            if (elt instanceof LeafPsiElement property) {
                CipherDecryptCommand.of(property).execute();
            }
        }
    }
}
