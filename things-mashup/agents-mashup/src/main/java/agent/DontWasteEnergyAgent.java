package agent;

import api.LightSensorAPI;
import api.LightThingAPI;
import api.PresenceDetectorAPI;
import api.VocalUIAPI;
import io.vertx.core.AbstractVerticle;

public class DontWasteEnergyAgent extends AbstractVerticle {


    LightThingAPI lightThing;
    LightSensorAPI lightSensor;
    PresenceDetectorAPI presenceDetector;
    VocalUIAPI vocalUI;

    int lowLightThreshold;
    int highLightThreshold;

    /** BELIEFS */
    boolean presence = false;
    int lightLevel = 0;

    public DontWasteEnergyAgent(LightThingAPI lightThing, LightSensorAPI lightSensor, PresenceDetectorAPI presenceDetector, VocalUIAPI vocalUI, int lowLightThreshold, int highLightThreshold) {
        this.lightThing = lightThing;
        this.lightSensor = lightSensor;
        this.presenceDetector = presenceDetector;
        this.vocalUI = vocalUI;
        this.lowLightThreshold = lowLightThreshold;
        this.highLightThreshold = highLightThreshold;
    }

    public void start(){
        presenceDetector.subscribeToDetectPresence(presence -> {
            this.presence = true;
            if(lightLevel < highLightThreshold)
                lightThing.switchOn();
        });

        presenceDetector.subscribeToNonDetectPresence(presence -> {
            this.presence = false;
            lightThing.switchOff();
        });

        lightSensor.subscribeToChangeLightLevel(lightLevel -> {
            this.lightLevel = lightLevel;
            if(lightLevel > highLightThreshold){
                lightThing.switchOff();
            }
        });
    }
}
