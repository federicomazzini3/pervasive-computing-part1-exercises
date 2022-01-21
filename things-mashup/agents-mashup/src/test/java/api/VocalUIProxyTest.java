package api;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class VocalUIProxyTest {

    VocalUIAPI vocalUI = new VocalUIProxy("vocal-ui", "localhost", 8084);


    @Test
    void getCommand() throws InterruptedException, ExecutionException {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        vocalUI.getCommand()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    completableFuture.complete(true);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assertEquals(true, completableFuture.get());
    }

    @Test
    void subscribeToNewCommand() throws InterruptedException, ExecutionException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        int eventsToListen = 4;
        AtomicInteger round = new AtomicInteger(0);

        vocalUI.subscribeToNewCommand(command -> {
            System.out.println("New command: " + command);
            round.set(round.get() + 1);
            if(round.get() == eventsToListen)
                completableFuture.complete(round.intValue());
        });

        assertEquals(eventsToListen, completableFuture.get());
    }
}