package api;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class LightSensorProxyTest {

    LightSensorProxy lightSensor = new LightSensorProxy("light-sensor-thing", "localhost", 8084);

    @Test
    void getLightLevel() throws InterruptedException {
        lightSensor.getLightLevel()
                .onSuccess(result ->
                {
                    System.out.println(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });
        Thread.sleep(5000);
    }

    @Test
    void setLightLevel() throws InterruptedException {
        lightSensor.setLightLevel(1000)
                .onSuccess(result -> {
                    System.out.println(result);
                    lightSensor.getLightLevel().onSuccess(lightLevel -> {System.out.println(lightLevel);});
                })
                .onFailure(message -> {
                    System.out.println(message);
                });
        Thread.sleep(2000);
    }

    @Test
    void subscribeToChangeLightLevel() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        int eventsToListen = 1;
        AtomicInteger round = new AtomicInteger(0);

        lightSensor.subscribeToChangeLightLevel(command -> {
            System.out.println("New light level: " + command);
            round.set(round.get() + 1);
            if(round.get() == eventsToListen)
                completableFuture.complete(round.intValue());
        });

        assertEquals(eventsToListen, completableFuture.get());
    }
}