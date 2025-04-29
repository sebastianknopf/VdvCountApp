package de.vdvcount.app;

import android.app.Application;
import android.content.Context;

import de.vdvcount.app.common.Logging;

public class App extends Application {

    private static Context staticContext;

    @Override
    public void onCreate() {
        super.onCreate();

        App.staticContext = this.getApplicationContext();

        Logging.i(getClass().getName(), "Application startup - Running static context constructor");
        Logging.i(getClass().getName(), String.format("Application version %s", BuildConfig.VERSION_NAME));
        Logging.i(getClass().getName(), String.format("Debug configuration %s", BuildConfig.DEBUG));
    }

    public static Context getStaticContext() {
        return staticContext;
    }

}
