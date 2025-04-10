package de.vdvcount.app.remote;

import java.io.IOException;
import java.security.InvalidKeyException;

import de.vdvcount.app.common.Secret;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class RemoteAuthenticationInterceptor implements Interceptor {

    private String credentials;

    public RemoteAuthenticationInterceptor() {
        try {
            this.credentials = Credentials.basic(
                    Secret.getSecretString(Secret.API_USERNAME, ""),
                    Secret.getSecretString(Secret.API_PASSWORD, "")
            );
        } catch (InvalidKeyException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request
                .newBuilder()
                .header("Authorization", this.credentials)
                .build();

        return chain.proceed(authenticatedRequest);
    }
}
