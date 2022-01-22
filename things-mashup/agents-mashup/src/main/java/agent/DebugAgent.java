package agent;

import api.LightSensorAPI;
import api.LightThingAPI;
import api.PresenceDetectorAPI;
import api.VocalUIAPI;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.auth.User;

public class DebugAgent extends AbstractVerticle {
    LightThingAPI lightThing;
    LightSensorAPI lightSensor;
    PresenceDetectorAPI presenceDetector;
    VocalUIAPI vocalUI;

    private final long periodicUpdate;

    private UserPanel panel;

    public DebugAgent(LightThingAPI lightThing, LightSensorAPI lightSensor, PresenceDetectorAPI presenceDetector, VocalUIAPI vocalUI) {
        this.lightThing = lightThing;
        this.lightSensor = lightSensor;
        this.presenceDetector = presenceDetector;
        this.vocalUI = vocalUI;
        this.periodicUpdate = 1000;
    }

    public void start(){
        this.createUserGUI();
        this.initializeFields();


        /** EVENTS AND GUI PRESENTATION */
        lightThing.subscribeToChangeState(this::setLightState);

        lightThing.subscribeToChangeIntensity(this::setLightIntensity);

        presenceDetector.subscribeToDetectPresence(this::setPresence);

        vocalUI.subscribeToNewCommand(this::setCommandByUser);

        lightSensor.subscribeToChangeLightLevel(this::setLightLevelInRoom);

        /** USER ACTIONS */
        this.getVertx().eventBus().consumer("gui-set-presence", message -> {
            presenceDetector.setPresence();
        });

        this.getVertx().eventBus().consumer("gui-set-lightLevel", message -> {
            lightSensor.setLightLevel(Integer.parseInt(message.body().toString()));
        });

        this.getVertx().eventBus().consumer("gui-set-vocalCommand", message -> {
            vocalUI.setCommand(message.body().toString());
        });
    }

    private void initializeFields(){
        log("Retrieve initial values");
        lightThing.isOn().onSuccess(this::setLightState);
        lightThing.getIntensity().onSuccess(this::setLightIntensity);
        presenceDetector.getPresence().onSuccess(this::setPresence);
        lightSensor.getLightLevel().onSuccess(this::setLightLevelInRoom);
        vocalUI.getCommand().onSuccess(this::setCommandByUser);
    }

    private void setPresence(Boolean presence) {
        log(presence ? "New Presence detected!" : "Presence timer run out");
        this.panel.setPresenceLabel(presence ? "Yes" : "No");
    }

    private void setLightLevelInRoom(Integer lightLevelInRoom){
        log("New light level in the room: " + lightLevelInRoom);
        this.panel.setLightInRoomLabel(lightLevelInRoom.toString());
    };

    private void setCommandByUser(String command){
        log("New command by the user: " + command);
        this.panel.setLastUserCommandLabel(command);
    };

    private void setLightState(String lightState){
        log(lightThing.getId() + " change state. Now it's " + lightState);
        this.lightThing.getIntensity().onSuccess(this::setLightIntensity);
        this.panel.setLightStateLabel(lightState);
    };

    private void setLightState(Boolean lightState){
        log(lightThing.getId() + " change state. Now it's: " + (lightState ? "on" : "off"));
        this.lightThing.getIntensity().onSuccess(this::setLightIntensity);
        this.panel.setLightStateLabel(lightState ? "on": "off");
    };

    private void setLightIntensity(Integer lightIntensity){
        log(lightThing.getId() + " change intensity. Now it's: " + lightIntensity);
        this.panel.setLightIntensity(lightIntensity.toString());
    };

    protected void createUserGUI() {
        panel = new UserPanel(this.getVertx().eventBus());
        panel.display();
    }

    private void log(Object message){
        System.out.println(message);
    }
}
