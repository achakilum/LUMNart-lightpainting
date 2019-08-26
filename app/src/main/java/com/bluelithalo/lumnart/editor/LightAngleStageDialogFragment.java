package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;

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
public class LightAngleStageDialogFragment extends DialogFragment implements LightAngleSurfaceView.OnLightAngleEditListener
{
    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String DIALOG_ANGLE_STAGE_START = "DIALOG_ANGLE_STAGE_START";
    private static final String DIALOG_ANGLE_STAGE_END = "DIALOG_ANGLE_STAGE_END";
    private static final String DIALOG_ANGLE_STAGE_DURATION = "DIALOG_ANGLE_STAGE_DURATION";
    private static final String DIALOG_ANGLE_STAGE_TRANSITION = "DIALOG_ANGLE_STAGE_TRANSITION";
    private static final String DIALOG_ANGLE_STAGE_INDEX = "DIALOG_ANGLE_STAGE_INDEX";

    private Stage angleStage;
    private TextView titleView;
    private int angleStageIdx;

    private LightAngleSurfaceView startAngleSurfaceView;
    private LightAngleSurfaceView endAngleSurfaceView;

    private EditText startEditText;
    private EditText endEditText;

    private ImageButton startCCWButton;
    private ImageButton startCWButton;
    private ImageButton endCCWButton;
    private ImageButton endCWButton;

    private EditText durationEditText;
    private Spinner transitionSpinner;

    private Button confirmButton;
    private Button cancelButton;

    public LightAngleStageDialogFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogTitle the title of the dialog box.
     * @param dialogAngleStage the angle stage represented in the dialog box
     * @return A new instance of fragment LightAngleStageDialogFragment.
     */
    public static LightAngleStageDialogFragment newInstance(String dialogTitle, Stage dialogAngleStage, int angleStageIdx)
    {
        LightAngleStageDialogFragment fragment = new LightAngleStageDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putFloatArray(DIALOG_ANGLE_STAGE_START, Arrays.copyOf(dialogAngleStage.getStartVector(), dialogAngleStage.getVectorLength()));
        args.putFloatArray(DIALOG_ANGLE_STAGE_END, Arrays.copyOf(dialogAngleStage.getEndVector(), dialogAngleStage.getVectorLength()));
        args.putInt(DIALOG_ANGLE_STAGE_DURATION, dialogAngleStage.getDuration());
        args.putInt(DIALOG_ANGLE_STAGE_TRANSITION, dialogAngleStage.getTransitionCurve().ordinal());
        args.putInt(DIALOG_ANGLE_STAGE_INDEX, angleStageIdx);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            angleStage = new Stage(1, 60);
            angleStage.setStartVector(getArguments().getFloatArray(DIALOG_ANGLE_STAGE_START));
            angleStage.setEndVector(getArguments().getFloatArray(DIALOG_ANGLE_STAGE_END));
            angleStage.setDuration(getArguments().getInt(DIALOG_ANGLE_STAGE_DURATION));
            angleStage.setTransitionCurve(Stage.Transition.values()[getArguments().getInt(DIALOG_ANGLE_STAGE_TRANSITION)]);
            angleStageIdx = getArguments().getInt(DIALOG_ANGLE_STAGE_INDEX);
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
        return inflater.inflate(R.layout.light_property_angle_stage_editor_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.light_property_angle_stage_editor_title_text_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        startAngleSurfaceView = (LightAngleSurfaceView) view.findViewById(R.id.light_property_angle_stage_editor_start_view);
        startAngleSurfaceView.setAngle(angleStage.getStartVector()[0]);
        startAngleSurfaceView.setOnLightDimensionsEditListener(this);

        endAngleSurfaceView = (LightAngleSurfaceView) view.findViewById(R.id.light_property_angle_stage_editor_end_view);
        endAngleSurfaceView.setAngle(angleStage.getEndVector()[0]);
        endAngleSurfaceView.setOnLightDimensionsEditListener(this);
        endAngleSurfaceView.setEditingEnd(true);

        startEditText = (EditText) view.findViewById(R.id.light_property_angle_stage_editor_start_angle_edit_text);
        startEditText.setText(Integer.toString(Math.round(angleStage.getStartVector()[0])));
        startEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                float startAngle = startAngleSurfaceView.getAngle();

                try
                {
                    startAngle = Integer.parseInt(s.toString()) * 1.0f;
                }
                catch (NumberFormatException e)
                {
                    startAngle = 0.0f;
                }

                startAngleSurfaceView.setAngle(startAngle);
            }
        });

        endEditText = (EditText) view.findViewById(R.id.light_property_angle_stage_editor_end_angle_edit_text);
        endEditText.setText(Integer.toString(Math.round(angleStage.getEndVector()[0])));
        endEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                float endAngle = endAngleSurfaceView.getAngle();

                try
                {
                    endAngle = Integer.parseInt(s.toString()) * 1.0f;
                }
                catch (NumberFormatException e)
                {
                    endAngle = 0.0f;
                }

                endAngleSurfaceView.setAngle(endAngle);
            }
        });

        startCCWButton = (ImageButton) view.findViewById(R.id.light_property_angle_stage_editor_start_ccw_button);
        startCCWButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    float startAngle = Float.parseFloat(startEditText.getText().toString());
                    startAngle -= 360.0f;
                    startEditText.setText(Integer.toString(Math.round(startAngle)));
                }
                catch (NumberFormatException e)
                {
                    startEditText.setText("0");
                }
            }
        });


        startCWButton = (ImageButton) view.findViewById(R.id.light_property_angle_stage_editor_start_cw_button);
        startCWButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    float startAngle = Float.parseFloat(startEditText.getText().toString());
                    startAngle += 360.0f;
                    startEditText.setText(Integer.toString(Math.round(startAngle)));
                }
                catch (NumberFormatException e)
                {
                    startEditText.setText("0");
                }
            }
        });

        endCCWButton = (ImageButton) view.findViewById(R.id.light_property_angle_stage_editor_end_ccw_button);
        endCCWButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    float endAngle = Float.parseFloat(endEditText.getText().toString());
                    endAngle -= 360.0f;
                    endEditText.setText(Integer.toString(Math.round(endAngle)));
                }
                catch (NumberFormatException e)
                {
                    endEditText.setText("0");
                }
            }
        });

        endCWButton = (ImageButton) view.findViewById(R.id.light_property_angle_stage_editor_end_cw_button);
        endCWButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    float endAngle = Float.parseFloat(endEditText.getText().toString());
                    endAngle += 360.0f;
                    endEditText.setText(Integer.toString(Math.round(endAngle)));
                }
                catch (NumberFormatException e)
                {
                    endEditText.setText("0");
                }
            }
        });

        durationEditText = (EditText) view.findViewById(R.id.light_property_angle_stage_editor_duration_edit_text);
        durationEditText.setText(Integer.toString(angleStage.getDuration()));

        transitionSpinner = (Spinner) view.findViewById(R.id.light_property_angle_stage_editor_transition_spinner);
        String[] transitionStrings = new String[Stage.Transition.values().length];
        for (int i = 0; i < transitionStrings.length; i++)
        {
            transitionStrings[i] = Stage.Transition.values()[i].toString();
        }
        ArrayAdapter<String> transitionAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, transitionStrings);
        transitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transitionSpinner.setAdapter(transitionAdapter);
        transitionSpinner.setSelection(angleStage.getTransitionCurve().ordinal());

        confirmButton = (Button) view.findViewById(R.id.light_property_angle_stage_editor_confirm_button);
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

        cancelButton = (Button) view.findViewById(R.id.light_property_angle_stage_editor_cancel_button);
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
            float[] startVector = new float[]{Integer.parseInt(startEditText.getText().toString()) * 1.0f};
            float[] endVector = new float[]{Integer.parseInt(endEditText.getText().toString()) * 1.0f};

            int duration = Integer.parseInt(durationEditText.getText().toString());
            if (duration <= 0)
            {
                SnackbarFactory.showSnackbar(getView(), "Duration can't be less than 0!", Snackbar.LENGTH_SHORT);
                //Toast.makeText(getActivity(), "Duration can't be less than 0!", Toast.LENGTH_LONG).show();
                return false;
            }

            Stage.Transition transitionCurve = Stage.Transition.valueOf((String) transitionSpinner.getSelectedItem());

            angleStage.setStartVector(startVector);
            angleStage.setEndVector(endVector);
            angleStage.setDuration(duration);
            angleStage.setTransitionCurve(transitionCurve);
        }
        catch (NumberFormatException e)
        {
            SnackbarFactory.showSnackbar(getView(), "Cannot parse numbers from text fields.", Snackbar.LENGTH_SHORT);
            //Toast.makeText(getActivity(), "Cannot parse numbers from text fields.", Toast.LENGTH_LONG).show();
            return false;
        }

        OnConfirmLightAngleStageListener mListener = (OnConfirmLightAngleStageListener) getTargetFragment();
        mListener.onConfirmLightAngleStage(titleView.getText().toString(), new Stage(angleStage), angleStageIdx);

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
    public void onEditLightAngle(float editedAngle, boolean isEditingEnd)
    {
        if (!isEditingEnd)
        {
            startEditText.setText(Integer.toString(Math.round(editedAngle)));
        }
        else
        {
            endEditText.setText(Integer.toString(Math.round(editedAngle)));
        }
    }

    public interface OnConfirmLightAngleStageListener
    {
        void invokeLightAngleStageDialog(String inputPrompt, Stage defaultAngleStage, int angleStageIdx);
        void onConfirmLightAngleStage(String inputPrompt, Stage inputAngleStage, int angleStageIdx);
    }

}
