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
import org.thermoweb.intellij.plugin.encrypt.vault.CipherConfiguration;

import com.intellij.openapi.ui.DialogPanel;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorComboBox;
import com.intellij.ui.PasswordFieldPanel;
import com.intellij.util.ui.JBInsets;

public class CipherInformationsDialog extends DialogWrapper {
	public static final String ALGORITHM_FIELD_NAME = "algorithm";
	public static final String IVGENERATOR_FIELD_NAME = "ivGenerator";
	public static final String PASSWORD_FIELD_NAME = "password";
	public static final String ENCAPSULATE_FIELD_NAME = "encapsulate";
	public static final String REMEMBER_PASSWORD = "rememberPassword";

	private final EditorComboBox algorithmComboBox = new EditorComboBox(ALGORITHM_FIELD_NAME);
	private final EditorComboBox ivGeneratorComboBox = new EditorComboBox(IVGENERATOR_FIELD_NAME);
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
		algorithmComboBox.setName(ALGORITHM_FIELD_NAME);
		ivGeneratorComboBox.setName(IVGENERATOR_FIELD_NAME);
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
		algorithmComboBox.removeAllItems();
		algorithmComboBox.setEnabled(Algorithms.values().length > 1);
		Arrays.stream(Algorithms.values()).forEach(a -> algorithmComboBox.appendItem(a.getCode()));
		Optional.ofNullable(settings)
				.map(JasyptPluginSettings::getAlgorithm)
				.ifPresent(algorithmComboBox::setSelectedItem);
		dialogCreator.add(algorithmComboBox);

		dialogCreator.add(new JLabel(IVGENERATOR_FIELD_NAME));
		ivGeneratorComboBox.removeAllItems();
		ivGeneratorComboBox.setEnabled(IvGenerators.values().length > 1);
		Arrays.stream(IvGenerators.values()).forEach(a -> ivGeneratorComboBox.appendItem(a.getCode()));
		Optional.ofNullable(settings)
				.map(JasyptPluginSettings::getIvGenerator)
				.ifPresent(ivGeneratorComboBox::setSelectedItem);
		dialogCreator.add(ivGeneratorComboBox);

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
		values.put(algorithmComboBox.getName(), algorithmComboBox.getText());
		values.put(ivGeneratorComboBox.getName(), ivGeneratorComboBox.getText());
		values.put(passwordTextField.getName(), passwordTextField.getText());
		values.put(encapsulateCheckBox.getName(), String.valueOf(encapsulateCheckBox.isSelected()));
		values.put(rememberPasswordCheckBox.getName(), String.valueOf(rememberPasswordCheckBox.isSelected()));
		return values;
	}

	public CipherConfiguration getCipherConfiguration() {
		return new CipherConfiguration(
				Algorithms.fromCode(algorithmComboBox.getText()),
				IvGenerators.fromCode(ivGeneratorComboBox.getText()).orElse(IvGenerators.RANDOMIVGENERATOR),
				passwordTextField.getText());
	}

	public void setPassword(String password) {
		passwordTextField.setText(password);
	}

	public void setAlgorithm(String algorithm) {
		algorithmComboBox.setSelectedItem(algorithm);
	}

	public void setIvGenerator(String ivGenerator) {
		ivGeneratorComboBox.setSelectedItem(ivGenerator);
	}

	public boolean isRememberPassword() {
		return rememberPasswordCheckBox.isSelected();
	}

	public boolean isEncapsulated() {
		return encapsulateCheckBox.isSelected();
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
