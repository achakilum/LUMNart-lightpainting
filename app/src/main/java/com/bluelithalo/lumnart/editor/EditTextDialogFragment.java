package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.util.SnackbarFactory;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditTextDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTextDialogFragment extends DialogFragment implements EditText.OnEditorActionListener
{
    private static final String QWERTY_CHARACTERS = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    private static final String ALPHANUMERIC_CHARACTERS = " QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789";

    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String DIALOG_DEFAULT_TEXT = "DIALOG_DEFAULT_TEXT";

    public OnConfirmEditTextListener mCustomListener;

    private TextView titleView;
    private EditText dialogEditText;
    private Button confirmButton;
    private Button cancelButton;

    public EditTextDialogFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogTitle the title of the dialog box.
     * @param dialogDefaultText the default text displayed in the editable text of the dialog box
     * @return A new instance of fragment EditTextDialogFragment.
     */
    public static EditTextDialogFragment newInstance(String dialogTitle, String dialogDefaultText)
    {
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        fragment.mCustomListener = null;

        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putString(DIALOG_DEFAULT_TEXT, dialogDefaultText);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogTitle the title of the dialog box.
     * @param dialogDefaultText the default text displayed in the editable text of the dialog box
     * @param customListener the custom context that listens onto the result of this fragment
     * @return A new instance of fragment EditTextDialogFragment.
     */
    public static EditTextDialogFragment newInstance(String dialogTitle, String dialogDefaultText, OnConfirmEditTextListener customListener)
    {
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        fragment.mCustomListener = customListener;

        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putString(DIALOG_DEFAULT_TEXT, dialogDefaultText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
        return inflater.inflate(R.layout.fragment_edit_text_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.edit_text_dialog_title_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        dialogEditText = (EditText) view.findViewById(R.id.edit_text_dialog_edit_text);
        dialogEditText.setText(getArguments().getString(DIALOG_DEFAULT_TEXT));
        dialogEditText.setOnEditorActionListener(this);

        confirmButton = (Button) view.findViewById(R.id.edit_text_dialog_confirm_button);
        confirmButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendResult();
            }
        });

        cancelButton = (Button) view.findViewById(R.id.edit_text_dialog_cancel_button);
        cancelButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        String dialogTitle = getArguments().getString(DIALOG_TITLE);
        getDialog().setTitle(dialogTitle);

        dialogEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        if (actionId == EditorInfo.IME_ACTION_DONE)
        {
            return sendResult();
        }

        return false;
    }

    public boolean sendResult()
    {
        String text = dialogEditText.getText().toString();

        if (text.isEmpty())
        {
            SnackbarFactory.showSnackbar(getView(), "Text field cannot be empty.", Snackbar.LENGTH_SHORT);
            //Toast.makeText(getActivity(), "Text field cannot be empty.", Toast.LENGTH_LONG).show();
            return false;
        }

        for (int i = 0; i < text.length(); i++)
        {
            char ch = text.charAt(i);
            if (ALPHANUMERIC_CHARACTERS.indexOf(ch) < 0)
            {
                SnackbarFactory.showSnackbar(getView(), "Sorry! Alphanumeric characters and spaces only.", Snackbar.LENGTH_SHORT);
                return false;
            }
        }

        OnConfirmEditTextListener mListener = (mCustomListener == null) ? (OnConfirmEditTextListener) getTargetFragment() : mCustomListener;
        mListener.onConfirmEditText(titleView.getText().toString(), dialogEditText.getText().toString());
        dismiss();

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

    public interface OnConfirmEditTextListener
    {
        void invokeEditTextDialog(String inputPrompt, String defaultText);
        void onConfirmEditText(String inputPrompt, String inputText);
    }
}
