import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.Date;

public interface CoffeeMachine {

    String getId();

    /** PROPERTIES **/
    Future<JsonObject> availableResources();

    Future<JsonObject> possibleDrinks();

    Future<Date> lastDrink();

    Future<Date> lastMaintenance();

    Future<Integer> servedCounter();

    Future<Boolean> maintenanceNeeded();

    /** ACTIONS **/
    Future<String> makeDrink();

    /** EVENTS **/
    void subscribeToOutOfResources(Handler<String> handler);

    void subscribeToNeedMaintenance(Handler<String> handler);

    void subscribeToLimitedResource(Handler<String> handler);
}
