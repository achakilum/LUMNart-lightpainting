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
import android.widget.Button;
import android.widget.EditText;
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
public class LightVisibleStageDialogFragment extends DialogFragment
{
    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String DIALOG_VISIBLE_STAGE_VISIBLE = "DIALOG_VISIBLE_STAGE_VISIBLE";
    private static final String DIALOG_VISIBLE_STAGE_DURATION = "DIALOG_VISIBLE_STAGE_DURATION";
    private static final String DIALOG_VISIBLE_STAGE_INDEX = "DIALOG_VISIBLE_STAGE_INDEX";

    private Stage visibleStage;
    private TextView titleView;
    private int visibleStageIdx;

    private Switch visibleSwitch;
    private EditText durationEditText;

    private Button confirmButton;
    private Button cancelButton;

    public LightVisibleStageDialogFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogTitle the title of the dialog box.
     * @param dialogVisibleStage the visibility stage represented in the dialog box
     * @return A new instance of fragment LightVisibleStageDialogFragment.
     */
    public static LightVisibleStageDialogFragment newInstance(String dialogTitle, Stage dialogVisibleStage, int visibleStageIdx)
    {
        LightVisibleStageDialogFragment fragment = new LightVisibleStageDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putFloatArray(DIALOG_VISIBLE_STAGE_VISIBLE, Arrays.copyOf(dialogVisibleStage.getStartVector(), dialogVisibleStage.getVectorLength()));
        args.putInt(DIALOG_VISIBLE_STAGE_DURATION, dialogVisibleStage.getDuration());
        args.putInt(DIALOG_VISIBLE_STAGE_INDEX, visibleStageIdx);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            visibleStage = new Stage(1, 60);
            visibleStage.setStartVector(getArguments().getFloatArray(DIALOG_VISIBLE_STAGE_VISIBLE));
            visibleStage.setEndVector(getArguments().getFloatArray(DIALOG_VISIBLE_STAGE_VISIBLE));
            visibleStage.setDuration(getArguments().getInt(DIALOG_VISIBLE_STAGE_DURATION));
            visibleStage.setTransitionCurve(Stage.Transition.None);
            visibleStageIdx = getArguments().getInt(DIALOG_VISIBLE_STAGE_INDEX);
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
        return inflater.inflate(R.layout.light_property_visible_stage_editor_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.light_property_visible_stage_editor_title_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        visibleSwitch = (Switch) view.findViewById(R.id.light_property_visible_stage_editor_visible_switch);
        visibleSwitch.setChecked((visibleStage.getStartVector()[0] >= 0.5f));

        durationEditText = (EditText) view.findViewById(R.id.light_property_visible_stage_editor_duration_edit_text);
        durationEditText.setText(Integer.toString(visibleStage.getDuration()));

        confirmButton = (Button) view.findViewById(R.id.light_property_visible_stage_editor_confirm_button);
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

        cancelButton = (Button) view.findViewById(R.id.light_property_visible_stage_editor_cancel_button);
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
            boolean visible = visibleSwitch.isChecked();
            int duration = Integer.parseInt(durationEditText.getText().toString());
            if (duration <= 0)
            {
                SnackbarFactory.showSnackbar(getView(), "Duration can't be less than 0!", Snackbar.LENGTH_SHORT);
                //Toast.makeText(getActivity(), "Duration can't be less than 0!", Toast.LENGTH_LONG).show();
                return false;
            }

            visibleStage.setStartVector(new float[]{(visible) ? 1.0f : 0.0f});
            visibleStage.setEndVector(new float[]{(visible) ? 1.0f : 0.0f});
            visibleStage.setDuration(duration);
            visibleStage.setTransitionCurve(Stage.Transition.None);
        }
        catch (NumberFormatException e)
        {
            SnackbarFactory.showSnackbar(getView(), "Cannot parse numbers from text fields.", Snackbar.LENGTH_SHORT);
            //Toast.makeText(getActivity(), "Cannot parse numbers from text fields.", Toast.LENGTH_LONG).show();
            return false;
        }

        OnConfirmLightVisibleStageListener mListener = (OnConfirmLightVisibleStageListener) getTargetFragment();
        mListener.onConfirmLightVisibleStage(titleView.getText().toString(), new Stage(visibleStage), visibleStageIdx);

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

    public interface OnConfirmLightVisibleStageListener
    {
        void invokeLightVisibleStageDialog(String inputPrompt, Stage defaultVisibleStage, int visibleStageIdx);
        void onConfirmLightVisibleStage(String inputPrompt, Stage inputVisibleStage, int visibleStageIdx);
    }

}
