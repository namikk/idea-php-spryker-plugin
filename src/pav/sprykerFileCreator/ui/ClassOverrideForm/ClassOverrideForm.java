package pav.sprykerFileCreator.ui.ClassOverrideForm;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ClassOverrideForm implements Configurable {
    private JPanel panel1;
    private JButton directoryToAppReset;
    private JLabel directoryToAppLabel;
    private TextFieldWithBrowseButton directoryToApp;
    private JCheckBox toggleOverrideClassContent;
    private JCheckBox toggleAllowAnyNamespace;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Class Override Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return panel1;
    }

    @Override
    public boolean isModified() {
        //@todo check if any field is modified by comparing to settings
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        //@todo save fields to settings
    }
}
