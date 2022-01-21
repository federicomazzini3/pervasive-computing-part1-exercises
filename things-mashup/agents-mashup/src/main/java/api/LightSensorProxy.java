package api;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class LightSensorProxy implements LightSensorAPI{

    private Vertx vertx;
    private WebClient client;

    private String thingId;
    private int thingPort;
    private String thingHost;

    private static final String NAME = "/light-sensor-thing";
    private static final String PROPERTY = NAME + "/properties";
    private static final String PROPERTY_LIGHTLEVEL = PROPERTY + "/lightLevel";

    public LightSensorProxy(String thingId, String thingHost, int thingPort){
        this.thingId = thingId;
        this.thingHost = thingHost;
        this.thingPort = thingPort;

        this.vertx = Vertx.vertx();
        this.client = WebClient.create(vertx);
    }

    @Override
    public String getId() {
        return thingId;
    }

    @Override
    public Future<Integer> getLightLevel() {
        Promise<Integer> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_LIGHTLEVEL)
                .send()
                .onSuccess(response -> {
                    System.out.println("Requesto to: " + thingHost+":"+thingPort+PROPERTY_LIGHTLEVEL);
                    promise.complete(Integer.parseInt(response.bodyAsString()));
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }
}
