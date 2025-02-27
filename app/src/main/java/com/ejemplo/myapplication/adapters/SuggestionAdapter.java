package com.ejemplo.myapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ejemplo.myapplication.R;
import com.ejemplo.myapplication.models.PlantSuggestion;

import java.util.List;

public class SuggestionAdapter extends BaseAdapter {

    private Context context;
    private List<PlantSuggestion> suggestions;

    public SuggestionAdapter(Context context, List<PlantSuggestion> suggestions) {
        this.context = context;
        this.suggestions = suggestions;
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public Object getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dialog_item_suggestion, parent, false);
        }
        PlantSuggestion suggestion = suggestions.get(position);
        ImageView imageView = convertView.findViewById(R.id.suggestionImageView);
        TextView textView = convertView.findViewById(R.id.suggestionTextView);


        String imageUrl = suggestion.getImageUrl();
        Log.d("SuggestionAdapter", "Image URL: " + imageUrl);
        if(imageUrl == null || imageUrl.isEmpty()){
            imageView.setImageResource(R.drawable.default_plant_image);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_plant_image)
                    .error(R.drawable.error_image)
                    .into(imageView);
        }
        textView.setText(suggestion.toString());
        return convertView;
    }
}
