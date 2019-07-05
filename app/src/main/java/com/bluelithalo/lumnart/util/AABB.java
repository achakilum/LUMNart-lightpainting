package com.bluelithalo.lumnart.util;

/**
 * An axis-aligned bounding box.
 * It is defined as a space that is reachable within the horizontal bounds of its minimum and maximum x-values,
 * and the vertical bounds of its minimum and maximum y-values.
 */
public class AABB
{
    private float xMin;
    private float xMax;
    private float yMin;
    private float yMax;

    /**
     * Construct an axis-aligned bounding box with a width and height of 1, centered around the origin.
     */
    public AABB()
    {
        this.xMin = -0.5f;
        this.xMax = 0.5f;
        this.yMin = -0.5f;
        this.yMax = 0.5f;
    }

    /**
     * Constructs an axis-aligned bounding box with custom bounds.
     * @param newXMin the minimum x-bound
     * @param newXMax the maximum x-bound
     * @param newYMin the minimum y-bound
     * @param newYMax the maximum y-bound
     */
    public AABB(float newXMin, float newXMax, float newYMin, float newYMax)
    {
        this.xMin = newXMin;
        this.xMax = newXMax;
        this.yMin = newYMin;
        this.yMax = newYMax;
    }

    /**
     * Constructs an axis-aligned bounding box that copies the attributes of another axis-aligned bounding box.
     * @param copy the axis-aligned bounding box to copy from
     */
    public AABB(AABB copy)
    {
        this.xMin = copy.getMinimumX();
        this.xMax = copy.getMaximumX();
        this.yMin = copy.getMinimumY();
        this.yMax = copy.getMaximumY();
    }

    /**
     * Retrieves the minimum x-bound.
     * @return the minimum x-bound
     */
    public float getMinimumX()
    {
        return xMin;
    }

    /**
     * Sets a new minimum x-bound.
     * @param newXMin the new minimum x-bound
     */
    public void setMinimumX(float newXMin)
    {
        this.xMin = newXMin;
    }

    /**
     * Retrieves the maximum x-bound.
     * @return the maximum x-bound
     */
    public float getMaximumX()
    {
        return xMax;
    }

    /**
     * Sets a new maximum x-bound.
     * @param newXMax the new maximum x-bound
     */
    public void setMaximumX(float newXMax)
    {
        this.xMax = newXMax;
    }

    /**
     * Retrieves the minimum y-bound.
     * @return the minimum y-bound
     */
    public float getMinimumY()
    {
        return yMin;
    }

    /**
     * Sets a new minimum y-bound.
     * @param newYMin the new minimum y-bound
     */
    public void setMinimumY(float newYMin)
    {
        this.yMin = newYMin;
    }

    /**
     * Retrieves the maximum y-bound.
     * @return the maximum y-bound
     */
    public float getMaximumY()
    {
        return yMax;
    }

    /**
     * Sets a new maximum y-bound.
     * @param newYMax the new maximum y-bound
     */
    public void setMaximumY(float newYMax)
    {
        this.yMax = newYMax;
    }

    @Override
    public String toString()
    {
        StringBuilder aabbStringBuilder = new StringBuilder();

        aabbStringBuilder.append("(X: [" + xMin + "," + xMax + "], ");
        aabbStringBuilder.append("Y: [" + yMin + "," + yMax + "])");

        return aabbStringBuilder.toString();
    }
}
