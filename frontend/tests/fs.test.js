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

    describe('hasBankInfo', function () {
      var testObj = {
        doCheck: 'yes',
        sortCode: '123456',
        accountNumber: '12345678',
        dob: '1974-05-13'
      }
      it('should return true when all properties are OK', function () {
        expect(fs.hasBankInfo(testObj)).toBeTruthy()
      })

      it('should return false when doCheck is anything but yes', function () {
        _.each(['Yes', 'yEs', 'no', '', 0, null], function (str) {
          testObj.doCheck = str
          expect(fs.hasBankInfo(testObj)).toBeFalsy()
        })
      })

      it('should return false when sortCode fails very basic validation', function () {
        testObj.doCheck = 'yes'
        _.each(['', '01234x', 'abcdef', '0123456', 0, null], function (str) {
          testObj.sortCode = str
          expect(fs.hasBankInfo(testObj)).toBeFalsy()
        })
      })

      it('should return false when accountNumber fails very basic validation', function () {
        testObj.sortCode = '123456'
        _.each(['', '01234x', 'abcdef', '012345678', 0, null], function (str) {
          testObj.accountNumber = str
          expect(fs.hasBankInfo(testObj)).toBeFalsy()
        })
      })
    })

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

    describe('getDailyBalanceStatusUrl', function () {
      var testObj = {}
      it('should NOT return anything when basic data is missing', function () {
        expect(fs.getDailyBalanceStatusUrl(testObj)).toEqual(null)
        testObj.tier = 4
        expect(fs.getDailyBalanceStatusUrl(testObj)).toEqual(null)
        testObj.accountNumber = '12345678'
        expect(fs.getDailyBalanceStatusUrl(testObj)).toEqual(null)
      })
      it('should return getDailyBalanceStatusUrl when v basic data is present', function () {
        testObj.sortCode = '123456'
        expect(fs.getDailyBalanceStatusUrl(testObj)).toEqual('t4/accounts/123456/12345678/dailybalancestatus')
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

    describe('getResults', function () {
      var testObj = {
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
  })
})
