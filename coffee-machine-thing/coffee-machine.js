const Servient = require('@node-wot/core').Servient;
const HttpServer = require('@node-wot/binding-http').HttpServer;
const servient = new Servient();
servient.addServer(new HttpServer({
    port: 8081
}));

servient.start().then((WoT) => {

    let drinkCounter;
    WoT.produce({
            "title": "Smart-Coffee-Machine",
            "description": "A smart coffee machine",
            "support": "git://github.com/eclipse/thingweb.node-wot.git",
            "@context": ["https://www.w3.org/2019/wot/td/v1"],
            "properties": {
                "availableResources": {
                    "type": "array",
                    "description": "Current level of all available resources given as an integer percentage for each particular resource.",
                    "items": {
                        "type": "object",
                        "properties": {
                            "name": {
                                "type": "string"
                            },
                            "value": {
                                "type": "integer",
                                "minimum": 0,
                                "maximum": 100
                            }
                        }
                    }
                },
                "possibleDrinks": {
                    "type": "array",
                    "description": "The list of possible drinks",
                    "items": {
                        "type": "object",
                        "properties": {
                            "id": {
                                "type": "integer",
                            },
                            "textId": {
                                "type": "string"
                            },
                            "name": {
                                "type": "string"
                            },
                            "ingredients": {
                                "type": "array",
                                "items": {
                                    "type": "object",
                                    "properties": {
                                        "resource": "string",
                                        "quantity": "integer"
                                    }
                                }
                            },
                            "available": {
                                "type": "boolean"
                            }
                        }
                    }
                },
                "lastDrink": {
                    "type": "string",
                    "description": "Date of the last drink produced",
                    "readOnly": true
                },
                "lastMantainance": {
                    "type": "string",
                    "description": "Date of last mantainance",
                    "readOnly": true
                },
                "servedCounter": {
                    "type": "integer",
                    "description": "The total number of served beverages",
                    "minimum": 0
                },
                "maintenanceNeeded": {
                    "type": "boolean",
                    "description": "Shows if a maintenance is needed",
                    "observable": true
                }
            },
            "actions": {
                "makeDrink": {
                    "description": "Make a drink from available list of beverages.",
                    "uriVariables": {
                        "drinkId": {
                            "type": "string",
                            "description": "Defines what drink to make, drinkId is one of possibleDrinks property values"
                        },
                        "sugarLevel": {
                            "type": "integer",
                            "description": "Defines the level of sugar for the drink",
                            "minimum": 0,
                            "maximum": 5
                        }
                    },
                    "output": {
                        "type": "object",
                        "description": "Returns true/false and a message when all invoked promises are resolved (asynchronous).",
                        "properties": {
                            "result": {
                                "type": "boolean"
                            },
                            "message": {
                                "type": "string"
                            }
                        }
                    }
                }
            },
            "events": {
                "outOfResource": {
                    "description": "Out of resource event. Emitted when the available resource level is not sufficient for a desired drink.",
                    "data": {
                        "type": "string"
                    }
                },
                "needMantainance": {
                    "description": "Generic problem, need for mantainance.",
                    "data": {
                        "type": "String"
                    }
                },
                "limitedResource": "Some resource is about to finish, it's necessary a refill.",
                "data": {
                    "type": "string"
                }
            }
        })
        .then((thing) => {
            drinkCounter = 0;
            lastDrink = new Date().toISOString();
            lastMantainance = new Date().toISOString();
            thing.writeProperty("availableResources", initialAvailableResources());
            thing.writeProperty("possibleDrinks", initialPossibleDrinks());
            thing.writeProperty("lastDrink", null);
            thing.writeProperty("lastMantainance", new Date().toISOString());
            thing.writeProperty("maintenanceNeeded", false);
            thing.setPropertyReadHandler("servedCounter", async () => drinkCounter);
            // Set up a handler for makeDrink action
            thing.setActionHandler("makeDrink", (params, options) => {
                let drinkId;
                let sugarLevel;
                let drink;
                if (options && typeof options === "object" && "uriVariables" in options) {
                    const uriVariables = options["uriVariables"];
                    drinkId = "drinkId" in uriVariables ? uriVariables["drinkId"] : drinkId;
                    sugarLevel = "sugarLevel" in uriVariables ? uriVariables["sugarLevel"] : 0;
                    if (sugarLevel > 5) sugarLevel = 5
                }
                return thing.readProperty("possibleDrinks").then((drinks) => {
                    drink = drinks.find((drink) => drink.textId == drinkId)
                    if (drink) {
                        if (drink.available) {
                            return thing.readProperty("availableResources").then((resources) => {
                                //consume resource for the drink and preparing it
                                if (verifyAvailableResources(resources, drink, sugarLevel)) {
                                    drinkCounter += 1;
                                    return new Promise((resolve, reject) => {
                                        console.log("preparing: " + drink)
                                        thing.writeProperty("availableResources", consumeResource(resources, drink, sugarLevel));
                                        thing.writeProperty("lastDrink", new Date().toISOString());
                                        resolve({
                                            result: true,
                                            message: "Making drink...have a good day :)"
                                        });
                                    });
                                    //if resource is not sufficient
                                } else {
                                    return new Promise((resolve, reject) => {
                                        resolve({
                                            result: false,
                                            message: `A resource is not sufficient`
                                        });
                                    });
                                }
                            });

                        } else {
                            return new Promise((resolve, reject) => {
                                resolve({
                                    result: false,
                                    message: "Drink unavailable"
                                })
                            })
                        }
                    } else {
                        return new Promise((resolve, reject) => {
                            resolve({
                                result: false,
                                message: "Drink not listed in Menu"
                            })
                        })
                    }
                });
            });

            thing.observeProperty("maintenanceNeeded", (data) => {
                // Notify a "maintainer" when the value has changed
                // (the notify function here simply logs a message to the console)
                notify("admin@coffeeMachine.com", `The coffe machine need maintenance!`);
            });

            thing.setPropertyWriteHandler("availableResources", resources => {
                let outOf = "";
                let limited = "";
                console.log(resources);
                resources.forEach((resource) => {
                    if (resource.value <= 0) {
                        outOf += ", " + resource.name;
                        thing.readProperty("possibleDrinks").then((drinks) => {
                            thing.writeProperty("possibleDrinks", updateDrinksWithAvailability(resource, drinks))
                        })
                    } else if (resource.value <= 15) {
                        limited += ", " + resource.name;
                        thing.readProperty("possibleDrinks").then((drinks) => {
                            thing.writeProperty("possibleDrinks", updateDrinksWithAvailability(resource, drinks))
                        })
                    }
                });

                if(outOf.length > 0){
                    thing.emitEvent("outOfResource", `Resource ${outOf.substring(1)} are finished!`);
                    thing.emitEvent("needMantainance", `The coffe machine need maintenance!`);
                    thing.writeProperty("maintenanceNeeded", true)
                } else if(limited.length > 0){
                    thing.emitEvent("limitedResource", `Resource ${limited.substring(1)} are limited!`);
                    thing.emitEvent("needMantainance", `The coffe machine need maintenance!`);
                    thing.writeProperty("maintenanceNeeded", true)
                }
            })

            // Finally expose the thing
            thing.expose().then(() => {
                console.info(`${thing.getThingDescription().title} ready`);
            });
            console.log(`Produced ${thing.getThingDescription().title}`);
        })
        .catch((e) => {
            console.log(e);
        });

})

function initialAvailableResources() {
    return [{
            "name": "water",
            "value": readFromSensor("water")
        }, {
            "name": "milk",
            "value": readFromSensor("milk")
        },
        {
            "name": "coffeeBeans",
            "value": readFromSensor("coffeeBeans")
        },
        {
            "name": "teaBlend",
            "value": readFromSensor("teaBlend")
        },
        {
            "name": "chocolate",
            "value": readFromSensor("chocolate")
        },
        {
            "name": "sugar",
            "value": readFromSensor("sugar")
        }
    ]
}

function initialPossibleDrinks() {
    return [{
            "id": 0,
            "textId": "espresso",
            "name": "Espresso",
            "ingredients": [{
                "resource": "water",
                "quantity": "1"
            }, {
                "resource": "coffeeBeans",
                "quantity": "1"
            }],
            "available": true
        },
        {
            "id": 1,
            "textId": "cappuccino",
            "name": "Cappuccino",
            "ingredients": [{
                "resource": "water",
                "quantity": "1"
            }, {
                "resource": "coffeeBeans",
                "quantity": "1"
            }, {
                "resource": "milk",
                "quantity": "2"
            }],
            "available": true
        },
        {
            "id": 2,
            "textId": "americano",
            "name": "Americano",
            "ingredients": [{
                "resource": "water",
                "quantity": "2"
            }, {
                "resource": "coffeeBeans",
                "quantity": "2"
            }],
            "available": true
        },
        {
            "id": 3,
            "textId": "tea",
            "name": "Tea",
            "ingredients": [{
                "resource": "water",
                "quantity": "1"
            }, {
                "resource": "teaBlend",
                "quantity": "1"
            }],
            "available": true
        },
        {
            "id": 4,
            "textId": "ukTea",
            "name": "Uk Tea",
            "ingredients": [{
                    "resource": "water",
                    "quantity": "1"
                }, {
                    "resource": "teaBlend",
                    "quantity": "1"
                },
                {
                    "resource": "milk",
                    "quantity": "1"
                }
            ],
            "available": true
        },
        {
            "id": 5,
            "textId": "milk",
            "name": "Milk",
            "ingredients": [{
                "resource": "water",
                "quantity": "1"
            }, {
                "resource": "milk",
                "quantity": "2"
            }],
            "available": true
        },
        {
            "id": 6,
            "textId": "chocolate",
            "name": "Chocolate",
            "ingredients": [{
                "resource": "water",
                "quantity": "1"
            }, {
                "resource": "chocolate",
                "quantity": "2"
            }],
            "available": true
        }

    ]
}

function readFromSensor(sensorType) {
    // Actual implementation of reading data from a sensor can go here
    // For the sake of example, let's just return a value
    return 100;
}

function notify(subscribers, msg) {
    // Actual implementation of notifying subscribers with a message can go here
    console.log(msg);
    return;
}

function updateDrinksWithAvailability(resource, drinks) {
    return drinks.map(drink => {
        return {
            ...drink,
            available: drink.ingredients.every(ingredient => {
                console.log(ingredient.resource)
                console.log(resource.name)
                if (ingredient.resource == resource.name)
                    return ingredient.quantity <= resource.value
                else return drink.available
            })
        }
    })
}

function verifyAvailableResources(resources, drink, sugar) {
    drink = addSugarToIngredient(drink, sugar)
    return resources.every(resource => {
        const resourceUsedForDrink = drink.ingredients.find(i => i.resource == resource.name);
        if (resourceUsedForDrink) {
            if (resourceUsedForDrink.quantity > resource.value) {
                console.log("false")
                return false;
            }
        }
        console.log("true")
        return true;
    })
}

function consumeResource(resources, drink, sugar) {
    drink = addSugarToIngredient(drink, sugar)
    return resources.map((resource) => {
        const resourceUsedForDrink = drink.ingredients.find(i => i.resource == resource.name);
        if (resourceUsedForDrink) {
            return {
                ...resource,
                "value": resource.value - resourceUsedForDrink.quantity
            }
        } else return resource
    });
}

function addSugarToIngredient(drink, sugar) {
    return {
        ...drink,
        "ingredients": drink.ingredients.concat({
            "resource": "sugar",
            "quantity": sugar
        })
    }
}