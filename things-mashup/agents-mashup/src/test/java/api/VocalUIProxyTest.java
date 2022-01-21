package api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import static org.junit.jupiter.api.Assertions.*;

class VocalUIProxyTest {

    VocalUIAPI vocalUI = new VocalUIProxy("vocal-ui", "localhost", 8084);


    @Test
    void getCommand() throws InterruptedException {
        vocalUI.getCommand()
                .onSuccess(result ->
                {
                    System.out.println(result);
                })
                .onFailure(message -> {
                    System.out.println(message);
                });
        Thread.sleep(5000);
    }

    @Test
    void testVerticle() throws InterruptedException {
        TestVerticle verticle = new TestVerticle(vocalUI);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(verticle);
        vertx.deployVerticle(verticle);
        Thread.sleep(50000);
    }

    private class TestVerticle extends AbstractVerticle{
        VocalUIAPI vocalUI;

        public TestVerticle(VocalUIAPI vocalUI){
            this.vocalUI = vocalUI;
        }

        public void start(){
            vocalUI.subscribeToNewCommand(command -> {
                System.out.println(command + '\n' + this.getVertx().toString());
            });
        }
    }
}