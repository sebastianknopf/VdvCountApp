package de.vdvcount.app;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static Context staticContext;

    @Override
    public void onCreate() {
        super.onCreate();

        App.staticContext = this.getApplicationContext();
    }

    public static Context getStaticContext() {
        return staticContext;
    }

}
