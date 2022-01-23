package agent;

import api.LightSensorAPI;
import api.LightThingAPI;
import api.PresenceDetectorAPI;
import api.VocalUIAPI;
import io.vertx.core.AbstractVerticle;

public class DebugAgent extends AbstractVerticle {
    LightThingAPI lightThing;
    LightSensorAPI lightSensor;
    PresenceDetectorAPI presenceDetector;
    VocalUIAPI vocalUI;

    private final int maxLumenLight;
    private final long periodicUpdate;

    private Integer lightIntensity;
    private boolean lightIsOn;
    private Integer lightLevel;
    private Integer lightLevelWithLampOn;
    private boolean presence;
    private String command;

    private UserPanel panel;

    public DebugAgent(LightThingAPI lightThing, LightSensorAPI lightSensor, PresenceDetectorAPI presenceDetector, VocalUIAPI vocalUI) {
        this.lightThing = lightThing;
        this.lightSensor = lightSensor;
        this.presenceDetector = presenceDetector;
        this.vocalUI = vocalUI;
        this.maxLumenLight = lightThing.getMaxLumen();
        this.periodicUpdate = 1000;
    }

    public void start() {
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

    private void initializeFields() {
        //log("Retrieve initial values");
        lightThing.isOn().onSuccess(this::setLightState);
        presenceDetector.getPresence().onSuccess(this::setPresence);
        lightThing.getIntensity().onSuccess(lightIntensity -> {
            this.setLightIntensity(lightIntensity);
        });
        lightSensor.getLightLevel().onSuccess(this::setLightLevelInRoom);
        this.setCommandByUser("None");
    }

    private void setPresence(Boolean presence) {
        //log(presence ? "New Presence detected!" : "Presence timer run out");
        this.presence = presence;
        this.panel.setPresenceLabel(presence ? "Yes" : "No");
    }

    private void setLightLevelInRoom(Integer lightLevelInRoom) {
        //log("New light level in the room: " + lightLevelInRoom);
        this.lightLevel = lightLevelInRoom;
        this.setLightLevelInRoomWithLampOn();
        this.panel.setLightInRoomLabel(lightLevelInRoom.toString());
    }

    private void setLightLevelInRoomWithLampOn() {
        if (lightIntensity != null)
            this.lightLevelWithLampOn = (int) (this.lightLevel + (maxLumenLight * (lightIntensity / 100.0)));
        else
            this.lightLevelWithLampOn = lightLevel;
        this.panel.setLightInRoomWithLightOnLabel(lightLevelWithLampOn.toString());
    }

    private void setCommandByUser(String command) {
        //log("New command by the user: " + command);
        this.command = command;
        this.panel.setLastUserCommandLabel(command);
    }

    private void setLightState(String lightState) {
        //log(lightThing.getId() + " change state. Now it's " + lightState);
        this.lightIsOn = lightState.equals("on") ? true : false;
        this.panel.setLightStateLabel(lightState);

        this.lightThing.getIntensity().onSuccess(this::setLightIntensity);
    }

    private void setLightState(Boolean lightState) {
        //log(lightThing.getId() + " change state. Now it's: " + (lightState ? "on" : "off"));
        this.lightIsOn = lightState;
        this.panel.setLightStateLabel(lightState ? "on" : "off");

        this.lightThing.getIntensity().onSuccess(this::setLightIntensity);
    }

    private void setLightIntensity(Integer lightIntensity) {
        //log(lightThing.getId() + " change intensity. Now it's: " + lightIntensity);
        this.lightIntensity = lightIntensity;
        this.setLightLevelInRoomWithLampOn();
        this.panel.setLightIntensity(lightIntensity.toString());
    }

    protected void createUserGUI() {
        panel = new UserPanel(this.getVertx().eventBus());
        panel.display();
    }

    private void log(Object message) {
        System.out.println("::: Debug Agent ::: " + message.toString());
    }
}
