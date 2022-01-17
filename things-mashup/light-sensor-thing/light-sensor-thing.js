const Servient = require('@node-wot/core').Servient;
const HttpServer = require('@node-wot/binding-http').HttpServer;
const td = require('./light-sensor-thing-description.json')

const servient = new Servient();
servient.addServer(new HttpServer({
    port: 8080
}));


servient.start().then((WoT) => {
    let lightLevel;
    let lightDirection; //test purpose
    let minLightLevel = 0; //test purpose
    let maxLightLevel = 1000; //test purpose
    WoT.produce(td).then((thing) => {
        lightLevel = 0;

        thing.setPropertyReadHandler("lightLevel", async () => lightLevel)

        // Finally expose the thing
        thing.expose().then(() => {

            setInterval(() => {
                if(lightLevel >= maxLightLevel) lightDirection = "down"
                else if (lightLevel <= minLightLevel) lightDirection = "up"

                if(lightDirection == "up") lightLevel += 1
                else if (lightDirection == "down") lightLevel -= 1
                console.log("Light level: " + lightLevel)
            }, 1000);

            console.info(`${thing.getThingDescription().title} ready`);
        });
        console.log(`Produced ${thing.getThingDescription().title}`);
    }).catch((e) => {
        console.log(e);
    });
})