package com.bluelithalo.lumnart.example;

import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.pattern.Stage;

public class RacewayRightTurnPattern extends Pattern
{
    public RacewayRightTurnPattern()
    {
        super();

        setName("Raceway Right Turn");
        setAuthor("Ashwin Chakilum");

        setup();
    }

    private void setup()
    {
        Layer trackLayer = this.getLayer(0);
        Section trackSection = trackLayer.getSection(0);
        trackSection.removeLight(0);

        // Left shoulder
        trackSection.insertLight();
        Light trackLeftShoulder = trackSection.getLight(0);

        Property tlsPosition = trackLeftShoulder.getProperty(Property.Type.Position);
        Property tlsDimensions = trackLeftShoulder.getProperty(Property.Type.Dimensions);

        Stage tlsPositionStage = new Stage(2, 30);
        tlsPositionStage.setStartVector(new float[]{0.0f, 0.48f});
        tlsPositionStage.setEndVector(new float[]{0.0f, 0.48f});
        tlsPositionStage.setTransitionCurve(Stage.Transition.Linear);

        Stage tlsDimensionsStage = new Stage(2, 30);
        tlsDimensionsStage.setStartVector(new float[]{0.025f, 0.025f});
        tlsDimensionsStage.setEndVector(new float[]{0.025f, 0.025f});
        tlsDimensionsStage.setTransitionCurve(Stage.Transition.Linear);

        tlsPosition.setStage(tlsPositionStage, 0);
        tlsDimensions.setStage(tlsDimensionsStage, 0);

        // Right shoulder
        trackSection.insertLight();
        Light trackRightShoulder = trackSection.getLight(1);

        Property trsPosition = trackRightShoulder.getProperty(Property.Type.Position);
        Property trsDimensions = trackRightShoulder.getProperty(Property.Type.Dimensions);

        Stage trsPositionStage = new Stage(2, 30);
        trsPositionStage.setStartVector(new float[]{0.0f, -0.4f});
        trsPositionStage.setEndVector(new float[]{0.0f, -0.4f});
        trsPositionStage.setTransitionCurve(Stage.Transition.Linear);

        Stage trsDimensionsStage = new Stage(2, 30);
        trsDimensionsStage.setStartVector(new float[]{0.025f, 0.025f});
        trsDimensionsStage.setEndVector(new float[]{0.025f, 0.025f});
        trsDimensionsStage.setTransitionCurve(Stage.Transition.Linear);

        trsPosition.setStage(trsPositionStage, 0);
        trsDimensions.setStage(trsDimensionsStage, 0);

        // Right rumble strip
        trackSection.insertLight();
        Light trackRumbleStrip = trackSection.getLight(2);

        Property rStripColor = trackRumbleStrip.getProperty(Property.Type.Color);
        Property rStripPosition = trackRumbleStrip.getProperty(Property.Type.Position);
        Property rStripDimension = trackRumbleStrip.getProperty(Property.Type.Dimensions);

        Stage rStripRedStage = new Stage(4, 30);
        rStripRedStage.setStartVector(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
        rStripRedStage.setEndVector(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
        rStripRedStage.setTransitionCurve(Stage.Transition.Linear);

        Stage rStripWhiteStage = new Stage(4, 30);
        rStripWhiteStage.setStartVector(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        rStripWhiteStage.setEndVector(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        rStripWhiteStage.setTransitionCurve(Stage.Transition.Linear);

        Stage rStripPositionStage = new Stage(2, 30);
        rStripPositionStage.setStartVector(new float[]{0.0f, -0.48f});
        rStripPositionStage.setEndVector(new float[]{0.0f, -0.48f});
        rStripPositionStage.setTransitionCurve(Stage.Transition.Linear);

        Stage rStripDimensionStage = new Stage(2, 30);
        rStripDimensionStage.setStartVector(new float[]{0.0175f, 0.1f});
        rStripDimensionStage.setEndVector(new float[]{0.0175f, 0.1f});
        rStripDimensionStage.setTransitionCurve(Stage.Transition.Linear);

        rStripColor.setStage(rStripRedStage, 0);
        rStripColor.insertStage(rStripWhiteStage);
        rStripPosition.setStage(rStripPositionStage, 0);
        rStripDimension.setStage(rStripDimensionStage, 0);
    }
}
