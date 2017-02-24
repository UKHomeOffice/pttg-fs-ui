/* global describe it beforeEach inject _ expect moment */

describe('app: hod.proving', function () {
  beforeEach(module('hod.proving'))
  beforeEach(module('hod.fs'))
  beforeEach(module('hod.io'))

  describe('FsService', function () {
    var fs
    var fsi
    beforeEach(inject(function (FsService, FsInfoService) {
      fs = FsService
      fsi = FsInfoService
    }))

    describe('hasThresholdInfo', function () {
      var testObj = {}
      it('should return false when no result info is available', function () {
        expect(fs.hasThresholdInfo(testObj)).toBeFalsy()
      })

      it('should return false when result info does not have calc', function () {
        testObj.thresholdResponse = {}
        expect(fs.hasThresholdInfo(testObj)).toBeFalsy()
      })

      it('should return true when result properties are present', function () {
        testObj.thresholdResponse.data = {}
        expect(fs.hasThresholdInfo(testObj)).toBeTruthy()
      })
    })

    describe('getThresholdUrl', function () {
      it('should NOT return anything when tier is not defined', function () {
        expect(fs.getThresholdUrl({})).toEqual(null)
      })
      it('should return the url', function () {
        expect(fs.getThresholdUrl({tier: 2})).toEqual('t2/threshold')
      })
    })

    describe('getThresholdParams', function () {
      var testObj = { tier: 4, applicantType: 'general' }
      it('should return the appropriate parameters to send in a threshold request', function () {
        var result = fs.getThresholdParams(testObj)
        expect(_.has(result, 'applicantType')).toBeTruthy()
      })
    })

    describe('getCriteria', function () {
      var testObj = {
        tier: 4,
        applicantType: 'general',
        endDate: '2016-05-13',
        continuationCourse: 'yes',
        courseType: 'main',
        originalCourseStartDate: '2014-04-01'

      }
      it('should return an object with field labels and display values', function () {
        var criteria = fs.getCriteria(testObj)
        console.log(criteria)
        // expect(_.has(criteria, 'endDate')).toBeTruthy()
        expect(_.has(criteria, 'continuationCourse')).toBeTruthy()
        expect(_.has(criteria, 'courseType')).toBeTruthy()
        expect(_.has(criteria, 'originalCourseStartDate')).toBeTruthy()

        // expect(criteria.endDate.display).toEqual('13/05/2016')
        expect(criteria.originalCourseStartDate.display).toEqual('01/04/2014')
      })

      it('should not include originalCourseStartDate when continuationCourse is \'no\'', function () {
        testObj.continuationCourse = 'no'
        var criteria = fs.getCriteria(testObj)
        expect(_.has(criteria, 'originalCourseStartDate')).toBeFalsy()
      })
    })

    describe('getResults', function () {
      var testObj = {
        tier: 4,
        thresholdResponse: {
          responseTime: moment('2017-06-23T12:34:56'),
          data: {
            threshold: 12345,
            leaveEndDate: '2016-05-13'
          }
        }
      }
      it('should return the appropriate results', function () {
        var result = fs.getResults(testObj)
        expect(_.has(result, 'totalFundsRequired')).toBeTruthy()
        expect(result.totalFundsRequired.display).toEqual('£12,345.00')
        expect(_.has(result, 'estimatedLeaveEndDate')).toBeTruthy()
        expect(result.estimatedLeaveEndDate.display).toEqual('13/05/2016')
        expect(_.has(result, 'responseTime')).toBeTruthy()
        expect(result.responseTime.display).toEqual('12:34:56 23/06/2017')
      })
    })

    describe('getPeriodChecked', function () {
      var testObj = {
        tier: 4,
        endDate: '2016-06-30'
      }
      it('shoudld return a string with a date range nDays before the end date', function () {
        expect(fs.getPeriodChecked(testObj)).toEqual('03/06/2016 to 30/06/2016')
        testObj.tier = 2
        expect(fs.getPeriodChecked(testObj)).toEqual('02/04/2016 to 30/06/2016')
      })
    })

    describe('getThingsToDoNext', function () {
      var testObj = {
        doCheck: 'yes',
        dailyBalanceResponse: {
          data: {
            fundingRequirementMet: true
          }
        },
        consentResponse: {
          data: {
            consent: 'SUCCESS'
          }
        }
      }
      it('should say check account holder name & copy to CID when check passed', function () {
        var doNext = fs.getThingsToDoNext(testObj)
        expect(doNext[0]).toEqual(fsi.t('checkName'))
        expect(doNext[1]).toEqual(fsi.t('copyToCid'))
      })

      it('should say check account holder name /check paper & copy to CID when check failed', function () {
        testObj.dailyBalanceResponse.data.fundingRequirementMet = false
        var doNext = fs.getThingsToDoNext(testObj)
        expect(doNext[0]).toEqual(fsi.t('checkName'))
        expect(doNext[1]).toEqual(fsi.t('checkPaper'))
        expect(doNext[2]).toEqual(fsi.t('copyToCid'))
      })

      it('should say check entered, manually check and copy to CID when consent was denied', function () {
        testObj.consentResponse.data.consent = 'FAILURE'
        var doNext = fs.getThingsToDoNext(testObj)
        expect(doNext[0]).toEqual(fsi.t('checkDataEntry'))
        expect(doNext[1]).toEqual(fsi.t('manualCheck'))
        expect(doNext[2]).toEqual(fsi.t('copyToCid'))
      })

      it('should say manual check and copy to CID', function () {
        var doNext = fs.getThingsToDoNext({})
        expect(doNext[0]).toEqual(fsi.t('manualCheck'))
        expect(doNext[1]).toEqual(fsi.t('copyToCid'))
      })
    })

    describe('getMonths', function () {
      it('is expected to return the rounded up months calculations from date strings', function () {
        var testCases = [
          { start: '2016-01-01', end: '2016-01-01', result: 0 },
          { start: '2016-01-01', end: '2016-01-29', result: 1 },
          { start: '2016-01-01', end: '2016-02-29', result: 2 },
          { start: '2016-01-15', end: '2016-03-14', result: 2 },
          { start: '2016-01-15', end: '2016-03-15', result: 3 },
          { start: '2016-01-15', end: '2016-03-16', result: 3 },
          { start: '2016-01-15', end: '2016-04-14', result: 3 },
          { start: '2016-01-15', end: '2016-04-15', result: 4 }
        ]
        _.each(testCases, function (data) {
          var months = fs.getMonths(data.start, data.end)
          expect(months).toEqual(data.result)
        })
      })
    })

    describe('splitApplicantType', function () {
      it('is expected to correctly split the applicantType url parameter into applicantType and dependantsOnly boolean', function () {
        var testCases = [
          {str: null, result: { applicantType: null, dependantsOnly: false }},
          {str: 'general-dependants', result: { applicantType: 'general', dependantsOnly: true }},
          {str: 'sso-dependants', result: { applicantType: 'sso', dependantsOnly: true }},
          {str: 'general', result: { applicantType: 'general', dependantsOnly: false }},
          {str: 'general-dependantzzz', result: { applicantType: 'general', dependantsOnly: false }}
        ]

        _.each(testCases, function (data) {
          var split = fs.splitApplicantType(data.str)
          expect(split.applicantType).toEqual(data.result.applicantType)
          expect(split.dependantsOnly).toEqual(data.result.dependantsOnly)
        })
      })
    })

    describe('setKnownParamsFromState', function () {
      it('is expected to be able to set tier, variant, and dependantOnly values from the url state params', function () {
        // setKnownParamsFromState
        var testCases = [
          {source: { tier: 4, applicantType: 'general' }, result: { tier: 4, applicantType: 'general', dependantsOnly: false }},
          {source: { tier: 2, applicantType: 'main' }, result: { tier: 2, applicantType: 'main', dependantsOnly: false }},
          {source: { tier: 4, applicantType: 'general-dependants' }, result: { tier: 4, applicantType: 'general', dependantsOnly: true }},
          {source: { tier: 5, applicantType: 'dependant' }, result: { tier: 5, applicantType: 'dependant', dependantsOnly: true }}
        ]

        _.each(testCases, function (data) {
          var obj = {}
          fs.setKnownParamsFromState(obj, data.source)
          expect(obj.tier).toEqual(data.result.tier)
          expect(obj.applicantType).toEqual(data.result.applicantType)
          expect(obj.dependantsOnly).toEqual(data.result.dependantsOnly)
          expect(obj.doCheck).toEqual('no')
        })
      })

      it('should clear bank details when not a bank route', function () {
        var testParams = {
          tier: 4,
          applicantType: 'general',
          calcOrBank: 'bank'
        }
        var obj = {
          sortCode: '01-06-16',
          accountNumber: '00030000',
          doCheck: 'yes'
        }

        fs.setKnownParamsFromState(obj, testParams)
        expect(obj.doCheck).toEqual('yes')

        testParams.calcOrBank = 'calc'
        fs.setKnownParamsFromState(obj, testParams)
        expect(obj.doCheck).toEqual('no')
        expect(obj.sortCode).toEqual('')
        expect(obj.accountNumber).toEqual('')
        expect(obj.dob).toEqual('')
      })
    })
  })
})
