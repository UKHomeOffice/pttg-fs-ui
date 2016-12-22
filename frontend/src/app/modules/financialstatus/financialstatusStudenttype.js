/* global angular ga */
/* jshint node: true */

'use strict'

var financialstatusModule = angular.module('hod.financialstatus')

// #### ROUTES #### //
financialstatusModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the question re student type
  $stateProvider.state({
    name: 'financialStatus',
    url: '/financial-status-student-type',
    title: 'Financial Status : Student Type',
    parent: 'default',
    views: {
      'content@': {
        templateUrl: 'modules/financialstatus/financialstatusStudenttype.html',
        controller: 'FinancialstatusCtrl'
      }
    }
  })

  $stateProvider.state({
    name: 'financialStatusCalc',
    url: '/financial-status-calc-student-type',
    title: 'Financial Status Calculator: Student Type ',
    parent: 'default',
    views: {
      'content@': {
        templateUrl: 'modules/financialstatus/financialstatusStudenttype.html',
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

    $scope.studentTypeOptions = FinancialstatusService.getStudentTypes()
    $scope.finStatus = FinancialstatusService.getDetails()

    console.log($state.current.name)
    var isCalc = ($state.current.name.indexOf('Calc') > 0)

    $scope.typeSubmit = function (isValid, formScope, formCtrl) {
      FinancialstatusService.trackFormSubmission(formScope)

      if ($scope.finStatus.studentType) {
        // simply go to the appropriate page for this student type
        FinancialstatusService.reset()
        FinancialstatusResultService.reset()
        $state.go((isCalc) ? 'financialStatusCalcDetails' : 'financialStatusDetails', {studentType: $scope.finStatus.studentType})
      }
    }
  }])
