package de.vdvcount.app.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import de.vdvcount.app.App;
import de.vdvcount.app.common.Logging;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.Trip;
import de.vdvcount.app.remote.CountedTripObject;
import kotlin.text.Charsets;

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

    public CountedTrip startCountedTrip(Trip trip, String vehicleId) {
        CountedTrip countedTrip = CountedTrip.from(trip);
        countedTrip.setVehicleId(vehicleId);

        this.updateCountedTrip(countedTrip);

        Logging.i(this.getClass().getName(), "Generated new CountedTrip object");

        return countedTrip;
    }

    public void updateCountedTrip(CountedTrip countedTrip) {
        String countedTripFilename = this.getCountedTripFilename();

        Gson gson = new GsonBuilder().serializeNulls().create();
        String countedTripData = gson.toJson(countedTrip.mapApiObject());

        FileOutputStream fos = null;

        try {
            File outputFile = new File(countedTripFilename);
            outputFile.createNewFile();

            fos = new FileOutputStream(countedTripFilename);

            PrintStream printStream = new PrintStream(fos);
            printStream.print(countedTripData);

            Logging.i(this.getClass().getName(), "Updated CountedTrip object in local storage");
        } catch (IOException e) {
            Logging.e(this.getClass().getName(), "Failed to update CountedTrip object in local storage", e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public CountedTrip loadCountedTrip() {
        String countedTripFilename = this.getCountedTripFilename();
        File file = new File(countedTripFilename);

        if (!file.exists()) {
            Logging.w(this.getClass().getName(), "CountedTrip object not existent in local storage");
            return null;
        }

        byte[] buffer = new byte[(int) file.length()];
        InputStream ios = null;

        try {
            ios = new FileInputStream(file);
            ios.read(buffer);
        } catch(IOException e) {
            Logging.e(this.getClass().getName(), "Failed to load CountedTrip object from local storage", e);
            return null;
        } finally {
            try {
                if (ios != null) {
                    ios.close();
                }
            } catch (IOException e) {
            }
        }

        String countedTripObjectJson = new String(buffer, Charsets.UTF_8);
        Gson gson = new GsonBuilder().serializeNulls().create();

        CountedTripObject countedTripObject = gson.fromJson(countedTripObjectJson, CountedTripObject.class);

        Logging.i(this.getClass().getName(), "Loaded CountedTrip object from local storage");

        return countedTripObject.mapDomainModel();
    }

    public void closeCountedTrip() {
        String countedTripFilename = this.getCountedTripFilename();
        File file = new File(countedTripFilename);
        file.delete();

        Logging.i(this.getClass().getName(), "Removed CountedTrip object from local storage");
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

    private String getCountedTripFilename() {
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(this.getAppDataPath());
        fileNameBuilder.append(File.separator);
        fileNameBuilder.append("de.vdvcount.app.countedtrip.json");

        return fileNameBuilder.toString();
    }
}
