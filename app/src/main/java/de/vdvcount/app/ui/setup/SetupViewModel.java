package de.vdvcount.app.ui.setup;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import androidx.lifecycle.ViewModel;
import de.vdvcount.app.common.Secret;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.security.Cipher;

public class SetupViewModel extends ViewModel {

    public void setupApplication(String setupString) {
        Cipher.generateSecretKey(Cipher.DEFAULT_KEY);

        try {
            URI uri = new URI(setupString);
            URI sanitizedUri = new URI(
                    uri.getScheme(),
                    null,
                    uri.getHost(),
                    uri.getPort(),
                    uri.getPath(),
                    uri.getQuery(),
                    uri.getFragment()
            );

            String userInfo = uri.getUserInfo();
            if (userInfo == null) {
                throw new RuntimeException("no user information specified in URI"); // output of URI with credentials not intended for security reasons
            }

            String[] userCredentials = userInfo.split(":");
            if (userCredentials.length < 2 || userCredentials[0].length() == 0 || userCredentials[1].length() == 0) {
                throw new RuntimeException("invalid user information specified in URI"); // output of URI with credentials not intended for security reasons
            }

            String apiEndpoint = sanitizedUri.toString();

            try {
                Secret.setSecretString(Secret.API_ENDPOINT, apiEndpoint);
                Secret.setSecretString(Secret.API_USERNAME, userCredentials[0]);
                Secret.setSecretString(Secret.API_PASSWORD, userCredentials[1]);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }

            Status.setString(Status.STATUS, Status.Values.READY);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}