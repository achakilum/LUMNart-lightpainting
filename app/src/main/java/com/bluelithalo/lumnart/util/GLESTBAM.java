package com.bluelithalo.lumnart.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.bluelithalo.lumnart.App;
import com.bluelithalo.lumnart.pattern.Figure;
import com.bluelithalo.lumnart.pattern.ImageFigure;
import com.bluelithalo.lumnart.pattern.TextFigure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * OpenGL ES Texture Buddy Allocation Manager.
 * An OpenGL texture manager that was designed to reduce memory fragmentation across OpenGL's texture memory space.
 *
 * This is a small library that is designed to manage texture memory using a combination of the buddy allocation technique and the next-fit memory allocation technique.
 * Given a Bitmap that references an image in memory, you may allocate that image, retrieve its rendering info such as its texture coordinates and designated texture sampler,
 * then free the images.  All basic allocation and de-allocation actions depend on having a naming reference (as a string) to a Bitmap.
 *
 * Because this library uses the buddy allocation technique, and this application mostly uses perfectly squared textures (from the provided shape and text assets),
 * this results in reduced memory fragmentation across the LUMNart app.
 *
 * For the purpose of supporting LUMNart, this version of GLESTBAM includes publicly interfaced functionality for the allocation and de-allocation of textures pertaining to shapes and text, along with images.
 * These assets may be referenced by a shape name, or a specific character with a corresponding font.
 */
public class GLESTBAM
{
    /**
     * A texture memory allocation node that is a part of a linked list of nodes to traverse in the next-fit allocation technique.
     */
    private static class GLESTList
    {
        boolean isFree;
        int size;
        int rIdx;

        float[] coords;
        int sampler;
        int refCount;

        GLESTList prevFree;
        GLESTList prev;
        GLESTList next;
        GLESTList nextFree;

        GLESTList(int tSize, int tRIdx, int tSampler)
        {
            isFree = true;
            size = tSize;
            rIdx = tRIdx;

            coords = null;
            sampler = tSampler;
            refCount = 0;

            prevFree = null;
            prev = null;
            next = null;
            nextFree = null;
        }
    }

    private static HashMap<String, GLESTList> glestMap = null;
    private static ArrayList<int[]> textureIds;

    private static GLESTList listHead;
    private static GLESTList listIterator;
    private static GLESTList listTail;

    private static final int MAX_TEXTURE_SPACE_SIZE = 2048;
	private static final int MAX_TEXTURE_SPACE_COUNT = 16;

    private static JSONObject shapeAtlasMetadata = null;
    private static JSONObject glyphAtlasMetadata = null;
    private static String shapeAtlasMetadataFilename = "";
    private static String glyphAtlasMetadataFilename = "";

    /**
     * Initializes the GLESTBAM texture manager.
     * This sets up a map of references from texture identifiers to texture memory allocation nodes,
     * and a linked list of those memory allocation nodes.
     */
    public static void initialize()
    {
		if ((listHead != null) && (listTail != null))
		{
			//Log.e("GLESTBAM", "GLESTBAM already initialized.");
			return;
		}

		glestMap = new HashMap<String, GLESTList>();
		textureIds = new ArrayList<int[]>();

        GLESTList prevSpace = null;
		for (int i = 0; i < MAX_TEXTURE_SPACE_COUNT; i++)
		{
			textureIds.add(null);

            GLESTList newSpace = new GLESTList(2048, i, i);

			if (i == 0)
			{
				listHead = newSpace;
				listIterator = newSpace;
			}

			listTail = newSpace;

			if (prevSpace != null)
			{
                prevSpace.next = newSpace;
                newSpace.prev = prevSpace;
			}

            prevSpace = newSpace;
		}
    }

    /**
     * Frees all resources in the GLESTBAM texture manager, including the textures that have been
     * placed in the OpenGL texture memory.
     */
    public static void deinitialize()
    {
        if (listHead == null && listTail == null)
        {
            //Log.e("GLESTBAM", "GLESTBAM already deinitialized.");
            return;
        }

        for (int i = 0; i < MAX_TEXTURE_SPACE_COUNT; i++)
        {
            if (textureIds.get(i) != null)
            {
                GLES20.glDeleteTextures(1, textureIds.get(i), 0);
            }

            textureIds.set(i, null);
        }

        glestMap.clear();
        glestMap = null;

        shapeAtlasMetadata = null;
        glyphAtlasMetadata = null;
        shapeAtlasMetadataFilename = "";
        glyphAtlasMetadataFilename = "";

        listHead = null;
        listIterator = null;
        listTail = null;
    }

    /**
     * Allocates a texture into OpenGL's texture memory, given a name by which the texture may be referenced.
     * If any references to a texture already exist, simply increment the texture's reference count.
     * @param textureName the name of the texture
     * @param textureBitmap a reference to the texture bitmap
     * @return true if the allocation was successful, false otherwise
     */
    private static boolean allocate(String textureName, Bitmap textureBitmap)
    {
        GLESTList existingItem = glestMap.get(textureName);
        if (existingItem != null)
        {
            existingItem.refCount++;
            return true;
        }

        GLESTList listStop = listIterator;
        boolean searchComplete = false;

        int w = textureBitmap.getWidth();
        int h = textureBitmap.getHeight();
        int textureSize = Math.max(w, h);

        // Allocator not initialized
        if (listIterator == null)
        {
            //Log.e("GLESTBAM", "iterator does not exist");
            return false;
        }

        // Bitmap won't fit
        if (textureSize > MAX_TEXTURE_SPACE_SIZE - 2)
        {
            //Log.e("GLESTBAM", "no space for texture of size " + textureSize);
            return false;
        }

        // Loop until we have searched through all texture memory blocks
        while (!searchComplete)
        {
            // Bitmap will fit
            if ((textureSize > (listIterator.size / 2) - 2) && (textureSize <= listIterator.size - 2) && listIterator.isFree)
            {
                // Allocation
                GLESTList toAllocate = listIterator;
                toAllocate.isFree = false;
                toAllocate.refCount = 1;
                glestMap.put(textureName, toAllocate);

                if (toAllocate.size == MAX_TEXTURE_SPACE_SIZE)
                {
                    growSpace(toAllocate.sampler);
                }

                int samplerRIdx = toAllocate.rIdx % ((MAX_TEXTURE_SPACE_SIZE / toAllocate.size) * (MAX_TEXTURE_SPACE_SIZE / toAllocate.size));
                int[] idx2d = index2D(samplerRIdx);

                int x = idx2d[0];
                int y = idx2d[1];

                //"texCoords": [0.185546875, 0.001953125, 0.126953125, 0.001953125, 0.126953125, 0.05269680172204971, 0.185546875, 0.05269680172204971],
                //"texCoords": [hiX,		loY,		loX,	loY,		loX,		hiY,		hiX,		hiY		],

                int spacePlacementX = x * toAllocate.size;
                int spacePlacementY = y * toAllocate.size;

                float loX = (spacePlacementX + 1) / (MAX_TEXTURE_SPACE_SIZE * 1.0f);
                float loY = (spacePlacementY + 1) / (MAX_TEXTURE_SPACE_SIZE * 1.0f);
                float hiX = (spacePlacementX + 1 + w) / (MAX_TEXTURE_SPACE_SIZE * 1.0f);
                float hiY = (spacePlacementY + 1 + h) / (MAX_TEXTURE_SPACE_SIZE * 1.0f);

                float[] coords = new float[]{hiX, loY, loX, loY, loX, hiY, hiX, hiY};
                toAllocate.coords = coords;

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + toAllocate.sampler);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds.get(toAllocate.sampler)[0]);
                GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, (spacePlacementX + 1), (spacePlacementY + 1), textureBitmap, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE);

                //Log.e("GLESTBAM", "(+) texture allocated with tag " + textureName);
                searchComplete = true;
            }
            else
            if (textureSize <= (listIterator.size / 2) - 2 && listIterator.isFree)
            {
                GLESTList toSplit = listIterator;

                if (toSplit.size == MAX_TEXTURE_SPACE_SIZE)
                {
                    growSpace(toSplit.sampler);
                }

                int newSize = toSplit.size >> 1;
                int newRIdxBase = toSplit.rIdx << 2;

                // Grow list with new sublist
                toSplit.size = newSize;
                toSplit.rIdx = newRIdxBase;
                GLESTList next1 = new GLESTList(newSize, newRIdxBase + 1, toSplit.sampler);
                GLESTList next2 = new GLESTList(newSize, newRIdxBase + 2, toSplit.sampler);
                GLESTList next3 = new GLESTList(newSize, newRIdxBase + 3, toSplit.sampler);

                // Whole sublist linking
                GLESTList curItemNext = toSplit.next;

                toSplit.next = next1;
                next1.next = next2;
                next2.next = next3;
                next3.next = curItemNext;

                if (curItemNext != null)
                {
                    curItemNext.prev = next3;
                }

                next3.prev = next2;
                next2.prev = next1;
                next1.prev = toSplit;

                if (listIterator == listTail)
                {
                    listTail = next3;
                }

                continue;
            }

            // Loop around list or iterate to next list item
            if (listIterator == listTail)
            {
                listIterator = listHead;
            }
            else
            {
                listIterator = listIterator.next;
            }

            if (listIterator == listStop)
            {
                searchComplete = true;
            }
        }

		if (textureName.length() <= 0)
        {
            //Log.e("GLESTBAM", "no space found for texture of size " + textureSize);
            return false;
        }

        return true;
    }

    /**
     * Frees a texture from OpenGL's texture memory, given a name by which the texture may be referenced.
     * If there are multiple references to the texture being freed, simply decrement the reference count.
     * @param textureName the name of the texture
     */
    private static void free(String textureName)
    {
        GLESTList freeItem = glestMap.get(textureName);
        if (freeItem != null && freeItem.refCount > 1)
        {
            freeItem.refCount--;
            return;
        }
        else
        if (freeItem != null)
        {
            freeItem.isFree = true;
            freeItem.coords = null;

            glestMap.remove(textureName);
            //Log.e("GLESTBAM", "(-) texture with tag " + textureName + " found and freed");

            // Free list coalescing
            while (freeItem.size < MAX_TEXTURE_SPACE_SIZE)
            {
                boolean coalesceFailure = false;

                // Search for blocks to coalesce
                //std::vector<GLTList *> tToCoalesce(4, NULL);
                ArrayList<GLESTList> toCoalesce = new ArrayList<GLESTList>();

                for (int i = 0; i < 4; i++)
                {
                    toCoalesce.add(null);
                }

                int freeItemIdx = freeItem.rIdx & 3;
                toCoalesce.set(freeItemIdx, freeItem);

                GLESTList freeRight = freeItem.next;
                for (int i = freeItemIdx + 1; i <= 3; i++)
                {
                    if ((freeRight != null) && // next node exists
                        (freeRight.isFree) && // next node is free
                        (freeRight.rIdx & (~3)) == (freeItem.rIdx & (~3)) && // next node and tFree belong in the same group
                        (freeRight.rIdx & 3) == i && // next node group index matches expected index
                        (freeRight.size == freeItem.size)) // next node size matches expected size
                    {
                        toCoalesce.set(i, freeRight);
                        freeRight = freeRight.next;
                    }
                    else
                    {
                        coalesceFailure = true;
                    }
                }

                GLESTList freeLeft = freeItem.prev;
                for (int i = freeItemIdx - 1; i >= 0; i--)
                {
                    if ((freeLeft != null) && // next node exists
                        (freeLeft.isFree) && // next node is free
                        (freeLeft.rIdx & (~3)) == (freeItem.rIdx & (~3)) && // next node and tFree belong in the same group
                        (freeLeft.rIdx & 3) == i && // next node group index matches expected index
                        (freeLeft.size == freeItem.size)) // next node size matches expected size
                    {
                        toCoalesce.set(i, freeLeft);
                        freeLeft = freeLeft.prev;
                    }
                    else
                    {
                        coalesceFailure = true;
                    }
                }

                if (coalesceFailure)
                {
                    break;
                }

                //Log.e("GLESTBAM", "[-] Coalescing blocks...");
                for (int i = 0; i < toCoalesce.size(); i++)
                {
                    GLESTList sublistItem = toCoalesce.get(i);
                    GLESTList sublistHead = toCoalesce.get(0);
                    if (sublistItem == listHead)
                    {
                        listHead = sublistHead;
                    }

                    if (sublistItem == listTail)
                    {
                        listTail = sublistHead;
                    }

                    if (sublistItem == listIterator)
                    {
                        listIterator = sublistHead;
                    }
                }

                // Coalesce blocks that were found
                toCoalesce.get(0).next = toCoalesce.get(3).next;
                if (toCoalesce.get(3).next != null)
                {
                    toCoalesce.get(3).next.prev = toCoalesce.get(0);
                }

                toCoalesce.get(0).size = toCoalesce.get(0).size << 1;
                toCoalesce.get(0).rIdx = toCoalesce.get(0).rIdx >> 2;

                freeItem = toCoalesce.get(0);
            }

            int idx = freeItem.rIdx % ((MAX_TEXTURE_SPACE_SIZE / freeItem.size) * (MAX_TEXTURE_SPACE_SIZE / freeItem.size));
            int[] idx2d = index2D(idx);

            int x = idx2d[0];
            int y = idx2d[1];

            int spacePlacementX = x * freeItem.size;
            int spacePlacementY = y * freeItem.size;

            Bitmap clearBitmap = Bitmap.createBitmap(freeItem.size, freeItem.size, Bitmap.Config.ARGB_8888);
            clearBitmap.eraseColor(0xFF000000); // Black (100% Opacity)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + freeItem.sampler);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds.get(freeItem.sampler)[0]);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, spacePlacementX, spacePlacementY, clearBitmap);
            clearBitmap.recycle();

            if (freeItem.size == MAX_TEXTURE_SPACE_SIZE)
            {
                GLES20.glDeleteTextures(1, textureIds.get(freeItem.sampler), 0);
                textureIds.set(freeItem.sampler, null);
            }
        }
    }

    /**
     * Loads a shape-representative texture into OpenGL's texture memory.
     * In return, the necessary metadata will be generated for the purpose of rendering the shape on-screen.
     * @param shapeIdentifier the name of the shape
     * @return a reference to a figure object that contains the shape's rendering metadata
     */
    public static Figure loadShape(String shapeIdentifier)
    {
        if (!shapeAtlasMetadataFilename.equals("shapes/shapes.json"))
        {
            if (loadShapeAtlasMetadata() != 0)
            {
                return new Figure();
            }
        }

        JSONObject shapeObj = null;
        String textureAtlasFilepath = "";
        Figure loadedFigure = null;

        float[] texCoords = null;
        float[] defaultPivot = null;
        float defaultWHRatio = 1.0f;
        int texImageIdx = 0;

        /* "triangle": {
                        "texCoords": [0.185546875, 0.001953125, 0.126953125, 0.001953125, 0.126953125, 0.05269680172204971, 0.185546875, 0.05269680172204971],
                        "pivot": [0.0, -0.3333333333333333],
                        "texIdx": 1,
                        "whRatio": 1.1547005220167499
                       }
        */

        try
        {
            shapeObj = shapeAtlasMetadata.getJSONObject(shapeIdentifier);
            textureAtlasFilepath = "shapes/" + shapeObj.getInt("texIdx") + ".png";

            texCoords = new float[8];
            JSONArray texCoordArr = shapeObj.getJSONArray("texCoords");
            for (int i = 0; i < texCoords.length; i++)
            {
                texCoords[i] = (float) texCoordArr.getDouble(i);
            }

            defaultPivot = new float[2];
            JSONArray defaultPivotArr = shapeObj.getJSONArray("pivot");
            for (int i = 0; i < defaultPivot.length; i++)
            {
                defaultPivot[i] = (float) defaultPivotArr.getDouble(i);
            }

            defaultWHRatio = (float) shapeObj.getDouble("whRatio");
            texImageIdx = shapeObj.getInt("texIdx");
        }
        catch (JSONException e)
        {
            //Log.e("GLESTBAM", "loadShape() : " + e.getMessage());
            return new Figure();
        }

        GLESTList atlasReferenceItem = glestMap.get(textureAtlasFilepath);
        if (atlasReferenceItem == null)
        {
            try
            {
                AssetManager am = App.getApplication().getAssets();
                InputStream atlasStream = am.open(textureAtlasFilepath);
                Bitmap atlasBitmap = BitmapFactory.decodeStream(atlasStream);

                allocate(textureAtlasFilepath, atlasBitmap);
                atlasReferenceItem = glestMap.get(textureAtlasFilepath);
                atlasBitmap.recycle();
            }
            catch (IOException e)
            {
                //Log.e("GLESTBAM", e.getMessage());
                return new Figure();
            }
        }
        else
        {
            atlasReferenceItem.refCount++; // Increment shape reference count
        }

        float minX = atlasReferenceItem.coords[2];
        float minY = atlasReferenceItem.coords[3];

        for (int i = 0; i < texCoords.length; i++)
        {
            texCoords[i] += ((i % 2 == 0) ? minX : minY);
        }

        loadedFigure = new Figure(shapeIdentifier);
        loadedFigure.setDefaultWHRatio(defaultWHRatio);
        loadedFigure.setTexCoords(texCoords);
        loadedFigure.setDefaultPivot(defaultPivot);
        loadedFigure.setTexSampler(atlasReferenceItem.sampler);

        return loadedFigure;
    }

    /**
     * Frees a shape-representative texture from OpenGL's texture memory.
     * @param shapeIdentifier the name of the shape
     * @return true if the shape texture was successfully freed, false otherwise
     */
    public static boolean unloadShape(String shapeIdentifier)
    {
        if (!shapeAtlasMetadataFilename.equals("shapes/shapes.json"))
        {
            if (loadShapeAtlasMetadata() != 0)
            {
                return false;
            }
        }

        JSONObject shapeObj = null;
        String textureAtlasFilepath = "";

        try
        {
            shapeObj = shapeAtlasMetadata.getJSONObject(shapeIdentifier);
            textureAtlasFilepath = "shapes/" + shapeObj.getInt("texIdx") + ".png";
        }
        catch (JSONException e)
        {
            //Log.e("GLESTBAM", "unloadShape() : " + e.getMessage());
            return false;
        }

        free(textureAtlasFilepath);
        return true;
    }

    /**
     * Loads a text-representative texture into OpenGL's texture memory.
     * In return, the necessary metadata will be generated for the purpose of rendering the text on-screen.
     * This works by allocating textures associated with each character in the text, or incrementing reference counts.
     * @param text the text to be displayed
     * @param font the font in which the text will be displayed
     * @return a reference to a textual figure object that contains the text's rendering metadata
     */
    public static TextFigure loadText(String text, String font)
    {
        if (!glyphAtlasMetadataFilename.equals("glyphs/" + font + "/glyphs.json"))
        {
            if (loadGlyphAtlasMetadata(font) != 0)
            {
                return new TextFigure();
            }
        }

        /*
        {
            // Index of glyph's texture atlas (glyph is in [INDEX #].png), plus the glyph's horizontal advance after the initial pen-point
            "idxAdvPairs": {" ": [0, 0.2576271186440678], ...},
            // Texture coordinates of the glyph in its respective texture atlas
            "texCoords": {" ": [0.04931640625, 0.00048828125, 0.00048828125, 0.00048828125, 0.00048828125, 0.04931640625, 0.04931640625, 0.04931640625], ...},
            // Pivot for ALL quads in font
            "pivot": [-0.42251815980629537, -0.2598062953995158]
        }
         */

        /*
            private String text;
            private String font;

            private float[] charAdvances = null;
            private float[][] charTexCoords = null;
            private int[] charTexSamplers = null;
         */

        JSONObject idxAdvPairObj = null;
        JSONObject texCoordObj = null;
        JSONArray pivotArr = null;

        String glyphAtlasFilepath = "";
        TextFigure loadedTextFigure = null;

        float[] charAdvances = new float[text.length()];
        float[][] charTexCoords = new float[text.length()][8];
        int[] charTexSamplers = new int[text.length()];
        float[] defaultPivot = new float[2];

        for (int i = 0; i < text.length(); i++)
        {
            String textChar = new String(new char[]{text.charAt(i)});

            try
            {
                idxAdvPairObj = glyphAtlasMetadata.getJSONObject("idxAdvPairs");
                JSONArray idxAdvPairArr = idxAdvPairObj.getJSONArray(textChar);

                glyphAtlasFilepath = "glyphs/" + font + "/" + idxAdvPairArr.getInt(0) + ".png";
                charAdvances[i] = (float) idxAdvPairArr.getDouble(1);

                texCoordObj = glyphAtlasMetadata.getJSONObject("texCoords");
                JSONArray texCoordArr = texCoordObj.getJSONArray(textChar);

                for (int j = 0; j < charTexCoords[i].length; j++)
                {
                    charTexCoords[i][j] = (float) texCoordArr.getDouble(j);
                }

                JSONArray defaultPivotArr = glyphAtlasMetadata.getJSONArray("pivot");
                for (int j = 0; j < defaultPivot.length; j++)
                {
                    defaultPivot[j] = (float) defaultPivotArr.getDouble(j);
                }

            }
            catch (JSONException e)
            {
                //Log.e("GLESTBAM", "loadText() : " + e.getMessage());
                return new TextFigure();
            }

            GLESTList atlasReferenceItem = glestMap.get(glyphAtlasFilepath);
            if (atlasReferenceItem == null)
            {
                try
                {
                    AssetManager am = App.getApplication().getAssets();
                    InputStream atlasStream = am.open(glyphAtlasFilepath);
                    Bitmap atlasBitmap = BitmapFactory.decodeStream(atlasStream);

                    allocate(glyphAtlasFilepath, atlasBitmap);
                    atlasReferenceItem = glestMap.get(glyphAtlasFilepath);
                    atlasBitmap.recycle();
                }
                catch (IOException e)
                {
                    //Log.e("GLESTBAM", e.getMessage());
                    return new TextFigure();
                }
            }
            else
            {
                atlasReferenceItem.refCount++; // Increment shape reference count
            }

            float minX = atlasReferenceItem.coords[2];
            float minY = atlasReferenceItem.coords[3];

            for (int j = 0; j < charTexCoords[i].length; j++)
            {
                charTexCoords[i][j] += ((j % 2 == 0) ? minX : minY);
            }

            charTexSamplers[i] = atlasReferenceItem.sampler;
        }

        loadedTextFigure = new TextFigure(text, font);
        loadedTextFigure.setCharTexSamplers(charTexSamplers);
        loadedTextFigure.setCharTexCoords(charTexCoords);
        loadedTextFigure.setCharAdvances(charAdvances);
        loadedTextFigure.setDefaultPivot(defaultPivot);

        return loadedTextFigure;
    }

    /**
     * Frees a text-representative texture into OpenGL's texture memory.
     * Works by freeing textures associated with each character in the text, or decrementing reference counts.
     * @param text the text that was displayed
     * @param font the font with which the text was displayed
     * @return true if the text textures were successfully freed, false otherwise
     */
    public static boolean unloadText(String text, String font)
    {
        if (!glyphAtlasMetadataFilename.equals("glyphs/" + font + "/glyphs.json"))
        {
            if (loadGlyphAtlasMetadata(font) != 0)
            {
                return false;
            }
        }

        JSONObject idxAdvPairObj = null;
        ArrayList<String> glyphAtlasFilepaths = new ArrayList<String>();

        for (int i = 0; i < text.length(); i++)
        {
            String textChar = new String(new char[]{text.charAt(i)});
            String glyphAtlasFilepath = "";

            try
            {
                idxAdvPairObj = glyphAtlasMetadata.getJSONObject("idxAdvPairs");
                JSONArray idxAdvPairArr = idxAdvPairObj.getJSONArray(textChar);

                glyphAtlasFilepath = "glyphs/" + font + "/" + idxAdvPairArr.getInt(0) + ".png";
            }
            catch (JSONException e)
            {
                //Log.e("GLESTBAM", "unloadText() : " + e.getMessage());
                return false;
            }

            glyphAtlasFilepaths.add(glyphAtlasFilepath);
        }

        for (int i = 0; i < glyphAtlasFilepaths.size(); i++)
        {
            free(glyphAtlasFilepaths.get(i));
        }

        return true;
    }

    /**
     * Loads an image-representative texture into OpenGL's texture memory.
     * In return, the necessary metadata will be generated for the purpose of rendering the image on-screen.
     * @param imagePath the path to the image in Android's file system or content management system
     * @return a reference to an image figure object that contains the image's rendering metadata
     */
    public static ImageFigure loadImage(String imagePath)
    {
        ImageFigure loadedImageFigure = null;
        float defaultWHRatio = 1.0f;
        float[] texCoords = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        float[] defaultPivot = new float[]{0.0f, 0.0f};
        int texSampler = 0;

        GLESTList atlasReferenceItem = glestMap.get(imagePath);
        if (atlasReferenceItem == null)
        {
            BitmapFactory.Options ops = new BitmapFactory.Options();
            ops.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap atlasBitmap = BitmapFactory.decodeFile(imagePath, ops);

            defaultWHRatio = atlasBitmap.getWidth() / (1.0f * atlasBitmap.getHeight());

            allocate(imagePath, atlasBitmap);
            atlasReferenceItem = glestMap.get(imagePath);
            atlasBitmap.recycle();

        }
        else
        {
            atlasReferenceItem.refCount++; // Increment shape reference count
        }

        loadedImageFigure = new ImageFigure(imagePath);
        loadedImageFigure.setDefaultWHRatio(defaultWHRatio);
        loadedImageFigure.setTexCoords(atlasReferenceItem.coords);
        loadedImageFigure.setDefaultPivot(defaultPivot);
        loadedImageFigure.setTexSampler(atlasReferenceItem.sampler);
        return loadedImageFigure;
    }

    /**
     * Frees an image-representative texture into OpenGL's texture memory.
     * @param imagePath the path to the image in Android's file system or content management system
     */
    public static void unloadImage(String imagePath)
    {
        free(imagePath);
    }

    /**
     * Converts a numeric, 1-dimensional index into 2-dimensional index.
     * This is used for exploiting the indexing of textures in order to place them within a 2D texture space.
     * @param idx the 1-dimensional index
     * @return the 2-dimensional index
     */
    private static int[] index2D(int idx)
    {
        int x = 0;
        int y = 0;

        int iter = 0;
        int addend = 1;

        while (idx > 0)
        {
            int bit = idx & 1;

            if (iter % 2 == 0)
            {
                y += (bit == 1) ? addend : 0;
            }
            else
            {
                x += (bit == 1) ? addend : 0;
                addend *= 2;
            }

            iter++;
            idx = idx >> 1;
        }

        return new int[]{x, y};
    }

    /**
     * Grows the texture heap space by one large, empty block of texture space,
     * and associates this new space with a given texture sampler ID.
     * @param samplerId the associative texture sampler ID
     */
    private static void growSpace(int samplerId)
    {
        int[] newTextureId = new int[1];
        textureIds.set(samplerId, newTextureId);

        GLES20.glGenTextures(1, textureIds.get(samplerId), 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + samplerId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds.get(samplerId)[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        Bitmap textureSpaceBitmap = Bitmap.createBitmap(MAX_TEXTURE_SPACE_SIZE, MAX_TEXTURE_SPACE_SIZE, Bitmap.Config.ARGB_8888);
        textureSpaceBitmap.eraseColor(0xFF000000); // Black (100% Opacity)
        //GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureSpaceBitmap,0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, textureSpaceBitmap, GLES20.GL_UNSIGNED_BYTE, 0);
        textureSpaceBitmap.recycle();
    }

    /**
     * Loads all metadata regarding the unique attributes of different shapes.
     * @return 0 if successful, 1 if not
     */
    private static int loadShapeAtlasMetadata()
    {
        try
        {
            AssetManager am = App.getApplication().getAssets();
            String atlasMetadataFilename = "shapes/shapes.json";

            InputStreamReader atlasMetadataReader = new InputStreamReader(am.open(atlasMetadataFilename));
            StringBuilder atlasMetadataBuilder = new StringBuilder();

            char[] buf = new char[1024];
            int read = 0;

            while ((read = atlasMetadataReader.read(buf)) != -1)
            {
                atlasMetadataBuilder.append(buf);
            }

            shapeAtlasMetadata = new JSONObject(atlasMetadataBuilder.toString());
            shapeAtlasMetadataFilename = atlasMetadataFilename;
        }
        catch (IOException e)
        {
            //Log.e("GLESTBAM", "loadShapeAtlasMetadata() : " + e.getMessage());
            return 1;
        }
        catch (JSONException e)
        {
            //Log.e("GLESTBAM", "loadShapeAtlasMetadata() : " + e.getMessage());
            return 1;
        }

        return 0;
    }

    /**
     * Loads all metadata regarding the unique attributes of different font glyphs (i.e. characters).
     * @return 0 if successful, 1 if not
     */
    private static int loadGlyphAtlasMetadata(String font)
    {
        try
        {
            AssetManager am = App.getApplication().getAssets();
            String atlasMetadataFilename = "glyphs/" + font + "/glyphs.json";

            InputStreamReader atlasMetadataReader = new InputStreamReader(am.open(atlasMetadataFilename));
            StringBuilder atlasMetadataBuilder = new StringBuilder();

            char[] buf = new char[1024];
            int read = 0;

            while ((read = atlasMetadataReader.read(buf)) != -1)
            {
                atlasMetadataBuilder.append(buf);
            }

            glyphAtlasMetadata = new JSONObject(atlasMetadataBuilder.toString());
            glyphAtlasMetadataFilename = atlasMetadataFilename;
        }
        catch (IOException e)
        {
            //Log.e("GLESTBAM", "loadGlyphAtlasMetadata() : " + e.getMessage());
            return 1;
        }
        catch (JSONException e)
        {
            //Log.e("GLESTBAM", "loadGlyphAtlasMetadata() : " + e.getMessage());
            return 1;
        }

        return 0;
    }
}
