package pav.sprykerFileCreator.action.overrideActions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import com.jetbrains.php.lang.psi.stubs.PhpMethodStubImpl;
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

        Editor editor = context.getData(CommonDataKeys.EDITOR);
        PhpFile phpFile = (PhpFile) context.getData(CommonDataKeys.PSI_FILE);
        PsiElement element = phpFile.findElementAt(editor.getCaretModel().getOffset());

        String elementText = element.getText();
        String parentText = element.getParent().getText();
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        //@todo duplicate as in OverrideClassAction
        this.project = anActionEvent.getProject();
        DataContext context = anActionEvent.getDataContext();
        VirtualFile virtualFile = context.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean isSprykerVendorFile = false;

        if (virtualFile == null) {
            anActionEvent.getPresentation().setEnabledAndVisible(false);
            return;
        } else {
            String filePath = virtualFile.getPath();

            isSprykerVendorFile = (filePath.contains("vendor") && getSettings().allowAnyNamespace) || filePath.contains("vendor/spryker");
        }

        Editor editor = context.getData(CommonDataKeys.EDITOR);
        PhpFile phpFile = (PhpFile) context.getData(CommonDataKeys.PSI_FILE);
        PsiElement element = phpFile.findElementAt(editor.getCaretModel().getOffset());

        anActionEvent.getPresentation().setEnabledAndVisible(
                isSprykerVendorFile && this.elementBelongsToClassMethod(element)
        );
    }

    private boolean elementBelongsToClassMethod(PsiElement element) {
        if (element == null) {
            return false;
        }

        boolean belongsToClassMethod = false;

        //@todo class variables are false positives
        if (element instanceof Method) {
            belongsToClassMethod = true;
        } else if (element instanceof PhpMethodStubImpl) {
            belongsToClassMethod = true;
        } else if (element.getParent() instanceof PhpMethodStubImpl) {
            belongsToClassMethod = true;
        } else if (element.getParent() instanceof PhpClassMember) {
            belongsToClassMethod = true;
        }

        return belongsToClassMethod;
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
