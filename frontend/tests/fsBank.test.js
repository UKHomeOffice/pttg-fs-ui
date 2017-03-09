/* global describe it beforeEach inject _ expect moment */

describe('app: hod.proving', function () {
  beforeEach(module('hod.proving'))
  beforeEach(module('hod.fs'))
  beforeEach(module('hod.io'))

  describe('FsBankService', function () {
    var fsb
    beforeEach(inject(function (FsBankService) {
      fsb = FsBankService
    }))

    describe('hasBankInfo', function () {
      var testObj = {
        doCheck: true,
        sortCode: '123456',
        accountNumber: '12345678',
        dob: '1974-05-13'
      }
      it('should return true when all properties are OK', function () {
        expect(fsb.hasBankInfo(testObj)).toBeTruthy()
      })

      it('should return false when doCheck is anything but true', function () {
        _.each(['Yes', 'yEs', 'no', 'yes', '', 0, null], function (str) {
          testObj.doCheck = str
          expect(fsb.hasBankInfo(testObj)).toBeFalsy()
        })
      })

      it('should return false when sortCode fails very basic validation', function () {
        testObj.doCheck = 'yes'
        _.each(['', '01234x', 'abcdef', '0123456', 0, null], function (str) {
          testObj.sortCode = str
          expect(fsb.hasBankInfo(testObj)).toBeFalsy()
        })
      })

      it('should return false when accountNumber fails very basic validation', function () {
        testObj.sortCode = '123456'
        _.each(['', '01234x', 'abcdef', '012345678', 0, null], function (str) {
          testObj.accountNumber = str
          expect(fsb.hasBankInfo(testObj)).toBeFalsy()
        })
      })
    })

    describe('getDailyBalanceStatusUrl', function () {
      var testObj = {}
      it('should NOT return anything when basic data is missing', function () {
        expect(fsb.getDailyBalanceStatusUrl(testObj)).toEqual(null)
        testObj.tier = 4
        expect(fsb.getDailyBalanceStatusUrl(testObj)).toEqual(null)
        testObj.accountNumber = '12345678'
        expect(fsb.getDailyBalanceStatusUrl(testObj)).toEqual(null)
      })
      it('should return getDailyBalanceStatusUrl when v basic data is present', function () {
        testObj.sortCode = '123456'
        expect(fsb.getDailyBalanceStatusUrl(testObj)).toEqual('t4/accounts/123456/12345678/dailybalancestatus')
      })
    })
  })
})
