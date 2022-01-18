function produce(WoT, td) {

    WoT.produce(td).then((thing) => {
        lightLevel = 0;

        thing.setPropertyWriteHandler("lightLevel", (level) => lightLevel = level)
        thing.setPropertyReadHandler("lightLevel", async () => lightLevel)

        // Finally expose the thing
        thing.expose().then(() => {

            console.info(`${thing.getThingDescription().title} ready`);
        });
        console.log(`Produced ${thing.getThingDescription().title}`);
    }).catch((e) => {
        console.log(e);
    });
}

module.exports = {produce}