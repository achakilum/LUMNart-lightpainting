package com.bluelithalo.lumnart.pattern;

import com.bluelithalo.lumnart.util.GLESTBAM;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Texture metadata storage for images. This will be used to render light sources as ordinary images.
 */
public class ImageFigure extends Figure
{
    /**
     * Constructs a default figure that will represent image metadata.
     * An image file path must be specified for it to be referenced in GLESTBAM, allocated, and eventually rendered
     */
    public ImageFigure(String imagePath)
    {
        super(imagePath);
        this.setFigureType(Type.IMAGE);
    }

    /**
     * Constructs an image-representing figure that copies the attributes of another image-representing figure.
     * @param copy the image-representing figure to copy from
     */
    public ImageFigure(ImageFigure copy)
    {
        super(copy);
        this.setFigureType(Type.IMAGE);
    }

    /**
     * Constructs an image-representing figure by reading JSON metadata from a JSON container object.
     * @param imageFigureObj the JSON metadata container object
     */
    public ImageFigure(JSONObject imageFigureObj) throws JSONException
    {
        super(imageFigureObj);
        this.setFigureType(Type.IMAGE);
    }

    /**
     * Prepares the image-representing figure to be rendered by loading it and its true metadata in the GLESTBAM texture manager,
     * then storing the metadata.
     */
    public void prepare()
    {
        if (this.isPrepared())
        {
            GLESTBAM.unloadImage(this.getPreparedIdentifier());
            this.setPrepared(false);
        }

        ImageFigure loadedImageFigure = GLESTBAM.loadImage(this.getImagePath());
        this.setDefaultWHRatio(loadedImageFigure.getDefaultWHRatio());
        this.setTexCoords(loadedImageFigure.getTexCoords());
        this.setDefaultPivot(loadedImageFigure.getDefaultPivot());
        this.setTexSampler(loadedImageFigure.getTexSampler());

        if (this.isUsingDefaultPivot())
        {
            this.setPivot(this.getDefaultPivot());
        }

        this.initializeBuffer();
        this.updateBuffer();
        this.markAsPrepared();
    }

    public void clean()
    {
        if (!this.isPrepared())
        {
            return;
        }

        updateBuffer(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
        GLESTBAM.unloadImage(this.getPreparedIdentifier());
        this.setPrepared(false);
    }

    /**
     * Retrieves the image file path (exactly the same as its identifier).
     * @return the image file path
     */
    public String getImagePath()
    {
        return this.getIdentifier();
    }

    /**
     * Sets the image file path (exactly the same as its identifier).
     * @param newImagePath the new image file path
     */
    public void setImagePath(String newImagePath)
    {
        this.setIdentifier(newImagePath);
    }

}
