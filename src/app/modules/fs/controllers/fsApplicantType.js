/* global angular moment _ ga */

'use strict'

var fsModule = angular.module('hod.fs')

// #### ROUTES #### //
fsModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'fsApplicantType',
    url: '/application/:statusOrCalc',
    title: 'Financial Status : Application type',
    parent: 'fsStart',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsApplicantType.html',
        controller: 'FsApplicantTypeCtrl'
      }
    }
  })

  $stateProvider.state({
    name: 'fsVariantType',
    url: '/:applicantType',
    title: 'Financial Status : Query',
    parent: 'fsApplicantType',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsApplicantType.html',
        controller: 'FsApplicantTypeCtrl'
      }
    }
  })
}])

fsModule.controller('FsApplicantTypeCtrl', ['$scope', '$state', 'FsService', 'FsInfoService', function ($scope, $state, FsService, FsInfoService) {
  FsService.reset()
  var fs = FsService.getApplication()
  FsService.setKnownParamsFromState(fs, $state.params)

  var t = Number($state.params.tier)
  $scope.tier = FsInfoService.getTier(t)

  var hasVariants = ($scope.tier.variants.length > 0)
  var variantFirst = FsInfoService.variantFirst($scope.tier)

  console.log(fs)

  if (fs.variantType) {
    var v = FsInfoService.getVariant(t, fs.variantType)
    if (v.dependantsOnlyOption === false || $scope.tier.dependantsOnlyOption === false) {
      // why are we here? there are no more choices to make!
      $state.go('fsDetails', {applicantType: fs.variantType, variantType: 'main'})
      return
    }
  } else if ($state.current.name === 'fsVariantType' && !hasVariants) {
    // why are we here? There are no variants to choose from
    $state.go('fsDetails', {applicantType: fs.applicantType, variantType: 'details'})
    return
  }

  $scope.showVariant = ($state.current.name === 'fsApplicantType' && variantFirst) || ($state.current.name === 'fsVariantType' && !variantFirst)

  $scope.selectApplicantType = function (t) {
    // MAIN or DEPENDANT ONLY
    if (variantFirst) {
      // the variant question has already been asked
      $state.go('fsDetails', {variantType: t})
    } else if (hasVariants) {
      // need to choose a variant option
      $state.go('fsVariantType', {applicantType: t})
    } else {
      // there are no variants to choose so go to the form
      $state.go('fsDetails', {applicantType: t, variantType: 'details'})
    }
  }

  $scope.selectVariantType = function (v) {
    // SELECTING a SUBTYPE or VARIANT
    if (variantFirst) {
      // the variant question has already been asked
      $state.go('fsVariantType', {applicantType: v})
    } else {
      // go to the form
      $state.go('fsDetails', {variantType: v})
    }
  }
}])

// fsModule.controller('FsVariantTypeCtrl', ['$scope', '$state', 'FsService', 'FsInfoService', function ($scope, $state, FsService, FsInfoService) {
//   FsService.reset()
//   var fs = FsService.getApplication()
//   FsService.setKnownParamsFromState(fs, $state.params)
//   $scope.tier = FsInfoService.getTier(fs.tier)
//   $scope.applicantType = fs.applicantType

//   if ($scope.tier.variants.length === 0) {
//     // skip this page if there are no variants to choose from, set the variant url parameter to 'details' as a generic placeholder
//     $state.go('fsDetails', {variantType: 'details'})
//     return
//   }

//   $scope.selectVariantType = function (v) {
//     $state.go('fsDetails', {variantType: v})
//   }
// }])
