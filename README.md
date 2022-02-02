# Pervasive computing
Mini project about Web of Things and Digital Twins 

## Exercise #1 - About WoT web thing design 

We want to develop an interoperable smart coffee machine (smart-cm) for the Campus, as a WoT Thing.   The machine has no physical UI, it can be used only via app (smartphone or whatever device). It provides an API for users and for maintainers. In this exercise we focus on users. 

Given a specific smart coffee machine (identified in some way), a user with a smartphone should be able to get a coffee (or a tea, or one of the products available from the specific machine), with the specified options (e.g. sugar level), as well as check the availability or state the machine, and the current availability of products as well.

The idea is that in the Campus there could be smart coffee machines produced by different vendors but having a shared smart coffee machine Thing Description, so that they can be used by any app working with that TD.

Given this scenario, then:
- Think about an abstract model of the smart coffee machine with essential capabilities to implement the use case above, and define a Thing Description based on the WoT model.  An Open API perspective should be adopted, so that the smart coffee machine web thing should provide a minimal API focused on its capabilities.
- Design and develop a simplified prototype implementation of the smart-cm web thing (using the software stack that you prefer and mocks up where needed) and a basic user app (mobile or desktop) allowing users to interact with a smart-cm, to do test/demo.

## Exercise #2 - About things organisation and mashup

In a smart room we have the following smart things/services: 

- A light thing
  - providing affordances to control the main light, including increasing/decreasing the intensity.
- A light sensor thing
  - providing affordances to know the current light level inside the room 
- A presence detector thing
  - providing affordances to know if there is someone in the room or not. An event is generated to signal entrance and exit 
- A vocal UI thing (service)
  - providing affordances to get notified of the vocal commands issued by the user in the room 

Let's consider that each thing/service is designed according to the WoT approach --  each thing having its own TD.

We want to realise a smart service at the room level to achieve the following  policies/goals:

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

Given this scenario:

- Provide a simple implementation of the things/services  as WoT web things, with their own TDs and define a strategy/approach to make the things discoverable at the room level
- Implement the smart service at the room level using two different approaches:
  - As a static mashup, using node-red (or any other flow-based / process-based programming approach)
  - As an agent (or a multi-agent systems) in any of the approaches/architectures discussed during the course
  
  
  
## Exercise #3 - About IIoT and DT

Let's consider the smart coffee machine case of exercise #1. In this case we consider the maintainer point of view. 

A maintainer is interested in monitoring the state and behaviour of a smart coffee machine, in order to provide a better service and optimize resources.  In particular a maintainer is interested to:
- track the quantity of the consumed/remaining resources and items (e.g. coffee, tea, sugar, glasses,...), in order to organize the refill
- track the machine usage, to generate alerts if some specific pattern occurs  
- monitor the state of the machine (working, not available, out of service), in order to promptly react to problems

A single maintainer can have a large dynamic set of smart-cm to manage, distributed in the territory.  

Given this scenario:
- Think about a cloud-based solution to manage the set of smart-cm, to be modelled using the digital twin architecture, eventually integrated with the solution designed  in exercise #1
- Implement a simplified version adopting any available IIoT / DT platform (e.g. Ditto, Azure) or a custom ad hoc implementation, integrated with the prototype developed in exercise #1, in particular including a simple dashboard (as a web app) that makes it possible to track and monitor the smart-cms.


