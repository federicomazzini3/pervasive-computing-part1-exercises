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
    void setCommand() throws InterruptedException, ExecutionException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        vocalUI.getCommand()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    vocalUI.setCommand("switchOn").onSuccess(result2 -> {
                        vocalUI.getCommand().onSuccess(result3 -> {
                            System.out.println(result3);
                            completableFuture.complete(result3);
                        });
                    });
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assertEquals("\"switchOn\"", completableFuture.get());
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