/* global angular _ moment ga */

/* jshint node: true */

'use strict'

var fsModule = angular.module('hod.fs')

fsModule.factory('FsBankService', ['IOService', 'FsInfoService', function (IOService, FsInfoService) {
  var me = this

  // does the object supplied (_application) have complete bank info?
  this.hasBankInfo = function (obj) {
    if (obj.doCheck !== true) {
      // doCheck must be YES
      return false
    }

    if (!obj.sortCode || obj.sortCode.length !== 6 || _.isNaN(Number(obj.sortCode))) {
      // sortcode must be complete
      return false
    }

    if (!obj.accountNumber || obj.accountNumber.length !== 8) {
      // account number must be filled in
      return false
    }

    if (!obj.dob || obj.dob.length !== 10) {
      // dob must be completed
      return false
    }

    return true
  }

  this.hasResult = function (obj) {
    if (_.has(obj, 'dailyBalanceResponse') && _.has(obj.dailyBalanceResponse, 'data') && _.has(obj.dailyBalanceResponse.data, 'pass')) {
      return true
    }
    return false
  }

  // get the consent url
  this.getConsentUrl = function (obj) {
    if (!obj.sortCode || !obj.accountNumber) {
      return null
    }
    return 'accounts/' + obj.sortCode + '/' + obj.accountNumber + '/consent'
  }

  this.getConsentParams = function (obj) {
    return {dob: obj.dob, toDate: moment().format('YYYY-MM-DD'), tier: obj.tier}
  }

  this.getConsentStatus = function (obj) {
    if (_.has(obj, 'consentResponse') && _.has(obj.consentResponse, 'data') && _.has(obj.consentResponse.data, 'consent')) {
      return obj.consentResponse.data.consent
    }
    if (_.has(obj, 'consentResponse') && _.has(obj.consentResponse, 'status')) {
      return obj.consentResponse.status
    }
    return null
  }

  this.consentGiven = function (obj) {
    return me.getConsentStatus(obj) === 'SUCCESS'
  }

  this.consentDenied = function (obj) {
    return me.getConsentStatus(obj) === 'FAILURE'
  }

  this.sendConsentRequest = function (obj) {
    var u = me.getConsentUrl(obj)
    var params = me.getConsentParams(obj)
    return IOService.get(u, params)
  }

  this.getResponseStatusMessage = function (obj) {
    if (_.has(obj, 'data') && _.has(obj.data, 'status') && _.has(obj.data.status, 'message')) {
      return obj.data.status.message
    }

    return null
  }

  // get daily balance status url
  this.getDailyBalanceStatusUrl = function (obj) {
    if (!obj.tier || !obj.sortCode || !obj.accountNumber) {
      return null
    }
    return 't' + obj.tier + '/accounts/' + obj.sortCode + '/' + obj.accountNumber + '/dailybalancestatus'
  }

  this.getDailyBalanceParams = function (obj) {
    var params = {}
    params.dob = obj.dob
    // params.sortcode = obj.sortcode
    // params.accountNumber = obj.accountNumber
    params.toDate = obj.endDate
    params.fromDate = obj.fromDate
    params.minimum = obj.minimum

    return params
  }

  this.sendDailyBalanceRequest = function (obj) {
    var u = me.getDailyBalanceStatusUrl(obj)
    var params = me.getDailyBalanceParams(obj)
    return IOService.get(u, params).then(function (results) {
      if (results.data && _.has(results.data, 'fundingRequirementMet')) {
        // [TODO] remove these they are only to support the old JAVA based UI-API
        results.data.pass = results.data.fundingRequirementMet
      }
      return results
    })
  }

  this.clearDailyBalanceResponse = function (obj) {
    if (me.hasResult(obj)) {
      delete obj.dailyBalanceResponse
      return true
    }
    return false
  }

  this.passed = function (obj) {
    if (me.hasResult(obj)) {
      return obj.dailyBalanceResponse.data.pass
    }

    return null
  }

  return me
}])
