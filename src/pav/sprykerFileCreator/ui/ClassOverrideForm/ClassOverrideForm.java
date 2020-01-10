package pav.sprykerFileCreator.ui.ClassOverrideForm;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pav.sprykerFileCreator.config.Settings;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClassOverrideForm implements Configurable {
    private Project project;
    private JPanel panel1;
    private JButton directoryToAppReset;
    private JLabel directoryToAppLabel;
    private TextFieldWithBrowseButton directoryToApp;
    private JCheckBox toggleExcludeParentClassContent;
    private JCheckBox toggleAllowAnyNamespace;

    public ClassOverrideForm(@NotNull final Project project) {
        this.project = project;
        directoryToAppReset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                directoryToApp.setText(Settings.DEFAULT_PROJECT_ROOT);
                super.mouseClicked(e);
            }
        });
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Class Override Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        directoryToApp.setText(getSettings().projectRoot);
        toggleExcludeParentClassContent.setSelected(getSettings().excludeClassContent);
        toggleAllowAnyNamespace.setSelected(getSettings().allowAnyNamespace);
        return panel1;
    }

    @Override
    public boolean isModified() {
        return !directoryToApp.getText().equals(getSettings().projectRoot)
                || !toggleAllowAnyNamespace.isSelected() == getSettings().allowAnyNamespace
                || !toggleExcludeParentClassContent.isSelected() == getSettings().excludeClassContent;
    }

    @Override
    public void apply() throws ConfigurationException {
        getSettings().projectRoot = directoryToApp.getText();
        getSettings().allowAnyNamespace = toggleAllowAnyNamespace.isSelected();
        getSettings().excludeClassContent = toggleExcludeParentClassContent.isSelected();
    }

    private Settings getSettings() {
        return Settings.getInstance(project);
    }
}
