package com.bluelithalo.lumnart.example;

import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.pattern.Stage;
import com.bluelithalo.lumnart.pattern.TextFigure;

public class SampleTextPattern extends Pattern
{
    private float[] redVec;
    private float[] orangeVec;
    private float[] yellowVec;
    private float[] greenVec;
    private float[] blueVec;
    private float[] purpleVec;

    public SampleTextPattern()
    {
        super();

        redVec = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
        orangeVec = new float[]{1.0f, 0.5f, 0.0f, 1.0f};
        yellowVec = new float[]{1.0f, 1.0f, 0.0f, 1.0f};
        greenVec = new float[]{0.0f, 1.0f, 0.0f, 1.0f};
        blueVec = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
        purpleVec = new float[]{1.0f, 0.0f, 1.0f, 1.0f};

        setName("Sample Text");
        setAuthor("Ashwin Chakilum");

        this.setup();
    }

    private void setup()
    {
        int timeScale = 3;

        Layer textLayer = this.getLayer(0);
        Section textSection = textLayer.getSection(0);
        textSection.removeLight(0);

        textSection.insertLight();
        Light textLight = textSection.getLight(0);

        TextFigure text = new TextFigure("Spinning sample text!", "Arvo-Regular");
        text.setIsUsingDefaultPivot(false);
        text.setPivot(0.0f, 0.25f);
        text.setAlignment(TextFigure.Alignment.CENTER);
        textLight.setFigure(text);

        Property dimensions = textLight.getProperty(Property.Type.Dimensions);
        Property angle = textLight.getProperty(Property.Type.Angle);
        Property position = textLight.getProperty(Property.Type.Position);
        Property color = textLight.getProperty(Property.Type.Color);

        Stage dimensionsStage1 = new Stage(2, 30*timeScale);
        dimensionsStage1.setStartVector(new float[]{0.05f, 0.10f});
        dimensionsStage1.setEndVector(new float[]{0.10f, 0.05f});
        dimensionsStage1.setTransitionCurve(Stage.Transition.Linear);
        dimensions.setStage(new Stage(dimensionsStage1), 0);

        Stage dimensionsStage2 = new Stage(2, 30*timeScale);
        dimensionsStage2.setStartVector(new float[]{0.10f, 0.05f});
        dimensionsStage2.setEndVector(new float[]{0.05f, 0.10f});
        dimensionsStage2.setTransitionCurve(Stage.Transition.Linear);
        dimensions.insertStage(new Stage(dimensionsStage2));

        Stage angleStage1 = new Stage(1, 30*timeScale);
        angleStage1.setStartVector(new float[]{0.0f});
        angleStage1.setEndVector(new float[]{360.0f});
        angleStage1.setTransitionCurve(Stage.Transition.Sinusoidal);
        angle.setStage(new Stage(angleStage1), 0);

        Stage angleStage2 = new Stage(1, 30*timeScale);
        angleStage2.setStartVector(new float[]{360.0f});
        angleStage2.setEndVector(new float[]{-360.0f});
        angleStage2.setTransitionCurve(Stage.Transition.Sinusoidal);
        angle.insertStage(angleStage2);

        Stage positionStage1 = new Stage(2, 45*timeScale);
        positionStage1.setStartVector(new float[]{0.0f, 0.15f});
        positionStage1.setEndVector(new float[]{0.0f, -0.15f});
        positionStage1.setTransitionCurve(Stage.Transition.Sinusoidal);
        position.setStage(new Stage(positionStage1), 0);

        Stage positionStage2 = new Stage(2, 45*timeScale);
        positionStage2.setStartVector(new float[]{0.0f, -0.15f});
        positionStage2.setEndVector(new float[]{0.0f, 0.15f});
        positionStage2.setTransitionCurve(Stage.Transition.Sinusoidal);
        position.insertStage(positionStage2);

        int a = 4;
        int b = 11 - a;

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


    }
}
