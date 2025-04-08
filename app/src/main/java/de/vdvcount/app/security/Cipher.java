package de.vdvcount.app.security;

import android.util.Base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Cipher {

    public final static String DEFAULT_KEY = "de.vdvcount.app.security.DEFAULT_KEY";

    public static String encryptString(String originalText, String keyName) throws InvalidKeyException {
        SecretKey secretKey = Cipher.loadSecretKey(keyName);
        return Cipher.encryptString(originalText, secretKey);
    }

    public static String encryptString(String originalText, SecretKey key) {
        return Cipher.encryptString(originalText, key, false);
    }

    public static String encryptString(String originalText, SecretKey key, boolean addHashSum) {
        try {
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);

            byte[] encryptedText = cipher.doFinal(originalText.getBytes(StandardCharsets.UTF_8));
            byte[] iv = cipher.getIV();

            StringBuilder resultStringBuilder = new StringBuilder();
            resultStringBuilder.append(Base64.encodeToString(encryptedText, Base64.DEFAULT));
            resultStringBuilder.append(".");
            resultStringBuilder.append(Base64.encodeToString(iv, Base64.DEFAULT));

            if (addHashSum) {
                MessageDigest shaDigest = MessageDigest.getInstance("SHA256");
                byte[] checkSum = shaDigest.digest(originalText.getBytes(StandardCharsets.UTF_8));

                resultStringBuilder.append(".");
                resultStringBuilder.append(Base64.encodeToString(checkSum, Base64.DEFAULT));
            }

            return resultStringBuilder.toString();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
        }

        return null;
    }

    public static String decryptString(String cipherText, String keyName) throws InvalidKeyException, IllegalAccessException {
        SecretKey secretKey = Cipher.loadSecretKey(keyName);
        return Cipher.decryptString(cipherText, secretKey);
    }

    public static String decryptString(String cipherText, SecretKey key) throws IllegalAccessException {
        if (cipherText == null) {
            throw new IllegalArgumentException("cipher text must not be null");
        }

        String[] cipherTextParts = cipherText.split("\\.");
        if (cipherTextParts.length < 2) {
            throw new IllegalArgumentException("incompatible cipher text value");
        }

        try {
            // extract cipher message and IV
            byte[] encryptedText = Base64.decode(cipherTextParts[0], Base64.DEFAULT);
            byte[] iv = Base64.decode(cipherTextParts[1], Base64.DEFAULT);

            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            byte[] decryptedText = cipher.doFinal(encryptedText);

            if (cipherTextParts.length == 3) {
                String hash = cipherTextParts[2];

                MessageDigest shaDigest = MessageDigest.getInstance("SHA256");
                String compareHash = Base64.encodeToString(shaDigest.digest(decryptedText), Base64.DEFAULT);

                if (!hash.equals(compareHash)) {
                    throw new IllegalAccessException("invalid key to decrypt message");
                }
            }

            return new String(decryptedText);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalAccessException("invalid key to decrypt message");
        }
    }

    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2); // 1 byte = 2 hex chars
        for (byte hexByte : data) {
            sb.append(String.format("%02X", hexByte));
        }

        return sb.toString().toUpperCase();
    }

    public static byte[] fromHexString(String hex) {
        hex = hex.toUpperCase();

        byte[] array = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2 )
        {
            String hexByte = hex.substring(i, i + 2);
            array[i / 2] = (byte) Integer.parseInt(hexByte, 16);
        }

        return array;
    }

    public static boolean seemsToBeEncryptedString(String testString) {
        String[] testStringComponents = testString.split("\\.");

        if (testStringComponents.length < 2) {
            return false;
        }

        String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern r = Pattern.compile(pattern);

        for (String testStringComponent : testStringComponents) {
            Matcher m = r.matcher(testStringComponent);

            if (!m.find()) {
                return false;
            }
        }

        return true;
    }

    private static SecretKey loadSecretKey(String keyName) throws InvalidKeyException {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            return (SecretKey) keyStore.getKey(keyName, null);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new InvalidKeyException(String.format("failed to load key %s", keyName));
        }
    }
}