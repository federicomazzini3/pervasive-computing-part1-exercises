package api;

import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface PresenceDetectorAPI {

    String getId();

    /** PROPERTIES **/
    Future<Boolean> getPresence();

    Future<Integer> getPresenceTimer();

    Future<Void> setPresence();

    /** EVENTS **/
    void subscribeToDetectPresence(Handler<Boolean> handler);

    void subscribeToNonDetectPresence(Handler<Boolean> handler);
}
