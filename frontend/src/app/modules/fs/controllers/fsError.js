/* global angular moment _ ga Clipboard */

'use strict'

var fsModule = angular.module('hod.fs')

// #### ROUTES #### //
fsModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'fsError',
    url: '/error',
    title: 'Financial Status : Error',
    parent: 'fsStart',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsError.html',
        controller: 'FsErrorCtrl'
      }
    }
  })
}])

fsModule.controller('FsErrorCtrl', ['$scope', '$state', 'FsInfoService', function ($scope, $state, FsInfoService) {
  $scope.outcome = FsInfoService.t('notnow')
  $scope.outcomeDetail = FsInfoService.t('trylater')
}])
