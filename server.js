// ENV vars and defaults
var apiRoot = process.env.API_ROOT || 'http://localhost:8050'
var feedbackRoot = process.env.FEEDBACK_ROOT || 'http://localhost:8051'
var port = process.env.SERVER_PORT || '8000'
var PROXY_DISCOVERY_URL = process.env.PROXY_DISCOVERY_URL || 'https://sso.digital.homeoffice.gov.uk/auth/realms/pttg-qa'
var PROXY_REDIRECTION_URL = process.env.PROXY_REDIRECTION_URL || 'https://fs.calc.dev.notprod.pttg.homeoffice.gov.uk'
var httpauth = process.env.FS_API_AUTH || ''

// require dependancies
var request = require('request')
var express = require('express')
var serveStatic = require('serve-static')
var app = express()
var moment = require('moment')
var uuid = require('uuid/v4')
var fs = require('fs')
var bodyParser = require('body-parser')
app.use(bodyParser.json())

// vars
var uiBaseUrl = '/pttg/financialstatus/v1/'
var apiBaseUrl = apiRoot + '/pttg/financialstatus/v1/'
var feedbackBaseUrl = feedbackRoot + '/feedback'

// required when running BDDs to force to root directory
var path = require('path')
process.chdir(path.resolve(__dirname))

const log = function (msg, info) {
  let output = {
    '@timestamp': moment().toISOString(),
    message: msg
  }
  if (info) {
    output.info = info
  }
  console.log(JSON.stringify(output))
}

var addSecureHeaders = function (res) {
  res.setHeader('Cache-control', 'no-store, no-cache')
}

var stdRelay = function (req, res, uri, qs, postdata, dontLog) {
  var headers = {}

  addSecureHeaders(res)

  if (req.headers['x-auth-userid']) {
    headers['x-auth-userid'] = req.headers['x-auth-userid']
  }

  if (req.headers['kc-access']) {
    headers['kc-access'] = req.headers['kc-access']
  }

  if (httpauth) {
    headers['Authorization'] = 'Basic ' + Buffer.from(httpauth).toString('base64')
  }

  headers['x-correlation-id'] = uuid()
  var opts = {
    uri: uri,
    method: 'GET'
  }

  if (qs) {
    opts.qs = qs
  }

  if (postdata) {
    opts.method = 'POST'
    opts.json = true
    opts.headers['content-type'] = 'application/json'
    opts.body = postdata
  }

  if (!dontLog) {
    log(`request ${opts.uri}`, Object.assign(opts, {correlation: headers['x-correlation-id']}))
  }

  opts.followRedirect = false
  opts.headers = headers
  opts = addCaCertsForHttps(opts, headers)

  request(opts, function (error, response, body) {
    var status = (response && response.statusCode) ? response.statusCode : 500
    if ((body === '' || body === '""') && status === 200) {
      status = 500
    }

    res.setHeader('Content-Type', 'application/json')
    res.status(status)
    res.send(body)

    if (!dontLog) {
      log(`response ${status} ${opts.uri}`, {
        uri: opts.uri,
        upstreamStatus:
        response.statusCode,
        status: status,
        body: body,
        correlation: headers['x-correlation-id']
      })
    }

    if (error) {
      log(headers['x-correlation-id'], body)
      if (error.code === 'ECONNREFUSED') {
        log('ERROR: Connection refused', uri)
      } else {
        log('ERROR', error)
      }
    }
  })
}

var getDaysToCheck = function (t) {
  return (t === 't4') ? 28 : 90
}

app.use(serveStatic('public/', { 'index': ['index.html'] }))

app.listen(port, function () {
  log('The server is running on port:' + port)
  log('apiRoot is:' + apiRoot)
})

app.get('/ping', function (req, res) {
  res.send('')
})

app.get('/healthz', function (req, res) {
  res.send({env: process.env.ENV, status: 'OK'})
})

app.get(uiBaseUrl + 'availability', function (req, res) {
  // req, res, uri, qs, postdata, dontLog)
  stdRelay(req, res, apiRoot + '/healthz', null, null, true)
})

app.get(uiBaseUrl + ':tier/threshold', function (req, res) {
  stdRelay(req, res, apiBaseUrl + req.params.tier + '/maintenance/threshold', req.query)
})

app.get(uiBaseUrl + 'accounts/:sortCode/:accountNumber/consent', function (req, res) {
  req.query.fromDate = moment(req.query.toDate).subtract(99, 'd').format('YYYY-MM-DD')
  stdRelay(req, res, apiBaseUrl + 'accounts/' + req.params.sortCode + '/' + req.params.accountNumber + '/consent', req.query)
})

app.get(uiBaseUrl + ':tier/accounts/:sortCode/:accountNumber/dailybalancestatus', function (req, res) {
  req.query.fromDate = moment(req.query.toDate).subtract(getDaysToCheck(req.params.tier) - 1, 'd').format('YYYY-MM-DD')
  stdRelay(req, res, apiBaseUrl + 'accounts/' + req.params.sortCode + '/' + req.params.accountNumber + '/dailybalancestatus', req.query)
})

app.get(uiBaseUrl + ':tier/conditioncodes', function (req, res) {
  stdRelay(req, res, apiBaseUrl + req.params.tier + '/conditioncodes', req.query)
})

app.get('/logout', function (req, res) {
  let url = PROXY_REDIRECTION_URL + '/oauth/logout?redirect=' + encodeURIComponent(PROXY_DISCOVERY_URL + '/protocol/openid-connect/logout?post_logout_redirect_uri=' + PROXY_REDIRECTION_URL)
  res.setHeader('Content-Type', 'application/json')
  res.send({logout: url})
})

app.post(uiBaseUrl + 'feedback', function (req, res) {
  stdRelay(req, res, feedbackBaseUrl, '', req.body)
})

function addCaCertsForHttps (opts, headers) {
  if (opts.uri && opts.uri.toLowerCase().startsWith('https')) {
    opts.ca = fs.readFileSync(process.env.CA_CERTS_PATH, 'utf8')
    // DSP certs do not include root ca - so we can not validate entire chain that OpenSSL requires
    // so until we have entire chain in bundle lets not be strict
    opts.strictSSL = false
  }
  return opts
}
