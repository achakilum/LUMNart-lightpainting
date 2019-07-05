package com.bluelithalo.lumnart.example;

import com.bluelithalo.lumnart.App;
import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.pattern.Stage;

public class ScreenBoundsPattern extends Pattern
{
    public ScreenBoundsPattern()
    {
        super();

        setName("Screen Bounds");
        setAuthor("Ashwin Chakilum");

        setup();
    }

    private void setup()
    {
        Layer bgLayer = this.getLayer(0);
        Section bgSection = bgLayer.getSection(0);
        bgSection.removeLight(0);

        bgSection.insertLight();
        Light bgLeft = bgSection.getLight(0);
        bgLeft.setFigure("rectangle");
        bgLeft.setUniformDimensions(false);

        bgSection.insertLight();
        Light bgRight = bgSection.getLight(1);
        bgRight.setFigure("rectangle");
        bgRight.setUniformDimensions(false);
        
        Property colorLeft = bgLeft.getProperty(Property.Type.Color);
        Property dimensionsLeft = bgLeft.getProperty(Property.Type.Dimensions);
        Property positionLeft = bgLeft.getProperty(Property.Type.Position);
        Property colorRight = bgRight.getProperty(Property.Type.Color);
        Property dimensionsRight = bgRight.getProperty(Property.Type.Dimensions);
        Property positionRight = bgRight.getProperty(Property.Type.Position);

        float[] colorBGVec = new float[]{0.3f, 0.0f, 0.0f, 0.9f};

        Stage colorBGStage = new Stage(4, 60);
        colorBGStage.setStartVector(colorBGVec);
        colorBGStage.setEndVector(colorBGVec);
        colorBGStage.setTransitionCurve(Stage.Transition.None);

        Stage dimensionsBGStage = new Stage(2, 60);
        dimensionsBGStage.setStartVector(new float[]{1.0f, 1.0f});
        dimensionsBGStage.setEndVector(new float[]{1.0f, 1.0f});
        dimensionsBGStage.setTransitionCurve(Stage.Transition.None);

        Stage positionBGLeftStage = new Stage(2, 60);
        positionBGLeftStage.setStartVector(new float[]{-(0.5f + (App.getAspectRatio(true) / 2.0f)), 0.0f});
        positionBGLeftStage.setEndVector(new float[]{-(0.5f + (App.getAspectRatio(true) / 2.0f)), 0.0f});
        positionBGLeftStage.setTransitionCurve(Stage.Transition.None);

        Stage positionBGRightStage = new Stage(2, 60);
        positionBGRightStage.setStartVector(new float[]{(0.5f + (App.getAspectRatio(true) / 2.0f)), 0.0f});
        positionBGRightStage.setEndVector(new float[]{(0.5f + (App.getAspectRatio(true) / 2.0f)), 0.0f});
        positionBGRightStage.setTransitionCurve(Stage.Transition.None);

        colorLeft.setStage(colorBGStage, 0);
        dimensionsLeft.setStage(dimensionsBGStage, 0);
        colorRight.setStage(colorBGStage, 0);
        dimensionsRight.setStage(dimensionsBGStage, 0);
        positionLeft.setStage(positionBGLeftStage, 0);
        positionRight.setStage(positionBGRightStage, 0);
    }
}
