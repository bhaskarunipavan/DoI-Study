package com.dic.survey.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.dic.survey.models.SurveyResponse;

@Database(entities = {SurveyResponse.class}, version = 1, exportSchema = false)
public abstract class SurveyDatabase extends RoomDatabase {

    private static volatile SurveyDatabase INSTANCE;
    private static final String DATABASE_NAME = "dic_survey.db";

    public abstract SurveyDao surveyDao();

    public static SurveyDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SurveyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            SurveyDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
