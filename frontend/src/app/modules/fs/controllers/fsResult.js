/* global angular moment _ ga */

'use strict'

var fsModule = angular.module('hod.fs')

// #### ROUTES #### //
fsModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'fsResult',
    url: '/result',
    title: 'Financial Status : Details',
    parent: 'fsDetails',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsResult.html',
        controller: 'FsResultCtrl'
      }
    }
  })
}])

fsModule.run(['$rootScope', '$state', 'FsService', function ($rootScope, $state, FsService) {
  $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
    var fs = FsService.getApplication()
    if (toState.name === 'fsResult' && !FsService.hasResultInfo(fs)) {
      // you cannot be on the 'fsResult' route/view if the result info is not present
      console.log('No result info')
      event.preventDefault()
      $state.go('fsDetails', toParams)
      return false
    }
  })
}])

fsModule.controller('FsResultCtrl', ['$scope', '$state', '$filter', 'FsService', 'FsInfoService', 'FsBankService', function ($scope, $state, $filter, FsService, FsInfoService, FsBankService) {
  console.log('CONTROLLER FsResultCtrl')

  var fs = FsService.getApplication()
  FsBankService.clearDailyBalanceResponse(fs)
  $scope.threshold = fs.thresholdResponse.data.threshold
  $scope.leaveEndDate = fs.thresholdResponse.data.leaveEndDate
  $scope.criteria = FsService.getCriteria(fs)
  $scope.results = FsService.getResults(fs)
  $scope.seconds = 60
  $scope.numTry = 0
  $scope.numTryLimit = 5
  $scope.timerScope = null
  $scope.doNext = []

  $scope.render = function (state) {
    $scope.state = state
    console.log('render', state)
    switch (state) {
      case 'PASSED':
        $scope.passedTitle = FsInfoService.t('passed')
        $scope.passedReason = FsInfoService.t('passedReason')
        $scope.doNext = FsService.getThingsToDoNext(fs)
        break
      case 'NOTPASSED':
        $scope.passedTitle = FsInfoService.t('notPassed')
        $scope.passedReason = FsInfoService.t('notPassedReason')
        $scope.doNext = FsService.getThingsToDoNext(fs)
        break
      case 'CONSENTDENIED':
        $scope.passedTitle = FsInfoService.t('consentDenied')
        $scope.passedReason = FsInfoService.t('consentDeniedReason')
        $scope.doNext = FsService.getThingsToDoNext(fs)
        break
    }
  }

  if (FsBankService.hasBankInfo(fs)) {
    $scope.render('PENDING')
  } else {
    $scope.render('CALCULATOR')
  }

  $scope.timerConf = {
    duration: 5000,
    onInit: function (timerScope) {
      $scope.timerScope = timerScope
      $scope.numTry = 0

      timerScope.$on('FsTimerSTARTED', function (e) {

      })

      timerScope.$on('FsTimerUPDATED', function (e) {
        // update the seconds countdown
        $scope.seconds = Math.ceil((100 - e.targetScope.percent) * e.targetScope.config.duration / 100000)
      })

      timerScope.$on('FsTimerENDED', function (e) {
        $scope.numTry++
        $scope.checkConsent()
      })

      // timerScope.startTimer()
      $scope.checkConsent()
    }
  }

  $scope.cancelTimer = function (e) {
    $scope.timerScope.stopTimer()
    $scope.timerScope.percent = 0
    $scope.seconds = '-'
    $scope.numTry = $scope.numTryLimit
  }

  $scope.tryAgainNow = function (e) {
    $scope.checkConsent()
  }

  $scope.checkConsent = function () {
    // reset the seconds countdown and progress bar
    $scope.timerScope.percent = 0
    $scope.seconds = '-'

    // send the consent API request
    FsBankService.sendConsentRequest(fs).then(function (data) {
      // start the timer again
      console.log(data.data.consent)
      fs.consentResponse = data
      if (data.data.consent === 'SUCCESS') {
        $scope.checkBalance()
      } else if (data.data.consent === 'FAILURE') {
        $scope.render('CONSENTDENIED')
      } else if ($scope.numTry < $scope.numTryLimit) {
        $scope.timerScope.startTimer()
      } else {
        console.log('ALL OVER')
      }
    }, function (err, data) {
      // something went wrong
      console.log('err', err)
    })
  }

  $scope.checkBalance = function () {
    FsBankService.sendDailyBalanceRequest(fs).then(function (data) {
      console.log(data)
      fs.dailyBalanceResponse = data
      var passed = FsBankService.passed(fs)
      if (passed) {
        $scope.render('PASSED')
      } else if (passed === false) {
        $scope.render('NOTPASSED')
      } else {
        $scope.showPassOrFail = false
      }
    }, function (err, data) {
      console.log(err, data)
    })
  }
}])
