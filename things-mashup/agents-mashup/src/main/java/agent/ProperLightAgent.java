package agent;

import api.LightSensorAPI;
import api.LightThingAPI;
import api.PresenceDetectorAPI;
import api.VocalUIAPI;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.awt.*;

public class ProperLightAgent extends AbstractVerticle {
    LightThingAPI lightThing;
    LightSensorAPI lightSensor;
    PresenceDetectorAPI presenceDetector;
    VocalUIAPI vocalUI;

    /** BELIEFS */
    int maxLumenLight;
    int goodLightLevel;

    public ProperLightAgent(LightThingAPI lightThing, LightSensorAPI lightSensor, PresenceDetectorAPI presenceDetector, VocalUIAPI vocalUI, int goodLightLevel) {
        this.lightThing = lightThing;
        this.lightSensor = lightSensor;
        this.presenceDetector = presenceDetector;
        this.vocalUI = vocalUI;
        this.maxLumenLight = lightThing.getMaxLumen();
        this.goodLightLevel = goodLightLevel;
    }

    public void start(){

        this.lightThing.subscribeToChangeState(state -> {
            System.out.println("ProperLightAgent Light state: " + state);
            if(state.equals("on")){
                Future<Integer> intensityFut = this.lightThing.getIntensity();
                Future<Integer> lightLevelFut = this.lightSensor.getLightLevel();

                CompositeFuture.all(intensityFut, lightLevelFut)
                        .onSuccess((CompositeFuture comp) -> {
                            int intensity = Integer.parseInt(comp.list().get(0).toString());
                            int lightLevel = Integer.parseInt(comp.list().get(1).toString()) + (maxLumenLight * (intensity / 100));

                            if(lightLevelDifferenceThan(goodLightLevel, lightLevel, 10)){
                                if(lightLevel > goodLightLevel)
                                    this.lightThing.decrease();
                                if(lightLevel < goodLightLevel)
                                    this.lightThing.increase();
                            }
                        });
                /*this.lightSensor.getLightLevel().onSuccess(lightLevel -> {
                    System.out.println("ProperLightAgent" + lightLevel);
                    int environmentLight = lightLevel + maxLumenLight;
                    if(lightLevelDifferenceThan(goodLightLevel, lightLevel, 10)){
                        if(lightLevel > goodLightLevel)
                            this.lightThing.decrease();
                        if(lightLevel < goodLightLevel)
                            this.lightThing.increase();
                    }
                });

                 */
            }
        });
    }

    private boolean lightLevelDifferenceThan(int lightLevel1, int lightLevel2, int perc){
        System.out.println(Math.abs(lightLevel1 - lightLevel2));
        System.out.println(lightLevel1 * (perc/100));
        return Math.abs(lightLevel1 - lightLevel2) > lightLevel1 * (perc/100);
    }

}
