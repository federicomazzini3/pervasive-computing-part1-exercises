import io.vertx.core.AbstractVerticle;

public class CoffeeMachineDigitalTwin extends AbstractVerticle {

    CoffeeMachine coffeeMachine;

    public CoffeeMachineDigitalTwin(String thingId, String thingHost, int thingPort){
        coffeeMachine = new CoffeeMachineImpl(thingId, thingHost, thingPort);
    }

    public void start(){

    }
}
