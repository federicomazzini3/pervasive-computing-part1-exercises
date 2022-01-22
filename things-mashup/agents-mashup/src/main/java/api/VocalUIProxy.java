package api;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.util.UUID;

public class VocalUIProxy implements VocalUIAPI{

    private Vertx vertx;
    private WebClient client;

    private String thingId;
    private int thingPort;
    private String thingHost;

    private static final String NAME = "/vocal-ui-thing";
    private static final String PROPERTY = NAME + "/properties";
    private static final String EVENT = NAME + "/events";
    private static final String PROPERTY_COMMAND = PROPERTY + "/command";
    private static final String EVENT_NEWCOMMAND = EVENT + "/newCommand";

    public static final String EVENT_NEWCOMMAND_ADDRESS = "new-command-event-address";

    public VocalUIProxy(String thingId, String thingHost, int thingPort){
        this.thingId = thingId;
        this.thingHost = thingHost;
        this.thingPort = thingPort;

        this.vertx = Vertx.vertx();
        this.client = WebClient.create(vertx);
    }

    @Override
    public String getId() {
        return this.thingId;
    }

    @Override
    public Future<String> getCommand() {
        Promise<String> promise = Promise.promise();
        client.get(thingPort, thingHost, PROPERTY_COMMAND)
                .send()
                .onSuccess(response -> {
                    System.out.println("Requesto to: " + thingHost+":"+thingPort+ PROPERTY_COMMAND);
                    promise.complete(response.bodyAsString());
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Void> setCommand(String command) {
        Promise<Void> promise = Promise.promise();
        client.put(thingPort, thingHost, PROPERTY_COMMAND)
                .sendBuffer(Buffer.buffer(command))
                .onSuccess(response -> {
                    System.out.println("PUT request to: " + thingHost+":"+thingPort + PROPERTY_COMMAND);
                    promise.complete();
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public void subscribeToNewCommand(Handler<String> handler){
        UUID id = UUID.randomUUID();
        this.longPollNewCommand(id);
        this.vertx.eventBus().consumer(EVENT_NEWCOMMAND_ADDRESS + id, message -> {
            handler.handle(message.body().toString());
            this.longPollNewCommand(id);
        });
    }

    private Future<Void> longPollNewCommand(UUID id){
        Promise<Void> promise = Promise.promise();
        client.get(thingPort, thingHost, EVENT_NEWCOMMAND)
                .send()
                .onSuccess(response -> {
                    JsonObject res = response.bodyAsJsonObject();
                    this.vertx.eventBus().publish(EVENT_NEWCOMMAND_ADDRESS + id, res.getString("command"));
                    System.out.println("new longpoll request");
                    promise.complete();
                })
                .onFailure(err -> {
                    promise.fail("Can't retrieve light level from: " + thingId + "; " + err.getMessage());
                });
        return promise.future();
    }
}
