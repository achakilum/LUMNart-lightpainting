package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Section;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LayerEditorFragment.OnLayerEditListener} interface
 * to handle interaction events.
 * Use the {@link LayerEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LayerEditorFragment extends Fragment implements    EditTextDialogFragment.OnConfirmEditTextListener,
                                                                LayerSectionListAdapter.LayerSectionListContainer
{
    private static final String SERIALIZED_LAYER = "serializedLayer";
    private static final String LAYER_INDEX = "layerIndex";
    private static final String SET_LAYER_NAME = "Layer name";

    private Layer layer;
    private int layerIndex;
    private OnLayerEditListener mListener;

    private TextView nameTextView;
    private ImageButton nameEditButton;
    private SeekBar alphaSeekBar;

    private RecyclerView layerSectionRecyclerView;
    private RecyclerView.Adapter layerSectionRVAdapter;
    private RecyclerView.LayoutManager layerSectionRVLayoutManager;

    private Button sectionAddButton;

    public LayerEditorFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param serializedLayer the JSON serialization of the layer.
     * @return A new instance of fragment LayerEditorFragment.
     */
    public static LayerEditorFragment newInstance(String serializedLayer, int layerIdx)
    {
        LayerEditorFragment fragment = new LayerEditorFragment();
        Bundle args = new Bundle();
        args.putString(SERIALIZED_LAYER, serializedLayer);
        args.putInt(LAYER_INDEX, layerIdx);
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
                String serializedLayer = getArguments().getString(SERIALIZED_LAYER);
                layer = new Layer(serializedLayer);
            }
            catch (JSONException e)
            {
                layer = new Layer();
            }

            layerIndex = getArguments().getInt(LAYER_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_layer_editor, container, false);

        nameTextView = (TextView) view.findViewById(R.id.layer_name_text_view);
        nameTextView.setText(layer.getName());

        nameEditButton = (ImageButton) view.findViewById(R.id.layer_name_edit_button);
        nameEditButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                invokeEditTextDialog(SET_LAYER_NAME, nameTextView.getText().toString());
            }
        });
        nameEditButton.setClickable(true);

        alphaSeekBar = (SeekBar) view.findViewById(R.id.layer_alpha_seek_bar);
        alphaSeekBar.setMax(100);
        alphaSeekBar.setProgress((int) (layer.getAlpha() * 100));
        alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                float newAlpha = progress * 0.01f;
                layer.setAlpha(newAlpha);
                mListener.onSetLayerAlpha(newAlpha);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        layerSectionRecyclerView = (RecyclerView) view.findViewById(R.id.layer_section_recycler_view);
        layerSectionRecyclerView.setHasFixedSize(false);

        layerSectionRVLayoutManager = new GridLayoutManager(this.getContext(), 2);
        layerSectionRecyclerView.setLayoutManager(layerSectionRVLayoutManager);

        layerSectionRVAdapter = new LayerSectionListAdapter(layer, this);
        layerSectionRecyclerView.setAdapter(layerSectionRVAdapter);

        layerSectionRecyclerView.setNestedScrollingEnabled(false);

        sectionAddButton = (Button) view.findViewById(R.id.layer_section_add_button);
        sectionAddButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                layer.insertSection(new Section());
                layerSectionRVAdapter.notifyDataSetChanged();
                mListener.onAddSection();
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnLayerEditListener)
        {
            mListener = (OnLayerEditListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnLayerEditListener");
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mListener.onBackFromLayer(layer.toJSONString(), layerIndex);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public Layer getLayer()
    {
        return layer;
    }

    public void invokeEditTextDialog(String inputPrompt, String defaultText)
    {
        FragmentManager fm = getFragmentManager();
        EditTextDialogFragment editTextDialogFragment = EditTextDialogFragment.newInstance(inputPrompt, defaultText);
        editTextDialogFragment.setTargetFragment(LayerEditorFragment.this, 300);
        editTextDialogFragment.show(getFragmentManager(), "editTextDialogFragment");
    }

    @Override
    public void onConfirmEditText(String inputPrompt, String inputText)
    {
        if (inputPrompt.equals(SET_LAYER_NAME))
        {
            nameTextView.setText(inputText);
            layer.setName(inputText);
            mListener.onSetLayerName(inputText);
        }
    }

    @Override
    public void onSectionVisibleToggle(int sectionNum)
    {
        if (layer.isSectionHidden(sectionNum))
        {
            layer.unhideSection(sectionNum, true);
        }
        else
        {
            layer.hideSection(sectionNum, true);
        }

        layerSectionRVAdapter.notifyItemChanged(sectionNum);
        mListener.onToggleSectionVisibility(sectionNum);
    }

    @Override
    public void onSectionSelected(int sectionNum)
    {
        mListener.onSelectSection(sectionNum);
    }

    @Override
    public void onSectionDelete(int sectionNum)
    {
        final int sectionIdx = sectionNum;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
        alertDialogBuilder.setMessage("Are you sure you want to delete section \"" + layer.getSection(sectionNum).getName() + "\"?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        layer.removeSection(sectionIdx);
                        layerSectionRVAdapter.notifyDataSetChanged();
                        mListener.onRemoveSection(sectionIdx);
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
    public void onSectionDuplicate(int sectionNum)
    {
        Section dupeSection = new Section(layer.getSection(sectionNum));
        dupeSection.setName(dupeSection.getName() + " (D)");

        layer.insertSection(sectionNum, dupeSection);
        layerSectionRVAdapter.notifyDataSetChanged();
        mListener.onDuplicateSection(sectionNum);
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
    public interface OnLayerEditListener
    {
        void onSetLayerName(String newName);
        void onSetLayerAlpha(float newAlpha);

        void onToggleSectionVisibility(int sectionIdx);
        void onAddSection();
        void onDuplicateSection(int sectionIdx);
        void onRemoveSection(int sectionIdx);
        void onSelectSection(int sectionIdx);

        void onBackFromLayer(String serializedLayer, int layerIdx);
    }
}
