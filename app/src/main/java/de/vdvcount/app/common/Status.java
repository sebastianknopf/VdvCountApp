package de.vdvcount.app.common;

public class Status extends KeyValueStore {

    public final static String STATUS = "de.vdvcount.app.kvs.status.STATUS";

    static {
        Status.init("de.vdvcount.app.kvs.status");
    }

}
