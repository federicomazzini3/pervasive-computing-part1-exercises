# Smart room - Things mashup

Sample things mashup of a smart room with
- Light
    - Providing affordances to control the main light, including increasing/decreasing the intensity.
- Light Sensor
    - Providing affordances to know the current light level inside the room 

- Presence detector
    - Providing affordances to know if there is someone in the room or not. An event is generated to signal entrance and exit 
- Vocal UI
    - Providing affordances to get notified of the vocal commands issued by the user in the room 


## Goals to achieve for the room 

- Don't waste energy
    - When no one is in the room, the light should be off.
    - The light should be off even in the case that the current light level is above a threshold LT1 
- Keep a proper light level for the user
    - If there is someone in the room, then the light level should be kept not below  some level LT2, by properly controlling the light. 
- Listen to the user
    - A user can request to switch on or off the light by issuing a proper vocal command
    - User's requests override policies (1) and (2), that is: 
        - if the user requested to switch off the light, the light should be kept off or on, the light should be kept off or on in spite of (1)(2).
        - The user can request to reset overriding by issuing a "reset" vocal command.


## Implementations
The smart service has been implemented with two different approaches:
- As a static mashup, using node-red 
- As an agent (or a multi-agent systems) in any of the approaches/architectures discussed during the course

# Node-red
### Requirement
- NodeJS
- NPM

### Run it
`npm install` to install all the modules

`node room.js` to start the smart room service and the things inside

`node-red` to start node red.


