/* global angular ga _ */
/* jshint node: true */

'use strict'

var financialstatusModule = angular.module('hod.financialstatus')

// #### ROUTES #### //
financialstatusModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the question re student type
  $stateProvider.state({
    name: 'financialStatus',
    url: '/financial-status',
    title: 'Financial Status : Student Type',
    parent: 'default',
    views: {
      'content@': {
        templateUrl: 'modules/financialstatus/financialstatusApplicantType.html',
        controller: 'FinancialstatusCtrl'
      }
    }
  })

  $stateProvider.state({
    name: 'financialStatusCalc',
    url: '/financial-status-calc',
    title: 'Financial Status Calculator: Student Type ',
    parent: 'default',
    views: {
      'content@': {
        templateUrl: 'modules/financialstatus/financialstatusApplicantType.html',
        controller: 'FinancialstatusCtrl'
      }
    }
  })
}])

// make a selection as to which type of student we're interested in
financialstatusModule.controller(
'FinancialstatusCtrl', ['$scope', '$state', 'FinancialstatusService', 'FinancialstatusResultService',
  function ($scope, $state, FinancialstatusService, FinancialstatusResultService) {
    ga('set', 'page', $state.href($state.current.name, {}))
    ga('send', 'pageview')

    var appTypes = FinancialstatusService.getApplicantTypes()

    $scope.applicantTypeOptions = {
      groups: [
        {
          label: 'Tier 2',
          options: _.where(appTypes, {tier: 2})
        },
        {
          label: 'Tier 4',
          options: _.where(appTypes, {tier: 4})
        },
        {
          label: 'Tier 5',
          options: _.where(appTypes, {tier: 5})
        }
      ]
    }

    $scope.finStatus = FinancialstatusService.getDetails()

    var isCalc = ($state.current.name.indexOf('Calc') > 0)

    $scope.title = (isCalc) ? 'Maintenance calculator' : 'Online statement checker for a Barclays current account holder (must be in the applicant’s name only).'
    $scope.typeSubmit = function (isValid, formScope, formCtrl) {
      FinancialstatusService.trackFormSubmission(formScope)

      if ($scope.finStatus.applicantType) {
        // simply go to the appropriate page for this student type
        FinancialstatusService.reset()
        FinancialstatusResultService.reset()
        $state.go((isCalc) ? 'financialStatusCalcDetails' : 'financialStatusDetails', {applicantType: $scope.finStatus.applicantType})
      }
    }
  }])
