package de.vdvcount.app.filesystem;

import java.io.File;

import de.vdvcount.app.App;

public class FilesystemRepository {

    private static FilesystemRepository repository;

    private String storageLocation;

    public static FilesystemRepository getInstance() {
        if (repository == null) {
            repository = new FilesystemRepository();
        }

        return repository;
    }

    private FilesystemRepository() {
        this.storageLocation = App.getStaticContext().getFilesDir().toString();

        this.verifyFileSystemStructure();
    }

    private void verifyFileSystemStructure() {
        File file = new File(this.getAppDataPath());
        if (!file.isDirectory()) {
            file.mkdirs();
        }
    }

    private String getAppDataPath() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.storageLocation);
        builder.append(File.separator);
        builder.append("appdata");

        return builder.toString();
    }
}
