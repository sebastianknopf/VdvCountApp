package de.vdvcount.app.ui.permission;

import android.util.Log;

import java.security.InvalidKeyException;

import androidx.lifecycle.ViewModel;
import de.vdvcount.app.common.Secret;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.security.Cipher;

public class PermissionViewModel extends ViewModel {

    public void initAppilcation() {
        if (Status.getString(Status.STATUS, Status.Values.INITIAL).equals(Status.Values.INITIAL)) {

            try {
                Cipher.generateSecretKey(Cipher.DEFAULT_KEY);

                Secret.setSecretString(Secret.API_USERNAME, "demo");
                String secret = Secret.getSecretString(Secret.API_USERNAME, "demo");

                Log.d(this.getClass().getSimpleName(), secret);
            } catch (InvalidKeyException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}