/* global angular moment _ ga */

'use strict'

var fsModule = angular.module('hod.fs')

// #### ROUTES #### //
fsModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'fsDetails',
    url: '/:calcOrBank/details',
    title: 'Financial Status : Details',
    parent: 'fsDoCheck',
    views: {
      'content@': {
        templateUrl: 'modules/fs/templates/fsDetails.html',
        controller: 'FsDetailsCtrl'
      }
    }
  })
}])

fsModule.run(['$rootScope', '$state', 'FsService', function ($rootScope, $state, FsService) {
  $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
    var fs = FsService.getApplication()
    if (toState.name === 'fsDetails' && toParams.calcOrBank === 'bank' && !FsService.hasBankInfo(fs)) {
      // you cannot be on the 'fsDetails' route/view if the bank details are not complete
      event.preventDefault()
      $state.go('fsGetConsent', toParams)
      return false
    }
  })
}])

fsModule.controller('FsDetailsCtrl', ['$scope', '$state', 'FsService', 'FsInfoService', function ($scope, $state, FsService, FsInfoService) {
  var fs = FsService.getApplication()
  $scope.fs = fs

  // force application values from the url params
  FsService.setKnownParamsFromState(fs, $state.params)

  console.log(fs)

  // determine fields to show
  $scope.tier = FsInfoService.getTier(fs.tier)
  $scope.variant = _.findWhere($scope.tier.variants, { value: $scope.fs.applicantType })
  $scope.fields = FsInfoService.getFields($scope.variant.fields)

  // config for all fields
  $scope.conf = {
    applicationRaisedDate: {
      max: moment().format('YYYY-MM-DD')
    },
    endDate: {
      validate: function (v, sc) {
        var fs = FsService.getApplication()
        var aDate = moment(fs.applicationRaisedDate, 'YYYY-MM-DD', true)
        var eDate = moment(fs.endDate, 'YYYY-MM-DD', true)
        var err = { summary: 'The end date is invalid', msg: 'Enter a valid end date' }
        if (!eDate.isValid()) {
            // must be a valid date
          return err
        }

        if (eDate.isAfter(moment.today)) {
            // cannot be in the future
          return err
        }

        if (!aDate.isValid()) {
            // if application raised date is invalid then no more checks can be done
            // as further checks require comparison between end data and application raised date
          return true
        }

        if (eDate.isAfter(aDate)) {
            // end date cannot be after the application raised date
          return err
        }

        if (eDate.isBefore(aDate.subtract(30, 'days'))) {
            // end date cannot be earlier than 31 days prior
            // to the application raised date
          return err
        }

        return true
      }
    },
    inLondon: FsInfoService.getFieldInfo('inLondon'),
    courseType: FsInfoService.getFieldInfo('courseType'),
    continuationCourse: angular.extend(FsInfoService.getFieldInfo('continuationCourse'), {
      onClick: function (opt, scope) {
        if (opt.value !== 'yes') {
          var fs = FsService.getApplication()
          fs.originalCourseStartDate = ''
        }
      }
    }),
    courseStartDate: {},
    courseEndDate: {
      validate: function (v, sc) {
        var fs = FsService.getApplication()
        var start = moment(fs.courseStartDate, 'YYYY-MM-DD', true)
        var endDateMom = moment(v, 'YYYY-MM-DD', true)
        if (!endDateMom.isValid()) {
          return { summary: 'The end date of course is invalid', msg: 'Enter a valid end date of course' }
        }
        if (!start.isBefore(endDateMom)) {
          return { summary: 'The end date of course is invalid', msg: 'Enter a valid course length' }
        }
        return true
      }
    },
    originalCourseStartDate: {
      required: false,
      validate: function (v, sc) {
        var fs = FsService.getApplication()
        var start = moment(fs.courseStartDate, 'YYYY-MM-DD', true)
        var contOriginalDateMom = moment(v, 'YYYY-MM-DD', true)
        if (fs.continuationCourse !== 'yes') {
            // not relevant as the course is not a continuation
          return true
        }

        if (contOriginalDateMom.isBefore(start)) {
          return true
        }

        return { summary: 'The original course start date is invalid', msg: 'Enter a valid original course start date' }
      }
    },
    totalTuitionFees: {
      prefix: '£ '
    },
    tuitionFeesAlreadyPaid: {
      prefix: '£ '
    },
    accommodationFeesAlreadyPaid: {
      prefix: '£ '
    },
    dependants: {
      classes: { 'form-control-1-8': true },
      validate: function (v, s) {
        var ok = true
        var n = Number(v)
        if (n < 0 || n > 99) {
          ok = false
        }

        if (v && v.length === 0) {
          ok = false
        }

        if (Math.ceil(n) !== Math.floor(n)) {
          ok = false
        }

        if (ok) {
          return true
        }

        return {
          summary: 'The number of dependants is invalid',
          msg: 'Enter a valid number of dependants'
        }
      }
    }
  }

  // set all fields to hidden
  _.each($scope.conf, function (f) {
    f.hidden = true
  })

  // the fields listed as required for this route should NOT be hidden
  _.each($scope.fields, function (f) {
    $scope.conf[f].hidden = false
  })

  $scope.submit = function (valid) {
    console.log('Valid', valid, $state)
    if (valid) {
      fs.thresholdResponse = {}
      FsService.sendThresholdRequest(fs).then(function (data) {
        data.responseTime = moment()
        fs.thresholdResponse = data

        $state.go('fsResult', $state.params)
      }, function (err) {
        console.log('err', err)
      })
    }
  }
}])
