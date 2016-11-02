/* jshint node: true */

'use strict';

var financialstatusModule = angular.module('hod.financialstatus', ['ui.router']);


financialstatusModule.factory('FinancialstatusService', ['IOService', '$state', '$timeout', function (IOService, $state, $timeout) {
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
      continuationEndDate: '',
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
        hiddenFields: [],
        noDependantsOnCourseLength: 6
      },
      {
        value: 'doctorate',
        label: 'Doctorate extension scheme',
        full: 'Tier 4 (General) student (doctorate extension scheme)',
        hiddenFields: [
          'courseStartDate',
          'courseEndDate',
          'totalTuitionFees',
          'tuitionFeesAlreadyPaid',
          'continuationEndDate'
        ],
        noDependantsOnCourseLength: null
      },
      {
        value: 'pgdd',
        label: 'Postgraduate doctor or dentist',
        full: 'Tier 4 (General) student (postgraduate doctor or dentist)',
        hiddenFields: [
          'totalTuitionFees',
          'tuitionFeesAlreadyPaid',
          'continuationEndDate'
        ],
        noDependantsOnCourseLength: null
      },
      {
        value: 'sso',
        label: 'Student union sabbatical officer',
        full: 'Tier 4 (General) student union (sabbatical officer)',
        hiddenFields: [
          'totalTuitionFees',
          'tuitionFeesAlreadyPaid',
          'continuationEndDate'
        ],
        noDependantsOnCourseLength: null
      }
    ];
  };

  // get the config detail of the student given the typ code eg sso fo Student union sabbatical officer
  this.getStudentTypeByID = function (typ) {
    return _.findWhere(me.getStudentTypes(), {value: typ });
  };

  // set form the validation status
  this.setValid = function (bool) {
    isValid = bool ? true: false;
  };

  // get the form validation state
  this.getValid = function () {
    return isValid;
  };

  // determine the course length given start and end dates
  this.getCourseLength = function () {
    if (finStatus.studentType === 'doctorate') {
      return 2;
    }

    var from, to;
    if (finStatus.continuationEndDate) {
      from = moment(finStatus.courseEndDate, 'YYYY-MM-DD').add(1, 'day');
      to = finStatus.continuationEndDate;
    } else {
      from = finStatus.courseStartDate;
      to = finStatus.courseEndDate;
    }

    return me.getMonths(from, to);
  };

  this.getEntireCourseLength = function () {
    if (finStatus.studentType === 'doctorate') {
      return 2;
    }

    var to;
    var from = finStatus.courseStartDate;
    if (finStatus.continuationEndDate) {
      to = finStatus.continuationEndDate;
    } else {
      to = finStatus.courseEndDate;
    }

    return me.getMonths(from, to);
  };

  this.getMonths = function (start, end) {
    var start = moment(start, 'YYYY-MM-DD', true);
    var end = moment(end, 'YYYY-MM-DD', true);
    var months = end.diff(start, 'months', true);
    if (start.date() === end.date() && !start.isSame(end)) {
      // when using moment diff months, the same day in months being compared
      // rounds down the months
      // eg 1st June to 1st July equals 1 month, NOT 1 month and 1 day which is the result we want
      // therefore if the start and end days are equal add a day onto the month.
      months += 1/31;
    }

    return months;
  };

  // send the API request
  this.sendDetails = function () {
    if (!isValid) {
      // we only want to send details when the form is valid
      return;
    }

    finStatus.courseLength = Math.ceil(me.getCourseLength());
    finStatus.entireCourseLength = Math.ceil(me.getEntireCourseLength());


    // console.log('courseLength', finStatus.courseLength);
    // console.log('entireCourseLength', finStatus.entireCourseLength);

    // make a copy of the finStatus object and delete fields we don't want to send
    var details = angular.copy(finStatus);
    var sortCode = details.sortCode;
    var accountNumber = details.accountNumber;


    delete details.sortCode;
    delete details.accountNumber;

    var stud = this.getStudentTypeByID(finStatus.studentType);
    _.each(stud.hiddenFields, function (f) {
      delete details[f];
    });

    var url = 'pttg/financialstatusservice/v1/accounts/' + sortCode + '/' + accountNumber + '/dailybalancestatus';
    var attemptNum = 0;
    var maxAttempts = 5;
    var timeoutDuration = 5000;

    var trySendDetails = function () {
      attemptNum++;
      // console.log(attemptNum + ' Starting request');

      IOService.get(url, details, {timeout: timeoutDuration }).then(function (result) {
        // console.log(attemptNum, 'SUCCESS');
        lastAPIresponse = result.data;
        $state.go('financialStatusResults', {studentType: finStatus.studentType});
      }, function (err) {
        // console.log(attemptNum, 'ERROR', err);
        if (err.status === -1 && attemptNum < maxAttempts) {
          // console.log(attemptNum, 'RETRY');
          trySendDetails();
          return;
        }

        // console.log(attemptNum, 'FINAIL FAILED', err);
        lastAPIresponse = {
          failureReason: {
            status: err.status
          }
        };
        $state.go('financialStatusResults', {studentType: finStatus.studentType});
      });
    };
    // start attempting to make the request
    // $timeout(function () {
      trySendDetails();
    // }, 10000);


  };

  this.getResponse = function () {
    return lastAPIresponse;
  };

  this.trackFormSubmission = function (frm) {
    var errcount = 0;
    var errcountstring = '';
    _.each(frm.objs, function (o) {
      if (o.error && o.error.msg) {
        errcount++;
        ga('send', 'event', frm.name, 'validation', o.config.id);
      }
    });
    errcountstring = '' + errcount;
    while(errcountstring.length < 3) {
      errcountstring = '0' + errcountstring;
    }
    ga('send', 'event', frm.name, 'errorcount', errcountstring);
  };

  // on first run set status to blank
  finStatus = this.getBlank();

  return this;
}]);






