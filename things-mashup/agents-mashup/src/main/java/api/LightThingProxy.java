package api;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.util.UUID;

public class LightThingProxy implements LightThingAPI{
    private Vertx vertx;
    private WebClient client;

    private String thingId;
    private int thingPort;
    private String thingHost;

    private int maxLumen;

    private static final String NAME = "/light-thing";
    private static final String PROPERTY = NAME + "/properties";
    private static final String ACTIONS = NAME + "/actions";
    private static final String EVENT = NAME + "/events";
    private static final String PROPERTY_ISON = PROPERTY + "/isOn";
    private static final String PROPERTY_ISOFF = PROPERTY + "/isOff";
    private static final String PROPERTY_INTENSITY = PROPERTY + "/intensity";
    private static final String PROPERTY_STATUS = PROPERTY + "/status";
    private static final String ACTION_SWITCHON = ACTIONS + "/switchOn";
    private static final String ACTION_SWITCHOFF = ACTIONS + "/switchOff";
    private static final String ACTION_INCREASE = ACTIONS + "/increase";
    private static final String ACTION_DECREASE = ACTIONS + "/decrease";
    private static final String EVENT_CHANGESTATE = EVENT + "/changeState";
    private static final String EVENT_CHANGEINTENSITY = EVENT + "/changeIntensity";

    public static final String EVENT_CHANGESTATE_ADDRESS = "state-change-event-address";
    public static final String EVENT_CHANGEINTENSITY_ADDRESS = "intensity-change-event-address";

    public LightThingProxy(String thingId, String thingHost, int thingPort, int maxLumen){
        this.thingId = thingId;
        this.thingHost = thingHost;
        this.thingPort = thingPort;

        this.maxLumen = maxLumen;

        this.vertx = Vertx.vertx();
        this.client = WebClient.create(vertx);
    }

    @Override
    public int getMaxLumen() {
        return this.maxLumen;
    }

    @Override
    public String getId() {
        return this.thingId;
    }

    @Override
    public Future<Boolean> isOn() {
        Promise<Boolean> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_ISON)
                .send()
                .onSuccess(response -> {
                    //System.out.println("Requesto to: " + thingHost+":"+thingPort+ PROPERTY_ISON);
                    promise.complete(Boolean.parseBoolean(response.bodyAsString()));
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Boolean> isOff() {
        Promise<Boolean> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_ISOFF)
                .send()
                .onSuccess(response -> {
                    //System.out.println("Requesto to: " + thingHost+":"+thingPort+ PROPERTY_ISOFF);
                    promise.complete(Boolean.parseBoolean(response.bodyAsString()));
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Integer> getIntensity() {
        Promise<Integer> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_INTENSITY)
                .send()
                .onSuccess(response -> {
                    //System.out.println("Requesto to: " + thingHost+":"+thingPort+ PROPERTY_INTENSITY);
                    promise.complete(Integer.parseInt(response.bodyAsString()));
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<String> getStatus() {
        Promise<String> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_STATUS)
                .send()
                .onSuccess(response -> {
                    //System.out.println("Requesto to: " + thingHost+":"+thingPort+ PROPERTY_STATUS);
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<String> increase() {
        Promise<String> promise = Promise.promise();
        client
                .post(this.thingPort, thingHost, ACTION_INCREASE)
                .send()
                .onSuccess(response -> {
                    //System.out.println(response.bodyAsString());
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Something went wrong " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<String> increase(Integer step) {
        Promise<String> promise = Promise.promise();
        client
                .post(this.thingPort, thingHost, ACTION_INCREASE)
                .addQueryParam("step", step.toString())
                .send()
                .onSuccess(response -> {
                    //System.out.println(response.bodyAsString());
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Something went wrong " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<String> decrease() {
        Promise<String> promise = Promise.promise();
        client
                .post(this.thingPort, thingHost, ACTION_DECREASE)
                .send()
                .onSuccess(response -> {
                    //System.out.println(response.bodyAsString());
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Something went wrong " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<String> decrease(Integer step) {
        Promise<String> promise = Promise.promise();
        client
                .post(this.thingPort, thingHost, ACTION_DECREASE)
                .addQueryParam("step", step.toString())
                .send()
                .onSuccess(response -> {
                    //System.out.println(response.bodyAsString());
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Something went wrong " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<String> switchOn() {
        Promise<String> promise = Promise.promise();
        client
                .post(this.thingPort, thingHost, ACTION_SWITCHON)
                .send()
                .onSuccess(response -> {
                    //System.out.println(response.bodyAsString());
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Something went wrong " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<String> switchOff() {
        Promise<String> promise = Promise.promise();
        client
                .post(this.thingPort, thingHost, ACTION_SWITCHOFF)
                .send()
                .onSuccess(response -> {
                    //System.out.println(response.bodyAsString());
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Something went wrong " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public void subscribeToChangeState(Handler<String> handler) {
        UUID id = UUID.randomUUID();
        this.longPollChangeState(id);
        this.vertx.eventBus().consumer(EVENT_CHANGESTATE_ADDRESS + id, message -> {
            handler.handle(message.body().toString());
            this.longPollChangeState(id);
        });
    }

    @Override
    public void subscribeToChangeIntensity(Handler<Integer> handler) {
        UUID id = UUID.randomUUID();
        this.longPollChangeIntensity(id);
        this.vertx.eventBus().consumer(EVENT_CHANGEINTENSITY_ADDRESS + id, message -> {
            handler.handle(Integer.parseInt(message.body().toString()));
            this.longPollChangeIntensity(id);
        });
    }

    private Future<String> longPollChangeState(UUID id){
        //System.out.println("New longpoll request for check light state");

        Promise<String> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_CHANGESTATE)
                .send()
                .onSuccess(response -> {
                    String res = response.bodyAsString().replace("\"", "");
                    this.vertx.eventBus().publish(EVENT_CHANGESTATE_ADDRESS + id, res);
                    promise.complete(res);
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    private Future<String> longPollChangeIntensity(UUID id){
        //System.out.println("New longpoll request for check light intensity");

        Promise<String> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_CHANGEINTENSITY)
                .send()
                .onSuccess(response -> {
                    JsonObject jsonResponse = response.bodyAsJsonObject();
                    this.vertx.eventBus().publish(EVENT_CHANGEINTENSITY_ADDRESS + id, jsonResponse.getInteger("intensity"));
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }
}
