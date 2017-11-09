var express = require('express')
var serveStatic = require('serve-static')
var app = express()
var apiRoot = process.env.API_ROOT || 'http://localhost:8050'
var uiBaseUrl = '/pttg/financialstatus/v1/'
var apiBaseUrl = apiRoot + '/pttg/financialstatus/v1/'
var request = require('request')
var port = process.env.SERVER_PORT || '8000'
var moment = require('moment')
var uuid = require('uuid/v4')
var fs = require('fs')

// required when running BDDs to force to root directory
var path = require('path')
process.chdir(path.resolve(__dirname))

var addSecureHeaders = function (res) {
    res.setHeader('Cache-control', 'no-store, no-cache')
}


var stdRelay = function (req, res, uri, qs) {
  var headers = {}

  addSecureHeaders(res)

  if (req.headers['x-auth-userid']) {
    headers['x-auth-userid'] = req.headers['x-auth-userid']
  }

  if (req.headers['kc-access']) {
    headers['kc-access'] = req.headers['kc-access']
  }

  headers['x-correlation-id'] = uuid()

  var opts = {
    uri: uri,
    qs: qs,
    headers: headers
  }
  opts = addCaCertsForHttps(opts, headers)

  request(opts, function (error, response, body) {
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

app.use(serveStatic('public/', { 'index': ['index.html'] }))

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
  stdRelay(req, res, apiRoot + '/healthz', '')
})

app.get(uiBaseUrl + ':tier/threshold', function (req, res) {
  stdRelay(req, res, apiBaseUrl + req.params.tier + '/maintenance/threshold', req.query)
})

app.get(uiBaseUrl + 'accounts/:sortCode/:accountNumber/consent', function (req, res) {
    req.query.fromDate = moment(req.query.toDate).subtract(getDaysToCheck(req.params.tier) - 1, 'd').format('YYYY-MM-DD')
  stdRelay(req, res, apiBaseUrl + 'accounts/' + req.params.sortCode + '/' + req.params.accountNumber + '/consent', req.query)
})

app.get(uiBaseUrl + ':tier/accounts/:sortCode/:accountNumber/dailybalancestatus', function (req, res) {
    req.query.fromDate = moment(req.query.toDate).subtract(99, 'd').format('YYYY-MM-DD')
    stdRelay(req, res, apiBaseUrl + 'accounts/' + req.params.sortCode + '/' + req.params.accountNumber + '/dailybalancestatus', req.query)
})

app.get(uiBaseUrl + ':tier/conditioncodes', function (req, res) {
  stdRelay(req, res, apiBaseUrl + req.params.tier + '/conditioncodes', req.query)
})

function addCaCertsForHttps (opts, headers) {
    // log("About to call " + opts.uri, headers)
    if (opts.uri && opts.uri.toLowerCase().startsWith('https')) {
        // log("Loading certs from  " + process.env.CA_CERTS_PATH, headers)
        opts.ca = fs.readFileSync(process.env.CA_CERTS_PATH, 'utf8')
        // DSP certs do not include root ca - so we can not validate entire chain that OpenSSL requires
        // so until we have entire chain in bundle lets not be strict
        opts.strictSSL = false
    }
    return opts
}

