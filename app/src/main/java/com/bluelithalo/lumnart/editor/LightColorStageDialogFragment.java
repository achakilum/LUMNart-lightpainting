package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
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
public class LightColorStageDialogFragment extends DialogFragment
{
    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String DIALOG_COLOR_STAGE_START = "DIALOG_COLOR_STAGE_START_R";
    private static final String DIALOG_COLOR_STAGE_END = "DIALOG_COLOR_STAGE_END_R";
    private static final String DIALOG_COLOR_STAGE_DURATION = "DIALOG_COLOR_STAGE_DURATION";
    private static final String DIALOG_COLOR_STAGE_TRANSITION = "DIALOG_COLOR_STAGE_TRANSITION";
    private static final String DIALOG_COLOR_STAGE_INDEX = "DIALOG_COLOR_STAGE_INDEX";

    private Stage colorStage;
    private TextView titleView;
    private int colorStageIdx;

    private View startView;
    private View endView;

    private SeekBar startRedSeekBar;
    private SeekBar startGreenSeekBar;
    private SeekBar startBlueSeekBar;
    private SeekBar startAlphaSeekBar;
    private SeekBar endRedSeekBar;
    private SeekBar endGreenSeekBar;
    private SeekBar endBlueSeekBar;
    private SeekBar endAlphaSeekBar;

    private TextView startRedProgressTextView;
    private TextView startGreenProgressTextView;
    private TextView startBlueProgressTextView;
    private TextView startAlphaProgressTextView;
    private TextView endRedProgressTextView;
    private TextView endGreenProgressTextView;
    private TextView endBlueProgressTextView;
    private TextView endAlphaProgressTextView;

    private EditText durationEditText;
    private Spinner transitionSpinner;

    private Button confirmButton;
    private Button cancelButton;

    public LightColorStageDialogFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogTitle the title of the dialog box.
     * @param dialogColorStage the color stage represented in the dialog box
     * @return A new instance of fragment LightColorStageDialogFragment.
     */
    public static LightColorStageDialogFragment newInstance(String dialogTitle, Stage dialogColorStage, int colorStageIdx)
    {
        LightColorStageDialogFragment fragment = new LightColorStageDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putFloatArray(DIALOG_COLOR_STAGE_START, Arrays.copyOf(dialogColorStage.getStartVector(), dialogColorStage.getVectorLength()));
        args.putFloatArray(DIALOG_COLOR_STAGE_END, Arrays.copyOf(dialogColorStage.getEndVector(), dialogColorStage.getVectorLength()));
        args.putInt(DIALOG_COLOR_STAGE_DURATION, dialogColorStage.getDuration());
        args.putInt(DIALOG_COLOR_STAGE_TRANSITION, dialogColorStage.getTransitionCurve().ordinal());
        args.putInt(DIALOG_COLOR_STAGE_INDEX, colorStageIdx);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            colorStage = new Stage(4, 60);
            colorStage.setStartVector(getArguments().getFloatArray(DIALOG_COLOR_STAGE_START));
            colorStage.setEndVector(getArguments().getFloatArray(DIALOG_COLOR_STAGE_END));
            colorStage.setDuration(getArguments().getInt(DIALOG_COLOR_STAGE_DURATION));
            colorStage.setTransitionCurve(Stage.Transition.values()[getArguments().getInt(DIALOG_COLOR_STAGE_TRANSITION)]);
            colorStageIdx = getArguments().getInt(DIALOG_COLOR_STAGE_INDEX);
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
        return inflater.inflate(R.layout.light_property_color_stage_editor_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.light_property_color_stage_editor_title_text_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        startView = (View) view.findViewById(R.id.light_property_color_stage_editor_start_view);
        startView.setBackgroundColor((Math.round(255 * colorStage.getStartVector()[3]) << 24) + (Math.round(255 * colorStage.getStartVector()[0]) << 16) + (Math.round(255 * colorStage.getStartVector()[1]) << 8) + Math.round(255 * colorStage.getStartVector()[2]));
        endView = (View) view.findViewById(R.id.light_property_color_stage_editor_end_view);
        endView.setBackgroundColor((Math.round(255 * colorStage.getEndVector()[3]) << 24) + (Math.round(255 * colorStage.getEndVector()[0]) << 16) + (Math.round(255 * colorStage.getEndVector()[1]) << 8) + Math.round(255 * colorStage.getEndVector()[2]));

        startRedSeekBar = (SeekBar) view.findViewById(R.id.light_property_color_stage_editor_start_r_seek_bar);
        startRedSeekBar.setProgress(Math.round(255 * colorStage.getStartVector()[0]));
        startRedProgressTextView = (TextView) view.findViewById(R.id.light_property_color_stage_editor_start_r_progress_text_view);
        startRedProgressTextView.setText(Integer.toString(startRedSeekBar.getProgress()));
        startRedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                startView.setBackgroundColor((startAlphaSeekBar.getProgress() << 24) + (startRedSeekBar.getProgress() << 16) + (startGreenSeekBar.getProgress() << 8) + startBlueSeekBar.getProgress());
                startRedProgressTextView.setText(Integer.toString(startRedSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        startGreenSeekBar = (SeekBar) view.findViewById(R.id.light_property_color_stage_editor_start_g_seek_bar);
        startGreenSeekBar.setProgress(Math.round(255 * colorStage.getStartVector()[1]));
        startGreenProgressTextView = (TextView) view.findViewById(R.id.light_property_color_stage_editor_start_g_progress_text_view);
        startGreenProgressTextView.setText(Integer.toString(startGreenSeekBar.getProgress()));
        startGreenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                startView.setBackgroundColor((startAlphaSeekBar.getProgress() << 24) + (startRedSeekBar.getProgress() << 16) + (startGreenSeekBar.getProgress() << 8) + startBlueSeekBar.getProgress());
                startGreenProgressTextView.setText(Integer.toString(startGreenSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        startBlueSeekBar = (SeekBar) view.findViewById(R.id.light_property_color_stage_editor_start_b_seek_bar);
        startBlueSeekBar.setProgress(Math.round(255 * colorStage.getStartVector()[2]));
        startBlueProgressTextView = (TextView) view.findViewById(R.id.light_property_color_stage_editor_start_b_progress_text_view);
        startBlueProgressTextView.setText(Integer.toString(startBlueSeekBar.getProgress()));
        startBlueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                startView.setBackgroundColor((startAlphaSeekBar.getProgress() << 24) + (startRedSeekBar.getProgress() << 16) + (startGreenSeekBar.getProgress() << 8) + startBlueSeekBar.getProgress());
                startBlueProgressTextView.setText(Integer.toString(startBlueSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        startAlphaSeekBar = (SeekBar) view.findViewById(R.id.light_property_color_stage_editor_start_a_seek_bar);
        startAlphaSeekBar.setProgress(Math.round(255 * colorStage.getStartVector()[3]));
        startAlphaProgressTextView = (TextView) view.findViewById(R.id.light_property_color_stage_editor_start_a_progress_text_view);
        startAlphaProgressTextView.setText(Integer.toString(startAlphaSeekBar.getProgress()));
        startAlphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                startView.setBackgroundColor((startAlphaSeekBar.getProgress() << 24) + (startRedSeekBar.getProgress() << 16) + (startGreenSeekBar.getProgress() << 8) + startBlueSeekBar.getProgress());
                startAlphaProgressTextView.setText(Integer.toString(startAlphaSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        endRedSeekBar = (SeekBar) view.findViewById(R.id.light_property_color_stage_editor_end_r_seek_bar);
        endRedSeekBar.setProgress(Math.round(255 * colorStage.getEndVector()[0]));
        endRedProgressTextView = (TextView) view.findViewById(R.id.light_property_color_stage_editor_end_r_progress_text_view);
        endRedProgressTextView.setText(Integer.toString(endRedSeekBar.getProgress()));
        endRedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                endView.setBackgroundColor((endAlphaSeekBar.getProgress() << 24) + (endRedSeekBar.getProgress() << 16) + (endGreenSeekBar.getProgress() << 8) + endBlueSeekBar.getProgress());
                endRedProgressTextView.setText(Integer.toString(endRedSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        endGreenSeekBar = (SeekBar) view.findViewById(R.id.light_property_color_stage_editor_end_g_seek_bar);
        endGreenSeekBar.setProgress(Math.round(255 * colorStage.getEndVector()[1]));
        endGreenProgressTextView = (TextView) view.findViewById(R.id.light_property_color_stage_editor_end_g_progress_text_view);
        endGreenProgressTextView.setText(Integer.toString(endGreenSeekBar.getProgress()));
        endGreenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                endView.setBackgroundColor((endAlphaSeekBar.getProgress() << 24) + (endRedSeekBar.getProgress() << 16) + (endGreenSeekBar.getProgress() << 8) + endBlueSeekBar.getProgress());
                endGreenProgressTextView.setText(Integer.toString(endGreenSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        endBlueSeekBar = (SeekBar) view.findViewById(R.id.light_property_color_stage_editor_end_b_seek_bar);
        endBlueSeekBar.setProgress(Math.round(255 * colorStage.getEndVector()[2]));
        endBlueProgressTextView = (TextView) view.findViewById(R.id.light_property_color_stage_editor_end_b_progress_text_view);
        endBlueProgressTextView.setText(Integer.toString(endBlueSeekBar.getProgress()));
        endBlueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                endView.setBackgroundColor((endAlphaSeekBar.getProgress() << 24) + (endRedSeekBar.getProgress() << 16) + (endGreenSeekBar.getProgress() << 8) + endBlueSeekBar.getProgress());
                endBlueProgressTextView.setText(Integer.toString(endBlueSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        endAlphaSeekBar = (SeekBar) view.findViewById(R.id.light_property_color_stage_editor_end_a_seek_bar);
        endAlphaSeekBar.setProgress(Math.round(255 * colorStage.getEndVector()[3]));
        endAlphaProgressTextView = (TextView) view.findViewById(R.id.light_property_color_stage_editor_end_a_progress_text_view);
        endAlphaProgressTextView.setText(Integer.toString(endAlphaSeekBar.getProgress()));
        endAlphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                endView.setBackgroundColor((endAlphaSeekBar.getProgress() << 24) + (endRedSeekBar.getProgress() << 16) + (endGreenSeekBar.getProgress() << 8) + endBlueSeekBar.getProgress());
                endAlphaProgressTextView.setText(Integer.toString(endAlphaSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        durationEditText = (EditText) view.findViewById(R.id.light_property_color_stage_editor_duration_edit_text);
        durationEditText.setText(Integer.toString(colorStage.getDuration()));

        transitionSpinner = (Spinner) view.findViewById(R.id.light_property_color_stage_editor_transition_spinner);
        String[] transitionStrings = new String[Stage.Transition.values().length];
        for (int i = 0; i < transitionStrings.length; i++)
        {
            transitionStrings[i] = Stage.Transition.values()[i].toString();
        }
        ArrayAdapter<String> transitionAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, transitionStrings);
        transitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transitionSpinner.setAdapter(transitionAdapter);
        transitionSpinner.setSelection(colorStage.getTransitionCurve().ordinal());

        confirmButton = (Button) view.findViewById(R.id.light_property_color_stage_editor_confirm_button);
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

        cancelButton = (Button) view.findViewById(R.id.light_property_color_stage_editor_cancel_button);
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
            float[] startVector = new float[]{startRedSeekBar.getProgress() / 255.0f, startGreenSeekBar.getProgress() / 255.0f, startBlueSeekBar.getProgress() / 255.0f, startAlphaSeekBar.getProgress() / 255.0f};
            float[] endVector = new float[]{endRedSeekBar.getProgress() / 255.0f, endGreenSeekBar.getProgress() / 255.0f, endBlueSeekBar.getProgress() / 255.0f, endAlphaSeekBar.getProgress() / 255.0f};

            int duration = Integer.parseInt(durationEditText.getText().toString());
            if (duration <= 0)
            {
                SnackbarFactory.showSnackbar(getView(), "Duration can't be less than 0!", Snackbar.LENGTH_SHORT);
                //Toast.makeText(getActivity(), "Duration can't be less than 0!", Toast.LENGTH_LONG).show();
                return false;
            }

            Stage.Transition transitionCurve = Stage.Transition.valueOf((String) transitionSpinner.getSelectedItem());

            colorStage.setStartVector(startVector);
            colorStage.setEndVector(endVector);
            colorStage.setDuration(duration);
            colorStage.setTransitionCurve(transitionCurve);
        }
        catch (NumberFormatException e)
        {
            SnackbarFactory.showSnackbar(getView(), "Cannot parse numbers from text fields.", Snackbar.LENGTH_SHORT);
            //Toast.makeText(getActivity(), "Cannot parse numbers from text fields.", Toast.LENGTH_LONG).show();
            return false;
        }

        OnConfirmLightColorStageListener mListener = (OnConfirmLightColorStageListener) getTargetFragment();
        mListener.onConfirmLightColorStage(titleView.getText().toString(), new Stage(colorStage), colorStageIdx);

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

    public interface OnConfirmLightColorStageListener
    {
        void invokeLightColorStageDialog(String inputPrompt, Stage defaultColorStage, int colorStageIdx);
        void onConfirmLightColorStage(String inputPrompt, Stage inputColorStage, int colorStageIdx);
    }

}
