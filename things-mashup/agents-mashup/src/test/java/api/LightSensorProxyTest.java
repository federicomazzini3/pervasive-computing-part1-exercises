package api;

import org.junit.jupiter.api.Test;

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
}