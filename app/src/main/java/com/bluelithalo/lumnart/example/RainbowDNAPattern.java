package com.bluelithalo.lumnart.example;

import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.pattern.Stage;

public class RainbowDNAPattern extends Pattern
{
    private float[] redVec;
    private float[] greenVec;
    private float[] blueVec;
    private float[] cyanVec;
    private float[] magentaVec;
    private float[] yellowVec;
    private float[] blankVec;

    public RainbowDNAPattern()
    {
        super();

        redVec = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
        greenVec = new float[]{0.0f, 1.0f, 0.0f, 1.0f};
        blueVec = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
        cyanVec = new float[]{0.0f, 1.0f, 1.0f, 1.0f};
        magentaVec = new float[]{1.0f, 0.0f, 1.0f, 1.0f};
        yellowVec = new float[]{1.0f, 1.0f, 0.0f, 1.0f};
        blankVec = new float[]{0.0f, 0.0f, 0.0f, 0.0f};

        setName("Rainbow DNA");
        setAuthor("Ashwin Chakilum");

        setup();
    }

    private void setup()
    {
        removeLayer(0);
        int timeScale = 2;

        Stage baseRedStage = new Stage(4, 1*timeScale);
        baseRedStage.setStartVector(redVec);
        baseRedStage.setEndVector(redVec);
        baseRedStage.setTransitionCurve(Stage.Transition.Linear);

        Stage baseGreenStage = new Stage(4, 1*timeScale);
        baseGreenStage.setStartVector(greenVec);
        baseGreenStage.setEndVector(greenVec);
        baseGreenStage.setTransitionCurve(Stage.Transition.Linear);

        Stage baseBlueStage = new Stage(4, 1*timeScale);
        baseBlueStage.setStartVector(blueVec);
        baseBlueStage.setEndVector(blueVec);
        baseBlueStage.setTransitionCurve(Stage.Transition.Linear);

        Stage baseCyanStage = new Stage(4, 1*timeScale);
        baseCyanStage.setStartVector(cyanVec);
        baseCyanStage.setEndVector(cyanVec);
        baseCyanStage.setTransitionCurve(Stage.Transition.Linear);

        Stage baseMagentaStage = new Stage(4, 1*timeScale);
        baseMagentaStage.setStartVector(magentaVec);
        baseMagentaStage.setEndVector(magentaVec);
        baseMagentaStage.setTransitionCurve(Stage.Transition.Linear);

        Stage baseYellowStage = new Stage(4, 1*timeScale);
        baseYellowStage.setStartVector(yellowVec);
        baseYellowStage.setEndVector(yellowVec);
        baseYellowStage.setTransitionCurve(Stage.Transition.Linear);

        Stage baseBlankStage = new Stage(4, 9*timeScale);
        baseBlankStage.setStartVector(blankVec);
        baseBlankStage.setEndVector(blankVec);
        baseBlankStage.setTransitionCurve(Stage.Transition.Linear);

        //

        Stage strandRYStage = new Stage(4, 30*timeScale);
        strandRYStage.setStartVector(redVec);
        strandRYStage.setEndVector(yellowVec);
        strandRYStage.setTransitionCurve(Stage.Transition.Linear);

        Stage strandYGStage = new Stage(4, 30*timeScale);
        strandYGStage.setStartVector(yellowVec);
        strandYGStage.setEndVector(greenVec);
        strandYGStage.setTransitionCurve(Stage.Transition.Linear);

        Stage strandGCStage = new Stage(4, 30*timeScale);
        strandGCStage.setStartVector(greenVec);
        strandGCStage.setEndVector(cyanVec);
        strandGCStage.setTransitionCurve(Stage.Transition.Linear);

        Stage strandCBStage = new Stage(4, 30*timeScale);
        strandCBStage.setStartVector(cyanVec);
        strandCBStage.setEndVector(blueVec);
        strandCBStage.setTransitionCurve(Stage.Transition.Linear);

        Stage strandBMStage = new Stage(4, 30*timeScale);
        strandBMStage.setStartVector(blueVec);
        strandBMStage.setEndVector(magentaVec);
        strandBMStage.setTransitionCurve(Stage.Transition.Linear);

        Stage strandMRStage = new Stage(4, 30*timeScale);
        strandMRStage.setStartVector(magentaVec);
        strandMRStage.setEndVector(redVec);
        strandMRStage.setTransitionCurve(Stage.Transition.Linear);

        Layer baseLayer = new Layer();
        Section baseSection = new Section();
        Light base1 = new Light();
        Light base2 = new Light();

        base1.setFigure("rectangle");
        base2.setFigure("rectangle");
        base1.getFigure().setIsUsingDefaultPivot(false);
        base2.getFigure().setIsUsingDefaultPivot(false);
        base1.getFigure().setPivot(new float[]{0.0f, -0.5f});
        base2.getFigure().setPivot(new float[]{0.0f, 0.5f});

        Property base1Color = base1.getProperty(Property.Type.Color);
        Property base1Position = base1.getProperty(Property.Type.Position);
        Property base1Dimensions = base1.getProperty(Property.Type.Dimensions);
        Property base1Angle = base1.getProperty(Property.Type.Angle);

        Property base2Color = base2.getProperty(Property.Type.Color);
        Property base2Position = base2.getProperty(Property.Type.Position);
        Property base2Dimensions = base2.getProperty(Property.Type.Dimensions);
        Property base2Angle = base2.getProperty(Property.Type.Angle);

        base1Color.setStage(new Stage(baseRedStage), 0); //1
        base1Color.insertStage(new Stage(baseBlankStage));

        base1Color.insertStage(new Stage(baseGreenStage)); //2
        base1Color.insertStage(new Stage(baseBlankStage));

        base1Color.insertStage(new Stage(baseBlueStage)); //3
        base1Color.insertStage(new Stage(baseBlankStage));

        base1Color.insertStage(new Stage(baseCyanStage)); //4
        base1Color.insertStage(new Stage(baseBlankStage));

        base1Color.insertStage(new Stage(baseMagentaStage)); //5
        base1Color.insertStage(new Stage(baseBlankStage));

        base1Color.insertStage(new Stage(baseYellowStage)); //6
        base1Color.insertStage(new Stage(baseBlankStage));

        base2Color.setStage(new Stage(baseCyanStage), 0); //1
        base2Color.insertStage(new Stage(baseBlankStage));

        base2Color.insertStage(new Stage(baseMagentaStage)); //2
        base2Color.insertStage(new Stage(baseBlankStage));

        base2Color.insertStage(new Stage(baseYellowStage)); //3
        base2Color.insertStage(new Stage(baseBlankStage));

        base2Color.insertStage(new Stage(baseRedStage)); //4
        base2Color.insertStage(new Stage(baseBlankStage));

        base2Color.insertStage(new Stage(baseGreenStage)); //5
        base2Color.insertStage(new Stage(baseBlankStage));

        base2Color.insertStage(new Stage(baseBlueStage)); //6
        base2Color.insertStage(new Stage(baseBlankStage));

        Stage basePositionStage = new Stage(2, 60*timeScale);
        basePositionStage.setStartVector(new float[]{0.0f, 0.0f});
        basePositionStage.setEndVector(new float[]{0.0f, 0.0f});
        basePositionStage.setTransitionCurve(Stage.Transition.Linear);

        base1Position.setStage(basePositionStage, 0);
        base2Position.setStage(basePositionStage, 0);

        Stage baseDimensionsStage = new Stage(2, 30*timeScale);
        baseDimensionsStage.setStartVector(new float[]{0.05f, 0.425f});
        baseDimensionsStage.setEndVector(new float[]{0.05f, -0.425f});
        baseDimensionsStage.setTransitionCurve(Stage.Transition.Sinusoidal);

        base1Dimensions.setStage(baseDimensionsStage, 0);
        base2Dimensions.setStage(baseDimensionsStage, 0);

        Stage baseAngleStage = new Stage(1, 60*timeScale);
        baseAngleStage.setStartVector(new float[]{0.0f});
        baseAngleStage.setEndVector(new float[]{0.0f});
        baseAngleStage.setTransitionCurve(Stage.Transition.Linear);

        base1Angle.setStage(baseAngleStage, 0);
        base2Angle.setStage(baseAngleStage, 0);

        baseSection.setLight(base1, 0);
        baseSection.insertLight(base2);
        baseLayer.setSection(baseSection, 0);
        //baseLayer.setHeight(0);
        insertLayer(baseLayer);

        //

        Layer strand1Layer = new Layer();
        Section strand1Section = new Section();
        Light strand1 = new Light();
        strand1.setUniformDimensions(true);
        strand1.setFigure("ellipse");

        Property strand1Color = strand1.getProperty(Property.Type.Color);
        Property strand1Position = strand1.getProperty(Property.Type.Position);
        Property strand1Dimensions = strand1.getProperty(Property.Type.Dimensions);
        Property strand1Angle = strand1.getProperty(Property.Type.Angle);

        strand1Color.setStage(new Stage(strandYGStage), 0);
        strand1Color.insertStage(new Stage(strandRYStage));
        strand1Color.insertStage(new Stage(strandCBStage));
        strand1Color.insertStage(new Stage(strandGCStage));
        strand1Color.insertStage(new Stage(strandMRStage));
        strand1Color.insertStage(new Stage(strandBMStage));

        Stage strand1PositionStage = new Stage(2, 30*timeScale);
        strand1PositionStage.setStartVector(new float[]{0.0f, 0.4f});
        strand1PositionStage.setEndVector(new float[]{0.0f, -0.4f});
        strand1PositionStage.setTransitionCurve(Stage.Transition.Sinusoidal);

        strand1Position.setStage(strand1PositionStage, 0);

        Stage strand1DimensionsStage = new Stage(2, 30*timeScale);
        strand1DimensionsStage.setStartVector(new float[]{0.10f, 0.10f});
        strand1DimensionsStage.setEndVector(new float[]{0.10f, 0.10f});
        strand1DimensionsStage.setTransitionCurve(Stage.Transition.Linear);

        strand1Dimensions.setStage(strand1DimensionsStage, 0);

        Stage strand1AngleStage = new Stage(1, 60*timeScale);
        strand1AngleStage.setStartVector(new float[]{0.0f});
        strand1AngleStage.setEndVector(new float[]{360.0f});
        strand1AngleStage.setTransitionCurve(Stage.Transition.Linear);

        strand1Angle.setStage(strand1AngleStage, 0);

        strand1Section.setLight(strand1, 0);
        strand1Layer.setSection(strand1Section, 0);
        //strand1Layer.setHeight(1);
        insertLayer(strand1Layer);

        //

        Layer strand2Layer = new Layer();
        Section strand2Section = new Section();
        Light strand2 = new Light();
        strand2.setUniformDimensions(true);
        strand2.setFigure("ellipse");

        Property strand2Color = strand2.getProperty(Property.Type.Color);
        Property strand2Position = strand2.getProperty(Property.Type.Position);
        Property strand2Dimensions = strand2.getProperty(Property.Type.Dimensions);
        Property strand2Angle = strand2.getProperty(Property.Type.Angle);

        strand2Color.setStage(new Stage(strandMRStage), 0);
        strand2Color.insertStage(new Stage(strandGCStage));
        strand2Color.insertStage(new Stage(strandYGStage));
        strand2Color.insertStage(new Stage(strandBMStage));
        strand2Color.insertStage(new Stage(strandCBStage));
        strand2Color.insertStage(new Stage(strandRYStage));

        Stage strand2PositionStage = new Stage(2, 30*timeScale);
        strand2PositionStage.setStartVector(new float[]{0.0f, -0.4f});
        strand2PositionStage.setEndVector(new float[]{0.0f, 0.4f});
        strand2PositionStage.setTransitionCurve(Stage.Transition.Sinusoidal);

        strand2Position.setStage(strand2PositionStage, 0);

        Stage strand2DimensionsStage = new Stage(2, 30*timeScale);
        strand2DimensionsStage.setStartVector(new float[]{0.10f, 0.10f});
        strand2DimensionsStage.setEndVector(new float[]{0.10f, 0.10f});
        strand2DimensionsStage.setTransitionCurve(Stage.Transition.Linear);

        strand2Dimensions.setStage(strand2DimensionsStage, 0);

        Stage strand2AngleStage = new Stage(1, 60*timeScale);
        strand2AngleStage.setStartVector(new float[]{0.0f});
        strand2AngleStage.setEndVector(new float[]{360.0f});
        strand2AngleStage.setTransitionCurve(Stage.Transition.Linear);

        strand2Angle.setStage(strand2AngleStage, 0);

        strand2Section.setLight(strand2, 0);
        strand2Layer.setSection(strand2Section, 0);
        //strand2Layer.setHeight(2);
        insertLayer(strand2Layer);
    }
}
