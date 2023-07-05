package org.thermoweb.intellij.plugin.encrypt;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(name = "org.thermoweb.JasyptPluginSettings", storages = @Storage(value = "jasyptplugin.xml"))
public class JasyptPluginSettings implements PersistentStateComponent<JasyptPluginSettings> {

    public String algorithm;
    public boolean isEncapsulated;

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
}
