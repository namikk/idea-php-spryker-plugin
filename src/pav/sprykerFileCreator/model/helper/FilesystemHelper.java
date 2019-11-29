package pav.sprykerFileCreator.model.helper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import pav.sprykerFileCreator.config.Settings;

import java.io.IOException;
import java.util.Arrays;

public class FilesystemHelper {
    private Project project;

    public FilesystemHelper(Project project) {
        this.project = project;
    }

    public VirtualFile findFile(String fileRelativePath) {
        VirtualFile projectRootFile = this.project.getBaseDir();
        return projectRootFile.findFileByRelativePath(fileRelativePath);
    }

    public VirtualFile createFile(String fileRelativePath, byte[] contents) throws IOException {
        fileRelativePath = getSettings().projectRoot + "/" + fileRelativePath;
        fileRelativePath = FileUtil.toSystemIndependentName(fileRelativePath);

        String[] filePathParts = fileRelativePath.split("/");

        String[] folderPathParts = Arrays.copyOf(filePathParts, filePathParts.length - 1);

        VirtualFile parentFolder = this.findOrCreateFolderFromRelativePath(folderPathParts);

        final VirtualFile newFile;
        try {
            String filename = filePathParts[filePathParts.length - 1];
            //@todo fix write-action only exception
            newFile = parentFolder.findOrCreateChildData(this.project, filename);
        } catch (IOException exception) {
            String msg = exception.getMessage();
            return null;
        }
        newFile.setBinaryContent(contents);
        newFile.refresh(false, false);

        return newFile;
    }

    public VirtualFile findOrCreateFolderFromRelativePath(String[] folderPathParts) throws IOException {
        VirtualFile parentFolder = this.project.getBaseDir();

        for (int i = 0; i < folderPathParts.length; i++) {
            String newFolderName = folderPathParts[i];
            if (newFolderName.equals("")) {
                continue;
            }
            VirtualFile newFolderVirtualFile = parentFolder.findChild(newFolderName);

            if (newFolderVirtualFile == null) {
                //@todo fix write-action only exception
                newFolderVirtualFile = parentFolder.createChildDirectory(this.project, newFolderName);
            } else {
                if (!newFolderVirtualFile.isDirectory()) {
                    throw new IOException("One of the folders in new folder path is an existing file.");
                }
            }

            parentFolder = newFolderVirtualFile;
        }

        return parentFolder;
    }

    public VirtualFile findOrCreateFile(String fileRelativePath, byte[] contents) throws IOException {
        VirtualFile existingFile = this.findFile(fileRelativePath);
        if (existingFile == null) {
            return this.createFile(fileRelativePath, contents);
        }
        return existingFile;
    }

    private Settings getSettings() {
        return Settings.getInstance(project);
    }
}
