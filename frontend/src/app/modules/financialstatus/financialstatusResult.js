/* jshint node: true */

'use strict';

var financialstatusModule = angular.module('hod.financialstatus');


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




// http://127.0.0.1:8001/pttg/financialstatusservice/v1/accounts/222222/12345678/dailybalancestatus?accommodationFeesAlreadyPaid=2&courseLength=2&innerLondonBorough=&numberOfDependants=2&studentType=&endDate=2016%2F7%2F21&totalTuitionFees=2&tuitionFeesAlreadyPaid=2

// http://127.0.0.1:8001/pttg/financialstatusservice/v1/accounts/222222/12345678/dailybalancestatus?accommodationFeesAlreadyPaid=2&courseLength=2&innerLondonBorough=true&numberOfDependants=2&studentType=&endDate=2016-07-21&totalTuitionFees=2&tuitionFeesAlreadyPaid=2

// display results
financialstatusModule.controller('FinancialstatusResultCtrl', ['$scope', '$state', '$filter', 'FinancialstatusService', function ($scope, $state, $filter, FinancialstatusService) {

  var pounds = function (num) {
    return $filter('currency')(num, '£', 2);
  };

  var dateDisplay = function (date) {
    return moment(date, 'YYYY-MM-DD').format('DD/MM/YYYY');
  };

  var sortDisplay = function (sortCode) {
    return sortCode.substr(0, 2) + '-' + sortCode.substr(2, 2) + '-' + sortCode.substr(4, 2);
  };

  var finStatus = FinancialstatusService.getDetails();
  var res = FinancialstatusService.getResponse();
  if (!res) {
    $state.go('financialStatusDetails');
    return;
  }

  var sType = FinancialstatusService.getStudentTypeByID(finStatus.studentType);


  $scope.summary = [
    {
      id: 'totalFundsRequired',
      label: 'Total funds required',
      value: pounds(res.minimum)
    },
    {
      id: 'maintenancePeriodChecked',
      label: '28-day period checked',
      value: dateDisplay(res.periodCheckedFrom) + ' to ' + dateDisplay(finStatus.toDate)
    },
  ];
  $scope.showSummary = true;
  $scope.showCriteria = true;

  if (sType.hiddenFields.indexOf('courseStartDate') === -1) {
    $scope.summary.push({id: 'courseLength', label: 'Course length', value: Math.ceil(FinancialstatusService.getCourseLength()) + ' (limited to 9)'});
  }

  $scope.haveResult = (res.fundingRequirementMet !== undefined) ? true : false;
  if ($scope.haveResult) {
    // WE HAVE A RESULT
    if (res.fundingRequirementMet) {

      // PASSED
      $scope.success = true;
      $scope.heading = 'Passed';
      $scope.reason = 'This applicant meets the financial requirements';
    } else {

      // FAILED
      $scope.success = false;
      $scope.heading = 'Not passed';
      $scope.reason = (res.failureReason.recordCount) ? 'The records for this account does not cover the whole 28 day period' : 'One or more daily closing balances are below the total funds required';
      if (res.failureReason.lowestBalanceValue) {
        $scope.summary.push({
          id: 'lowestBalance',
          label: 'Lowest balance',
          value: pounds(res.failureReason.lowestBalanceValue) + ' on ' + dateDisplay(res.failureReason.lowestBalanceDate)
        });
      }
    }
  } else {
    // NO RESULT SO SOME SORT OF ERROR OCCURRED
    console.log('RES', res);
    if (res.failureReason.status === 404) {
      $scope.heading = 'There is no record for the sort code and account number with Barclays';
      $scope.reason = 'We couldn\'t perform the financial requirement check as no information exists for sort code ' + sortDisplay(finStatus.sortCode) + ' and account number ' + finStatus.accountNumber;
    } else {
      $scope.heading = 'You can’t use this service just now. The problem will be fixed as soon as possible';
      $scope.reason = 'Please try again later.';
      $scope.showCriteria = false;
    }

    $scope.success = false;
    $scope.showSummary = false;
  }


  var aFeesAlreadyPaid = pounds(finStatus.accommodationFeesAlreadyPaid);
  if (res.cappedValues && res.cappedValues.accommodationFeesPaid) {
    aFeesAlreadyPaid += ' (limited to ' + pounds(res.cappedValues.accommodationFeesPaid) + ')';
  }

  var criteria = [];
  if (!(res.failureReason && res.failureReason.status === 404)) {
    criteria.push({id: 'studentType', label: 'Student type', value: sType.full});
    criteria.push({id: 'inLondon', label: 'In London', value: (finStatus.inLondon.toLowerCase() === 'yes') ? 'Yes' : 'No'});
    if (sType.hiddenFields.indexOf('courseStartDate') === -1) {
      criteria.push({id: 'courseDatesChecked', label: 'Course dates', value: dateDisplay(finStatus.courseStartDate) + ' to ' + dateDisplay(finStatus.courseEndDate)});
    }

    if (sType.hiddenFields.indexOf('totalTuitionFees') === -1) {
      criteria.push({id: 'totalTuitionFees', label: 'Total tuition fees', value: pounds(finStatus.totalTuitionFees)});
      criteria.push({id: 'tuitionFeesAlreadyPaid', label: 'Tuition fees already paid', value: pounds(finStatus.tuitionFeesAlreadyPaid)});
    }
    criteria.push({id: 'accommodationFeesAlreadyPaid', label: 'Accommodation fees already paid', value: aFeesAlreadyPaid});
    criteria.push({id: 'numberOfDependants', label: 'Number of dependants', value: finStatus.numberOfDependants});
  }
  criteria.push({id: 'sortCode', label: 'Sort code', value: sortDisplay(finStatus.sortCode)});
  criteria.push({id: 'accountNumber', label: 'Account number', value: finStatus.accountNumber});
  criteria.push({id: 'dob', label: 'Date of birth', value: dateDisplay(finStatus.dob)});

  $scope.newSearch = function (e) {
    FinancialstatusService.reset();
    $state.go('financialStatus');
  };

  $scope.searchCriteria = _.filter(criteria, function (row) {
    return (sType.hiddenFields.indexOf(row.id) >= 0 ) ? false: true;
  });


}]);
