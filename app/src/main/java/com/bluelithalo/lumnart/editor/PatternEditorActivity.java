package com.bluelithalo.lumnart.editor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bluelithalo.lumnart.App;
import com.bluelithalo.lumnart.R;
import com.bluelithalo.lumnart.pattern.Figure;
import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.pattern.Stage;
import com.bluelithalo.lumnart.util.AABB;
import com.bluelithalo.lumnart.util.GLESTBAM;
import com.bluelithalo.lumnart.util.ShaderUtils;
import com.bluelithalo.lumnart.util.SnackbarFactory;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class PatternEditorActivity extends AppCompatActivity implements PatternEditorFragment.OnPatternEditListener,
                                                                        LayerEditorFragment.OnLayerEditListener,
                                                                        SectionEditorFragment.OnSectionEditListener,
                                                                        LightEditorFragment.OnLightEditListener
{

    private FrameLayout previewLayout;
    private FrameLayout dashboardLayout;

    private PatternPreviewFragment patternPreviewFragment;
    private PatternEditorFragment patternEditorFragment;
    private LayerEditorFragment layerEditorFragment;
    private SectionEditorFragment sectionEditorFragment;
    private LightEditorFragment lightEditorFragment;

    private int patternHierarchyState = 0;
    private String patternFilepath;
    private boolean patternSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.hide();
        }

        previewLayout = (FrameLayout) findViewById(R.id.preview_layout);
        dashboardLayout = (FrameLayout) findViewById(R.id.dashboard_layout);

        Intent glTestIntent = getIntent();
        Pattern pattern = null;

        try
        {
            patternFilepath = glTestIntent.getStringExtra("patternFilepath");
            //Log.i("GLTestActivity", patternFilepath);

            File patternFile = new File(patternFilepath);
            FileInputStream inStream = new FileInputStream(patternFile);
            int ch = 0;

            StringBuffer jsonStringBuffer = new StringBuffer("");
            while ((ch = inStream.read()) != -1)
            {
                jsonStringBuffer.append((char) ch);
            }

            String jsonString = new String(jsonStringBuffer);
            JSONObject jsonObject = new JSONObject(jsonString);
            pattern = new Pattern(jsonObject);
            //Log.i("GLTestActivity", jsonObject.toString());
        }
        catch (Exception e)
        {
            //Log.e("GLTestActivity", e.toString());
            Toast.makeText(App.getContext(), "Not a valid Pattern file.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        patternPreviewFragment = null;
        patternEditorFragment = null;
        layerEditorFragment = null;
        sectionEditorFragment = null;
        lightEditorFragment = null;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        patternPreviewFragment = PatternPreviewFragment.newInstance(pattern.toJSONString());
        ft.add(R.id.preview_layout, patternPreviewFragment);
        patternEditorFragment = PatternEditorFragment.newInstance(pattern.toJSONString());
        ft.add(R.id.dashboard_layout, patternEditorFragment);
        ft.commit();

        patternSaved = true;
    }

    protected void onDestroy()
    {
        super.onDestroy();

        GLESTBAM.deinitialize();
        ShaderUtils.freeShaderProgram();
    }

    @Override
    public void onBackPressed()
    {
        if (!patternSaved && layerEditorFragment == null)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Your changes to \"" + patternPreviewFragment.getFocusedPattern().getName() + "\" haven't been saved.  Save before exiting?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        saveAndExit();
                                        //Log.i("PatternEditorActivity", "Saved before exiting!");
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        exitWithoutSaving();
                                        //Log.i("PatternEditorActivity", "Exited without saving.");
                                    }
                                })
                                .setCancelable(true);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void savePattern()
    {
        File patternFile = new File(patternFilepath);

        try
        {
            FileWriter patternFileWriter = new FileWriter(patternFile);
            patternFileWriter.write(patternPreviewFragment.getFocusedPattern().toJSONString());
            patternFileWriter.close();
        }
        catch (IOException e)
        {
            //Log.i("PatternEditorActivity", "IO Error when saving pattern.");
        }

        SnackbarFactory.showSnackbar(patternEditorFragment.getView(), "\"" + patternPreviewFragment.getFocusedPattern().getName() + "\" has been saved!", Snackbar.LENGTH_SHORT);
        patternSaved = true;
    }

    private void saveAndExit()
    {
        savePattern();
        super.onBackPressed();
    }

    private void exitWithoutSaving()
    {
        super.onBackPressed();
    }

    @Override
    public void onSavePattern()
    {
        savePattern();
    }

    @Override
    public void onSetPatternName(String newName)
    {
        final String name = newName;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedPattern().setName(name);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "PatternEditorFragment.OnPatternEditListener: onSetPatternName(" + newName + ")");
    }

    @Override
    public void onSetPatternAuthor(String newAuthor)
    {
        final String author = newAuthor;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedPattern().setAuthor(author);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "PatternEditorFragment.OnPatternEditListener: onSetPatternAuthor(" + newAuthor + ")");
    }

    @Override
    public void onSetPatternDescription(String newDescription)
    {
        final String description = newDescription;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedPattern().setDescription(description);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "PatternEditorFragment.OnPatternEditListener: onSetPatternDescription(" + newDescription + ")");
    }

    @Override
    public void onSetPatternColorCode(int newColorCode)
    {
        final int colorCode = newColorCode;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedPattern().setColorCode(colorCode);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "PatternEditorFragment.OnPatternEditListener: onSetPatternColorCode(" + colorCode + ")");
    }

    @Override
    public void onToggleLayerVisibility(int layerIdx)
    {
        final int lIdx = layerIdx;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                if (patternPreviewFragment.getFocusedPattern().isLayerHidden(lIdx))
                {
                    patternPreviewFragment.getFocusedPattern().unhideLayer(lIdx, true);
                }
                else
                {
                    patternPreviewFragment.getFocusedPattern().hideLayer(lIdx, true);
                }
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "PatternEditorFragment.OnPatternEditListener: onToggleLayerVisibility(" + layerIdx + ")");
    }

    @Override
    public void onAddLayer()
    {
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                Layer newLayer = new Layer();
                newLayer.prepare();
                patternPreviewFragment.getFocusedPattern().insertLayer(newLayer);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "PatternEditorFragment.OnPatternEditListener: onAddLayer()");
    }

    @Override
    public void onDuplicateLayer(int layerIdx)
    {
        final int lIdx = layerIdx;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                Layer newLayer = new Layer(patternPreviewFragment.getFocusedPattern().getLayer(lIdx));
                newLayer.setName(newLayer.getName() + "_D");
                newLayer.prepare();
                patternPreviewFragment.getFocusedPattern().insertLayer(lIdx, newLayer);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "PatternEditorFragment.OnPatternEditListener: onDuplicateLayer(" + layerIdx + ")");
    }

    @Override
    public void onMoveLayer(int fromLayerIdx, int toLayerIdx)
    {
        final int fromLIdx = fromLayerIdx;
        final int toLIdx = toLayerIdx;

        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                if (fromLIdx < toLIdx)
                {
                    for (int i = fromLIdx; i < toLIdx; i++)
                    {
                        Layer layerRef = patternPreviewFragment.getFocusedPattern().getLayer(i);
                        boolean layerRefHidden = patternPreviewFragment.getFocusedPattern().isLayerHidden(i);

                        patternPreviewFragment.getFocusedPattern().setLayer(patternPreviewFragment.getFocusedPattern().getLayer(i+1), i);
                        patternPreviewFragment.getFocusedPattern().setLayer(layerRef, i+1);

                        if (patternPreviewFragment.getFocusedPattern().isLayerHidden(i+1)) { patternPreviewFragment.getFocusedPattern().hideLayer(i, false); } else { patternPreviewFragment.getFocusedPattern().unhideLayer(i, false); }
                        if (layerRefHidden) { patternPreviewFragment.getFocusedPattern().hideLayer(i+1, false); } else { patternPreviewFragment.getFocusedPattern().unhideLayer(i+1, false); }
                    }
                }
                else
                {
                    for (int i = fromLIdx; i > toLIdx; i--)
                    {
                        Layer layerRef = patternPreviewFragment.getFocusedPattern().getLayer(i);
                        boolean layerRefHidden = patternPreviewFragment.getFocusedPattern().isLayerHidden(i);

                        patternPreviewFragment.getFocusedPattern().setLayer(patternPreviewFragment.getFocusedPattern().getLayer(i-1), i);
                        patternPreviewFragment.getFocusedPattern().setLayer(layerRef, i-1);

                        if (patternPreviewFragment.getFocusedPattern().isLayerHidden(i-1)) { patternPreviewFragment.getFocusedPattern().hideLayer(i, false); } else { patternPreviewFragment.getFocusedPattern().unhideLayer(i, false); }
                        if (layerRefHidden) { patternPreviewFragment.getFocusedPattern().hideLayer(i-1, false); } else { patternPreviewFragment.getFocusedPattern().unhideLayer(i-1, false); }
                    }
                }
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "PatternEditorFragment.OnPatternEditListener: onMoveLayer(" + fromLayerIdx + ", " + toLayerIdx + ")");
    }

    @Override
    public void onRemoveLayer(int layerIdx)
    {
        final int lIdx = layerIdx;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedPattern().getLayer(lIdx).clean();
                patternPreviewFragment.getFocusedPattern().removeLayer(lIdx);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "PatternEditorFragment.OnPatternEditListener: onRemoveLayer(" + layerIdx + ")");
    }

    @Override
    public void onSelectLayer(int layerIdx)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        layerEditorFragment = LayerEditorFragment.newInstance(patternEditorFragment.getPattern().getLayer(layerIdx).toJSONString(), layerIdx);
        ft.replace(R.id.dashboard_layout, layerEditorFragment);
        ft.addToBackStack("NEW_PATTERN");
        ft.commit();

        patternPreviewFragment.setFocusedLayer(layerIdx);
        //Log.i("PatternEditorActivity", "PatternEditorFragment.OnPatternEditListener: onSelectLayer(" + layerIdx + ")");
    }

    @Override
    public void onSetLayerName(String newName)
    {
        final String name = newName;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedLayer().setName(name);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LayerEditorFragment.OnLayerEditListener: onSetLayerName(" + newName + ")");
    }

    @Override
    public void onSetLayerAlpha(float newAlpha)
    {
        final float alpha = newAlpha;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedLayer().setAlpha(alpha);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LayerEditorFragment.OnLayerEditListener: onSetLayerAlpha(" + newAlpha + ")");
    }

    @Override
    public void onBackFromLayer(String serializedLayer, int layerIdx)
    {
        try
        {
            patternEditorFragment.getPattern().setLayer(new Layer(serializedLayer), layerIdx);
        }
        catch (JSONException e)
        {
            //Log.e("PatternEditorActivity", "Could not modify layer #" + layerIdx + "of the pattern.");
        }

        layerEditorFragment = null;
        //Log.i("PatternEditorActivity", "LayerEditorFragment.OnLayerEditListener: onBackFromLayer(" + serializedLayer + ", " + layerIdx + ")");
    }

    @Override
    public void onToggleSectionVisibility(int sectionIdx)
    {
        final int sIdx = sectionIdx;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                if (patternPreviewFragment.getFocusedLayer().isSectionHidden(sIdx))
                {
                    patternPreviewFragment.getFocusedLayer().unhideSection(sIdx, true);
                    patternPreviewFragment.getFocusedPattern().unhideLayer(patternPreviewFragment.getFocusedLayerIndex(), false);
                    patternEditorFragment.getPattern().unhideLayer(patternPreviewFragment.getFocusedLayerIndex(), false);
                }
                else
                {
                    patternPreviewFragment.getFocusedLayer().hideSection(sIdx, true);

                    boolean allSectionsHidden = true;
                    for (int se = 0; se < patternPreviewFragment.getFocusedLayer().getSectionCount(); se++)
                    {
                        allSectionsHidden &= patternPreviewFragment.getFocusedLayer().isSectionHidden(se);
                    }
                    if (allSectionsHidden)
                    {
                        patternPreviewFragment.getFocusedPattern().hideLayer(patternPreviewFragment.getFocusedLayerIndex(), false);
                        patternEditorFragment.getPattern().hideLayer(patternPreviewFragment.getFocusedLayerIndex(), false);
                    }
                }
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LayerEditorFragment.OnLayerEditListener: onToggleSectionVisibility(" + sectionIdx + ")");
    }

    @Override
    public void onAddSection()
    {
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                Section newSection = new Section();
                newSection.prepare();
                patternPreviewFragment.getFocusedLayer().insertSection(newSection);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LayerEditorFragment.OnLayerEditListener: onAddSection()");
    }

    @Override
    public void onDuplicateSection(int sectionIdx)
    {
        final int sIdx = sectionIdx;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                Section newSection = new Section(patternPreviewFragment.getFocusedLayer().getSection(sIdx));
                newSection.setName(newSection.getName() + "_D");
                newSection.prepare();
                patternPreviewFragment.getFocusedLayer().insertSection(sIdx, newSection);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LayerEditorFragment.OnLayerEditListener: onDuplicateSection(" + sectionIdx + ")");
    }

    @Override
    public void onRemoveSection(int sectionIdx)
    {
        final int sIdx = sectionIdx;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedLayer().getSection(sIdx).clean();
                patternPreviewFragment.getFocusedLayer().removeSection(sIdx);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LayerEditorFragment.OnLayerEditListener: onRemoveSection(" + sectionIdx + ")");
    }

    @Override
    public void onSelectSection(int sectionIdx)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        sectionEditorFragment = SectionEditorFragment.newInstance(layerEditorFragment.getLayer().getSection(sectionIdx).toJSONString(), sectionIdx);
        ft.replace(R.id.dashboard_layout, sectionEditorFragment);
        ft.addToBackStack("LAYER");
        ft.commit();

        patternPreviewFragment.setFocusedSection(sectionIdx);
        //Log.i("PatternEditorActivity", "LayerEditorFragment.OnLayerEditListener: onSelectSection(" + sectionIdx + ")");
    }

    @Override
    public void onSetSectionName(String newName)
    {
        final String name = newName;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedSection().setName(name);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "SectionEditorFragment.OnSectionEditListener: onSetSectionName(" + newName + ")");
    }

    @Override
    public void onSetSectionFitting(boolean newFitting)
    {
        final boolean fitting = newFitting;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedSection().setFitting(fitting);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "SectionEditorFragment.OnSectionEditListener: onSetSectionFitting(" + newFitting + ")");
    }

    @Override
    public void onSetSectionAABB(AABB newAABB)
    {
        final AABB aabb = newAABB;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedSection().setBoundingBox(aabb);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "SectionEditorFragment.OnSectionEditListener: onSetSectionAABB(" + newAABB.toString() + ")");
    }

    @Override
    public void onToggleLightVisibility(int lightIdx)
    {
        final int lIdx = lightIdx;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                if (patternPreviewFragment.getFocusedSection().isLightHidden(lIdx))
                {
                    patternPreviewFragment.getFocusedSection().unhideLight(lIdx);
                    patternPreviewFragment.getFocusedLayer().unhideSection(patternPreviewFragment.getFocusedSectionIndex(), false);
                    layerEditorFragment.getLayer().unhideSection(patternPreviewFragment.getFocusedSectionIndex(), false);
                    patternPreviewFragment.getFocusedPattern().unhideLayer(patternPreviewFragment.getFocusedLayerIndex(), false);
                    patternEditorFragment.getPattern().unhideLayer(patternPreviewFragment.getFocusedLayerIndex(), false);
                }
                else
                {
                    patternPreviewFragment.getFocusedSection().hideLight(lIdx);

                    boolean allLightsHidden = true;
                    for (int li = 0; li < patternPreviewFragment.getFocusedSection().getLightCount(); li++)
                    {
                        allLightsHidden &= patternPreviewFragment.getFocusedSection().isLightHidden(li);
                    }
                    if (allLightsHidden)
                    {
                        patternPreviewFragment.getFocusedLayer().hideSection(patternPreviewFragment.getFocusedSectionIndex(), false);
                        layerEditorFragment.getLayer().hideSection(patternPreviewFragment.getFocusedSectionIndex(), false);
                    }

                    boolean allSectionsHidden = true;
                    for (int se = 0; se < patternPreviewFragment.getFocusedLayer().getSectionCount(); se++)
                    {
                        allSectionsHidden &= patternPreviewFragment.getFocusedLayer().isSectionHidden(se);
                    }
                    if (allSectionsHidden)
                    {
                        patternPreviewFragment.getFocusedPattern().hideLayer(patternPreviewFragment.getFocusedLayerIndex(), false);
                        patternEditorFragment.getPattern().hideLayer(patternPreviewFragment.getFocusedLayerIndex(), false);
                    }
                }
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "SectionEditorFragment.OnSectionEditListener: onToggleLightVisibility(" + lightIdx + ")");
    }

    @Override
    public void onBackFromSection(String serializedSection, int sectionIdx)
    {
        try
        {
            layerEditorFragment.getLayer().setSection(new Section(serializedSection), sectionIdx);
        }
        catch (JSONException e)
        {
            //Log.e("PatternEditorActivity", "Could not modify section #" + sectionIdx + "of the layer.");
        }

        sectionEditorFragment = null;
        //Log.i("PatternEditorActivity", "SectionEditorFragment.OnSectionEditListener: onBackFromSection(" + serializedSection + ", " + sectionIdx + ")");
    }

    @Override
    public void onAddLight()
    {
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                Light newLight = new Light();
                newLight.prepare();
                patternPreviewFragment.getFocusedSection().insertLight(newLight);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "SectionEditorFragment.OnSectionEditListener: onAddLight()");
    }

    @Override
    public void onDuplicateLight(int lightIdx)
    {
        final int lIdx = lightIdx;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                Light newLight = new Light(patternPreviewFragment.getFocusedSection().getLight(lIdx));
                newLight.setName(newLight.getName() + "_D");
                newLight.prepare();
                patternPreviewFragment.getFocusedSection().insertLight(lIdx, newLight);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "SectionEditorFragment.OnSectionEditListener: onDuplicateLight(" + lightIdx + ")");
    }

    @Override
    public void onRemoveLight(int lightIdx)
    {
        final int lIdx = lightIdx;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedSection().getLight(lIdx).clean();
                patternPreviewFragment.getFocusedSection().removeLight(lIdx);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "SectionEditorFragment.OnSectionEditListener: onRemoveLight(" + lightIdx + ")");
    }

    @Override
    public void onSelectLight(int lightIdx)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        lightEditorFragment = LightEditorFragment.newInstance(sectionEditorFragment.getSection().getLight(lightIdx).toJSONString(), lightIdx);
        ft.replace(R.id.dashboard_layout, lightEditorFragment);
        ft.addToBackStack("SECTION");
        ft.commit();

        patternPreviewFragment.setFocusedLight(lightIdx);
        //Log.i("PatternEditorActivity", "LightEditorFragment.OnLightEditListener: onSelectLight(" + lightIdx + ")");
    }

    @Override
    public void onSetLightName(String newName)
    {
        final String name = newName;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedLight().setName(name);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LightEditorFragment.OnLightEditListener: onSetLightName(" + newName + ")");
    }

    @Override
    public void onSetLightFigure(Figure newFigure)
    {
        final Figure figure = newFigure;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedLight().clean();
                patternPreviewFragment.getFocusedLight().setFigure(figure);
                patternPreviewFragment.getFocusedLight().prepare();
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LightEditorFragment.OnLightEditListener: onSetLightFigure(" + newFigure.toJSONString() + ")");
    }

    @Override
    public void onSetLightUniformDimensions(boolean newUniformDimensions)
    {
        final boolean uniformDimensions = newUniformDimensions;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedLight().setUniformDimensions(uniformDimensions);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LightEditorFragment.OnLightEditListener: onSetLightUniformDimensions(" + newUniformDimensions + ")");
    }

    @Override
    public void onSetLightOutlineWidth(float newOutlineWidth)
    {
        final float outlineWidth = newOutlineWidth;
        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedLight().setOutlineWidth(outlineWidth);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LightEditorFragment.OnLightEditListener: onSetLightOutlineWidth(" + newOutlineWidth + ")");
    }

    @Override
    public void onAddPropertyStage(Property.Type propertyCode, Stage newStage)
    {
        final Property.Type propertyType = propertyCode;
        final Stage stage = newStage;

        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedPattern().reset();
                patternPreviewFragment.getFocusedLight().getProperty(propertyType).insertStage(stage);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LightEditorFragment.OnLightEditListener: onAddPropertyStage(" + propertyCode.name() + ")");
    }

    @Override
    public void onSetPropertyStage(Property.Type propertyCode, int stageIdx, Stage newStage)
    {
        final Property.Type propertyType = propertyCode;
        final int sIdx = stageIdx;
        final Stage stage = newStage;

        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedPattern().reset();
                patternPreviewFragment.getFocusedLight().getProperty(propertyType).setStage(stage, sIdx);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LightEditorFragment.OnLightEditListener: onSetPropertyStage(" + propertyCode.name() + ", " + stageIdx + ", " + newStage.toJSONString() + ")");
    }

    @Override
    public void onDuplicatePropertyStage(Property.Type propertyCode, int stageIdx)
    {
        final Property.Type propertyType = propertyCode;
        final int sIdx = stageIdx;

        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                Stage newStage = new Stage(patternPreviewFragment.getFocusedLight().getProperty(propertyType).getStage(sIdx));
                patternPreviewFragment.getFocusedPattern().reset();
                patternPreviewFragment.getFocusedLight().getProperty(propertyType).insertStage(sIdx, newStage);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LightEditorFragment.OnLightEditListener: onDuplicatePropertyStage(" + propertyCode.name() + ", " + stageIdx + ")");
    }

    @Override
    public void onRemovePropertyStage(Property.Type propertyCode, int stageIdx)
    {
        final Property.Type propertyType = propertyCode;
        final int sIdx = stageIdx;

        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedPattern().reset();
                patternPreviewFragment.getFocusedLight().getProperty(propertyType).removeStage(sIdx);
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LightEditorFragment.OnLightEditListener: onRemovePropertyStage(" + propertyCode.name() + ", " + stageIdx + ")");
    }

    @Override
    public void onMovePropertyStage(Property.Type propertyCode, int fromStageIdx, int toStageIdx)
    {
        final Property.Type propertyType = propertyCode;
        final int fromSIdx = fromStageIdx;
        final int toSIdx = toStageIdx;

        patternPreviewFragment.glView.queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                patternPreviewFragment.getFocusedPattern().reset();

                if (fromSIdx < toSIdx)
                {
                    for (int i = fromSIdx; i < toSIdx; i++)
                    {
                        Stage stageRef = patternPreviewFragment.getFocusedLight().getProperty(propertyType).getStage(i);
                        patternPreviewFragment.getFocusedLight().getProperty(propertyType).setStage(patternPreviewFragment.getFocusedLight().getProperty(propertyType).getStage(i+1), i);
                        patternPreviewFragment.getFocusedLight().getProperty(propertyType).setStage(stageRef, i+1);
                    }
                }
                else
                {
                    for (int i = fromSIdx; i > toSIdx; i--)
                    {
                        Stage stageRef = patternPreviewFragment.getFocusedLight().getProperty(propertyType).getStage(i);
                        patternPreviewFragment.getFocusedLight().getProperty(propertyType).setStage(patternPreviewFragment.getFocusedLight().getProperty(propertyType).getStage(i-1), i);
                        patternPreviewFragment.getFocusedLight().getProperty(propertyType).setStage(stageRef, i-1);
                    }
                }
            }
        });

        patternSaved = false;
        //Log.i("PatternEditorActivity", "LightEditorFragment.OnLightEditListener: onMovePropertyStage(" + propertyCode.name() + ", " + fromStageIdx + ", " + toStageIdx + ")");
    }

    @Override
    public void onBackFromLight(String serializedLight, int lightIdx)
    {
        try
        {
            sectionEditorFragment.getSection().setLight(new Light(serializedLight), lightIdx);
        }
        catch (JSONException e)
        {
            //Log.e("PatternEditorActivity", "Could not modify light #" + lightIdx + "of the section.");
        }

        //Log.i("PatternEditorActivity", "LightEditorFragment.OnLightEditListener: onBackFromLight(" + serializedLight + ", " + lightIdx + ")");
    }
}
