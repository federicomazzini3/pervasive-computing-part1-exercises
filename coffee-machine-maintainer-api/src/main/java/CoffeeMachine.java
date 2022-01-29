import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface CoffeeMachine {

    String getId();

    /** PROPERTIES **/
    Future<JsonArray> availableResources();

    Future<JsonArray> possibleDrinks();

    Future<Optional<OffsetDateTime>> lastDrink();

    Future<Optional<OffsetDateTime>> lastMaintenance();

    Future<Integer> servedCounter();

    Future<Boolean> maintenanceNeeded();

    /** ACTIONS **/
    Future<String> makeDrink();

    /** EVENTS **/
    void subscribeToOutOfResources(Handler<String> handler);

    void subscribeToNeedMaintenance(Handler<String> handler);

    void subscribeToLimitedResource(Handler<String> handler);
}
