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

        var NON_ZERO_WHOLE_NUMBER_REGEX = /^0*[1-9]\d*$/; //allows leading zeros

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

            fundingRequirementMet: '',
            threshold: '',
            accountNumberChecked: '',
            sortCodeChecked: '',
            periodCheckedFrom: '',
            periodCheckedTo: ''

        };

        vm.validateError = false;

        vm.maintenancePeriodEndDateInvalidError = false;
        vm.maintenancePeriodEndDateMissingError = false;

        vm.totalFundsRequiredInvalidError = false;
        vm.totalFundsRequiredMissingError = false;

        vm.accountNumberInvalidError = false;
        vm.accountNumberMissingError = false;

        vm.sortCodeInvalidError = false;
        vm.sortCodeMissingError = false;

        vm.serverError = '';

        vm.formatMoney = function (moneyToFormat) {
            return accounting.formatMoney(moneyToFormat, {symbol: CURRENCY_SYMBOL, precision: 2});
        };

        vm.formatMoneyWholePounds = function (moneyToFormat) {
            return accounting.formatMoney(moneyToFormat, {symbol: CURRENCY_SYMBOL, precision: 0});
        };

        vm.getPeriodChecked = function () {
            return vm.formatDate(vm.model.periodCheckedFrom) +
                " to " +
                vm.formatDate(vm.model.periodCheckedTo)
        }

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
                restService.checkFinancialStatus(
                    vm.model.accountNumber,
                    vm.getFullSortCode(),
                    vm.model.totalFundsRequired,
                    vm.getFullMaintenancePeriodEndDate())
                    .then(function (data) {
                        vm.model.fundingRequirementMet = data.fundingRequirementMet;
                        vm.model.periodCheckedFrom = data.periodCheckedFrom;
                        vm.model.periodCheckedTo = data.periodCheckedTo;
                        vm.model.threshold = data.threshold;
                        vm.model.accountNumberChecked = data.accountNumber;
                        vm.model.sortCodeChecked = data.sortCode;
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

            if (vm.model.totalFundsRequired === '' || vm.model.totalFundsRequired === null) {
                vm.queryForm.totalFundsRequired.$setValidity(false);
                vm.totalFundsRequiredMissingError = true;
                validated = false;
            } else if (vm.model.totalFundsRequired !== null && !(NON_ZERO_WHOLE_NUMBER_REGEX.test(vm.model.totalFundsRequired))) {
                vm.queryForm.totalFundsRequired.$setValidity(false);
                vm.totalFundsRequiredInvalidError = true;
                validated = false;
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
                if (!SORT_CODE_REGEX.test(vm.getFullSortCode())) {
                    vm.sortCodeInvalidError = true;
                    validated = false;
                }
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

            return validated;
        }
    }

})();
