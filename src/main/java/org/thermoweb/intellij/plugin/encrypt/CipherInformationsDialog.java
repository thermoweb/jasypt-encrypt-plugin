package org.thermoweb.intellij.plugin.encrypt;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.ui.DialogPanel;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorComboBox;
import com.intellij.ui.PasswordFieldPanel;
import com.intellij.util.ui.JBInsets;

public class CipherInformationsDialog extends DialogWrapper {
    public static final String ALGORITHM_FIELD_NAME = "algorithm";
    public static final String PASSWORD_FIELD_NAME = "password";
    public static final String ENCAPSULATE_FIELD_NAME = "encapsulate";
    public static final String REMEMBER_PASSWORD = "rememberPassword";

    private final EditorComboBox comboBox = new EditorComboBox(ALGORITHM_FIELD_NAME);
    private final PasswordFieldPanel passwordTextField = new PasswordFieldPanel();
    private final JCheckBox encapsulateCheckBox = new JCheckBox(ENCAPSULATE_FIELD_NAME, true);
    private final JCheckBox rememberPasswordCheckBox = new JCheckBox(REMEMBER_PASSWORD, true);
    private final JasyptPluginSettings settings;
    private final boolean textIsEncapsulated;

    public CipherInformationsDialog(JasyptPluginSettings settings) {
        this(settings, false);
    }

    public CipherInformationsDialog(JasyptPluginSettings settings, boolean textIsEncapsulated) {
        super(true);
        this.settings = settings;
        this.textIsEncapsulated = textIsEncapsulated;
        passwordTextField.setName(PASSWORD_FIELD_NAME);
        encapsulateCheckBox.setText("surrounded by ENC(...)");
        encapsulateCheckBox.setName(ENCAPSULATE_FIELD_NAME);
        comboBox.setName(ALGORITHM_FIELD_NAME);
        rememberPasswordCheckBox.setName(REMEMBER_PASSWORD);
        setTitle("Password");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        DialogCreator dialogCreator = DialogCreator.builder();

        dialogCreator.add(new JLabel(PASSWORD_FIELD_NAME));
        dialogCreator.add(passwordTextField);
        dialogCreator.add(new JLabel(ALGORITHM_FIELD_NAME));
        comboBox.removeAllItems();
        comboBox.setEnabled(Algorithms.values().length > 1);
        Arrays.stream(Algorithms.values()).forEach(a -> comboBox.appendItem(a.getCode()));
        Optional.ofNullable(settings)
                .map(JasyptPluginSettings::getAlgorithm)
                .ifPresent(comboBox::setSelectedItem);
        dialogCreator.add(comboBox);

        Boolean encapsulatedSetting = Optional.ofNullable(settings)
                .map(JasyptPluginSettings::isEncapsulated)
                .orElse(true);
        encapsulateCheckBox.setSelected(encapsulatedSetting || textIsEncapsulated);
        dialogCreator.add(encapsulateCheckBox);

        rememberPasswordCheckBox.setText("remember password");
        Boolean rememberPasswordSetting = Optional.ofNullable(settings)
                .map(JasyptPluginSettings::isRememberPassword)
                .orElse(false);
        rememberPasswordCheckBox.setSelected(rememberPasswordSetting);
        dialogCreator.add(rememberPasswordCheckBox);

        return dialogCreator.build();
    }

    public Map<String, String> getValues() {
        Map<String, String> values = new HashMap<>();
        values.put(comboBox.getName(), comboBox.getText());
        values.put(passwordTextField.getName(), passwordTextField.getText());
        values.put(encapsulateCheckBox.getName(), String.valueOf(encapsulateCheckBox.isSelected()));
        values.put(rememberPasswordCheckBox.getName(), String.valueOf(rememberPasswordCheckBox.isSelected()));
        return values;
    }

    public void setPassword(String password) {
        passwordTextField.setText(password);
    }

    public void setAlgorithm(String algorithm) {
        comboBox.setSelectedItem(algorithm);
    }

    static class DialogCreator {
        private final DialogPanel dialogPanel;
        private final GridBagConstraints grid;

        private DialogCreator() {
            dialogPanel = new DialogPanel(new GridBagLayout());
            grid = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                    new JBInsets(2, 4, 2, 1), 0, 0);
        }

        public static DialogCreator builder() {
            return new DialogCreator();
        }

        public void newLine() {
            grid.gridy += 1;
            grid.gridx = 0;
        }

        public void add(Component component) {
            dialogPanel.add(component, grid);
            grid.gridx += 1;
            if (grid.gridx > grid.gridwidth) {
                newLine();
            }
        }

        public DialogPanel build() {
            return dialogPanel;
        }

    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return passwordTextField;
    }
}
