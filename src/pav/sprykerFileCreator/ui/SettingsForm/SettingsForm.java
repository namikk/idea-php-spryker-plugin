package pav.sprykerFileCreator.ui.SettingsForm;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pav.sprykerFileCreator.config.Settings;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;

public class SettingsForm implements Configurable {
    public static String HELP_URL = "https://github.com/namikk/idea-php-spryker-plugin/";
    private Project project;

    private JPanel panel1;

    private JCheckBox pluginEnabled;
    private JButton buttonHelp;

    public SettingsForm(@NotNull final Project project) {
        this.project = project;
        buttonHelp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                SettingsForm.openUrl(SettingsForm.HELP_URL);
            }
        });
    }

    //@todo move to helper class
    public static void openUrl(String url) {
        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                try {
                    java.net.URI uri = new java.net.URI(url);
                    desktop.browse(uri);
                } catch (URISyntaxException | IOException ignored) {
                }
            }
        }
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Spryker Plugin";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        return panel1;
    }

    @Override
    public boolean isModified() {
        return !pluginEnabled.isSelected() == getSettings().pluginEnabled;
    }

    @Override
    public void apply() throws ConfigurationException {
        getSettings().pluginEnabled = pluginEnabled.isSelected();
    }

    @Override
    public void reset() {
        updateUIFromSettings();
    }

    @Override
    public void disposeUIResources() {
    }

    private Settings getSettings() {
        return Settings.getInstance(project);
    }

    private void updateUIFromSettings() {
        pluginEnabled.setSelected(getSettings().pluginEnabled);
    }

    public static void show(@NotNull Project project) {
        ShowSettingsUtilImpl.showSettingsDialog(project, "Spryker.SettingsForm", null);
    }
}
