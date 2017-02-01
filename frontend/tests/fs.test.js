/* global describe it beforeEach inject _ expect moment */

describe('app: hod.proving', function () {
  beforeEach(module('hod.proving'))
  beforeEach(module('hod.financialstatus'))
  beforeEach(module('hod.io'))

  describe('FsService', function () {
    var fs
    beforeEach(inject(function (FsService) {
      fs = FsService
    }))

    describe('hasResultInfo', function () {
      var testObj = {}
      it('should return false when no result info is available', function () {
        expect(fs.hasResultInfo(testObj)).toBeFalsy()
      })

      it('should return false when result info does not have calc', function () {
        testObj.result = {}
        expect(fs.hasResultInfo(testObj)).toBeFalsy()
      })

      it('should return true when result properties are present', function () {
        testObj.thresholdResponse = {}
        expect(fs.hasResultInfo(testObj)).toBeTruthy()
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
      var testObj = { tier: 4, applicantType: 'nondoctorate' }
      it('should return the appropriate parameters to send in a threshold request', function () {
        var result = fs.getThresholdParams(testObj)
        expect(_.has(result, 'studentType')).toBeTruthy()
      })
    })

    describe('getCriteria', function () {
      var testObj = {
        tier: 4,
        applicantType: 'nondoctorate',
        endDate: '2016-05-13',
        continuationCourse: 'yes',
        courseType: 'main',
        originalCourseStartDate: '2014-04-01'

      }
      it('should return an object with field labels and display values', function () {
        var criteria = fs.getCriteria(testObj)
        console.log(criteria)
        expect(_.has(criteria, 'endDate')).toBeTruthy()
        expect(_.has(criteria, 'continuationCourse')).toBeTruthy()
        expect(_.has(criteria, 'courseType')).toBeTruthy()
        expect(_.has(criteria, 'originalCourseStartDate')).toBeTruthy()

        expect(criteria.endDate.display).toEqual('13 May 2016')
        expect(criteria.originalCourseStartDate.display).toEqual('01 April 2014')
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
        expect(_.has(result, 'threshold')).toBeTruthy()
        expect(result.threshold.display).toEqual('£12,345.00')
        expect(_.has(result, 'leaveEndDate')).toBeTruthy()
        expect(result.leaveEndDate.display).toEqual('13 May 2016')
        expect(_.has(result, 'responseTime')).toBeTruthy()
        expect(result.responseTime.display).toEqual('12:34:56 23 June 2017')
      })
    })

    describe('getPeriodChecked', function () {
      var testObj = {
        tier: 4,
        endDate: '2016-06-30'
      }
      it('shoudld return a string with a date range nDays before the end date', function () {
        expect(fs.getPeriodChecked(testObj)).toEqual('03 June 2016 - 30 June 2016')
        testObj.tier = 2
        expect(fs.getPeriodChecked(testObj)).toEqual('02 April 2016 - 30 June 2016')
      })
    })
  })
})
