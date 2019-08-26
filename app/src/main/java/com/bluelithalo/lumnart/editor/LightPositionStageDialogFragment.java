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
public class LightPositionStageDialogFragment extends DialogFragment implements LightPositionSurfaceView.OnLightPositionEditListener
{
    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String DIALOG_POSITION_STAGE_START = "DIALOG_POSITION_STAGE_START";
    private static final String DIALOG_POSITION_STAGE_END = "DIALOG_POSITION_STAGE_END";
    private static final String DIALOG_POSITION_STAGE_DURATION = "DIALOG_POSITION_STAGE_DURATION";
    private static final String DIALOG_POSITION_STAGE_TRANSITION = "DIALOG_POSITION_STAGE_TRANSITION";
    private static final String DIALOG_POSITION_STAGE_INDEX = "DIALOG_POSITION_STAGE_INDEX";

    private Stage positionStage;
    private TextView titleView;
    private int positionStageIdx;

    private LightPositionSurfaceView touchEditSurfaceView;
    private Switch startEndSwitch;

    private EditText startXEditText;
    private EditText startYEditText;
    private EditText endXEditText;
    private EditText endYEditText;

    private EditText durationEditText;
    private Spinner transitionSpinner;

    private Button confirmButton;
    private Button cancelButton;

    public LightPositionStageDialogFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogTitle the title of the dialog box.
     * @param dialogPositionStage the position stage represented in the dialog box
     * @return A new instance of fragment LightPositionStageDialogFragment.
     */
    public static LightPositionStageDialogFragment newInstance(String dialogTitle, Stage dialogPositionStage, int positionStageIdx)
    {
        LightPositionStageDialogFragment fragment = new LightPositionStageDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putFloatArray(DIALOG_POSITION_STAGE_START, Arrays.copyOf(dialogPositionStage.getStartVector(), dialogPositionStage.getVectorLength()));
        args.putFloatArray(DIALOG_POSITION_STAGE_END, Arrays.copyOf(dialogPositionStage.getEndVector(), dialogPositionStage.getVectorLength()));
        args.putInt(DIALOG_POSITION_STAGE_DURATION, dialogPositionStage.getDuration());
        args.putInt(DIALOG_POSITION_STAGE_TRANSITION, dialogPositionStage.getTransitionCurve().ordinal());
        args.putInt(DIALOG_POSITION_STAGE_INDEX, positionStageIdx);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            positionStage = new Stage(2, 60);
            positionStage.setStartVector(getArguments().getFloatArray(DIALOG_POSITION_STAGE_START));
            positionStage.setEndVector(getArguments().getFloatArray(DIALOG_POSITION_STAGE_END));
            positionStage.setDuration(getArguments().getInt(DIALOG_POSITION_STAGE_DURATION));
            positionStage.setTransitionCurve(Stage.Transition.values()[getArguments().getInt(DIALOG_POSITION_STAGE_TRANSITION)]);
            positionStageIdx = getArguments().getInt(DIALOG_POSITION_STAGE_INDEX);
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
        return inflater.inflate(R.layout.light_property_position_stage_editor_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.light_property_position_stage_editor_title_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        touchEditSurfaceView = (LightPositionSurfaceView) view.findViewById(R.id.light_property_position_stage_editor_surface_view);
        touchEditSurfaceView.setStartPosition(positionStage.getStartVector());
        touchEditSurfaceView.setEndPosition(positionStage.getEndVector());
        touchEditSurfaceView.setOnLightPositionEditListener(this);

        startXEditText = (EditText) view.findViewById(R.id.light_property_position_stage_editor_start_x_edit_text);
        startXEditText.setText(Integer.toString(Math.round(positionStage.getStartVector()[0] * 1000.0f)));
        startXEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                float[] startPosition = touchEditSurfaceView.getStartPosition();

                try
                {
                    startPosition[0] = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    startPosition[0] = 0.0f;
                }

                touchEditSurfaceView.setStartPosition(startPosition);
            }
        });

        startYEditText = (EditText) view.findViewById(R.id.light_property_position_stage_editor_start_y_edit_text);
        startYEditText.setText(Integer.toString(Math.round(positionStage.getStartVector()[1] * 1000.0f)));
        startYEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                float[] startPosition = touchEditSurfaceView.getStartPosition();

                try
                {
                    startPosition[1] = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    startPosition[1] = 0.0f;
                }

                touchEditSurfaceView.setStartPosition(startPosition);
            }
        });

        endXEditText = (EditText) view.findViewById(R.id.light_property_position_stage_editor_end_x_edit_text);
        endXEditText.setText(Integer.toString(Math.round(positionStage.getEndVector()[0] * 1000.0f)));
        endXEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                float[] endPosition = touchEditSurfaceView.getEndPosition();

                try
                {
                    endPosition[0] = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    endPosition[0] = 0.0f;
                }

                touchEditSurfaceView.setEndPosition(endPosition);
            }
        });

        endYEditText = (EditText) view.findViewById(R.id.light_property_position_stage_editor_end_y_edit_text);
        endYEditText.setText(Integer.toString(Math.round(positionStage.getEndVector()[1] * 1000.0f)));
        endYEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                float[] endPosition = touchEditSurfaceView.getEndPosition();

                try
                {
                    endPosition[1] = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    endPosition[1] = 0.0f;
                }

                touchEditSurfaceView.setEndPosition(endPosition);
            }
        });

        durationEditText = (EditText) view.findViewById(R.id.light_property_position_stage_editor_duration_edit_text);
        durationEditText.setText(Integer.toString(positionStage.getDuration()));

        transitionSpinner = (Spinner) view.findViewById(R.id.light_property_position_stage_editor_transition_spinner);
        String[] transitionStrings = new String[Stage.Transition.values().length];
        for (int i = 0; i < transitionStrings.length; i++)
        {
            transitionStrings[i] = Stage.Transition.values()[i].toString();
        }
        ArrayAdapter<String> transitionAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, transitionStrings);
        transitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transitionSpinner.setAdapter(transitionAdapter);
        transitionSpinner.setSelection(positionStage.getTransitionCurve().ordinal());

        confirmButton = (Button) view.findViewById(R.id.light_property_position_stage_editor_confirm_button);
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

        cancelButton = (Button) view.findViewById(R.id.light_property_position_stage_editor_cancel_button);
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
            float[] startVector = new float[]{Integer.parseInt(startXEditText.getText().toString()) / 1000.0f, Integer.parseInt(startYEditText.getText().toString()) / 1000.0f};
            float[] endVector = new float[]{Integer.parseInt(endXEditText.getText().toString()) / 1000.0f, Integer.parseInt(endYEditText.getText().toString()) / 1000.0f};

            int duration = Integer.parseInt(durationEditText.getText().toString());
            if (duration <= 0)
            {
                SnackbarFactory.showSnackbar(getView(), "Duration can't be less than 0!", Snackbar.LENGTH_SHORT);
                //Toast.makeText(getActivity(), "Duration can't be less than 0!", Toast.LENGTH_LONG).show();
                return false;
            }

            Stage.Transition transitionCurve = Stage.Transition.valueOf((String) transitionSpinner.getSelectedItem());

            positionStage.setStartVector(startVector);
            positionStage.setEndVector(endVector);
            positionStage.setDuration(duration);
            positionStage.setTransitionCurve(transitionCurve);
        }
        catch (NumberFormatException e)
        {
            SnackbarFactory.showSnackbar(getView(), "Cannot parse numbers from text fields.", Snackbar.LENGTH_SHORT);
            //Toast.makeText(getActivity(), "Cannot parse numbers from text fields.", Toast.LENGTH_LONG).show();
            return false;
        }

        OnConfirmLightPositionStageListener mListener = (OnConfirmLightPositionStageListener) getTargetFragment();
        mListener.onConfirmLightPositionStage(titleView.getText().toString(), new Stage(positionStage), positionStageIdx);

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
    public void onEditLightPosition(float[] editedStartPosition, float[] editedEndPosition)
    {
        startXEditText.setText(Integer.toString(Math.round(editedStartPosition[0] * 1000.0f)));
        startYEditText.setText(Integer.toString(Math.round(editedStartPosition[1] * 1000.0f)));
        endXEditText.setText(Integer.toString(Math.round(editedEndPosition[0] * 1000.0f)));
        endYEditText.setText(Integer.toString(Math.round(editedEndPosition[1] * 1000.0f)));
    }

    public interface OnConfirmLightPositionStageListener
    {
        void invokeLightPositionStageDialog(String inputPrompt, Stage defaultPositionStage, int positionStageIdx);
        void onConfirmLightPositionStage(String inputPrompt, Stage inputPositionStage, int positionStageIdx);
    }

}
