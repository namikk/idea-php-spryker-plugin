package pav.sprykerFileCreator.action.testActions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.php.lang.psi.PhpFile;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestAction extends AnAction {

    public TestAction() {
        super("Test Action", "Test Action Description", IconLoader.getIcon("testAction.png"));
    }

    private PsiFile doStuff(Project project, String filename, String fullPath) {
        PsiFile[] files = FilenameIndex.getFilesByName(project, filename, GlobalSearchScope.projectScope(project));

        for (PsiFile file: files) {
            if (file.getName().equals(filename)) {
                return file;
            }
        }

        return null;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
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
                PsiFile psiFile = context.getData(CommonDataKeys.PSI_FILE);

                PhpFile phpFile = (PhpFile) psiFile;
                phpFile.getVirtualFile().refresh(false, false);
                phpFile.getVirtualFile().refresh(false, true);

                String filename = file.getName();
                //@todo handle null; should I use file path instead of namespace?
                String sprykerNamespace = phpFile.getMainNamespaceName();
                //@todo make it work for other spryker namespaces (SprykerShop etc.)
                //@todo move string to constants
                String projectNamespace = sprykerNamespace.replace("Spryker", "Pyz").replace("\\", "/");

                String newPath = projectBasePath + "/src" + projectNamespace + "/" + filename;
                byte[] fileContents = file.contentsToByteArray();
                File newFileJavaio = new File(newPath);
                newFileJavaio.getParentFile().mkdirs();
                FileWriter writer = new FileWriter(newFileJavaio.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(writer);
                bw.write(new String(fileContents));
                bw.close();

                //@todo navigate to newly created file

                PsiFile newPsiFile = this.doStuff(project, filename, newPath);

                newPsiFile.getVirtualFile().refresh(false, false);
                newPsiFile.getVirtualFile().refresh(false, true);

//                VirtualFile newVirtualFile = LocalFileSystem.getInstance().findFileByPath(newPath);
//                newVirtualFile.refresh(false, false);

//                VirtualFile refreshedFile = VirtualFileManager.getInstance().refreshAndFindFileByUrl(newPath);
                OpenFileDescriptor meh = new OpenFileDescriptor(project, newPsiFile.getVirtualFile());
                meh.navigate(true);

//                MultiMap<String, PhpNamedElement> map = phpFile.getTopLevelDefs();
//                for (Map.Entry entry: map.entrySet()) {
//                    Object key = entry.getKey();
//                    Object value = entry.getValue();
//                }

//                PsiDirectory psiDirectory = psiFile.getContainingDirectory();
//                String folderPath = folder.getPath();
            } catch (IOException exception) {
                //@todo show dialog: failed to read/write file
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
    public void update(@NotNull AnActionEvent anActionEvent) {
        //@todo should change to checking if current file is in vendor/spryker folder and then enable/disable plugin action accordingly?
        Project project = anActionEvent.getProject();
        anActionEvent.getPresentation().setEnabledAndVisible(project != null);

    }

}
