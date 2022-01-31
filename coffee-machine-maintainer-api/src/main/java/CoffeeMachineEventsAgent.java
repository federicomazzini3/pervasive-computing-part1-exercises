import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class CoffeeMachineEventsAgent extends AbstractVerticle {

    CoffeeMachine coffeeMachine;

    public CoffeeMachineEventsAgent(CoffeeMachineImpl coffeeMachine){
        this.coffeeMachine = coffeeMachine;
    }

    public void start(){

        coffeeMachine.subscribeToLimitedResource(limitedResource -> {
            log(limitedResource);
            JsonObject json = new JsonObject().put("event", "limitedResource").put("message", limitedResource).put("machineId", coffeeMachine.getId());
            this.getVertx().eventBus().publish("event", json);
        });

        coffeeMachine.subscribeToNeedMaintenance(maintenance -> {
            log(maintenance);
            JsonObject json = new JsonObject().put("event", "needMaintenance").put("message", maintenance).put("machineId", coffeeMachine.getId());
            this.getVertx().eventBus().publish("event", json);
        });

        coffeeMachine.subscribeToOutOfResources(outOfResource -> {
            log(outOfResource);
            JsonObject json = new JsonObject().put("event", "limitedResource").put("message", outOfResource).put("machineId", coffeeMachine.getId());
            this.getVertx().eventBus().publish("event", json);
        });
    }

    private void log(Object msg){
        System.out.println(msg);
    }
}
