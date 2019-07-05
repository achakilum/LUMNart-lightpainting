package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bluelithalo.lumnart.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListItemOptionsDialogFragment.OnListItemOptionSelectedListener} interface
 * to handle interaction events.
 * Use the {@link ListItemOptionsDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListItemOptionsDialogFragment extends DialogFragment
{
    private static final String LIST_ITEM_NAME = "listItemName";
    private static final String LIST_ITEM_POSITION = "listItemPos";
    private static final String LIST_ITEM_CATEGORY = "listItemCategory";

    public enum Option
    {
        EDIT, REMOVE, DUPLICATE, ADD_BEFORE, ADD_AFTER, MOVE_UP, MOVE_DOWN
    }

    private String itemName;
    private int itemPosition;
    private String itemCategory;

    private TextView itemTextView;
    private ListView optionsListView;

    public ListItemOptionsDialogFragment()
    {
        // Required empty public constructor
    }

    public static ListItemOptionsDialogFragment newInstance(String listItemName, int listItemPos, String listItemCategory)
    {
        ListItemOptionsDialogFragment fragment = new ListItemOptionsDialogFragment();
        Bundle args = new Bundle();

        args.putString(LIST_ITEM_NAME, listItemName);
        args.putInt(LIST_ITEM_POSITION, listItemPos);
        args.putString(LIST_ITEM_CATEGORY, listItemCategory);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            itemName = getArguments().getString(LIST_ITEM_NAME);
            itemPosition = getArguments().getInt(LIST_ITEM_POSITION);
            itemCategory = getArguments().getString(LIST_ITEM_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_item_options_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        itemTextView = (TextView) view.findViewById(R.id.dialog_item_text_view);
        String itemTextViewContent = itemCategory.toUpperCase() + " " + (itemPosition + 1) + ": " + itemName;
        itemTextView.setText(itemTextViewContent);

        optionsListView = (ListView) view.findViewById(R.id.dialog_options_list_view);
        ArrayList<String> optionNamesList = new ArrayList<String>();

        optionNamesList.add("Edit \"" + itemName + "\""); // EDIT
        optionNamesList.add("Remove \"" + itemName + "\""); // REMOVE
        optionNamesList.add("Duplicate \"" + itemName + "\""); // DUPLICATE
        optionNamesList.add("Add new " + itemCategory + " before \"" + itemName + "\""); // ADD_BEFORE
        optionNamesList.add("Add new " + itemCategory + " after \"" + itemName + "\""); // ADD_AFTER
        optionNamesList.add("Move \"" + itemName + "\" up"); // MOVE_UP
        optionNamesList.add("Move \"" + itemName + "\" down"); // MOVE_DOWN

        ArrayAdapter<String> layerListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, optionNamesList);
        optionsListView.setAdapter(layerListAdapter);

        ViewGroup.LayoutParams lp = optionsListView.getLayoutParams();
        lp.height = optionsListView.getAdapter().getCount() * optionsListView.getAdapter().getView(0, null, optionsListView).getMinimumHeight();
        optionsListView.setLayoutParams(lp);

        optionsListView.setOnItemClickListener(new ListView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                OnListItemOptionSelectedListener mListener = (OnListItemOptionSelectedListener) getTargetFragment();
                mListener.onListItemOptionSelected(itemPosition, Option.values()[position]);
                dismiss();
            }
        });
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
    public interface OnListItemOptionSelectedListener
    {
        void invokeListItemOptionsDialog(String listItemName, int listItemPos, String listItemCategory);
        void onListItemOptionSelected(int listItemPos, Option option);
    }
}
