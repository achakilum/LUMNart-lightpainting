package com.bluelithalo.lumnart.editor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Stage;
import com.bluelithalo.lumnart.util.ItemTouchHelperAdapter;

public class LightPropertyStageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter
{
    private Light light;
    private Property.Type currentPropertyType;
    private LightPropertyStageListContainer lightPropertyStageListContainer;

    public static class VisibleStageViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView visibleStageStateImageView;
        public TextView visibleStageLengthTextView;
        public TextView visibleStageStateTextView;
        public ImageButton visibleStageDuplicateButton;
        public ImageButton visibleStageDeleteButton;
        public ImageView visibleStageDragIndicator;

        public VisibleStageViewHolder(View v)
        {
            super(v);

            visibleStageStateImageView = (ImageView) v.findViewById(R.id.light_property_visible_stage_state_image_view);
            visibleStageLengthTextView = (TextView) v.findViewById(R.id.light_property_visible_stage_length_text_view);
            visibleStageStateTextView = (TextView) v.findViewById(R.id.light_property_visible_stage_state_text_view);
            visibleStageDuplicateButton = (ImageButton) v.findViewById(R.id.light_property_visible_stage_duplicate_button);
            visibleStageDeleteButton = (ImageButton) v.findViewById(R.id.light_property_visible_stage_delete_button);
            visibleStageDragIndicator = (ImageView) v.findViewById(R.id.light_property_visible_stage_drag_indicator);
        }
    }

    public static class ColorStageViewHolder extends RecyclerView.ViewHolder
    {
        public View colorStageStartView;
        public View colorStageEndView;
        public TextView colorStageLengthTextView;
        public TextView colorStageTransitionTextView;
        public ImageButton colorStageDuplicateButton;
        public ImageButton colorStageDeleteButton;
        public ImageView colorStageDragIndicator;

        public ColorStageViewHolder(View v)
        {
            super(v);

            colorStageStartView = (View) v.findViewById(R.id.light_property_color_stage_start_view);
            colorStageEndView = (View) v.findViewById(R.id.light_property_color_stage_end_view);
            colorStageLengthTextView = (TextView) v.findViewById(R.id.light_property_color_stage_length_text_view);
            colorStageTransitionTextView = (TextView) v.findViewById(R.id.light_property_color_stage_transition_text_view);
            colorStageDuplicateButton = (ImageButton) v.findViewById(R.id.light_property_color_stage_duplicate_button);
            colorStageDeleteButton = (ImageButton) v.findViewById(R.id.light_property_color_stage_delete_button);
            colorStageDragIndicator = (ImageView) v.findViewById(R.id.light_property_color_stage_drag_indicator);
        }
    }

    public static class PositionStageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView positionStageStartXTextView;
        public TextView positionStageStartYTextView;
        public TextView positionStageEndXTextView;
        public TextView positionStageEndYTextView;
        public TextView positionStageLengthTextView;
        public TextView positionStageTransitionTextView;
        public ImageButton positionStageDuplicateButton;
        public ImageButton positionStageDeleteButton;
        public ImageView positionStageDragIndicator;

        public PositionStageViewHolder(View v)
        {
            super(v);

            positionStageStartXTextView = (TextView) v.findViewById(R.id.light_property_position_stage_start_x_text_view);
            positionStageStartYTextView = (TextView) v.findViewById(R.id.light_property_position_stage_start_y_text_view);
            positionStageEndXTextView = (TextView) v.findViewById(R.id.light_property_position_stage_end_x_text_view);
            positionStageEndYTextView = (TextView) v.findViewById(R.id.light_property_position_stage_end_y_text_view);
            positionStageLengthTextView = (TextView) v.findViewById(R.id.light_property_position_stage_length_text_view);
            positionStageTransitionTextView = (TextView) v.findViewById(R.id.light_property_position_stage_transition_text_view);
            positionStageDuplicateButton = (ImageButton) v.findViewById(R.id.light_property_position_stage_duplicate_button);
            positionStageDeleteButton = (ImageButton) v.findViewById(R.id.light_property_position_stage_delete_button);
            positionStageDragIndicator = (ImageView) v.findViewById(R.id.light_property_position_stage_drag_indicator);
        }
    }

    public static class DimensionsStageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView dimensionsStageStartWidthTextView;
        public TextView dimensionsStageStartHeightTextView;
        public TextView dimensionsStageEndWidthTextView;
        public TextView dimensionsStageEndHeightTextView;
        public TextView dimensionsStageLengthTextView;
        public TextView dimensionsStageTransitionTextView;
        public ImageButton dimensionsStageDuplicateButton;
        public ImageButton dimensionsStageDeleteButton;
        public ImageView dimensionsStageDragIndicator;

        public DimensionsStageViewHolder(View v)
        {
            super(v);

            dimensionsStageStartWidthTextView = (TextView) v.findViewById(R.id.light_property_dimensions_stage_start_w_text_view);
            dimensionsStageStartHeightTextView = (TextView) v.findViewById(R.id.light_property_dimensions_stage_start_h_text_view);
            dimensionsStageEndWidthTextView = (TextView) v.findViewById(R.id.light_property_dimensions_stage_end_w_text_view);
            dimensionsStageEndHeightTextView = (TextView) v.findViewById(R.id.light_property_dimensions_stage_end_h_text_view);
            dimensionsStageLengthTextView = (TextView) v.findViewById(R.id.light_property_dimensions_stage_length_text_view);
            dimensionsStageTransitionTextView = (TextView) v.findViewById(R.id.light_property_dimensions_stage_transition_text_view);
            dimensionsStageDuplicateButton = (ImageButton) v.findViewById(R.id.light_property_dimensions_stage_duplicate_button);
            dimensionsStageDeleteButton = (ImageButton) v.findViewById(R.id.light_property_dimensions_stage_delete_button);
            dimensionsStageDragIndicator = (ImageView) v.findViewById(R.id.light_property_dimensions_stage_drag_indicator);
        }
    }

    public static class AngleStageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView angleStageStartTextView;
        public TextView angleStageEndTextView;
        public TextView angleStageLengthTextView;
        public TextView angleStageTransitionTextView;
        public ImageButton angleStageDuplicateButton;
        public ImageButton angleStageDeleteButton;
        public ImageView angleStageDragIndicator;

        public AngleStageViewHolder(View v)
        {
            super(v);

            angleStageStartTextView = (TextView) v.findViewById(R.id.light_property_angle_stage_start_text_view);
            angleStageEndTextView = (TextView) v.findViewById(R.id.light_property_angle_stage_end_text_view);
            angleStageLengthTextView = (TextView) v.findViewById(R.id.light_property_angle_stage_length_text_view);
            angleStageTransitionTextView = (TextView) v.findViewById(R.id.light_property_angle_stage_transition_text_view);
            angleStageDuplicateButton = (ImageButton) v.findViewById(R.id.light_property_angle_stage_duplicate_button);
            angleStageDeleteButton = (ImageButton) v.findViewById(R.id.light_property_angle_stage_delete_button);
            angleStageDragIndicator = (ImageView) v.findViewById(R.id.light_property_angle_stage_drag_indicator);
        }
    }

    public LightPropertyStageListAdapter(Light newLight, LightPropertyStageListContainer newLightPropertyStageListContainer, Property.Type newPropertyType)
    {
        light = newLight;
        lightPropertyStageListContainer = newLightPropertyStageListContainer;
        currentPropertyType = newPropertyType;
    }

    @Override
    public int getItemCount()
    {
        return light.getProperty(currentPropertyType).getStageCount();
    }

    @Override
    public int getItemViewType(int position)
    {
        return currentPropertyType.ordinal();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (viewType == Property.Type.Visible.ordinal())
        {
            View visibleStageView = inflater.inflate(R.layout.light_property_visible_stage_list_item, viewGroup, false);
            viewHolder = new VisibleStageViewHolder(visibleStageView);
        }
        else
        if (viewType == Property.Type.Color.ordinal())
        {
            View colorStageView = inflater.inflate(R.layout.light_property_color_stage_list_item, viewGroup, false);
            viewHolder = new ColorStageViewHolder(colorStageView);
        }
        else
        if (viewType == Property.Type.Position.ordinal())
        {
            View positionStageView = inflater.inflate(R.layout.light_property_position_stage_list_item, viewGroup, false);
            viewHolder = new PositionStageViewHolder(positionStageView);
        }
        else
        if (viewType == Property.Type.Dimensions.ordinal())
        {
            View dimensionsStageView = inflater.inflate(R.layout.light_property_dimensions_stage_list_item, viewGroup, false);
            viewHolder = new DimensionsStageViewHolder(dimensionsStageView);
        }
        else
        if (viewType == Property.Type.Angle.ordinal())
        {
            View angleStageView = inflater.inflate(R.layout.light_property_angle_stage_list_item, viewGroup, false);
            viewHolder = new AngleStageViewHolder(angleStageView);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        if (viewHolder.getItemViewType() == Property.Type.Visible.ordinal())
        {
            VisibleStageViewHolder vsvh = (VisibleStageViewHolder) viewHolder;
            configureVisibleStageViewHolder(vsvh, position);
        }
        else
        if (viewHolder.getItemViewType() == Property.Type.Color.ordinal())
        {
            ColorStageViewHolder csvh = (ColorStageViewHolder) viewHolder;
            configureColorStageViewHolder(csvh, position);
        }
        else
        if (viewHolder.getItemViewType() == Property.Type.Position.ordinal())
        {
            PositionStageViewHolder psvh = (PositionStageViewHolder) viewHolder;
            configurePositionStageViewHolder(psvh, position);
        }
        else
        if (viewHolder.getItemViewType() == Property.Type.Dimensions.ordinal())
        {
            DimensionsStageViewHolder dsvh = (DimensionsStageViewHolder) viewHolder;
            configureDimensionsStageViewHolder(dsvh, position);
        }
        else
        if (viewHolder.getItemViewType() == Property.Type.Angle.ordinal())
        {
            AngleStageViewHolder asvh = (AngleStageViewHolder) viewHolder;
            configureAngleStageViewHolder(asvh, position);
        }
    }

    public Property.Type getCurrentPropertyType()
    {
        return currentPropertyType;
    }

    public void setCurrentPropertyType(Property.Type newPropertyType)
    {
        currentPropertyType = newPropertyType;
    }

    private void configureVisibleStageViewHolder(VisibleStageViewHolder viewHolder, int position)
    {
        final Stage stage = light.getProperty(Property.Type.Visible).getStage(position);
        boolean visible = (stage.getStartVector()[0] >= 0.5f);
        final int pos = position;

        viewHolder.visibleStageStateImageView.setImageResource((visible) ? R.drawable.ic_visible : R.drawable.ic_invisible);

        String stageLengthString = Integer.toString(stage.getDuration());
        viewHolder.visibleStageLengthTextView.setText(stageLengthString);

        viewHolder.visibleStageStateTextView.setText((visible) ? R.string.stage_visible_state_on : R.string.stage_visible_state_off);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageSelected(Property.Type.Visible, pos);
            }
        });

        viewHolder.visibleStageDuplicateButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageDuplicate(Property.Type.Visible, pos);
            }
        });

        viewHolder.visibleStageDeleteButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageDelete(Property.Type.Visible, pos);
            }
        });
    }

    private void configureColorStageViewHolder(ColorStageViewHolder viewHolder, int position)
    {
        final Stage stage = light.getProperty(Property.Type.Color).getStage(position);
        float[] startColor = stage.getStartVector();
        float[] endColor = stage.getEndVector();
        final int pos = position;

        int startColorCode =    (Math.round(startColor[3] * 255) << 24) + (Math.round(startColor[0] * 255) << 16) +
                                (Math.round(startColor[1] * 255) << 8) + Math.round(startColor[2] * 255);
        int endColorCode =      (Math.round(endColor[3] * 255) << 24) + (Math.round(endColor[0] * 255) << 16) +
                                (Math.round(endColor[1] * 255) << 8) + Math.round(endColor[2] * 255);

        viewHolder.colorStageStartView.setBackgroundColor(startColorCode);
        viewHolder.colorStageEndView.setBackgroundColor(endColorCode);

        String stageLengthString = Integer.toString(stage.getDuration());
        viewHolder.colorStageLengthTextView.setText(stageLengthString);

        int stageTransitionStringId = 0;
        switch(stage.getTransitionCurve())
        {
            case None:
                stageTransitionStringId = R.string.stage_transition_none;
                break;
            case Instant:
                stageTransitionStringId = R.string.stage_transition_instant;
                break;
            case Linear:
                stageTransitionStringId = R.string.stage_transition_linear;
                break;
            case Sinusoidal:
                stageTransitionStringId = R.string.stage_transition_sinusoidal;
                break;
        }
        viewHolder.colorStageTransitionTextView.setText(stageTransitionStringId);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageSelected(Property.Type.Color, pos);
            }
        });

        viewHolder.colorStageDuplicateButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageDuplicate(Property.Type.Color, pos);
            }
        });

        viewHolder.colorStageDeleteButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageDelete(Property.Type.Color, pos);
            }
        });
    }

    private void configurePositionStageViewHolder(PositionStageViewHolder viewHolder, int position)
    {
        final Stage stage = light.getProperty(Property.Type.Position).getStage(position);
        float[] startPosition = stage.getStartVector();
        float[] endPosition = stage.getEndVector();
        final int pos = position;

        viewHolder.positionStageStartXTextView.setText(Integer.toString(Math.round(startPosition[0] * 1000.0f)));
        viewHolder.positionStageStartYTextView.setText(Integer.toString(Math.round(startPosition[1] * 1000.0f)));
        viewHolder.positionStageEndXTextView.setText(Integer.toString(Math.round(endPosition[0] * 1000.0f)));
        viewHolder.positionStageEndYTextView.setText(Integer.toString(Math.round(endPosition[1] * 1000.0f)));

        String stageLengthString = Integer.toString(stage.getDuration());
        viewHolder.positionStageLengthTextView.setText(stageLengthString);

        int stageTransitionStringId = 0;
        switch(stage.getTransitionCurve())
        {
            case None:
                stageTransitionStringId = R.string.stage_transition_none;
                break;
            case Instant:
                stageTransitionStringId = R.string.stage_transition_instant;
                break;
            case Linear:
                stageTransitionStringId = R.string.stage_transition_linear;
                break;
            case Sinusoidal:
                stageTransitionStringId = R.string.stage_transition_sinusoidal;
                break;
        }
        viewHolder.positionStageTransitionTextView.setText(stageTransitionStringId);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageSelected(Property.Type.Position, pos);
            }
        });

        viewHolder.positionStageDuplicateButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageDuplicate(Property.Type.Position, pos);
            }
        });

        viewHolder.positionStageDeleteButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageDelete(Property.Type.Position, pos);
            }
        });
    }

    private void configureDimensionsStageViewHolder(DimensionsStageViewHolder viewHolder, int position)
    {
        final Stage stage = light.getProperty(Property.Type.Dimensions).getStage(position);
        float[] startDimensions = stage.getStartVector();
        float[] endDimensions = stage.getEndVector();
        final int pos = position;

        viewHolder.dimensionsStageStartWidthTextView.setText(Integer.toString(Math.round(startDimensions[0] * 1000.0f)));
        viewHolder.dimensionsStageStartHeightTextView.setText(Integer.toString(Math.round(startDimensions[1] * 1000.0f)));
        viewHolder.dimensionsStageEndWidthTextView.setText(Integer.toString(Math.round(endDimensions[0] * 1000.0f)));
        viewHolder.dimensionsStageEndHeightTextView.setText(Integer.toString(Math.round(endDimensions[1] * 1000.0f)));

        String stageLengthString = Integer.toString(stage.getDuration());
        viewHolder.dimensionsStageLengthTextView.setText(stageLengthString);

        int stageTransitionStringId = 0;
        switch(stage.getTransitionCurve())
        {
            case None:
                stageTransitionStringId = R.string.stage_transition_none;
                break;
            case Instant:
                stageTransitionStringId = R.string.stage_transition_instant;
                break;
            case Linear:
                stageTransitionStringId = R.string.stage_transition_linear;
                break;
            case Sinusoidal:
                stageTransitionStringId = R.string.stage_transition_sinusoidal;
                break;
        }
        viewHolder.dimensionsStageTransitionTextView.setText(stageTransitionStringId);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageSelected(Property.Type.Dimensions, pos);
            }
        });

        viewHolder.dimensionsStageDuplicateButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageDuplicate(Property.Type.Dimensions, pos);
            }
        });

        viewHolder.dimensionsStageDeleteButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageDelete(Property.Type.Dimensions, pos);
            }
        });
    }

    private void configureAngleStageViewHolder(AngleStageViewHolder viewHolder, int position)
    {
        final Stage stage = light.getProperty(Property.Type.Angle).getStage(position);
        float[] startAngle = stage.getStartVector();
        float[] endAngle = stage.getEndVector();
        final int pos = position;

        viewHolder.angleStageStartTextView.setText(Integer.toString(Math.round(startAngle[0])));
        viewHolder.angleStageEndTextView.setText(Integer.toString(Math.round(endAngle[0])));

        String stageLengthString = Integer.toString(stage.getDuration());
        viewHolder.angleStageLengthTextView.setText(stageLengthString);

        int stageTransitionStringId = 0;
        switch(stage.getTransitionCurve())
        {
            case None:
                stageTransitionStringId = R.string.stage_transition_none;
                break;
            case Instant:
                stageTransitionStringId = R.string.stage_transition_instant;
                break;
            case Linear:
                stageTransitionStringId = R.string.stage_transition_linear;
                break;
            case Sinusoidal:
                stageTransitionStringId = R.string.stage_transition_sinusoidal;
                break;
        }
        viewHolder.angleStageTransitionTextView.setText(stageTransitionStringId);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageSelected(Property.Type.Angle, pos);
            }
        });

        viewHolder.angleStageDuplicateButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageDuplicate(Property.Type.Angle, pos);
            }
        });

        viewHolder.angleStageDeleteButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageListContainer.onLightPropertyStageDelete(Property.Type.Angle, pos);
            }
        });
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition)
    {
        lightPropertyStageListContainer.onLightPropertyStageMove(currentPropertyType, fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position)
    {

    }

    public interface LightPropertyStageListContainer
    {
        void onLightPropertyStageSelected(Property.Type propertyType, int stageNum);
        void onLightPropertyStageDelete(Property.Type propertyType, int stageNum);
        void onLightPropertyStageDuplicate(Property.Type propertyType, int stageNum);
        void onLightPropertyStageMove(Property.Type propertyType, int fromStageNum, int toStageNum);
    }
}
