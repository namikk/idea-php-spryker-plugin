package pav.sprykerFileCreator.action.testActions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.MultiMap;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public class TestAction extends AnAction {
    private Project project;

    public TestAction() {
        super("Test Action");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        this.project = anActionEvent.getProject();
        DataContext context = anActionEvent.getDataContext();
        VirtualFile virtualFile = context.getData(CommonDataKeys.VIRTUAL_FILE);

        String filePath = virtualFile.getPath();
        if (filePath.contains("vendor/spryker")) {
            try {
                PsiFile psiFile = context.getData(CommonDataKeys.PSI_FILE);
                PhpFile phpFile = (PhpFile) psiFile;

                //@todo make it work for other spryker namespaces (SprykerShop etc.)
                //@todo move string to constants
                String oldFilepath = virtualFile.getPath();
                String newFilePath = oldFilepath.substring(oldFilepath.indexOf("/src/")).replaceAll("/src/.*?/", "src/Pyz/").replace("\\", "/");

                byte[] fileContents = virtualFile.contentsToByteArray();
                VirtualFile newFile = this.findOrCreateFile(newFilePath, fileContents);

                OpenFileDescriptor meh = new OpenFileDescriptor(this.project, newFile);
                meh.navigate(true);

                PsiFile newPsiFile = PsiManager.getInstance(this.project).findFile(newFile);
                PhpFile newPhpFile = (PhpFile) newPsiFile;

                MultiMap<String, PhpNamedElement> map = newPhpFile.getTopLevelDefs();

                for (String key : map.keySet()) {
                    Collection<PhpNamedElement> elementCollection = map.get(key);

                    for (PhpNamedElement element : elementCollection) {
                        if (element instanceof PhpNamespace) {
                            if (element.getFirstChild().getText().equals("namespace")) {
                                PsiElement namespaceElement = element.getFirstChild().getNextSibling().getNextSibling();
                                String namespaceElementText = namespaceElement.getText();
                                namespaceElementText = namespaceElementText.substring(0, namespaceElementText.length() - 1).replace("Spryker", "Pyz");

                                PsiElement testasd = PhpPsiElementFactory.createNamespaceReference(this.project, namespaceElementText, false);
                                WriteCommandAction.runWriteCommandAction(this.project, () -> {
                                    namespaceElement.replace(testasd);
                                });
                            }
                        }
                    }

                }
                //@todo modify use statement to use overridden spryker class
                //@todo modify extend statement to extend overridden spryker class
                //@todo modify class content to remove all old code?
/*
//@todo try to use accept maybe?
                newPhpFile.accept(new PsiElementVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        if (element instanceof PhpNamespace) {
                            WriteCommandAction.runWriteCommandAction(this.project, element::delete);
                        }

                        ProgressIndicatorProvider.checkCanceled();
                    }
                });
*/
                newPhpFile.getVirtualFile().refresh(false, false);
            } catch (IOException exception) {
                //@todo show dialog: failed to read/write file
                return;
            }
        } else {
            Messages.showMessageDialog(anActionEvent.getProject(), "Selected file is not in vendor/spryker ", "Info", Messages.getInformationIcon());
            return;
        }

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
//        Project this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
//        String text = Messages.showInputDialog(this.project, "Where is your god now!?", "Puny Mortal.", Messages.getQuestionIcon());
//        Messages.showMessageDialog(this.project, "Hello, " + text, "Information", Messages.getInformationIcon());
    }

    public VirtualFile findOrCreateFile(String fileRelativePath, byte[] contents) throws IOException {
        VirtualFile projectRootFile = this.project.getBaseDir();
        VirtualFile existingFile = projectRootFile.findFileByRelativePath(fileRelativePath);
        if (existingFile != null) {
            return existingFile;
        }

        VirtualFile latestFolder = projectRootFile;

        String[] folderPathParts = fileRelativePath.split("/");

        for (int i = 0; i < folderPathParts.length - 1; i++) {
            String newFolderName = folderPathParts[i];
            VirtualFile newFolderVirtualFile = latestFolder.findChild(newFolderName);

            if (newFolderVirtualFile == null) {
                newFolderVirtualFile = latestFolder.createChildDirectory(this.project, newFolderName);
            } else {
                if (!newFolderVirtualFile.isDirectory()) {
                    throw new IOException("One of the folders in new folder path is an existing file.");
                }
            }

            latestFolder = newFolderVirtualFile;
        }

        String filename = folderPathParts[folderPathParts.length - 1];

        VirtualFile newFile = latestFolder.findOrCreateChildData(this.project, filename);
        newFile.setBinaryContent(contents);
        newFile.refresh(false, false);

        return newFile;
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        DataContext context = anActionEvent.getDataContext();
        VirtualFile virtualFile = context.getData(CommonDataKeys.VIRTUAL_FILE);
        String filePath = virtualFile.getPath();
        anActionEvent.getPresentation().setEnabledAndVisible(filePath.contains("vendor/spryker"));
    }

}
