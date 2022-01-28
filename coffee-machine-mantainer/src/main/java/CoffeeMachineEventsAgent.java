import io.vertx.core.AbstractVerticle;

public class CoffeeMachineEventsAgent extends AbstractVerticle {

    CoffeeMachine coffeeMachine;

    public CoffeeMachineEventsAgent(CoffeeMachineImpl coffeeMachine){
        this.coffeeMachine = coffeeMachine;
    }

    public void start(){

        coffeeMachine.subscribeToLimitedResource(limitedResource -> {

            log("Limited resources at coffe machine: " + coffeeMachine.getId() + "\n" + limitedResource);
        });

        coffeeMachine.subscribeToNeedMaintenance(maintenance -> {
            log("Coffe machine " + coffeeMachine.getId() + " need maintenance!" + "\n" + maintenance);
        });

        coffeeMachine.subscribeToOutOfResources(outOfResource -> {
            log("Out of resources at coffe machine: " + coffeeMachine.getId() + "\n" + outOfResource);
        });
    }

    private void log(Object msg){
        System.out.println(msg);
    }
}
