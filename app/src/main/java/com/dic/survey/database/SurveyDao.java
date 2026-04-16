package com.dic.survey.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.dic.survey.models.SurveyResponse;
import java.util.List;

@Dao
public interface SurveyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SurveyResponse response);

    @Update
    void update(SurveyResponse response);

    @Delete
    void delete(SurveyResponse response);

    @Query("SELECT * FROM survey_responses ORDER BY updated_at DESC")
    LiveData<List<SurveyResponse>> getAllResponses();

    @Query("SELECT * FROM survey_responses ORDER BY updated_at DESC")
    List<SurveyResponse> getAllResponsesSync();

    @Query("SELECT * FROM survey_responses WHERE id = :id")
    SurveyResponse getById(int id);

    @Query("SELECT * FROM survey_responses WHERE status = :status ORDER BY updated_at DESC")
    LiveData<List<SurveyResponse>> getByStatus(String status);

    @Query("SELECT * FROM survey_responses WHERE status IN ('SUBMITTED','DRAFT') AND (server_id IS NULL OR server_id = '')")
    List<SurveyResponse> getPendingSync();

    @Query("SELECT * FROM survey_responses WHERE questionnaire_type = :type ORDER BY updated_at DESC")
    LiveData<List<SurveyResponse>> getByType(String type);

    @Query("SELECT COUNT(*) FROM survey_responses WHERE status = 'SUBMITTED' AND (server_id IS NULL OR server_id = '')")
    LiveData<Integer> getPendingSyncCount();

    @Query("SELECT COUNT(*) FROM survey_responses WHERE status = 'SUBMITTED' AND (server_id IS NULL OR server_id = '')")
    int getPendingSyncCountSync();

    @Query("UPDATE survey_responses SET server_id = :serverId, status = 'SYNCED', synced_at = :syncedAt WHERE id = :id")
    void markSynced(int id, String serverId, long syncedAt);

    @Query("SELECT * FROM survey_responses WHERE district = :district ORDER BY updated_at DESC")
    LiveData<List<SurveyResponse>> getByDistrict(String district);
}
