(function () {
    'use strict';

    angular
        .module('app.core')
        .controller('coreController', coreController);

    coreController.$inject = ['$rootScope', '$location', 'restService', '$anchorScroll'];
    /* @ngInject */
    function coreController($rootScope, $location, restService, $anchorScroll) {
        var vm = this;

        var CURRENCY_SYMBOL = '£';
        var DATE_DISPLAY_FORMAT = 'DD/MM/YYYY';
        var DATE_VALIDATE_FORMAT = 'YYYY-M-D';

        var ACCOUNTNUMBER_REGEX = /^[0-9]{8}$/;

        /* has it*/

        vm.model = {
            applicantDateOfBirthDateDay: '',
            applicantDateOfBirthDateMonth: '',
            applicantDateOfBirthDateYear: '',
            applicationRaisedDateDay: '',
            applicationRaisedDateMonth: '',
            applicationRaisedDateYear: '',

            insideLondon: '',
            courseLength: '',
            totalTuitionFees: '',
            tuitionFeesAlreadyPaid: '',
            accommodationFeesAlreadyPaid: '',
            accountNumber: '',
            sortCode: '',

            greeting: ''
        };

        vm.validateError = false;

        vm.applicantDateOfBirthDateInvalidError = false;
        vm.applicantDateOfBirthDateMissingError = false;
        vm.applicationRaisedDateInvalidError = false;
        vm.applicationRaisedDateMissingError = false;

        vm.insideLondonMissingError = false;
        vm.courseLengthInvalidError = false;
        vm.totalTuitionFeesInvalidError = false;
        vm.tuitionFeesAlreadyPaidInvalidError = false;
        vm.accommodationFeesAlreadyPaidInvalidError = false;
        vm.accountNumberInvalidError = false;
        vm.accountNumberMissingError = false;
        vm.sortCodeInvalidError = false;
        vm.sortCodeMissingError = false;

        vm.serverError = '';

        vm.formatMoney = function (moneyToFormat) {
            return accounting.formatMoney(moneyToFormat, {symbol: CURRENCY_SYMBOL, precision: 2});
        };

        // to do - parameterise this with prefix
        vm.getFullApplicantDateOfBirthDate = function () {
            var month = vm.model.applicantDateOfBirthDateMonth > 9 ? vm.model.applicantDateOfBirthDateMonth : '0' + vm.model.applicantDateOfBirthDateMonth;
            var day = vm.model.applicantDateOfBirthDateDay > 9 ? vm.model.applicantDateOfBirthDateDay : '0' + vm.model.applicantDateOfBirthDateDay
            return vm.model.applicantDateOfBirthDateYear + '-' + month + '-' + day;
            return vm.model.applicantDateOfBirthDateYear + '-' + vm.model.applicantDateOfBirthDateMonth + '-' + vm.model.applicantDateOfBirthDateDay;
        };

        vm.getFullApplicationRaisedDate = function () {
            var month = vm.model.applicationRaisedDateMonth > 9 ? vm.model.applicationRaisedDateMonth : '0' + vm.model.applicationRaisedDateMonth;
            var day = vm.model.applicationRaisedDateDay > 9 ? vm.model.applicationRaisedDateDay : '0' + vm.model.applicationRaisedDateDay
            return vm.model.applicationRaisedDateYear + '-' + month + '-' + day;
        };

        vm.formatApplicantDateOfBirthDate = function () {
            return vm.formatDate(vm.getFullApplicantDateOfBirthDate());
        }

        vm.formatApplicationRaisedDate = function () {
            return vm.formatDate(vm.getFullApplicationRaisedDate());
        }

        vm.formatDate = function (dateToFormat) {
            return moment(dateToFormat, DATE_VALIDATE_FORMAT, true).format("DD/MM/YYYY");
        }

        vm.scrollTo = function (anchor) {
            $anchorScroll(anchor);
        };

        vm.submit = function () {

            if (validateForm()) {

                // to do - add all other parameters
                restService.checkFinancialStatus(vm.model.accountNumber)
                    .then(function (data) {
                        vm.model.greeting = data.greeting;
                        $location.path('/financial-status-result');
                    }).catch(function (error) {
                    if (error.status === 400 && error.data.error.code === INVALID_NINO_NUMBER) {
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

        vm.newSearch = function () {
            $location.path('/financial-status-query');
        };

        function clearErrors() {
            vm.applicantDateOfBirthDateInvalidError = false;
            vm.applicantDateOfBirthDateMissingError = false;
            vm.applicationRaisedDateInvalidError = false;
            vm.applicationRaisedDateMissingError = false;

            vm.insideLondonMissingError = false;
            vm.courseLengthInvalidError = false;
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

        function validateForm() {
            var validated = true;
            clearErrors();


            if (vm.model.applicantDateOfBirthDateDay === null ||
                vm.model.applicantDateOfBirthDateMonth === null ||
                vm.model.applicantDateOfBirthDateYear === null) {
                vm.queryForm.applicantDateOfBirthDateDay.$setValidity(false);
                vm.queryForm.applicantDateOfBirthDateMonth.$setValidity(false);
                vm.queryForm.applicantDateOfBirthDateYear.$setValidity(false);
                vm.applicantDateOfBirthDateMissingError = true;
                validated = false;
            } else if (!moment(vm.getFullApplicantDateOfBirthDate(), DATE_VALIDATE_FORMAT, true).isValid()) {
                vm.applicantDateOfBirthDateInvalidError = true;
                validated = false;
            } else if (moment(vm.getFullApplicantDateOfBirthDate(), DATE_VALIDATE_FORMAT, true).isAfter(moment(), 'day')) {
                vm.applicantDateOfBirthDateInvalidError = true;
                validated = false;
            }

            if (vm.model.applicationRaisedDateDay === null ||
                vm.model.applicationRaisedDateMonth === null ||
                vm.model.applicationRaisedDateYear === null) {
                vm.queryForm.applicationRaisedDateDay.$setValidity(false);
                vm.queryForm.applicationRaisedDateMonth.$setValidity(false);
                vm.queryForm.applicationRaisedDateYear.$setValidity(false);
                vm.applicationRaisedDateMissingError = true;
                validated = false;
            } else if (!moment(vm.getFullApplicationRaisedDate(), DATE_VALIDATE_FORMAT, true).isValid()) {
                vm.applicationRaisedDateInvalidError = true;
                validated = false;
            } else if (moment(vm.getFullApplicationRaisedDate(), DATE_VALIDATE_FORMAT, true).isAfter(moment(), 'day')) {
                vm.applicationRaisedDateInvalidError = true;
                validated = false;
            }

            if (vm.model.accountNumber === '' || vm.model.accountNumber === null) {
                vm.queryForm.accountNumber.$setValidity(false);
                vm.accountNumberMissingError = true;
                validated = false;
            } else {
                if (!ACCOUNTNUMBER_REGEX.test(vm.model.accountNumber)) {
                    vm.accountNumberInvalidError = true;
                    vm.queryForm.accountNumber.$setValidity(false);
                    validated = false;
                }
            }

            return validated;
        }
    }

})();
