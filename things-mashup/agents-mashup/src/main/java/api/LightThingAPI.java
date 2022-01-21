package api;

import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface LightThingAPI {

    String getId();

    /** PROPERTIES **/
    Future<Boolean> isOn();

    Future<Boolean> isOff();

    Future<Integer> getIntensity();

    Future<String> getStatus();

    /** ACTIONS **/
    Future<String> increase();

    Future<String> increase(Integer step);

    Future<String> decrease();

    Future<String> decrease(Integer step);

    Future<String> switchOn();

    Future<String> switchOff();

    /** EVENTS **/
    void subscribeToChangeState(Handler<String> handler);

    void subscribeToChangeIntensity(Handler<Integer> handler);
}
