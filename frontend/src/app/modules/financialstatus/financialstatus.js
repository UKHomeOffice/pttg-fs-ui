/* global angular _ moment ga */

/* jshint node: true */

'use strict'

var financialstatusModule = angular.module('hod.financialstatus', ['ui.router'])

financialstatusModule.factory('FinancialstatusService', ['IOService', '$state', '$timeout', 'CONFIG', '$window', function (IOService, $state, $timeout, CONFIG, $window) {
  var me = this
  var finStatus
  var isValid = false
  var lastAPIresponse

  this.isCalc = function () {
    return ($state.current.name.indexOf('Calc') > 0)
  }

  this.reset = function () {
    isValid = false
    lastAPIresponse = null
    finStatus = this.getBlank()
  }

  // get the form details
  this.getDetails = function () {
    return finStatus
  }

  // get the defaults
  this.getBlank = function () {
    return {
      continuationCourse: null,
      applicationRaisedDate: '',
      applicantType: '',
      toDate: '',
      inLondon: null,
      originalCourseStartDate: '',
      courseStartDate: '',
      courseEndDate: '',
      totalTuitionFees: '',
      tuitionFeesAlreadyPaid: '',
      accommodationFeesAlreadyPaid: '',
      dependants: '',
      sortCode: '',
      accountNumber: '',
      dob: ''
    }
  }

  this.getCourseTypeOptions = function () {
    return [
      { label: 'Pre-sessional', value: 'pre-sessional' },
      { label: 'Main', value: 'main' }
    ]
  }

  // get the available types
  this.getApplicantTypes = function () {
    return [
      {
        tier: 2,
        value: 't2main',
        label: 'Main applicant (with & without dependants)',
        full: 'Main applicant (with & without dependants)',
        hiddenFields: [
          'courseStartDate',
          'courseEndDate',
          'accommodationFeesAlreadyPaid',
          'inLondon',
          'courseType',
          'totalTuitionFees',
          'tuitionFeesAlreadyPaid',
          'continuationCourse'
        ],
        noDependantsOnCourseLength: null
      },
      {
        tier: 2,
        value: 't2dependant',
        label: 'Dependant only',
        full: 'Dependant only',
        hiddenFields: [
          'dependants',
          'courseStartDate',
          'courseEndDate',
          'accommodationFeesAlreadyPaid',
          'inLondon',
          'courseType',
          'totalTuitionFees',
          'tuitionFeesAlreadyPaid',
          'continuationCourse',
          'originalCourseStartDate'
        ],
        noDependantsOnCourseLength: null
      },
      {
        tier: 4,
        value: 'nondoctorate',
        label: 'General student',
        full: 'Tier 4 (General) student',
        hiddenFields: [],
        noDependantsOnCourseLength: 6
      },
      {
        tier: 4,
        value: 'doctorate',
        label: 'Doctorate extension scheme',
        full: 'Tier 4 (General) student (doctorate extension scheme)',
        hiddenFields: [
          'courseType',
          'courseStartDate',
          'courseEndDate',
          'totalTuitionFees',
          'tuitionFeesAlreadyPaid',
          'continuationCourse',
          'originalCourseStartDate'
        ],
        noDependantsOnCourseLength: null
      },
      {
        tier: 4,
        value: 'pgdd',
        label: 'Postgraduate doctor or dentist',
        full: 'Tier 4 (General) student (postgraduate doctor or dentist)',
        hiddenFields: [
          'courseType',
          'totalTuitionFees',
          'tuitionFeesAlreadyPaid'
        ],
        noDependantsOnCourseLength: null
      },
      {
        tier: 4,
        value: 'sso',
        label: 'Student union sabbatical officer',
        full: 'Tier 4 (General) student union (sabbatical officer)',
        hiddenFields: [
          'courseType',
          'totalTuitionFees',
          'tuitionFeesAlreadyPaid'
        ],
        noDependantsOnCourseLength: null
      },
      {
        tier: 5,
        value: 't5main',
        label: 'Main applicant (with & without dependants)',
        full: 'Main applicant (with & without dependants)',
        hiddenFields: [
          'courseStartDate',
          'courseEndDate',
          'accommodationFeesAlreadyPaid',
          'inLondon',
          'courseType',
          'totalTuitionFees',
          'tuitionFeesAlreadyPaid',
          'continuationCourse'
        ],
        noDependantsOnCourseLength: null
      },
      {
        tier: 5,
        value: 't5dependant',
        label: 'Dependant only',
        full: 'Dependant only',
        hiddenFields: [
          'dependants',
          'courseStartDate',
          'courseEndDate',
          'accommodationFeesAlreadyPaid',
          'inLondon',
          'courseType',
          'totalTuitionFees',
          'tuitionFeesAlreadyPaid',
          'continuationCourse',
          'originalCourseStartDate'
        ],
        noDependantsOnCourseLength: null
      }
    ]
  }

  // get the config detail of the student given the typ code eg sso fo Student union sabbatical officer
  this.getApplicantTypeByID = function (typ) {
    return _.findWhere(me.getApplicantTypes(), { value: typ })
  }

  // set form the validation status
  this.setValid = function (bool) {
    isValid = (bool) ? true : false
  }

  // get the form validation state
  this.getValid = function () {
    return isValid
  }

  // determine the course length given start and end dates
  this.getCourseLength = function () {
    if (finStatus.applicantType === 'doctorate') {
      return 2
    }

    var from, to
    from = finStatus.courseStartDate
    to = finStatus.courseEndDate

    return me.getMonths(from, to)
  }

  this.getEntireCourseLength = function () {
    if (finStatus.applicantType === 'doctorate') {
      return 2
    }

    var to = finStatus.courseEndDate
    var from = finStatus.courseStartDate
    if (finStatus.originalCourseStartDate) {
      from = finStatus.originalCourseStartDate
    }
    return me.getMonths(from, to)
  }

  this.getMonths = function (start, end) {
    start = moment(start, 'YYYY-MM-DD', true)
    end = moment(end, 'YYYY-MM-DD', true)
    var months = end.diff(start, 'months', true)
    if (start.date() === end.date() && !start.isSame(end)) {
      // when using moment diff months, the same day in months being compared
      // rounds down the months
      // eg 1st June to 1st July equals 1 month, NOT 1 month and 1 day which is the result we want
      // therefore if the start and end days are equal add a day onto the month.
      months += 1 / 31
    }
    return months
  }

  // send the API request
  this.sendDetails = function () {
    if (!isValid) {
      // we only want to send details when the form is valid
      return
    }

    var applicantType = this.getApplicantTypeByID(finStatus.applicantType)
    var tier = applicantType.tier

    if (tier === 4) {
      finStatus.courseLength = Math.ceil(this.getCourseLength())
      finStatus.entireCourseLength = Math.ceil(this.getEntireCourseLength())
    }

    // make a copy of the finStatus object and delete fields we don't want to send
    var details = angular.copy(finStatus)
    var sortCode = details.sortCode
    var accountNumber = details.accountNumber
    var resultUrl = 'financialStatusResults'

    if (this.isCalc()) {
      sortCode = '010616'
      accountNumber = '00030000'
      details.dob = '1974-05-13'
      resultUrl = 'financialStatusCalcResults'
    }

    if (tier === 2 || tier === 5) {
      details.applicantType = finStatus.applicantType.substr(2)
    }
    details.studentType = details.applicantType

    // delete details.applicantType
    delete details.applicationRaisedDate
    delete details.sortCode
    delete details.accountNumber

    if (details.continuationCourse !== 'yes') {
      delete details.originalCourseStartDate
    }
    delete details.continuationCourse

    var stud = this.getApplicantTypeByID(finStatus.applicantType)
    _.each(stud.hiddenFields, function (f) {
      delete details[f]
    })

    if (details.applicantType === 'dependant') {
      details.dependants = 0
    }

    var url = 't' + tier + '/accounts/' + sortCode + '/' + accountNumber + '/dailybalancestatus'
    var attemptNum = 0

    var trySendDetails = function () {
      attemptNum++

      IOService.get(url, details, { timeout: CONFIG.timeout }).then(function (result) {
        console.log('trySendDetails', attemptNum, url, details)
        lastAPIresponse = result.data
        lastAPIresponse.responseTimeStamp = new Date()
        $state.go(resultUrl, {applicantType: finStatus.applicantType})
      }, function (err) {
        console.log('trySendDetails', attemptNum, err)
        if (err.status === -1 && attemptNum < CONFIG.retries) {
          trySendDetails()
          return
        }

        if (err.status === -1) {
          $window.location.reload()
          return
        }

        lastAPIresponse = {
          failureReason: {
            status: err.status
          }
        }
        $state.go(resultUrl, {applicantType: finStatus.applicantType})
      })
    }
    // start attempting to make the request
    trySendDetails()
  }

  this.getResponse = function () {
    return lastAPIresponse
  }

  this.trackFormSubmission = function (frm) {
    var errcount = 0
    var errcountstring = ''
    _.each(frm.objs, function (o) {
      if (o.error && o.error.msg) {
        errcount++
        ga('send', 'event', frm.name, 'validation', o.config.id)
      }
    })
    errcountstring = '' + errcount
    while (errcountstring.length < 3) {
      errcountstring = '0' + errcountstring
    }
    ga('send', 'event', frm.name, 'errorcount', errcountstring)
  }

  // on first run set status to blank
  finStatus = this.getBlank()

  return this
}])

