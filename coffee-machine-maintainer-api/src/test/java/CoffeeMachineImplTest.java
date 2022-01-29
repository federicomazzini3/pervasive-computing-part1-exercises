import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class CoffeeMachineImplTest {

    CoffeeMachineImpl coffeeMachine = new CoffeeMachineImpl(Vertx.vertx(), "1", "localhost", 8080);
    @Test
    void availableResources() throws ExecutionException, InterruptedException {
        CompletableFuture<JsonArray> completableFuture = new CompletableFuture<>();

        coffeeMachine.availableResources()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    completableFuture.complete(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assertNotNull(completableFuture.get());
    }

    @Test
    void possibleDrinks() throws ExecutionException, InterruptedException {
        CompletableFuture<JsonArray> completableFuture = new CompletableFuture<>();

        coffeeMachine.possibleDrinks()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    completableFuture.complete(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assertNotNull(completableFuture.get());
    }

    @Test
    void lastDrink() throws ExecutionException, InterruptedException {
        CompletableFuture<Optional<OffsetDateTime>> completableFuture = new CompletableFuture<>();

        coffeeMachine.lastDrink()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    completableFuture.complete(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assertNotNull(completableFuture.get());
    }

    @Test
    void lastMaintenance() throws ExecutionException, InterruptedException {
        CompletableFuture<Optional<OffsetDateTime>> completableFuture = new CompletableFuture<>();

        coffeeMachine.lastMaintenance()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    completableFuture.complete(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assertNotNull(completableFuture.get());
    }

    @Test
    void servedCounter() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        coffeeMachine.servedCounter()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    completableFuture.complete(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assertNotNull(completableFuture.get());
    }

    @Test
    void maintenanceNeeded() throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        coffeeMachine.maintenanceNeeded()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    completableFuture.complete(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assertNotNull(completableFuture.get());
    }

    @Test
    void makeDrink() {
    }

    @Test
    void subscribeToOutOfResources() {
    }

    @Test
    void subscribeToNeedMaintenance() {
    }

    @Test
    void subscribeToLimitedResource() {
    }
}