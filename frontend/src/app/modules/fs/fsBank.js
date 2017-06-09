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
    return {dob: obj.dob}
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

  // get daily balance status url
  this.getDailyBalanceStatusUrl = function (obj) {
    if (!obj.tier || !obj.sortCode || !obj.accountNumber) {
      return null
    }
    return 't' + obj.tier + '/accounts/' + obj.sortCode + '/' + obj.accountNumber + '/dailybalancestatus'
  }

  this.getDailyBalanceParams = function (obj) {
    var fields = FsInfoService.getFieldsForObject(obj)

    var params = {}
    _.each(fields, function (f) {
      if (!_.has(obj, f)) {
        return
      }

      params[f] = obj[f]
    })

    params.dependantsOnly = obj.dependantsOnly || false

    // add the date of birth
    params.dob = obj.dob

    // add the applicant type
    params.applicantType = obj.variantType || obj.applicantType
    params.studentType = obj.variantType
    if (obj.dependantsOnly) {
      if (!_.has(fields.accommodationFeesPaid)) {
        params.accommodationFeesPaid = 0
      }
      if (!_.has(fields.tuitionFees)) {
        params.tuitionFees = 0
      }
      if (!_.has(fields.tuitionFeesPaid)) {
        params.tuitionFeesPaid = 0
      }
    }

    params.toDate = obj.endDate

    // [TODO] remove these they are only to support the old JAVA based UI-API
    params.accommodationFeesAlreadyPaid = params.accommodationFeesPaid
    params.totalTuitionFees = params.tuitionFees
    params.tuitionFeesAlreadyPaid = params.tuitionFeesPaid

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

fsModule.directive('fsTimer', ['$interval', function ($interval) {
  return {
    restrict: 'E',
    scope: {
      config: '=?'
    },
    templateUrl: 'modules/fs/templates/fsTimer.html',
    compile: function (element, attrs) {
      return function (scope, element, attrs) {
        scope.interval = null
        scope.percent = 0
        scope.startTimer = function () {
          scope.stopTimer()
          scope.startTime = new Date().getTime()
          scope.interval = $interval(function () {
            var frac = scope.getFrac()
            scope.percent = (frac * 100)
            scope.$emit('FsTimerUPDATED')
            if (frac === 1) {
              scope.stopTimer()
              scope.$emit('FsTimerENDED')
            }
          }, 50)
          scope.$emit('FsTimerSTARTED')
        }

        scope.stopTimer = function () {
          $interval.cancel(scope.interval)
        }

        scope.getElapsed = function () {
          var now = new Date().getTime()
          var et = now - scope.startTime
          return et
        }

        scope.getFrac = function () {
          return Math.min(1, scope.getElapsed() / scope.config.duration)
        }

        if (_.isFunction(scope.config.onInit)) {
          scope.config.onInit(scope)
        }
      }
    }
  }
}])
