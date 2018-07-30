package com.example.jburgos.life_notes.data;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    //when reading Date from Database converts timestamp to date
    public static Date toDate(Long timestamp){
        return timestamp ==null ? null : new Date(timestamp);
    }

    //when writing date into the Database converts Date into timestamp
    @TypeConverter
    public static Long toTimeStamp(Date date){
        return date == null ? null : date.getTime();
    }


}
