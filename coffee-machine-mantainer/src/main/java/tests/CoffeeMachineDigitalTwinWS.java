package tests;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import java.util.Optional;

public class CoffeeMachineDigitalTwinWS extends AbstractVerticle {

    private String host;
    private int port;
    private Boolean initialized;
    /** PROPERTY */
    private CoffeeMachineModel coffeeMachineModel;

    public CoffeeMachineDigitalTwinWS(String host, int port) {
        this.host = host;
        this.port = port;
        this.initialized = false;
        this.coffeeMachineModel = CoffeeMachineModel.init();
    }

    public void start() {
        vertx.createHttpClient().webSocket(8081, "localhost", "", ws -> {
            if (ws.succeeded()) {
                System.out.println("Connected");
                WebSocket ctx = ws.result();
                ctx.textMessageHandler(msg -> {
                    JsonObject jsonMessage = new JsonObject(msg);
                    if (jsonMessage.getBoolean("init")) {
                        System.out.println("Init message received");
                        coffeeMachineModel = CoffeeMachineModel.fromJson(jsonMessage);
                        initialized = true;
                        System.out.println(coffeeMachineModel);
                    } else {
                    }
                });
            } else System.out.println(ws.cause().getMessage());
        });
    }

    public Optional<CoffeeMachineModel> getCoffeeMachineModel() {
        if (initialized) return Optional.of(coffeeMachineModel);
        return Optional.empty();
    }
}
