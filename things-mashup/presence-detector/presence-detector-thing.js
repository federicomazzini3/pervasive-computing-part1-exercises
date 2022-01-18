/*
If the sensor detect movement it starts a timer. Timer is resetted every time the sensor detect a movement.
If the sensor doesn't detect any movement for X seconds (default 60), the movement value (called presence) is set to false.

The user can specify the seconds for the timer. If a timer was already started, it will be modified with the difference. 
*/

function produce(WoT, td) {
    let presenceTimer = 30; //seconds after which the presence is reset
    let currentTimer = presenceTimer; //current seconds left to reset the presence
    let currentCountdown;
    WoT.produce(td).then((thing) => {

        thing.writeProperty("presence", false);
        thing.writeProperty("presenceTimer", presenceTimer);

        thing.setPropertyWriteHandler("presence", (presence) => {

            currentTimer = presenceTimer;

            if (presence) {
                clearInterval(currentCountdown)
                thing.emitEvent("detectPresence", "The sensor has detected movement")
                currentCountdown = setInterval(() => {
                    currentTimer -= 1;
                    console.log(`Seconds left to reset presence: ${currentTimer}`)
                    if (currentTimer == 0) {
                        console.log("Presence reset");
                        clearInterval(currentCountdown);
                        thing.writeProperty("presence", false);
                    }
                }, 1000);
            } else
                thing.emitEvent("nonDetectPresence", `The sensor hasn't detect movement for ${presenceTimer} seconds`)
        })

        thing.setActionHandler("setPresenceTimer", (params, options) => {
            let newPresenceTimer;
            if (options && typeof options === "object" && "uriVariables" in options) {
                const uriVariables = options["uriVariables"];
                newPresenceTimer = "seconds" in uriVariables ? uriVariables["seconds"] : presenceTimer;
            }

            if (newPresenceTimer == presenceTimer)
                return new Promise((resolve, reject) => {
                    resolve({
                        result: false,
                        message: `The current presence timer and the new presence timer are the same.`
                    })
                })

            if (newPresenceTimer < 30) return new Promise((resolve, reject) => {
                resolve({
                    result: false,
                    message: `Seconds are too few.`
                })
            })

            if (newPresenceTimer > 3600) return new Promise((resolve, reject) => {
                resolve({
                    result: false,
                    message: `Seconds are too many.`
                })
            })

            if (isNaN(newPresenceTimer)) return new Promise((resolve, reject) => {
                resolve({
                    result: false,
                    message: `Insert a valid number of seconds.`
                })
            })

            if (newPresenceTimer - presenceTimer > 0)
                currentTimer += newPresenceTimer - presenceTimer;
            else currentTimer = newPresenceTimer
            presenceTimer = newPresenceTimer;

            return new Promise((resolve, reject) => {
                resolve({
                    result: true,
                    message: `Presence timer set`
                })
            })

        })

        // Finally expose the thing
        thing.expose().then(() => {
            console.info(`${thing.getThingDescription().title} ready`);
        });
        console.log(`Produced ${thing.getThingDescription().title}`);
    }).catch((e) => {
        console.log(e);
    });


}

module.exports = { produce };