var express = require('express')
var serveStatic = require('serve-static')
var app = express()
var apiRoot = process.env.API_ROOT || 'http://localhost:8080'
var uiBaseUrl = '/pttg/financialstatus/v1/'
var apiBaseUrl = apiRoot + '/pttg/financialstatus/v1/'
var request = require('request')
var port = process.env.SERVER_PORT || '8000'
var moment = require('moment')

// required when running BDDs to force to root directory
var path = require('path');
process.chdir(path.resolve(__dirname));

var stdRelay = function (res, uri, qs) {
  request({uri: uri, qs: qs}, function (error, response, body) {
    var status = (response && response.statusCode) ? response.statusCode : 500
    if ((body === '' || body === '""') && status === 200) {
      status = 500
    }
    res.setHeader('Content-Type', 'application/json')
    res.status(status)
    res.send(body)

    // console.log(uri)
    // console.log(body)
    // console.log('\n')
    if (error) {
      if (error.code === 'ECONNREFUSED') {
        console.log('ERROR: Connection refused', uri)
      } else {
        console.log('ERROR', error)
      }
    }
  })
}

var getDaysToCheck = function (t) {
  return (t === 't4') ? 28 : 90
}

app.use(serveStatic('src/main/webapp/', { 'index': ['index.html'] }))

app.listen(port, function () {
  console.log('The server is running on port:' + port)
  console.log('apiRoot is:' + apiRoot)
})

app.get('/ping', function (req, res) {
  res.send('')
})

app.get('/healthz', function (req, res) {
  res.send({env: process.env.ENV, status: 'OK'})
})

app.get(uiBaseUrl + 'availability', function (req, res) {
  stdRelay(res, apiRoot + '/healthz', '')
})

app.get(uiBaseUrl + ':tier/threshold', function (req, res) {
  stdRelay(res, apiBaseUrl + req.params.tier + '/maintenance/threshold', req.query)
})

app.get(uiBaseUrl + 'accounts/:sortCode/:accountNumber/consent', function (req, res) {
  stdRelay(res, apiBaseUrl + 'accounts/' + req.params.sortCode + '/' + req.params.accountNumber + '/consent', req.query)
})

app.get(uiBaseUrl + ':tier/accounts/:sortCode/:accountNumber/dailybalancestatus', function (req, res) {
  var uri = apiBaseUrl + req.params.tier + '/maintenance/threshold'
  // request the threshold
  request({uri: uri, qs: req.query}, function (error, response, body) {
    if (error) {
      res.status((response && response.statusCode) ? response.statusCode : 500)
      res.send()
      return
    }
    try {
      req.query.minimum = JSON.parse(body).threshold
      req.query.fromDate = moment(req.query.endDate).subtract(getDaysToCheck(req.params.tier) - 1, 'd').format('YYYY-MM-DD')
      stdRelay(res, apiBaseUrl + 'accounts/' + req.params.sortCode + '/' + req.params.accountNumber + '/dailybalancestatus', req.query)
    } catch (e) {
      // console.log('ERROR:')
      // console.log(e)
      // console.log(body)
      res.status(500)
      res.send()
    }
  })
})

app.get(uiBaseUrl + ':tier/conditioncodes', function (req, res) {
  stdRelay(res, apiBaseUrl + req.params.tier + '/conditioncodes', req.query)
})
