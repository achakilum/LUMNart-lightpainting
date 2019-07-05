package com.bluelithalo.lumnart.example;

import com.bluelithalo.lumnart.pattern.Layer;
import com.bluelithalo.lumnart.pattern.Light;
import com.bluelithalo.lumnart.pattern.Property;
import com.bluelithalo.lumnart.pattern.Section;
import com.bluelithalo.lumnart.pattern.Stage;

public class FlashyTextPattern extends SampleTextPattern
{

    public FlashyTextPattern()
    {
        super();
        setName("Flashy Text");
        this.setup();
    }

    public void setup()
    {
        Layer textLayer = this.getLayer(0);
        Section textSection = textLayer.getSection(0);
        Light textLight = textSection.getLight(0);
        Property visible = textLight.getProperty(Property.Type.Visible);

        Stage visibleStage1 = new Stage(1, 20);
        visibleStage1.setStartVector(new float[]{0.0f});
        visibleStage1.setEndVector(new float[]{0.0f});
        visibleStage1.setTransitionCurve(Stage.Transition.None);
        visible.setStage(new Stage(visibleStage1), 0);

        Stage visibleStage2 = new Stage(1, 1);
        visibleStage2.setStartVector(new float[]{1.0f});
        visibleStage2.setEndVector(new float[]{1.0f});
        visibleStage2.setTransitionCurve(Stage.Transition.None);
        visible.insertStage(visibleStage2);

        Property dimensions = textLight.getProperty(Property.Type.Dimensions);
        Property angle = textLight.getProperty(Property.Type.Angle);
        Property position = textLight.getProperty(Property.Type.Position);
        Property color = textLight.getProperty(Property.Type.Color);

        angle.removeStage(1);

        for (int i = 0; i < dimensions.getStageCount(); i++)
        {
            dimensions.getStage(i).setTransitionCurve(Stage.Transition.Linear);
        }

        for (int i = 0; i < angle.getStageCount(); i++)
        {
            angle.getStage(i).setTransitionCurve(Stage.Transition.Linear);
        }

        for (int i = 0; i < position.getStageCount(); i++)
        {
            position.getStage(i).setTransitionCurve(Stage.Transition.Linear);
        }

        for (int i = 0; i < color.getStageCount(); i++)
        {
            color.getStage(i).setTransitionCurve(Stage.Transition.Linear);
        }
    }

}
