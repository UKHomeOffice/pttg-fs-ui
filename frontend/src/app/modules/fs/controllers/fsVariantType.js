/* global angular moment _ ga */

'use strict'

var fsModule = angular.module('hod.fs')

// #### ROUTES #### //
fsModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'fsVariantType',
    url: '/:applicantType',
    title: 'Financial Status : Query',
    parent: 'fsApplicantType',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsVariantType.html',
        controller: 'FsVariantTypeCtrl'
      }
    }
  })
}])

fsModule.controller('FsVariantTypeCtrl', ['$scope', '$state', 'FsService', 'FsInfoService', function ($scope, $state, FsService, FsInfoService) {
  FsService.reset()
  var fs = FsService.getApplication()
  FsService.setKnownParamsFromState(fs, $state.params)
  $scope.tier = FsInfoService.getTier(fs.tier)
  $scope.applicantType = fs.applicantType

  if ($scope.tier.variants.length === 0) {
    // skip this page if there are no variants to choose from, set the variant url parameter to 'details' as a generic placeholder
    $state.go('fsDetails', {variantType: 'details'})
    return
  }

  $scope.selectVariantType = function (v) {
    $state.go('fsDetails', {variantType: v})
  }
}])
