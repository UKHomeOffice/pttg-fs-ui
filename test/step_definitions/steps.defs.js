var chai = require('chai')
var chaiAsPromised = require('chai-as-promised')
var expect = chai.expect
var _ = require('underscore')
var seleniumWebdriver = require('selenium-webdriver')
var until = seleniumWebdriver.until
var By = seleniumWebdriver.By
var {defineSupportCode} = require('cucumber')
var mockdata = require('./mockdata')
var request = require('request')
chai.use(chaiAsPromised)

var urls = {
  threshold: '/pttg/financialstatus/v1/:tier/maintenance/threshold',
  dailyBalance: '/pttg/financialstatus/v1/accounts/:sortCode/:accountNumber/dailybalancestatus',
  consent: '/pttg/financialstatus/v1/accounts/:sortCode/:accountNumber/consent',
  conditionCodes: '/pttg/financialstatus/v1/:tier/conditioncodes'
}

var radioElements = {
  inLondon: [{
    key: 'yes',
    value: 'Yes'
  }, {
    key: 'no',
    value: 'No'
  }],
  courseType: [
    {
      key: 'pre-sessional',
      value: 'Pre-sessional'
    }, {
      key: 'main',
      value: 'Main course degree or higher'
    }],
  courseInstitution: [{
    key: 'yes',
    value: 'Yes'
  }, {
    key: 'no',
    value: 'No'
  }],
  continuationCourse: [{
    key: 'true',
    value: 'Recognised body or HEI'
  }, {
    key: 'false',
    value: 'Other institution'
  }]
}

var getHttp = function (uri) {
  return new Promise(function (resolve, reject) {
    request({uri: uri}, function (error, response, body) {
      var statusCode = (response && response.statusCode) ? response.statusCode : 500
      if (error) {
        return reject({ status: statusCode, error: error, body: body })
      }
      return resolve({ status: statusCode, error: error, body: body })
    })
  })
}

var whenAllDone = function (promises) {
  var counter = 0
  var errors = []
  var results = []

  return new Promise(function (resolve, reject) {
    var done = function () {
      counter++
      if (counter >= promises.length) {
        if (errors.length) {
          return reject(errors)
        } else {
          return resolve(results)
        }
      }
    }
    _.each(promises, function (p) {
      p.then(function (result) {
        results.push(result)
        done()
      }, function (err) {
        errors.push(err)
        done()
      })
    })
  })
}

var justWait = function (howLong) {
  return new Promise(function (resolve, reject) {
    setTimeout(resolve, howLong)
  })
}

var isRadio = function (key) {
  return (_.keys(radioElements).indexOf(key) >= 0)
}

var toCamelCase = function (str) {
  return str.toLowerCase().replace(/(?:^\w|-|\b\w)/g, function (letter, index) {
    return (index === 0 || str.substr(index - 1, 1) === '-') ? letter.toLowerCase() : letter.toUpperCase()
  }).replace(/\s+/g, '')
}

var toCamelCaseKeys = function (data) {
  var result = {}
  _.each(data, function (val, key) {
    result[toCamelCase(key)] = val
  })
  return result
}

var shouldSee = function (text) {
  var xpath = "//*[contains(text(),'" + text + "')]"
  return seleniumWebdriver.until.elementLocated({xpath: xpath})
}

var confirmContentById = function (d, data, timeoutLength) {
  var promises = []
  _.each(data, function (val, key) {
    var expectation = new Promise(function (resolve, reject) {
      d.wait(until.elementLocated({id: key}), timeoutLength || 5 * 1000, 'TIMEOUT: Waiting for element #' + key).then(function (el) {
        // wait until driver has located the element
        return expect(el.getText()).to.eventually.equal(val)
      }).then(function (result) {
        // test OK
        return resolve(result)
      }, function (err) {
        // test failed
        return reject(err)
      })
    })
    promises.push(expectation)
  })
  return whenAllDone(promises)
}

var confirmInputValuesById = function (d, data) {
  var promises = []
  var xpath
  var expectation
  _.each(data, function (val, key) {
    if (isRadio(key)) {
      var theRadio = _.findWhere(radioElements[key], {value: val})
      xpath = '//input[@id="' + key + '-' + theRadio.key + '"]'
      expectation = d.wait(until.elementLocated({xpath: xpath}), 1000, 'TIMEOUT: Waiting for element ' + xpath).then(function (el) {
        return expect(el.isSelected()).to.eventually.equal(true)
      })
    } else {
      expectation = new Promise(function (resolve, reject) {
        xpath = '//input[@id="' + key + '"]'
        expectation = d.wait(until.elementLocated({xpath: xpath}), 1000, 'TIMEOUT: Waiting for element ' + xpath).then(function (el) {
          // wait until driver has located the element
          return el.getAttribute('value')
        }).then(function (elValue) {
          if (_.isNaN(Number(elValue)) || _.isNaN(Number(val))) {
            return expect(elValue).to.equal(val)
          }
          return expect(Number(elValue)).to.equal(Number(val))
        }).then(function (result) {
          return resolve(result)
        }, function (err) {
          return reject(err)
        })
      })
    }
    promises.push(expectation)
  })
  return whenAllDone(promises)
}

var expandFields = function (obj) {
  var bits

  var splitDates = function (o, key) {
    if (o[key] || o.key === '') {
      bits = o[key].split('/')
      o[key + 'Day'] = bits[0]
      o[key + 'Month'] = bits[1] || ''
      o[key + 'Year'] = bits[2] || ''
      delete o[key]
    }
    return o
  }

  _.each(['dob', 'applicationRaisedDate', 'endDate', 'courseStartDate', 'courseEndDate', 'originalCourseStartDate'], function (key) {
    obj = splitDates(obj, key)
  })

  if (_.has(obj, 'sortCode')) {
    bits = obj.sortCode.split('-')
    obj['sortCodePart1'] = bits[0]
    obj['sortCodePart2'] = bits[1] || ''
    obj['sortCodePart3'] = bits[2] || ''
    delete obj.sortCode
  }

  if (_.has(obj, 'accommodationFeesAlreadyPaid')) {
    obj.accommodationFeesPaid = obj.accommodationFeesAlreadyPaid
    delete obj.accommodationFeesAlreadyPaid
  }

  return obj
}

var selectRadio = function (d, key, val) {
  var rID = key + '-' + val.toLowerCase() + '-label'
  return d.findElement({id: rID}).then(function (el) {
    return el.click().then(function (result) {
      return true
    }, function () {
      return false
    })
  }, function () {
    return true
  })
}

var inputEnterText = function (d, key, val) {
  return d.findElement({id: key}).then(function (el) {
    return el.sendKeys(val)
  }).then(function (result) {
    return true
  }, function () {
    return true
  })
}

var completeInput = function (d, key, val) {
  if (isRadio(key)) {
    return selectRadio(d, key, val)
  }
  return inputEnterText(d, key, val)
}

var completeInputs = function (d, data) {
  var promises = []
  _.each(data, function (val, key) {
    promises.push(completeInput(d, key, val))
  })
  return Promise.all(promises)
}

var submitAction = function (d) {
  return d.findElement({className: 'button'}).click().then(function () {
    return seleniumWebdriver.until.elementLocated({id: 'outcome'})
  }).then(function () {
    return justWait(500)
  })
}

var getTableHeaders = function (d, id) {
  return d.wait(until.elementLocated({id: id}), 5 * 1000, 'TIMEOUT: Waiting for element #' + id).then(function () {
    // wait until driver has located the element
    var selector = '#' + id + ' th'
    return d.findElements(By.css(selector))
  }).then(function (headers) {
    var promises = []
    _.each(headers, function (el) {
      promises.push(el.getText())
    })
    return whenAllDone(promises)
  }, function (err) {

  })
}

defineSupportCode(function ({Given, When, Then}) {
  Given('the api is unreachable', function (callback) {
    mockdata.stubHealthz(503)
    mockdata.stubIt(urls.threshold, '', 404)
    callback()
  })

  Then('the api health check response has status {int}', function (int, callback) {
    mockdata.stubHealthz(int)
    callback()
  })

  Given('the api response is empty', function (callback) {
    mockdata.stubIt(urls.threshold, '', 200)
    callback()
  })

  Given('the api response is delayed for {int} seconds', {timeout: 60000}, function (int, callback) {
    mockdata.stubItFile(urls.threshold, 'threshold-t4.json', 200, int * 1000)
    callback()
  })

  Given('the api response is garbage', function (callback) {
    mockdata.stubHealthz(503)
    mockdata.stubIt(urls.threshold, 'dsadsadasda', 500)
    mockdata.stubIt(urls.dailyBalance, 'dsadsadasda', 500)
    mockdata.stubIt(urls.consent, 'dsadsadasda', 500)
    callback()
  })

  Given('the api response has status {int}', function (int, callback) {
    mockdata.stubIt(urls.threshold, '', int)
    callback()
  })

  Given(/^the api response is a validation error - (.+) parameter$/, function (ref, callback) {
    mockdata.stubItFile(urls.threshold, 'validation-error-' + ref + '.json', 400)
    callback()
  })

  Given('the account does not have sufficient funds', function (callback) {
    mockdata.stubItFile(urls.dailyBalance, 'dailyBalanceFail-low-balance.json')
    callback()
  })

  Given(/^the api condition codes response will be (.+)$/, function (codes, callback) {
    mockdata.stubItFile(urls.conditionCodes, 'conditioncodes-' + codes + '.json')
    callback()
  })

  Given(/^the api threshold response will be (.+)$/, function (ref, callback) {
    mockdata.stubItFile(urls.threshold, 'threshold-' + ref + '.json')
    callback()
  })

  Given(/^the api consent response will be (.+)$/, function (ref, callback) {
    if (_.isNaN(Number(ref))) {
      mockdata.stubItFile(urls.consent, 'consentcheckresponse-' + ref + '.json')
    } else {
      mockdata.stubIt(urls.consent, '', ref)
    }

    callback()
  })

  Given(/^the api daily balance response will (.+)$/, function (ref, callback) {
    mockdata.stubItFile(urls.dailyBalance, 'dailyBalance' + ref + '.json')
    callback()
  })

  Given('the account has sufficient funds', function (callback) {
    mockdata.stubItFile(urls.dailyBalance, 'dailyBalancePass.json')
    callback()
  })

  Given('caseworker is using the financial status service ui', function () {
    var d = this.driver

    return d.get('http://127.0.0.1:8000/#!/fs/').then(function () {
      return d.navigate().refresh()
    }).then(function () {
      var el = d.findElement({id: 'pageTitle'})
      return expect(el.getText()).to.eventually.equal('Check financial status')
    })
  })

  Given('the default details are', function (table) {
    this.defaults = toCamelCaseKeys(_.object(table.rawTable))
    return true
  })

  Given('consent is sought for the following:', function (table) {
    var d = this.driver
    var data = expandFields(toCamelCaseKeys(_.object(table.rawTable)), this.defaults)
    return completeInputs(d, data).then(function () {
      return submitAction(d)
    })
  })

  Given('the progress bar is displayed', function () {
    var d = this.driver
    return d.wait(until.elementLocated({className: 'progress-bar'}), 2 * 1000, 'TIMEOUT: Waiting for element .progress-bar').then(function () {
      return d.findElement({className: 'progress-bar'})
    }).then(function (el) {
      return expect(el.isDisplayed()).to.eventually.equal(true)
    })
  })

  When(/^caseworker is on page (.+)$/, function (str) {
    var d = this.driver
    return d.get('http://127.0.0.1:8000/#!/fs/' + str).then(function () {
      return d.navigate().refresh()
    })
  })

  When('the financial status check is performed', {timeout: 10 * 1000}, function () {
    var d = this.driver
    var data = expandFields(_.defaults(this.defaults))
    return completeInputs(d, data).then(function () {
      return submitAction(d)
    })
  })

  When('the financial status check is performed with', {timeout: 10 * 1000}, function (table) {
    var d = this.driver
    var data = expandFields(_.defaults(toCamelCaseKeys(_.object(table.rawTable)), this.defaults))
    return completeInputs(d, data).then(function () {
      return submitAction(d)
    })
  })

  When('after at least {int} seconds', {timeout: 60 * 1000}, function (int) {
    return justWait(int * 1000)
  })

  When(/^the (.+) button is clicked$/, function (btnRef) {
    return this.driver.findElement({id: toCamelCase(btnRef) + 'Btn'}).click().then(function () {
      return justWait(200)
    })
  })

  Then('the result table contains the following', function (table) {
    var data = toCamelCaseKeys(_.object(table.rawTable))
    var d = this.driver
    return d.findElement({id: 'result'}).then(function (el) {
      return confirmContentById(d, data)
    })
  })

  Then('the service displays the following page content', function (table) {
    var data = toCamelCaseKeys(_.object(table.rawTable))
    return confirmContentById(this.driver, data)
  })

  Then('the service displays the following result', function (table) {
    var data = toCamelCaseKeys(_.object(table.rawTable))
    var d = this.driver
    return d.wait(until.elementLocated({id: 'outcome'}), 5 * 1000).then(function () {
      return confirmContentById(d, data)
    })
  })

  Then('the service displays the following error message', function (table) {
    var data = toCamelCaseKeys(_.object(table.rawTable))
    data['validation-error-summary-heading'] = 'There\'s some invalid information'
    return confirmContentById(this.driver, data)
  })

  Then('the error summary list contains the text', function (table) {
    var el = this.driver.findElement({id: 'error-summary-list'})
    return el.getText().then(function (txt) {
      var promiseList = []
      _.each(table.rawTable, function (row) {
        promiseList.push(expect(txt).to.contain(row[0]))
      })
      return Promise.all(promiseList)
    })
  })

  Then('the availability warning box should not be shown', function (callback) {
    var d = this.driver
    d.wait(until.elementLocated({className: 'availability'}), 2 * 1000).then(function () {
      callback(null, 'The availability warning box should not be shown')
    }, function () {
      callback()
    })
  })

  Then('the inputs will be populated with', function (table) {
    var data = expandFields(toCamelCaseKeys(_.object(table.rawTable)))
    return confirmInputValuesById(this.driver, data)
  })

  Then(/^the service displays the following ([a-zA-Z0-9]+) headers in order$/, function (tableID, table) {
    var test
    return getTableHeaders(this.driver, tableID).then(function (results) {
      var errors = []
      _.each(table.rawTable, function (row, r) {
        test = expect(results[r]).to.equal(row[0])
        if (test !== true) {
          errors.push(test)
        }
      })
      test = expect(results.length).to.equal(table.rawTable.length)
      if (test !== true) {
        errors.push(test)
      }
      return errors
    })
  })

  Then('the copied text includes', function (table, callback) {
    callback()
  })

  Then('the service has the following links', function (table) {
    var promises = []
    var errors = []
    var test
    var d = this.driver
    _.each(table.rawTable, function (row, r) {
      var key = toCamelCase(row[0])
      var val = row[1]
      var lnk = row[2]
      var theElement
      var expectation = d.wait(until.elementLocated({id: key}), 5000, 'TIMEOUT: Waiting for element #' + key).then(function (el) {
        theElement = el
        return el.getText()
      }).then(function (txt) {
        test = expect(txt).to.equal(val)
        if (test !== true) {
          errors.push(test)
        }
        return theElement.getAttribute('href')
      }).then(function (href) {
        test = expect(href).to.equal(lnk)
        if (test !== true) {
          errors.push(test)
        }
        return errors
      })
      promises.push(expectation)
    })
    return whenAllDone(promises)
  })

  Then('the service displays the following page content within {int} seconds', {timeout: 20000}, function (int, table) {
    var data = toCamelCaseKeys(_.object(table.rawTable))
    return confirmContentById(this.driver, data, int * 1000)
  })

  Then('the liveness response status should be {int}', function (int) {
    return getHttp('http://localhost:8000/ping').then(function (result) {
      return expect(result.status).to.equal(int)
    }, function (error) {
      return expect(error.status).to.equal(int)
    })
  })

  Then('the readiness response status should be {int}', function (int) {
    return getHttp('http://localhost:8000/healthz').then(function (result) {
      return expect(result.status).to.equal(int)
    }, function (error) {
      return expect(error.status).to.equal(int)
    })
  })

  Then('the connection attempt count should be {int}', function (int, callback) {
    callback(null, 'pending')
  })
})
