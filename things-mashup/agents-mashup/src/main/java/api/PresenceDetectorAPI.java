package api;

import io.vertx.core.Future;

public interface PresenceDetectorAPI {

    String getId();

    /** PROPERTIES **/
    Future<Boolean> getPresence();

    Future<Integer> getPresenceTimer();

    /** ACTIONS **/
    Future<Void> setPresenceTimer();

    /** EVENTS **/
    Future<String> subscribeToDetectPresence();

    Future<String> subscribeToNonDetectPresence();
}
