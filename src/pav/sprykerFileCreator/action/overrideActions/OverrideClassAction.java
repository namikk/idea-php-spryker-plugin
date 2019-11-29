package pav.sprykerFileCreator.action.overrideActions;

import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
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
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.ExtendsListImpl;
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl;
import org.jetbrains.annotations.NotNull;
import pav.sprykerFileCreator.config.Settings;
import pav.sprykerFileCreator.model.ModelFactory;
import pav.sprykerFileCreator.model.helper.FilesystemHelper;

import java.io.IOException;
import java.util.Collection;

public class OverrideClassAction extends AnAction {
    private Project project;
    private PhpFile newPhpFile;
    private ModelFactory modelFactory;

    public OverrideClassAction() {
        super("Override Spryker Class Action");
    }

    private void navigateToFile(VirtualFile file) {
        OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(this.project, file);
        fileDescriptor.navigate(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        this.project = anActionEvent.getProject();
        FilesystemHelper filesystemHelper = getModelFactory().createFilesystemHelper(this.project);

        DataContext context = anActionEvent.getDataContext();
        VirtualFile virtualFile = context.getData(CommonDataKeys.VIRTUAL_FILE);


        String filePath = virtualFile.getPath();
        //@todo improve path checking?
        if (filePath.contains("vendor/spryker")) {
            try {
                PsiFile psiFile = context.getData(CommonDataKeys.PSI_FILE);

                //@todo move string to constants
                String oldFilepath = virtualFile.getPath();
                String newFilePath = oldFilepath.substring(oldFilepath.indexOf("/src/")).replaceAll("/src/.*?/", "src/Pyz/").replace("\\", "/");

                byte[] fileContents = virtualFile.contentsToByteArray();

                VirtualFile newFile = filesystemHelper.findFile(newFilePath);
                if (newFile == null) {
                    newFile = filesystemHelper.findOrCreateFile(newFilePath, fileContents);
                    this.navigateToFile(newFile);
                } else {
                    this.navigateToFile(newFile);
                    return;
                }

                PsiFile newPsiFile = PsiManager.getInstance(this.project).findFile(newFile);

                this.newPhpFile = (PhpFile) newPsiFile;
                MultiMap<String, PhpNamedElement> map = this.newPhpFile.getTopLevelDefs();

                for (String key : map.keySet()) {
                    Collection<PhpNamedElement> elementCollection = map.get(key);

                    for (PhpNamedElement element : elementCollection) {
                        //@todo is there a better way to do this?
                        if (element instanceof PhpNamespace) {
                            if (element.getFirstChild().getText().equals("namespace")) {
                                PsiElement classElement = this.getFirstElementOfType(PhpClassImpl.class.getName());

                                /**
                                 * Replace Spryker namespace with Pyz
                                 */
                                PsiElement baseNamespaceElement = element.getFirstChild().getNextSibling().getNextSibling();

                                String newNamespaceElementText = this.getNewNamespace(baseNamespaceElement);
                                PsiElement newNamespaceElement = PhpPsiElementFactory.createNamespaceReference(this.project, newNamespaceElementText, false);

                                WriteCommandAction.runWriteCommandAction(this.project, () -> {
                                    baseNamespaceElement.replace(newNamespaceElement);
                                });

                                if (getSettings().overrideClassContent) {
                                    /*
                                    Collection<Method> classMethods = ((PhpClassImpl) classElement).getMethods();
                                    for (Method classMethod : classMethods) {
                                        //@todo override all parent classes and call parent:: methods instead of parent method content
                                        String methodText = classMethod.getText();
                                        String methodName = classMethod.getName();
                                        Parameter[] methodParameters =  classMethod.getParameters();
                                        PsiElement methodBody = classMethod.getLastChild();


                                        //@todo moe logic to separate function
//                                        MethodImpl

//                                        methodBody.
                                        PhpReturn returnElement = PhpPsiElementFactory.createReturnStatement(this.project, "true");
                                        PhpPsiElementFactory.createFromText(this.project, PsiElement, "{");


                                        PhpDocComment methodComment = classMethod.getDocComment();
                                    }
                                     */
                                } else {
                                    /**
                                     * Delete class content
                                     */
                                    WriteCommandAction.runWriteCommandAction(this.project, () -> {
                                        Collection<Field> classFields = ((PhpClassImpl) classElement).getFields();
                                        for (Field classField : classFields) {
                                            //@todo delete public const keywords as well
                                            //@todo do NOT delete original file content ffs
                                            classField.getPrevSibling().delete();
                                            classField.delete();
                                        }

                                        Collection<Method> classMethods = ((PhpClassImpl) classElement).getMethods();
                                        for (Method classMethod : classMethods) {
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
                                }

                                /**
                                 * Add use statement for overridden Spryker class
                                 */
                                String oldBaseNamespaceElementText = baseNamespaceElement.getText();
                                if (!oldBaseNamespaceElementText.contains("Pyz")) {
                                    PsiElement finalNamespaceElement = element.getLastChild().getPrevSibling().getPrevSibling().getPrevSibling();

                                    String finalNamespaceElementText = finalNamespaceElement.getText();
                                    String className = ((PhpClassImpl) classElement).getName();
                                    String fqn = oldBaseNamespaceElementText + finalNamespaceElementText + "\\" + className;
                                    //@todo won't work with arbitrary namespaces
                                    String parentClassAlias = "Spryker" + className;

                                    PhpUseList newUseStatement = PhpPsiElementFactory.createUseStatement(this.project, fqn, parentClassAlias);
                                    WriteCommandAction.runWriteCommandAction(this.project, () -> {
                                        element.addBefore(newUseStatement, classElement);
                                    });

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
                                 * @todo import missing (facade) interface(s)
                                 */

                                this.reformatCode(this.newPhpFile);
                                this.organizeImports(this.newPhpFile);
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

    private String getNewNamespace(PsiElement baseNamespaceElement) {
        String oldBaseNamespaceElementText = baseNamespaceElement.getText();
        String newBaseNamespaceElementText;
        if (getSettings().allowAnyNamespace) {
            //@todo alternative: replace first part to make it work with any namespace (it should be namespace anyways)
            newBaseNamespaceElementText = oldBaseNamespaceElementText;
        } else {
            newBaseNamespaceElementText = oldBaseNamespaceElementText
                    .substring(0, oldBaseNamespaceElementText.length() - 1)
                    .replace("SprykerMiddleware", "Pyz")
                    .replace("SprykerShop", "Pyz")
                    .replace("SprykerEco", "Pyz")
                    .replace("Spryker", "Pyz");
        }

        return newBaseNamespaceElementText;
    }

    private void reformatCode(PsiFile psiFile) {
        ReformatCodeProcessor reformatCodeProcessor = new ReformatCodeProcessor(psiFile, false);
        reformatCodeProcessor.run();
    }

    private void organizeImports(PsiFile psiFile) {
        OptimizeImportsProcessor optimizeImportsProcessor = new OptimizeImportsProcessor(this.project, psiFile);
        optimizeImportsProcessor.run();
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
                    getSettings().allowAnyNamespace || filePath.contains("vendor/spryker")
            );
        }
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
