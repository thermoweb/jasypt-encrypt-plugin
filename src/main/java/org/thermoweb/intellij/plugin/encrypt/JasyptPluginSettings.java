package org.thermoweb.intellij.plugin.encrypt;

import java.util.Map;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(name = "org.thermoweb.JasyptPluginSettings", storages = @Storage(value = "jasyptplugin.xml"))
public class JasyptPluginSettings implements PersistentStateComponent<JasyptPluginSettings> {

    private String algorithm;
    private String ivGenerator;
    private boolean isEncapsulated;
    private boolean rememberPassword;

    @Override
    public JasyptPluginSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull JasyptPluginSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static JasyptPluginSettings getInstance(Project project) {
        return project.getService(JasyptPluginSettings.class);
    }

    public static void updateSettings(Project project, Map<String, String> values) {
        JasyptPluginSettings settings = getInstance(project);
        for (ConfigurationField field : ConfigurationField.values()) {
            field.setter.accept(settings, values.get(field.fieldName));
        }
    }

    public enum ConfigurationField {
        ALGORITHM(CipherInformationsDialog.ALGORITHM_FIELD_NAME, JasyptPluginSettings::setAlgorithm),
        IV_GENERATOR(CipherInformationsDialog.IVGENERATOR_FIELD_NAME, JasyptPluginSettings::setIvGenerator),
        ENCAPSULATE(CipherInformationsDialog.ENCAPSULATE_FIELD_NAME, JasyptPluginSettings::setIsEncapsulated),
        REMEMBER_PASSWORD(CipherInformationsDialog.REMEMBER_PASSWORD, JasyptPluginSettings::setRememberPassword);

        private final String fieldName;
        private final BiConsumer<JasyptPluginSettings, String> setter;

        ConfigurationField(String fieldName, BiConsumer<JasyptPluginSettings, String> setter) {
            this.fieldName = fieldName;
            this.setter = setter;
        }
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getIvGenerator() {
        return ivGenerator;
    }

    public void setIvGenerator(String ivGenerator) {
        this.ivGenerator = ivGenerator;
    }

    public void setIsEncapsulated(boolean isEncapsulated) {
        this.isEncapsulated = isEncapsulated;
    }

    public void setIsEncapsulated(String isEncapsulated) {
        this.isEncapsulated = Boolean.parseBoolean(isEncapsulated);
    }

    public boolean isEncapsulated() {
        return isEncapsulated;
    }

    public void setRememberPassword(boolean rememberPassword) {
        this.rememberPassword = rememberPassword;
    }

    public void setRememberPassword(String rememberPassword) {
        this.rememberPassword = Boolean.parseBoolean(rememberPassword);
    }

    public boolean isRememberPassword() {
        return rememberPassword;
    }
}
