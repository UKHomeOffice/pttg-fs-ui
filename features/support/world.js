// require OUR APPLICATION THAT WE'RE TESTING

// OVERRIDE THE TIMERS FOR TESTING
process.env.TIMER_BAR_DURATION = 5000
process.env.TIME_BETWEEN_CONSENT_AND_BALANCE = 500

require('../../server.js')
//

require('chromedriver')

var mockdata = require('../step_definitions/mockdata')

var seleniumWebdriver = require('selenium-webdriver')
var chrome = require('selenium-webdriver/chrome')
var {defineSupportCode} = require('cucumber')
var globalDriver
var os = require('os')

// config
var shareBrowserInstances = true
var browserName = 'chrome'
var headless = (process.env.HEADLESS !== false && process.env.HEADLESS !== 'false')
//
console.log(os.cpus())
console.log('Load average', os.loadavg())
console.log('Free mem (MB)', Math.round((os.freemem() / (1024 * 1024))))

var getNewBrowser = function (name) {
  var builder = new seleniumWebdriver.Builder()
  var opts = new chrome.Options()
  if (headless) {
    opts.addArguments(['headless', 'no-sandbox'])
  }
  opts.addArguments('disable-extensions')
  // opts.setChromeBinaryPath('/Applications/Google Chrome Canary.app/Contents/MacOS/Google Chrome Canary')
  builder.setChromeOptions(opts)

  var forBrowser = builder.forBrowser(name)

  var driver = forBrowser.build()
  // driver.manage().window().setSize(1280, 1024)
  return driver
}

if (shareBrowserInstances) {
  globalDriver = getNewBrowser(browserName)
}

function CustomWorld (done) {
  console.log('Load average', os.loadavg())
  console.log('Free mem (MB)', Math.round((os.freemem() / (1024 * 1024))))

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
  this.driver.get('http://127.0.0.1:8000/#!/fs/').then(done)
}

defineSupportCode(function ({setWorldConstructor}) {
  setWorldConstructor(CustomWorld)
})

defineSupportCode(function ({registerHandler}) {
  //
  registerHandler('AfterFeatures', function (features, callback) {
    callback()
  })
})
