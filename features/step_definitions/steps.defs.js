const chai = require('chai')
const chaiAsPromised = require('chai-as-promised')
const expect = chai.expect
const _ = require('underscore')
const seleniumWebdriver = require('selenium-webdriver')
const until = seleniumWebdriver.until
const Keys = seleniumWebdriver.keys
const By = seleniumWebdriver.By
const {defineSupportCode} = require('cucumber')
const mockdata = require('./mockdata')
const request = require('request')
chai.use(chaiAsPromised)

const urls = {
  threshold: '/pttg/financialstatus/v1/:tier/maintenance/threshold',
  dailyBalance: '/pttg/financialstatus/v1/accounts/:sortCode/:accountNumber/dailybalancestatus',
  consent: '/pttg/financialstatus/v1/accounts/:sortCode/:accountNumber/consent',
  conditionCodes: '/pttg/financialstatus/v1/:tier/conditioncodes'
}

const radioElements = {
  inLondon: [{
    key: 'yes',
    value: 'Yes'
  },
  {
    key: 'no',
    value: 'No'
  }],
  courseType: [
    {
      key: 'pre-sessional',
      value: 'Pre-sessional'
    },
    {
      key: 'main',
      value: 'Main course degree or higher'
    }],
  continuationCourse: [{
    key: 'yes',
    value: 'Yes'
  },
  {
    key: 'no',
    value: 'No'
  }],
  courseInstitution: [{
    key: 'true',
    value: 'Recognised body or HEI'
  },
  {
    key: 'false',
    value: 'Other institution'
  }],
  match: [{
    key: 'yes',
    value: 'Yes'
  },
  {
    key: 'no',
    value: 'No'
  }],
  whynot: [
    {
      key: 'calculation',
      value: 'Total funds required is incorrect'
    },
    {
      key: 'ltrdate',
      value: 'LTR calculated end date is incorrect'
    },
    {
      key: 'name',
      value: 'Barclays customer name does not match applicant name'
    },
    {
      key: 'balances',
      value: 'Closing balance data does not correspond with paper evidence'
    }
  ]
}

const getHttp = function (uri) {
  return new Promise(function (resolve, reject) {
    request({uri: uri}, function (error, response, body) {
      const statusCode = (response && response.statusCode) ? response.statusCode : 500
      if (error) {
        return reject({status: statusCode, error: error, body: body})
      }
      return resolve({status: statusCode, error: error, body: body})
    })
  })
}

const whenAllDone = function (promises) {
  let counter = 0
  const errors = []
  const results = []

  return new Promise(function (resolve, reject) {
    const done = function () {
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

const justWait = function (howLong) {
  return new Promise(function (resolve, reject) {
    setTimeout(resolve, howLong)
  })
}

const isRadio = function (key) {
  return (_.keys(radioElements).indexOf(key) >= 0)
}

const toCamelCase = function (str) {
  return str.toLowerCase().replace(/(?:^\w|-|\b\w)/g, function (letter, index) {
    return (index === 0 || str.substr(index - 1, 1) === '-') ? letter.toLowerCase() : letter.toUpperCase()
  }).replace(/\s+/g, '')
}

const toCamelCaseKeys = function (data) {
  const result = {}
  _.each(data, function (val, key) {
    result[toCamelCase(key)] = val
  })
  return result
}

const shouldSee = function (text) {
  const xpath = "//*[contains(text(),'" + text + "')]"
  return seleniumWebdriver.until.elementLocated({xpath: xpath})
}

const confirmVisible = function (d, data, visibility, timeoutLength) {
  const promises = []
  _.each(data, function (val, key) {
    const expectation = new Promise(function (resolve, reject) {
      d.wait(until.elementLocated({id: key}), timeoutLength || 1 * 1000, 'TIMEOUT: Waiting for element #' + key).then(function (el) {
        return el.isDisplayed()
      }).then(function (result) {
        if (result === !!visibility) {
          return resolve(result)
        } else {
          return reject()
        }
      }, function (err) {
                // test failed
        if (visibility === false) {
          return resolve(true)
        } else {
          return reject(err)
        }
      })
    })
    promises.push(expectation)
  })
  return whenAllDone(promises)
}

const confirmContentById = function (d, data, timeoutLength) {
  const promises = []
  _.each(data, function (val, key) {
    const expectation = new Promise(function (resolve, reject) {
      let elem = null
      d.wait(until.elementLocated({id: key}), timeoutLength || 5 * 1000, 'TIMEOUT: Waiting for element #' + key).then(function (el) {
        elem = el
        return d.wait(elem.isDisplayed(), 1000, 'TIMEOUT: Waiting for element to be visible #' + key)
      }).then(function (visible) {
        // it is displayed
        return elem.getTagName()
      }).then(function (n) {
        if (n === 'input') {
          // for an input compare the value attribute
          return elem.getAttribute('value')
        }
        // otherwise just get the test of the element
        return elem.getText()
      }).then(function (result) {
        // here we are removing line breaks from both the bdd sepcified value and the return from Chromedriver
        // because of an issue where in some environments it compares \n correctly and in others it doesn't
        val = val.replace(/\n/g, ' ')
        result = result.replace(/\n/g, ' ')
        return resolve(expect(result).to.equal(val))
      }, function (err) {
        return reject(err)
      })
    })
    promises.push(expectation)
  })
  return whenAllDone(promises)
}

const confirmInputValuesById = function (d, data) {
  const promises = []
  let xpath
  let expectation
  _.each(data, function (val, key) {
    if (isRadio(key)) {
      const theRadio = _.findWhere(radioElements[key], {value: val})
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

const expandFields = function (obj) {
  let bits

  const splitDates = function (o, key) {
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

const selectRadio = function (d, key, val) {
  const rID = key + '-' + val.toLowerCase() + '-label'
    // console.log('selectRadio', rID)
  let elem
  return d.findElement({id: rID}).then(function (el) {
    elem = el
        // return el.SendKeys(Keys.Return)
    return el.click()
  }).then(function (result) {
    return true
  }, function (err) {
        // console.log('NOT CLICKED', key, val, elem, err)
    return false
  })
}

const clickCheckbox = function (d, key) {
  const rID = key + '-label'
  let elem
  return d.findElement({id: rID}).then(function (el) {
    elem = el
    return el.click()
  }).then(function (result) {
    return true
  }, function (err) {
    return false
  })
}

const inputEnterText = function (d, key, val) {
  var el
  return d.findElement({id: key}).then(function (e) {
    el = e
    return e.getAttribute('type')
  }).then(function (t) {
    if (t === 'checkbox') {
      return clickCheckbox(d, key)
    }
    return el.sendKeys(val)
  }).then(function (result) {
    return true
  }, function () {
    return true
  })
}

const completeInput = function (d, key, val) {
    // console.log('completeInput', key, val)
  if (isRadio(key)) {
    return selectRadio(d, key, val)
  }
  return inputEnterText(d, key, val)
}

const completeInputs = function (d, data) {
  const promises = []
  _.each(data, function (val, key) {
    promises.push(completeInput(d, key, val))
  })
  return Promise.all(promises)
}

const submitAction = function (d) {
  return d.findElement({className: 'button'}).click().then(function () {
    return seleniumWebdriver.until.elementLocated({id: 'outcome'})
  }).then(function () {
    return justWait(500)
  })
}

const getTableHeaders = function (d, id) {
  return d.wait(until.elementLocated({id: id}), 5 * 1000, 'TIMEOUT: Waiting for element #' + id).then(function () {
        // wait until driver has located the element
    const selector = '#' + id + ' th'
    return d.findElements(By.css(selector))
  }).then(function (headers) {
    const promises = []
    _.each(headers, function (el) {
      promises.push(el.getText())
    })
    return whenAllDone(promises)
  }, function (err) {

  })
}

defineSupportCode(function ({Given, When, Then}) {
  Given(/the api is unreachable/, function (callback) {
    mockdata.stubHealthz(503)
    mockdata.stubIt(urls.threshold, '', 404)
    callback()
  })

  Then(/^the api health check response has status (\d+)$/, function (int, callback) {
    mockdata.stubHealthz(int)
    callback()
  })

  Given(/the api response is empty/, function (callback) {
    mockdata.stubIt(urls.threshold, '', 200)
    callback()
  })

  Given(/the api response is delayed for (\d+) seconds/, {timeout: 60000}, function (int, callback) {
    mockdata.stubItFile(urls.threshold, 'threshold-t4.json', 200, int * 1000)
    callback()
  })

  Given(/the api response is garbage/, function (callback) {
    mockdata.stubHealthz(503)
    mockdata.stubIt(urls.threshold, 'dsadsadasda', 500)
    mockdata.stubIt(urls.dailyBalance, 'dsadsadasda', 500)
    mockdata.stubIt(urls.consent, 'dsadsadasda', 500)
    callback()
  })

  Given(/the api response has status (\d+)/, function (int, callback) {
    mockdata.stubIt(urls.threshold, '', int)
    callback()
  })

  Given(/^the api response is a validation error - (.+) parameter$/, function (ref, callback) {
    mockdata.stubItFile(urls.threshold, 'validation-error-' + ref + '.json', 400)
    callback()
  })

  Given(/the account does not have sufficient funds/, function (callback) {
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

  Given(/the account has sufficient funds/, function (callback) {
    mockdata.stubItFile(urls.dailyBalance, 'dailyBalancePass.json')
    callback()
  })

  Given(/caseworker is using the financial status service ui/, {timeout: 10 * 1000}, function () {
    const d = this.driver

    return d.get('http://127.0.0.1:8000/#!/fs/').then(function () {
      return d.navigate().refresh()
    }).then(function () {
      const el = d.findElement({id: 'pageTitle'})
      return expect(el.getText()).to.eventually.equal('Check financial status')
    })
  })

  Given(/the default details are/, function (table) {
    this.defaults = toCamelCaseKeys(_.object(table.rawTable))
    return true
  })

  Given(/consent is sought for the following:/, function (table) {
    const d = this.driver
    const data = expandFields(toCamelCaseKeys(_.object(table.rawTable)), this.defaults)
    return completeInputs(d, data).then(function () {
      return submitAction(d)
    })
  })

  Given(/the progress bar is displayed/, function () {
    const d = this.driver
    return d.wait(until.elementLocated({className: 'progress-bar'}), 2 * 1000, 'TIMEOUT: Waiting for element .progress-bar').then(function () {
      return d.findElement({className: 'progress-bar'})
    }).then(function (el) {
      return expect(el.isDisplayed()).to.eventually.equal(true)
    })
  })

  Given(/^the feedback form is completed$/, {timeout: 10 * 1000}, function (table) {
    const d = this.driver
    const data = toCamelCaseKeys(_.object(table.rawTable))
    return completeInputs(d, data).then(function () {
      return submitAction(d)
    })
  })

  When(/^caseworker is on page (.+)$/, {timeout: 10 * 1000}, function (str) {
    const d = this.driver
    return d.get('http://127.0.0.1:8000/#!/fs/' + str).then(function () {
      return d.navigate().refresh()
    })
  })

  When(/^the financial status check is performed$/, {timeout: 10 * 1000}, function () {
    const d = this.driver
    const data = expandFields(_.defaults(this.defaults))
    return completeInputs(d, data).then(function () {
      return submitAction(d)
    })
  })

  When(/^the financial status check is performed with$/, {timeout: 10 * 1000}, function (table) {
    const d = this.driver
    const data = expandFields(_.defaults(toCamelCaseKeys(_.object(table.rawTable)), this.defaults))
    return completeInputs(d, data).then(function () {
      return submitAction(d)
    })
  })

  When(/after at least (\d+) seconds/, {timeout: 60 * 1000}, function (int) {
    return justWait(int * 1000)
  })

  When(/^the (.+) button is clicked$/, function (btnRef) {
    return this.driver.findElement({id: toCamelCase(btnRef) + 'Btn'}).click().then(function () {
      return justWait(200)
    })
  })

  Then(/the result table contains the following/, function (table) {
    const data = toCamelCaseKeys(_.object(table.rawTable))
    const d = this.driver
    return d.findElement({id: 'result'}).then(function (el) {
      return confirmContentById(d, data)
    })
  })

  Then(/^the service displays the following page content$/, function (table) {
    const data = toCamelCaseKeys(_.object(table.rawTable))
    return confirmContentById(this.driver, data)
  })

  Then(/^the service displays the following result$/, function (table) {
    const data = toCamelCaseKeys(_.object(table.rawTable))
    const d = this.driver
    return d.wait(until.elementLocated({id: 'outcome'}), 5 * 1000).then(function () {
      return confirmContentById(d, data)
    })
  })

  Then(/the service displays the following error message/, function (table) {
    const data = toCamelCaseKeys(_.object(table.rawTable))
    data['validation-error-summary-heading'] = 'There\'s some invalid information'
    return confirmContentById(this.driver, data)
  })

  Then(/the error summary list contains the text/, function (table) {
    const el = this.driver.findElement({id: 'error-summary-list'})
    return el.getText().then(function (txt) {
      const promiseList = []
      _.each(table.rawTable, function (row) {
        let prom = expect(txt).to.contain(row[0])
        promiseList.push(prom)
      })
      return Promise.all(promiseList)
    })
  })

  Then(/the availability warning box should not be shown/, function (callback) {
    const d = this.driver
    d.wait(until.elementLocated({className: 'availability'}), 2 * 1000).then(function () {
      callback(null, 'The availability warning box should not be shown')
    }, function () {
      callback()
    })
  })

  Then(/the inputs will be populated with/, function (table) {
    const data = expandFields(toCamelCaseKeys(_.object(table.rawTable)))
    return confirmInputValuesById(this.driver, data)
  })

  Then(/^the service displays the following ([a-zA-Z0-9]+) headers in order$/, function (tableID, table) {
    let test
    return getTableHeaders(this.driver, tableID).then(function (results) {
      const errors = []
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

  Then(/the copied text includes/, function (table, callback) {
    callback()
  })

  Then(/the service has the following links/, function (table) {
    const promises = []
    const errors = []
    let test
    const d = this.driver
    _.each(table.rawTable, function (row, r) {
      const key = toCamelCase(row[0])
      const val = row[1]
      const lnk = row[2]
      let theElement
      const expectation = d.wait(until.elementLocated({id: key}), 5000, 'TIMEOUT: Waiting for element #' + key).then(function (el) {
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

  Then(/^the service displays the following page content within (\d+) seconds$/, {timeout: 20000}, function (int, table) {
    const data = toCamelCaseKeys(_.object(table.rawTable))
    return confirmContentById(this.driver, data, int * 1000)
  })

  Then(/the liveness response status should be (\d+)/, function (int) {
    return getHttp('http://localhost:8000/ping').then(function (result) {
      return expect(result.status).to.equal(int)
    }, function (error) {
      return expect(error.status).to.equal(int)
    })
  })

  Then(/the readiness response status should be (\d+)/, function (int) {
    return getHttp('http://localhost:8000/healthz').then(function (result) {
      return expect(result.status).to.equal(int)
    }, function (error) {
      return expect(error.status).to.equal(int)
    })
  })

  Then(/the connection attempt count should be (\d+)/, function (int, callback) {
    callback(null, 'pending')
  })

  Then(/^the following are (visible|hidden)$/, function (showOrHide, table) {
    const data = toCamelCaseKeys(_.object(table.rawTable))
    return confirmVisible(this.driver, data, (showOrHide === 'visible'))
  })
})
