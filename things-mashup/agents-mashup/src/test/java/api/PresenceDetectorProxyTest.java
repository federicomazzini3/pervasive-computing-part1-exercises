package api;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class PresenceDetectorProxyTest {

    PresenceDetectorAPI presenceDetector = new PresenceDetectorProxy("presence-detector", "localhost", 8084);

    @Test
    void getPresence() throws InterruptedException, ExecutionException {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        presenceDetector.getPresence()
                .onSuccess(res -> {
                    System.out.println(res);
                    completableFuture.complete(res);
                })
                .onFailure(err -> {
                    System.out.println(err);
                });
        assertEquals(false, completableFuture.get());
    }

    @Test
    void getPresenceTimer() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        presenceDetector.getPresenceTimer()
                .onSuccess(res -> {
                    System.out.println(res);
                    completableFuture.complete(res);
                })
                .onFailure(err -> {
                    System.out.println(err);
                });
        assertEquals(30, completableFuture.get());
    }

    @Test
    void setPresenceTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> completableFuture1 = new CompletableFuture<>();
        CompletableFuture<Boolean> completableFuture2 = new CompletableFuture<>();
        presenceDetector.getPresence()
                        .onSuccess(res1 -> {
                            System.out.println("The initial value is " + res1);
                            completableFuture1.complete(res1);
                            presenceDetector.setPresence()
                                    .onSuccess(res2 -> {
                                        System.out.println("Write property done");
                                        presenceDetector.getPresence()
                                                .onSuccess(res3 -> {
                                                    System.out.println("Final value is " + res3);
                                                    completableFuture2.complete(res3);
                                                });
                                    });
                        });
        assertEquals(false, completableFuture1.get());
        assertEquals(true, completableFuture2.get());
    }

    @Test
    void subscribeToDetectPresence() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        int eventsToListen = 4;
        AtomicInteger round = new AtomicInteger(0);

        presenceDetector.subscribeToDetectPresence(presence -> {
            System.out.println("New presence detected!");
            round.set(round.get() + 1);
            if(round.get() == eventsToListen)
                completableFuture.complete(round.intValue());
        });

        assertEquals(eventsToListen, completableFuture.get());
    }

    @Test
    void subscribeToNonDetectPresence() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        int eventsToListen = 1;
        AtomicInteger round = new AtomicInteger(0);

        presenceDetector.subscribeToNonDetectPresence(presence -> {
            System.out.println("Not presence detected!");
            round.set(round.get() + 1);
            if(round.get() == eventsToListen)
                completableFuture.complete(round.intValue());
        });

        assertEquals(eventsToListen, completableFuture.get());
    }
}