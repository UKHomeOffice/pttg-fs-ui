/* global angular */

'use strict'

var fsModule = angular.module('hod.fs')

fsModule.directive('fsTimer', ['$interval', function ($interval) {
  return {
    restrict: 'E',
    scope: {
      config: '=?'
    },
    templateUrl: 'modules/fs/templates/fsTimer.html',
    compile: function (element, attrs) {
      return function (scope, element, attrs) {
        scope.msg = 'Timer stopped'
        scope.interval = null
        scope.percent = 0
        scope.startTimer = function () {
          scope.stopTimer()
          scope.msg = 'Timer running'
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
          scope.$applyAsync()
        }

        scope.stopTimer = function () {
          scope.msg = 'Timer stopped'
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
