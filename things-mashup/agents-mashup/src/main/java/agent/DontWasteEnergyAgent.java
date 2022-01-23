package agent;

import api.LightSensorAPI;
import api.LightThingAPI;
import api.PresenceDetectorAPI;
import api.VocalUIAPI;
import io.vertx.core.AbstractVerticle;

import java.util.Optional;

public class DontWasteEnergyAgent extends AbstractVerticle {


    LightThingAPI lightThing;
    LightSensorAPI lightSensor;
    PresenceDetectorAPI presenceDetector;
    VocalUIAPI vocalUI;

    int lowLightThreshold;
    int highLightThreshold;

    /**
     * BELIEFS
     */
    boolean presence = false;
    int lightLevel = 0;
    Optional<String> userCommand;
    boolean lightIsOn;


    public DontWasteEnergyAgent(LightThingAPI lightThing, LightSensorAPI lightSensor, PresenceDetectorAPI presenceDetector, VocalUIAPI vocalUI, int lowLightThreshold, int highLightThreshold) {
        this.lightThing = lightThing;
        this.lightSensor = lightSensor;
        this.presenceDetector = presenceDetector;
        this.vocalUI = vocalUI;
        this.lowLightThreshold = lowLightThreshold;
        this.highLightThreshold = highLightThreshold;
        userCommand = Optional.empty();
        this.lightIsOn = false;
    }

    public void start() {
        presenceDetector.subscribeToDetectPresence(presence -> {
            this.presence = true;
            if (lightLevel < highLightThreshold) {
                if(userCommand.isEmpty()){
                    if(!lightIsOn){
                        log("Presence detected, going to switch on the light");
                        lightThing.switchOn();
                    } else
                        log("Presence detected, but nothing to do because the light is already on");
                } else {
                    log("Presence detected, but nothing to do because the user set a command");
                }
            }
        });

        presenceDetector.subscribeToNonDetectPresence(presence -> {
            this.presence = false;
            if (userCommand.isEmpty()) {
                if(lightIsOn){
                    log("Presence not detected, going to switch off the light");
                    lightThing.switchOff();
                } else
                    log("Presence not detected, but nothing to do because the light is already off");
            } else {
                log("Presence not detected, but nothing to do because the user set a command");
            }
        });

        lightSensor.subscribeToChangeLightLevel(lightLevel -> {
            this.lightLevel = lightLevel;
            if (lightLevel > highLightThreshold) {
                if (userCommand.isEmpty()) {
                    if(lightIsOn){
                        log("There's enough natural light, going to switch off the light");
                        lightThing.switchOff();
                    } else
                        log("There's enough natural light, but nothing to do because the light is already off");
                } else
                    log("There's enough natural light, but nothing to do because the user set a command");
            }
        });

        lightThing.subscribeToChangeState(state -> {
            lightIsOn = state.equals("on") ? true : false;
        });

        this.getVertx().eventBus().consumer("user-command", message -> {
            if (message.body().equals("reset"))
                userCommand = Optional.empty();
            else
                userCommand = Optional.of(message.body().toString());
        });
    }

    private void log(Object message) {
        System.out.println("::: DontWasteEnergy Agent ::: " + message.toString());
    }
}
