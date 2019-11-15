package pav.sprykerFileCreator.action.testActions;

import com.intellij.codeInsight.actions.OptimizeImportsAction;
import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.ide.favoritesTreeView.ImportUsagesAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.MultiMap;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.inspections.quickfix.PhpExchangeExtendsImplementsQuickFix;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.PhpElementType;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;

public class TestAction extends AnAction {
    private Project project;
    private MultiMap<String, PhpNamedElement> map;
    private PhpFile newPhpFile;

    public TestAction() {
        super("Test Action");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        this.project = anActionEvent.getProject();
        DataContext context = anActionEvent.getDataContext();
        VirtualFile virtualFile = context.getData(CommonDataKeys.VIRTUAL_FILE);

        String filePath = virtualFile.getPath();
        //@todo improve path checking?
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

                this.newPhpFile = (PhpFile) newPsiFile;
                this.map = this.newPhpFile.getTopLevelDefs();

                for (String key : map.keySet()) {
                    Collection<PhpNamedElement> elementCollection = map.get(key);

                    for (PhpNamedElement element : elementCollection) {
                        if (element instanceof PhpNamespace) {
                            if (element.getFirstChild().getText().equals("namespace")) {
                                PsiElement classElement = this.getFirstElementOfType(PhpClassImpl.class.getName());

                                /**
                                 * Replace Spryker namespace with Pyz
                                 */
                                PsiElement baseNamespaceElement = element.getFirstChild().getNextSibling().getNextSibling();

                                String oldBaseNamespaceElementText = baseNamespaceElement.getText();
                                String newBaseNamespaceElementText = oldBaseNamespaceElementText.substring(0, oldBaseNamespaceElementText.length() - 1).replace("Spryker", "Pyz");

                                PsiElement newNamespaceElement = PhpPsiElementFactory.createNamespaceReference(this.project, newBaseNamespaceElementText, false);

                                WriteCommandAction.runWriteCommandAction(this.project, () -> {
                                    baseNamespaceElement.replace(newNamespaceElement);
                                });

                                //@todo config: clean up parent class content vs override all parent classes and call parent:: methods

                                /**
                                 * Delete class content
                                 */
                                WriteCommandAction.runWriteCommandAction(this.project, () -> {
                                    Collection<Field> classFields = ((PhpClassImpl) classElement).getFields();
                                    for (Field classField: classFields) {
                                        //@todo delete public const keywords as well
                                        //@todo do NOT delete original file content ffs
                                        classField.getPrevSibling().delete();
                                        classField.delete();
                                    }

                                    Collection<Method> classMethods = ((PhpClassImpl) classElement).getMethods();
                                    for (Method classMethod: classMethods) {
                                        PhpDocComment methodComment = classMethod.getDocComment();
                                        if (methodComment != null) {
                                            methodComment.delete();
                                        }
                                        classMethod.delete();
                                    }

                                    PhpDocComment classDocComment = ((PhpClassImpl) classElement).getDocComment();
                                    if (classDocComment != null) {
                                        classDocComment.delete();
                                    }
                                });

                                /**
                                 * Add use statement for overridden Spryker class
                                 */
                                if (!oldBaseNamespaceElementText.contains("Pyz")) {
                                    PsiElement finalNamespaceElement = element.getLastChild().getPrevSibling().getPrevSibling().getPrevSibling();

                                    String finalNamespaceElementText = finalNamespaceElement.getText();
                                    String className = ((PhpClassImpl) classElement).getName();

                                    PhpUseList newUseStatement = PhpPsiElementFactory.createUseStatement(this.project, oldBaseNamespaceElementText + finalNamespaceElementText + "\\" + className, "Spryker" + className);

                                    WriteCommandAction.runWriteCommandAction(this.project, () -> {
                                        element.addBefore(newUseStatement, classElement);
                                    });

                                    OptimizeImportsProcessor optimizeImportsProcessor = new OptimizeImportsProcessor(this.project, this.newPhpFile);
                                    optimizeImportsProcessor.run();
                                }

                                /**
                                 * Add extends statement for overridden Spryker class
                                 */
                                String className = ((PhpClassImpl) classElement).getName();
                                PsiElement extendsListElement = this.getFirstElementOfType(ExtendsListImpl.class.getName());

                                PsiElement extendsElement = PhpPsiElementFactory.createExtendsList(this.project, "Spryker" + className);

                                WriteCommandAction.runWriteCommandAction(this.project, () -> {
                                    extendsListElement.replace(extendsElement);
                                });

                                /**
                                 * @todo import missing interface(s)
                                 */

                                ReformatCodeProcessor reformatCodeProcessor = new ReformatCodeProcessor(this.newPhpFile, false);
                                reformatCodeProcessor.run();
                            }
                        }
                    }
                }

                //@todo focus new file in project tree
                newPhpFile.getVirtualFile().refresh(false, false);
            } catch (IOException exception) {
                //@todo show dialog: failed to read/write file
                return;
            }
        } else {
            Messages.showMessageDialog(anActionEvent.getProject(), "Selected file is not in vendor/spryker ", "Info", Messages.getInformationIcon());
            return;
        }
    }

    private PsiElement getFirstElementOfType(String elementTypeName, PsiElement parentElement) {
        PsiElement element = null;
        PsiElement[] children = parentElement.getChildren();

        for (PsiElement child : children) {
            if (element != null) {
                return element;
            }
            if (child.getClass().getName().equals(elementTypeName)) {
                element = child;
            } else {
                element = this.getFirstElementOfType(elementTypeName, child);
            }
        }

        return element;
    }

    private PsiElement getFirstElementOfType(String elementName) {
        return this.getFirstElementOfType(elementName, this.newPhpFile.getOriginalElement());
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

        VirtualFile newFile;
        try {
            newFile = latestFolder.findOrCreateChildData(this.project, filename);
        } catch (IOException exception) {
            String msg = exception.getMessage();
            return null;
        }
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
