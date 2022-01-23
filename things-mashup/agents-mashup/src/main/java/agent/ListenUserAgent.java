package agent;

import api.LightSensorAPI;
import api.LightThingAPI;
import api.PresenceDetectorAPI;
import api.VocalUIAPI;
import io.vertx.core.AbstractVerticle;

import java.util.UUID;

public class ListenUserAgent extends AbstractVerticle {

    LightThingAPI lightThing;
    LightSensorAPI lightSensor;
    PresenceDetectorAPI presenceDetector;
    VocalUIAPI vocalUI;
    String id;

    public ListenUserAgent(LightThingAPI lightThing, LightSensorAPI lightSensor, PresenceDetectorAPI presenceDetector, VocalUIAPI vocalUI) {
        this.id = UUID.randomUUID().toString();
        this.lightThing = lightThing;
        this.lightSensor = lightSensor;
        this.presenceDetector = presenceDetector;
        this.vocalUI = vocalUI;
    }

    public void start() {

        this.vocalUI.subscribeToNewCommand(command -> {
            if (command.contains("switch") && command.contains("on")) {
                log("The user inject a new command: " + command);
                this.getVertx().eventBus().publish("user-command", "command");
                log("Going to switch on the light");
                lightThing.switchOn();
                lightThing.getIntensity().onSuccess(intensity -> {
                    if (intensity != 100)
                        lightThing.increase(100 - intensity);
                });
            } else if (command.contains("switch") && command.contains("off")) {
                log("The user inject a new command: " + command);
                this.getVertx().eventBus().publish("user-command", "command");
                log("Going to switch off the light");
                lightThing.switchOff();
            } else if (command.contains("reset")) {
                log("The user reset the agents behavior");
                this.getVertx().eventBus().publish("user-command", "reset");
            }
        });

    }

    private void log(Object message) {
        System.out.println("::: ListenUser Agent ::: " + message.toString());
    }
}
