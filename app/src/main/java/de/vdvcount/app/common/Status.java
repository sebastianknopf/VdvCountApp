package de.vdvcount.app.common;

public class Status extends KeyValueStore {

    public final static String STATUS = "de.vdvcount.app.kvs.status.STATUS";

    public final static String CURRENT_STATION_ID = "de.vdvcount.app.kvs.status.CURRENT_STATION_ID";
    public final static String CURRENT_STATION_NAME = "de.vdvcount.app.kvs.status.CURRENT_STATION_NAME";

    public static class Values {
        public final static String INITIAL = "INITIAL";
        public final static String READY = "READY";
        public final static String COUNTING = "COUNTING";
    }

}
