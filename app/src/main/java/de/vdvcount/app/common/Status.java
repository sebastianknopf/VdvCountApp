package de.vdvcount.app.common;

public class Status extends KeyValueStore {

    public final static String STATUS = "de.vdvcount.app.kvs.status.STATUS";

    public class Values {
        public final static String INITIAL = "INITIAL";
        public final static String READY = "READY";
    }

    static {
        Status.init("de.vdvcount.app.kvs.status");
    }

}
