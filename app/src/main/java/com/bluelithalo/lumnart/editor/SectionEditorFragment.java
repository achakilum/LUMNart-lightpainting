package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.util.AABB;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SectionEditorFragment.OnSectionEditListener} interface
 * to handle interaction events.
 * Use the {@link SectionEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SectionEditorFragment extends Fragment implements  EditTextDialogFragment.OnConfirmEditTextListener,
                                                                AABBDialogFragment.OnConfirmAABBListener,
                                                                SectionLightListAdapter.SectionLightListContainer
{
    private static final String SERIALIZED_SECTION = "serializedSection";
    private static final String SECTION_INDEX = "sectionIndex";
    private static final String SET_SECTION_NAME = "Section name";
    private static final String SET_SECTION_AABB = "Section bounding box values";

    private Section section;
    private int sectionIndex;
    private OnSectionEditListener mListener;

    private TextView nameTextView;
    private ImageButton nameEditButton;
    private TextView aabbTextView;
    private ImageButton aabbEditButton;
    private TextView fittingTextView;
    private Switch fittingSwitch;

    private RecyclerView sectionLightRecyclerView;
    private RecyclerView.Adapter sectionLightRVAdapter;
    private RecyclerView.LayoutManager sectionLightRVLayoutManager;

    private Button lightAddButton;

    public SectionEditorFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param serializedSection the JSON serialization of the section.
     * @return A new instance of fragment SectionEditorFragment.
     */
    public static SectionEditorFragment newInstance(String serializedSection, int sectionIdx)
    {
        SectionEditorFragment fragment = new SectionEditorFragment();
        Bundle args = new Bundle();
        args.putString(SERIALIZED_SECTION, serializedSection);
        args.putInt(SECTION_INDEX, sectionIdx);
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
                String serializedSection = getArguments().getString(SERIALIZED_SECTION);
                section = new Section(serializedSection);
            }
            catch (JSONException e)
            {
                section = new Section();
            }

            sectionIndex = getArguments().getInt(SECTION_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_section_editor, container, false);

        nameTextView = (TextView) view.findViewById(R.id.section_name_text_view);
        nameTextView.setText(section.getName());

        nameEditButton = (ImageButton) view.findViewById(R.id.section_name_edit_button);
        nameEditButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                invokeEditTextDialog(SET_SECTION_NAME, nameTextView.getText().toString());
            }
        });
        nameEditButton.setClickable(true);

        AABB aabb = section.getBoundingBox();
        String aabbText = "[" + Math.round(aabb.getMinimumX() * 1000) + ", " +
                                Math.round(aabb.getMinimumY() * 1000) + "] --> [" +
                                Math.round(aabb.getMaximumX() * 1000) + ", " +
                                Math.round(aabb.getMaximumY() * 1000) + "]";

        aabbTextView = (TextView) view.findViewById(R.id.section_aabb_text_view);
        aabbTextView.setText(aabbText);

        aabbEditButton = (ImageButton) view.findViewById(R.id.section_aabb_edit_button);
        aabbEditButton.setOnClickListener(new TextView.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                invokeAABBDialog(SET_SECTION_AABB, new AABB(section.getBoundingBox()));
            }
        });

        fittingTextView = (TextView) view.findViewById(R.id.section_fitting_text_view);
        fittingTextView.setText((section.hasFitting()) ? R.string.fragment_section_editor_fitting_on : R.string.fragment_section_editor_fitting_off);

        fittingSwitch = (Switch) view.findViewById(R.id.section_fitting_switch);
        fittingSwitch.setChecked(section.hasFitting());
        fittingSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                section.setFitting(isChecked);
                fittingTextView.setText((isChecked) ? R.string.fragment_section_editor_fitting_on : R.string.fragment_section_editor_fitting_off);
                mListener.onSetSectionFitting(isChecked);
            }
        });

        sectionLightRecyclerView = (RecyclerView) view.findViewById(R.id.section_light_recycler_view);
        sectionLightRecyclerView.setHasFixedSize(false);

        sectionLightRVLayoutManager = new GridLayoutManager(this.getContext(), 2);
        sectionLightRecyclerView.setLayoutManager(sectionLightRVLayoutManager);

        sectionLightRVAdapter = new SectionLightListAdapter(section, this);
        sectionLightRecyclerView.setAdapter(sectionLightRVAdapter);

        sectionLightRecyclerView.setNestedScrollingEnabled(false);

        lightAddButton = (Button) view.findViewById(R.id.section_light_add_button);
        lightAddButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                section.insertLight(new Light());
                sectionLightRVAdapter.notifyDataSetChanged();
                mListener.onAddLight();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnSectionEditListener)
        {
            mListener = (OnSectionEditListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnSectionEditListener");
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mListener.onBackFromSection(section.toJSONString(), sectionIndex);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public Section getSection()
    {
        return section;
    }

    @Override
    public void invokeEditTextDialog(String inputPrompt, String defaultText)
    {
        FragmentManager fm = getFragmentManager();
        EditTextDialogFragment editTextDialogFragment = EditTextDialogFragment.newInstance(inputPrompt, defaultText);
        editTextDialogFragment.setTargetFragment(SectionEditorFragment.this, 300);
        editTextDialogFragment.show(getFragmentManager(), "editTextDialogFragment");
    }

    @Override
    public void onConfirmEditText(String inputPrompt, String inputText)
    {
        if (inputPrompt.equals(SET_SECTION_NAME))
        {
            nameTextView.setText(inputText);
            section.setName(inputText);
            mListener.onSetSectionName(inputText);
        }
    }

    @Override
    public void invokeAABBDialog(String inputPrompt, AABB defaultAABB)
    {
        FragmentManager fm = getFragmentManager();
        AABBDialogFragment aabbDialogFragment = AABBDialogFragment.newInstance(inputPrompt, defaultAABB);
        aabbDialogFragment.setTargetFragment(SectionEditorFragment.this, 300);
        aabbDialogFragment.show(getFragmentManager(), "aabbDialogFragment");
    }

    @Override
    public void onConfirmAABB(String inputPrompt, AABB inputAABB)
    {
        if (inputPrompt.equals(SET_SECTION_AABB))
        {
            String inputAABBText = "[" + Math.round(inputAABB.getMinimumX() * 1000) + ", " +
                                         Math.round(inputAABB.getMinimumY() * 1000) + "] --> [" +
                                         Math.round(inputAABB.getMaximumX() * 1000) + ", " +
                                         Math.round(inputAABB.getMaximumY() * 1000) + "]";

            //Log.i("SectionEditorFragment", inputAABBText);

            section.setBoundingBox(inputAABB);
            aabbTextView.setText(inputAABBText);
            mListener.onSetSectionAABB(inputAABB);
        }
        else
        {
            //Log.i("SectionEditorFragment", "Dialog title mismatch");
        }
    }

    @Override
    public void onLightVisibleToggle(int lightNum)
    {
        if (section.isLightHidden(lightNum))
        {
            section.unhideLight(lightNum);
        }
        else
        {
            section.hideLight(lightNum);
        }

        sectionLightRVAdapter.notifyItemChanged(lightNum);
        mListener.onToggleLightVisibility(lightNum);
    }

    @Override
    public void onLightSelected(int lightNum)
    {
        mListener.onSelectLight(lightNum);
    }

    @Override
    public void onLightDelete(int lightNum)
    {
        final int lightIdx = lightNum;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
        alertDialogBuilder.setMessage("Are you sure you want to delete light \"" + section.getLight(lightNum).getName() + "\"?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        section.removeLight(lightIdx);
                        sectionLightRVAdapter.notifyDataSetChanged();
                        mListener.onRemoveLight(lightIdx);
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
    public void onLightDuplicate(int lightNum)
    {
        Light dupeLight = new Light(section.getLight(lightNum));
        dupeLight.setName(dupeLight.getName() + " (D)");

        section.insertLight(lightNum, dupeLight);
        sectionLightRVAdapter.notifyDataSetChanged();
        mListener.onDuplicateLight(lightNum);
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
    public interface OnSectionEditListener
    {
        void onSetSectionName(String newName);
        void onSetSectionFitting(boolean newFitting);
        void onSetSectionAABB(AABB newAABB);

        void onToggleLightVisibility(int lightIdx);
        void onAddLight();
        void onDuplicateLight(int lightIdx);
        void onRemoveLight(int lightIdx);
        void onSelectLight(int lightIdx);

        void onBackFromSection(String serializedSection, int sectionIdx);
    }
}
