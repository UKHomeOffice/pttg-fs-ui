// require OUR APPLICATION THAT WE'RE TESTING
require('../../server.js')
//

require('chromedriver')

var mockdata = require('../step_definitions/mockdata')
var reporter = require('cucumber-html-reporter')
var seleniumWebdriver = require('selenium-webdriver')
var {defineSupportCode} = require('cucumber')
var globalDriver

// config
var usePhantomJS = false
var shareBrowserInstances = true
//

var browserName = usePhantomJS ? 'phantomjs' : 'chrome'

var getNewBrowser = function (name) {
  return new seleniumWebdriver.Builder().forBrowser(name).build()
}

if (shareBrowserInstances) {
  globalDriver = getNewBrowser(browserName)
}

function CustomWorld () {
  mockdata.clearAll()

  this.driver = shareBrowserInstances ? globalDriver : getNewBrowser(browserName)
  this.defaults = {
    applicationRaisedDate: '29/06/2016',
    endDate: '30/05/2016',
    inLondon: 'Yes',
    accommodationFeesPaid: '0',
    dependants: '1',
    continuationCourse: 'No',
    courseType: 'main',
    courseInstitution: 'Yes',
    dob: '13/05/1974'
  }
}

defineSupportCode(function ({setWorldConstructor}) {
  setWorldConstructor(CustomWorld)
})

defineSupportCode(function ({registerHandler}) {
  registerHandler('AfterFeatures', function (features, callback) {
    var options = {
      theme: 'foundation',
      jsonFile: 'report/cucumber_report.json',
      output: 'report/cucumber_report.html',
      reportSuiteAsScenarios: true,
      launchReport: true,
      metadata: {
        // 'App Version': '0.3.2',
        // 'Test Environment': 'STAGING',
        'Browser': 'Chrome'
        // 'Platform': 'Windows 10',
        // 'Parallel': 'Scenarios',
        // 'Executed': 'Remote'
      }
    }

    reporter.generate(options)
    callback()
  })
})
