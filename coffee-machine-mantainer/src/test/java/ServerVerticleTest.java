import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class ServerVerticleTest {

    ServerVerticle server = new ServerVerticle(5000);

    @Test
    public void machineList() throws ExecutionException, InterruptedException {
        server.addMachine("localhost", 8080);
        CompletableFuture<JsonArray> completableFuture = new CompletableFuture<>();
        server.getMachinesList()
                .onSuccess(list -> {
                    System.out.println(list);
                    completableFuture.complete(list);
                })
                .onFailure(message -> System.out.println(message.getMessage()));

        assertNotNull(completableFuture.get());
    }

    @Test
    public void machineInfo() throws ExecutionException, InterruptedException {
        server.addMachine("localhost", 8080);
        CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();
        server.getMachineInfo(server.twins.get(server.twins.keySet().iterator().next()))
                .onSuccess(info -> {
                    System.out.println(info);
                    completableFuture.complete(info);
                })
                .onFailure(message -> System.out.println(message.getMessage()));

        assertNotNull(completableFuture.get());
    }

    @Test
    public void machineDetails() throws ExecutionException, InterruptedException {
        server.addMachine("localhost", 8080);
        CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();
        server.getMachineDetails(server.twins.keySet().iterator().next())
                .onSuccess(info -> {
                    System.out.println(info);
                    completableFuture.complete(info);
                })
                .onFailure(message -> System.out.println(message.getMessage()));

        assertNotNull(completableFuture.get());
    }
}