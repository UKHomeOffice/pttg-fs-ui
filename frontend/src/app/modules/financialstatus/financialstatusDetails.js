/* jshint node: true */

'use strict';

var financialstatusModule = angular.module('hod.financialstatus');

// #### ROUTES #### //
financialstatusModule.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
  // define a route for the details of the form
  $stateProvider.state({
    name: 'financialStatusDetails',
    url: '/:studentType',
    title: 'Financial Status : Query',
    parent: 'financialStatus',
    views: {
      'content@': {
        templateUrl: 'modules/financialstatus/financialstatusDetails.html',
        controller: 'FinancialstatusDetailsCtrl'
      },
    },
  });
}]);

// fill in the details of the form
financialstatusModule.controller(
'FinancialstatusDetailsCtrl', ['$rootScope', '$scope', '$state', '$stateParams', 'FinancialstatusService', 'IOService', '$window', '$timeout',
function ($rootScope, $scope, $state, $stateParams, FinancialstatusService, IOService, $window, $timeout) {

  var sType = _.findWhere(FinancialstatusService.getStudentTypes(), {value: $stateParams.studentType});

  if (!sType) {
    // this is not a valid student type option - abort!
    $state.go('financialStatus');
    return;
  }

  // track that we're now on the main form details page
  ga('set', 'page', $state.href($state.current.name, $stateParams));
  ga('send', 'pageview');


  // set the configuration for the form fields
  $scope.conf = {

    endDate: {
      max: moment().format('YYYY-MM-DD'),
      errors: {
        max: {
          msg: 'Enter a valid end date'
        },
        invalid: {
          summary: 'The end date is invalid',
          msg: 'Enter a valid end date'
        },
        required: {
          msg: 'Enter a valid end date',
          summary: 'The end date is invalid'
        }
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
    courseStartDate: {
      //
    },
    courseEndDate: {
      validate: function (v, sc) {
        var len = FinancialstatusService.getCourseLength();
        if (len <= 0) {
          return { summary: 'Enter a valid course length', msg: 'Enter a valid course length' };
        }
        return true;
      }
    },

    continuationEndDate: {
      required: false,
      validate: function (v, sc) {
        if (v === '') {
          // not a required field
          return true;
        }
        console.log('continuationEndDate', v);
        var finStatus = FinancialstatusService.getDetails();
        var end = moment(finStatus.courseEndDate, 'YYYY-MM-DD', true);
        var contEndDateMom = moment(v, 'YYYY-MM-DD', true);

        if (end.isBefore(contEndDateMom)) {
          return true;
        } else {
          return { summary: 'The continuation end date is invalid', msg: 'Enter a valid continuation end date' };
        }
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
    numberOfDependants: {
      classes: { 'form-control-1-8': true },
      validate: function (v, s) {
        var len = FinancialstatusService.getCourseLength();
        var finStatus = FinancialstatusService.getDetails();
        var ok = true;
        var n = Number(v);
        if (n < 0 || n > 99) {
          ok = false;
        }

        if (v.length === 0) {
          ok = false;
        }

        if (Math.ceil(n) !== Math.floor(n)) {
          ok = false;
        }

        if (
          sType.noDependantsOnCourseLength
          && len <= sType.noDependantsOnCourseLength
          && n
          && finStatus.continuationEndDate === ''
        ) {

          var msg = 'Main applicants cannot be accompanied by dependants on courses of ';
          msg += sType.noDependantsOnCourseLength;
          msg += ' months or less';
          return {
            summary: msg,
            msg: msg
          };
        }

        if (ok) {
          return true;
        }

        return {
          summary: 'The number of dependants is invalid',
          msg: 'Enter a valid number of dependants'
        };
      }
    },
    accountNumber: {
      length: 8,
      min: '1',
      max: '99999999',
      errors: {
        numeric: {
          summary: 'Enter a valid account number',
          msg: 'Enter a valid account number',
        },
        min: {
          summary: 'Enter a valid account number',
          msg: 'Enter a valid account number',
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
  };

  _.each(sType.hiddenFields, function (id) {
    $scope.conf[id].hidden = true;
  });

  $scope.finStatus = FinancialstatusService.getDetails();
  $scope.finStatus.studentType = sType.value;
  $scope.yesNoOptions = [{label: 'Yes', value: 'yes'}, {label: 'No', value: 'no'}];
  $scope.pageTitle = sType.label;

  // submit button code
  $scope.detailsSubmit = function (isValid, formScope, formCtrl) {
    FinancialstatusService.trackFormSubmission(formScope);
    FinancialstatusService.setValid(isValid);
    FinancialstatusService.sendDetails();
  };
}]);