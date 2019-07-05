package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.util.ItemTouchHelperAdapter;
import com.bluelithalo.lumnart.util.SimpleItemTouchHelperCallback;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PatternEditorFragment.OnPatternEditListener} interface
 * to handle interaction events.
 * Use the {@link PatternEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatternEditorFragment extends Fragment implements  EditTextDialogFragment.OnConfirmEditTextListener,
                                                                PatternColorCodeDialogFragment.OnConfirmPatternColorCodeListener,
                                                                PatternLayerListAdapter.PatternLayerListContainer
{
    private static final String SERIALIZED_PATTERN = "serializedPattern";
    private static final String SET_PATTERN_NAME = "Pattern name";
    private static final String SET_PATTERN_AUTHOR = "Pattern author name";
    private static final String SET_PATTERN_DESCRIPTION = "Pattern description";
    private static final String SET_PATTERN_COLOR_CODE = "Pattern color code";

    private Pattern pattern;
    private OnPatternEditListener mListener;

    private Button saveButton;
    private TextView nameTextView;
    private ImageButton nameEditButton;
    private TextView authorTextView;
    private ImageButton authorEditButton;
    private TextView descriptionTextView;
    private ImageButton descriptionEditButton;
    private Button colorCodeEditButton;

    private RecyclerView patternLayerRecyclerView;
    private RecyclerView.Adapter patternLayerRVAdapter;
    private RecyclerView.LayoutManager patternLayerRVLayoutManager;

    private Button layerAddButton;

    public PatternEditorFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param serializedPattern the JSON serialization of the pattern.
     * @return A new instance of fragment PatternEditorFragment.
     */
    public static PatternEditorFragment newInstance(String serializedPattern)
    {
        PatternEditorFragment fragment = new PatternEditorFragment();
        Bundle args = new Bundle();
        args.putString(SERIALIZED_PATTERN, serializedPattern);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            try
            {
                String serializedPattern = getArguments().getString(SERIALIZED_PATTERN);
                pattern = new Pattern(serializedPattern);
            }
            catch (JSONException e)
            {
                pattern = new Pattern();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pattern_editor, container, false);

        saveButton = (Button) view.findViewById(R.id.pattern_save_button);
        saveButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onSavePattern();
            }
        });

        nameTextView = (TextView) view.findViewById(R.id.pattern_editor_name_text_view);
        nameTextView.setText(pattern.getName());

        nameEditButton = (ImageButton) view.findViewById(R.id.pattern_name_edit_button);
        nameEditButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                invokeEditTextDialog(SET_PATTERN_NAME, nameTextView.getText().toString());
            }
        });
        nameEditButton.setClickable(true);

        authorTextView = (TextView) view.findViewById(R.id.pattern_editor_author_text_view);
        authorTextView.setText(pattern.getAuthor());

        authorEditButton = (ImageButton) view.findViewById(R.id.pattern_author_edit_button);
        authorEditButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                invokeEditTextDialog(SET_PATTERN_AUTHOR, authorTextView.getText().toString());
            }
        });
        authorEditButton.setClickable(true);

        descriptionTextView = (TextView) view.findViewById(R.id.pattern_editor_description_text_view);
        descriptionTextView.setText(pattern.getDescription());

        descriptionEditButton = (ImageButton) view.findViewById(R.id.pattern_description_edit_button);
        descriptionEditButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                invokeEditTextDialog(SET_PATTERN_DESCRIPTION, descriptionTextView.getText().toString());
            }
        });
        descriptionEditButton.setClickable(true);

        colorCodeEditButton = (Button) view.findViewById(R.id.pattern_color_code_button);
        colorCodeEditButton.setBackgroundTintList(ColorStateList.valueOf(pattern.getColorCode()));
        colorCodeEditButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                invokePatternColorCodeDialog(SET_PATTERN_COLOR_CODE, pattern.getColorCode());
            }
        });
        patternLayerRecyclerView = (RecyclerView) view.findViewById(R.id.pattern_layer_recycler_view);
        patternLayerRecyclerView.setHasFixedSize(false);

        patternLayerRVLayoutManager = new LinearLayoutManager(this.getContext());
        patternLayerRecyclerView.setLayoutManager(patternLayerRVLayoutManager);

        patternLayerRVAdapter = new PatternLayerListAdapter(pattern, this);
        patternLayerRecyclerView.setAdapter(patternLayerRVAdapter);

        patternLayerRecyclerView.setNestedScrollingEnabled(false);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) patternLayerRVAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(patternLayerRecyclerView);

        layerAddButton = (Button) view.findViewById(R.id.pattern_layer_add_button);
        layerAddButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pattern.insertLayer(new Layer());
                patternLayerRVAdapter.notifyDataSetChanged();
                mListener.onAddLayer();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnPatternEditListener)
        {
            mListener = (OnPatternEditListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnPatternEditListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public void invokeEditTextDialog(String inputPrompt, String defaultText)
    {
        FragmentManager fm = getFragmentManager();
        EditTextDialogFragment editTextDialogFragment = EditTextDialogFragment.newInstance(inputPrompt, defaultText);
        editTextDialogFragment.setTargetFragment(PatternEditorFragment.this, 300);
        editTextDialogFragment.show(getFragmentManager(), "editTextDialogFragment");
    }

    @Override
    public void onConfirmEditText(String inputPrompt, String inputText)
    {
        if (inputPrompt.equals(SET_PATTERN_NAME))
        {
            nameTextView.setText(inputText);
            pattern.setName(inputText);
            mListener.onSetPatternName(inputText);
        }
        else
        if (inputPrompt.equals(SET_PATTERN_AUTHOR))
        {
            authorTextView.setText(inputText);
            pattern.setAuthor(inputText);
            mListener.onSetPatternAuthor(inputText);
        }
        else
        if (inputPrompt.equals(SET_PATTERN_DESCRIPTION))
        {
            descriptionTextView.setText(inputText);
            pattern.setDescription(inputText);
            mListener.onSetPatternDescription(inputText);
        }
    }

    @Override
    public void invokePatternColorCodeDialog(String inputPrompt, int defaultColorCode)
    {
        FragmentManager fm = getFragmentManager();
        PatternColorCodeDialogFragment patternColorCodeDialogFragment = PatternColorCodeDialogFragment.newInstance(inputPrompt, defaultColorCode);
        patternColorCodeDialogFragment.setTargetFragment(PatternEditorFragment.this, 300);
        patternColorCodeDialogFragment.show(getFragmentManager(), "patternColorCodeDialogFragment");
    }

    @Override
    public void onConfirmPatternColorCode(String inputPrompt, int inputColorCode)
    {
        if (inputPrompt.equals(SET_PATTERN_COLOR_CODE))
        {
            colorCodeEditButton.setBackgroundTintList(ColorStateList.valueOf(inputColorCode));
            pattern.setColorCode(inputColorCode);
            mListener.onSetPatternColorCode(inputColorCode);
        }
    }

    public Pattern getPattern()
    {
        return pattern;
    }

    @Override
    public void onLayerVisibleToggle(int layerNum)
    {
        if (pattern.isLayerHidden(layerNum))
        {
            pattern.unhideLayer(layerNum, true);
        }
        else
        {
            pattern.hideLayer(layerNum, true);
        }

        patternLayerRVAdapter.notifyItemChanged(layerNum);
        mListener.onToggleLayerVisibility(layerNum);
    }

    @Override
    public void onLayerSelected(int layerNum)
    {
        mListener.onSelectLayer(layerNum);
    }

    @Override
    public void onLayerDelete(int layerNum)
    {
        final int layerIdx = layerNum;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
        alertDialogBuilder.setMessage("Are you sure you want to delete layer \"" + pattern.getLayer(layerNum).getName() + "\"?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        pattern.removeLayer(layerIdx);
                        patternLayerRVAdapter.notifyDataSetChanged();
                        mListener.onRemoveLayer(layerIdx);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // do nothing
                    }
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onLayerDuplicate(int layerNum)
    {
        Layer dupeLayer = new Layer(pattern.getLayer(layerNum));
        dupeLayer.setName(dupeLayer.getName() + " (D)");

        pattern.insertLayer(layerNum, dupeLayer);
        patternLayerRVAdapter.notifyDataSetChanged();
        mListener.onDuplicateLayer(layerNum);
    }

    @Override
    public void onLayerMove(int fromLayerNum, int toLayerNum)
    {
        if (fromLayerNum < toLayerNum)
        {
            for (int i = fromLayerNum; i < toLayerNum; i++)
            {
                Layer layerRef = pattern.getLayer(i);
                boolean layerRefHidden = pattern.isLayerHidden(i);

                pattern.setLayer(pattern.getLayer(i+1), i);
                pattern.setLayer(layerRef, i+1);

                if (pattern.isLayerHidden(i+1)) { pattern.hideLayer(i, false); } else { pattern.unhideLayer(i, false); }
                if (layerRefHidden) { pattern.hideLayer(i+1, false); } else { pattern.unhideLayer(i+1, false); }
            }
        }
        else
        {
            for (int i = fromLayerNum; i > toLayerNum; i--)
            {
                Layer layerRef = pattern.getLayer(i);
                boolean layerRefHidden = pattern.isLayerHidden(i);

                pattern.setLayer(pattern.getLayer(i-1), i);
                pattern.setLayer(layerRef, i-1);

                if (pattern.isLayerHidden(i-1)) { pattern.hideLayer(i, false); } else { pattern.unhideLayer(i, false); }
                if (layerRefHidden) { pattern.hideLayer(i-1, false); } else { pattern.unhideLayer(i-1, false); }
            }
        }

        patternLayerRVAdapter.notifyItemMoved(fromLayerNum, toLayerNum);
        mListener.onMoveLayer(fromLayerNum, toLayerNum);
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPatternEditListener
    {
        void onSavePattern();

        void onSetPatternName(String newName);
        void onSetPatternAuthor(String newAuthor);
        void onSetPatternDescription(String newDescription);
        void onSetPatternColorCode(int newColorCode);

        void onToggleLayerVisibility(int layerIdx);
        void onAddLayer();
        void onDuplicateLayer(int layerIdx);
        void onMoveLayer(int fromLayerIdx, int toLayerIdx);
        void onRemoveLayer(int layerIdx);
        void onSelectLayer(int layerIdx);
    }
}
