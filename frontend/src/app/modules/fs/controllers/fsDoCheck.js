/* global angular moment _ ga */

'use strict'

var fsModule = angular.module('hod.fs')

// #### ROUTES #### //
fsModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  // $stateProvider.state({
  //   name: 'fsDoCheck',
  //   url: '/:applicantType',
  //   title: 'Financial Status : Query',
  //   parent: 'fsApplicantType',
  //   views: {
  //     'content@': {
  //       templateUrl: 'modules/fs/templates/fsDoCheck.html',
  //       controller: 'FsDoCheckCtrl'
  //     }
  //   }
  // })
}])

fsModule.controller('FsDoCheckCtrl', ['$scope', '$state', 'FsService', 'FsInfoService', function ($scope, $state, FsService, FsInfoService) {
  var t = Number($state.params.tier)
  $scope.tier = FsInfoService.getTier(t)
  $scope.fs = FsService.getApplication()
  $scope.fs.tier = t
  $scope.fs.applicantType = $state.params.applicantType

  var info = FsInfoService.getFieldInfo('doCheck')
  $scope.doCheckOptions = info.options
  $scope.submit = function (valid) {
    if (valid) {
      if ($scope.fs.doCheck === 'yes') {
        $state.go('fsGetConsent', { tier: t, applicantType: $scope.fs.applicantType })
      } else {
        $state.go('fsDetails', { tier: t, applicantType: $scope.fs.applicantType, calcOrBank: 'calc' })
      }
    }
  }
}])
