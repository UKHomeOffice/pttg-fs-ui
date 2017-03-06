/* global angular moment _ ga */

'use strict'

var fsModule = angular.module('hod.fs')

// #### ROUTES #### //
fsModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'fsApplicantType',
    url: '/:statusOrCalc',
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

fsModule.controller('FsApplicantTypeCtrl', ['$scope', '$state', 'FsInfoService', function ($scope, $state, FsInfoService) {
  var t = Number($state.params.tier)
  $scope.tier = FsInfoService.getTier(t)
}])

