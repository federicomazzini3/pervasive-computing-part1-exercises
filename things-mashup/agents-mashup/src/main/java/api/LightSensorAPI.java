package api;

import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface LightSensorAPI {

    String getId();

    /** PROPERTIES **/
    Future<Integer> getLightLevel();

    Future<Void> setLightLevel(Integer newLightLevel);

    /** EVENTS **/
    void subscribeToChangeLightLevel(Handler<Integer> handler);
}
