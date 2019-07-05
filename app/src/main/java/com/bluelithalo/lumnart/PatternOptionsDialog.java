package com.bluelithalo.lumnart;

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
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatternOptionsDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatternOptionsDialog extends DialogFragment
{
    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String PATTERN_FILENAME = "PATTERN_FILENAME";
    public static final int MOVE_PATTERN = 0;
    public static final int DUPLICATE_PATTERN = 1;
    public static final int DELETE_PATTERN = 2;
    public OnConfirmPatternOptionListener mListener;

    private String patternFilename;

    private TextView titleView;
    private Button moveButton;
    private Button duplicateButton;
    private Button deleteButton;

    public PatternOptionsDialog()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogTitle the title of the dialog box.
     * @return A new instance of fragment LightVisibleStageDialogFragment.
     */
    public static PatternOptionsDialog newInstance(String dialogTitle, String newPatternFilename, OnConfirmPatternOptionListener listener)
    {
        PatternOptionsDialog fragment = new PatternOptionsDialog();
        fragment.mListener = listener;

        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putString(PATTERN_FILENAME, newPatternFilename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            patternFilename = getArguments().getString(PATTERN_FILENAME);
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
        return inflater.inflate(R.layout.fragment_pattern_options_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.pattern_options_dialog_title_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        moveButton = (Button) view.findViewById(R.id.pattern_options_dialog_move_button);
        moveButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (sendResult(MOVE_PATTERN))
                {
                    dismiss();
                }
            }
        });

        duplicateButton = (Button) view.findViewById(R.id.pattern_options_dialog_duplicate_button);
        duplicateButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (sendResult(DUPLICATE_PATTERN))
                {
                    dismiss();
                }
            }
        });

        deleteButton = (Button) view.findViewById(R.id.pattern_options_dialog_delete_button);
        deleteButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (sendResult(DELETE_PATTERN))
                {
                    dismiss();
                }
            }
        });

        String dialogTitle = getArguments().getString(DIALOG_TITLE);
        getDialog().setTitle(dialogTitle);
    }

    public boolean sendResult(int result)
    {
        mListener.onConfirmPatternOption(titleView.getText().toString(), patternFilename, result);
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

    public interface OnConfirmPatternOptionListener
    {
        void invokePatternOptionsDialog(String inputPrompt, String patternFilename);
        void onConfirmPatternOption(String inputPrompt, String patternFilename, int patternOption);
    }
}

