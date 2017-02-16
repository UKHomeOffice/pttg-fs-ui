/* global angular moment _ ga */

'use strict'

var fsModule = angular.module('hod.fs')

// #### ROUTES #### //
fsModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'fsApplicantType',
    url: '/t:tier',
    title: 'Financial Status : Application type',
    parent: 'fsStart',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsApplicantType.html',
        controller: 'FsApplicantTypeCtrl'
      }
    }
  })
}])

fsModule.controller('FsApplicantTypeCtrl', ['$scope', '$state', 'FsService', 'FsInfoService', function ($scope, $state, FsService, FsInfoService) {
  var t = Number($state.params.tier)
  FsService.reset()
  $scope.tier = FsInfoService.getTier(t)
  $scope.fs = FsService.getApplication()
  $scope.fs.tier = t
  $scope.submit = function (valid) {
    if (valid) {
      $state.go('fsDoCheck', { tier: t, applicantType: $scope.fs.applicantType })
    }
  }
}])

