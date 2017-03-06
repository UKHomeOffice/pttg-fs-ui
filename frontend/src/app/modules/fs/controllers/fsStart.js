/* global angular moment _ ga */

'use strict'

var fsModule = angular.module('hod.fs')

// #### ROUTES #### //
fsModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'fsStart',
    url: '/fs/t:tier',
    title: 'Financial Status',
    // parent: 'financialStatus',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsStart.html',
        controller: 'FsDoCheckCtrl'
      },
      'nav@': {
        templateUrl: 'modules/fs/templates/fsNav.html',
        controller: 'FsNavCtrl'
      }
    }
  })
}])

fsModule.run(['$rootScope', function ($rootScope) {
  $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
    $rootScope.tier = Number(toParams.tier) || 0
  })
}])

fsModule.controller('FsStartCtrl', ['$scope', '$state', 'FsService', 'FsInfoService', function ($scope, $state, FsService, FsInfoService) {
  var fs = FsService.getApplication()
  FsService.setKnownParamsFromState(fs, $state.params)
  $scope.tiers = FsInfoService.getTiers()
}])

fsModule.controller('FsNavCtrl', ['$rootScope', '$scope', '$state', '$location', 'FsService', 'FsInfoService', function ($rootScope, $scope, $state, $location, FsService, FsInfoService) {
  $scope.tiers = FsInfoService.getTiers()
  $scope.showDebug = ($location.host() === '127.0.0.1')

  FsService.reset()

  $scope.setBank = function (accountNumber) {
    var fs = FsService.getApplication()
    fs.accountNumber = accountNumber
  }
}])
