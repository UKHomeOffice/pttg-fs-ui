/* jshint node: true */

'use strict';

var financialstatusModule = angular.module('hod.financialstatus', ['ui.router']);


financialstatusModule.factory('FinancialstatusService', ['IOService', '$state', function (IOService, $state) {
  var me = this;
  var finStatus;
  var isValid = false;
  var lastAPIresponse;

  this.reset = function () {
    isValid = false;
    lastAPIresponse = null;
    finStatus = this.getBlank();
  };

  // get the form details
  this.getDetails = function () {
    return finStatus;
  };

  // get the defaults
  this.getBlank = function () {
    return {
      studentType: '',
      toDate: '',
      inLondon: null,
      courseStartDate: '',
      courseEndDate: '',
      totalTuitionFees: '',
      tuitionFeesAlreadyPaid: '',
      accommodationFeesAlreadyPaid: '',
      numberOfDependants: '',
      sortCode: '',
      accountNumber: '',
      dob: ''
    };
  };

  // get the available types
  this.getStudentTypes = function () {
    return [
      {
        value: 'nondoctorate',
        label: 'General student',
        full: 'Tier 4 (General) student',
        hiddenFields: []
      },
      {
        value: 'doctorate',
        label: 'Doctorate extension scheme)',
        full: 'Tier 4 (General) student (doctorate extension scheme)',
        hiddenFields: ['courseStartDate', 'courseEndDate', 'totalTuitionFees', 'tuitionFeesAlreadyPaid']
      },
      {
        value: 'pgdd',
        label: 'Postgraduate doctor or dentist',
        full: 'Tier 4 (General) student (postgraduate doctor or dentist)',
        hiddenFields: ['totalTuitionFees', 'tuitionFeesAlreadyPaid']
      },
      {
        value: 'sso',
        label: 'Student sabbatical officer',
        full: 'Tier 4 (General) student (sabbatical officer)',
        hiddenFields: ['totalTuitionFees', 'tuitionFeesAlreadyPaid']
      }
    ];
  };

  this.getStudentTypeByID = function (typ) {
    return _.findWhere(me.getStudentTypes(), {value: typ });
  };

  this.setValid = function (bool) {
    isValid = bool ? true: false;
    console.log('isValid', isValid);
  };

  this.getValid = function () {
    return isValid;
  };

  this.getCourseLength = function () {
    if (finStatus.studentType === 'doctorate') {
      return 2;
    }
    var start = moment(finStatus.courseStartDate, 'YYYY-MM-DD', true);
    var end = moment(finStatus.courseEndDate, 'YYYY-MM-DD', true);
    var months = end.diff(start, 'months', true);
    if (start.date() === end.date() && !start.isSame(end)) {
      // when using moment diff months, the same day in months being compared
      // rounds down the months
      // eg 1st June to 1st July equals 1 month, NOT 1 month and 1 day which is the result we want
      // therefore if the start and end days are equal add a day onto the month.
      months += 1/31;
    }

    console.log(finStatus.courseStartDate, finStatus.courseEndDate, months);
    return months;
  };

  this.sendDetails = function () {
    if (!isValid) {
      // we only want to send details when the form is valid
      return;
    }
    // make a copy of the finStatus object and delete fields we don't want to send
    var details = angular.copy(finStatus);
    var sortCode = details.sortCode;
    var accountNumber = details.accountNumber;


    delete details.sortCode;
    delete details.accountNumber;

    details.courseLength = Math.ceil(me.getCourseLength());

    var url = 'pttg/financialstatusservice/v1/accounts/' + sortCode + '/' + accountNumber + '/dailybalancestatus';

    console.log('detailsSubmit');
    console.log(url);
    console.log(details);

    IOService.get(url, details).then(function (result) {
      lastAPIresponse = result.data;
      $state.go('financialStatusResults', {studentType: finStatus.studentType});
    }, function (err) {
      lastAPIresponse = {
        failureReason: {
          status: 404
        }
      };
      $state.go('financialStatusResults', {studentType: finStatus.studentType});
    });
  };

  this.getResponse = function () {
    return lastAPIresponse;
  };

  // on first run set status to blank
  finStatus = this.getBlank();

  return this;
}]);





