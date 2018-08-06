package com.example.android.vestigeapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "vestige")
public class VestigeEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    private int status;
    @ColumnInfo(name = "modified_on")
    private Date modifiedOn;
    @ColumnInfo(name = "created_on")
    private Date createdOn;

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }



    public VestigeEntry(int id, String description, int status, Date modifiedOn,Date createdOn) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.modifiedOn = modifiedOn;
        this.createdOn = createdOn;
    }

    @Ignore
    public VestigeEntry(String description, int status, Date modifiedOn,Date createdOn) {
        this.description = description;
        this.status = status;
        this.modifiedOn = modifiedOn;
        this.createdOn = createdOn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }






}
