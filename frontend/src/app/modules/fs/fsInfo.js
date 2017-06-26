/* global angular _ moment ga */

/* jshint node: true */

'use strict'

var fsModule = angular.module('hod.fs')

fsModule.factory('FsInfoService', [ function () {
  var me = this

  me.text = {
    passed: 'Passed',
    passedReason: 'This applicant meets the financial requirements',
    notPassed: 'Not passed', // 'Financial status not met',
    notPassedReason: 'One or more daily closing balances are below the total funds required',
    checkName: 'Check that the account holder name matches the applicant\'s.',
    copyToCid: 'Copy the information into CID',
    checkPaper: 'Check paper evidence to see if the applicant can meet the criteria in some other way.',
    manualCheck: 'Manually check the applicants evidence to make sure they have the total funds required.',
    checkDataEntry: 'Check you have entered the correct information.',
    consentGiven: 'Consent given',
    consentGivenReason: 'Applicant has given permission to access their account',
    consentRequested: 'Consent requested',
    consentRequestedReason: 'Awaiting response from applicant',
    consentDenied: 'Consent not given',
    consentDeniedReason: 'Applicant has refused permission to access their account',
    inaccessibleaccount: 'Invalid or inaccessible account',
    conditionspreventedus: 'One or more of the following conditions prevented us from accessing the account:',
    datamismatch: 'the account number, sort code and date of birth do not match a Barclays account',
    notbarclays: 'it is not a Barclays account',
    frozen: 'it is frozen',
    businessacc: 'it is a business account',
    accountclosed: 'the account is closed',
    notEnoughRecords: 'The records for this account does not cover the whole {{ nDaysRequired }} day period',
    notnow: 'You can’t use this service just now. The problem will be fixed as soon as possible',
    trylater: 'Please try again later.'
  }

  // get a specific tier based on it's tier number
  this.getTier = function (t) {
    var tier = _.findWhere(this.getTiers(), {tier: t})
    if (tier) {
      return tier
    }
    return null
  }

  // get a variant given the tier number and variant value eg t=4, v=general
  this.getVariant = function (t, v) {
    var tier = me.getTier(t)
    if (!tier) {
      return null
    }
    return _.findWhere(tier.variants, {value: v})
  }

  // get the available tiers and assoc info
  this.getTiers = function () {
    return [
      {
        tier: 4,
        label: 'Tier 4',
        nDaysRequired: 28,
        dependantsOnlyOption: true,
        variantTypesLabel: 'Student type',
        variants: [{
          value: 'general',
          label: 'General student',
          full: 'Tier 4 (General) student',
          fields: ['*default', '*t4all', 'dependants', '*courses', 'courseType', 'courseInstitution', 'tuitionFees', 'tuitionFeesPaid']
        },
        {
          value: 'des',
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
          value: 'suso',
          label: 'Student union sabbatical officer',
          full: 'Tier 4 (General) student union (sabbatical officer)',
          fields: ['*default', '*courses', '*t4all', 'dependants']
        }]
      },
      {
        tier: 2,
        label: 'Tier 2',
        nDaysRequired: 90,
        defaultFields: ['*default', 'dependants'],
        variants: [],
        dependantsOnly: true
      },
      {
        tier: 5,
        label: 'Tier 5',
        nDaysRequired: 90,
        variants: [{
          value: 'temp',
          label: 'Temporary Worker',
          full: 'Temporary Worker (with & without dependants)',
          fields: ['*default', 'dependants'],
          dependantsOnlyOption: true
        },
        {
          value: 'youth',
          label: 'Youth Mobility Scheme',
          full: 'Youth Mobility Scheme',
          fields: ['*default'],
          dependantsOnlyOption: false
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
        return ['inLondon', 'accommodationFeesPaid']
      case '*bank':
        return ['sortCode', 'accountNumber', 'dob']
    }

    return []
  }

  this.getFieldsForObject = function (obj) {
    var tier = me.getTier(obj.tier)
    var v = me.getVariant(obj.tier, obj.variantType)
    var fields = (_.has(v, 'fields')) ? v.fields : tier.defaultFields

    // add the bank fields
    if (obj.doCheck) {
      fields.push('*bank')
    }

    fields = me.getFields(fields)

    if (obj.dependantsOnly) {
      // these fields should be excluded on a dependant only route
      fields = _.without(fields, 'accommodationFeesPaid', 'tuitionFeesPaid', 'tuitionFees')
    }

    if (obj.continuationCourse !== 'yes') {
      fields = _.without(fields, 'originalCourseStartDate')
    }

    return fields
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
        options: [
          { value: 'pre-sessional', label: 'Pre-sessional' },
          { value: 'main', label: 'Main course degree or higher' },
          { value: 'below-degree', label: 'Main course below degree' }
        ],
        format: 'radio'
      },
      courseInstitution: {
        summary: 'Course institution',
        options: [
          { value: 'true', label: 'Recognised body or HEI' },
          { value: 'false', label: 'Other institution' }
        ],
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
        summary: 'Continuation course',
        options: [{ value: 'yes', label: 'Yes' }, { value: 'no', label: 'No' }],
        format: 'radio'
      },
      originalCourseStartDate: {
        summary: 'Original course start date',
        format: 'date'
      },
      tuitionFeesPaid: {
        summary: 'Tuition fees already paid',
        format: 'pounds'
      },
      tuitionFees: {
        summary: 'Total tuition fees for the first year',
        format: 'pounds'
      },
      accommodationFeesPaid: {
        summary: 'Accommodation fees already paid',
        format: 'pounds'
      },
      // doCheck: {
      //   summary: '',
      //   options: [{ value: 'yes', label: 'Yes, check Barclays' }, { value: 'no', label: 'No' }]
      // },
      dependantsOnly: {
        summary: '',
        options: [{ value: 'main', label: 'Main applicant (with & without dependants)' }, { value: 'dependant', label: 'Dependants only' }]
      },
      accountNumber: {
        summary: 'Account number'
      },
      sortCode: {
        summary: 'Sort code'
      },
      dob: {
        summary: 'Date of birth'
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

  this.variantFirst = function (tier) {
    return !_.every(tier.variants, function (v) {
      return (v.fields.indexOf('dependants') >= 0)
    })
  }

  return this
}])
