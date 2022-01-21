package api;

import io.vertx.core.Future;

public interface LightThingAPI {

    String getId();

    /** PROPERTIES **/
    Future<Boolean> isOn();

    Future<Boolean> isOff();

    Future<Integer> getIntensity();

    Future<String> getStatus();

    /** ACTIONS **/
    Future<Void> increase();

    Future<Void> increase(int step);

    Future<Void> decrease();

    Future<Void> decrease(int step);

    Future<Void> switchOn();

    Future<Void> switchOff();

    /** EVENTS **/
    Future<String> subscribeToChangeState();

    Future<String> subscribeToChangeIntensity();
}
