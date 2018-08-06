package com.example.android.vestigeapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
@Dao
public interface VestigeDAO {
    @Query("Select * from vestige Order by created_on desc")
    LiveData<List<VestigeEntry>> loadAllEntries();

    @Insert
    void insertEntry(VestigeEntry vestigeEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEntry(VestigeEntry vestigeEntry);

    @Delete
    void deleteEntry(VestigeEntry vestigeEntry);

    @Query("SELECT * FROM vestige WHERE id = :id")
    LiveData<VestigeEntry> loadEntryById(int id);




}
