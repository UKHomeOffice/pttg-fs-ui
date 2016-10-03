/* jshint node: true */

'use strict';

var financialstatusModule = angular.module('hod.financialstatus');

// possible result state codes
financialstatusModule.constant('RESULT_STATES', {
  'passed'           : 'passed',
  'notpassed_28days' : 'notpassed/28days',
  'notpassed_funds'  : 'notpassed/funds',
  'failure_norecord' : 'failure/norecord',
  'failure'          : 'failure'
});


// #### ROUTES #### //
financialstatusModule.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {

  // define a route for the results operation
  $stateProvider.state({
    name: 'financialStatusResults',
    url: '/result',
    title: 'Financial Status : Result',
    parent: 'financialStatusDetails',
    views: {
      'content@': {
        templateUrl: 'modules/financialstatus/financialstatusResult.html',
        controller: 'FinancialstatusResultCtrl'
      },
    },
  });
}]);


financialstatusModule.factory('FinancialstatusResultService', ['FinancialstatusService', '$filter', 'RESULT_STATES', function (FinancialstatusService, $filter, RESULT_STATES) {
  var me = this;
  var data;
  var reqdata;
  var student;

  // what state was the result, eg passed, notpassed/funds, failure etc
  this.getState = function () {
    if (me.haveResult()) {
      // valid result
      if (me.getSuccess()) {
        // OK
        return RESULT_STATES.passed;
      } else {
        // Denied
        if (data.failureReason && data.failureReason.recordCount) {
          // Not enough records
          return RESULT_STATES.notpassed_28days;
        }
        // funds too low
        return RESULT_STATES.notpassed_funds;
      }
    }

    // service failures
    if (data.failureReason.status === 404) {
      return RESULT_STATES.failure_norecord;
    }

    // failure code
    return RESULT_STATES.failure;
  };

  // tell the result service what student type we're using
  this.setStudent = function (studentdata) {
    student = studentdata;
  };

  // tell the result service what the response data was
  this.setResponse = function (responsedata) {
    data = responsedata;
  };

  // tell the result service what data we sent to get this result
  this.setRequest = function (details) {
    reqdata = details;
  };

  // wipe the data
  this.reset = function () {
    data = null;
    reqdata = null;
  };

  // return the data
  this.getData = function () {
    return data;
  };

  // did the api return a non failure result - eg passed or notpassed
  this.haveResult = function () {
    return (data.fundingRequirementMet !== undefined) ? true : false;
  };

  // did the api return a PASSED status
  this.getSuccess = function () {
    return (data.fundingRequirementMet) ? true : false;
  };

  // what results are we summerising
  this.getSummary = function () {
    var summary = [
      {
        id: 'accountHolderName',
        label: 'Account holder name',
        value: data.accountHolderName
      },
      {
        id: 'totalFundsRequired',
        label: 'Total funds required',
        value: $filter('pounds')(data.minimum)
      },
      {
        id: 'maintenancePeriodChecked',
        label: '28-day period checked',
        value: $filter('dateDisplay')(data.periodCheckedFrom) + ' to ' + $filter('dateDisplay')(reqdata.toDate)
      },
    ];

    // if course dates were supplied then show the calculated course length
    if (student.hiddenFields.indexOf('courseStartDate') === -1) {
      summary.push({
        id: 'courseLength',
        label: 'Course length',
        value: reqdata.courseLength + ' (limited to 9)'
      });
    }

    // if failed due to lowest balance value then display that
    if (data.failureReason && data.failureReason.lowestBalanceValue) {
      summary.push({
        id: 'lowestBalance',
        label: 'Lowest balance',
        value: $filter('pounds')(data.failureReason.lowestBalanceValue) + ' on ' + $filter('dateDisplay')(data.failureReason.lowestBalanceDate)
      });
    }

    return summary;
  };

  // get the headings text to display on the results page for each state
  this.getText = function (state) {
    switch (state) {
      case RESULT_STATES.passed:
        return {
          heading: 'Passed',
          reason:  'This applicant meets the financial requirements'
        };
        break;

      case RESULT_STATES.notpassed_28days:
        return {
          heading: 'Not passed',
          reason:  'The records for this account does not cover the whole 28 day period'
        };
        break;

      case RESULT_STATES.notpassed_funds:
        return {
          heading: 'Not passed',
          reason:  'One or more daily closing balances are below the total funds required'
        };
        break;

      case RESULT_STATES.failure_norecord:
        return {
          heading: 'There is no record for the sort code and account number with Barclays',
          reason:  'We couldn\'t perform the financial requirement check as no information exists for sort code ' + $filter('sortDisplay')(reqdata.sortCode) + ' and account number ' + reqdata.accountNumber
        };
        break;
    }

    return {
      heading: 'You can’t use this service just now. The problem will be fixed as soon as possible',
      reason:  'Please try again later.'
    };
  };


  // get the summary of the search criteria used to arrive at this result
  this.getCriteria = function (state) {
    var criteria = [];

    // no summary required if the api failed
    if (state === RESULT_STATES.failure) {
      return criteria;
    }

    // default criteria list we're going to show
    var criteriaList = {
      studentType: true,
      inLondon: true,
      courseDatesChecked: (student.hiddenFields.indexOf('courseStartDate') === -1) ? true : false,
      tuitionFees: (student.hiddenFields.indexOf('totalTuitionFees') === -1) ? true : false,
      accommodationFeesAlreadyPaid: (student.hiddenFields.indexOf('accommodationFeesAlreadyPaid') === -1) ? true : false,
      numberOfDependants: true,
      bankAccount: true,
      dob: true
    }

    // if no record was found we only show the bank account and DOB
    if (state === RESULT_STATES.failure_norecord) {
      criteriaList = {
        bankAccount: true,
        dob: true
      };
    }

    // add the criteria
    if (criteriaList.studentType) {
      criteria.push({
        id: 'studentType',
        label: 'Student type',
        value: student.full
      });
    }

    if (criteriaList.inLondon) {
      criteria.push({
        id: 'inLondon',
        label: 'In London',
        value: (reqdata.inLondon.toLowerCase() === 'yes') ? 'Yes' : 'No'
      });
    }

    if (criteriaList.courseDatesChecked) {
      criteria.push({
        id: 'courseDatesChecked',
        label: 'Course dates',
        value: $filter('dateDisplay')(reqdata.courseStartDate) + ' to ' + $filter('dateDisplay')(reqdata.courseEndDate)
      });
    }

    if (criteriaList.tuitionFees) {
      criteria.push({
        id: 'totalTuitionFees',
        label: 'Total tuition fees',
        value: $filter('pounds')(reqdata.totalTuitionFees)
      });

      criteria.push({
        id: 'tuitionFeesAlreadyPaid',
        label: 'Tuition fees already paid',
        value: $filter('pounds')(reqdata.tuitionFeesAlreadyPaid)
      });
    }

    if (criteriaList.accommodationFeesAlreadyPaid) {
      var aFeesAlreadyPaid = $filter('pounds')(reqdata.accommodationFeesAlreadyPaid);
      if (data.cappedValues && data.cappedValues.accommodationFeesPaid) {
        aFeesAlreadyPaid += ' (limited to ' + $filter('pounds')(data.cappedValues.accommodationFeesPaid) + ')';
      }
      criteria.push({
        id: 'accommodationFeesAlreadyPaid',
        label: 'Accommodation fees already paid',
        value: aFeesAlreadyPaid
      });
    }

    if (criteriaList.numberOfDependants) {
      criteria.push({
        id: 'numberOfDependants',
        label: 'Number of dependants',
        value: reqdata.numberOfDependants
      });
    }

    if (criteriaList.bankAccount) {
      criteria.push({
        id: 'sortCode',
        label: 'Sort code',
        value: $filter('sortDisplay')(reqdata.sortCode)
      });

      criteria.push({
        id: 'accountNumber',
        label: 'Account number',
        value: reqdata.accountNumber
      });
    }

    if (criteriaList.dob) {
      criteria.push({
        id: 'dob',
        label: 'Date of birth',
        value: $filter('dateDisplay')(reqdata.dob)
      });
    }

    return criteria;
  };

  return this;
}]);


// display results
financialstatusModule.controller('FinancialstatusResultCtrl', ['$scope', '$state', '$stateParams', '$filter', 'FinancialstatusService', 'FinancialstatusResultService', 'RESULT_STATES', function ($scope, $state, $stateParams, $filter, FinancialstatusService, FinancialstatusResultService, RESULT_STATES) {


  // check for result data
  var resdata = FinancialstatusService.getResponse();
  if (!resdata) {
    // no result data so no we cannot display a result page - go back to the details page
    $state.go('financialStatusDetails');
    return;
  }

  // setup the results service object with what we already know
  var resServ = FinancialstatusResultService;
  var finStatus = FinancialstatusService.getDetails();
  var sType = FinancialstatusService.getStudentTypeByID(finStatus.studentType);

  resServ.setStudent(sType);
  resServ.setResponse(resdata);
  resServ.setRequest(finStatus);

  var state = resServ.getState();

  $scope.haveResult = resServ.haveResult();
  $scope.summary = resServ.getSummary();
  $scope.showSummary = $scope.haveResult;
  $scope.success = resServ.getSuccess();


  // set the text
  var text = resServ.getText(state);
  $scope.heading = text.heading;
  $scope.reason = text.reason;

  $scope.searchCriteria = resServ.getCriteria(state);
  $scope.showCriteria = (state !== RESULT_STATES.failure) ? true : false;

  // track
  ga('set', 'page', $state.href($state.current.name, $stateParams) + '/' + state);
  ga('send', 'pageview');

  // new search button
  $scope.newSearch = function (e) {
    FinancialstatusService.reset();
    $state.go('financialStatus');
  };
}]);
