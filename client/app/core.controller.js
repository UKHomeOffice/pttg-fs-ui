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

        var ACCOUNT_NUMBER_REGEX = /^(?!0{8})[0-9]{8}$/;
        var SORT_CODE_REGEX = /^(?!00-00-00)(?:\d{2}-){2}\d{2}$/;
        var BARCLAYS_SORT_CODE_REGEX = /^(?!00-00-00)(?:13|14|2[0-9])(?:-\d{2}){2}$/;

        /* has it*/

        vm.model = {

            maintenancePeriodEndDateDay: '',
            maintenancePeriodEndDateMonth: '',
            maintenancePeriodEndDateYear: '',

            totalFundsRequired: '',
            accountNumber: '',
            sortCodeFirst: '',
            sortCodeSecond: '',
            sortCodeThird: '',

            greeting: ''
        };

        vm.validateError = false;

        vm.maintenancePeriodEndDateInvalidError = false;
        vm.maintenancePeriodEndDateMissingError = false;

        vm.totalFundsRequiredMissingError = false;
        vm.totalFundsRequiredInvalidError = false;

        vm.accountNumberInvalidError = false;
        vm.accountNumberMissingError = false;

        vm.sortCodeInvalidError = false;
        vm.sortCodeMissingError = false;

        vm.serverError = '';

        vm.formatMoney = function (moneyToFormat) {
            return accounting.formatMoney(moneyToFormat, {symbol: CURRENCY_SYMBOL, precision: 2});
        };

        vm.getFullMaintenancePeriodEndDate = function () {
            var month = vm.model.maintenancePeriodEndDateMonth > 9 ? vm.model.maintenancePeriodEndDateMonth : '0' + vm.model.maintenancePeriodEndDateMonth;
            var day = vm.model.maintenancePeriodEndDateDay > 9 ? vm.model.maintenancePeriodEndDateDay : '0' + vm.model.maintenancePeriodEndDateDay
            return vm.model.maintenancePeriodEndDateYear + '-' + month + '-' + day;
        };

        vm.formatMaintenancePeriodEndDate = function () {
            return vm.formatDate(vm.getFullMaintenancePeriodEndDate());
        }

        vm.formatDate = function (dateToFormat) {
            return moment(dateToFormat, DATE_VALIDATE_FORMAT, true).format("DD/MM/YYYY");
        }

        vm.getFullSortCode = function () {
            return vm.model.sortCodeFirst + '-' + vm.model.sortCodeSecond + '-' + vm.model.sortCodeThird;
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
            vm.maintenancePeriodEndDateInvalidError = false;
            vm.maintenancePeriodEndDateMissingError = false;

            vm.totalFundsRequiredInvalidError = false;
            vm.totalFundsRequiredMissingError = false;

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


            if (vm.model.maintenancePeriodEndDateDay === null ||
                vm.model.maintenancePeriodEndDateMonth === null ||
                vm.model.maintenancePeriodEndDateYear === null) {
                vm.queryForm.maintenancePeriodEndDateDay.$setValidity(false);
                vm.queryForm.maintenancePeriodEndDateMonth.$setValidity(false);
                vm.queryForm.maintenancePeriodEndDateYear.$setValidity(false);
                vm.maintenancePeriodEndDateMissingError = true;
                validated = false;
            } else if (!moment(vm.getFullMaintenancePeriodEndDate(), DATE_VALIDATE_FORMAT, true).isValid()) {
                vm.maintenancePeriodEndDateInvalidError = true;
                validated = false;
            } else if (moment(vm.getFullMaintenancePeriodEndDate(), DATE_VALIDATE_FORMAT, true).isAfter(moment(), 'day')) {
                vm.maintenancePeriodEndDateInvalidError = true;
                validated = false;
            }

            if (vm.model.accountNumber === '' || vm.model.accountNumber === null) {
                vm.queryForm.accountNumber.$setValidity(false);
                vm.accountNumberMissingError = true;
                validated = false;
            } else {
                if (!ACCOUNT_NUMBER_REGEX.test(vm.model.accountNumber)) {
                    vm.accountNumberInvalidError = true;
                    vm.queryForm.accountNumber.$setValidity(false);
                    validated = false;
                }
            }

            if (vm.model.sortCodeFirst === null ||
                vm.model.sortCodeSecond === null ||
                vm.model.sortCodeThird === null) {
                vm.queryForm.sortCodeFirst.$setValidity(false);
                vm.queryForm.sortCodeSecond.$setValidity(false);
                vm.queryForm.sortCodeThird.$setValidity(false);
                vm.sortCodeMissingError = true;
                validated = false;
            } else {
                if (!BARCLAYS_SORT_CODE_REGEX.test(vm.getFullSortCode())) {
                    vm.sortCodeInvalidError = true;
                    validated = false;
                }
            }

            return validated;
        }
    }

})();
