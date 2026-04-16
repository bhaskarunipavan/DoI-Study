package com.dic.survey.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dic.survey.R;
import com.dic.survey.models.SurveyResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResponsesAdapter extends RecyclerView.Adapter<ResponsesAdapter.ViewHolder> {

    private List<SurveyResponse> items;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public ResponsesAdapter(List<SurveyResponse> items) { this.items = items; }

    public void updateData(List<SurveyResponse> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_response, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        SurveyResponse r = items.get(pos);

        // Type icon + color
        switch (r.questionnaireType != null ? r.questionnaireType : "") {
            case "DIC":          h.tvTypeIcon.setText("🏢"); break;
            case "ENTERPRISE":   h.tvTypeIcon.setText("🏭"); break;
            case "ASSOCIATION":  h.tvTypeIcon.setText("🤝"); break;
            case "STAKEHOLDER":  h.tvTypeIcon.setText("🏛"); break;
            default:             h.tvTypeIcon.setText("📋"); break;
        }

        // Type label
        String typeLabel = r.questionnaireType != null ? r.questionnaireType : "UNKNOWN";
        switch (typeLabel) {
            case "DIC":         h.tvType.setText("DoI / DIC Questionnaire"); break;
            case "ENTERPRISE":  h.tvType.setText("Industry / Enterprise Survey"); break;
            case "ASSOCIATION": h.tvType.setText("Industrial Association Survey"); break;
            case "STAKEHOLDER": h.tvType.setText("Stakeholder Questionnaire"); break;
            default:            h.tvType.setText(typeLabel); break;
        }

        h.tvDistrict.setText(r.district != null && !r.district.isEmpty() ? r.district + " District" : "District not set");

        // Date
        long ts = r.submittedAt > 0 ? r.submittedAt : r.updatedAt;
        h.tvDate.setText(ts > 0 ? sdf.format(new Date(ts)) : "—");

        // Status badge
        h.tvStatus.setText(r.status != null ? r.status : "DRAFT");
        int badgeColor;
        switch (r.status != null ? r.status : "") {
            case "SYNCED":    badgeColor = Color.parseColor("#4CAF50"); break;
            case "SUBMITTED": badgeColor = Color.parseColor("#2196F3"); break;
            default:          badgeColor = Color.parseColor("#FF9800"); break; // DRAFT
        }
        h.tvStatus.getBackground().setTint(badgeColor);
    }

    @Override
    public int getItemCount() { return items != null ? items.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTypeIcon, tvType, tvDistrict, tvDate, tvStatus;
        ViewHolder(View v) {
            super(v);
            tvTypeIcon = v.findViewById(R.id.tvTypeIcon);
            tvType     = v.findViewById(R.id.tvType);
            tvDistrict = v.findViewById(R.id.tvDistrict);
            tvDate     = v.findViewById(R.id.tvDate);
            tvStatus   = v.findViewById(R.id.tvStatus);
        }
    }
}
