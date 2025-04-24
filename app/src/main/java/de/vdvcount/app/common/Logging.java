package de.vdvcount.app.common;

import android.util.Log;

import com.google.firebase.components.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.vdvcount.app.App;

public class Logging {

   private static String storageLocation;

   static {
      Logging.storageLocation = App.getStaticContext().getFilesDir().toString();

      Logging.verifyFileSystemStructure();
   }

   private static String getLogTimestamp() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      return sdf.format(new Date());
   }

   private static void addLogEntry(String level, String tag, String message) {
      String logFilename = Logging.getLogPath("de.vdvcount.app.log");

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      String timestamp = sdf.format(new Date());

      try {
         FileWriter fileWriter = new FileWriter(logFilename, true);
         fileWriter.write(String.format("%s\t%s\t%s %s", timestamp, tag, level, message));
         fileWriter.write(System.lineSeparator());
         fileWriter.close();
      } catch (IOException ex) {
      }
   }

   private static void verifyFileSystemStructure() {
      File file = new File(Logging.getLogPath());
      if (!file.isDirectory()) {
         file.mkdirs();
      }
   }

   private static String getLogPath(String filename) {
      StringBuilder builder = new StringBuilder();
      builder.append(Logging.storageLocation);
      builder.append(File.separator);
      builder.append("logs");
      builder.append(File.separator);
      builder.append(filename);

      return builder.toString();
   }

   private static String getLogPath() {
      StringBuilder builder = new StringBuilder();
      builder.append(Logging.storageLocation);
      builder.append(File.separator);
      builder.append("logs");

      return builder.toString();
   }

   public static String getCurrentLogs() {
      String logFilename = Logging.getLogPath("de.vdvcount.app.log");

      StringBuilder sb = new StringBuilder();

      try {
         BufferedReader reader = new BufferedReader(new FileReader(logFilename));

         String line;
         while((line = reader.readLine()) != null) {
            sb.append(line).append(System.lineSeparator());
         }
      } catch (IOException ex) {
      }

      return sb.toString();
   }

   public static void clearCurrentLogs() {
      String logFilename = Logging.getLogPath("de.vdvcount.app.log");

      try {
         FileWriter fileWriter = new FileWriter(logFilename, false);
         fileWriter.close();
      } catch (IOException ex) {
      }
   }

   public static void d(String tag, String message) {
      Logging.addLogEntry("DEBUG", tag, message);

      if (BuildConfig.DEBUG) {
         Log.d(tag, message);
      }
   }

   public static void i(String tag, String message) {
      Logging.addLogEntry("INFO", tag, message);

      if (BuildConfig.DEBUG) {
         Log.i(tag, message);
      }
   }

   public static void w(String tag, String message) {
      Logging.addLogEntry("WARNING", tag, message);

      if (BuildConfig.DEBUG) {
         Log.w(tag, message);
      }
   }

   public static void e(String tag, String message) {
      Logging.addLogEntry("ERROR", tag, message);

      if (BuildConfig.DEBUG) {
         Log.e(tag, message);
      }
   }

   public static void e(String tag, String message, Throwable t) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);

      Logging.addLogEntry("ERROR", tag, message);
      Logging.addLogEntry("STACK", tag, sw.toString());

      if (BuildConfig.DEBUG) {
         Log.e(tag, message, t);
      }
   }

}
