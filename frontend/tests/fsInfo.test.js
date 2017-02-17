/* global describe it beforeEach inject _ expect */

describe('app: hod.proving', function () {
  beforeEach(module('hod.proving'))
  beforeEach(module('hod.fs'))
  beforeEach(module('hod.io'))

  describe('FsInfoService', function () {
    var fsi
    var tiers
    beforeEach(inject(function (FsInfoService) {
      fsi = FsInfoService
      tiers = fsi.getTiers()
    }))

    describe('getTiers() get all tiers', function () {
      it('should return a list of 3 tiers', function () {
        expect(tiers.length).toEqual(3)
      })

      describe('tier properties', function () {
        it('each tier shoud have properties tier, label and variants', function () {
          _.each(tiers, function (t) {
            expect(_.keys(t).length).toEqual(4)
            expect(_.has(t, 'label')).toBeTruthy()
            expect(_.has(t, 'tier')).toBeTruthy()
            expect(_.has(t, 'variants')).toBeTruthy()
            expect(_.has(t, 'nDaysRequired')).toBeTruthy()
          })
        })
      })
    })

    describe('getTier(t) get a specific tier by its number', function () {
      it('should return a tier object with the correct number', function () {
        var testCases = [
          { tier: 1, type: null },
          { tier: 2, type: 'object' },
          { tier: 3, type: null },
          { tier: 4, type: 'object' },
          { tier: 5, type: 'object' },
          { tier: 6, type: null }
        ]

        _.each(testCases, function (t) {
          var tier = fsi.getTier(t.tier)
          if (t.type) {
            expect(_.isObject(tier)).toBeTruthy()
            expect(_.has(tier, 'tier')).toBeTruthy()
            expect(tier.tier).toEqual(t.tier)
          } else {
            expect(_.isNull(tier)).toBeTruthy()
          }
        })
      })
    })

    describe('variant properties', function () {
      it('each variant should have properties value, label, full', function () {
        _.each(tiers, function (t) {
          _.each(t.variants, function (v) {
            expect(_.keys(v).length >= 4).toBeTruthy()
            expect(_.has(v, 'label')).toBeTruthy()
            expect(_.has(v, 'value')).toBeTruthy()
            expect(_.has(v, 'full')).toBeTruthy()
            expect(_.has(v, 'fields')).toBeTruthy()
          })
        })
      })
    })

    describe('getFieldGroup', function () {
      it('should have 3 fields in *default', function () {
        var fields = fsi.getFieldGroup('*default')
        expect(fields.length).toEqual(2)
        expect(fields.join()).toEqual('applicationRaisedDate,endDate')
      })

      it('should have 4 fields in *courses', function () {
        var fields = fsi.getFieldGroup('*courses')
        expect(fields.length).toEqual(4)
        expect(fields.join()).toEqual('courseStartDate,courseEndDate,continuationCourse,originalCourseStartDate')
      })

      it('should have 2 fields in *t4all', function () {
        var fields = fsi.getFieldGroup('*t4all')
        expect(fields.length).toEqual(2)
        expect(fields.join()).toEqual('inLondon,accommodationFeesAlreadyPaid')
      })
    })

    describe('getFields', function () {
      it('should return the actual fields when given a field group name/s', function () {
        expect(fsi.getFields(['*default']).length).toEqual(2)
        expect(fsi.getFields(['*default', '*courses']).length).toEqual(6)
        expect(fsi.getFields(['*default', '*courses', '*t4all']).length).toEqual(8)
      })

      it('should return resolved field groups and individual fields', function () {
        expect(fsi.getFields(['*courses', 'courseType', 'tutitionFeesAlreadyPaid']).length).toEqual(6)
        expect(fsi.getFields(['fielda', '*t4all', 'fieldb', 'fieldc']).length).toEqual(5)
      })

      it('should not change anything if no field groups are present', function () {
        expect(fsi.getFields(['A', 'B', 'C']).length).toEqual(3)
        expect(fsi.getFields(['A', 'B', 'C', 'X', 'Y', 'Z']).join('')).toEqual('ABCXYZ')
      })
    })

    describe('getAllFieldInfo', function () {
      it('should return a list of all available fields', function () {
        var fieldInfo = fsi.getAllFieldInfo()
        expect(_.keys(fieldInfo).length).toEqual(14)
        expect(_.has(fieldInfo, 'applicationRaisedDate')).toBeTruthy()
      })
    })

    describe('getFieldInfo', function () {
      it('should return info about a specific field', function () {
        var info = fsi.getFieldInfo('inLondon')
        expect(_.has(info, 'summary')).toBeTruthy()
        expect(_.has(info, 'options')).toBeTruthy()
      })
    })

    describe('t', function () {
      it('should return a string of text when given a ref', function () {
        expect(fsi.t('passed')).toEqual('Passed')
        expect(fsi.t('notPassed')).toEqual('Not passed')
      })
    })
  })
})
