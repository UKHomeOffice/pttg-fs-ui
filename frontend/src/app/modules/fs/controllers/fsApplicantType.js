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
  $scope.conf = {
    applicantType: {
      id: $scope.tier.dependantsOnlyOption ? 'studentType' : 'applicantType',
      options: $scope.tier.variants,
      hidden: $scope.tier.dependantsOnlyOption
    },
    dependantsOnly: {
      id: $scope.tier.dependantsOnlyOption ? 'applicantType' : 'studentType',
      options: FsInfoService.getFieldInfo('dependantsOnly').options,
      hidden: !$scope.tier.dependantsOnlyOption

    }
  }
  $scope.submit = function (valid) {
    if (valid) {
      $scope.variant = _.findWhere($scope.tier.variants, { value: $scope.fs.applicantType })

      if ($scope.variant) {
        if (!$scope.tier.dependantsOnlyOption) {
          return $state.go('fsDoCheck', { tier: t, applicantType: $scope.fs.applicantType })
        }

        if ($scope.fs.dependantsOnly === 'main') {
          return $state.go('fsDoCheck', { tier: t, applicantType: $scope.fs.applicantType })
        }

        if ($scope.fs.dependantsOnly === 'dependant') {
          return $state.go('fsDoCheck', { tier: t, applicantType: $scope.fs.applicantType + '-dependants' })
        }
      }
      // show the dependant only route choice
      $scope.conf.applicantType.hidden = false
      $scope.conf.dependantsOnly.hidden = true
    }
  }
}])

