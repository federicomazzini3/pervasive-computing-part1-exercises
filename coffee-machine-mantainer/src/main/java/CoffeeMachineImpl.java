import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class CoffeeMachineImpl implements CoffeeMachine {

    private Vertx vertx;
    private WebClient client;

    private String thingId;
    public int thingPort;
    public String thingHost;

    private static final String NAME = "/smart-coffee-machine";
    private static final String PROPERTY = NAME + "/properties";
    private static final String ACTIONS = NAME + "/actions";
    private static final String EVENT = NAME + "/events";
    private static final String PROPERTY_AVAILABLERESOURCES = PROPERTY + "/availableResources";
    private static final String PROPERTY_POSSIBLEDRINKS = PROPERTY + "/possibleDrinks";
    private static final String PROPERTY_LASTDRINK = PROPERTY + "/lastDrink";
    private static final String PROPERTY_LASTMAINTENANCE = PROPERTY + "/lastMantainance";
    private static final String PROPERTY_SERVEDCOUNTER = PROPERTY + "/servedCounter";
    private static final String PROPERTY_MAINTENANCENEEDED = PROPERTY + "/maintenanceNeeded";
    private static final String ACTION_MAKEDRINK = ACTIONS + "/makeDrink";
    private static final String EVENT_OUTOFRESOURCES = EVENT + "/outOfResource";
    private static final String EVENT_NEEDMAINTENANCE = EVENT + "/needMantainance";
    private static final String EVENT_LIMITEDRESOURCES = EVENT + "/limitedResource";

    public static final String EVENT_OUTOFRESOURCES_ADDRESS = "out-of-resources-event-address";
    public static final String EVENT_NEEDMAINTENANCE_ADDRESS = "need-maintenance-event-address";
    public static final String EVENT_LIMITEDRESOURCES_ADDRESS = "limited-resources-event-address";

    public CoffeeMachineImpl(Vertx vertx, String thingId, String thingHost, int thingPort){
        this.thingId = thingId;
        this.thingHost = thingHost;
        this.thingPort = thingPort;

        this.vertx = vertx;
        this.client = WebClient.create(vertx);
    }

    @Override
    public String getId() {
        return thingId;
    }

    @Override
    public Future<JsonArray> availableResources() {
        Promise<JsonArray> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_AVAILABLERESOURCES)
                .send()
                .onSuccess(response -> {
                    promise.complete(response.bodyAsJsonArray());
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve available resources from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<JsonArray> possibleDrinks() {
        Promise<JsonArray> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_POSSIBLEDRINKS)
                .send()
                .onSuccess(response -> {
                    promise.complete(response.bodyAsJsonArray());
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve possible drinks from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Optional<OffsetDateTime>> lastDrink() {
        Promise<Optional<OffsetDateTime>> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_LASTDRINK)
                .send()
                .onSuccess(response -> {
                    try {
                        String lastDrinkString = response.bodyAsString().replace("\"", "");
                        OffsetDateTime lastDrink = OffsetDateTime.parse(lastDrinkString);
                        promise.complete(Optional.of(lastDrink));
                    } catch (Exception err) {
                        promise.complete(Optional.empty());
                    }
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve last drink from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Optional<OffsetDateTime>> lastMaintenance() {
        Promise<Optional<OffsetDateTime>> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_LASTMAINTENANCE)
                .send()
                .onSuccess(response -> {
                    try {
                        String lastMaintenanceString = response.bodyAsString().replace("\"", "");
                        OffsetDateTime lastMaintenance = OffsetDateTime.parse(lastMaintenanceString);
                        promise.complete(Optional.of(lastMaintenance));
                    } catch (Exception err) {
                        log("Error retrieving lastMaintenance: " + err.getMessage());
                        promise.complete(Optional.empty());
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
                    try{
                        promise.complete(Integer.parseInt(response.bodyAsString()));
                    } catch (Exception e){
                        log("Error retrieving served counter: " + e.getMessage());
                        promise.fail("Error retrieving served counter: " + e.getMessage());
                    }
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


    private Future<String> longPollOutOfResource(UUID id) {
        Promise<String> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_OUTOFRESOURCES)
                .send()
                .onSuccess(response -> {
                    this.vertx.eventBus().publish(EVENT_OUTOFRESOURCES_ADDRESS + id, response.bodyAsString());
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve out of resources event from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    private Future<String> longPollNeedMaintenance(UUID id) {
        Promise<String> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_NEEDMAINTENANCE)
                .send()
                .onSuccess(response -> {
                    this.vertx.eventBus().publish(EVENT_NEEDMAINTENANCE_ADDRESS + id, response.bodyAsString());
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve need maintenance event from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    private Future<String> longPollLimitedResources(UUID id) {
        Promise<String> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_LIMITEDRESOURCES)
                .send()
                .onSuccess(response -> {
                    this.vertx.eventBus().publish(EVENT_LIMITEDRESOURCES_ADDRESS + id, response.bodyAsString());
                    promise.complete(response.bodyAsString());
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
