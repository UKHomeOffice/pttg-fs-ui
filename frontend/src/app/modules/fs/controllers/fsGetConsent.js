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
    parent: 'fsDoCheck',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsGetConsent.html',
        controller: 'FsGetConsentCtrl'
      }
    }
  })

  $stateProvider.state({
    name: 'fsConsentError',
    url: '/problem',
    title: 'Financial Status : Consent problem',
    parent: 'fsGetConsent',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsConsentError.html',
        controller: 'FsConsentErrorCtrl'
      }
    }
  })
}])

fsModule.controller('FsGetConsentCtrl', ['$scope', '$state', 'FsService', 'FsInfoService', 'FsBankService', function ($scope, $state, FsService, FsInfoService, FsBankService) {
  var t = Number($state.params.tier)
  $scope.tier = FsInfoService.getTier(t)
  $scope.fs = FsService.getApplication()
  $scope.fs.tier = t
  $scope.fs.doCheck = 'yes'
  $scope.fs.applicantType = $state.params.applicantType

  $scope.conf = {
    accountNumber: {
      length: 8,
      min: '1',
      max: '99999999',
      errors: {
        numeric: {
          msg: 'Enter a valid account number'
        },
        min: {
          msg: 'Enter a valid account number'
        }
      }
    },
    dob: {
      max: moment().subtract(10, 'years').format('YYYY-MM-DD'),
      errors: {
        max: {
          msg: 'Enter a valid date of birth'
        }
      }
    }
  }

  $scope.submit = function (valid) {
    if (valid) {
      FsBankService.sendConsentRequest($scope.fs).then(function (data) {
        console.log('FsGetConsentCtrl $scope.submit', data)
        $scope.fs.consentResponse = data
        if (data.data.consent === 'FAILURE') {
          $state.go('fsConsentError', { tier: t, applicantType: $scope.fs.applicantType, calcOrBank: 'bank' })
        } else {
          $state.go('fsDetails', { tier: t, applicantType: $scope.fs.applicantType, calcOrBank: 'bank' })
        }
      }, function (data) {
        console.log('FsGetConsentCtrl $scope.submit err', data)
        $scope.fs.consentResponse = {}
        if (data.status === 500) {
          $state.go('fsError', $state.params)
        } else {
          $state.go('fsConsentError', $state.params)
        }
      })
    }
  }
}])

fsModule.controller('FsConsentErrorCtrl', ['$scope', '$state', 'FsService', 'FsInfoService', function ($scope, $state, FsService, FsInfoService) {
  var t = Number($state.params.tier)
  var fs = FsService.getApplication()
  $scope.tier = FsInfoService.getTier(t)
  if (_.has(fs.consentResponse, 'data') && fs.consentResponse.data.consent === 'FAILURE') {
    $scope.outcome = FsInfoService.t('consentDenied')
    $scope.outcomeDetail = FsInfoService.t('consentDeniedReason')
  } else {
    $scope.outcome = FsInfoService.t('inaccessibleaccount')
    $scope.outcomeDetail = FsInfoService.t('conditionspreventedus')
    $scope.criteria = FsService.getConsentCriteria(fs)
  }

  var reasons = {}
  _.each(['datamismatch', 'notbarclays', 'frozen', 'businessacc', 'accountclosed'], function (f) {
    reasons[f] = FsInfoService.t(f)
  })
  $scope.reasons = reasons
}])
