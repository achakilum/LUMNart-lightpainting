package com.bluelithalo.lumnart;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PatternListAdapter extends RecyclerView.Adapter<PatternListAdapter.PatternViewHolder>
{
    private List<String> patternFilePathList;
    private PatternListContainer patternListContainer;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class PatternViewHolder extends RecyclerView.ViewHolder
    {
        public TextView patternNameTextView;
        public TextView patternAuthorTextView;
        public TextView patternDescriptionTextView;
        public ImageButton patternPlayButton;
        public ImageButton patternOptionsButton;

        public PatternViewHolder(View v)
        {
            super(v);
            patternNameTextView = (TextView) v.findViewById(R.id.pattern_name_text_view);
            patternAuthorTextView = (TextView) v.findViewById(R.id.pattern_author_text_view);
            patternDescriptionTextView = (TextView) v.findViewById(R.id.pattern_description_text_view);
            patternPlayButton = (ImageButton) v.findViewById(R.id.pattern_play_button);
            patternOptionsButton = (ImageButton) v.findViewById(R.id.pattern_options_button);
        }
    }

    public PatternListAdapter(List<String> newPatternFilePathList, PatternListContainer newPatternListContainer)
    {
        patternFilePathList = newPatternFilePathList;
        patternListContainer = newPatternListContainer;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PatternListAdapter.PatternViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pattern_list_item, parent, false);
        PatternViewHolder pvh = new PatternViewHolder(v);
        return pvh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PatternViewHolder holder, final int position)
    {
        String name = "";
        String author = "";
        String description = "";
        int colorCode = 0;

        try
        {
            JsonFactory jsonFactory = new JsonFactory();
            File patternFile = new File(patternFilePathList.get(position));
            JsonParser jsonParser = jsonFactory.createParser(patternFile);
            jsonParser.nextToken();

            while (jsonParser.nextToken() != JsonToken.END_OBJECT)
            {
                String token = jsonParser.getCurrentName();
                if (token == null)
                {
                    continue;
                }
                if (token.equals("name"))
                {
                    name = jsonParser.getText();
                }
                if (token.equals("author"))
                {
                    author = jsonParser.getText();
                }
                if (token.equals("description"))
                {
                    description = jsonParser.getText();
                }
                if (token.equals("colorCode"))
                {
                    colorCode = jsonParser.getValueAsInt();
                }
                if (token.equals("layers"))
                {
                    break;
                }
            }

            jsonParser.close();
        }
        catch (JsonGenerationException e)
        {
            name = "EXCEPTION";
            author = "JsonGenerationException";
            description = e.getMessage();
            colorCode = 0;
        }
        catch (JsonMappingException e)
        {
            name = "EXCEPTION";
            author = "JsonMappingException";
            description = e.getMessage();
            colorCode = 0;
        }
        catch (IOException e)
        {
            name = "EXCEPTION";
            author = "IOException";
            description = e.getMessage();
            colorCode = 0;
        }

        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(colorCode), Color.green(colorCode), Color.blue(colorCode), hsv);
        int colorCodeFaded = Color.HSVToColor(150, new float[]{hsv[0], hsv[1], hsv[2] * 0.5f});

        holder.patternNameTextView.setText(name);
        holder.patternNameTextView.setBackgroundColor(colorCode);

        holder.patternAuthorTextView.setText(author);
        holder.patternAuthorTextView.setBackgroundColor(colorCode);

        holder.patternDescriptionTextView.setText(description);
        holder.patternDescriptionTextView.setBackgroundColor(colorCodeFaded);

        holder.patternPlayButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    patternListContainer.onPatternPlayButtonClick(patternFilePathList.get(position));
                }
            });

        holder.patternOptionsButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    patternListContainer.onPatternEditButtonClick(patternFilePathList.get(position));
                }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                patternListContainer.onPatternListItemLongClick(patternFilePathList.get(position));
                return true;
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return patternFilePathList.size();
    }

    public interface PatternListContainer
    {
        void onPatternPlayButtonClick(String patternFilepath);
        void onPatternEditButtonClick(String patternFilepath);
        void onPatternListItemLongClick(String patternFilepath);
    }
}
