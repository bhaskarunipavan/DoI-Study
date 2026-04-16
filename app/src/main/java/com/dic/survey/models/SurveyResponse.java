package com.dic.survey.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "survey_responses")
public class SurveyResponse {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "questionnaire_type")
    public String questionnaireType; // DIC, ENTERPRISE, ASSOCIATION, STAKEHOLDER

    @ColumnInfo(name = "district")
    public String district;

    @ColumnInfo(name = "respondent_name")
    public String respondentName;

    @ColumnInfo(name = "form_data")
    public String formData; // JSON blob of all answers

    @ColumnInfo(name = "status")
    public String status; // DRAFT, SUBMITTED, SYNCED

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    @ColumnInfo(name = "submitted_at")
    public long submittedAt;

    @ColumnInfo(name = "synced_at")
    public long syncedAt;

    @ColumnInfo(name = "officer_id")
    public String officerId;

    @ColumnInfo(name = "server_id")
    public String serverId; // ID after sync to Supabase

    @ColumnInfo(name = "sync_attempts")
    public int syncAttempts;

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_SUBMITTED = "SUBMITTED";
    public static final String STATUS_SYNCED = "SYNCED";

    public static final String TYPE_DIC = "DIC";
    public static final String TYPE_ENTERPRISE = "ENTERPRISE";
    public static final String TYPE_ASSOCIATION = "ASSOCIATION";
    public static final String TYPE_STAKEHOLDER = "STAKEHOLDER";
}
