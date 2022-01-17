const Servient = require('@node-wot/core').Servient;
const HttpServer = require('@node-wot/binding-http').HttpServer;
const td = require('./light-thing-description.json')

const servient = new Servient();
servient.addServer(new HttpServer({
    port: 8080
}));

servient.start().then((WoT) => {
    let isOn;
    let intensity;

    const intensityStepDefault = (direction) => {
        if (direction == "down")
            if (intensity - 10 >= 0) return 10
        else return intensity

        if (direction == "up")
            if ((intensity + 10) <= 100) return 10
        else return 100 - intensity

        return 10
    };

    const status = () => {
        return {
            state: isOn ? "on" : "off",
            intensity: isOn ? intensity : 0
        }
    }

    WoT.produce(td).then((thing) => {
        isOn = false;
        intensity = 100;

        thing.setPropertyReadHandler("isOn", async () => isOn);

        thing.setPropertyReadHandler("isOff", async () => !isOn);

        thing.setPropertyReadHandler("intensity", async () => !isOn ? 0 : intensity);

        thing.setPropertyReadHandler("status", async () => status());

        thing.setActionHandler("switch", (params, options) => {
            return performSwitch(
                () => {
                    isOn = !isOn;
                    thing.emitEvent("changeState", `${isOn ? "on" : "off"}`)
                },
                `Light switched ${isOn ? "on" : "off"}`
            )
        })

        thing.setActionHandler("switchOn", (params, options) => {
            return performSwitch(
                () => !isOn,
                () => {
                    isOn = true;
                    thing.emitEvent("changeState", "on")
                },
                `Light switched on`,
                `Light is already on`
            )
        })

        thing.setActionHandler("switchOff", (params, options) => {
            return performSwitch(
                () => isOn,
                () => {
                    isOn = false
                    thing.emitEvent("changeState", "off")
                },
                `Light switched off`,
                `Light is already off`
            )
        })

        thing.setActionHandler("increase", (params, options) => {
            return performIntensityModify(
                isOn,
                options,
                intensityStepDefault("up"),
                (step) => {
                    intensity += step;
                    if(step != 0) thing.emitEvent("changeIntensity", intensity)
                },
                (step) => `Light increased of ${step}%`,
                (reason) => `Light not increased because ${reason}`)
        })

        thing.setActionHandler("decrease", (params, options) => {
            return performIntensityModify(
                isOn,
                options,
                intensityStepDefault("down"),
                (step) => {
                    intensity -= step;
                    if(step != 0) thing.emitEvent("changeIntensity", intensity)
                },
                (step) => `Light decreased of ${step}%`,
                (reason) => `Light not decreased because ${reason}`)
        })

        thing.subscribeEvent("changeState", (e) => console.log("########## EVENT NEW STATE " + e + " ##########"))
        thing.subscribeEvent("changeIntensity", (e) => console.log("########## EVENT NEW INTENSITY " + e + " ##########"))


        // Finally expose the thing
        thing.expose().then(() => {
            console.info(`${thing.getThingDescription().title} ready`);
        });
        console.log(`Produced ${thing.getThingDescription().title}`);

    }).catch((e) => {
        console.log(e);
    });
})

const performSwitch = (evaluate, action, succMessage, failMessage) => {
    if(evaluate()) {
        action();
        return new Promise((resolve, reject) => {
            resolve({
                result: true,
                message: succMessage
            })
        })
    } else {
        return new Promise((resolve, reject) => {
            resolve({
                result: false,
                message: failMessage
            })
        })
    }
}

const performIntensityModify = (isOn, options, defaultStep, action, succMessage, failMessage) => {
    if (isOn) {
        let step = defaultStep;
        if (options && typeof options === "object" && "uriVariables" in options) {
            const uriVariables = options["uriVariables"];
            step = "step" in uriVariables ? uriVariables["step"] : defaultStep;
        }

        action(step);

        return new Promise((resolve, reject) => {
            resolve({
                result: true,
                message: succMessage(step)
            })
        })
    } else {
        return new Promise((resolve, reject) => {
            resolve({
                result: false,
                message: failMessage("the light is off")
            })
        })
    }
}

/*
SCHELETON
const Servient = require('@node-wot/core').Servient;
const HttpServer = require('@node-wot/binding-http').HttpServer;
const td = require('./light-thing-description.json')

const servient = new Servient();
servient.addServer(new HttpServer({
    port: 8080
}));

servient.start().then((WoT) => {
    WoT.produce({

    }).then((thing) => {

        // Finally expose the thing
        thing.expose().then(() => {
            console.info(`${thing.getThingDescription().title} ready`);
        });
        console.log(`Produced ${thing.getThingDescription().title}`);
    }).catch((e) => {
        console.log(e);
    });
})

*/