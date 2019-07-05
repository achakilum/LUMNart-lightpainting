package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Figure;
import com.bluelithalo.lumnart.pattern.TextFigure;
import com.bluelithalo.lumnart.util.SnackbarFactory;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditTextDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LightFigureDialogFragment extends DialogFragment
{
    private static final String QWERTY_CHARACTERS = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    private static final String ALPHANUMERIC_CHARACTERS = " QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789";

    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String DIALOG_DEFAULT_FIGURE = "DIALOG_DEFAULT_FIGURE";

    private ArrayList<String> shapeIdList;
    private HashMap<String, Integer> shapeIdToIconId;
    private HashMap<String, Integer> shapeIdToStringId;
    private ArrayList<String> textFontList;

    private Figure figure;
    private TextFigure textFigure;
    private Figure.Type selectedFigure;

    private TextView titleView;
    private ImageView shapePreview;
    private Spinner shapeSpinner;

    private Spinner textFontSpinner;
    private ImageButton leftAlignmentButton;
    private ImageButton centerAlignmentButton;
    private ImageButton rightAlignmentButton;
    private EditText textEditText;

    private EditText pivotXEditText;
    private EditText pivotYEditText;
    private CheckBox defaultPivotCheckBox;
    private ImageView selectedFigureIcon;
    private TextView selectedFigureText;

    private TextView shapeLabel;
    private TextView textLabel;
    private RadioButton shapeRadioButton;
    private RadioButton textRadioButton;

    private Button confirmButton;
    private Button cancelButton;

    public LightFigureDialogFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogTitle the title of the dialog box.
     * @param dialogDefaultFigure the default figure displayed in the respective components of the dialog box
     * @return A new instance of fragment LightFigureDialogFragment.
     */
    public static LightFigureDialogFragment newInstance(String dialogTitle, Figure dialogDefaultFigure)
    {
        LightFigureDialogFragment fragment = new LightFigureDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, dialogTitle);
        args.putString(DIALOG_DEFAULT_FIGURE, dialogDefaultFigure.toJSONString());
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
                JSONObject figureObj = new JSONObject(getArguments().getString(DIALOG_DEFAULT_FIGURE));
                Figure.Type figureType = Figure.Type.values()[figureObj.getInt("type")];

                switch(figureType)
                {
                    case SHAPE:
                        figure = new Figure(figureObj);
                        textFigure = new TextFigure();
                        selectedFigure = Figure.Type.SHAPE;
                        break;
                    case TEXT:
                        figure = new Figure();
                        textFigure = new TextFigure(figureObj);
                        selectedFigure = Figure.Type.TEXT;
                        break;
                }
            }
            catch (JSONException e)
            {
                dismiss();
            }
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
        return inflater.inflate(R.layout.light_figure_editor_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = (TextView) view.findViewById(R.id.light_figure_editor_dialog_title_view);
        titleView.setText(getArguments().getString(DIALOG_TITLE));

        shapePreview = (ImageView) view.findViewById(R.id.light_figure_editor_dialog_shape_preview);
        shapeSpinner = (Spinner) view.findViewById(R.id.light_figure_editor_dialog_shape_spinner);
        configureShapeSpinner();

        textFontSpinner = (Spinner) view.findViewById(R.id.light_figure_editor_dialog_text_font_spinner);
        textEditText = (EditText) view.findViewById(R.id.light_figure_editor_dialog_text_edit_text);
        textEditText.setText(textFigure.getText());
        switch (textFigure.getAlignment())
        {
            case LEFT:
                textEditText.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                break;
            case CENTER:
                textEditText.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                break;
            case RIGHT:
                textEditText.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                break;
        }
        textEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (selectedFigure == Figure.Type.TEXT)
                {
                    selectedFigureText.setText(s);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        leftAlignmentButton = (ImageButton) view.findViewById(R.id.light_figure_editor_dialog_text_alignment_left_button);
        leftAlignmentButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                textFigure.setAlignment(TextFigure.Alignment.LEFT);
                textEditText.setGravity(Gravity.LEFT | Gravity.BOTTOM);
            }
        });
        centerAlignmentButton = (ImageButton) view.findViewById(R.id.light_figure_editor_dialog_text_alignment_center_button);
        centerAlignmentButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                textFigure.setAlignment(TextFigure.Alignment.CENTER);
                textEditText.setGravity(Gravity.CENTER | Gravity.BOTTOM);
            }
        });
        rightAlignmentButton = (ImageButton) view.findViewById(R.id.light_figure_editor_dialog_text_alignment_right_button);
        rightAlignmentButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                textFigure.setAlignment(TextFigure.Alignment.RIGHT);
                textEditText.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
            }
        });

        configureTextFontSpinner();

        pivotXEditText = (EditText) view.findViewById(R.id.light_figure_editor_dialog_pivot_x_edit_text);
        pivotYEditText = (EditText) view.findViewById(R.id.light_figure_editor_dialog_pivot_y_edit_text);
        switch(selectedFigure)
        {
            case SHAPE:
                pivotXEditText.setText(Float.toString(figure.getPivot()[0]));
                pivotYEditText.setText(Float.toString(figure.getPivot()[1]));
                break;
            case TEXT:
                pivotXEditText.setText(Float.toString(textFigure.getPivot()[0]));
                pivotYEditText.setText(Float.toString(textFigure.getPivot()[1]));
                break;
        }

        defaultPivotCheckBox = (CheckBox) view.findViewById(R.id.light_figure_editor_dialog_default_pivot_check_box);
        switch(selectedFigure)
        {
            case SHAPE:
                defaultPivotCheckBox.setChecked(figure.isUsingDefaultPivot());
                pivotXEditText.setEnabled(!figure.isUsingDefaultPivot());
                pivotYEditText.setEnabled(!figure.isUsingDefaultPivot());
                break;
            case TEXT:
                defaultPivotCheckBox.setChecked(textFigure.isUsingDefaultPivot());
                pivotXEditText.setEnabled(!textFigure.isUsingDefaultPivot());
                pivotYEditText.setEnabled(!textFigure.isUsingDefaultPivot());
                break;
        }

        defaultPivotCheckBox.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                figure.setIsUsingDefaultPivot(isChecked);
                textFigure.setIsUsingDefaultPivot(isChecked);
                pivotXEditText.setEnabled(!isChecked);
                pivotYEditText.setEnabled(!isChecked);
            }
        });

        selectedFigureIcon = (ImageView) view.findViewById(R.id.light_figure_editor_dialog_selected_figure_icon);
        selectedFigureText = (TextView) view.findViewById(R.id.light_figure_editor_dialog_selected_figure_text);

        shapeLabel = (TextView) view.findViewById(R.id.light_figure_editor_dialog_shapes_label);
        textLabel = (TextView) view.findViewById(R.id.light_figure_editor_dialog_text_label);

        shapeRadioButton = (RadioButton) view.findViewById(R.id.light_figure_editor_dialog_shape_select_radio_button);
        shapeRadioButton.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    //Log.i("LightFigureDialog", "SHAPE checked!");
                    selectedFigure = Figure.Type.SHAPE;
                    selectedFigureIcon.setImageResource(shapeIdToIconId.get(figure.getIdentifier()));
                    selectedFigureText.setText(shapeIdToStringId.get(figure.getIdentifier()));

                    shapeRadioButton.setBackgroundColor(getResources().getColor(R.color.blue));
                    shapeLabel.setBackgroundColor(getResources().getColor(R.color.blue));
                    textRadioButton.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                    textLabel.setBackgroundColor(getResources().getColor(R.color.dark_blue));

                    textRadioButton.setChecked(false);
                }
            }
        });

        textRadioButton = (RadioButton) view.findViewById(R.id.light_figure_editor_dialog_text_select_radio_button);
        textRadioButton.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    //Log.i("LightFigureDialog", "TEXT checked!");
                    selectedFigure = Figure.Type.TEXT;
                    selectedFigureIcon.setImageResource(R.drawable.ic_text);
                    selectedFigureText.setText(textEditText.getText().toString());

                    textRadioButton.setBackgroundColor(getResources().getColor(R.color.blue));
                    textLabel.setBackgroundColor(getResources().getColor(R.color.blue));
                    shapeRadioButton.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                    shapeLabel.setBackgroundColor(getResources().getColor(R.color.dark_blue));

                    shapeRadioButton.setChecked(false);
                }
            }
        });

        switch (selectedFigure)
        {
            case SHAPE:
                shapeRadioButton.setChecked(true);
                selectedFigureIcon.setImageResource(shapeIdToIconId.get(figure.getIdentifier()));
                selectedFigureText.setText(shapeIdToStringId.get(figure.getIdentifier()));
                break;

            case TEXT:
                textRadioButton.setChecked(true);
                selectedFigureIcon.setImageResource(R.drawable.ic_text);
                selectedFigureText.setText(textFigure.getText());
                break;
        }

        confirmButton = (Button) view.findViewById(R.id.light_figure_editor_dialog_confirm_button);
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

        cancelButton = (Button) view.findViewById(R.id.light_figure_editor_dialog_cancel_button);
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
            float pivotX = Float.parseFloat(pivotXEditText.getText().toString());
            float pivotY = Float.parseFloat(pivotYEditText.getText().toString());

            if (selectedFigure == Figure.Type.SHAPE)
            {
                figure.setPivot(pivotX, pivotY);
                figure.setIsUsingDefaultPivot(defaultPivotCheckBox.isChecked());
            }
            else
            if (selectedFigure == Figure.Type.TEXT)
            {
                String finalText = textEditText.getText().toString();

                if (finalText.isEmpty())
                {
                    SnackbarFactory.showSnackbar(getView(), "Don't forget to write some text to display!", Snackbar.LENGTH_SHORT);
                    //Toast.makeText(getActivity(), "Don't forget to write some text to display!", Toast.LENGTH_LONG).show();
                    return false;
                }

                for (int i = 0; i < finalText.length(); i++)
                {
                    char ch = finalText.charAt(i);
                    if (ALPHANUMERIC_CHARACTERS.indexOf(ch) < 0)
                    {
                        SnackbarFactory.showSnackbar(getView(), "Sorry! Alphanumeric characters and spaces only.", Snackbar.LENGTH_SHORT);
                        return false;
                    }
                }

                textFigure.setPivot(pivotX, pivotY);
                textFigure.setIsUsingDefaultPivot(defaultPivotCheckBox.isChecked());
                textFigure.setText(textEditText.getText().toString());
            }
        }
        catch (NumberFormatException e)
        {
            SnackbarFactory.showSnackbar(getView(), "Cannot parse numbers from text fields.", Snackbar.LENGTH_SHORT);
            //Toast.makeText(getActivity(), "Cannot parse numbers from text fields.", Toast.LENGTH_LONG).show();
            return false;
        }

        OnLightFigureSelectedListener mListener = (OnLightFigureSelectedListener) getTargetFragment();
        if (selectedFigure == Figure.Type.SHAPE)
        {
            mListener.onSelectFigure(titleView.getText().toString(), figure);
        }
        else
        if (selectedFigure == Figure.Type.TEXT)
        {
            mListener.onSelectTextFigure(titleView.getText().toString(), textFigure);
        }

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

    private void configureShapeSpinner()
    {
        shapeIdList = new ArrayList<String>();
        shapeIdList.add("arrownotched");
        shapeIdList.add("heart");
        shapeIdList.add("arrowpentagon");
        shapeIdList.add("righttriangle");
        shapeIdList.add("triangle");
        shapeIdList.add("star6pt");
        shapeIdList.add("star5ptround");
        shapeIdList.add("arrowchevron");
        shapeIdList.add("parallelogram");
        shapeIdList.add("octagon");
        shapeIdList.add("star3pt");
        shapeIdList.add("star5pt");
        shapeIdList.add("arrow");
        shapeIdList.add("star4pt");
        shapeIdList.add("trapezoid");
        shapeIdList.add("hexagon");
        shapeIdList.add("pentagon");
        shapeIdList.add("ellipse");
        shapeIdList.add("rectangle");
        Collections.sort(shapeIdList);

        shapeIdToIconId = new HashMap<String, Integer>();
        shapeIdToIconId.put("arrownotched", R.drawable.ic_arrownotched);
        shapeIdToIconId.put("heart", R.drawable.ic_heart);
        shapeIdToIconId.put("arrowpentagon", R.drawable.ic_arrowpentagon);
        shapeIdToIconId.put("righttriangle", R.drawable.ic_righttriangle);
        shapeIdToIconId.put("triangle", R.drawable.ic_triangle);
        shapeIdToIconId.put("star6pt", R.drawable.ic_star6pt);
        shapeIdToIconId.put("star5ptround", R.drawable.ic_star5ptround);
        shapeIdToIconId.put("arrowchevron", R.drawable.ic_arrowchevron);
        shapeIdToIconId.put("parallelogram", R.drawable.ic_parallelogram);
        shapeIdToIconId.put("octagon", R.drawable.ic_octagon);
        shapeIdToIconId.put("star3pt", R.drawable.ic_star3pt);
        shapeIdToIconId.put("star5pt", R.drawable.ic_star5pt);
        shapeIdToIconId.put("arrow", R.drawable.ic_arrow);
        shapeIdToIconId.put("star4pt", R.drawable.ic_star4pt);
        shapeIdToIconId.put("trapezoid", R.drawable.ic_trapezoid);
        shapeIdToIconId.put("hexagon", R.drawable.ic_hexagon);
        shapeIdToIconId.put("pentagon", R.drawable.ic_pentagon);
        shapeIdToIconId.put("ellipse", R.drawable.ic_ellipse);
        shapeIdToIconId.put("rectangle", R.drawable.ic_rectangle);

        shapeIdToStringId = new HashMap<String, Integer>();
        shapeIdToStringId.put("arrownotched", R.string.figure_arrownotched);
        shapeIdToStringId.put("heart", R.string.figure_heart);
        shapeIdToStringId.put("arrowpentagon", R.string.figure_arrowpentagon);
        shapeIdToStringId.put("righttriangle", R.string.figure_righttriangle);
        shapeIdToStringId.put("triangle", R.string.figure_triangle);
        shapeIdToStringId.put("star6pt", R.string.figure_star6pt);
        shapeIdToStringId.put("star5ptround", R.string.figure_star5ptround);
        shapeIdToStringId.put("arrowchevron", R.string.figure_arrowchevron);
        shapeIdToStringId.put("parallelogram", R.string.figure_parallelogram);
        shapeIdToStringId.put("octagon", R.string.figure_octagon);
        shapeIdToStringId.put("star3pt", R.string.figure_star3pt);
        shapeIdToStringId.put("star5pt", R.string.figure_star5pt);
        shapeIdToStringId.put("arrow", R.string.figure_arrow);
        shapeIdToStringId.put("star4pt", R.string.figure_star4pt);
        shapeIdToStringId.put("trapezoid", R.string.figure_trapezoid);
        shapeIdToStringId.put("hexagon", R.string.figure_hexagon);
        shapeIdToStringId.put("pentagon", R.string.figure_pentagon);
        shapeIdToStringId.put("ellipse", R.string.figure_ellipse);
        shapeIdToStringId.put("rectangle", R.string.figure_rectangle);

        String[] shapeStrings = new String[shapeIdList.size()];
        for (int i = 0; i < shapeStrings.length; i++)
        {
            shapeStrings[i] = getResources().getString(shapeIdToStringId.get(shapeIdList.get(i)));
        }

        ArrayAdapter<String> shapeAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, shapeStrings);
        shapeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shapeSpinner.setAdapter(shapeAdapter);
        shapeSpinner.setSelection(shapeIdList.indexOf(figure.getIdentifier()));
        shapePreview.setImageResource(shapeIdToIconId.get(figure.getIdentifier()));

        shapeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                figure.setIdentifier(shapeIdList.get(position));
                shapePreview.setImageResource(shapeIdToIconId.get(shapeIdList.get(position)));

                if (selectedFigure == Figure.Type.SHAPE)
                {
                    selectedFigureIcon.setImageResource(shapeIdToIconId.get(shapeIdList.get(position)));
                    selectedFigureText.setText(shapeIdToStringId.get(shapeIdList.get(position)));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });
    }

    private void configureTextFontSpinner()
    {
        textFontList = new ArrayList<String>();

        textFontList.add("Arvo-Regular");
        textFontList.add("Arvo-Bold");
        textFontList.add("Arvo-Italic");
        textFontList.add("Arvo-BoldItalic");

        textFontList.add("Cantarell-Regular");
        textFontList.add("Cantarell-Bold");
        textFontList.add("Cantarell-Italic");
        textFontList.add("Cantarell-BoldItalic");

        textFontList.add("DroidSerif-Regular");
        textFontList.add("DroidSerif-Bold");
        textFontList.add("DroidSerif-Italic");
        textFontList.add("DroidSerif-BoldItalic");

        textFontList.add("Merriweather-Regular");
        textFontList.add("Merriweather-Bold");
        textFontList.add("Merriweather-Italic");
        textFontList.add("Merriweather-BoldItalic");

        textFontList.add("NoticiaText-Regular");
        textFontList.add("NoticiaText-Bold");
        textFontList.add("NoticiaText-Italic");
        textFontList.add("NoticiaText-BoldItalic");

        textFontList.add("OpenSans-Regular");
        textFontList.add("OpenSans-Bold");
        textFontList.add("OpenSans-Italic");
        textFontList.add("OpenSans-BoldItalic");

        ArrayAdapter<String> textFontAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, textFontList);
        textFontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        textFontSpinner.setAdapter(textFontAdapter);
        textFontSpinner.setSelection(textFontList.indexOf(textFigure.getFont()));

        textFontSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                textFigure.setFont(textFontList.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });
    }

    public interface OnLightFigureSelectedListener
    {
        void invokeFigureDialog(String inputPrompt, Figure defaultFigure);
        void onSelectFigure(String inputPrompt, Figure inputFigure);
        void onSelectTextFigure(String inputPrompt, TextFigure inputTextFigure);
    }
}
