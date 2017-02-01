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
        }
      }
    },
    dob: {
      max: moment().subtract(10, 'years').format('YYYY-MM-DD')
    }
  }

  $scope.submit = function (valid) {
    if (valid) {
      FsBankService.sendConsentRequest($scope.fs).then(function (data) {
        $scope.fs.consentResponse = data
        $state.go('fsDetails', { tier: t, applicantType: $scope.fs.applicantType, calcOrBank: 'bank' })
      }, function (err, data) {
        console.log(err, data)
        $state.go('fsDetails', { tier: t, applicantType: $scope.fs.applicantType, calcOrBank: 'bank' })
      })
    }
  }
}])
