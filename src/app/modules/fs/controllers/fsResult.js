/* global angular moment _ ga Clipboard */

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
    if (toState.name === 'fsResult' && !FsService.hasThresholdInfo(fs)) {
      // you cannot be on the 'fsResult' route/view if the result info is not present
      event.preventDefault()
      $state.go('fsDetails', toParams)
      return false
    }
  })
}])

fsModule.controller('FsResultCtrl', ['$rootScope', '$scope', '$state', '$filter', '$timeout', 'FsService', 'FsInfoService', 'FsBankService', function ($rootScope, $scope, $state, $filter, $timeout, FsService, FsInfoService, FsBankService) {
  var fs = FsService.getApplication()
  var tier = FsInfoService.getTier(fs.tier)
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

  // show hide blocks of text and set display strings as required
  $scope.render = function (state) {
    $scope.state = state

    var label = 't' + fs.tier + '-' + fs.applicantType + '-' + fs.variantType

    switch (state) {
      case 'PASSED':
        FsService.track('result', 'passed', label)
        $scope.stateTitle = FsInfoService.t('passed')
        $scope.stateReason = FsInfoService.t('passedReason')
        $scope.doNext = FsService.getThingsToDoNext(fs)
        break
      case 'NOTPASSED':
        FsService.track('result', 'notpassed', label)
        $scope.stateTitle = FsInfoService.t('notPassed')
        if (fs.dailyBalanceResponse.data.failureReason && fs.dailyBalanceResponse.data.failureReason.recordCount) {
          $scope.stateReason = FsInfoService.t('notEnoughRecords').replace('{{ nDaysRequired }}', tier.nDaysRequired)
        } else {
          $scope.stateReason = FsInfoService.t('notPassedReason')
        }

        $scope.doNext = FsService.getThingsToDoNext(fs)
        break
      case 'CONSENTDENIED':
        FsService.track('result', 'consentdenied', label)
        $scope.stateTitle = FsInfoService.t('consentDenied')
        $scope.stateReason = FsInfoService.t('consentDeniedReason')
        $scope.doNext = FsService.getThingsToDoNext(fs)
        break
      case 'ERROR':
        FsService.track('result', 'consenterror', label)
        $scope.stateTitle = 'Error'
        $scope.stateReason = 'Something went wrong, please try again later.'
        break
      case 'CALCULATOR':
        FsService.track('result', 'calculator', label)
        break
    }

    $rootScope.$broadcast('focusOnH1')
  }

  // set the default status
  if (FsBankService.hasBankInfo(fs)) {
    $scope.render('PENDING')
  } else {
    $scope.render('CALCULATOR')
  }

  //
  $scope.timerConf = {
    duration: 5000,
    onInit: function (timerScope) {
      $scope.timerScope = timerScope
      $scope.numTry = 0

      timerScope.$on('FsTimerSTARTED', function (e) {

      })

      timerScope.$on('FsTimerUPDATED', function (e) {
        // update the seconds countdown
        var s = Math.ceil((100 - e.targetScope.percent) * e.targetScope.config.duration / 100000)
        if (s) {
          $scope.consentCheck = 'We will automatically check for consent again in ' + s + 's.'
        } else {
          $scope.consentCheck = 'We will no longer check automatically for consent'
        }
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
      fs.consentResponse = data
      if (data.data.consent === 'SUCCESS') {
        $scope.cancelTimer()
        $scope.checkBalance()
      } else if (data.data.consent === 'FAILURE' || data.data.consent === 'INVALID') {
        $scope.cancelTimer()
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
    var nDays = tier.nDaysRequired - 1
    var endDate = moment(fs.endDate)
    fs.fromDate = endDate.clone().subtract(nDays, 'days')
    fs.minimum = $scope.threshold
    FsBankService.sendDailyBalanceRequest(fs).then(function (data) {
      fs.dailyBalanceResponse = data
      $scope.results = FsService.getResults(fs)
      var passed = FsBankService.passed(fs)
      if (passed) {
        $scope.render('PASSED')
      } else if (passed === false) {
        $scope.render('NOTPASSED')
      } else {
        $scope.showPassOrFail = false
      }
    }, function (err, data) {
      $scope.render('ERROR')
      console.log('FsResultCtrl $scope.checkBalance err', err, data)
    })
  }

  // #### COPY AND PASTE ####
  // init the clipboard object
  var clipboard = new Clipboard('#copyBtn', {
    text: function () {
      return FsService.getPlainTextResults(fs)
    }
  })

  var timeoutResetButtonText = function () {
    $timeout(function () {
      $scope.showCopied = false
      $scope.$applyAsync()
    }, 2000)
  }

  // $scope.showCopied = true
  clipboard.on('success', function (e) {
    $scope.showCopied = true
    $scope.$applyAsync()
    e.clearSelection()
    timeoutResetButtonText()
  })
  clipboard.on('error', function (e) {
    console.log('ClipBoard error', e)
    $scope.$applyAsync()
  })
}])
