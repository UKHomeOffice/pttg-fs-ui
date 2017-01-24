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

fsModule.controller('FsResultCtrl', ['$scope', '$state', '$filter', 'FsService', 'FsInfoService', function ($scope, $state, $filter, FsService, FsInfoService) {
  var fs = FsService.getApplication()
  $scope.threshold = fs.thresholdResponse.data.threshold
  $scope.leaveEndDate = fs.thresholdResponse.data.leaveEndDate
  $scope.hasBankInfo = FsService.hasBankInfo(fs)
  $scope.criteria = FsService.getCriteria(fs)
  $scope.results = FsService.getResults(fs)
}])
