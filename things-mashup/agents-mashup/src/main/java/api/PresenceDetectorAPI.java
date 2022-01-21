package api;

import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface PresenceDetectorAPI {

    String getId();

    /** PROPERTIES **/
    Future<Boolean> getPresence();

    Future<Integer> getPresenceTimer();

    /** ACTIONS **/
    Future<Void> setPresenceTimer(Integer seconds);

    /** EVENTS **/
    void subscribeToDetectPresence(Handler<Boolean> handler);

    void subscribeToNonDetectPresence(Handler<Boolean> handler);
}
