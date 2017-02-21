/* global angular _ moment ga */

/* jshint node: true */

'use strict'

var fsModule = angular.module('hod.fs', ['ui.router'])

fsModule.factory('FsService', ['$filter', 'FsInfoService', 'FsBankService', 'IOService', 'CONFIG', function ($filter, FsInfoService, FsBankService, IOService, CONFIG) {
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
      dependants: '',
      dependantsOnly: null
    }
  }

  // get the form details
  this.getApplication = function () {
    return _application
  }

  this.splitApplicantType = function (str) {
    var results = {
      applicantType: null,
      dependantsOnly: false
    }

    if (!_.isString(str)) {
      return results
    }

    var split = str.split('-')
    if (split.length >= 1) {
      results.applicantType = split[0]
    }

    if (split.length >= 2 && split[1] === 'dependants') {
      results.dependantsOnly = true
    }

    return results
  }

  this.setKnownParamsFromState = function (obj, stateParams) {
    obj.tier = Number(stateParams.tier) // tier 4
    var tier = FsInfoService.getTier(obj.tier)
    var split = me.splitApplicantType(stateParams.applicantType)
    obj.applicantType = split.applicantType // general, sso
    obj.dependantsOnly = split.dependantsOnly

    var variant = _.findWhere(tier.variants, { value: obj.applicantType })
    if (variant.dependantsOnly) {
      obj.dependantsOnly = true
    }

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
    var params = {dependants: 0}
    _.each(fields, function (f) {
      params[f] = obj[f]
    })

    params.dependantsOnly = obj.dependantsOnly

    // [TODO]
    params.studentType = obj.applicantType
    params.applicantType = obj.applicantType
    if (obj.dependantsOnly) {
      if (!_.has(fields.accommodationFeesAlreadyPaid)) {
        params.accommodationFeesAlreadyPaid = 0
      }
      if (!_.has(fields.totalTuitionFees)) {
        params.totalTuitionFees = 0
      }
      if (!_.has(fields.tuitionFeesAlreadyPaid)) {
        params.tuitionFeesAlreadyPaid = 0
      }
    }

    return params
  }

  // make the AJAX to the api for the threshold value
  this.sendThresholdRequest = function (obj) {
    var u = me.getThresholdUrl(obj)
    var params = me.getThresholdParams(obj)
    return IOService.get(u, params, { timeout: CONFIG.timeout })
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

    if (FsBankService.hasResult(obj) && !FsBankService.passed(obj) && obj.dailyBalanceResponse.data.failureReason.lowestBalanceValue) {
      results.lowestBalance = {
        label: 'Lowest balance',
        display: $filter('pounds')(obj.dailyBalanceResponse.data.failureReason.lowestBalanceValue) + ' on ' + $filter('dateDisplay')(obj.dailyBalanceResponse.data.failureReason.lowestBalanceDate)
      }
    }

    if (me.hasThresholdInfo(obj)) {
      if (_.has(obj.thresholdResponse.data, 'leaveEndDate') && obj.thresholdResponse.data.leaveEndDate) {
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
    var dependantsOnlyOptions = FsInfoService.getFieldInfo('dependantsOnly')

    if (obj.continuationCourse !== 'yes') {
      // remove the original course start date from the results if its not a continuation course
      fields = _.without(fields, 'originalCourseStartDate')
    }

    if (obj.dependantsOnly) {
      fields = _.without(fields, 'accommodationFeesAlreadyPaid', 'tuitionFeesAlreadyPaid', 'totalTuitionFees')
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

    if (tier.dependantsOnlyOption) {
      var opt = _.findWhere(dependantsOnlyOptions.options, {value: (obj.dependantsOnly) ? 'yes' : 'no'})
      criteria.dependantsOnly = {
        label: 'Dependant/Main applicant',
        display: opt.label
      }
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

      if (f === 'accommodationFeesAlreadyPaid' && capped && capped.accommodationFeesPaid) {
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

    var plain = ''
    var results = me.getResults(obj)
    var criteria = angular.merge(me.getCriteria(obj), me.getConsentCriteria(obj))
    var passed = FsBankService.passed(obj)
    if (passed) {
      plain += 'PASSED\n\n'
    } else if (passed === false) {
      plain += 'NOT PASSED\n\n'
    }

    plain += '## Results ##\n'
    _.each(results, function (r, k) {
      plain += lineLength(r.label, 40) + r.display + '\n'
    })

    plain += '\n\n## Your calculation ##\n'
    _.each(criteria, function (c, k) {
      c.display = (k === 'accountNumber') ? 'XXXX' + c.display.substr(4) : c.display
      plain += lineLength(c.label, 40) + c.display + '\n'
    })

    return plain
  }

  this.track = function (category, action, label) {
    if (label) {
      ga('send', 'event', category, action, label)
    } else {
      ga('send', 'event', category, action)
    }
  }

  me.reset()

  return me
}])
