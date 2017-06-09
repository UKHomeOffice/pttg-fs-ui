/* global angular _ moment ga */

/* jshint node: true */

'use strict'

var fsModule = angular.module('hod.fs', ['ui.router'])

fsModule.factory('FsService', ['$filter', 'FsInfoService', 'FsBankService', 'IOService', 'CONFIG', function ($filter, FsInfoService, FsBankService, IOService, CONFIG) {
  var me = this
  var _application

  // reset all the current application's values back to their defaults
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
      tuitionFees: '',
      tuitionFeesPaid: '',
      accommodationFeesPaid: '',
      dependants: '',
      dependantsOnly: null
    }
  }

  // get the form details
  this.getApplication = function () {
    return _application
  }

  /* using the angular router state parameters set some primary values in the application object
  e.g. it should be possible to determine from the url structure alone:
  - tier
  - applicantType
  - dependantOnly
  - wether or not a bank check is required
  */
  this.setKnownParamsFromState = function (obj, stateParams) {
    obj.tier = Number(stateParams.tier) // tier 4

    obj.doCheck = (stateParams.statusOrCalc === 'status')

    obj.applicantType = (stateParams.applicantType === 'dependant') ? 'dependant' : 'main'
    obj.dependantsOnly = (stateParams.applicantType === 'dependant')

    var v = FsInfoService.getVariant(obj.tier, stateParams.variantType)
    obj.variantType = (v) ? stateParams.variantType : null

    if (!obj.doCheck) {
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

  // clear any previous response to the threshold request
  this.clearThresholdResponse = function (obj) {
    if (_.has(obj, 'thresholdResponse')) {
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
    var fields = FsInfoService.getFieldsForObject(obj)
    var params = {dependants: 0}
    _.each(fields, function (f) {
      params[f] = obj[f]
    })

    params.dependantsOnly = obj.dependantsOnly

    // [TODO]
    if (obj.variantType) {
      params.studentType = obj.variantType
      params.applicantType = obj.variantType
    } else {
      params.studentType = obj.applicantType
      params.applicantType = obj.applicantType
    }

    if (obj.dependantsOnly) {
      params.accommodationFeesPaid = 0
      params.tuitionFees = 0
      params.tuitionFeesPaid = 0
    }

    // [TODO] remove these they are only to support the old JAVA based UI-API
    params.accommodationFeesAlreadyPaid = params.accommodationFeesPaid
    params.totalTuitionFees = params.tuitionFees
    params.tuitionFeesAlreadyPaid = params.tuitionFeesPaid

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

    if (FsBankService.hasResult(obj)) {
      results.maintenancePeriodChecked = {
        label: tier.nDaysRequired + '-day period checked',
        display: me.getPeriodChecked(obj)
      }
    }

    if (FsBankService.hasResult(obj) && !FsBankService.passed(obj) && obj.dailyBalanceResponse.data.failureReason.lowestBalanceValue) {
      results.lowestBalance = {
        label: 'Lowest balance',
        display: $filter('pounds')(obj.dailyBalanceResponse.data.failureReason.lowestBalanceValue) + ' on ' + $filter('dateDisplay')(obj.dailyBalanceResponse.data.failureReason.lowestBalanceDate)
      }
    }

    if (me.hasConditionCodeInfo(obj)) {
      var codes = []
      var cc = obj.conditionCodeResponse.data

      if (cc.applicantConditionCode) {
        codes.push(cc.applicantConditionCode + ' - Applicant')
      }
      if (cc.partnerConditionCode) {
        codes.push(cc.partnerConditionCode + ' - Adult dependant')
      }
      if (cc.childConditionCode) {
        codes.push(cc.childConditionCode + ' - Child dependant')
      }
      results.conditionCode = {
        label: 'Condition code',
        display: codes.join('\n')
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

  // get the capped values from the threshold response, eg capped months for course length when used in the calculation
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
    var variant = FsInfoService.getVariant(obj.tier, obj.variantType)

    var fields = FsInfoService.getFieldsForObject(obj)
    var capped = me.getThresholdCappedValues(obj)
    var dependantsOnlyOptions = FsInfoService.getFieldInfo('dependantsOnly')

    fields = _.without(fields, 'courseEndDate', 'endDate')

    var criteria = {}

    criteria.tier = {
      label: 'Tier',
      display: tier.label
    }

    var opt = _.findWhere(dependantsOnlyOptions.options, {value: (obj.dependantsOnly) ? 'dependant' : 'main'})
    criteria.applicantType = {
      label: 'Applicant type',
      display: opt.label
    }

    if (obj.variantType) {
      criteria.studentType = {
        label: 'Student type',
        display: variant.label
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

      if (f === 'accommodationFeesPaid' && capped && capped.accommodationFeesPaid) {
        disp += ' (limited to ' + $filter('pounds')(capped.accommodationFeesPaid) + ')'
      }

      criteria[f] = { label: lab, display: disp }
    })

    criteria = angular.merge(criteria, me.getConsentCriteria(obj))

    return criteria
  }

  // get the bank account specific criteria
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

  // what should a caseworker do next?
  this.getThingsToDoNext = function (obj) {
    var doNext = []
    if (!obj.doCheck) {
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

  // get the duration in months between two dates
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

  // get the course length from the obj courseStartDate and courseEndDate
  this.getCourseLength = function (obj) {
    return me.getMonths(obj.courseStartDate, obj.courseEndDate)
  }

  // get the entire course length between the original courseStartDate and the courseEndDate
  this.getEntireCourseLength = function (obj) {
    return me.getMonths(obj.originalCourseStartDate, obj.courseEndDate)
  }

  // get the plain text required for the copy into paste buffer function
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

  this.clearConditionCode = function (fs) {
    fs.conditionCodeResponse = {}
  }

  this.hasConditionCodeInfo = function (fs) {
    return _.has(fs, 'conditionCodeResponse') && _.has(fs.conditionCodeResponse, 'data')
  }

  this.getConditionCodeUrl = function (obj) {
    if (!obj.tier === 4) {
      return null
    }

    return 't' + obj.tier + '/conditioncodes'
  }

  this.getConditionCodeParams = function (obj) {
    if (obj.variantType !== 'general') {
      return {
        studentType: obj.variantType,
        dependants: obj.dependants,
        dependantsOnly: obj.dependantsOnly
      }
    }

    var params = {
      studentType: obj.variantType,
      dependants: obj.dependants,
      dependantsOnly: obj.dependantsOnly,
      courseType: obj.courseType,
      recognisedBodyOrHEI: obj.courseInstitution
    }

    if (obj.courseEndDate) {
      params.courseEndDate = obj.courseEndDate
    }

    if (obj.courseStartDate) {
      params.courseStartDate = obj.courseStartDate
    }

    return params
  }

  this.sendConditionCodeRequest = function (obj) {
    var u = me.getConditionCodeUrl(obj)
    var params = me.getConditionCodeParams(obj)
    return IOService.get(u, params, { timeout: CONFIG.timeout })
  }

  me.reset()

  return me
}])
