import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ServerVerticle extends AbstractVerticle {

    public ServerVerticle(){

    }

    public void start(){

        Router mainRouter = Router.router(vertx);

        mainRouter.route().handler(routingContext -> {
            System.out.println("New http request " + routingContext.request().absoluteURI());
            routingContext.response().putHeader("Content-Type", "application/json;charset=UTF-8");
            routingContext.next();
        });

        mainRouter.route(HttpMethod.GET, "/machines").handler(routingContext -> {
            JsonObject json = new JsonObject().put("path", "machines").put("description", "list of all machines");
            routingContext.response().end(json.toString());
        });

        mainRouter.route(HttpMethod.GET, "/machines/:id").handler(routingContext -> {
            String id = routingContext.request().getParam("id");
            JsonObject json = new JsonObject().put("path", "machines/:id").put("description", "detail of a single machine");
            routingContext.response().end(json.toString());
        });

        mainRouter.route(HttpMethod.GET, "/stats").handler(routingContext -> {
            JsonObject json = new JsonObject().put("path", "stats").put("description", "stats of all the machines");
            routingContext.response().end(json.toString());
        });

        mainRouter.route(HttpMethod.PUT, "/addMachine/:uri").handler(routingContext -> {
            String uri = routingContext.request().getParam("uri");
            routingContext.response().end("You add a new machine available at " + uri);
        });

        mainRouter.route(HttpMethod.DELETE, "/deleteMachine/:id").handler(routingContext -> {
            String id = routingContext.request().getParam("id");
            JsonObject json = new JsonObject().put("action", id);
            routingContext.response().end("You delete the machine " + id);
        });

        vertx.createHttpServer()
                .requestHandler(mainRouter)
                .listen(8080);

        System.out.println("listening on: 8080");
    }

}
