package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Stage;
import com.bluelithalo.lumnart.util.SnackbarFactory;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditTextDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LightDimensionsStageDialogFragment extends DialogFragment implements LightDimensionsSurfaceView.OnLightDimensionsEditListener
{
    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String DIALOG_DIMENSIONS_STAGE_START = "DIALOG_DIMENSIONS_STAGE_START";
    private static final String DIALOG_DIMENSIONS_STAGE_END = "DIALOG_DIMENSIONS_STAGE_END";
    private static final String DIALOG_DIMENSIONS_STAGE_DURATION = "DIALOG_DIMENSIONS_STAGE_DURATION";
    private static final String DIALOG_DIMENSIONS_STAGE_TRANSITION = "DIALOG_DIMENSIONS_STAGE_TRANSITION";
    private static final String DIALOG_DIMENSIONS_STAGE_INDEX = "DIALOG_DIMENSIONS_STAGE_INDEX";
    private static final String DIALOG_DIMENSIONS_UNIFORM_STATE = "DIALOG_DIMENSIONS_UNIFORM_STATE";


    private Stage dimensionsStage;
    private TextView titleView;
    private int dimensionsStageIdx;
    private boolean uniformDimensions;

    private LightDimensionsSurfaceView touchEditSurfaceView;
    private Switch startEndSwitch;

    private EditText startWEditText;
    private EditText startHEditText;
    private EditText endWEditText;
    private EditText endHEditText;

    private EditText durationEditText;
    private Spinner transitionSpinner;

    private Button confirmButton;
    private Button cancelButton;

    public LightDimensionsStageDialogFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogTitle the title of the dialog box.
     * @param dialogDimensionsStage the dimensions stage represented in the dialog box
     * @return A new instance of fragment LightDimensionsStageDialogFragment.
     */
    public static LightDimensionsStageDialogFragment newInstance(String dialogTitle, Stage dialogDimensionsStage, int dimensionsStageIdx, boolean uniformDimensionsState)
    {
        LightDimensionsStageDialogFragment fragment = new LightDimensionsStageDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putFloatArray(DIALOG_DIMENSIONS_STAGE_START, Arrays.copyOf(dialogDimensionsStage.getStartVector(), dialogDimensionsStage.getVectorLength()));
        args.putFloatArray(DIALOG_DIMENSIONS_STAGE_END, Arrays.copyOf(dialogDimensionsStage.getEndVector(), dialogDimensionsStage.getVectorLength()));
        args.putInt(DIALOG_DIMENSIONS_STAGE_DURATION, dialogDimensionsStage.getDuration());
        args.putInt(DIALOG_DIMENSIONS_STAGE_TRANSITION, dialogDimensionsStage.getTransitionCurve().ordinal());
        args.putInt(DIALOG_DIMENSIONS_STAGE_INDEX, dimensionsStageIdx);
        args.putBoolean(DIALOG_DIMENSIONS_UNIFORM_STATE, uniformDimensionsState);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            dimensionsStage = new Stage(2, 60);
            dimensionsStage.setStartVector(getArguments().getFloatArray(DIALOG_DIMENSIONS_STAGE_START));
            dimensionsStage.setEndVector(getArguments().getFloatArray(DIALOG_DIMENSIONS_STAGE_END));
            dimensionsStage.setDuration(getArguments().getInt(DIALOG_DIMENSIONS_STAGE_DURATION));
            dimensionsStage.setTransitionCurve(Stage.Transition.values()[getArguments().getInt(DIALOG_DIMENSIONS_STAGE_TRANSITION)]);
            dimensionsStageIdx = getArguments().getInt(DIALOG_DIMENSIONS_STAGE_INDEX);
            uniformDimensions = getArguments().getBoolean(DIALOG_DIMENSIONS_UNIFORM_STATE);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.light_property_dimensions_stage_editor_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.light_property_dimensions_stage_editor_title_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        touchEditSurfaceView = (LightDimensionsSurfaceView) view.findViewById(R.id.light_property_dimensions_stage_editor_surface_view);
        touchEditSurfaceView.setUniformDimensions(uniformDimensions);
        touchEditSurfaceView.setStartDimensions(dimensionsStage.getStartVector());
        touchEditSurfaceView.setEndDimensions(dimensionsStage.getEndVector());
        touchEditSurfaceView.setOnLightDimensionsEditListener(this);

        startEndSwitch = (Switch) view.findViewById(R.id.light_property_dimensions_stage_editor_start_end_switch);
        startEndSwitch.setChecked(false);
        startEndSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                touchEditSurfaceView.setEndMovement(isChecked);
            }
        });

        startWEditText = (EditText) view.findViewById(R.id.light_property_dimensions_stage_editor_start_w_edit_text);
        startWEditText.setText(Integer.toString(Math.round(dimensionsStage.getStartVector()[0] * 1000.0f)));
        startWEditText.setFocusable(!uniformDimensions);
        startWEditText.setEnabled(!uniformDimensions);
        startWEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                float[] startDimensions = touchEditSurfaceView.getStartDimensions();

                try
                {
                    startDimensions[0] = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    startDimensions[0] = 0.5f;
                }

                touchEditSurfaceView.setStartDimensions(startDimensions);
            }
        });

        startHEditText = (EditText) view.findViewById(R.id.light_property_dimensions_stage_editor_start_h_edit_text);
        startHEditText.setText(Integer.toString(Math.round(dimensionsStage.getStartVector()[1] * 1000.0f)));
        startHEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                float[] startDimensions = touchEditSurfaceView.getStartDimensions();

                try
                {
                    startDimensions[1] = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    startDimensions[1] = 0.5f;
                }

                touchEditSurfaceView.setStartDimensions(startDimensions);
            }
        });

        endWEditText = (EditText) view.findViewById(R.id.light_property_dimensions_stage_editor_end_w_edit_text);
        endWEditText.setText(Integer.toString(Math.round(dimensionsStage.getEndVector()[0] * 1000.0f)));
        endWEditText.setFocusable(!uniformDimensions);
        endWEditText.setEnabled(!uniformDimensions);
        endWEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                float[] endDimensions = touchEditSurfaceView.getEndDimensions();

                try
                {
                    endDimensions[0] = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    endDimensions[0] = 0.5f;
                }

                touchEditSurfaceView.setEndDimensions(endDimensions);
            }
        });

        endHEditText = (EditText) view.findViewById(R.id.light_property_dimensions_stage_editor_end_h_edit_text);
        endHEditText.setText(Integer.toString(Math.round(dimensionsStage.getEndVector()[1] * 1000.0f)));
        endHEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                float[] endDimensions = touchEditSurfaceView.getEndDimensions();

                try
                {
                    endDimensions[1] = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    endDimensions[1] = 0.5f;
                }

                touchEditSurfaceView.setEndDimensions(endDimensions);
            }
        });

        durationEditText = (EditText) view.findViewById(R.id.light_property_dimensions_stage_editor_duration_edit_text);
        durationEditText.setText(Integer.toString(dimensionsStage.getDuration()));

        transitionSpinner = (Spinner) view.findViewById(R.id.light_property_dimensions_stage_editor_transition_spinner);
        String[] transitionStrings = new String[Stage.Transition.values().length];
        for (int i = 0; i < transitionStrings.length; i++)
        {
            transitionStrings[i] = Stage.Transition.values()[i].toString();
        }
        ArrayAdapter<String> transitionAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, transitionStrings);
        transitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transitionSpinner.setAdapter(transitionAdapter);
        transitionSpinner.setSelection(dimensionsStage.getTransitionCurve().ordinal());

        confirmButton = (Button) view.findViewById(R.id.light_property_dimensions_stage_editor_confirm_button);
        confirmButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (sendResult())
                {
                    dismiss();
                };
            }
        });

        cancelButton = (Button) view.findViewById(R.id.light_property_dimensions_stage_editor_cancel_button);
        cancelButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        String dialogTitle = getArguments().getString(DIALOG_TITLE);
        getDialog().setTitle(dialogTitle);
    }

    public boolean sendResult()
    {
        try
        {
            float[] startVector = new float[]{Integer.parseInt(startWEditText.getText().toString()) / 1000.0f, Integer.parseInt(startHEditText.getText().toString()) / 1000.0f};
            float[] endVector = new float[]{Integer.parseInt(endWEditText.getText().toString()) / 1000.0f, Integer.parseInt(endHEditText.getText().toString()) / 1000.0f};

            int duration = Integer.parseInt(durationEditText.getText().toString());
            if (duration <= 0)
            {
                SnackbarFactory.showSnackbar(getView(), "Duration can't be less than 0!", Snackbar.LENGTH_SHORT);
                //Toast.makeText(getActivity(), "Duration can't be less than 0!", Toast.LENGTH_LONG).show();
                return false;
            }

            Stage.Transition transitionCurve = Stage.Transition.valueOf((String) transitionSpinner.getSelectedItem());

            dimensionsStage.setStartVector(startVector);
            dimensionsStage.setEndVector(endVector);
            dimensionsStage.setDuration(duration);
            dimensionsStage.setTransitionCurve(transitionCurve);
        }
        catch (NumberFormatException e)
        {
            SnackbarFactory.showSnackbar(getView(), "Cannot parse numbers from text fields.", Snackbar.LENGTH_SHORT);
            //Toast.makeText(getActivity(), "Cannot parse numbers from text fields.", Toast.LENGTH_LONG).show();
            return false;
        }

        OnConfirmLightDimensionsStageListener mListener = (OnConfirmLightDimensionsStageListener) getTargetFragment();
        mListener.onConfirmLightDimensionsStage(titleView.getText().toString(), new Stage(dimensionsStage), dimensionsStageIdx);

        return true;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onEditLightDimensions(float[] editedStartDimensions, float[] editedEndDimensions)
    {
        if (!uniformDimensions) { startWEditText.setText(Integer.toString(Math.round(editedStartDimensions[0] * 1000.0f))); }
        startHEditText.setText(Integer.toString(Math.round(editedStartDimensions[1] * 1000.0f)));
        if (!uniformDimensions) { endWEditText.setText(Integer.toString(Math.round(editedEndDimensions[0] * 1000.0f))); }
        endHEditText.setText(Integer.toString(Math.round(editedEndDimensions[1] * 1000.0f)));
    }

    public interface OnConfirmLightDimensionsStageListener
    {
        void invokeLightDimensionsStageDialog(String inputPrompt, Stage defaultDimensionsStage, int dimensionsStageIdx);
        void onConfirmLightDimensionsStage(String inputPrompt, Stage inputDimensionsStage, int dimensionsStageIdx);
    }

}
