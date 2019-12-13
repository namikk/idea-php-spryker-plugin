package pav.sprykerFileCreator.action.overrideActions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import pav.sprykerFileCreator.config.Settings;
import pav.sprykerFileCreator.model.ModelFactory;
import pav.sprykerFileCreator.model.helper.FilesystemHelper;

public class OverrideMethodAction extends AnAction {
    private Project project;
    private ModelFactory modelFactory;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        this.project = anActionEvent.getProject();
        FilesystemHelper filesystemHelper = getModelFactory().createFilesystemHelper(this.project);

        DataContext context = anActionEvent.getDataContext();
        VirtualFile virtualFile = context.getData(CommonDataKeys.VIRTUAL_FILE);

    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        this.project = anActionEvent.getProject();
        DataContext context = anActionEvent.getDataContext();
        VirtualFile virtualFile = context.getData(CommonDataKeys.VIRTUAL_FILE);

        if (virtualFile == null) {
            anActionEvent.getPresentation().setEnabledAndVisible(false);
        } else {
            String filePath = virtualFile.getPath();

            anActionEvent.getPresentation().setEnabledAndVisible(
                    (filePath.contains("vendor") && getSettings().allowAnyNamespace) || filePath.contains("vendor/spryker")
            );
        }

        Editor editor = context.getData(CommonDataKeys.EDITOR);

        //@todo check editor context to see if click was on a method name, then enable action
    }

    private ModelFactory getModelFactory() {
        if (this.modelFactory == null) {
            this.modelFactory = ModelFactory.getInstance();
        }

        return this.modelFactory;
    }

    private Settings getSettings() {
        return Settings.getInstance(project);
    }
}
