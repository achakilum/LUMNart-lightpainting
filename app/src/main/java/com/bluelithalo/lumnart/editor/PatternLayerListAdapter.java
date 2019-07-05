package com.bluelithalo.lumnart.editor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.util.ItemTouchHelperAdapter;

public class PatternLayerListAdapter extends RecyclerView.Adapter<PatternLayerListAdapter.LayerViewHolder> implements ItemTouchHelperAdapter
{
    private Pattern pattern;
    private PatternLayerListContainer patternLayerListContainer;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class LayerViewHolder extends RecyclerView.ViewHolder
    {
        public ImageButton layerVisibleToggleButton;
        public TextView layerNameTextView;
        public ImageButton layerDeleteButton;
        public ImageButton layerDuplicateButton;
        public ImageView layerDragIndicator;

        public LayerViewHolder(View v)
        {
            super(v);

            layerVisibleToggleButton = (ImageButton) v.findViewById(R.id.pattern_layer_visible_toggle_button);
            layerNameTextView = (TextView) v.findViewById(R.id.pattern_layer_name_text_view);
            layerDeleteButton = (ImageButton) v.findViewById(R.id.pattern_layer_delete_button);
            layerDuplicateButton = (ImageButton) v.findViewById(R.id.pattern_layer_duplicate_button);
            layerDragIndicator = (ImageView) v.findViewById(R.id.pattern_layer_drag_indicator);
        }
    }

    public PatternLayerListAdapter(Pattern newPattern, PatternLayerListContainer newPatternPatternLayerListContainer)
    {
        pattern = newPattern;
        patternLayerListContainer = newPatternPatternLayerListContainer;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PatternLayerListAdapter.LayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pattern_layer_list_item, parent, false);
        LayerViewHolder pvh = new LayerViewHolder(v);
        return pvh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LayerViewHolder holder, final int position)
    {
        final Layer layer = pattern.getLayer(position);

        holder.layerVisibleToggleButton.setImageResource((pattern.isLayerHidden(position)) ? R.drawable.ic_invisible : R.drawable.ic_visible);
        holder.layerVisibleToggleButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                patternLayerListContainer.onLayerVisibleToggle(position);
            }
        });

        holder.layerNameTextView.setText(layer.getName());
        holder.layerNameTextView.setOnClickListener(new TextView.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                patternLayerListContainer.onLayerSelected(position);
            }
        });

        holder.layerDeleteButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                patternLayerListContainer.onLayerDelete(position);
            }
        });

        holder.layerDuplicateButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                patternLayerListContainer.onLayerDuplicate(position);
            }
        });
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition)
    {
        patternLayerListContainer.onLayerMove(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position)
    {

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return pattern.getLayerCount();
    }

    public interface PatternLayerListContainer
    {
        void onLayerVisibleToggle(int layerNum);
        void onLayerSelected(int layerNum);
        void onLayerDelete(int layerNum);
        void onLayerDuplicate(int layerNum);
        void onLayerMove(int fromLayerNum, int toLayerNum);
    }
}
