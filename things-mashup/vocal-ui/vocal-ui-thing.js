const Servient = require('@node-wot/core').Servient;
const HttpServer = require('@node-wot/binding-http').HttpServer;
const td = require('./vocal-ui-thing-description.json')

const servient = new Servient();
servient.addServer(new HttpServer({
    port: 8080
}));


servient.start().then((WoT) => {

    WoT.produce(td).then((thing) => {

        thing.setActionHandler("triggerVocalCommand", (params, options) => {
            let command;
            if (options && typeof options === "object" && "uriVariables" in options) {
                const uriVariables = options["uriVariables"];
                command = "command" in uriVariables ? uriVariables["command"] : null;
            }

            if (command != null) {
                thing.emitEvent("vocalCommand", {
                    message: "New vocal command by user",
                    command: command
                })
                return new Promise((resolve, reject) => {
                    resolve({
                        result: true,
                        message: `New command triggered`
                    });
                })
            } else {
                return new Promise((resolve, reject) => {
                    resolve({
                        result: false,
                        message: `Can't handle the command`
                    });
                })
            }
        })
        // Finally expose the thing
        thing.expose().then(() => {

            thing.subscribeEvent("vocalCommand", (e) => console.log(e))

            console.info(`${thing.getThingDescription().title} ready`);
        });
        console.log(`Produced ${thing.getThingDescription().title}`);
    }).catch((e) => {
        console.log(e);
    });
})