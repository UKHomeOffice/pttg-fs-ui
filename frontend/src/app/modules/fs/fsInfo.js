/* global angular _ moment ga */

/* jshint node: true */

'use strict'

var fsModule = angular.module('hod.fs')

fsModule.factory('FsInfoService', [ function () {
  var me = this

  me.text = {
    passed: 'Passed',
    passedReason: 'The applicant\'s daily closing balance was above the total funds required',
    notPassed: 'Not passed', // 'Financial status not met',
    notPassedReason: 'One or more daily closing balances are below the total funds required',
    checkName: 'Check that the account holder name matches the applicant\'s.',
    copyToCid: 'Copy the information into CID',
    checkPaper: 'Check paper evidence to see if the applicant can meet the criteria in some other way.',
    manualCheck: 'Manually check the applicants evidence to make sure they have the total funds required.',
    checkDataEntry: 'Check you have entered the correct information.',
    consentDenied: 'Consent has not been given',
    consentDeniedReason: 'The applicant has not given consent to check their financial status directly with their bank.',
    datamismatch: 'the account number, sort code and date of birth do not match a Barclays account',
    notbarclays: 'it is not a Barclays account',
    frozen: 'it is frozen',
    businessacc: 'it is a business account',
    accountclosed: 'the account is closed'
  }

  // get a specific tier based on it's tier number
  this.getTier = function (t) {
    var tier = _.findWhere(this.getTiers(), {tier: t})
    if (tier) {
      return tier
    }
    return null
  }

  // get a variant given the tier number and variant value eg t=4, v=main
  this.getVariant = function (t, v) {
    var tier = me.getTier(t)
    if (!tier) {
      return null
    }
    return _.findWhere(tier.variants, {value: v})
  }

  // get the available tiers and assoc info
  this.getTiers = function () {
    return [{
      tier: 2,
      label: 'Tier 2',
      nDaysRequired: 90,
      variants: [{
        value: 'main',
        label: 'Main applicant (with & without dependants)',
        full: 'Main applicant (with & without dependants)',
        fields: ['*default']
      },
      {
        value: 'dependant',
        label: 'Dependant only',
        full: 'Dependant only',
        fields: ['*default']
      }]
    },
    {
      tier: 4,
      label: 'Tier 4 (General)',
      nDaysRequired: 28,
      variants: [{
        value: 'nondoctorate',
        label: 'General student',
        full: 'Tier 4 (General) student',
        fields: ['*default', '*t4all', 'dependants', '*courses', 'courseType', 'totalTuitionFees', 'tuitionFeesAlreadyPaid']
      },
      {
        value: 'doctorate',
        label: 'Doctorate extension scheme',
        full: 'Tier 4 (General) student (doctorate extension scheme)',
        fields: ['*default', '*t4all', 'dependants']
      },
      {
        value: 'pgdd',
        label: 'Postgraduate doctor or dentist',
        full: 'Tier 4 (General) student (postgraduate doctor or dentist)',
        fields: ['*default', '*courses', '*t4all', 'dependants']
      },
      {
        value: 'sso',
        label: 'Student union sabbatical officer',
        full: 'Tier 4 (General) student union (sabbatical officer)',
        fields: ['*default', '*courses', '*t4all', 'dependants']
      }]
    },
    {
      tier: 5,
      label: 'Tier 5',
      nDaysRequired: 90,
      variants: [{
        value: 'main',
        label: 'Main applicant (with & without dependants)',
        full: 'Main applicant (with & without dependants)',
        fields: ['*default']
      },
      {
        value: 'dependant',
        label: 'Dependant only',
        full: 'Dependant only',
        fields: ['*default']
      }]
    }]
  }

  // given a field group name return the individual fields
  this.getFieldGroup = function (groupName) {
    switch (groupName) {
      case '*default':
        // all routes have these fields
        return ['applicationRaisedDate', 'endDate']
      case '*courses':
        // the t4 student routes need course start and end dates
        return ['courseStartDate', 'courseEndDate', 'continuationCourse', 'originalCourseStartDate']
      case '*t4all':
        // common fields for t4
        return ['inLondon', 'accommodationFeesAlreadyPaid']
    }

    return []
  }

  // given a list of fields - resolve field group name for the actual fields
  this.getFields = function (fields) {
    var fieldList = []
    _.each(fields, function (f) {
      if (f.substr(0, 1) === '*') {
        // this is a group
        fieldList = fieldList.concat(me.getFieldGroup(f))
      } else {
        // individual fields
        fieldList.push(f)
      }
    })
    return fieldList
  }

  this.getAllFieldInfo = function () {
    var fieldInfo = {
      applicationRaisedDate: {
        summary: 'Application raised date',
        format: 'date'
      },
      endDate: {
        summary: 'Last date of the {{nDaysRequired}}-day period to check',
        format: 'date'
      },
      dependants: {
        summary: 'Number of dependants'
      },
      inLondon: {
        summary: 'In London',
        options: [{ value: 'yes', label: 'Yes' }, { value: 'no', label: 'No' }],
        format: 'radio'
      },
      courseType: {
        summary: 'Course type',
        options: [{ value: 'pre-sessional', label: 'Pre-sessional' }, { value: 'main', label: 'Main course' }],
        format: 'radio'
      },
      courseStartDate: {
        summary: 'Course start date',
        format: 'date'
      },
      courseEndDate: {
        summary: 'Course end date',
        format: 'date'
      },
      continuationCourse: {
        summary: 'Is the course a continuation?',
        options: [{ value: 'yes', label: 'Yes' }, { value: 'no', label: 'No' }],
        format: 'radio'
      },
      originalCourseStartDate: {
        summary: 'Original course start date',
        format: 'date'
      },
      tuitionFeesAlreadyPaid: {
        summary: 'Tuition fees already paid',
        format: 'pounds'
      },
      totalTuitionFees: {
        summary: 'Total tuition fees for the first year',
        format: 'pounds'
      },
      accommodationFeesAlreadyPaid: {
        summary: 'Accommodation fees already paid',
        format: 'pounds'
      },
      doCheck: {
        summary: '',
        options: [{ value: 'yes', label: 'Yes, check Barclays' }, { value: 'no', label: 'No' }]
      }
    }

    return fieldInfo
  }

  this.getFieldInfo = function (f) {
    var fieldInfo = me.getAllFieldInfo()
    return fieldInfo[f] || null
  }

  this.t = function (ref) {
    if (_.has(me.text, ref)) {
      return me.text[ref]
    }
    return ''
  }

  return this
}])
