package api;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

import java.util.UUID;

public class PresenceDetectorProxy implements PresenceDetectorAPI {

    private Vertx vertx;
    private WebClient client;

    private String thingId;
    private int thingPort;
    private String thingHost;

    private static final String NAME = "/presence-detector-thing";
    private static final String PROPERTY = NAME + "/properties";
    private static final String ACTIONS = NAME + "/actions";
    private static final String EVENT = NAME + "/events";
    private static final String PROPERTY_PRESENCE = PROPERTY + "/presence";
    private static final String PROPERTY_PRESENCETIMER = PROPERTY + "/presenceTimer";
    private static final String ACTION_SETPRESENCETIMER = ACTIONS + "/setPresenceTimer";
    private static final String EVENT_DETECTPRESENCE = EVENT + "/detectPresence";
    private static final String EVENT_NONDETECTPRESENCE = EVENT + "/nonDetectPresence";

    public static final String EVENT_DETECTPRESENCE_ADDRESS = "detect-presence-event-address";
    public static final String EVENT_NONDETECTPRESENCE_ADDRESS = "non-detect-presence-event-address";

    public PresenceDetectorProxy(String thingId, String thingHost, int thingPort){
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
    public Future<Boolean> getPresence() {
        Promise<Boolean> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_PRESENCE)
                .send()
                .onSuccess(response -> {
                    System.out.println("Requesto to: " + thingHost+":"+thingPort+ PROPERTY_PRESENCE);
                    promise.complete(Boolean.parseBoolean(response.bodyAsString()));
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Integer> getPresenceTimer() {
        Promise<Integer> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_PRESENCETIMER)
                .send()
                .onSuccess(response -> {
                    System.out.println("Requesto to: " + thingHost+":"+thingPort+ PROPERTY_PRESENCETIMER);
                    promise.complete(Integer.parseInt(response.bodyAsString()));
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Void> setPresenceTimer(Integer seconds) {
        Promise<Void> promise = Promise.promise();
        client
                .post(this.thingPort, thingHost, ACTION_SETPRESENCETIMER)
                .addQueryParam("seconds", seconds.toString())
                .send()
                .onSuccess(response -> {
                    System.out.println(response.bodyAsString());
                    promise.complete(null);
                })
                .onFailure(err -> {
                    promise.fail("Something went wrong " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public void subscribeToDetectPresence(Handler<Boolean> handler) {
        UUID id = UUID.randomUUID();
        this.longPollDetectPresence(id);
        this.vertx.eventBus().consumer(EVENT_DETECTPRESENCE_ADDRESS + id, message -> {
            handler.handle(true);
            this.longPollDetectPresence(id);
        });
    }

    @Override
    public void subscribeToNonDetectPresence(Handler<Boolean> handler) {
        UUID id = UUID.randomUUID();
        this.longPollNonDetectPresence(id);
        this.vertx.eventBus().consumer(EVENT_NONDETECTPRESENCE_ADDRESS + id, message -> {
            handler.handle(true);
            this.longPollNonDetectPresence(id);
        });
    }

    private Future<Void> longPollDetectPresence(UUID id){
        System.out.println("New longpoll request for detect presence");

        Promise<Void> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_DETECTPRESENCE)
                .send()
                .onSuccess(response -> {
                    this.vertx.eventBus().publish(EVENT_DETECTPRESENCE_ADDRESS + id, response.body().toString());
                    promise.complete();
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    private Future<Void> longPollNonDetectPresence(UUID id){
        System.out.println("New longpoll request for non detect presence");

        Promise<Void> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_NONDETECTPRESENCE)
                .send()
                .onSuccess(response -> {
                    this.vertx.eventBus().publish(EVENT_NONDETECTPRESENCE_ADDRESS + id, response.body().toString());
                    promise.complete();
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }
}
