package com.bluelithalo.lumnart;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovePatternDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovePatternDialog extends DialogFragment implements DirectoryListAdapter.DirectoryListContainer
{
    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String PATTERN_FILENAME = "PATTERN_FILENAME";
    private static final String DIRECTORY_FILENAME_LIST = "DIRECTORY_FILENAME_LIST";
    public OnConfirmMovePatternListener mListener;

    private String patternFilename;
    private ArrayList<String> directoryFilenameList;

    private TextView titleView;
    private RecyclerView directoryRecyclerView;
    private RecyclerView.Adapter directoryRVAdapter;
    private RecyclerView.LayoutManager directoryRVLayoutManager;

    public MovePatternDialog()
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
    public static MovePatternDialog newInstance(String dialogTitle, String newPatternFilename, ArrayList<String> newDirectoryFilenameList, OnConfirmMovePatternListener listener)
    {
        MovePatternDialog fragment = new MovePatternDialog();
        fragment.mListener = listener;

        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putString(PATTERN_FILENAME, newPatternFilename);
        args.putStringArrayList(DIRECTORY_FILENAME_LIST, newDirectoryFilenameList);
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
            directoryFilenameList = getArguments().getStringArrayList(DIRECTORY_FILENAME_LIST);
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
        return inflater.inflate(R.layout.fragment_move_pattern_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.move_pattern_dialog_title_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        directoryRecyclerView = (RecyclerView) view.findViewById(R.id.move_pattern_dialog_directory_recycler_view);
        directoryRecyclerView.setHasFixedSize(true);

        directoryRVLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        directoryRecyclerView.setLayoutManager(directoryRVLayoutManager);

        directoryRVAdapter = new DirectoryListAdapter(directoryFilenameList, this);
        directoryRecyclerView.setAdapter(directoryRVAdapter);

        String dialogTitle = getArguments().getString(DIALOG_TITLE);
        getDialog().setTitle(dialogTitle);
    }

    public boolean sendResult(String directoryName)
    {
        mListener.onConfirmMovePatternOption(titleView.getText().toString(), patternFilename, directoryName);
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
    public void onDirectoryImageClick(String directoryFilepath)
    {
        if (sendResult(directoryFilepath))
        {
            dismiss();
        }
    }

    @Override
    public void onDirectoryImageLongClick(String directoryFilepath)
    {
        // do nothing
    }

    public interface OnConfirmMovePatternListener
    {
        void invokeMovePatternDialog(String inputPrompt, String patternFilename);
        void onConfirmMovePatternOption(String inputPrompt, String patternFilename, String directoryName);
    }
}

