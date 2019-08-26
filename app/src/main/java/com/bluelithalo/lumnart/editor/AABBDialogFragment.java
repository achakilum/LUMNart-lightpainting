package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;

import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.util.AABB;
import com.bluelithalo.lumnart.util.SnackbarFactory;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link EditTextDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AABBDialogFragment extends DialogFragment implements AABBSurfaceView.OnAABBEditListener
{
    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String DIALOG_DEFAULT_MIN_X = "DIALOG_DEFAULT_MIN_X";
    private static final String DIALOG_DEFAULT_MAX_X = "DIALOG_DEFAULT_MAX_X";
    private static final String DIALOG_DEFAULT_MIN_Y = "DIALOG_DEFAULT_MIN_Y";
    private static final String DIALOG_DEFAULT_MAX_Y = "DIALOG_DEFAULT_MAX_Y";

    private AABB aabb;
    private AABBSurfaceView aabbSurfaceView;

    private TextView titleView;
    private EditText minXEditText;
    private EditText maxXEditText;
    private EditText minYEditText;
    private EditText maxYEditText;
    private Button confirmButton;
    private Button cancelButton;

    public AABBDialogFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogTitle the title of the dialog box.
     * @param dialogDefaultAABB the default AABB displayed in the editable text fields of the dialog box
     * @return A new instance of fragment EditTextDialogFragment.
     */
    public static AABBDialogFragment newInstance(String dialogTitle, AABB dialogDefaultAABB)
    {
        AABBDialogFragment fragment = new AABBDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putFloat(DIALOG_DEFAULT_MIN_X, dialogDefaultAABB.getMinimumX());
        args.putFloat(DIALOG_DEFAULT_MAX_X, dialogDefaultAABB.getMaximumX());
        args.putFloat(DIALOG_DEFAULT_MIN_Y, dialogDefaultAABB.getMinimumY());
        args.putFloat(DIALOG_DEFAULT_MAX_Y, dialogDefaultAABB.getMaximumY());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            aabb = new AABB();
            aabb.setMinimumX(getArguments().getFloat(DIALOG_DEFAULT_MIN_X));
            aabb.setMaximumX(getArguments().getFloat(DIALOG_DEFAULT_MAX_X));
            aabb.setMinimumY(getArguments().getFloat(DIALOG_DEFAULT_MIN_Y));
            aabb.setMaximumY(getArguments().getFloat(DIALOG_DEFAULT_MAX_Y));
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
        return inflater.inflate(R.layout.fragment_aabb_editor_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.aabb_dialog_title_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        aabbSurfaceView = (AABBSurfaceView) view.findViewById(R.id.aabb_dialog_surface_view);
        aabbSurfaceView.setAABB(aabb);
        aabbSurfaceView.setOnAABBEditListener(this);

        minXEditText = (EditText) view.findViewById(R.id.aabb_dialog_min_x_edit_text);
        minXEditText.setText(Integer.toString(Math.round(aabb.getMinimumX() * 1000)));
        minXEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {

                float minX = -0.5f;

                try
                {
                    minX = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    minX = -0.5f;
                }

                AABB surfaceViewAABB = aabbSurfaceView.getAABB();
                surfaceViewAABB.setMinimumX(minX);
                aabbSurfaceView.setAABB(surfaceViewAABB);
            }
        });

        maxXEditText = (EditText) view.findViewById(R.id.aabb_dialog_max_x_edit_text);
        maxXEditText.setText(Integer.toString(Math.round(aabb.getMaximumX() * 1000)));
        maxXEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }
            @Override
            public void afterTextChanged(Editable s)
            {
                float maxX = 0.5f;

                try
                {
                    maxX = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    maxX = 0.5f;
                }

                AABB surfaceViewAABB = aabbSurfaceView.getAABB();
                surfaceViewAABB.setMaximumX(maxX);
                aabbSurfaceView.setAABB(surfaceViewAABB);
            }
        });

        minYEditText = (EditText) view.findViewById(R.id.aabb_dialog_min_y_edit_text);
        minYEditText.setText(Integer.toString(Math.round(aabb.getMinimumY() * 1000)));
        minYEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }
            @Override
            public void afterTextChanged(Editable s)
            {
                float minY = -0.5f;

                try
                {
                    minY = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    minY = -0.5f;
                }

                AABB surfaceViewAABB = aabbSurfaceView.getAABB();
                surfaceViewAABB.setMinimumY(minY);
                aabbSurfaceView.setAABB(surfaceViewAABB);
            }
        });

        maxYEditText = (EditText) view.findViewById(R.id.aabb_dialog_max_y_edit_text);
        maxYEditText.setText(Integer.toString(Math.round(aabb.getMaximumY() * 1000)));
        maxYEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }
            @Override
            public void afterTextChanged(Editable s)
            {
                float maxY = 0.5f;

                try
                {
                    maxY = Integer.parseInt(s.toString()) / 1000.0f;
                }
                catch (NumberFormatException e)
                {
                    maxY = 0.5f;
                }

                AABB surfaceViewAABB = aabbSurfaceView.getAABB();
                surfaceViewAABB.setMaximumY(maxY);
                aabbSurfaceView.setAABB(surfaceViewAABB);
            }
        });

        confirmButton = (Button) view.findViewById(R.id.aabb_dialog_confirm_button);
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

        cancelButton = (Button) view.findViewById(R.id.aabb_dialog_cancel_button);
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

    @Override
    public void onStart()
    {
        super.onStart();
    }

    public boolean sendResult()
    {
        try
        {
            float minX = Integer.parseInt(minXEditText.getText().toString()) / 1000.0f;
            float maxX = Integer.parseInt(maxXEditText.getText().toString()) / 1000.0f;
            float minY = Integer.parseInt(minYEditText.getText().toString()) / 1000.0f;
            float maxY = Integer.parseInt(maxYEditText.getText().toString()) / 1000.0f;

            aabb.setMinimumX(Math.min(minX, maxX));
            aabb.setMaximumX(Math.max(minX, maxX));
            aabb.setMinimumY(Math.min(minY, maxY));
            aabb.setMaximumY(Math.max(minY, maxY));
        }
        catch (NumberFormatException e)
        {
            SnackbarFactory.showSnackbar(getView(), "Cannot parse numbers from text fields.", Snackbar.LENGTH_SHORT);
            //Toast.makeText(getActivity(), "Cannot parse numbers from text fields.", Toast.LENGTH_LONG).show();
            return false;
        }

        OnConfirmAABBListener mListener = (OnConfirmAABBListener) getTargetFragment();
        mListener.onConfirmAABB(titleView.getText().toString(), new AABB(aabb));

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
    public void onEditAABB(AABB editedAABB)
    {
        minXEditText.setText(Integer.toString(Math.round(editedAABB.getMinimumX() * 1000)));
        maxXEditText.setText(Integer.toString(Math.round(editedAABB.getMaximumX() * 1000)));
        minYEditText.setText(Integer.toString(Math.round(editedAABB.getMinimumY() * 1000)));
        maxYEditText.setText(Integer.toString(Math.round(editedAABB.getMaximumY() * 1000)));
    }

    public interface OnConfirmAABBListener
    {
        void invokeAABBDialog(String inputPrompt, AABB defaultAABB);
        void onConfirmAABB(String inputPrompt, AABB inputAABB);
    }
}
