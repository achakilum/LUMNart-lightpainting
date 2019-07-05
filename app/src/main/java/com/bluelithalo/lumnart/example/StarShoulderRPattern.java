package com.bluelithalo.lumnart.example;

import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.pattern.Stage;

public class StarShoulderRPattern extends Pattern
{
    public StarShoulderRPattern()
    {
        super();

        setName("Right Star Shoulder");
        setAuthor("Ashwin Chakilum");

        setup();
    }

    private void setup()
    {
        float starSpacing = 0.1575f;
        int translationTime = 120;
        float gapSize = 0.0025f;
        String figureIdentifier = "star5ptround";

        // Star rim
        Layer starRimLayer = this.getLayer(0);

        Section starRimSection = starRimLayer.getSection(0);
        starRimSection.setFitting(false);
        starRimSection.getBoundingBox().setMaximumY(gapSize);
        starRimSection.getBoundingBox().setMinimumY(-gapSize);

        Light starRim0 = starRimSection.getLight(0);

        starRim0.setUniformDimensions(true);
        starRim0.setFigure(figureIdentifier);

        Property colorRim0 = starRim0.getProperty(Property.Type.Color);
        Property positionRim0 = starRim0.getProperty(Property.Type.Position);
        Property dimensionsRim0 = starRim0.getProperty(Property.Type.Dimensions);
        Property angleRim0 = starRim0.getProperty(Property.Type.Angle);

        Stage colorStageRim0 = new Stage(4, translationTime);
        colorStageRim0.setStartVector(new float[]{1.0f, 1.0f, 0.0f, 1.0f});
        colorStageRim0.setEndVector(new float[]{1.0f, 1.0f, 0.0f, 1.0f});
        colorStageRim0.setTransitionCurve(Stage.Transition.Linear);

        Stage positionStageRim0 = new Stage(2, translationTime);
        positionStageRim0.setStartVector(new float[]{-0.21f, starSpacing});
        positionStageRim0.setEndVector(new float[]{-0.21f, -starSpacing});
        positionStageRim0.setTransitionCurve(Stage.Transition.Linear);

        Stage dimensionsStageRim0 = new Stage(2, translationTime);
        dimensionsStageRim0.setStartVector(new float[]{0.15f, 0.15f});
        dimensionsStageRim0.setEndVector(new float[]{0.15f, 0.15f});
        dimensionsStageRim0.setTransitionCurve(Stage.Transition.Linear);

        Stage angleStageRim0 = new Stage(1, translationTime);
        angleStageRim0.setStartVector(new float[]{90.0f});
        angleStageRim0.setEndVector(new float[]{90.0f});
        angleStageRim0.setTransitionCurve(Stage.Transition.Linear);

        colorRim0.setStage(colorStageRim0, 0);
        positionRim0.setStage(positionStageRim0, 0);
        dimensionsRim0.setStage(dimensionsStageRim0, 0);
        angleRim0.setStage(angleStageRim0, 0);

        Light starRim1 = new Light(starRim0);
        Property positionRim1 = starRim1.getProperty(Property.Type.Position);

        Stage positionStageRim1 = new Stage(2, translationTime);
        positionStageRim1.setStartVector(new float[]{-0.21f, 3 * starSpacing});
        positionStageRim1.setEndVector(new float[]{-0.21f, starSpacing});
        positionStageRim1.setTransitionCurve(Stage.Transition.Linear);

        positionRim1.setStage(positionStageRim1, 0);
        starRimSection.insertLight(starRim1);

        // Star hole
        this.insertLayer();

        Layer starHoleLayer = this.getLayer(1);
        Section starHoleSection = starHoleLayer.getSection(0);
        Light starHole0 = starHoleSection.getLight(0);

        starHole0.setUniformDimensions(true);
        starHole0.setFigure(figureIdentifier);

        Property colorHole0 = starHole0.getProperty(Property.Type.Color);
        Property positionHole0 = starHole0.getProperty(Property.Type.Position);
        Property dimensionsHole0 = starHole0.getProperty(Property.Type.Dimensions);
        Property angleHole0 = starHole0.getProperty(Property.Type.Angle);

        Stage colorStageHole0 = new Stage(4, translationTime);
        colorStageHole0.setStartVector(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
        colorStageHole0.setEndVector(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
        colorStageHole0.setTransitionCurve(Stage.Transition.Linear);

        Stage positionStageHole0 = new Stage(2, translationTime);
        positionStageHole0.setStartVector(new float[]{-0.21f, starSpacing});
        positionStageHole0.setEndVector(new float[]{-0.21f, -starSpacing});
        positionStageHole0.setTransitionCurve(Stage.Transition.Linear);

        Stage dimensionsStageHole0 = new Stage(2, translationTime);
        dimensionsStageHole0.setStartVector(new float[]{0.105f, 0.105f});
        dimensionsStageHole0.setEndVector(new float[]{0.105f, 0.105f});
        dimensionsStageHole0.setTransitionCurve(Stage.Transition.Linear);

        Stage angleStageHole0 = new Stage(1, translationTime);
        angleStageHole0.setStartVector(new float[]{90.0f});
        angleStageHole0.setEndVector(new float[]{90.0f});
        angleStageHole0.setTransitionCurve(Stage.Transition.Linear);

        colorHole0.setStage(colorStageHole0, 0);
        positionHole0.setStage(positionStageHole0, 0);
        dimensionsHole0.setStage(dimensionsStageHole0, 0);
        angleHole0.setStage(angleStageHole0, 0);

        Light starHole1 = new Light(starHole0);
        Property positionHole1 = starHole1.getProperty(Property.Type.Position);

        Stage positionStageHole1 = new Stage(2, translationTime);
        positionStageHole1.setStartVector(new float[]{-0.21f, 3 * starSpacing});
        positionStageHole1.setEndVector(new float[]{-0.21f, starSpacing});
        positionStageHole1.setTransitionCurve(Stage.Transition.Linear);

        positionHole1.setStage(positionStageHole1, 0);
        starHoleSection.insertLight(starHole1);


    }
}
