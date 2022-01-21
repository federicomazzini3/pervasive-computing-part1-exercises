package api;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class LightThingProxyTest {

    LightThingProxy lightThing = new LightThingProxy("light-thing", "localhost", 8084);

    @Test
    void isOn() throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        lightThing.isOn()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    completableFuture.complete(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assertFalse(completableFuture.get());
    }

    @Test
    void isOff() throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        lightThing.isOff()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    completableFuture.complete(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assertTrue(completableFuture.get());
    }

    @Test
    void getIntensity() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        lightThing.getIntensity()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    completableFuture.complete(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assertEquals(0, completableFuture.get());
    }

    @Test
    void getStatus() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        lightThing.getStatus()
                .onSuccess(result ->
                {
                    System.out.println(result);
                    completableFuture.complete(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });

        assert (completableFuture.get() != null && completableFuture.get() != "");
    }

    @Test
    void switchOn() throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        lightThing.switchOn()
                .onSuccess(res -> {
                    System.out.println(res);
                    lightThing.isOn()
                            .onSuccess(state -> {
                                System.out.println(state);
                                completableFuture.complete(state);
                            });
                })
                .onFailure(err -> {
                    System.out.println(err);
                });
        assertEquals(true, completableFuture.get());
    }

    @Test
    void testDecrease() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        lightThing.decrease()
                .onSuccess(res -> {
                    System.out.println(res);
                    lightThing.getIntensity()
                            .onSuccess(intensity -> {
                                System.out.println(intensity);
                                completableFuture.complete(intensity);
                            });
                })
                .onFailure(err -> {
                    System.out.println(err);
                });
        assertEquals(90, completableFuture.get());
    }

    @Test
    void testDecrease2() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        lightThing.decrease(10)
                .onSuccess(res -> {
                    System.out.println(res);
                    lightThing.getIntensity()
                            .onSuccess(intensity -> {
                                System.out.println(intensity);
                                completableFuture.complete(intensity);
                            });
                })
                .onFailure(err -> {
                    System.out.println(err);
                });
        assertEquals(80, completableFuture.get());
    }

    @Test
    void testIncrease() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        lightThing.increase(10)
                .onSuccess(res -> {
                    System.out.println(res);
                    lightThing.getIntensity()
                            .onSuccess(intensity -> {
                                System.out.println(intensity);
                                completableFuture.complete(intensity);
                            });
                })
                .onFailure(err -> {
                    System.out.println(err);
                });
        assertEquals(90, completableFuture.get());
    }

    @Test
    void testIncrease2() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        lightThing.increase()
                .onSuccess(res -> {
                    System.out.println(res);
                    lightThing.getIntensity()
                            .onSuccess(intensity -> {
                                System.out.println(intensity);
                                completableFuture.complete(intensity);
                            });
                })
                .onFailure(err -> {
                    System.out.println(err);
                });
        assertEquals(100, completableFuture.get());
    }

    @Test
    void switchOff() throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        lightThing.switchOff()
                .onSuccess(res -> {
                    System.out.println(res);
                    lightThing.isOff()
                            .onSuccess(state -> {
                                System.out.println(state);
                                completableFuture.complete(state);
                            });
                })
                .onFailure(err -> {
                    System.out.println(err);
                });
        assertEquals(true, completableFuture.get());
    }

    @Test
    void subscribeToChangeState() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        int eventsToListen = 4;
        AtomicInteger round = new AtomicInteger(0);

        lightThing.subscribeToChangeState(command -> {
            System.out.println("New command: " + command);
            round.set(round.get() + 1);
            if(round.get() == eventsToListen)
                completableFuture.complete(round.intValue());
        });

        assertEquals(eventsToListen, completableFuture.get());
    }

    @Test
    void subscribeToChangeIntensity() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        int eventsToListen = 4;
        AtomicInteger round = new AtomicInteger(0);

        lightThing.subscribeToChangeIntensity(command -> {
            System.out.println("New command: " + command);
            round.set(round.get() + 1);
            if(round.get() == eventsToListen)
                completableFuture.complete(round.intValue());
        });

        assertEquals(eventsToListen, completableFuture.get());
    }
}