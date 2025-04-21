package de.vdvcount.app.common;

public class Status extends KeyValueStore {

    public final static String STATUS = "de.vdvcount.app.kvs.status.STATUS";

    public final static String CURRENT_STATION_ID = "de.vdvcount.app.kvs.status.CURRENT_STATION_ID";
    public final static String CURRENT_STATION_NAME = "de.vdvcount.app.kvs.status.CURRENT_STATION_NAME";
    public final static String CURRENT_TRIP_ID = "de.vdvcount.app.kvs.status.CURRENT_TRIP_ID";
    public final static String CURRENT_START_STOP_SEQUENCE = "de.vdvcount.app.kvs.status.CURRENT_START_STOP_SEQUENCE";
    public final static String CURRENT_VEHICLE_ID = "de.vdvcount.app.kvs.status.CURRENT_VEHICLE_ID";
    public final static String CURRENT_COUNTED_DOOR_IDS = "de.vdvcount.app.kvs.status.CURRENT_COUNTED_DOOR_IDS";

    public static class Values {
        public final static String INITIAL = "INITIAL";
        public final static String READY = "READY";
        public final static String COUNTING = "COUNTING";
    }

}
