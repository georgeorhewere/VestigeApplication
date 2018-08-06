package com.example.android.vestigeapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {VestigeEntry.class},version = 1,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class VestigeDatabase extends RoomDatabase {

private static final Object LOCK = new Object();
private static final String DATABASE_NAME = "vestige_database";
private static VestigeDatabase dbInstance;

public static VestigeDatabase getInstance(Context context){

    if(dbInstance == null){
synchronized (LOCK) {
    dbInstance = Room.databaseBuilder(context.getApplicationContext(), VestigeDatabase.class, DATABASE_NAME)

            .build();
}
    }

return dbInstance;
}


public abstract VestigeDAO vestigeDAO();
}
