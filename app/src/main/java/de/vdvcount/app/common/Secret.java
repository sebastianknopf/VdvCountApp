package de.vdvcount.app.common;

import android.content.SharedPreferences;

import java.security.InvalidKeyException;

import de.vdvcount.app.security.Cipher;

public class Secret extends KeyValueStore {

   public final static String DEVICE_ID = "de.vdvcount.app.kvs.secret.DEVICE_ID";
   public final static String API_ENDPOINT = "de.vdvcount.app.kvs.secret.API_ENDPOINT";
   public final static String API_USERNAME = "de.vdvcount.app.kvs.secret.API_USERNAME";
   public final static String API_PASSWORD = "de.vdvcount.app.kvs.secret.API_PASSWORD";

   public static String getSecretString(String variableName, String defaultValue) {
      try {
         return Cipher.decryptString(
                 KeyValueStore.getString(variableName, defaultValue),
                 Cipher.DEFAULT_KEY
         );
      } catch (Exception ex) {
         return defaultValue;
      }
   }

   public static void setSecretString(String variableName, String value) {
      try {
         SharedPreferences.Editor editor = Secret.sharedPreferences.edit();
         editor.putString(variableName,
                 Cipher.encryptString(
                         value,
                         Cipher.DEFAULT_KEY
                 )
         );
         editor.commit();
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }

   }
}
