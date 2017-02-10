/* global angular _ moment ga */

/* jshint node: true */

'use strict'

var fsModule = angular.module('hod.fs', ['ui.router'])

fsModule.factory('FsService', ['$filter', 'FsInfoService', 'FsBankService', 'IOService', function ($filter, FsInfoService, FsBankService, IOService) {
  var me = this
  var _application

  this.reset = function () {
    _application = {
      doCheck: '',
      dob: '',
      sortCode: '',
      accountNumber: '',
      inLondon: '',
      courseType: '',
      continuationCourse: '',
      applicationRaisedDate: '',
      applicantType: '',
      endDate: '',
      originalCourseStartDate: '',
      courseStartDate: '',
      courseEndDate: '',
      totalTuitionFees: '',
      tuitionFeesAlreadyPaid: '',
      accommodationFeesAlreadyPaid: '',
      dependants: ''
    }
  }

  // get the form details
  this.getApplication = function () {
    return _application
  }

  this.setKnownParamsFromState = function (obj, stateParams) {
    obj.tier = Number(stateParams.tier) // tier 4
    obj.applicantType = stateParams.applicantType || null // nondoctorate, sso
    obj.doCheck = (stateParams.calcOrBank === 'bank') ? 'yes' : 'no' // bank check

    if (obj.doCheck !== 'yes') {
      obj.sortCode = ''
      obj.accountNumber = ''
      obj.dob = ''
    }
  }

  // does the object (an _application) have result/calc properties
  this.hasThresholdInfo = function (obj) {
    if (_.has(obj, 'thresholdResponse') && _.has(obj.thresholdResponse, 'data')) {
      return true
    }

    return false
  }

  this.clearThresholdResponse = function (obj) {
    if (me.hasThresholdInfo) {
      delete obj.thresholdResponse
      return true
    }

    return false
  }

  // get the threshold url
  this.getThresholdUrl = function (obj) {
    if (!obj.tier) {
      return null
    }
    return 't' + obj.tier + '/threshold'
  }

  // get the required params for the threshold request
  this.getThresholdParams = function (obj) {
    var variant = FsInfoService.getVariant(obj.tier, obj.applicantType)
    var fields = FsInfoService.getFields(variant.fields)
    var params = {}
    _.each(fields, function (f) {
      params[f] = obj[f]
    })

    // [TODO]
    params.studentType = obj.applicantType
    params.applicantType = obj.applicantType

    return params
  }

  // make the AJAX to the api for the threshold value
  this.sendThresholdRequest = function (obj) {
    var u = me.getThresholdUrl(obj)
    var params = me.getThresholdParams(obj)
    return IOService.get(u, params)
  }

  // determine the result data to show on the results page
  this.getResults = function (obj) {
    var results = {}
    var tier = FsInfoService.getTier(obj.tier)
    var capped = me.getThresholdCappedValues(obj)

    if (FsBankService.hasResult(obj) && _.has(obj.dailyBalanceResponse.data, 'accountHolderName')) {
      results.accountHolderName = {
        label: 'Account holder name',
        display: obj.dailyBalanceResponse.data.accountHolderName
      }
    }

    if (me.hasThresholdInfo(obj)) {
      results.totalFundsRequired = {
        label: 'Total funds required',
        display: $filter('pounds')(obj.thresholdResponse.data.threshold)
      }
    }

    results.maintenancePeriodChecked = {
      label: tier.nDaysRequired + '-day period checked',
      display: me.getPeriodChecked(obj)
    }

    if (FsBankService.hasResult(obj) && !FsBankService.passed(obj)) {
      results.lowestBalance = {
        label: 'Lowest balance',
        display: $filter('pounds')(obj.dailyBalanceResponse.data.failureReason.lowestBalanceValue) + ' on ' + $filter('dateDisplay')(obj.dailyBalanceResponse.data.failureReason.lowestBalanceDate)
      }
    }

    if (me.hasThresholdInfo(obj)) {
      if (_.has(obj.thresholdResponse.data, 'leaveEndDate')) {
        results.estimatedLeaveEndDate = {
          label: 'Estimated leave end date',
          display: $filter('dateDisplay')(obj.thresholdResponse.data.leaveEndDate)
        }
      }
      if (_.has(obj.thresholdResponse, 'responseTime')) {
        results.responseTime = {
          label: 'Result timestamp',
          display: obj.thresholdResponse.responseTime.format('HH:mm:ss DD/MM/YYYY')
        }
      }
    }

    if (obj.courseStartDate && obj.courseEndDate) {
      results.courseLength = {
        label: 'Course length',
        display: me.getCourseLength(obj)
      }

      if (_.has(capped, 'courseLength')) {
        results.courseLength.display += ' (limited to ' + capped.courseLength + ')'
      }

      if (obj.continuationCourse === 'yes') {
        results.entireCourseLength = {
          label: 'Entire course length',
          display: me.getEntireCourseLength(obj)
        }
      }
    }

    return results
  }

  // determine the period that was/needs to be checked for lowest balance
  this.getPeriodChecked = function (obj) {
    var tier = FsInfoService.getTier(obj.tier)
    var nDays = tier.nDaysRequired - 1
    var endDate = moment(obj.endDate)
    var startDate = endDate.clone().subtract(nDays, 'days')
    return $filter('dateDisplay')(startDate) + ' to ' + $filter('dateDisplay')(endDate)
  }

  this.getThresholdCappedValues = function (obj) {
    if (_.has(obj, 'thresholdResponse') && _.has(obj.thresholdResponse, 'data') && _.has(obj.thresholdResponse.data, 'cappedValues')) {
      return obj.thresholdResponse.data.cappedValues
    }
    return null
  }

  // return data that made up the search criteria
  this.getCriteria = function (obj) {
    // basics
    var tier = FsInfoService.getTier(obj.tier)
    var variant = _.findWhere(tier.variants, { value: obj.applicantType })
    var fields = FsInfoService.getFields(variant.fields)
    var capped = me.getThresholdCappedValues(obj)

    if (obj.continuationCourse !== 'yes') {
      // remove the original course start date from the results if its not a continuation course
      fields = _.without(fields, 'originalCourseStartDate')
    }

    fields = _.without(fields, 'courseEndDate', 'endDate')

    var criteria = {}

    criteria.tier = {
      label: 'Tier',
      display: tier.label
    }

    criteria.applicantType = {
      label: 'Applicant type',
      display: variant.label
    }

    _.each(fields, function (f) {
      if (!_.has(obj, f)) {
        return
      }
      var info = FsInfoService.getFieldInfo(f)
      var disp = ''
      var lab = info.summary.replace(/({{nDaysRequired}})/, tier.nDaysRequired)
      switch (info.format) {
        case 'date':
          disp = $filter('dateDisplay')(obj[f])
          break

        case 'pounds':
          disp = $filter('pounds')(obj[f])
          break

        case 'radio':
          disp = _.findWhere(info.options, {value: obj[f]}).label
          break

        default:
          disp = obj[f]
      }

      if (f === 'courseStartDate') {
        f = 'courseDatesChecked'
        lab = 'Course dates checked'
        disp += ' to ' + $filter('dateDisplay')(obj.courseEndDate)
      }

      if (f === 'accommodationFeesAlreadyPaid' && capped.accommodationFeesPaid) {
        disp += ' (limited to ' + $filter('pounds')(capped.accommodationFeesPaid) + ')'
      }

      criteria[f] = { label: lab, display: disp }
    })

    criteria = angular.merge(criteria, me.getConsentCriteria(obj))

    return criteria
  }

  this.getConsentCriteria = function (obj) {
    var criteria = {}
    if (!FsBankService.hasBankInfo(obj)) {
      return {}
    }
    criteria.sortCode = { label: 'Sort code', display: $filter('sortDisplay')(obj.sortCode) }
    criteria.accountNumber = { label: 'Account number', display: obj.accountNumber }
    criteria.dob = { label: 'Date of birth', display: $filter('dateDisplay')(obj.dob) }
    return criteria
  }

  this.getThingsToDoNext = function (obj) {
    var doNext = []
    if (obj.doCheck !== 'yes') {
      doNext.push(FsInfoService.t('manualCheck'))
      doNext.push(FsInfoService.t('copyToCid'))
      return doNext
    }

    if (FsBankService.consentDenied(obj)) {
      doNext.push(FsInfoService.t('checkDataEntry'))
      doNext.push(FsInfoService.t('manualCheck'))
      doNext.push(FsInfoService.t('copyToCid'))
      return doNext
    }

    if (FsBankService.hasResult(obj)) {
      doNext.push(FsInfoService.t('checkName'))

      if (!FsBankService.passed(obj)) {
        doNext.push(FsInfoService.t('checkPaper'))
      }

      doNext.push(FsInfoService.t('copyToCid'))
    } else {

    }

    return doNext
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
    return Math.ceil(months)
  }

  this.getCourseLength = function (obj) {
    return me.getMonths(obj.courseStartDate, obj.courseEndDate)
  }

  this.getEntireCourseLength = function (obj) {
    return me.getMonths(obj.originalCourseStartDate, obj.courseEndDate)
  }

  this.getPlainTextResults = function (obj) {
    var lineLength = function (str, len) {
      while (str.length < len) {
        str += ' '
      }
      return str
    }

    return lineLength('Plain text results', 40)
  }

  me.reset()

  return me
}])
