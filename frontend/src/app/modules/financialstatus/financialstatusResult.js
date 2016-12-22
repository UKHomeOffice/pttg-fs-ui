/* global angular _ moment Clipboard ga */

/* jshint node: true */

'use strict'

var financialstatusModule = angular.module('hod.financialstatus')

// possible result state codes
financialstatusModule.constant('RESULT_STATES', {
  'passed': 'passed',
  'notpassed_28days': 'notpassed/28days',
  'notpassed_funds': 'notpassed/funds',
  'failure_kc': 'failure/kc',
  'failure_norecord': 'failure/norecord',
  'failure': 'failure'
})

financialstatusModule.constant('RESULT_TEXT', {
  passed: 'Passed',
  notpassed: 'Not passed',
  meetsreq: 'This applicant meets the financial requirements',
  day28cover: 'The records for this account does not cover the whole 28 day period',
  balancesbelow: 'One or more daily closing balances are below the total funds required',
  datamismatch: 'the account number, sort code and date of birth do not match a Barclays account',
  accesscond: 'One or more of the following conditions prevented us from accessing the account:',
  invalid: 'Invalid or inaccessible account',
  notbarclays: 'it is not a Barclays account',
  frozen: 'it is frozen',
  businessacc: 'it is a business account',
  accountclosed: 'the account is closed',
  outoforder: 'You can’t use this service just now. The problem will be fixed as soon as possible',
  kctoblame: 'An error occurred.',
  trylater: 'Please try again later.',
  refresh: 'Please try reloading the page',
  checkname: 'check that the name matches applicant\'s details',
  checkinfo: 'check you have entered the correct information,',
  checkpaper: 'check paper evidence to see if applicant can meet criteria in some other way,',
  checkbank: 'check it is a Barclays current account',
  copybtn: 'Copy to clipboard',
  copiedbtn: 'Copied',
  copysummary: 'The check financial status service confirmed that {{name}} {{passed}} the requirements as the daily closing balance was {{above}} the total funds required.'

})

// #### ROUTES #### //
financialstatusModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
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
      }
    }
  })
  $stateProvider.state({
    name: 'financialStatusCalcResults',
    url: '/result',
    title: 'Financial Status : Result',
    parent: 'financialStatusCalcDetails',
    views: {
      'content@': {
        templateUrl: 'modules/financialstatus/financialstatusResult.html',
        controller: 'FinancialstatusResultCtrl'
      }
    }
  })
}])

financialstatusModule.factory('FinancialstatusResultService', ['FinancialstatusService', '$filter', 'RESULT_STATES', 'RESULT_TEXT', function (FinancialstatusService, $filter, RESULT_STATES, RESULT_TEXT) {
  var me = this
  var data
  var reqdata
  var student

  // what state was the result, eg passed, notpassed/funds, failure etc
  this.getState = function () {
    if (me.haveResult()) {
      // valid result
      if (me.getSuccess()) {
        // OK
        return RESULT_STATES.passed
      } else {
        // Denied
        if (data.failureReason && data.failureReason.recordCount) {
          // Not enough records
          return RESULT_STATES.notpassed_28days
        }
        // funds too low
        return RESULT_STATES.notpassed_funds
      }
    }

    // service failures
    switch (data.failureReason.status) {
      case 404:
        return RESULT_STATES.failure_norecord
      case -1:
        return RESULT_STATES.failure_kc
      default:
        return RESULT_STATES.failure
    }
  }

  // tell the result service what student type we're using
  this.setStudent = function (studentdata) {
    student = studentdata
  }

  // tell the result service what the response data was
  this.setResponse = function (responsedata) {
    data = responsedata
  }

  // tell the result service what data we sent to get this result
  this.setRequest = function (details) {
    reqdata = details
  }

  // wipe the data
  this.reset = function () {
    data = null
    reqdata = null
  }

  // return the data
  this.getData = function () {
    return data
  }

  // did the api return a non failure result - eg passed or notpassed
  this.haveResult = function () {
    return (data.fundingRequirementMet !== undefined) ? true : false
  }

  // did the api return a PASSED status
  this.getSuccess = function () {
    return (data.fundingRequirementMet) ? true : false
  }

  // what results are we summerising
  this.getSummary = function () {
    var summary = []
    if (data.accountHolderName !== '') {[
      {
        id: 'accountHolderName',
        label: 'Account holder name',
        value: data.accountHolderName
      }]}

      summary.concat([
      {
        id: 'totalFundsRequired',
        label: 'Total funds required',
        value: $filter('pounds')(data.minimum)
      },
      {
        id: 'maintenancePeriodChecked',
        label: '28-day period checked',
        value: $filter('dateDisplay')(data.periodCheckedFrom) + ' to ' + $filter('dateDisplay')(reqdata.toDate)
      }
    ])

    // if course dates were supplied then show the calculated course length
    if (student.hiddenFields.indexOf('courseStartDate') === -1) {
      var str = Math.ceil(FinancialstatusService.getCourseLength())
      if (data.cappedValues && data.cappedValues.courseLength) {
        str += ' (limited to ' + data.cappedValues.courseLength + ')'
      } else if (data.cappedValues && data.cappedValues.continuationLength) {
        str += ' (limited to ' + data.cappedValues.continuationLength + ')'
      }
      summary.push({
        id: 'courseLength',
        label: 'Course length',
        value: str
      })
    }

    // if course continuation date is available supplied then show the calculated course length
    if (reqdata.originalCourseStartDate) {
      summary.push({
        id: 'entireCourseLength',
        label: 'Entire course length',
        value: Math.ceil(FinancialstatusService.getMonths(reqdata.originalCourseStartDate, reqdata.courseEndDate))
      })
    }

    // if failed due to lowest balance value then display that
    if (data.failureReason && data.failureReason.lowestBalanceValue) {
      summary.push({
        id: 'lowestBalance',
        label: 'Lowest balance',
        value: $filter('pounds')(data.failureReason.lowestBalanceValue) + ' on ' + $filter('dateDisplay')(data.failureReason.lowestBalanceDate)
      })
    }

    if (data.leaveEndDate) {
      summary.push({
        id: 'estimatedLeaveEndDate',
        label: 'Estimated leave end date',
        value: $filter('dateDisplay')(data.leaveEndDate)
      })
    }

    summary.push({
      id: 'resultTimestamp',
      label: 'Result timestamp',
      value: moment(reqdata.responseTimeStamp).format('DD/MM/YYYY HH:mm:ss')
    })

    return summary
  }

  this.getCopySummary = function () {
    var str = RESULT_TEXT.copysummary
    str = str.replace('{{name}}', data.accountHolderName)
    if (me.getSuccess()) {
      str = str.replace('{{passed}}', 'passed')
      str = str.replace('{{above}}', 'above')
    } else {
      str = str.replace('{{passed}}', 'did not pass')
      str = str.replace('{{above}}', 'below')
    }
    return str
  }

  // get the headings text to display on the results page for each state
  this.getText = function (state) {
    switch (state) {
      case RESULT_STATES.passed:
        return {
          heading: RESULT_TEXT.passed,
          reason: RESULT_TEXT.meetsreq
        }

      case RESULT_STATES.notpassed_28days:
        return {
          heading: RESULT_TEXT.notpassed,
          reason: RESULT_TEXT.day28cover
        }

      case RESULT_STATES.notpassed_funds:
        return {
          heading: RESULT_TEXT.notpassed,
          reason: RESULT_TEXT.balancesbelow
        }

      case RESULT_STATES.failure_norecord:
        return {
          heading: RESULT_TEXT.invalid,
          reason: RESULT_TEXT.accesscond,
          reasonInfo: [
            RESULT_TEXT.datamismatch,
            RESULT_TEXT.notbarclays,
            RESULT_TEXT.frozen,
            RESULT_TEXT.businessacc,
            RESULT_TEXT.accountclosed
          ]
        }

      case RESULT_STATES.failure_kc:
        return {
          heading: RESULT_TEXT.kctoblame,
          reason: RESULT_TEXT.refresh
        }
    }

    return {
      heading: RESULT_TEXT.outoforder,
      reason: RESULT_TEXT.trylater
    }
  }

  // get the summary of the search criteria used to arrive at this result
  this.getCriteria = function (state) {
    var criteria = []
    var from
    var to

    // no summary required if the api failed
    if (state === RESULT_STATES.failure) {
      return criteria
    }

    // default criteria list we're going to show
    var criteriaList = {
      applicationRaisedDate: true,
      studentType: true,
      courseType: (student.hiddenFields.indexOf('courseType') === -1),
      inLondon: true,
      courseDatesChecked: (student.hiddenFields.indexOf('courseStartDate') === -1),
      continuationCourse: (student.hiddenFields.indexOf('continuationCourse') === -1),
      tuitionFees: (student.hiddenFields.indexOf('totalTuitionFees') === -1),
      accommodationFeesAlreadyPaid: (student.hiddenFields.indexOf('accommodationFeesAlreadyPaid') === -1),
      numberOfDependants: true,
      bankAccount: true,
      dob: true
    }

    // if no record was found we only show the bank account and DOB
    if (state === RESULT_STATES.failure_norecord) {
      criteriaList = {
        bankAccount: true,
        dob: true
      }
    }
    console.log(reqdata.applicationRaisedDate)
    if (criteriaList.applicationRaisedDate && reqdata.applicationRaisedDate !== 'Invalid date') {
      criteria.push({
        id: 'applicationRaisedDate',
        label: 'Application raised date',
        value: moment(reqdata.applicationRaisedDate, 'YYYY-MM-DD').format('DD/MM/YYYY')
      })
    }

    // add the criteria
    if (criteriaList.studentType) {
      criteria.push({
        id: 'studentType',
        label: 'Student type',
        value: student.full
      })
    }

    if (criteriaList.courseType) {
      var course = _.findWhere(FinancialstatusService.getCourseTypeOptions(), {value: reqdata.courseType})
      criteria.push({
        id: 'courseType',
        label: 'Course type',
        value: course.label
      })
    }

    if (criteriaList.inLondon) {
      criteria.push({
        id: 'inLondon',
        label: 'In London',
        value: (reqdata.inLondon.toLowerCase() === 'yes') ? 'Yes' : 'No'
      })
    }

    if (criteriaList.courseDatesChecked) {
      from = reqdata.courseStartDate
      to = reqdata.courseEndDate

      criteria.push({
        id: 'courseDatesChecked',
        label: 'Course dates',
        value: $filter('dateDisplay')(from) + ' to ' + $filter('dateDisplay')(to)
      })
    }

    if (criteriaList.continuationCourse) {
      if (reqdata.continuationCourse === 'yes') {
        criteria.push({
          id: 'continuationCourse',
          label: 'Continuation course',
          value: 'Yes'
        })

        criteria.push({
          id: 'originalCourseStartDate',
          label: 'Original course start date',
          value: $filter('dateDisplay')(reqdata.originalCourseStartDate)
        })
      } else {
        criteria.push({
          id: 'continuationCourse',
          label: 'Continuation course',
          value: 'No'
        })
      }
    }

    if (criteriaList.tuitionFees) {
      criteria.push({
        id: 'totalTuitionFees',
        label: 'Total tuition fees',
        value: $filter('pounds')(reqdata.totalTuitionFees)
      })

      criteria.push({
        id: 'tuitionFeesAlreadyPaid',
        label: 'Tuition fees already paid',
        value: $filter('pounds')(reqdata.tuitionFeesAlreadyPaid)
      })
    }

    if (criteriaList.accommodationFeesAlreadyPaid) {
      var aFeesAlreadyPaid = $filter('pounds')(reqdata.accommodationFeesAlreadyPaid)
      if (data.cappedValues && data.cappedValues.accommodationFeesPaid) {
        aFeesAlreadyPaid += ' (limited to ' + $filter('pounds')(data.cappedValues.accommodationFeesPaid) + ')'
      }
      criteria.push({
        id: 'accommodationFeesAlreadyPaid',
        label: 'Accommodation fees already paid',
        value: aFeesAlreadyPaid
      })
    }

    if (criteriaList.numberOfDependants) {
      criteria.push({
        id: 'numberOfDependants',
        label: 'Number of dependants',
        value: reqdata.numberOfDependants
      })
    }

    if (criteriaList.bankAccount && reqdata.accountNumber !== '') {
      criteria.push({
        id: 'sortCode',
        label: 'Sort code',
        value: $filter('sortDisplay')(reqdata.sortCode)
      })

      criteria.push({
        id: 'accountNumber',
        label: 'Account number',
        value: reqdata.accountNumber
      })
    }

    if (criteriaList.dob && reqdata.dob !== '') {
      criteria.push({
        id: 'dob',
        label: 'Date of birth',
        value: $filter('dateDisplay')(reqdata.dob)
      })
    }

    return criteria
  }

  this.getWhatNext = function (state) {
    switch (state) {
      case RESULT_STATES.passed:
        return [
          RESULT_TEXT.checkname,
          RESULT_TEXT.checkinfo
        ]

      case RESULT_STATES.failure_norecord:
        return [
          RESULT_TEXT.checkinfo,
          RESULT_TEXT.checkpaper,
          RESULT_TEXT.checkbank
        ]

      default:
        return [
          RESULT_TEXT.checkinfo,
          RESULT_TEXT.checkpaper
        ]
    }
  }

  return this
}])

// display results
financialstatusModule.controller('FinancialstatusResultCtrl', ['$scope', '$state', '$stateParams', '$filter', 'FinancialstatusService', 'FinancialstatusResultService', 'RESULT_STATES', 'RESULT_TEXT', '$timeout',
  function ($scope, $state, $stateParams, $filter, FinancialstatusService, FinancialstatusResultService, RESULT_STATES, RESULT_TEXT, $timeout) {
    // check for result data
    var resdata = FinancialstatusService.getResponse()
    if (!resdata) {
    // no result data so no we cannot display a result page - go back to the details page
      $state.go('financialStatusDetails')
      return
    }

    // setup the results service object with what we already know
    var resServ = FinancialstatusResultService
    var finStatus = FinancialstatusService.getDetails()
    var sType = FinancialstatusService.getStudentTypeByID(finStatus.studentType)

    resServ.setStudent(sType)
    resServ.setResponse(resdata)
    resServ.setRequest(finStatus)

    var state = resServ.getState()

    $scope.haveResult = resServ.haveResult()
    $scope.summary = resServ.getSummary()
    $scope.copysummary = resServ.getCopySummary()
    $scope.showSummary = $scope.haveResult
    $scope.success = resServ.getSuccess()

    // set the text
    var text = resServ.getText(state)
    $scope.heading = text.heading
    $scope.reason = text.reason
    $scope.reasonInfo = text.reasonInfo

    $scope.whatNext = resServ.getWhatNext(state)

    $scope.searchCriteria = resServ.getCriteria(state)
    $scope.showCriteria = (state !== RESULT_STATES.failure) ? true : false

    // track
    ga('set', 'page', $state.href($state.current.name, $stateParams) + '/' + state)
    ga('send', 'pageview')

    // new search button
    $scope.newSearch = function (e) {
      FinancialstatusService.reset()
      $state.go('financialStatus')
    }

    // edit search button
    $scope.editSearch = function (e) {
      $state.go('financialStatusDetails', {studentType: sType.value})
    }

  // #### COPY AND PASTE ####
    $scope.copyToClipboardBtnText = RESULT_TEXT.copybtn
    var lineLength = function (str, len) {
      while (str.length < len) {
        str += ' '
      }
      return str
    }

    // compile the copy text
    var copyText = text.heading.toUpperCase() + '\n' + text.reason + '\n\nRESULTS\n'
    _.each($scope.summary, function (obj) {
      copyText += lineLength(obj.label + ': ', 36) + obj.value + '\n'
    })

    // add the your search to it
    copyText += '\n\nSEARCH CRITERIA\n'
    _.each($scope.searchCriteria, function (obj) {
      var val = (obj.id === 'accountNumber') ? 'XXXX' + obj.value.substr(4) : obj.value
      copyText += lineLength(obj.label + ': ', 36) + val + '\n'
    })

    // copyText += '\n\n' + lineLength('Enquiry time: ', 36) + moment(resdata.responseTimeStamp).format('DD/MM/YYYY HH:mm:ss')

    $scope.copyText = copyText

    // init the clipboard object
    var clipboard = new Clipboard('.button--copy', {
      text: function () {
        return copyText
      }
    })

    var timeoutResetButtonText = function () {
      $timeout(function () {
        $scope.copyToClipboardBtnText = RESULT_TEXT.copybtn
        $scope.$applyAsync()
      }, 2000)
    }

    clipboard.on('success', function (e) {
      $scope.copyToClipboardBtnText = RESULT_TEXT.copiedbtn
      $scope.$applyAsync()
      e.clearSelection()
      timeoutResetButtonText()
    })
    clipboard.on('error', function (e) {
      console.log('ClipBoard error', e)
      $scope.copysummary = e.action + ' ' + e.trigger
      $scope.$applyAsync()
    })
  }])
