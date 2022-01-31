import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ServerVerticle extends AbstractVerticle {

    public int port;
    public Map<String, CoffeeMachineImpl> twins;
    public List<ServerWebSocket> subscribers;

    public ServerVerticle(int port) {
        this.port = port;
        this.twins = new HashMap<>();
        this.subscribers = new ArrayList<>();
    }

    public void start() {

        Router mainRouter = Router.router(vertx);

        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("X-PINGARUNER");

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);

        mainRouter.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));

        mainRouter.route().handler(routingContext -> {
            System.out.println("New http request " + routingContext.request().absoluteURI());
            routingContext.response().putHeader("Content-Type", "application/json;charset=UTF-8");
            routingContext.next();
        });

        mainRouter.route().handler(BodyHandler.create());

        mainRouter.route(HttpMethod.GET, "/machines").handler(routingContext -> {
            this.getMachinesList()
                    .onSuccess(machinesInfo -> {
                        routingContext.response().end(machinesInfo.toString());
                    })
                    .onFailure(err -> log("Can't retrieve machine list info: " + err.getMessage()));
        });

        mainRouter.route(HttpMethod.GET, "/machines/:id").handler(routingContext -> {
            String id = routingContext.request().getParam("id");
            this.getMachineDetails(id).onSuccess(machineDetails -> {
                routingContext.response().end(machineDetails.toString());
            }).onFailure(err -> routingContext.response().end(err.getMessage()));
        });

        mainRouter.route(HttpMethod.GET, "/stats").handler(routingContext -> {
            this.getStats().onSuccess(stats -> {
                routingContext.response().end(stats.toString());
            }).onFailure(err -> routingContext.response().end(err.getMessage()));
        });

        mainRouter.route(HttpMethod.PUT, "/addMachine").handler(routingContext -> {
            log("add machine body: " + routingContext.getBodyAsString());
            JsonObject newMachine = routingContext.getBodyAsJson();
            String host = newMachine.getString("host");
            int port = Integer.parseInt(newMachine.getString("port"));
            this.addMachine(host, port)
                            .onSuccess(msg -> {
                                routingContext.response().end(new JsonObject().put("result", true).put("message", "You add a new machine").put("host", host).put("port", port).toString());
                            })
                    .onFailure(err -> routingContext.response().end(new JsonObject().put("result", false).put("message", "Can't add the machine").put("causes", err.getMessage()).toString()));
        });

        mainRouter.route(HttpMethod.DELETE, "/machines/:id").handler(routingContext -> {
            String id = routingContext.request().getParam("id");
            twins.remove(id);
            routingContext.response().end("Succesfully remove machine " + id);
        });

        vertx.eventBus().consumer("event", msg -> {
            Iterator<ServerWebSocket> it = this.subscribers.iterator();
            while (it.hasNext()) {
                ServerWebSocket ws = it.next();
                if (!ws.isClosed()) {
                    try {
                        log(msg.body().toString());
                        ws.writeTextMessage(new JsonObject(msg.body().toString()).toString());
                    } catch (Exception ex) {
                        it.remove();
                    }
                } else {
                    it.remove();
                }
            }
        });

        vertx.createHttpServer()
                .requestHandler(mainRouter)
                .webSocketHandler(ws -> {
                    log("New subscriber from " + ws.remoteAddress());
                    subscribers.add(ws);
                })
                .listen(port)
                .onSuccess(msg -> System.out.println("Server started on " + msg.actualPort()))
                .onFailure(err -> System.out.println("Error" + err.getMessage()));
    }

    public Future<Void> addMachine(String host, int port) {
        Promise<Void> promise = Promise.promise();
        String id = UUID.randomUUID().toString();
        CoffeeMachineImpl twin = new CoffeeMachineImpl(this.vertx, id, host, port);
        this.verifyMachine(twin)
                .onSuccess(msg -> {
                    twins.put(id, twin);
                    this.vertx.deployVerticle(new CoffeeMachineEventsAgent(twin));
                    promise.complete();
                })
                .onFailure(err -> promise.fail("The machine isn't online"));
        return promise.future();
    }

    private Future<Void> verifyMachine(CoffeeMachineImpl twin) {
        Promise<Void> promise = Promise.promise();
        twin.servedCounter()
                .onSuccess(msg -> promise.complete())
                .onFailure(err -> promise.fail("The machine isn't online"));
        return promise.future();
    }

    public Future<JsonArray> getMachinesList() {
        Promise<JsonArray> promise = Promise.promise();
        List<Future> machinesListFuture = new ArrayList<>();
        JsonArray machines = new JsonArray();
        //make the async call and save the future to handle it later
        this.twins.values().stream().forEach(machine -> machinesListFuture.add(getMachineInfo(machine)));
        //await for the completion of all futures
        CompositeFuture.all(machinesListFuture).onComplete(machinesFutures -> {
            if (machinesFutures.succeeded()) {
                machinesFutures.result().list().forEach(machine -> machines.add(machine));
                promise.complete(machines);
            } else {
                promise.fail(machinesFutures.cause());
            }
        });
        return promise.future();
    }

    public Future<JsonObject> getMachineInfo(CoffeeMachineImpl machine) {
        Promise<JsonObject> promise = Promise.promise();
        JsonObject machineObj = new JsonObject();
        String id = machine.getId();
        Future<Boolean> maintenance = machine.maintenanceNeeded();
        String host = machine.thingHost;
        int port = machine.thingPort;
        maintenance.onSuccess(
                maintenanceNeeded -> {
                    machineObj.put("id", id)
                            .put("maintenanceNeeded", maintenanceNeeded)
                            .put("host", host)
                            .put("port", port);
                    promise.complete(machineObj);
                }
        ).onFailure(err -> promise.fail("Can't send request to machine " + id));
        return promise.future();
    }

    public Future<JsonObject> getMachineDetails(String id) {
        Promise promise = Promise.promise();

        CoffeeMachineImpl machine = twins.get(id);
        if (machine == null) {
            promise.fail("The machine " + id + " doesn't exist.");
            return promise.future();
        }
        Future<JsonArray> availableResourceFut = machine.availableResources();
        Future<JsonArray> possibleDrinksFut = machine.possibleDrinks();
        Future<Optional<OffsetDateTime>> lastDrinkFut = machine.lastDrink();
        Future<Optional<OffsetDateTime>> lastMaintenanceFut = machine.lastMaintenance();
        Future<Boolean> maintenanceNeededFut = machine.maintenanceNeeded();

        CompositeFuture.all(availableResourceFut, possibleDrinksFut, lastDrinkFut, lastMaintenanceFut, maintenanceNeededFut)
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        JsonArray availableResources = availableResourceFut.result();
                        JsonArray possibleDrinks = possibleDrinksFut.result();
                        String lastDrink = lastDrinkFut.result().isPresent() ? lastDrinkFut.result().get().toString() : "null";
                        String lastMaintenance = lastMaintenanceFut.result().isPresent() ? lastMaintenanceFut.result().get().toString() : "null";
                        String maintenanceNeeded = maintenanceNeededFut.result().toString();
                        promise.complete(new JsonObject()
                                .put("id", id)
                                .put("availableResources", availableResources)
                                .put("possibleDrinks", possibleDrinks)
                                .put("lastDrink", lastDrink)
                                .put("lastMaintenance", lastMaintenance)
                                .put("maintenanceNeeded", maintenanceNeeded)
                        );
                    } else {
                        promise.fail("Can't complete the promise" + ar.cause().getMessage());
                    }
                });
        return promise.future();
    }

    public Future<JsonObject> getStats() {
        Promise<JsonObject> promise = Promise.promise();
        List<Future> machinesListFuture = new ArrayList<>();
        //make the async call and save the future to handle it later
        this.twins.values().stream().forEach(machine -> machinesListFuture.add(machine.servedCounter()));
        //await for the completion of all futures
        CompositeFuture.all(machinesListFuture).onComplete(machinesFutures -> {
            if (machinesFutures.succeeded()) {
                int drinkServed = machinesFutures.result().list().stream().mapToInt(count -> (int) count).sum();
                promise.complete(new JsonObject().put("drinkServed", drinkServed));
            } else {
                promise.fail(machinesFutures.cause());
            }
        });
        return promise.future();
    }

    private void log(Object msg) {
        System.out.println(msg);
    }

}
