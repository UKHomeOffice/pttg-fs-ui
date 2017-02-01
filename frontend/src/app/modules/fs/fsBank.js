/* global angular _ moment ga */

/* jshint node: true */

'use strict'

var fsModule = angular.module('hod.fs')

fsModule.factory('FsBankService', ['IOService', function (IOService) {
  var me = this

  // does the object supplied (_application) have complete bank info?
  this.hasBankInfo = function (obj) {
    if (obj.doCheck !== 'yes') {
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
    return false
  }

  // get daily balance status url
  this.getDailyBalanceStatusUrl = function (obj) {
    if (!obj.tier || !obj.sortCode || !obj.accountNumber) {
      return null
    }
    return 't' + obj.tier + '/accounts/' + obj.sortCode + '/' + obj.accountNumber + '/dailybalancestatus'
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

  this.sendConsentRequest = function (obj) {
    var u = me.getConsentUrl(obj)
    var params = me.getConsentParams(obj)
    return IOService.get(u, params)
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
