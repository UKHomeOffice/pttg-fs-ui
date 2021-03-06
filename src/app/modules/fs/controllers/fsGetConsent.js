/* global angular moment _ ga */

'use strict'

var fsModule = angular.module('hod.fs')

// #### ROUTES #### //
fsModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'fsGetConsent',
    url: '/consent',
    title: 'Financial Status : Consent',
    parent: 'fsStart',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsGetConsent.html',
        controller: 'FsGetConsentCtrl'
      }
    }
  })

  $stateProvider.state({
    name: 'fsGetConsentResult',
    url: '/result',
    title: 'Financial Status : Consent',
    parent: 'fsGetConsent',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsGetConsentResult.html',
        controller: 'FsGetConsentResultCtrl'
      }
    }
  })
}])

fsModule.controller('FsGetConsentCtrl', ['$scope', '$state', 'FsService', 'FsInfoService', 'FsBankService', function ($scope, $state, FsService, FsInfoService, FsBankService) {
  var fs = FsService.getApplication()
  fs.tier = Number($state.params.tier)
  fs.doCheck = true

  $scope.showSubmit = true
  $scope.showWait = false
  $scope.tier = FsInfoService.getTier(fs.tier)
  $scope.fs = fs
  $scope.conf = {
    accountNumber: {
      length: 8,
      min: '1',
      max: '99999999',
      errors: {
        numeric: {
          msg: 'Enter a valid "Account number"'
        },
        min: {
          msg: 'Enter a valid "Account number"'
        }
      }
    },
    dob: {
      max: moment().subtract(10, 'years').format('YYYY-MM-DD'),
      errors: {
        max: {
          msg: 'Enter a valid "Date of birth"'
        }
      }
    }
  }

  $scope.submit = function (valid) {
    if (valid) {
      //
      $scope.showSubmit = false
      $scope.showWait = true
      //
      FsBankService.sendConsentRequest($scope.fs).then(function (data) {
        $scope.fs.consentResponse = data
        $state.go('fsGetConsentResult')
      }, function (data) {
        $scope.fs.consentResponse = data
        $state.go('fsGetConsentResult')
      })
    }
  }
}])

fsModule.controller('FsGetConsentResultCtrl', ['$scope', '$state', 'FsService', 'FsInfoService', 'FsBankService', function ($scope, $state, FsService, FsInfoService, FsBankService) {
  var fs = FsService.getApplication()

  $scope.tier = FsInfoService.getTier(fs.tier)
  $scope.criteria = FsService.getConsentCriteria(fs)
  $scope.ok = false

  if (!FsBankService.hasBankInfo(fs)) {
    return $state.go('fsGetConsent')
  }

  var consentStatus = FsBankService.getConsentStatus(fs)
  console.log('ERROR')
  FsService.track('consent', consentStatus, 'consent-only')
  switch (consentStatus) {
    case 'INITIATED':
    case 'PENDING':
      $scope.outcome = FsInfoService.t('consentRequested')
      $scope.outcomeDetail = FsInfoService.t('consentRequestedReason')
      $scope.ok = true
      break

    case 'SUCCESS':
      $scope.outcome = FsInfoService.t('consentGiven')
      $scope.outcomeDetail = FsInfoService.t('consentGivenReason')
      $scope.ok = true
      break

    case 'FAILURE':
    case 'INVALID':
      $scope.outcome = FsInfoService.t('consentDenied')
      $scope.outcomeDetail = FsInfoService.t('consentDeniedReason')
      break
    case 500:
      $scope.outcome = FsInfoService.t('notnow')
      $scope.outcomeDetail = FsInfoService.t('trylater')
      break

    default:
      var msg = FsBankService.getResponseStatusMessage(fs.consentResponse)
      if (msg && msg !== 'OK' && msg !== 'ERROR') {
        // OK should never appear at this point and ERROR is too generic
        $scope.outcome = 'ERROR'
        $scope.outcomeDetail = msg
      } else {
        $scope.outcome = FsInfoService.t('inaccessibleaccount')
        $scope.outcomeDetail = FsInfoService.t('conditionspreventedus')

        var reasons = {}
        _.each(['datamismatch', 'notbarclays', 'frozen', 'businessacc', 'accountclosed'], function (f) {
          reasons[f] = FsInfoService.t(f)
        })
        $scope.reasons = reasons

        $scope.doNext = [FsInfoService.t('checkDataEntry')]
      }
  }
}])
