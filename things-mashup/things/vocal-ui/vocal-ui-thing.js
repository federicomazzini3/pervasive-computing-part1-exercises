function produce (WoT, td) {

    let lastCommand;

    WoT.produce(td).then((thing) => {

        thing.setPropertyWriteHandler("command", (command) => {
            console.log("new command : " + command);
            lastCommand = command
            thing.emitEvent("newCommand", {
                message: "New command by the user",
                command: lastCommand
            })
        })
        // Finally expose the thing
        thing.expose().then(() => {

            thing.subscribeEvent("newCommand", (e) => console.log(e))

            console.info(`${thing.getThingDescription().title} ready`);
        });
        console.log(`Produced ${thing.getThingDescription().title}`);
    }).catch((e) => {
        console.log(e);
    });
}

module.exports = { produce };