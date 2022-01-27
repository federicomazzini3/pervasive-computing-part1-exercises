package tests;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoffeeMachineModel {

    String id;
    List<Resource> availableResources;
    List<Drink> possibleDrinks;
    Date lastDrink;
    Date lastMaintenance;
    Integer servedCounter;
    Boolean maintenanceNeeded;

    private CoffeeMachineModel(String id, List<Resource> availableResources, List<Drink> possibleDrinks, Date lastDrink, Date lastMaintenance, Integer servedCounter, Boolean maintenanceNeeded) {
        this.id = id;
        this.availableResources = availableResources;
        this.possibleDrinks = possibleDrinks;
        this.lastDrink = lastDrink;
        this.lastMaintenance = lastMaintenance;
        this.servedCounter = servedCounter;
        this.maintenanceNeeded = maintenanceNeeded;
    }

    public static CoffeeMachineModel init() {
        return new CoffeeMachineModel(null, null, null, null, null, null, null);
    }

    public static CoffeeMachineModel fromJson(JsonObject json){
        return CoffeeMachineModel.init()
                .setId(json)
                .setAvailableResources(json)
                .setPossibleDrinks(json)
                .setLastDrink(json)
                .setLastMaintenance(json)
                .setMaintenanceNeeded(json)
                .setServedCounter(json);
    }

    public CoffeeMachineModel setId(String id) {
        return new CoffeeMachineModel(id, this.availableResources, this.possibleDrinks, this.lastDrink, this.lastMaintenance, this.servedCounter, this.maintenanceNeeded);
    }

    public CoffeeMachineModel setAvailableResources(List<Resource> availableResources) {
        return new CoffeeMachineModel(this.id, availableResources, this.possibleDrinks, this.lastDrink, this.lastMaintenance, this.servedCounter, this.maintenanceNeeded);
    }

    public CoffeeMachineModel setPossibleDrinks(List<Drink> possibleDrinks) {
        return new CoffeeMachineModel(this.id, availableResources, this.possibleDrinks, this.lastDrink, this.lastMaintenance, this.servedCounter, this.maintenanceNeeded);
    }

    public CoffeeMachineModel setLastDrink(Date lastDrink) {
        return new CoffeeMachineModel(this.id, this.availableResources, this.possibleDrinks, lastDrink, this.lastMaintenance, this.servedCounter, this.maintenanceNeeded);
    }

    public CoffeeMachineModel setLastMaintenance(Date lastMaintenance) {
        return new CoffeeMachineModel(this.id, this.availableResources, this.possibleDrinks, this.lastDrink, lastMaintenance, this.servedCounter, this.maintenanceNeeded);
    }

    public CoffeeMachineModel setServedCounter(int servedCounter) {
        return new CoffeeMachineModel(this.id, this.availableResources, this.possibleDrinks, this.lastDrink, this.lastMaintenance, servedCounter, this.maintenanceNeeded);
    }

    public CoffeeMachineModel setMaintenanceNeeded(Boolean maintenanceNeeded) {
        return new CoffeeMachineModel(this.id, this.availableResources, this.possibleDrinks, this.lastDrink, this.lastMaintenance, this.servedCounter, maintenanceNeeded);
    }

    public CoffeeMachineModel setId(JsonObject json) {
        return this.setId(json.getString("id"));
    }

    public CoffeeMachineModel setAvailableResources(JsonObject json) {
        List<Resource> resources = new ArrayList<>();
        JsonArray jsonResources = json.getJsonArray("availableResources");
        jsonResources.stream().forEach(jsonResource -> {
            JsonObject resource = new JsonObject(jsonResource.toString());
            resources.add(new Resource(resource.getString("name"), resource.getInteger("value")));
        });
        return this.setAvailableResources(resources);
    }

    public CoffeeMachineModel setPossibleDrinks(JsonObject json) {
        List<Drink> drinks = new ArrayList<>();
        JsonArray jsonDrinks = json.getJsonArray("possibleDrinks");
        jsonDrinks.stream().forEach(jsonDrink -> {
            JsonObject drink = new JsonObject(jsonDrink.toString());
            drinks.add(new Drink(drink.getString("id"), drink.getString("textId"), drink.getString("name"), drink.getString("ingredients"), drink.getBoolean("available")));
        });
        return this.setPossibleDrinks(drinks);
    }

    public CoffeeMachineModel setLastDrink(JsonObject json) {
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("lastDrink"));
            return this.setLastDrink(date);
        } catch (Exception e) {
            System.out.println("Error in setLastDrink");
            return this;
        }
    }

    public CoffeeMachineModel setLastMaintenance(JsonObject json){
        try {
            return this.setLastMaintenance(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("lastMantainance")));
        } catch (Exception e) {
            System.out.println("Error in setLastMaintenance");
            return this;
        }
    }

    public CoffeeMachineModel setServedCounter(JsonObject json) {
        return this.setServedCounter(json.getInteger("servedCounter"));
    }

    public CoffeeMachineModel setMaintenanceNeeded(JsonObject json) {
        return this.setMaintenanceNeeded(json.getBoolean("maintenanceNeeded"));
    }

    @Override
    public String toString() {
        return "tests.CoffeeMachineModel{" +
                "id='" + id + '\'' +
                ", availableResources=" + availableResources +
                ", possibleDrinks=" + possibleDrinks +
                ", lastDrink=" + lastDrink +
                ", lastMaintenance=" + lastMaintenance +
                ", servedCounter=" + servedCounter +
                ", maintenanceNeeded=" + maintenanceNeeded +
                '}';
    }
}
