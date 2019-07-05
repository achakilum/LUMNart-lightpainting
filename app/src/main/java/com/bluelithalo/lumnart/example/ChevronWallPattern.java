package com.bluelithalo.lumnart.example;

import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.pattern.Stage;

public class ChevronWallPattern extends Pattern
{
    public ChevronWallPattern()
    {
        super();

        setName("Chevron Wall");
        setAuthor("Ashwin Chakilum");

        setup();
    }

    private void setup()
    {
        float chevronSpacing = 0.666f;
        int translationTime = 120;
        float gapSize = 0.01f;

        Layer chevronLayer = this.getLayer(0);

        Section chevronSection = chevronLayer.getSection(0);
        chevronSection.setFitting(false);
        chevronSection.getBoundingBox().setMaximumY(0.005f);
        chevronSection.getBoundingBox().setMinimumY(-0.005f);

        Light chevron0 = chevronSection.getLight(0);

        chevron0.setUniformDimensions(true);
        chevron0.setFigure("arrowchevron");

        Property color = chevron0.getProperty(Property.Type.Color);
        Property position0 = chevron0.getProperty(Property.Type.Position);
        Property dimensions = chevron0.getProperty(Property.Type.Dimensions);
        Property angle = chevron0.getProperty(Property.Type.Angle);

        Stage colorStage = new Stage(4, translationTime);
        colorStage.setStartVector(new float[]{1.0f, 1.0f, 0.0f, 1.0f});
        colorStage.setEndVector(new float[]{1.0f, 1.0f, 0.0f, 1.0f});
        colorStage.setTransitionCurve(Stage.Transition.Linear);

        Stage positionStage0 = new Stage(2, translationTime);
        positionStage0.setStartVector(new float[]{0.0f, chevronSpacing});
        positionStage0.setEndVector(new float[]{0.0f, -chevronSpacing});
        positionStage0.setTransitionCurve(Stage.Transition.Linear);

        Stage dimensionsStage = new Stage(2, translationTime);
        dimensionsStage.setStartVector(new float[]{0.5f, 0.5f});
        dimensionsStage.setEndVector(new float[]{0.5f, 0.5f});
        dimensionsStage.setTransitionCurve(Stage.Transition.Linear);

        Stage angleStage = new Stage(1, translationTime);
        angleStage.setStartVector(new float[]{90.0f});
        angleStage.setEndVector(new float[]{90.0f});
        angleStage.setTransitionCurve(Stage.Transition.Linear);

        color.setStage(colorStage, 0);
        position0.setStage(positionStage0, 0);
        dimensions.setStage(dimensionsStage, 0);
        angle.setStage(angleStage, 0);

        Light chevron1 = new Light(chevronSection.getLight(0));

        Stage positionStage1 = new Stage(2, translationTime);
        positionStage1.setStartVector(new float[]{0.0f, 3 * chevronSpacing});
        positionStage1.setEndVector(new float[]{0.0f, 1 * chevronSpacing});
        positionStage1.setTransitionCurve(Stage.Transition.Linear);

        Property position1 = chevron1.getProperty(Property.Type.Position);
        position1.setStage(positionStage1, 0);

        chevronSection.insertLight(chevron1);
    }
}
