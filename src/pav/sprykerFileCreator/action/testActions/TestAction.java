package pav.sprykerFileCreator.action.testActions;

import com.intellij.ide.util.PlatformPackageUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.util.ResourceUtil;
import org.jetbrains.annotations.NotNull;
import pav.sprykerFileCreator.model.ModelFactory;
import pav.sprykerFileCreator.model.manager.ClassManagerInterface;

import java.io.IOException;
import java.net.URL;

public class TestAction extends AnAction {

    private final ModelFactory modelFactory;

    public TestAction()
    {
        super("Test Action", "Test Action Description", IconLoader.getIcon("testAction.png"));
        this.modelFactory = new ModelFactory();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent)
    {
        Project project = anActionEvent.getProject();
        DataContext context = anActionEvent.getDataContext();
        String projectName = anActionEvent.getProject().getName();
        String projectBasePath = anActionEvent.getProject().getBasePath();

        VirtualFile file = context.getData(CommonDataKeys.VIRTUAL_FILE);
        String filePath = file.getPath();
        VirtualFile folder = file.getParent();
        Editor editor = context.getData(CommonDataKeys.EDITOR);


        PsiFileFactory factory = PsiFileFactory.getInstance(anActionEvent.getProject());


        if (filePath.contains("vendor/spryker")) {
            try {
                byte[] fileContents = file.contentsToByteArray();

                //@todo create new file
                ClassManagerInterface classManager = this.modelFactory.createClassManager(anActionEvent.getProject(), projectName);

                PsiFile psiFile = context.getData(CommonDataKeys.PSI_FILE);
                PsiDirectory psiDirectory = psiFile.getContainingDirectory();

                String newFilePath = new String("test.php");
                PsiFile newFile = factory.createFileFromText(newFilePath, psiFile.getLanguage(), new String(fileContents));

                VirtualFile src = LocalFileSystem.getInstance().findFileByPath(projectBasePath + "/src");

                VirtualFile targetDir = project.getBaseDir().createChildDirectory(this, "src");
                VirtualFile[] children = targetDir.getChildren();

                VirtualFile newFile2 = targetDir.findOrCreateChildData(this, "test2.php");

                newFile2.setBinaryContent(fileContents);

                PsiDirectory newDir = PsiDirectoryFactory
                        .getInstance(project)
                        .createDirectory(targetDir);

                /*   WriteCommandAction.runWriteCommandAction(e.getProject(), new Runnable() {
        @Override
        public void run() {
            try {
                targetDir.createChildData(this, System.currentTimeMillis()+".txt");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    });*/
                new WriteCommandAction(project) {

                    @Override
                    protected void run(@NotNull Result result) throws Throwable {
                        newDir.add(newFile);

                        PsiFile virtualFile = newDir.findFile(newFilePath);
                        if (virtualFile != null) {
                            new OpenFileDescriptor(project, virtualFile.getVirtualFile()).navigate(true);
                        }
                    }

                    @Override
                    public String getGroupID() {
                        return "Create Command";
                    }

                }.execute();

                try {
                    classManager.writeFile(psiDirectory, projectName, new String(fileContents));
                } catch (Exception exception) {
                    //@todo show dialog: unable to save file
                    return;
                }

            } catch (IOException exception) {
                //@todo show dialog: failed to read file
                return;
            }
        } else {
            Messages.showMessageDialog(anActionEvent.getProject(), "Selected file is not in vendor/spryker ", "Info", Messages.getInformationIcon());
            return;
        }


        //@todo get current file
        //@todo no need to check if it's in vendor folder already since update() should take care of that???
        //@todo create identical file in src/Pyz namespace (hardcoded for now)
        //@todo find a way to alter file content
        //@todo find a way to parse php file
        //@todo find a way to alter php code

//        StringBuffer dlgMsg = new StringBuffer(anActionEvent.getPresentation().getText() + " Selected");
//        String dlgTitle = anActionEvent.getPresentation().getDescription();
//
//        Navigatable nav = anActionEvent.getData(CommonDataKeys.NAVIGATABLE);
//
//        if (nav != null) {
//            dlgMsg.append(String.format("/nSelected Element: %s", nav.toString()));
//        }
//        Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon());
//
//        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
//        String text = Messages.showInputDialog(project, "Where is your god now!?", "Puny Mortal.", Messages.getQuestionIcon());
//        Messages.showMessageDialog(project, "Hello, " + text, "Information", Messages.getInformationIcon());
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent)
    {
        //@todo should change to checking if current file is in vendor/spryker folder and then enable/disable plugin action accordingly?
        Project project = anActionEvent.getProject();
        anActionEvent.getPresentation().setEnabledAndVisible(project != null);

    }

}
