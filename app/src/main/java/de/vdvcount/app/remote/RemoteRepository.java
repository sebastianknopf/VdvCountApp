package de.vdvcount.app.remote;

import java.security.InvalidKeyException;

import de.vdvcount.app.common.Secret;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RemoteRepository {

    private static RemoteRepository repository;

    private RemoteAPI remoteApiClient;

    public static RemoteRepository getInstance() {
        if (repository == null) {
            try {
                repository = new RemoteRepository();
            } catch (InvalidKeyException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        return repository;
    }

    private RemoteRepository() throws InvalidKeyException, IllegalAccessException {
        String remoteApiEndpoint = Secret.getSecretString(Secret.API_ENDPOINT, null);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RemoteAuthenticationInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(remoteApiEndpoint)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.remoteApiClient = retrofit.create(RemoteAPI.class);
    }
}
