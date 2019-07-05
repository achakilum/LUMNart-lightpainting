package com.bluelithalo.lumnart.editor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bluelithalo.lumnart.GLTestView;
import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.pattern.Section;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatternPreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatternPreviewFragment extends Fragment
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SERIALIZED_PATTERN = "serializedPattern";


    public GLTestView glView;
    private Pattern pattern;
    private Layer layer;
    private Section section;
    private Light light;

    private int focusedLayerIdx;
    private int focusedSectionIdx;
    private int focusedLightIdx;

    public PatternPreviewFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param serializedPattern Parameter 1.
     * @return A new instance of fragment PatternPreviewFragment.
     */
    public static PatternPreviewFragment newInstance(String serializedPattern)
    {
        PatternPreviewFragment fragment = new PatternPreviewFragment();
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
                String mSerializedPattern = getArguments().getString(SERIALIZED_PATTERN);
                pattern = new Pattern(mSerializedPattern);
            }
            catch (JSONException e)
            {
                pattern = new Pattern();
            }

            layer = pattern.getLayer(0);
            section = layer.getSection(0);
            light = section.getLight(0);

            focusedLayerIdx = 0;
            focusedSectionIdx = 0;
            focusedLightIdx = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pattern_preview, container, false);
        glView = new GLTestView(getActivity(), pattern, true);
        return glView;
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

    public Pattern getFocusedPattern()
    {
        return pattern;
    }

    public Layer getFocusedLayer()
    {
        return layer;
    }

    public int getFocusedLayerIndex()
    {
        return focusedLayerIdx;
    }

    public void setFocusedLayer(int layerId)
    {
        layer = pattern.getLayer(layerId);
        focusedLayerIdx = layerId;
    }

    public Section getFocusedSection()
    {
        return section;
    }

    public int getFocusedSectionIndex()
    {
        return focusedSectionIdx;
    }

    public void setFocusedSection(int sectionId)
    {
        section = layer.getSection(sectionId);
        focusedSectionIdx = sectionId;
    }

    public Light getFocusedLight()
    {
        return light;
    }

    public int getFocusedLightIndex()
    {
        return focusedLightIdx;
    }

    public void setFocusedLight(int lightId)
    {
        light = section.getLight(lightId);
        focusedLightIdx = lightId;
    }
}
