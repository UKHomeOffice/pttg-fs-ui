/* global angular _ moment */
/* jshint node: true */

'use strict'

var financialstatusModule = angular.module('hod.financialstatus')

// #### ROUTES #### //
financialstatusModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'financialStatusDetails',
    url: '/:applicantType',
    title: 'Financial Status : Query',
    parent: 'financialStatus',
    views: {
      'content@': {
        templateUrl: 'modules/financialstatus/financialstatusDetails.html',
        controller: 'FinancialstatusDetailsCtrl'
      }
    }
  })

  $stateProvider.state({
    name: 'financialStatusCalcDetails',
    url: '/:applicantType',
    title: 'Financial Status : Query',
    parent: 'financialStatusCalc',
    views: {
      'content@': {
        templateUrl: 'modules/financialstatus/financialstatusDetails.html',
        controller: 'FinancialstatusDetailsCtrl'
      }
    }
  })
}])

// fill in the details of the form
financialstatusModule.controller(
'FinancialstatusDetailsCtrl', ['$rootScope', '$scope', '$state', '$stateParams', 'FinancialstatusService', 'IOService', '$window', '$timeout',
  function ($rootScope, $scope, $state, $stateParams, FinancialstatusService, IOService, $window, $timeout) {
    var sType = _.findWhere(FinancialstatusService.getApplicantTypes(), {value: $stateParams.applicantType})
    $scope.isCalc = FinancialstatusService.isCalc()
    if (!sType) {
    // this is not a valid student type option - abort!
      $state.go(($scope.isCalc) ? 'financialStatusCalc' : 'financialStatus')
      return
    }

    $scope.tier = sType.tier
    $scope.ndays = (sType.tier === 4) ? 28 : 90
    // track that we're now on the main form details page
    ga('set', 'page', $state.href($state.current.name, $stateParams))
    ga('send', 'pageview')

    // set the configuration for the form fields
    $scope.conf = {
      applicationRaisedDate: {
        max: moment().format('YYYY-MM-DD'),
        errors: {
          max: {
            msg: 'Enter a valid application raised date'
          },
          invalid: {
            summary: 'The application raised date is invalid',
            msg: 'Enter a valid application raised date'
          },
          required: {
            msg: 'Enter a valid application raised date',
            summary: 'The application raised date is invalid'
          }
        }
      },
      endDate: {
        validate: function (v, sc) {
          var finStatus = FinancialstatusService.getDetails()
          var aDate = moment(finStatus.applicationRaisedDate, 'YYYY-MM-DD', true)
          var eDate = moment(finStatus.toDate, 'YYYY-MM-DD', true)
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
      inLondon: {
        inline: true,
        errors: {
          required: {
            summary: 'The in London option is invalid'
          }
        }
      },
      courseType: {
        inline: true,
        errors: {
          required: {
            summary: 'The course type option is invalid'
          }
        }
      },
      continuationCourse: {
        inline: true,
        errors: {
          required: {
            summary: 'The course continuation option is invalid'
          }
        },
        onClick: function (opt, scope) {
          // console.log('onClick', opt, scope)
          if (opt.value !== 'yes') {
            var finStatus = FinancialstatusService.getDetails()
            finStatus.originalCourseStartDate = ''
          }
        }
      },
      courseStartDate: {
      //
      },
      courseEndDate: {
        validate: function (v, sc) {
          var finStatus = FinancialstatusService.getDetails()
          var start = moment(finStatus.courseStartDate, 'YYYY-MM-DD', true)
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
          var finStatus = FinancialstatusService.getDetails()
          var start = moment(finStatus.courseStartDate, 'YYYY-MM-DD', true)
          var contOriginalDateMom = moment(v, 'YYYY-MM-DD', true)
          if (finStatus.continuationCourse !== 'yes') {
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
        prefix: '£ ',
        errors: {
          required: {
            summary: 'The total tuition fees is invalid',
            msg: 'Enter a valid total tuition fees'
          },
          numeric: {
            summary: 'The total tuition fees is invalid',
            msg: 'Enter a valid total tuition fees'
          }
        }
      },
      tuitionFeesAlreadyPaid: {
        prefix: '£ ',
        errors: {
          required: {
            summary: 'The tuition fees already paid is invalid',
            msg: 'Enter a valid tuition fees already paid'
          },
          numeric: {
            summary: 'The tuition fees already paid is invalid',
            msg: 'Enter a valid tuition fees already paid'
          }
        }
      },
      accommodationFeesAlreadyPaid: {
        prefix: '£ ',
        errors: {
          required: {
            summary: 'The accommodation fees already paid is invalid',
            msg: 'Enter a valid accommodation fees already paid'
          },
          numeric: {
            summary: 'The accommodation fees already paid is invalid',
            msg: 'Enter a valid accommodation fees already paid'
          }
        }
      },
      dependants: {
        classes: { 'form-control-1-8': true },
        validate: function (v, s) {
          var len = FinancialstatusService.getCourseLength()
          var finStatus = FinancialstatusService.getDetails()
          var ok = true
          var n = Number(v)
          if (n < 0 || n > 99) {
            ok = false
          }

          if (v.length === 0) {
            ok = false
          }

          if (Math.ceil(n) !== Math.floor(n)) {
            ok = false
          }

          if (
            sType.noDependantsOnCourseLength &&
            len <= sType.noDependantsOnCourseLength &&
            n &&
            finStatus.originalCourseStartDate === ''
          ) {
            var msg = 'Main applicants cannot be accompanied by dependants on courses of '
            msg += sType.noDependantsOnCourseLength
            msg += ' months or less'
            return {
              summary: msg,
              msg: msg
            }
          }

          if (ok) {
            return true
          }

          return {
            summary: 'The number of dependants is invalid',
            msg: 'Enter a valid number of dependants'
          }
        }
      },
      sortCode: {
      },
      accountNumber: {
        length: 8,
        min: '1',
        max: '99999999',
        errors: {
          numeric: {
            summary: 'Enter a valid account number',
            msg: 'Enter a valid account number'
          },
          min: {
            summary: 'Enter a valid account number',
            msg: 'Enter a valid account number'
          }
        }
      },
      dob: {
        max: moment().format('YYYY-MM-DD'),
        errors: {
          max: {
            summary: 'Enter a valid date of birth',
            msg: 'Enter a valid date of birth'
          }
        }
      }
    }

    _.each(sType.hiddenFields, function (id) {
      $scope.conf[id].hidden = true
    })

    $scope.finStatus = FinancialstatusService.getDetails()
    $scope.finStatus.applicantType = sType.value
    $scope.yesNoOptions = [{label: 'Yes', value: 'yes'}, {label: 'No', value: 'no'}]
    $scope.courseTypeOptions = FinancialstatusService.getCourseTypeOptions()
    $scope.pageTitle = sType.label
    $scope.submitButton = {
      text: 'Check financial status',
      disabled: false
    }

  // submit button code
    $scope.detailsSubmit = function (isValid, formScope, formCtrl) {
      FinancialstatusService.trackFormSubmission(formScope)
      FinancialstatusService.setValid(isValid)
      if (isValid) {
        $scope.submitButton.text = 'Sending'
        $scope.submitButton.disabled = true
        FinancialstatusService.sendDetails()
        $scope.$applyAsync()
      }
    }
  }])
