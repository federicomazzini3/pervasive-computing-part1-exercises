package api;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;

import java.util.UUID;

public class LightSensorProxy implements LightSensorAPI{

    private Vertx vertx;
    private WebClient client;

    private String thingId;
    private int thingPort;
    private String thingHost;

    private static final String NAME = "/light-sensor-thing";
    private static final String PROPERTY = NAME + "/properties";
    private static final String EVENT = NAME + "/events";
    private static final String PROPERTY_LIGHTLEVEL = PROPERTY + "/lightLevel";
    private static final String EVENT_CHANGELIGHTLEVEL = EVENT + "/changeLight";

    public static final String EVENT_CHANGELIGHTLEVEL_ADDRESS = "lightLevel-change-event-address";

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
                    System.out.println("GET request to: " + thingHost+":"+thingPort+PROPERTY_LIGHTLEVEL);
                    promise.complete(Integer.parseInt(response.bodyAsString()));
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Void> setLightLevel(Integer newLightLevel) {
        Promise<Void> promise = Promise.promise();
        client.put(thingPort, thingHost, PROPERTY_LIGHTLEVEL)
                .sendBuffer(Buffer.buffer(newLightLevel.toString()))
                .onSuccess(response -> {
                    System.out.println("PUT request to: " + thingHost+":"+thingPort+PROPERTY_LIGHTLEVEL);
                    promise.complete();
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public void subscribeToChangeLightLevel(Handler<Integer> handler) {
        UUID id = UUID.randomUUID();
        this.longPollChangeLightLevel(id);
        this.vertx.eventBus().consumer(EVENT_CHANGELIGHTLEVEL_ADDRESS + id, message -> {
            handler.handle(Integer.parseInt(message.body().toString()));
            this.longPollChangeLightLevel(id);
        });
    }

    private Future<Integer> longPollChangeLightLevel(UUID id){
        System.out.println("New longpoll request for check light state");

        Promise<Integer> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_CHANGELIGHTLEVEL)
                .send()
                .onSuccess(response -> {
                    System.out.println(response.bodyAsString());
                    this.vertx.eventBus().publish(EVENT_CHANGELIGHTLEVEL_ADDRESS + id, response.body().toString());
                    promise.complete(Integer.parseInt(response.bodyAsString()));
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }
}
