package com.bluelithalo.lumnart.example;

import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.pattern.Stage;

public class VioletSpiralPattern extends Pattern
{
    public VioletSpiralPattern()
    {
        super();

        setName("Violet Spiral");
        setAuthor("Ashwin Chakilum");

        setup();
    }

    private void setup()
    {
        int transitionTime = 120;

        Layer spiralLayer = this.getLayer(0);
        Section spiralSection = spiralLayer.getSection(0);
        spiralSection.removeLight(0);

        spiralSection.insertLight();
        Light strand1 = spiralSection.getLight(0);
        strand1.setFigure("ellipse");
        strand1.getFigure().setIsUsingDefaultPivot(false);
        strand1.getFigure().setPivot(0.0f, 1.5f);

        spiralSection.insertLight();
        Light strand2 = spiralSection.getLight(1);
        strand2.setFigure("ellipse");
        strand2.getFigure().setIsUsingDefaultPivot(false);
        strand2.getFigure().setPivot(0.0f, -1.5f);

        Property color1 = strand1.getProperty(Property.Type.Color);
        Property dimensions1 = strand1.getProperty(Property.Type.Dimensions);
        Property angle1 = strand1.getProperty(Property.Type.Angle);

        Property color2 = strand2.getProperty(Property.Type.Color);
        Property dimensions2 = strand2.getProperty(Property.Type.Dimensions);
        Property angle2 = strand2.getProperty(Property.Type.Angle);

        float[] colorVec = new float[]{0.5f, 0.0f, 1.0f, 1.0f};

        Stage colorStage = new Stage(4, transitionTime);
        colorStage.setStartVector(colorVec);
        colorStage.setEndVector(colorVec);
        colorStage.setTransitionCurve(Stage.Transition.Linear);

        Stage dimensionsStage = new Stage(2, transitionTime);
        dimensionsStage.setStartVector(new float[]{0.125f, 0.125f});
        dimensionsStage.setEndVector(new float[]{0.125f, 0.125f});
        dimensionsStage.setTransitionCurve(Stage.Transition.Linear);

        Stage angleStage = new Stage(1, transitionTime);
        angleStage.setStartVector(new float[]{0.0f});
        angleStage.setEndVector(new float[]{360.0f});
        angleStage.setTransitionCurve(Stage.Transition.Linear);

        color1.setStage(new Stage(colorStage), 0);
        dimensions1.setStage(new Stage(dimensionsStage), 0);
        angle1.setStage(new Stage(angleStage), 0);

        color2.setStage(new Stage(colorStage), 0);
        dimensions2.setStage(new Stage(dimensionsStage), 0);
        angle2.setStage(new Stage(angleStage), 0);
    }
}
