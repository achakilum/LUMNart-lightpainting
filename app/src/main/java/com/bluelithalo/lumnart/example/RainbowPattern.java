package com.bluelithalo.lumnart.example;

import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.pattern.Stage;

public class RainbowPattern extends Pattern
{
    private float[] redVec;
    private float[] orangeVec;
    private float[] yellowVec;
    private float[] greenVec;
    private float[] blueVec;
    private float[] purpleVec;

    public RainbowPattern()
    {
        super();

        redVec = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
        orangeVec = new float[]{1.0f, 0.5f, 0.0f, 1.0f};
        yellowVec = new float[]{1.0f, 1.0f, 0.0f, 1.0f};
        greenVec = new float[]{0.0f, 1.0f, 0.0f, 1.0f};
        blueVec = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
        purpleVec = new float[]{0.5f, 0.0f, 0.5f, 1.0f};

        setName("Rainbow Pathway");
        setAuthor("Ashwin Chakilum");

        setup();
    }

    private void setup()
    {
        int timeScale = 1;
        int a = 20;
        int b = 30 - a;

        Stage redStage = new Stage(4, a*timeScale);
        redStage.setStartVector(redVec);
        redStage.setEndVector(redVec);
        redStage.setTransitionCurve(Stage.Transition.Linear);

        Stage roStage = new Stage(4, b*timeScale);
        roStage.setStartVector(redVec);
        roStage.setEndVector(orangeVec);
        roStage.setTransitionCurve(Stage.Transition.Linear);

        Stage orangeStage = new Stage(4, a*timeScale);
        orangeStage.setStartVector(orangeVec);
        orangeStage.setEndVector(orangeVec);
        orangeStage.setTransitionCurve(Stage.Transition.Linear);

        Stage oyStage = new Stage(4, b*timeScale);
        oyStage.setStartVector(orangeVec);
        oyStage.setEndVector(yellowVec);
        oyStage.setTransitionCurve(Stage.Transition.Linear);

        Stage yellowStage = new Stage(4, a*timeScale);
        yellowStage.setStartVector(yellowVec);
        yellowStage.setEndVector(yellowVec);
        yellowStage.setTransitionCurve(Stage.Transition.Linear);

        Stage ygStage = new Stage(4, b*timeScale);
        ygStage.setStartVector(yellowVec);
        ygStage.setEndVector(greenVec);
        ygStage.setTransitionCurve(Stage.Transition.Linear);

        Stage greenStage = new Stage(4, a*timeScale);
        greenStage.setStartVector(greenVec);
        greenStage.setEndVector(greenVec);
        greenStage.setTransitionCurve(Stage.Transition.Linear);

        Stage gbStage = new Stage(4, b*timeScale);
        gbStage.setStartVector(greenVec);
        gbStage.setEndVector(blueVec);
        gbStage.setTransitionCurve(Stage.Transition.Linear);

        Stage blueStage = new Stage(4, a*timeScale);
        blueStage.setStartVector(blueVec);
        blueStage.setEndVector(blueVec);
        blueStage.setTransitionCurve(Stage.Transition.Linear);

        Stage bpStage = new Stage(4, b*timeScale);
        bpStage.setStartVector(blueVec);
        bpStage.setEndVector(purpleVec);
        bpStage.setTransitionCurve(Stage.Transition.Linear);

        Stage purpleStage = new Stage(4, a*timeScale);
        purpleStage.setStartVector(purpleVec);
        purpleStage.setEndVector(purpleVec);
        purpleStage.setTransitionCurve(Stage.Transition.Linear);

        Stage prStage = new Stage(4, b*timeScale);
        prStage.setStartVector(purpleVec);
        prStage.setEndVector(redVec);
        prStage.setTransitionCurve(Stage.Transition.Linear);

        Stage staticDimensStage = new Stage(2, 120);
        staticDimensStage.setStartVector(new float[]{0.015f, 1.0f});
        staticDimensStage.setEndVector(new float[]{0.015f, 1.0f});
        staticDimensStage.setTransitionCurve(Stage.Transition.None);

        Stage staticPosStage = new Stage(2, 120);
        staticPosStage.setStartVector(new float[]{-0.15f, 0.0f});
        staticPosStage.setEndVector(new float[]{-0.15f, 0.0f});
        staticPosStage.setTransitionCurve(Stage.Transition.None);

        Layer layer = getLayer(0);
        Section section = layer.getSection(0);
        Light light = section.getLight(0);
        light.setFigure("rectangle");

        Property color = light.getProperty(Property.Type.Color);
        Property dimensions = light.getProperty(Property.Type.Dimensions);
        Property position = light.getProperty(Property.Type.Position);

        color.setStage(redStage, 0);
        color.insertStage(roStage);
        color.insertStage(orangeStage);
        color.insertStage(oyStage);
        color.insertStage(yellowStage);
        color.insertStage(ygStage);
        color.insertStage(greenStage);
        color.insertStage(gbStage);
        color.insertStage(blueStage);
        color.insertStage(bpStage);
        color.insertStage(purpleStage);
        color.insertStage(prStage);

        dimensions.setStage(staticDimensStage, 0);
        position.setStage(staticPosStage, 0);
    }
}
