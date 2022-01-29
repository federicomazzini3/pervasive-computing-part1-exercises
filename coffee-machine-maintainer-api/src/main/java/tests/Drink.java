package tests;

public class Drink {

    String id;
    String textId;
    String name;
    String ingredients;
    Boolean available;

    public Drink(String id, String textId, String name, String ingredients, Boolean available){
        this.id = id;
        this.textId = textId;
        this.name = name;
        this.ingredients = ingredients;
        this.available = available;
    }
}
