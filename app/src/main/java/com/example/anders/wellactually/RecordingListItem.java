package com.example.anders.wellactually;

import android.support.annotation.NonNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Anders on 25/01/2018.
 */

class RecordingListItem {
    private String name;
    private String savePath;
    private long duration;

    public RecordingListItem(String name, String newSavePath, long duration) {
        this.duration = duration;
        this.name = name;
        savePath = newSavePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getTimestamp() {
        Date date = new Date(duration);
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(date);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RecordingListItem)
            return name.equals(((RecordingListItem) o).getName());
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

}
