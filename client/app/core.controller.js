(function() {
    'use strict';

    angular
        .module('app.core')
        .controller('coreController', coreController);

    coreController.$inject = ['$rootScope','$location','restService','$anchorScroll'];
    /* @ngInject */
    function coreController($rootScope, $location, restService, $anchorScroll) {
        var vm = this;

        var NINO_REGEX = /^[a-zA-Z]{2}[0-9]{6}[a-dA-D]{1}$/;
        var CURRENCY_SYMBOL = '£';
        var DATE_DISPLAY_FORMAT = 'DD/MM/YYYY';
        var DATE_VALIDATE_FORMAT = 'YYYY-M-D';
        var INVALID_NINO_NUMBER = '0001';

        /* has it*/

        vm.model = {
            applicantDateOfBirthDay: '',
            applicantDateOfBirthMonth: '',
            applicantDateOfBirthYear: '',
            applicationRaisedDateDay: '',
            applicationRaisedDateMonth: '',
            applicationRaisedDateYear: '',

            insideLondon:'',
            courseLength:'',
            dependants:'',
            totalTuitionFees:'',
            tuitionFeesAlreadyPaid:'',
            accommodationFeesAlreadyPaid:'',
            accountNumber:'',
            sortCode:'',

            greeting:''
        };

        vm.validateError = false;

        vm.applicantDateOfBirthInvalidError = false;
        vm.applicantDateOfBirthMissingError = false;
        vm.applicationRaisedDateInvalidError = false;
        vm.applicationRaisedDateMissingError = false;

        vm.insideLondonMissingError = false;
        vm.courseLengthInvalidError = false;
        vm.dependantsInvalidError = false;
        vm.totalTuitionFeesInvalidError = false;
        vm.tuitionFeesAlreadyPaidInvalidError = false;
        vm.accommodationFeesAlreadyPaidInvalidError = false;
        vm.accountNumberInvalidError = false;
        vm.accountNumberMissingError = false;
        vm.sortCodeInvalidError = false;
        vm.sortCodeMissingError = false;

        vm.serverError = '';

        vm.formatMoney = function(moneyToFormat) {
            return accounting.formatMoney(moneyToFormat, { symbol: CURRENCY_SYMBOL, precision: 2});
        };

        // to do - parameterise this with prefix
        vm.getFullApplicantDateOfBirth = function() {
                var month = vm.model.applicantDateOfBirthMonth > 9 ? vm.model.applicantDateOfBirthMonth : '0' + vm.model.applicantDateOfBirthMonth;
                var day = vm.model.applicantDateOfBirthDay > 9 ? vm.model.applicantDateOfBirthDay : '0' + vm.model.applicantDateOfBirthDay
                return vm.model.applicantDateOfBirthYear+'-'+month+'-'+day;
            return vm.model.applicantDateOfBirthYear+'-'+vm.model.applicantDateOfBirthMonth+'-'+vm.model.applicantDateOfBirthDay;
        };

        vm.getFullApplicationRaisedDate = function() {
                    var month = vm.model.applicationRaisedDateMonth > 9 ? vm.model.applicationRaisedDateMonth : '0' + vm.model.applicationRaisedDateMonth;
                    var day = vm.model.applicationRaisedDateDay > 9 ? vm.model.applicationRaisedDateDay : '0' + vm.model.applicationRaisedDateDay
                    return vm.model.applicationRaisedDateYear+'-'+month+'-'+day;
        };

        vm.formatApplicantDateOfBirth = function() {
            return vm.formatDate(vm.getFullApplicantDateOfBirth());
        }

        vm.formatApplicationRaisedDate = function() {
                  return vm.formatDate(vm.getFullApplicationRaisedDate());
        }

        vm.formatDate = function(dateToFormat) {
                  return moment(dateToFormat, DATE_VALIDATE_FORMAT, true).format("DD/MM/YYYY");
        }

        vm.scrollTo = function(anchor){
            $anchorScroll(anchor);
        };

        vm.submit = function() {

            if (validateForm()) {

                // to do - add all other parameters
                restService.checkFinancialStatus(vm.model.accountNumber)
                    .then(function(data) {
                        vm.model.greeting = data.greeting;
                        $location.path('/financial-status-result');
                    }).catch(function(error) {
                        if (error.status === 400 && error.data.error.code === INVALID_NINO_NUMBER){
                            vm.ninoInvalidError = true;
                            vm.restError = true;
                        } else {
                            vm.serverError = 'Unable to process your request, please try again.';
                        }
                   });
             } else {
                vm.validateError = true;
             }
        };

        vm.newSearch = function() {
            $location.path('/financial-status-query');
        };

        function clearErrors() {
            vm.applicantDateOfBirthInvalidError = false;
            vm.applicantDateOfBirthMissingError = false;
            vm.applicationRaisedDateInvalidError = false;
            vm.applicationRaisedDateMissingError = false;

            vm.insideLondonMissingError = false;
            vm.courseLengthInvalidError = false;
            vm.dependantsInvalidError = false;
            vm.totalTuitionFeesInvalidError = false;
            vm.tuitionFeesAlreadyPaidInvalidError = false;
            vm.accommodationFeesAlreadyPaidInvalidError = false;
            vm.accountNumberInvalidError = false;
            vm.accountNumberMissingError = false;
            vm.sortCodeInvalidError = false;
            vm.sortCodeMissingError = false;

            vm.serverError = '';
            vm.validateError = false;
        }

        function validateForm(){
            var validated = true;
            clearErrors();


            if (vm.model.applicantDateOfBirthDay === null ||
                vm.model.applicantDateOfBirthMonth === null ||
                vm.model.applicantDateOfBirthYear === null  ) {
                vm.queryForm.applicantDateOfBirthDay.$setValidity(false);
                vm.queryForm.applicantDateOfBirthMonth.$setValidity(false);
                vm.queryForm.applicantDateOfBirthYear.$setValidity(false);
                vm.applicantDateOfBirthMissingError = true;
                validated = false;
            } else  if (!moment(vm.getFullApplicantDateOfBirth(), DATE_VALIDATE_FORMAT, true).isValid()){
                vm.applicantDateOfBirthInvalidError = true;
                validated = false;
            }

            if (vm.model.applicationRaisedDateDay === null ||
                vm.model.applicationRaisedDateMonth === null ||
                vm.model.applicationRaisedDateYear === null  ) {
                vm.queryForm.applicationRaisedDateDay.$setValidity(false);
                vm.queryForm.applicationRaisedDateMonth.$setValidity(false);
                vm.queryForm.applicationRaisedDateYear.$setValidity(false);
                vm.applicationRaisedDateMissingError = true;
                validated = false;
            } else  if (!moment(vm.getFullApplicationRaisedDate(), DATE_VALIDATE_FORMAT, true).isValid()){
                vm.applicationRaisedDateInvalidError = true;
                validated = false;
            }

            if (vm.model.accountNumber === '' || vm.model.accountNumber === null) {
                vm.queryForm.accountNumber.$setValidity(false);
                vm.accountNumberMissingError = true;
                validated =  false;
            }

            return validated;
        }
    }

})();
