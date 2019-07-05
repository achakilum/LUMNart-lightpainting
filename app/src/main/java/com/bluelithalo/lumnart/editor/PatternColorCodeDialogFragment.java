package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bluelithalo.lumnart.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditTextDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatternColorCodeDialogFragment extends DialogFragment
{
    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String DIALOG_COLOR_CODE = "DIALOG_COLOR_CODE";

    private int colorCode;

    private TextView titleView;
    private View colorCodeView;
    private SeekBar hueSeekBar;
    private SeekBar saturationSeekBar;

    private Button confirmButton;
    private Button cancelButton;

    public PatternColorCodeDialogFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogTitle the title of the dialog box.
     * @param dialogColorCode the color code represented in the dialog box
     * @return A new instance of fragment LightColorStageDialogFragment.
     */
    public static PatternColorCodeDialogFragment newInstance(String dialogTitle, int dialogColorCode)
    {
        PatternColorCodeDialogFragment fragment = new PatternColorCodeDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putInt(DIALOG_COLOR_CODE, dialogColorCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            colorCode = getArguments().getInt(DIALOG_COLOR_CODE);
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
        return inflater.inflate(R.layout.fragment_pattern_color_code_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.pattern_color_code_title_text_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        colorCodeView = (View) view.findViewById(R.id.pattern_color_code_view);
        colorCodeView.setBackgroundColor(colorCode);

        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(colorCode), Color.green(colorCode), Color.blue(colorCode), hsv);

        hueSeekBar = (SeekBar) view.findViewById(R.id.pattern_color_code_hue_seek_bar);
        hueSeekBar.setProgress(Math.round(hsv[0]));
        saturationSeekBar = (SeekBar) view.findViewById(R.id.pattern_color_code_saturation_seek_bar);
        saturationSeekBar.setProgress(Math.round(hsv[1] * 100));

        hueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                float[] hsv = new float[]{progress * 1.0f, saturationSeekBar.getProgress() / 100.0f, 0.25f};
                colorCode = Color.HSVToColor(hsv);
                colorCodeView.setBackgroundColor(colorCode);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        saturationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                float[] hsv = new float[]{hueSeekBar.getProgress() * 1.0f, progress / 100.0f, 0.25f};
                colorCode = Color.HSVToColor(hsv);
                colorCodeView.setBackgroundColor(colorCode);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });


        confirmButton = (Button) view.findViewById(R.id.pattern_color_code_confirm_button);
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

        cancelButton = (Button) view.findViewById(R.id.pattern_color_code_cancel_button);
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
        float[] hsv = new float[]{hueSeekBar.getProgress() * 1.0f, saturationSeekBar.getProgress() / 100.0f, 0.25f};
        colorCode = Color.HSVToColor(hsv);

        OnConfirmPatternColorCodeListener mListener = (OnConfirmPatternColorCodeListener) getTargetFragment();
        mListener.onConfirmPatternColorCode(titleView.getText().toString(), colorCode);

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

    public interface OnConfirmPatternColorCodeListener
    {
        void invokePatternColorCodeDialog(String inputPrompt, int defaultColorCode);
        void onConfirmPatternColorCode(String inputPrompt, int inputColorCode);
    }

}
