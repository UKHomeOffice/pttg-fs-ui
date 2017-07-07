# Financial Status Service UI #


## Overview ##

UI project to add basic interface to an API connected to Barclays Bank which can help Tier 2,4 & 5 applicants prove their financial status to government far quicker and easier than before.

## Tech ##

- Node
- Angular
- Cucumber JS & Gherkin

The frontend is an Angular 1 project.

Userbase is a known limited quantity of users internal to The Home Office and access to Chrome as a browser for those users is confirmed, it has therefore been determined that supporting other browsers is unnessesary - however web standards should be adheared to.

This project has a Node JS backend soley for the purposes of relaying asynchronous requests to the API which exists in its own project, this is one single Node file `server.js`.

## install ##

    // Checkout the project
    git clone git@github.com:UKHomeOffice/pttg-fs-ui.git `

    // move into cloned directory
    cd pttg-fs-ui

    // install modules
    npm install
    
    // generate the compiled/minified resources e.g. js, css
    gulp
    
    // start the project
    npm start

## Specification ##

The specification of this project is primarily via Feature files using a BDD approach.

These features are written in Gherkin and can be consumed by CucumberJS to run tests against those specifications.

## Tests ##

    // To run BDD feature file tests
    npm run test:bdd
    
    // to run Unit tests
    npm run test:unit
    
    // to run all tests
    npm run test:all
   
Tests are run using Chrome in Headless mode, you need an up-to-date version of Chrome to do this.

Tests may be run in Chrome with a browser window by changing the `var headless = true` in the config section of the `features/support/world.js`



