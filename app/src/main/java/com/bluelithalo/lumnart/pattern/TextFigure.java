package com.bluelithalo.lumnart.pattern;

import android.util.Log;

import com.bluelithalo.lumnart.util.GLESTBAM;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Texture metadata storage for font characters.  These characters will be rendered independently of light source properties such as color, rotation, etc.
 * This object is primarily used as a launchpad for illuminating font characters, thus allowing the user to display and animate lines of text with LUMNart.
 * During rendering, care must be taken to account for the spacing between characters, and the sampling of individual characters from different texture samplers in GLESTBAM.
 * After rendering a single character, the next character must be set up for rendering, given that a next character exists in the desired text to be rendered.
 */
public class TextFigure extends Figure implements JSONizable
{
    /**
     * Descriptors for the alignment of a textual figure
     */
    public enum Alignment
    {
            LEFT, CENTER, RIGHT
    }

    private String text;
    private String font;
    private Alignment alignment;

    private float[] charAdvances = null;
    private float[][] charTexCoords = null;
    private int[] charTexSamplers = null;

    private int characterCount;
    private int curCharacterIdx;
    private float curCharAdvance;

    /**
     * Initializes a default textual figure, displaying "Sample text" in the Open Sans font.
     */
    public TextFigure()
    {
        super();

        this.text = "Sample text";
        this.font = "OpenSans-Regular";
        this.alignment = Alignment.CENTER;

        this.setIdentifier(font + alignment.ordinal() + "_" + text);
        this.setFigureType(Figure.Type.TEXT);

        this.charAdvances = new float[this.text.length()];
        this.charTexCoords = new float[this.text.length()][8];
        this.charTexSamplers = new int[this.text.length()];

        for (int i = 0; i < this.text.length(); i++)
        {
            this.charAdvances[i] = 1.0f;

            this.charTexCoords[i][0] = 1.0f;
            this.charTexCoords[i][1] = 0.0f;
            this.charTexCoords[i][2] = 0.0f;
            this.charTexCoords[i][3] = 0.0f;
            this.charTexCoords[i][4] = 0.0f;
            this.charTexCoords[i][5] = 1.0f;
            this.charTexCoords[i][6] = 1.0f;
            this.charTexCoords[i][7] = 1.0f;

            this.charTexSamplers[i] = 0;
        }
    }

    /**
     * Initializes a textual figure, displaying a piece of custom text with a selected font
     * @param figureText the text to be displayed
     * @param figureFont the font to be used for the text
     */
    public TextFigure(String figureText, String figureFont)
    {
        super("rectangle");

        this.text = figureText;
        this.font = figureFont;
        this.alignment = Alignment.CENTER;

        this.setIdentifier(font + alignment.ordinal() + "_" + text);
        this.setFigureType(Figure.Type.TEXT);

        if (figureText.length() <= 0)
        {
            this.text = "Sample text";
        }

        this.charAdvances = new float[this.text.length()];
        this.charTexCoords = new float[this.text.length()][8];
        this.charTexSamplers = new int[this.text.length()];

        for (int i = 0; i < this.text.length(); i++)
        {
            this.charAdvances[i] = 1.0f;

            this.charTexCoords[i][0] = 1.0f;
            this.charTexCoords[i][1] = 0.0f;
            this.charTexCoords[i][2] = 0.0f;
            this.charTexCoords[i][3] = 0.0f;
            this.charTexCoords[i][4] = 0.0f;
            this.charTexCoords[i][5] = 1.0f;
            this.charTexCoords[i][6] = 1.0f;
            this.charTexCoords[i][7] = 1.0f;

            this.charTexSamplers[i] = 0;
        }
    }

    /**
     * Initializes a textual figure by copying from the attributes of another textual figure.
     * @param copy the textual figure to copy from
     */
    public TextFigure(TextFigure copy)
    {
        super(copy);

        this.text = copy.getText();
        this.font = copy.getFont();
        this.alignment = copy.getAlignment();

        this.setFigureType(copy.getFigureType());

        this.setCharAdvances(copy.getCharAdvances());
        this.setCharTexCoords(copy.getCharTexCoords());
        this.setCharTexSamplers(copy.getCharTexSamplers());
    }

    /**
     * Initializes a textual figure by building it from the contents of a JSON metadata container.
     * @param textFigureObj the JSON metadata container
     */
    public TextFigure(JSONObject textFigureObj) throws JSONException
    {
        super(textFigureObj);

        int idxOfUnderscore = this.getIdentifier().indexOf('_');

        this.text = this.getIdentifier().substring(idxOfUnderscore + 1);
        this.font = this.getIdentifier().substring(0, idxOfUnderscore - 1);

        String alignStr = this.getIdentifier().substring(idxOfUnderscore - 1, idxOfUnderscore);
        this.alignment = Alignment.values()[Integer.parseInt(alignStr)];

        this.setFigureType(Figure.Type.TEXT);

        this.charAdvances = new float[this.text.length()];
        this.charTexCoords = new float[this.text.length()][8];
        this.charTexSamplers = new int[this.text.length()];

        for (int i = 0; i < this.text.length(); i++)
        {
            this.charAdvances[i] = 1.0f;

            this.charTexCoords[i][0] = 1.0f;
            this.charTexCoords[i][1] = 0.0f;
            this.charTexCoords[i][2] = 0.0f;
            this.charTexCoords[i][3] = 0.0f;
            this.charTexCoords[i][4] = 0.0f;
            this.charTexCoords[i][5] = 1.0f;
            this.charTexCoords[i][6] = 1.0f;
            this.charTexCoords[i][7] = 1.0f;

            this.charTexSamplers[i] = 0;
        }
    }

    /**
     * Prepares the textual figure to be rendered by loading its font data and true metadata in the GLESTBAM texture manager,
     * then storing the metadata.
     */
    public void prepare()
    {
        if (this.isPrepared())
        {
            int idxOfUnderscore = this.getPreparedIdentifier().indexOf('_');
            String preparedText = this.getPreparedIdentifier().substring(idxOfUnderscore + 1);
            String preparedFont = this.getPreparedIdentifier().substring(0, idxOfUnderscore - 1);

            GLESTBAM.unloadText(preparedText, preparedFont);
            this.setPrepared(false);
        }

        //Log.i("TextFigure", "prepare()");
        TextFigure loadedTextFigure = GLESTBAM.loadText(this.text, this.font);

        this.setText(loadedTextFigure.getText());
        this.setFont(loadedTextFigure.getFont());

        this.setCharTexCoords(loadedTextFigure.getCharTexCoords());
        this.setCharTexSamplers(loadedTextFigure.getCharTexSamplers());
        this.setCharAdvances(loadedTextFigure.getCharAdvances());

        this.characterCount = this.text.length();
        this.curCharacterIdx = 0;

        float[] aggregateDefaultPivot = loadedTextFigure.getDefaultPivot();

        float totalAdvance = 0.0f;
        for (int i = 0; i < charAdvances.length; i++)
        {
            totalAdvance += charAdvances[i];
        }

        switch (alignment)
        {
            case CENTER:
                aggregateDefaultPivot[0] += (totalAdvance / 2.0f);
                break;
            case RIGHT:
                aggregateDefaultPivot[0] += totalAdvance;
                break;
        }

        //this.setDefaultPivot(aggregateDefaultPivot);

        if (!this.isUsingDefaultPivot())
        {
            this.setPreparedPivot(new float[]{aggregateDefaultPivot[0] + this.getPivot()[0], aggregateDefaultPivot[1] + this.getPivot()[1]});
        }
        else
        {
            this.setPreparedPivot(aggregateDefaultPivot);
        }

        this.initializeBuffer();

        this.setTexSampler(this.charTexSamplers[0]);
        this.updateBuffer(this.charTexCoords[0]);
        this.curCharAdvance = charAdvances[0];

        this.markAsPrepared();
    }

    public void clean()
    {
        if (!this.isPrepared())
        {
            return;
        }

        updateBuffer(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        GLESTBAM.unloadText(this.text, this.font);
        this.setPrepared(false);
    }

    /**
     * Retrieve whether or not there is at least one remaining character that needs to be rendered.
     * @return true if a renderable character exists, false if not.
     */
    public boolean hasNextCharacter()
    {
        return (curCharacterIdx < characterCount);
    }

    /**
     * Updates the current texture buffers with metadata that pertains to the next renderable character.
     * Essentially prepares the next character to be rendered.
     */
    public void bufferNextCharacter()
    {
        this.setTexSampler(this.charTexSamplers[curCharacterIdx]);
        this.updateBuffer(this.charTexCoords[curCharacterIdx]);
        this.curCharAdvance = charAdvances[curCharacterIdx];

        curCharacterIdx++;
    }

    /**
     * Resets the buffer position for renderable characters back to the start.
     */
    public void resetCharacterSeeker()
    {
        curCharacterIdx = 0;
    }

    /**
     * Sets a new name for the textual figure, which will define the text and the respective font in turn.
     * The format for this new name must be "FONTNAME_TEXT"
     * @param newIdentifier the new textual figure identifier to assign.
     */
    public void setIdentifier(String newIdentifier)
    {
        super.setIdentifier(newIdentifier);

        int idxOfUnderscore = this.getIdentifier().indexOf('_');

        this.text = this.getIdentifier().substring(idxOfUnderscore + 1);
        this.font = this.getIdentifier().substring(0, idxOfUnderscore - 1);

        String alignStr = this.getIdentifier().substring(idxOfUnderscore - 1, idxOfUnderscore);
        this.alignment = Alignment.values()[Integer.parseInt(alignStr)];
    }

    /**
     * Retrieve the text that will be rendered from the metadata of this textual figure.
     * @return the text to be rendered
     */
    public String getText()
    {
        return text;
    }

    /**
     * Retrieve the text that will be rendered from the metadata of this textual figure.
     * @param newText the new text to be rendered
     */
    public void setText(String newText)
    {
        this.text = newText;
        this.setIdentifier(font + alignment.ordinal() + "_" + text);
    }

    /**
     * Retrieve the font that will be used to render the contents of this textual figure.
     * @return the current font
     */
    public String getFont()
    {
        return font;
    }

    /**
     * Retrieve the font that will be used to render the contents of this textual figure.
     * @param newFont the new font
     */
    public void setFont(String newFont)
    {
        this.font = newFont;
        this.setIdentifier(font + alignment.ordinal() + "_" + text);
    }

    /**
     * Retrieve the current text alignment that will be used in calculating the textual figure's pivot.
     * @return the current text alignment
     */
    public Alignment getAlignment()
    {
        return alignment;
    }

    /**
     * Set the new text alignment that will be used in calculating the textual figure's pivot.
     * @param newAlignment the new text alignment
     */
    public void setAlignment(Alignment newAlignment)
    {
        alignment = newAlignment;
        this.setIdentifier(font + alignment.ordinal() + "_" + text);
    }

    /**
     * Retrieve the current list of character advancements, relative to OpenGL's 2D space coordinates
     * @return the current list of character advancements
     */
    public float[] getCharAdvances()
    {
        return charAdvances;
    }

    /**
     * Retrieve the character advancement of the currently buffered character, relative to OpenGL's 2D space coordinates
     * @return the current character's advancement value
     */
    public float getCurrentCharAdvance()
    {
        return curCharAdvance;
    }

    /**
     * Set the new list of character advancements, relative to OpenGL's 2D space coordinates.
     * @param newCharAdvances the new list of character advancements
     */
    public void setCharAdvances(float[] newCharAdvances)
    {
        if (newCharAdvances == null)
        {
            this.charAdvances = new float[this.text.length()];
            return;
        }
        charAdvances = Arrays.copyOf(newCharAdvances, newCharAdvances.length);
    }

    /**
     * Retrieve the texture coordinates of each of the characters, given that they were loaded into the GLESTBAM texture manager.
     * @return the current texture coordinates of each character
     */
    public float[][] getCharTexCoords()
    {
        return charTexCoords;
    }

    /**
     * Retrieve the texture coordinates for the character that is currently buffered for rendering.
     * @return the current texture coordinates of the loaded character
     */
    public float[] getCurrentCharTexCoords()
    {
        return this.getBuffer().array();
    }

    /**
     * Set the texture coordinates of each character.
     * This method is only to be used by the GLESTBAM texture manager to load texture coordinates.
     * @param newCharTexCoords the new texture coordinates for each character
     */
    public void setCharTexCoords(float[][] newCharTexCoords)
    {
        if (newCharTexCoords == null)
        {
            this.charTexCoords = new float[this.text.length()][8];
            return;
        }

        charTexCoords = new float[newCharTexCoords.length][newCharTexCoords[0].length];

        for (int i = 0 ; i < charTexCoords.length; i++)
        {
            for (int j = 0; j < charTexCoords[0].length; j++)
            {
                this.charTexCoords[i][j] = newCharTexCoords[i][j];
            }
        }
    }

    /**
     * Retrieve the current list of texture samplers that will be used for each character during their rendering.
     * @return the current list of texture samplers
     */
    public int[] getCharTexSamplers()
    {
        return charTexSamplers;
    }

    /**
     * Retrieve the texture sampler for the character that is currently buffered for rendering.
     * @return the current character's texture sampler
     */
    public int getCurrentCharTexSampler()
    {
        return this.getTexSampler();
    }

    /**
     * Set a new list of texture samplers that will be used for each character during their rendering.
     * @param newCharTexSamplers the new list of character texture samplers
     */
    public void setCharTexSamplers(int[] newCharTexSamplers)
    {
        if (newCharTexSamplers == null)
        {
            this.charTexSamplers = new int[this.text.length()];
            return;
        }
        this.charTexSamplers = Arrays.copyOf(newCharTexSamplers, newCharTexSamplers.length);
    }

}
