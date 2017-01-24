/* global describe it beforeEach inject _ expect */

describe('app: hod.proving', function () {
  beforeEach(module('hod.proving'))
  beforeEach(module('hod.financialstatus'))
  beforeEach(module('hod.io'))

  describe('FinancialStatus', function () {
    var fs
    beforeEach(inject(function (FinancialstatusService) {
      fs = FinancialstatusService
    }))

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
        expect(Math.ceil(months)).toEqual(data.result)
      })
    })
  })
})
