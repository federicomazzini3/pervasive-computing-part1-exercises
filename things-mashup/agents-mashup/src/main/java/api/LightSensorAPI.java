package api;

import io.vertx.core.Future;

public interface LightSensorAPI {

    String getId();

    /** PROPERTIES **/
    Future<Integer> getLightLevel();
}
