/* global angular _ moment ga */

/* jshint node: true */

'use strict'

var fsModule = angular.module('hod.fs', ['ui.router'])

fsModule.factory('FsService', ['$filter', 'FsInfoService', 'IOService', function ($filter, FsInfoService, IOService) {
  var me = this
  var _application

  this.reset = function () {
    _application = {
      doCheck: 'yes',
      dob: '1974-05-13',
      sortCode: '010616',
      accountNumber: '00030000',
      inLondon: 'no',
      courseType: 'main',
      continuationCourse: 'no',
      applicationRaisedDate: '2016-06-02',
      applicantType: 'nondoctorate',
      endDate: '2016-06-01',
      originalCourseStartDate: '',
      courseStartDate: '2016-06-01',
      courseEndDate: '2018-01-01',
      totalTuitionFees: '0',
      tuitionFeesAlreadyPaid: '0',
      accommodationFeesAlreadyPaid: '0',
      dependants: '0'
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
  }

  // does the object supplied (_application) have complete bank info?
  this.hasBankInfo = function (obj) {
    if (obj.doCheck !== 'yes') {
      // doCheck must be YES
      return false
    }

    if (!obj.sortCode || obj.sortCode.length !== 6 || _.isNaN(Number(obj.sortCode))) {
      // sortcode must be complete
      return false
    }

    if (!obj.accountNumber || obj.accountNumber.length !== 8) {
      // account number must be filled in
      return false
    }

    if (!obj.dob || obj.dob.length !== 10) {
      // dob must be completed
      return false
    }

    return true
  }

  // does the object (an _application) have result/calc properties
  this.hasResultInfo = function (obj) {
    if (!_.has(obj, 'thresholdResponse')) {
      return false
    }

    return true
  }

  // get daily balance status url
  this.getDailyBalanceStatusUrl = function (obj) {
    if (!obj.tier || !obj.sortCode || !obj.accountNumber) {
      return null
    }
    return 't' + obj.tier + '/accounts/' + obj.sortCode + '/' + obj.accountNumber + '/dailybalancestatus'
  }

  // get the consent url
  this.getConsentUrl = function (obj) {
    if (!obj.sortCode || !obj.accountNumber) {
      return null
    }
    return 'accounts/' + obj.sortCode + '/' + obj.accountNumber + '/consent'
  }

  // get the threshold url
  this.getThresholdUrl = function (obj) {
    if (!obj.tier) {
      return null
    }
    return 't' + obj.tier + '/threshold'
  }

  this.getConsentParams = function (obj) {
    return {dob: obj.dob}
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

  this.sendConsentRequest = function (obj) {
    var u = me.getConsentUrl(obj)
    var params = me.getConsentParams(obj)
    return IOService.get(u, params)
  }

  this.sendThresholdRequest = function (obj) {
    var u = me.getThresholdUrl(obj)
    var params = me.getThresholdParams(obj)
    return IOService.get(u, params)
  }

  this.getResults = function (obj) {
    var results = {}
    var tier = FsInfoService.getTier(obj.tier)

    if (obj.thresholdResponse && obj.thresholdResponse.data) {
      if (_.has(obj.thresholdResponse.data, 'threshold')) {
        results.threshold = {
          label: 'Total funds required',
          display: $filter('pounds')(obj.thresholdResponse.data.threshold)
        }
      }
    }

    results.periodChecked = {
      label: tier.nDaysRequired + '-day period to check',
      display: moment(obj.endDate).subtract(tier.nDaysRequired, 'days')
    }

    if (obj.thresholdResponse && obj.thresholdResponse.data) {
      if (_.has(obj.thresholdResponse.data, 'leaveEndDate')) {
        results.leaveEndDate = {
          label: 'Estimated leave period if passes',
          display: $filter('dateDisplay')(obj.thresholdResponse.data.leaveEndDate)
        }
      }
      if (_.has(obj.thresholdResponse, 'responseTime')) {
        results.responseTime = {
          label: 'Calculator result received',
          display: obj.thresholdResponse.responseTime.format('HH:mm:ss DD MMMM YYYY')
        }
      }
    }

    return results
  }

  this.getCriteria = function (obj) {
    // basics
    var tier = FsInfoService.getTier(obj.tier)
    var variant = _.findWhere(tier.variants, { value: obj.applicantType })
    var fields = FsInfoService.getFields(variant.fields)

    var criteria = {}
    _.each(fields, function (f) {
      var info = FsInfoService.getFieldInfo(f)
      var disp = ''
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

      criteria[f] = { label: info.summary.replace(/({{nDaysRequired}})/, tier.nDaysRequired), value: obj[f], display: disp }
    })

    return criteria
  }

  me.reset()

  return me
}])
