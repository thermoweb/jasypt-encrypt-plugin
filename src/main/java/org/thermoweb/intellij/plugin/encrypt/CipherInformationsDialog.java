package org.thermoweb.intellij.plugin.encrypt;

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

    private final EditorComboBox comboBox = new EditorComboBox(ALGORITHM_FIELD_NAME);
    private final PasswordFieldPanel passwordTextField = new PasswordFieldPanel();
    private final JCheckBox checkbox = new JCheckBox(ENCAPSULATE_FIELD_NAME, true);
    private final JasyptPluginSettings settings;
    private final boolean textIsEncapsulated;

    public CipherInformationsDialog(JasyptPluginSettings settings) {
        this(settings, false);
    }

    public CipherInformationsDialog(JasyptPluginSettings settings, boolean textIsEncapsulated) {
        super(true);
        this.settings = settings;
        this.textIsEncapsulated = textIsEncapsulated;
        setTitle("Password");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        DialogPanel dialogPanel = new DialogPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new JBInsets(2, 4, 2, 1), 0, 0);
        JLabel passwordLabel = new JLabel(PASSWORD_FIELD_NAME);
        dialogPanel.add(passwordLabel, c);
        c.gridx = 1;
        passwordTextField.setName(PASSWORD_FIELD_NAME);
        dialogPanel.add(passwordTextField, c);

        c.gridy = 1;
        c.gridx = 0;
        JLabel algoLabel = new JLabel(ALGORITHM_FIELD_NAME);
        dialogPanel.add(algoLabel, c);
        c.gridx = 1;
        comboBox.setName(ALGORITHM_FIELD_NAME);
        comboBox.removeAllItems();
        comboBox.setEnabled(Algorithms.values().length > 1);
        Arrays.stream(Algorithms.values()).forEach(a -> comboBox.appendItem(a.getCode()));
        Optional.ofNullable(settings)
                .map(s -> s.algorithm)
                .ifPresent(comboBox::setSelectedItem);
        dialogPanel.add(comboBox, c);

        c.gridx = 0;
        c.gridy = 2;
        checkbox.setText("surrounded by ENC(...)");
        checkbox.setName(ENCAPSULATE_FIELD_NAME);
        Boolean encapsulatedSetting = Optional.ofNullable(settings)
                .map(s -> s.isEncapsulated)
                .orElse(true);
        checkbox.setSelected(encapsulatedSetting || textIsEncapsulated);
        dialogPanel.add(checkbox, c);
        return dialogPanel;
    }

    public Map<String, String> getValues() {
        Map<String, String> values = new HashMap<>();
        values.put(comboBox.getName(), comboBox.getText());
        values.put(passwordTextField.getName(), passwordTextField.getText());
        values.put(checkbox.getName(), String.valueOf(checkbox.isSelected()));
        return values;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return passwordTextField;
    }
}
