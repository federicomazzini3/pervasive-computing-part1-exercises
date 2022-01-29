import io.vertx.core.AbstractVerticle;

public class CoffeeMachineEventsAgent extends AbstractVerticle {

    CoffeeMachine coffeeMachine;

    public CoffeeMachineEventsAgent(CoffeeMachineImpl coffeeMachine){
        this.coffeeMachine = coffeeMachine;
    }

    public void start(){

        coffeeMachine.subscribeToLimitedResource(limitedResource -> {
            log("Limited resources at coffe machine: " + coffeeMachine.getId() + "\n" + limitedResource);
            this.getVertx().eventBus().publish("event", "Limited resources at coffe machine: " + coffeeMachine.getId() + "" + limitedResource);
        });

        coffeeMachine.subscribeToNeedMaintenance(maintenance -> {
            log("Coffe machine " + coffeeMachine.getId() + " need maintenance!" + "\n" + maintenance);
            this.getVertx().eventBus().publish("event", "Coffe machine " + coffeeMachine.getId() + " need maintenance!" + "\n" + maintenance);
        });

        coffeeMachine.subscribeToOutOfResources(outOfResource -> {
            log("Out of resources at coffe machine: " + coffeeMachine.getId() + "\n" + outOfResource);
            this.getVertx().eventBus().publish("event", "Out of resources at coffe machine: " + coffeeMachine.getId() + "\n" + outOfResource);
        });
    }

    private void log(Object msg){
        System.out.println(msg);
    }
}
