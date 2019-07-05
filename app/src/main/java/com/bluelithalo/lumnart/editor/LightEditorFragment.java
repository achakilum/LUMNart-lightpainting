package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelithalo.lumnart.App;
import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Figure;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Stage;
import com.bluelithalo.lumnart.pattern.TextFigure;
import com.bluelithalo.lumnart.util.ItemTouchHelperAdapter;
import com.bluelithalo.lumnart.util.SimpleItemTouchHelperCallback;
import com.bluelithalo.lumnart.util.SnackbarFactory;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LightEditorFragment.OnLightEditListener} interface
 * to handle interaction events.
 * Use the {@link LightEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LightEditorFragment extends Fragment implements    EditTextDialogFragment.OnConfirmEditTextListener,
                                                                LightFigureDialogFragment.OnLightFigureSelectedListener,
                                                                LightPropertyStageListAdapter.LightPropertyStageListContainer,
                                                                LightVisibleStageDialogFragment.OnConfirmLightVisibleStageListener,
                                                                LightColorStageDialogFragment.OnConfirmLightColorStageListener,
                                                                LightPositionStageDialogFragment.OnConfirmLightPositionStageListener,
                                                                LightDimensionsStageDialogFragment.OnConfirmLightDimensionsStageListener,
                                                                LightAngleStageDialogFragment.OnConfirmLightAngleStageListener/*,
                                                                FigureChooserDialogFragment.OnConfirmFigureChoice,
                                                                VisibleStageEditorDialogFragment.OnConfirmVisibleStageEdit,
                                                                DimensionsStageEditorDialogFragment.OnConfirmDimensionsStageEdit,
                                                                PositionStageEditorDialogFragment.OnConfirmPositionStageEdit,
                                                                AngleStageEditorDialogFragment.OnConfirmAngleStageEdit*/
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SERIALIZED_LIGHT = "serializedLight";
    private static final String LIGHT_INDEX = "lightIndex";
    private static final String SET_LIGHT_NAME = "Light name";
    private static final String SET_LIGHT_FIGURE = "Light figure options";
    private static final String SET_LIGHT_VISIBLE_STAGE = "Light visibility stage options";
    private static final String SET_LIGHT_COLOR_STAGE = "Light color stage options";
    private static final String SET_LIGHT_DIMENSIONS_STAGE = "Light dimensions stage options";
    private static final String SET_LIGHT_POSITION_STAGE = "Light position stage options";
    private static final String SET_LIGHT_ANGLE_STAGE = "Light angle stage options";

    private Light light;
    private int lightIndex;
    private OnLightEditListener mListener;

    private ArrayList<String> figureIds;
    private HashMap<String, Integer> figureIdToIconId;
    private HashMap<String, Integer> figureIdToStringId;

    private TextView nameTextView;
    private ImageButton nameEditButton;
    private ImageView figureImageView;
    private TextView figureTextView;
    private ImageButton figureEditButton;
    private TextView uniformDimensionsTextView;
    private Switch uniformDimensionsSwitch;
    private SeekBar outlineSeekBar;
    private TextView outlineStatusTextView;

    private TextView headerTextView;

    private RecyclerView lightPropertyStageRecyclerView;
    private LightPropertyStageListAdapter lightPropertyStageRVAdapter;
    private RecyclerView.LayoutManager lightPropertyStageRVLayoutManager;

    private Button stageAddButton;

    private ImageButton visiblePropertyButton;
    private ImageButton colorPropertyButton;
    private ImageButton dimensionsPropertyButton;
    private ImageButton positionPropertyButton;
    private ImageButton anglePropertyButton;

    public LightEditorFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param serializedLight the JSON serialization of the light.
     * @return A new instance of fragment LightEditorFragment.
     */
    public static LightEditorFragment newInstance(String serializedLight, int lightIdx)
    {
        LightEditorFragment fragment = new LightEditorFragment();
        Bundle args = new Bundle();
        args.putString(SERIALIZED_LIGHT, serializedLight);
        args.putInt(LIGHT_INDEX, lightIdx);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            try
            {
                String serializedLight = getArguments().getString(SERIALIZED_LIGHT);
                light = new Light(serializedLight);
            }
            catch (JSONException e)
            {
                light = new Light();
            }

            lightIndex = getArguments().getInt(LIGHT_INDEX);

            figureIds = new ArrayList<String>();
            figureIds.add("arrownotched");
            figureIds.add("heart");
            figureIds.add("arrowpentagon");
            figureIds.add("righttriangle");
            figureIds.add("triangle");
            figureIds.add("star6pt");
            figureIds.add("star5ptround");
            figureIds.add("arrowchevron");
            figureIds.add("parallelogram");
            figureIds.add("octagon");
            figureIds.add("star3pt");
            figureIds.add("star5pt");
            figureIds.add("arrow");
            figureIds.add("star4pt");
            figureIds.add("trapezoid");
            figureIds.add("hexagon");
            figureIds.add("pentagon");
            figureIds.add("ellipse");
            figureIds.add("rectangle");

            figureIdToIconId = new HashMap<String, Integer>();
            figureIdToIconId.put("arrownotched", R.drawable.ic_arrownotched);
            figureIdToIconId.put("heart", R.drawable.ic_heart);
            figureIdToIconId.put("arrowpentagon", R.drawable.ic_arrowpentagon);
            figureIdToIconId.put("righttriangle", R.drawable.ic_righttriangle);
            figureIdToIconId.put("triangle", R.drawable.ic_triangle);
            figureIdToIconId.put("star6pt", R.drawable.ic_star6pt);
            figureIdToIconId.put("star5ptround", R.drawable.ic_star5ptround);
            figureIdToIconId.put("arrowchevron", R.drawable.ic_arrowchevron);
            figureIdToIconId.put("parallelogram", R.drawable.ic_parallelogram);
            figureIdToIconId.put("octagon", R.drawable.ic_octagon);
            figureIdToIconId.put("star3pt", R.drawable.ic_star3pt);
            figureIdToIconId.put("star5pt", R.drawable.ic_star5pt);
            figureIdToIconId.put("arrow", R.drawable.ic_arrow);
            figureIdToIconId.put("star4pt", R.drawable.ic_star4pt);
            figureIdToIconId.put("trapezoid", R.drawable.ic_trapezoid);
            figureIdToIconId.put("hexagon", R.drawable.ic_hexagon);
            figureIdToIconId.put("pentagon", R.drawable.ic_pentagon);
            figureIdToIconId.put("ellipse", R.drawable.ic_ellipse);
            figureIdToIconId.put("rectangle", R.drawable.ic_rectangle);

            figureIdToStringId = new HashMap<String, Integer>();
            figureIdToStringId.put("arrownotched", R.string.figure_arrownotched);
            figureIdToStringId.put("heart", R.string.figure_heart);
            figureIdToStringId.put("arrowpentagon", R.string.figure_arrowpentagon);
            figureIdToStringId.put("righttriangle", R.string.figure_righttriangle);
            figureIdToStringId.put("triangle", R.string.figure_triangle);
            figureIdToStringId.put("star6pt", R.string.figure_star6pt);
            figureIdToStringId.put("star5ptround", R.string.figure_star5ptround);
            figureIdToStringId.put("arrowchevron", R.string.figure_arrowchevron);
            figureIdToStringId.put("parallelogram", R.string.figure_parallelogram);
            figureIdToStringId.put("octagon", R.string.figure_octagon);
            figureIdToStringId.put("star3pt", R.string.figure_star3pt);
            figureIdToStringId.put("star5pt", R.string.figure_star5pt);
            figureIdToStringId.put("arrow", R.string.figure_arrow);
            figureIdToStringId.put("star4pt", R.string.figure_star4pt);
            figureIdToStringId.put("trapezoid", R.string.figure_trapezoid);
            figureIdToStringId.put("hexagon", R.string.figure_hexagon);
            figureIdToStringId.put("pentagon", R.string.figure_pentagon);
            figureIdToStringId.put("ellipse", R.string.figure_ellipse);
            figureIdToStringId.put("rectangle", R.string.figure_rectangle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_light_editor, container, false);

        nameTextView = (TextView) view.findViewById(R.id.light_name_text_view);
        nameTextView.setText(light.getName());

        nameEditButton = (ImageButton) view.findViewById(R.id.light_name_edit_button);
        nameEditButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                invokeEditTextDialog(SET_LIGHT_NAME, nameTextView.getText().toString());
            }
        });
        nameEditButton.setClickable(true);

        figureImageView = (ImageView) view.findViewById(R.id.light_figure_image_view);
        if (light.getFigure().getFigureType() == Figure.Type.IMAGE)
        {
            figureImageView.setImageResource(R.drawable.ic_image);
        }
        else
        if (light.getFigure().getFigureType() == Figure.Type.TEXT)
        {
            figureImageView.setImageResource(R.drawable.ic_text);
        }
        else
        {
            figureImageView.setImageResource(figureIdToIconId.get(light.getFigure().getIdentifier()));
        }

        figureTextView = (TextView) view.findViewById(R.id.light_figure_text_view);

        if (light.getFigure().getFigureType() == Figure.Type.IMAGE)
        {
            figureTextView.setText(light.getFigure().getIdentifier());
        }
        else
        if (light.getFigure().getFigureType() == Figure.Type.TEXT)
        {
            figureTextView.setText(((TextFigure) light.getFigure()).getText());
        }
        else
        {
            figureTextView.setText(figureIdToStringId.get(light.getFigure().getIdentifier()));
        }

        figureEditButton = (ImageButton) view.findViewById(R.id.light_figure_edit_button);
        figureEditButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                invokeFigureDialog(SET_LIGHT_FIGURE, light.getFigure());
            }
        });

        uniformDimensionsTextView = (TextView) view.findViewById(R.id.light_uniform_dimensions_text_view);
        uniformDimensionsTextView.setText((light.hasUniformDimensions()) ? R.string.fragment_light_editor_uniform_dimensions_on : R.string.fragment_light_editor_uniform_dimensions_off);

        uniformDimensionsSwitch = (Switch) view.findViewById(R.id.light_uniform_dimensions_switch);
        uniformDimensionsSwitch.setChecked(light.hasUniformDimensions());
        uniformDimensionsSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                light.setUniformDimensions(isChecked);
                uniformDimensionsTextView.setText((isChecked) ? R.string.fragment_light_editor_uniform_dimensions_on : R.string.fragment_light_editor_uniform_dimensions_off);
                mListener.onSetLightUniformDimensions(isChecked);
            }
        });

        outlineSeekBar = (SeekBar) view.findViewById(R.id.light_outline_seek_bar);
        outlineStatusTextView = (TextView) view.findViewById(R.id.light_outline_status_text_view);
        outlineSeekBar.setProgress((light.getOutlineWidth() > 1.0f) ? outlineSeekBar.getMax() : Math.round((light.getOutlineWidth() - 0.5f) * 200));
        outlineStatusTextView.setText((light.getOutlineWidth() > 1.0f) ? "Off" : "" + outlineSeekBar.getProgress() + "%");
        outlineSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (progress > 100)
                {
                    light.setOutlineWidth(1.5f);
                    outlineStatusTextView.setText("Off");
                    mListener.onSetLightOutlineWidth(1.5f);
                }
                else
                {
                    float newOutlineWidth = (progress / 200.0f) + 0.5f;
                    light.setOutlineWidth(newOutlineWidth);
                    outlineStatusTextView.setText("" + progress + "%");
                    mListener.onSetLightOutlineWidth(newOutlineWidth);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        headerTextView = (TextView) view.findViewById(R.id.light_property_header_text_view);
        headerTextView.setText(R.string.fragment_light_editor_property_color_header);

        stageAddButton = (Button) view.findViewById(R.id.light_property_stage_add_button);
        stageAddButton.setText(R.string.fragment_light_editor_add_stage_color);
        stageAddButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Stage newStage = new Stage(4, 60);
                newStage.setStartVector(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
                newStage.setEndVector(new float[]{0.0f, 0.0f, 0.0f, 1.0f});

                light.getProperty(Property.Type.Color).insertStage(newStage);
                lightPropertyStageRVAdapter.notifyDataSetChanged();
                mListener.onAddPropertyStage(Property.Type.Color, new Stage(newStage));
            }
        });

        lightPropertyStageRecyclerView = (RecyclerView) view.findViewById(R.id.light_property_stage_recycler_view);
        lightPropertyStageRecyclerView.setHasFixedSize(false);

        lightPropertyStageRVLayoutManager = new LinearLayoutManager(this.getContext());
        lightPropertyStageRecyclerView.setLayoutManager(lightPropertyStageRVLayoutManager);

        lightPropertyStageRVAdapter = new LightPropertyStageListAdapter(light,this, Property.Type.Color);

        lightPropertyStageRecyclerView.setAdapter(lightPropertyStageRVAdapter);
        lightPropertyStageRecyclerView.setNestedScrollingEnabled(false);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) lightPropertyStageRVAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(lightPropertyStageRecyclerView);

        visiblePropertyButton = (ImageButton) view.findViewById(R.id.light_property_visible_button);
        visiblePropertyButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageRVAdapter.setCurrentPropertyType(Property.Type.Visible);
                lightPropertyStageRVAdapter.notifyDataSetChanged();
                headerTextView.setText(R.string.fragment_light_editor_property_visible_header);
                stageAddButton.setText(R.string.fragment_light_editor_add_stage_visible);
                stageAddButton.setOnClickListener(new ImageButton.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Stage newStage = new Stage(1, 60);
                        newStage.setStartVector(new float[]{1.0f});
                        newStage.setEndVector(new float[]{1.0f});

                        light.getProperty(Property.Type.Visible).insertStage(newStage);
                        lightPropertyStageRVAdapter.notifyDataSetChanged();
                        mListener.onAddPropertyStage(Property.Type.Visible, new Stage(newStage));
                    }
                });
            }
        });

        colorPropertyButton = (ImageButton) view.findViewById(R.id.light_property_color_button);
        colorPropertyButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageRVAdapter.setCurrentPropertyType(Property.Type.Color);
                lightPropertyStageRVAdapter.notifyDataSetChanged();
                headerTextView.setText(R.string.fragment_light_editor_property_color_header);
                stageAddButton.setText(R.string.fragment_light_editor_add_stage_color);
                stageAddButton.setOnClickListener(new ImageButton.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Stage newStage = new Stage(4, 60);
                        newStage.setStartVector(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
                        newStage.setEndVector(new float[]{1.0f, 1.0f, 1.0f, 1.0f});

                        light.getProperty(Property.Type.Color).insertStage(newStage);
                        lightPropertyStageRVAdapter.notifyDataSetChanged();
                        mListener.onAddPropertyStage(Property.Type.Color, new Stage(newStage));
                    }
                });
            }
        });

        dimensionsPropertyButton = (ImageButton) view.findViewById(R.id.light_property_dimensions_button);
        dimensionsPropertyButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageRVAdapter.setCurrentPropertyType(Property.Type.Dimensions);
                lightPropertyStageRVAdapter.notifyDataSetChanged();
                headerTextView.setText(R.string.fragment_light_editor_property_dimensions_header);
                stageAddButton.setText(R.string.fragment_light_editor_add_stage_dimensions);
                stageAddButton.setOnClickListener(new ImageButton.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Stage newStage = new Stage(2, 60);
                        newStage.setStartVector(new float[]{0.5f, 0.5f});
                        newStage.setEndVector(new float[]{0.5f, 0.5f});

                        light.getProperty(Property.Type.Dimensions).insertStage(newStage);
                        lightPropertyStageRVAdapter.notifyDataSetChanged();
                        mListener.onAddPropertyStage(Property.Type.Dimensions, new Stage(newStage));
                    }
                });
            }
        });

        positionPropertyButton = (ImageButton) view.findViewById(R.id.light_property_position_button);
        positionPropertyButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageRVAdapter.setCurrentPropertyType(Property.Type.Position);
                lightPropertyStageRVAdapter.notifyDataSetChanged();
                headerTextView.setText(R.string.fragment_light_editor_property_position_header);
                stageAddButton.setText(R.string.fragment_light_editor_add_stage_position);
                stageAddButton.setOnClickListener(new ImageButton.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Stage newStage = new Stage(2, 60);
                        newStage.setStartVector(new float[]{0.0f, 0.0f});
                        newStage.setEndVector(new float[]{0.0f, 0.0f});

                        light.getProperty(Property.Type.Position).insertStage(newStage);
                        lightPropertyStageRVAdapter.notifyDataSetChanged();
                        mListener.onAddPropertyStage(Property.Type.Position, new Stage(newStage));
                    }
                });
            }
        });

        anglePropertyButton = (ImageButton) view.findViewById(R.id.light_property_angle_button);
        anglePropertyButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lightPropertyStageRVAdapter.setCurrentPropertyType(Property.Type.Angle);
                lightPropertyStageRVAdapter.notifyDataSetChanged();
                headerTextView.setText(R.string.fragment_light_editor_property_angle_header);
                stageAddButton.setText(R.string.fragment_light_editor_add_stage_angle);
                stageAddButton.setOnClickListener(new ImageButton.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Stage newStage = new Stage(1, 60);
                        newStage.setStartVector(new float[]{0.0f});
                        newStage.setEndVector(new float[]{0.0f});

                        light.getProperty(Property.Type.Angle).insertStage(newStage);
                        lightPropertyStageRVAdapter.notifyDataSetChanged();
                        mListener.onAddPropertyStage(Property.Type.Angle, new Stage(newStage));
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnLightEditListener)
        {
            mListener = (OnLightEditListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnLightEditListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mListener.onBackFromLight(light.toJSONString(), lightIndex);
    }

    public Light getLight()
    {
        return light;
    }

    @Override
    public void invokeEditTextDialog(String inputPrompt, String defaultText)
    {
        FragmentManager fm = getFragmentManager();
        EditTextDialogFragment editTextDialogFragment = EditTextDialogFragment.newInstance(inputPrompt, defaultText);
        editTextDialogFragment.setTargetFragment(LightEditorFragment.this, 300);
        editTextDialogFragment.show(getFragmentManager(), "editTextDialogFragment");
    }

    @Override
    public void onConfirmEditText(String inputPrompt, String inputText)
    {
        if (inputPrompt.equals(SET_LIGHT_NAME))
        {
            nameTextView.setText(inputText);
            light.setName(inputText);
            mListener.onSetLightName(inputText);
        }
    }

    @Override
    public void invokeFigureDialog(String inputPrompt, Figure defaultFigure)
    {
        FragmentManager fm = getFragmentManager();
        LightFigureDialogFragment lightFigureDialogFragment = LightFigureDialogFragment.newInstance(inputPrompt, defaultFigure);
        lightFigureDialogFragment.setTargetFragment(LightEditorFragment.this, 300);
        lightFigureDialogFragment.show(getFragmentManager(), "lightFigureDialogFragment");
    }

    @Override
    public void onSelectFigure(String inputPrompt, Figure inputFigure)
    {
        light.setFigure(inputFigure);
        figureImageView.setImageResource(figureIdToIconId.get(inputFigure.getIdentifier()));
        figureTextView.setText(figureIdToStringId.get(inputFigure.getIdentifier()));
        mListener.onSetLightFigure(inputFigure);
    }

    @Override
    public void onSelectTextFigure(String inputPrompt, TextFigure inputTextFigure)
    {
        light.setFigure(inputTextFigure);
        figureImageView.setImageResource(R.drawable.ic_text);
        figureTextView.setText(inputTextFigure.getText());
        mListener.onSetLightFigure(inputTextFigure);
    }

    @Override
    public void invokeLightVisibleStageDialog(String inputPrompt, Stage defaultVisibleStage, int visibleStageIdx)
    {
        FragmentManager fm = getFragmentManager();
        LightVisibleStageDialogFragment lightVisibleStageDialogFragment = LightVisibleStageDialogFragment.newInstance(inputPrompt, defaultVisibleStage, visibleStageIdx);
        lightVisibleStageDialogFragment.setTargetFragment(LightEditorFragment.this, 300);
        lightVisibleStageDialogFragment.show(getFragmentManager(), "lightVisibleStageDialogFragment");
    }

    @Override
    public void onConfirmLightVisibleStage(String inputPrompt, Stage inputVisibleStage, int visibleStageIdx)
    {
        if (inputPrompt.equals(SET_LIGHT_VISIBLE_STAGE))
        {
            light.getProperty(Property.Type.Visible).setStage(inputVisibleStage, visibleStageIdx);
            lightPropertyStageRVAdapter.notifyItemChanged(visibleStageIdx);
            mListener.onSetPropertyStage(Property.Type.Visible, visibleStageIdx, inputVisibleStage);
        }
    }

    @Override
    public void invokeLightColorStageDialog(String inputPrompt, Stage defaultColorStage, int colorStageIdx)
    {
        FragmentManager fm = getFragmentManager();
        LightColorStageDialogFragment lightColorStageDialogFragment = LightColorStageDialogFragment.newInstance(inputPrompt, defaultColorStage, colorStageIdx);
        lightColorStageDialogFragment.setTargetFragment(LightEditorFragment.this, 300);
        lightColorStageDialogFragment.show(getFragmentManager(), "lightColorStageDialogFragment");
    }

    @Override
    public void onConfirmLightColorStage(String inputPrompt, Stage inputColorStage, int colorStageIdx)
    {
        if (inputPrompt.equals(SET_LIGHT_COLOR_STAGE))
        {
            light.getProperty(Property.Type.Color).setStage(inputColorStage, colorStageIdx);
            lightPropertyStageRVAdapter.notifyItemChanged(colorStageIdx);
            mListener.onSetPropertyStage(Property.Type.Color, colorStageIdx, inputColorStage);
        }
    }

    @Override
    public void invokeLightPositionStageDialog(String inputPrompt, Stage defaultPositionStage, int positionStageIdx)
    {
        FragmentManager fm = getFragmentManager();
        LightPositionStageDialogFragment lightPositionStageDialogFragment = LightPositionStageDialogFragment.newInstance(inputPrompt, defaultPositionStage, positionStageIdx);
        lightPositionStageDialogFragment.setTargetFragment(LightEditorFragment.this, 300);
        lightPositionStageDialogFragment.show(getFragmentManager(), "lightPositionStageDialogFragment");
    }

    @Override
    public void onConfirmLightPositionStage(String inputPrompt, Stage inputPositionStage, int positionStageIdx)
    {
        if (inputPrompt.equals(SET_LIGHT_POSITION_STAGE))
        {
            light.getProperty(Property.Type.Position).setStage(inputPositionStage, positionStageIdx);
            lightPropertyStageRVAdapter.notifyItemChanged(positionStageIdx);
            mListener.onSetPropertyStage(Property.Type.Position, positionStageIdx, inputPositionStage);
        }
    }

    @Override
    public void invokeLightDimensionsStageDialog(String inputPrompt, Stage defaultDimensionsStage, int dimensionsStageIdx)
    {
        FragmentManager fm = getFragmentManager();
        LightDimensionsStageDialogFragment lightDimensionsStageDialogFragment = LightDimensionsStageDialogFragment.newInstance(inputPrompt, defaultDimensionsStage, dimensionsStageIdx, light.hasUniformDimensions());
        lightDimensionsStageDialogFragment.setTargetFragment(LightEditorFragment.this, 300);
        lightDimensionsStageDialogFragment.show(getFragmentManager(), "lightDimensionsStageDialogFragment");
    }

    @Override
    public void onConfirmLightDimensionsStage(String inputPrompt, Stage inputDimensionsStage, int dimensionsStageIdx)
    {
        if (inputPrompt.equals(SET_LIGHT_DIMENSIONS_STAGE))
        {
            light.getProperty(Property.Type.Dimensions).setStage(inputDimensionsStage, dimensionsStageIdx);
            lightPropertyStageRVAdapter.notifyItemChanged(dimensionsStageIdx);
            mListener.onSetPropertyStage(Property.Type.Dimensions, dimensionsStageIdx, inputDimensionsStage);
        }
    }

    @Override
    public void invokeLightAngleStageDialog(String inputPrompt, Stage defaultAngleStage, int angleStageIdx)
    {
        FragmentManager fm = getFragmentManager();
        LightAngleStageDialogFragment lightAngleStageDialogFragment = LightAngleStageDialogFragment.newInstance(inputPrompt, defaultAngleStage, angleStageIdx);
        lightAngleStageDialogFragment.setTargetFragment(LightEditorFragment.this, 300);
        lightAngleStageDialogFragment.show(getFragmentManager(), "lightAngleStageDialogFragment");
    }

    @Override
    public void onConfirmLightAngleStage(String inputPrompt, Stage inputAngleStage, int angleStageIdx)
    {
        if (inputPrompt.equals(SET_LIGHT_ANGLE_STAGE))
        {
            light.getProperty(Property.Type.Angle).setStage(inputAngleStage, angleStageIdx);
            lightPropertyStageRVAdapter.notifyItemChanged(angleStageIdx);
            mListener.onSetPropertyStage(Property.Type.Angle, angleStageIdx, inputAngleStage);
        }
    }

    @Override
    public void onLightPropertyStageSelected(Property.Type propertyType, int stageNum)
    {
        if (propertyType == Property.Type.Visible)
        {
            invokeLightVisibleStageDialog(SET_LIGHT_VISIBLE_STAGE, light.getProperty(Property.Type.Visible).getStage(stageNum), stageNum);
        }
        else
        if (propertyType == Property.Type.Color)
        {
            invokeLightColorStageDialog(SET_LIGHT_COLOR_STAGE, light.getProperty(Property.Type.Color).getStage(stageNum), stageNum);
        }
        else
        if (propertyType == Property.Type.Position)
        {
            invokeLightPositionStageDialog(SET_LIGHT_POSITION_STAGE, light.getProperty(Property.Type.Position).getStage(stageNum), stageNum);
        }
        else
        if (propertyType == Property.Type.Dimensions)
        {
            invokeLightDimensionsStageDialog(SET_LIGHT_DIMENSIONS_STAGE, light.getProperty(Property.Type.Dimensions).getStage(stageNum), stageNum);
        }
        else
        if (propertyType == Property.Type.Angle)
        {
            invokeLightAngleStageDialog(SET_LIGHT_ANGLE_STAGE, light.getProperty(Property.Type.Angle).getStage(stageNum), stageNum);
        }
    }

    @Override
    public void onLightPropertyStageDelete(Property.Type propertyType, int stageNum)
    {
        final Property property = light.getProperty(propertyType);
        final Property.Type propertyKind = propertyType;

        if (property.getStageCount() <= 1)
        {
            SnackbarFactory.showSnackbar(getView(), getResources().getString(R.string.error_light_property_stage_deletion_one), Snackbar.LENGTH_SHORT);
            return;
        }

        final int stageIdx = stageNum;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
        alertDialogBuilder.setMessage("Are you sure you want to delete this property?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        property.removeStage(stageIdx);
                        lightPropertyStageRVAdapter.notifyDataSetChanged();
                        mListener.onRemovePropertyStage(propertyKind, stageIdx);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // do nothing
                    }
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onLightPropertyStageDuplicate(Property.Type propertyType, int stageNum)
    {
        Property property = light.getProperty(propertyType);
        Stage dupeStage = new Stage(property.getStage(stageNum));

        property.insertStage(stageNum, dupeStage);
        lightPropertyStageRVAdapter.notifyDataSetChanged();
        mListener.onDuplicatePropertyStage(propertyType, stageNum);
    }

    @Override
    public void onLightPropertyStageMove(Property.Type propertyType, int fromStageNum, int toStageNum)
    {
        Property property = light.getProperty(propertyType);

        if (fromStageNum < toStageNum)
        {
            for (int i = fromStageNum; i < toStageNum; i++)
            {
                Stage stageRef = property.getStage(i);
                property.setStage(property.getStage(i+1), i);
                property.setStage(stageRef, i+1);
            }
        }
        else
        {
            for (int i = fromStageNum; i > toStageNum; i--)
            {
                Stage stageRef = property.getStage(i);
                property.setStage(property.getStage(i-1), i);
                property.setStage(stageRef, i-1);
            }
        }

        lightPropertyStageRVAdapter.notifyItemMoved(fromStageNum, toStageNum);
        mListener.onMovePropertyStage(propertyType, fromStageNum, toStageNum);
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLightEditListener
    {
        void onSetLightName(String newName);
        void onSetLightFigure(Figure newFigure);
        void onSetLightUniformDimensions(boolean newUniformDimensions);
        void onSetLightOutlineWidth(float newOutlineWidth);

        void onAddPropertyStage(Property.Type propertyCode, Stage newStage);
        void onSetPropertyStage(Property.Type propertyCode, int stageIdx, Stage newStage);
        void onDuplicatePropertyStage(Property.Type propertyCode, int stageIdx);
        void onRemovePropertyStage(Property.Type propertyCode, int stageIdx);
        void onMovePropertyStage(Property.Type propertyCode, int fromStageIdx, int toStageIdx);

        void onBackFromLight(String serializedLight, int lightIdx);
    }
}
