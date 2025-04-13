package de.vdvcount.app.common;

public class Status extends KeyValueStore {

    public final static String STATUS = "de.vdvcount.app.kvs.status.STATUS";

    public final static String CURRENT_STOP_ID = "de.vdvcount.app.kvs.status.CURRENT_STOP_ID";
    public final static String CURRENT_STOP_NAME = "de.vdvcount.app.kvs.status.CURRENT_STOP_NAME";

    public static class Values {
        public final static String INITIAL = "INITIAL";
        public final static String READY = "READY";
        public final static String COUNTING = "COUNTING";
    }

}
