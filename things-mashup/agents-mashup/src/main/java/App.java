import agent.DebugAgent;
import agent.DontWasteEnergyAgent;
import agent.ListenUserAgent;
import agent.ProperLightAgent;
import api.*;
import io.vertx.core.Vertx;

public class App {

    public static void main(String[]args){
        LightThingAPI lightThingAPI = new LightThingProxy("light-thing", "localhost", 8084, 1000);
        LightSensorAPI lightSensorAPI = new LightSensorProxy("light-thing", "localhost", 8084);
        PresenceDetectorAPI presenceDetectorAPI = new PresenceDetectorProxy("light-thing", "localhost", 8084);
        VocalUIAPI vocalUIAPI = new VocalUIProxy("light-thing", "localhost", 8084);

        int lowLightThreshold = 1500;
        int goodLightLevel = 2500;
        int highLightThreshold = 3500;

        Vertx vertx = Vertx.vertx();
        DebugAgent debugAgent = new DebugAgent(lightThingAPI, lightSensorAPI, presenceDetectorAPI, vocalUIAPI);
        DontWasteEnergyAgent dontWasteEnergyAgent = new DontWasteEnergyAgent(lightThingAPI, lightSensorAPI, presenceDetectorAPI, vocalUIAPI, lowLightThreshold, highLightThreshold);
        ProperLightAgent properLightAgent = new ProperLightAgent(lightThingAPI, lightSensorAPI, presenceDetectorAPI, vocalUIAPI, goodLightLevel);
        ListenUserAgent listenUserAgent = new ListenUserAgent(lightThingAPI, lightSensorAPI, presenceDetectorAPI, vocalUIAPI);

        vertx.deployVerticle(debugAgent);
        vertx.deployVerticle(dontWasteEnergyAgent);
        vertx.deployVerticle(properLightAgent);
        vertx.deployVerticle(listenUserAgent);
    }
}
