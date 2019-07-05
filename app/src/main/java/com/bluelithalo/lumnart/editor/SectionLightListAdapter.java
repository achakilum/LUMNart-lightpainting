package com.bluelithalo.lumnart.editor;

import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Figure;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Section;

import java.util.HashMap;

public class SectionLightListAdapter extends RecyclerView.Adapter<SectionLightListAdapter.LightViewHolder>
{
    private Section section;
    private SectionLightListContainer sectionLightListContainer;
    private HashMap<String, Integer> figureIdToDrawableId;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class LightViewHolder extends RecyclerView.ViewHolder
    {
        public ImageButton lightVisibleToggleButton;
        public TextView lightNameTextView;
        public ImageButton lightDeleteButton;
        public ImageButton lightDuplicateButton;
        public ImageView lightImageView;

        public LightViewHolder(View v)
        {
            super(v);

            lightVisibleToggleButton = (ImageButton) v.findViewById(R.id.section_light_visible_toggle_button);
            lightNameTextView = (TextView) v.findViewById(R.id.section_light_name_text_view);
            lightDeleteButton = (ImageButton) v.findViewById(R.id.section_light_delete_button);
            lightDuplicateButton = (ImageButton) v.findViewById(R.id.section_light_duplicate_button);
            lightImageView = (ImageView) v.findViewById(R.id.section_light_image_view);
        }
    }

    public SectionLightListAdapter(Section newSection, SectionLightListContainer newSectionLightListContainer)
    {
        section = newSection;
        sectionLightListContainer = newSectionLightListContainer;

        figureIdToDrawableId = new HashMap<String, Integer>();
        figureIdToDrawableId.put("arrownotched", R.drawable.arrownotched);
        figureIdToDrawableId.put("heart", R.drawable.heart);
        figureIdToDrawableId.put("arrowpentagon", R.drawable.arrowpentagon);
        figureIdToDrawableId.put("righttriangle", R.drawable.righttriangle);
        figureIdToDrawableId.put("triangle", R.drawable.triangle);
        figureIdToDrawableId.put("star6pt", R.drawable.star6pt);
        figureIdToDrawableId.put("star5ptround", R.drawable.star5ptround);
        figureIdToDrawableId.put("arrowchevron", R.drawable.arrowchevron);
        figureIdToDrawableId.put("parallelogram", R.drawable.parallelogram);
        figureIdToDrawableId.put("octagon", R.drawable.octagon);
        figureIdToDrawableId.put("star3pt", R.drawable.star3pt);
        figureIdToDrawableId.put("star5pt", R.drawable.star5pt);
        figureIdToDrawableId.put("arrow", R.drawable.arrow);
        figureIdToDrawableId.put("star4pt", R.drawable.star4pt);
        figureIdToDrawableId.put("trapezoid", R.drawable.trapezoid);
        figureIdToDrawableId.put("hexagon", R.drawable.hexagon);
        figureIdToDrawableId.put("pentagon", R.drawable.pentagon);
        figureIdToDrawableId.put("ellipse", R.drawable.ellipse);
        figureIdToDrawableId.put("rectangle", R.drawable.rectangle);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SectionLightListAdapter.LightViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_light_list_item, parent, false);
        LightViewHolder lvh = new LightViewHolder(v);
        return lvh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LightViewHolder holder, final int position)
    {
        final Light light = section.getLight(position);

        holder.lightVisibleToggleButton.setImageResource((section.isLightHidden(position)) ? R.drawable.ic_invisible : R.drawable.ic_visible);
        holder.lightVisibleToggleButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sectionLightListContainer.onLightVisibleToggle(position);
            }
        });

        holder.lightNameTextView.setText(light.getName());
        holder.lightNameTextView.setOnClickListener(new TextView.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sectionLightListContainer.onLightSelected(position);
            }
        });

        holder.lightDeleteButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sectionLightListContainer.onLightDelete(position);
            }
        });

        holder.lightDuplicateButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sectionLightListContainer.onLightDuplicate(position);
            }
        });

        if (light.getFigure().getFigureType() == Figure.Type.IMAGE)
        {
            holder.lightImageView.setImageResource(R.drawable.ic_image);
        }
        else
        if (light.getFigure().getFigureType() == Figure.Type.TEXT)
        {
            holder.lightImageView.setImageResource(R.drawable.ic_text);
        }
        else
        {
            holder.lightImageView.setImageResource(figureIdToDrawableId.get(light.getFigure().getIdentifier()));
        }

        holder.lightImageView.setOnClickListener(new SurfaceView.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sectionLightListContainer.onLightSelected(position);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return section.getLightCount();
    }

    public interface SectionLightListContainer
    {
        void onLightVisibleToggle(int lightNum);
        void onLightSelected(int lightNum);
        void onLightDelete(int lightNum);
        void onLightDuplicate(int lightNum);
    }
}