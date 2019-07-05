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
 * Use the {@link DirectoryOptionsDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectoryOptionsDialog extends DialogFragment
{
    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    public static final int RENAME_DIRECTORY = 0;
    public static final int DELETE_DIRECTORY = 1;
    public OnConfirmDirectoryOptionListener mListener;

    private TextView titleView;
    private Button renameButton;
    private Button deleteButton;

    public DirectoryOptionsDialog()
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
    public static DirectoryOptionsDialog newInstance(String dialogTitle, OnConfirmDirectoryOptionListener listener)
    {
        DirectoryOptionsDialog fragment = new DirectoryOptionsDialog();
        fragment.mListener = listener;

        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {

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
        return inflater.inflate(R.layout.fragment_directory_options_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.directory_options_dialog_title_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        renameButton = (Button) view.findViewById(R.id.directory_options_dialog_rename_button);
        renameButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (sendResult(RENAME_DIRECTORY))
                {
                    dismiss();
                }
            }
        });

        deleteButton = (Button) view.findViewById(R.id.directory_options_dialog_delete_button);
        deleteButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (sendResult(DELETE_DIRECTORY))
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
        mListener.onConfirmDirectoryOption(titleView.getText().toString(), result);
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

    public interface OnConfirmDirectoryOptionListener
    {
        void invokeDirectoryOptionsDialog(String inputPrompt);
        void onConfirmDirectoryOption(String inputPrompt, int directoryOption);
    }

}

