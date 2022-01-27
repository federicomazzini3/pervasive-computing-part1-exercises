import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

public class CoffeeMachineImpl implements CoffeeMachine {

    private Vertx vertx;
    private WebClient client;

    private String thingId;
    private int thingPort;
    private String thingHost;

    private String id;

    private static final String NAME = "/light-thing";
    private static final String PROPERTY = NAME + "/properties";
    private static final String ACTIONS = NAME + "/actions";
    private static final String EVENT = NAME + "/events";
    private static final String PROPERTY_AVAILABLERESOURCES = PROPERTY + "/availableResources";
    private static final String PROPERTY_POSSIBLEDRINKS = PROPERTY + "/possibleDrinks";
    private static final String PROPERTY_LASTDRINK = PROPERTY + "/lastDrink";
    private static final String PROPERTY_LASTMAINTENANCE = PROPERTY + "/lastMantainance";
    private static final String PROPERTY_SERVEDCOUNTER = ACTIONS + "/servedCounter";
    private static final String PROPERTY_MAINTENANCENEEDED = ACTIONS + "/maintenanceNeeded";
    private static final String ACTION_MAKEDRINK = ACTIONS + "/makeDrink";
    private static final String EVENT_OUTOFRESOURCES = EVENT + "/outOfResource";
    private static final String EVENT_NEEDMAINTENANCE = EVENT + "/needMantainance";
    private static final String EVENT_LIMITEDRESOURCES = EVENT + "/limitedResource";

    public static final String EVENT_OUTOFRESOURCES_ADDRESS = "out-of-resources-event-address";
    public static final String EVENT_NEEDMAINTENANCE_ADDRESS = "need-maintenance-event-address";
    public static final String EVENT_LIMITEDRESOURCES_ADDRESS = "limited-resources-event-address";

    public CoffeeMachineImpl(String thingId, String thingHost, int thingPort){
        this.thingId = thingId;
        this.thingHost = thingHost;
        this.thingPort = thingPort;

        this.vertx = Vertx.vertx();
        this.client = WebClient.create(vertx);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Future<JsonObject> availableResources() {
        Promise<JsonObject> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_AVAILABLERESOURCES)
                .send()
                .onSuccess(response -> {
                    log(response.bodyAsJsonObject().toString());
                    promise.complete(response.bodyAsJsonObject());
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve available resources from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<JsonObject> possibleDrinks() {
        Promise<JsonObject> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_POSSIBLEDRINKS)
                .send()
                .onSuccess(response -> {
                    log(response.bodyAsJsonObject().toString());
                    promise.complete(response.bodyAsJsonObject());
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve possible drinks from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Date> lastDrink() {
        Promise<Date> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_LASTDRINK)
                .send()
                .onSuccess(response -> {
                    try {
                        Date lastDrink = DateFormat.getDateInstance().parse(response.bodyAsString());
                        promise.complete(lastDrink);
                    } catch (ParseException err) {
                        promise.fail("Can't retrieve last drink from: " + thingId + "; " + err.getMessage());
                    }
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve last drink from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Date> lastMaintenance() {
        Promise<Date> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_LASTMAINTENANCE)
                .send()
                .onSuccess(response -> {
                    try {
                        Date lastDrink = DateFormat.getDateInstance().parse(response.bodyAsString());
                        promise.complete(lastDrink);
                    } catch (ParseException err) {
                        promise.fail("Can't retrieve last maintenance from: " + thingId + "; " + err.getMessage());
                    }
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve last maintenance from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Integer> servedCounter() {
        Promise<Integer> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_SERVEDCOUNTER)
                .send()
                .onSuccess(response -> {
                    log(response.bodyAsString());
                    promise.complete(Integer.parseInt(response.bodyAsString()));
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve served drinks from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Boolean> maintenanceNeeded() {
        Promise<Boolean> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_MAINTENANCENEEDED)
                .send()
                .onSuccess(response -> {
                    log(response.bodyAsString());
                    promise.complete(response.bodyAsString().equals("true") ? true : false);
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve maintenance from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<String> makeDrink() {
        Promise<String> promise = Promise.promise();
        client
                .post(this.thingPort, thingHost, ACTION_MAKEDRINK)
                .send()
                .onSuccess(response -> {
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Something went wrong " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public void subscribeToOutOfResources(Handler<String> handler) {
        UUID id = UUID.randomUUID();
        this.longPollOutOfResource(id);
        this.vertx.eventBus().consumer(EVENT_OUTOFRESOURCES_ADDRESS + id, message -> {
            handler.handle(message.body().toString());
            this.longPollOutOfResource(id);
        });
    }

    @Override
    public void subscribeToNeedMaintenance(Handler<String> handler) {
        UUID id = UUID.randomUUID();
        this.longPollNeedMaintenance(id);
        this.vertx.eventBus().consumer(EVENT_NEEDMAINTENANCE_ADDRESS + id, message -> {
            handler.handle(message.body().toString());
            this.longPollNeedMaintenance(id);
        });
    }

    @Override
    public void subscribeToLimitedResource(Handler<String> handler) {
        UUID id = UUID.randomUUID();
        this.longPollLimitedResources(id);
        this.vertx.eventBus().consumer(EVENT_LIMITEDRESOURCES_ADDRESS + id, message -> {
            handler.handle(message.body().toString());
            this.longPollLimitedResources(id);
        });
    }


    private Future<Boolean> longPollOutOfResource(UUID id) {
        Promise<Boolean> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_OUTOFRESOURCES)
                .send()
                .onSuccess(response -> {
                    this.vertx.eventBus().publish(EVENT_OUTOFRESOURCES_ADDRESS + id, true);
                    promise.complete(true);
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve out of resources event from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    private Future<Boolean> longPollNeedMaintenance(UUID id) {
        Promise<Boolean> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_NEEDMAINTENANCE)
                .send()
                .onSuccess(response -> {
                    this.vertx.eventBus().publish(EVENT_NEEDMAINTENANCE_ADDRESS + id, true);
                    promise.complete(true);
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve need maintenance event from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    private Future<Boolean> longPollLimitedResources(UUID id) {
        Promise<Boolean> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_LIMITEDRESOURCES)
                .send()
                .onSuccess(response -> {
                    this.vertx.eventBus().publish(EVENT_LIMITEDRESOURCES_ADDRESS + id, true);
                    promise.complete(true);
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve limited resources event from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    private void log(Object message){
        System.out.println(message);
    }
}
