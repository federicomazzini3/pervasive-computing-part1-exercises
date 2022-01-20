const Servient = require('@node-wot/core').Servient;
const HttpServer = require('@node-wot/binding-http').HttpServer;

const vocalUI = require('./vocal-ui/vocal-ui-thing.js')
const presenceDetector = require('./presence-detector/presence-detector-thing.js')
const lightSensor = require('./light-sensor-thing/light-sensor-thing.js')
const light = require('./light-thing/light-thing.js')

const vocalUITD = require('./vocal-ui/vocal-ui-thing-description.json')
const presenceDetectorTD = require('./presence-detector/presence-detector-thing-description.json')
const lightSensorTD = require('./light-sensor-thing/light-sensor-thing-description.json')
const lightTD = require('./light-thing/light-thing-description.json')

const servient = new Servient();
servient.addServer(new HttpServer({
    port: 8084
}));


servient.start().then((WoT) => {
    vocalUI.produce(WoT, vocalUITD);
    presenceDetector.produce(WoT, presenceDetectorTD);
    lightSensor.produce(WoT, lightSensorTD);
    light.produce(WoT, lightTD);
})