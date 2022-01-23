function produce(WoT, td) {

    WoT.produce(td).then((thing) => {
        lightLevel = 0;
        lastLightLevelNotified = lightLevel;

        thing.setPropertyWriteHandler("lightLevel", (level) => {
            if(!isNaN(level)){
                let newLevel = parseInt(level);
                let incrementThanLastNotify = Math.abs(newLevel - lastLightLevelNotified) * 100 / lastLightLevelNotified;
                if(incrementThanLastNotify > 10){
                    thing.emitEvent("changeLight", newLevel);
                    lastLightLevelNotified = newLevel;
                }
                lightLevel = newLevel;
            }
        })
        
        thing.setPropertyReadHandler("lightLevel", async () => lightLevel)

        // Finally expose the thing
        thing.expose().then(() => {
            console.info(`${thing.getThingDescription().title} ready`);
            thing.subscribeEvent("changeLight", ev => console.log("change light: " + ev))
        });
        console.log(`Produced ${thing.getThingDescription().title}`);
    }).catch((e) => {
        console.log(e);
    });
}

module.exports = {produce}