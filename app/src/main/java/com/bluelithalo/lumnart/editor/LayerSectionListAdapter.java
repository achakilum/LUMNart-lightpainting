package com.bluelithalo.lumnart.editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluelithalo.lumnart.App;
import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.util.AABB;

public class LayerSectionListAdapter extends RecyclerView.Adapter<LayerSectionListAdapter.SectionViewHolder>
{
    private Layer layer;
    private LayerSectionListContainer layerSectionListContainer;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SectionViewHolder extends RecyclerView.ViewHolder
    {
        public ImageButton sectionVisibleToggleButton;
        public TextView sectionNameTextView;
        public ImageButton sectionDeleteButton;
        public ImageButton sectionDuplicateButton;
        public ImageView sectionImageView;

        public SectionViewHolder(View v)
        {
            super(v);

            sectionVisibleToggleButton = (ImageButton) v.findViewById(R.id.layer_section_visible_toggle_button);
            sectionNameTextView = (TextView) v.findViewById(R.id.layer_section_name_text_view);
            sectionDeleteButton = (ImageButton) v.findViewById(R.id.layer_section_delete_button);
            sectionDuplicateButton = (ImageButton) v.findViewById(R.id.layer_section_duplicate_button);
            sectionImageView = (ImageView) v.findViewById(R.id.layer_section_image_view);
        }
    }

    public LayerSectionListAdapter(Layer newLayer, LayerSectionListContainer newLayerSectionListContainer)
    {
        layer = newLayer;
        layerSectionListContainer = newLayerSectionListContainer;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LayerSectionListAdapter.SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layer_section_list_item, parent, false);
        SectionViewHolder svh = new SectionViewHolder(v);
        return svh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SectionViewHolder holder, final int position)
    {
        final Section section = layer.getSection(position);

        holder.sectionImageView.setImageBitmap(this.getAABBBitmap(section.getBoundingBox()));

        holder.sectionVisibleToggleButton.setImageResource((layer.isSectionHidden(position)) ? R.drawable.ic_invisible : R.drawable.ic_visible);
        holder.sectionVisibleToggleButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                layerSectionListContainer.onSectionVisibleToggle(position);
            }
        });

        holder.sectionNameTextView.setText(section.getName());
        holder.sectionNameTextView.setOnClickListener(new TextView.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                layerSectionListContainer.onSectionSelected(position);
            }
        });

        holder.sectionDeleteButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                layerSectionListContainer.onSectionDelete(position);
            }
        });

        holder.sectionDuplicateButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                layerSectionListContainer.onSectionDuplicate(position);
            }
        });

        holder.sectionImageView.setOnClickListener(new SurfaceView.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                layerSectionListContainer.onSectionSelected(position);
            }
        });
    }

    private Bitmap getAABBBitmap(AABB aabb)
    {
        int imageViewWidth = 72;
        int imageViewHeight = 72;

        Bitmap bm = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        paint.setStrokeWidth(2);


        float aabbMinXLeft = (aabb.getMinimumX() * imageViewWidth) + (imageViewWidth / 2.0f);
        float aabbMinYBottom = -((aabb.getMinimumY() * imageViewHeight) - (imageViewHeight / 2.0f));
        float aabbMaxXRight = (aabb.getMaximumX() * imageViewWidth) + (imageViewWidth / 2.0f);
        float aabbMaxYTop = -((aabb.getMaximumY() * imageViewHeight) - (imageViewHeight / 2.0f));

        float aspectRatio = App.getAspectRatio(true);
        float screenMinX = (imageViewWidth / 2.0f) - ((imageViewWidth * aspectRatio) / 2.0f);
        float screenMinY = 0.0f;
        float screenMaxX = (imageViewWidth / 2.0f) + ((imageViewWidth * aspectRatio) / 2.0f);
        float screenMaxY = imageViewHeight * 1.0f;

        // Draw background
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        canvas.drawRect(0, 0, imageViewWidth, imageViewHeight, paint);

        // Draw screen dimensions
        paint.setColor(App.getApplication().getResources().getColor(R.color.surface_view_screen_color));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, imageViewWidth, imageViewHeight, paint);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(screenMinX, screenMaxY, screenMaxX, screenMinY, paint);

        // Draw AABB
        paint.setColor(App.getApplication().getResources().getColor(R.color.section_aabb_color));
        paint.setStrokeWidth(0.5f);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(aabbMinXLeft, aabbMaxYTop, aabbMaxXRight, aabbMinYBottom, paint);

        // Draw AABB outline
        paint.setColor(App.getApplication().getResources().getColor(R.color.section_aabb_outline_color));
        paint.setStrokeWidth(1.25f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(aabbMinXLeft, aabbMaxYTop, aabbMaxXRight, aabbMinYBottom, paint);

        return bm;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return layer.getSectionCount();
    }

    public interface LayerSectionListContainer
    {
        void onSectionVisibleToggle(int sectionNum);
        void onSectionSelected(int sectionNum);
        void onSectionDelete(int sectionNum);
        void onSectionDuplicate(int sectionNum);
    }
}
