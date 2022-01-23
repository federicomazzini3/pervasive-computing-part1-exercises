package agent;

import api.LightSensorAPI;
import api.LightThingAPI;
import api.PresenceDetectorAPI;
import api.VocalUIAPI;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.Optional;
import java.util.UUID;

public class ProperLightAgent extends AbstractVerticle {
    LightThingAPI lightThing;
    LightSensorAPI lightSensor;
    PresenceDetectorAPI presenceDetector;
    VocalUIAPI vocalUI;
    String id;

    /**
     * BELIEFS
     */
    enum IntensityDirection {UP, DOWN, IDLE}
    int maxLumenLight;
    int goodLightLevel;
    boolean lightIsOn;
    IntensityDirection direction;
    Optional<String> userCommand;

    public ProperLightAgent(LightThingAPI lightThing, LightSensorAPI lightSensor, PresenceDetectorAPI presenceDetector, VocalUIAPI vocalUI, int goodLightLevel) {
        this.id = UUID.randomUUID().toString();
        this.lightThing = lightThing;
        this.lightSensor = lightSensor;
        this.presenceDetector = presenceDetector;
        this.vocalUI = vocalUI;
        this.maxLumenLight = lightThing.getMaxLumen();
        this.goodLightLevel = goodLightLevel;
        lightIsOn = false;
        direction = IntensityDirection.IDLE;
        userCommand = Optional.empty();
    }

    public void start() {

        this.lightThing.subscribeToChangeState(state -> {
            lightIsOn = state.equals("on") ? true : false;
            if (lightIsOn) {
                if(userCommand.isEmpty()){
                    log("Light turned on, going to control intensity based on light level");
                    this.getVertx().eventBus().publish(id, "control-intensity");
                } else
                    log("Light turned on, but nothing to do because the user sets a command before");
            } else {
                direction = IntensityDirection.IDLE;
            }
        });

        this.lightSensor.subscribeToChangeLightLevel(lightLevel -> {
            if(userCommand.isEmpty()){
                if(lightIsOn){
                    log("Light level change, going to control light intensity based on the new light level");
                    this.getVertx().eventBus().publish(id, "control-intensity");
                } else
                    log("Light level change, but nothing to do because the light is off");
            } else
                log("Light level change, but nothing to do because the user sets a command");
        });

        this.getVertx().eventBus().consumer(id, message -> {
            if (message.body().equals("control-intensity") && lightIsOn && userCommand.isEmpty()) {
                Future<Integer> intensityFut = this.lightThing.getIntensity();
                Future<Integer> lightLevelFut = this.lightSensor.getLightLevel();

                CompositeFuture.all(intensityFut, lightLevelFut)
                        .onSuccess((CompositeFuture comp) -> {
                            int intensity = Integer.parseInt(comp.list().get(0).toString());
                            int lightLevel = (int) (Integer.parseInt(comp.list().get(1).toString()) + (maxLumenLight * (intensity / 100.0)));

                            if (lightLevel > goodLightLevel) direction = IntensityDirection.DOWN;
                            else if (lightLevel < goodLightLevel) direction = IntensityDirection.UP;

                            if (lightLevelDifferenceThan(goodLightLevel, lightLevel, 10)) {

                                if (direction == IntensityDirection.DOWN && intensity >= 10) {
                                    log("Decrease light intensity cause the light level is too high");
                                    this.lightThing.decrease().onSuccess(res -> {
                                        this.getVertx().eventBus().publish(id, "control-intensity");
                                    });
                                }

                                if (direction == IntensityDirection.UP && intensity <= 90) {
                                    this.lightThing.increase().onSuccess(res -> {
                                        log("Decrease light intensity cause the light level is too low");
                                        this.getVertx().eventBus().publish(id, "control-intensity");
                                    });
                                }
                            } else direction = IntensityDirection.IDLE;
                        });
            }
        });

        this.getVertx().eventBus().consumer("user-command", message -> {
            if (message.body().equals("reset"))
                userCommand = Optional.empty();
            else
                userCommand = Optional.of(message.body().toString());
        });
    }

    private boolean lightLevelDifferenceThan(int lightLevel1, int lightLevel2, int perc) {
        //return Math.abs(lightLevel1 - lightLevel2) > lightLevel1 * (perc/100);
        return Math.abs(lightLevel1 - lightLevel2) > 100;
    }

    private void log(Object message) {
        System.out.println("::: ProperLight Agent ::: " + message.toString());
    }

}
