var express = require('express')
var serveStatic = require('serve-static')
var app = express()
var apiRoot = process.env.API_ROOT || 'http://localhost:8080'
var uiBaseUrl = '/pttg/financialstatus/v1/'
var apiBaseUrl = apiRoot + '/pttg/financialstatus/v1/'
var request = require('request')
var port = process.env.SERVER_PORT || '8000'
var moment = require('moment')

var stdRelay = function (res, uri, qs) {
  // res.setHeader('Content-Type', 'application/json')
  // res.send({uri: uri, qs: qs})
  // console.log({uri: uri, qs: qs})
  // return
  console.log('Relay start:', uri)
  console.log('qs: ', qs)
  request({uri: uri, qs: qs}, function (error, response, body) {
    res.setHeader('Content-Type', 'application/json')
    res.status((response && response.statusCode) ? response.statusCode : 500)
    res.send(body)
    console.log('response: ', res.statusCode)
    console.log('response error?:', error)
    console.log(body)
    if (error) {
      console.log('ERROR', error)
    }
    console.log('Relay end:', uri)
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
    req.query.minimum = JSON.parse(body).threshold
    req.query.fromDate = moment(req.query.endDate).subtract(getDaysToCheck(req.params.tier) - 1, 'd').format('YYYY-MM-DD')
    if (error) {
      res.status((response && response.statusCode) ? response.statusCode : 500)
      res.send()
      return
    }
    stdRelay(res, apiBaseUrl + 'accounts/' + req.params.sortCode + '/' + req.params.accountNumber + '/dailybalancestatus', req.query)
  })
})

app.get(uiBaseUrl + ':tier/conditioncodes', function (req, res) {
  stdRelay(res, apiBaseUrl + req.params.tier + '/conditioncodes', req.query)
})
