(function () {
    'use strict';

    angular
        .module('app.core')
        .controller('coreController', coreController);


    coreController.$inject = ['$rootScope', '$location', 'restService', '$anchorScroll', '$log'];
    /* @ngInject */
    function coreController($rootScope, $location, restService, $anchorScroll, $log) {
        var vm = this;

        var CURRENCY_SYMBOL = '£';
        var DATE_DISPLAY_FORMAT = 'DD/MM/YYYY';
        var DATE_VALIDATE_FORMAT = 'YYYY-M-D';

        var ACCOUNT_NUMBER_REGEX = /^(?!0{8})[0-9]{8}$/;
        var SORT_CODE_REGEX = /^(?!00-00-00)(?:\d{2}-){2}\d{2}$/;
        var BARCLAYS_SORT_CODE_REGEX = /^(?!00-00-00)(?:13|14|2[0-9])(?:-\d{2}){2}$/;

        var NON_ZERO_WHOLE_NUMBER_REGEX = /^0*[1-9]\d*$/; //allows leading zeros
        var NUMBER_REGEX = /^\d*\.?\d*$/;
        var WHOLE_NUMBER_REGEX = /^\d*$/;

        var STUDENT_TYPE_NON_DOCTORATE_DISPLAY = "Tier 4 (General) student";
        var STUDENT_TYPE_DOCTORATE_DISPLAY = "Tier 4 (General) student (doctorate extension scheme)";



        vm.getCourseLength = function () {
            var start = moment(vm.getFullCourseStartDate(), DATE_VALIDATE_FORMAT, true);
            var end = moment(vm.getFullCourseEndDate(), DATE_VALIDATE_FORMAT, true);
            var months = end.diff(start, 'months', true);
            if (start.date() === end.date() && !start.isSame(end)) {
                // when using moment diff months, the same day in months being compared
                // rounds down the months
                // eg 1st June to 1st July equals 1 month, NOT 1 month and 1 day which is the result we want
                // therefore if the start and end days are equal add a day onto the month.
                months += 1/31;
            }


            return months;
        };

        vm.getCourseDatesChecked = function () {
            var start = moment(vm.getFullCourseStartDate(), DATE_VALIDATE_FORMAT, true);
            var end = moment(vm.getFullCourseEndDate(), DATE_VALIDATE_FORMAT, true);

            return start.format(DATE_DISPLAY_FORMAT) + ' to ' + end.format(DATE_DISPLAY_FORMAT);
        };

        vm.formatMoneyWholePounds = function (moneyToFormat) {
            return accounting.formatMoney(moneyToFormat, {symbol: CURRENCY_SYMBOL, precision: 0});
        };

        vm.formatMoneyPoundsPence = function (moneyToFormat) {
            return accounting.formatMoney(moneyToFormat, {symbol: CURRENCY_SYMBOL, precision: 2});
        };


        vm.getPeriodChecked = function () {
            return vm.formatDate(vm.model.periodCheckedFrom) +
                " to " +
                vm.formatDate(vm.model.periodCheckedTo)
        }

        vm.getFullDate = function (d, m, y) {
            var month = m.length > 1 ? m : '0' + m;
            var day = d.length > 1 ? d : '0' + d
            var result = y + '-' + month + '-' + day;
            return result;
        };

        vm.getFullEndDate = function () {
            return vm.getFullDate(vm.model.endDateDay, vm.model.endDateMonth, vm.model.endDateYear);
        };

        vm.getFullCourseStartDate = function () {
            return vm.getFullDate(vm.model.courseStartDateDay, vm.model.courseStartDateMonth, vm.model.courseStartDateYear);
        };

        vm.getFullCourseEndDate = function () {
            return vm.getFullDate(vm.model.courseEndDateDay, vm.model.courseEndDateMonth, vm.model.courseEndDateYear);
        };

        vm.formatEndDate = function () {
            return vm.formatDate(vm.getFullEndDate());
        }

        vm.formatDate = function (dateToFormat) {
            return moment(dateToFormat, DATE_VALIDATE_FORMAT, true).format(DATE_DISPLAY_FORMAT);
        }

        vm.getFullSortCode = function () {
            return vm.model.sortCodeFirst + '-' + vm.model.sortCodeSecond + '-' + vm.model.sortCodeThird;
        }

        vm.getFullSortCodeDigits = function () {
            return vm.model.sortCodeFirst + vm.model.sortCodeSecond + vm.model.sortCodeThird;
        }

        vm.scrollTo = function (anchor) {
            $anchorScroll(anchor);
        };

        function copyInputs() {
            vm.model.periodCheckedTo = vm.getFullEndDate();
            vm.model.accountNumberChecked = vm.model.accountNumber;
            vm.model.sortCodeChecked = vm.getFullSortCode();
            vm.model.inLondonChecked = vm.model.inLondon == 'true' ? 'Yes' : 'No';
            vm.model.courseDatesChecked = vm.getCourseDatesChecked();
            vm.model.totalTuitionFeesChecked = vm.model.totalTuitionFees;
            vm.model.tuitionFeesAlreadyPaidChecked = vm.model.tuitionFeesAlreadyPaid;
            vm.model.accommodationFeesAlreadyPaidChecked = vm.model.accommodationFeesAlreadyPaid;
            vm.model.numberOfDependantsChecked = vm.model.numberOfDependants;
        }

        vm.submit = function () {
            if (validateForm()) {

                restService.checkFinancialStatus(
                    vm.model.accountNumber,
                    vm.getFullSortCodeDigits(),
                    vm.getFullEndDate(),
                    vm.model.inLondon,
                    vm.model.studentType,
                    vm.model.courseLength,
                    vm.model.totalTuitionFees,
                    vm.model.tuitionFeesAlreadyPaid,
                    vm.model.accommodationFeesAlreadyPaid,
                    vm.model.numberOfDependants
                )
                    .then(function (data) {
                        copyInputs();
                        vm.model.fundingRequirementMet = data.fundingRequirementMet;
                        vm.model.minimum = data.minimum;
                        vm.model.periodCheckedFrom = data.periodCheckedFrom;

                        if (vm.model.fundingRequirementMet == true) {
                            $location.path('/financial-status-result-pass');
                        } else {
                            vm.model.minimumBalanceDate = data.minimumBalanceDate;
                            vm.model.minimumBalanceValue = data.minimumBalanceValue;
                            $location.path('/financial-status-result-not-pass');
                        }
                        vm.scrollTo('content');
                    }).catch(function (error) {
                    $log.debug("received a non success result: " + error.status + " : " + error.statusText)
                    if (error.status === 404) {
                        copyInputs();
                        $location.path('/financial-status-no-record');
                        vm.scrollTo('content');
                    } else {
                        vm.serverError = 'Unable to process your request, please try again.';
                        vm.serverErrorDetail = error.data.message;
                    }

                });
            } else {
                vm.validateError = true;
            }
        };

        vm.newSearch = function () {
            initialise()
            $location.path('/financial-status-student-type');
        };

        function initialise() {
            vm.scrollTo('content');
            vm.model = {

                endDateDay: '',
                endDateMonth: '',
                endDateYear: '',

                courseEndDateDay: '',
                courseEndDateMonth: '',
                courseEndDateYear: '',

                courseStartDateDay: '',
                courseStartDateMonth: '',
                courseStartDateYear: '',

                inLondon: '',
                studentType: '',
                courseLength: '',
                totalTuitionFees: '',
                tuitionFeesAlreadyPaid: '',
                accommodationFeesAlreadyPaid: '',
                numberOfDependants: '',

                accountNumber: '',
                sortCodeFirst: '',
                sortCodeSecond: '',
                sortCodeThird: '',

                totalFundsRequired: '',

                fundingRequirementMet: '',
                minimum: '',
                accountNumberChecked: '',
                sortCodeChecked: '',
                periodCheckedFrom: '',
                periodCheckedTo: '',
                inLondonChecked: '',
                studentTypeChecked: '',
                courseLengthChecked: '',
                totalTuitionFeesChecked: '',
                tuitionFeesAlreadyPaidChecked: '',
                accommodationFeesAlreadyPaidChecked: '',
                numberOfDependantsChecked: '',
                minimumBalanceDate: '',
                minimumBalanceValue: '',

                doctorate: false
            };

            vm.validateError = false;

            vm.endDateInvalidError = false;
            vm.endDateMissingError = false;

            vm.courseEndDateInvalidError = false;
            vm.courseEndDateMissingError = false;

            vm.courseStartDateInvalidError = false;
            vm.courseStartDateMissingError = false;

            vm.inLondonInvalidError = false;
            vm.inLondonMissingError = false;

            vm.studentTypeMissingError = false;

            vm.courseLengthInvalidError = false;
            vm.courseLengthMissingError = false;

            vm.totalTuitionFeesInvalidError = false;
            vm.totalTuitionFeesMissingError = false;

            vm.tuitionFeesAlreadyPaidInvalidError = false;
            vm.tuitionFeesAlreadyPaidMissingError = false;

            vm.accommodationFeesAlreadyPaidInvalidError = false;
            vm.accommodationFeesAlreadyPaidMissingError = false;

            vm.accountNumberInvalidError = false;
            vm.accountNumberMissingError = false;

            vm.numberOfDependantsInvalidError = false;
            vm.numberOfDependantsMissingError = false;

            vm.sortCodeInvalidError = false;
            vm.sortCodeMissingError = false;

            vm.serverError = '';
            vm.serverErrorDetail = '';

            // if refreshing on a query page, default the student type
            // to that reflected in the url
            switch($location.path()) {
                case '/financial-status-query-non-doctorate':
                    vm.model.studentType = 'nondoctorate';
                    vm.model.doctorate = false;
                    break;
                case '/financial-status-query-doctorate':
                    vm.model.studentType = 'doctorate';
                    vm.model.doctorate = true;
                    break;
            };
        }

        function clearErrors() {
            vm.endDateInvalidError = false;
            vm.endDateMissingError = false;

            vm.inLondonInvalidError = false;
            vm.inLondonMissingError = false;

            vm.studentTypeMissingError = false;

            vm.courseEndDateInvalidError = false;
            vm.courseEndDateMissingError = false;

            vm.courseStartDateInvalidError = false;
            vm.courseStartDateMissingError = false;

            vm.courseLengthInvalidError = false;
            vm.courseLengthMissingError = false;

            vm.totalTuitionFeesInvalidError = false;
            vm.totalTuitionFeesMissingError = false;

            vm.tuitionFeesAlreadyPaidInvalidError = false;
            vm.tuitionFeesAlreadyPaidMissingError = false;

            vm.accommodationFeesAlreadyPaidInvalidError = false;
            vm.accommodationFeesAlreadyPaidMissingError = false;

            vm.numberOfDependantsInvalidError = false;
            vm.numberOfDependantsMissingError = false;

            vm.accountNumberInvalidError = false;
            vm.accountNumberMissingError = false;

            vm.sortCodeInvalidError = false;
            vm.sortCodeMissingError = false;

            vm.serverError = '';
            vm.serverErrorDetail = '';
            vm.validateError = false;
        }

        function validateForm() {
            var validated = true;
            clearErrors();

            if (vm.model.endDateDay === null ||
                vm.model.endDateMonth === null ||
                vm.model.endDateYear === null) {
                vm.queryForm.endDateDay.$setValidity(false);
                vm.queryForm.endDateMonth.$setValidity(false);
                vm.queryForm.endDateYear.$setValidity(false);
                vm.endDateMissingError = true;
                validated = false;
            } else if (!moment(vm.getFullEndDate(), DATE_VALIDATE_FORMAT, true).isValid()) {
                vm.endDateInvalidError = true;
                validated = false;
            } else if (moment(vm.getFullEndDate(), DATE_VALIDATE_FORMAT, true).isAfter(moment(), 'day')) {
                vm.endDateInvalidError = true;
                validated = false;
            }

            if (vm.model.inLondon === '' || vm.model.inLondon === null) {
                vm.queryForm.inLondon.$setValidity(false);
                vm.inLondonMissingError = true;
                validated = false;
            }


            // validate the course start date
            if (vm.model.courseStartDateDay === null ||
                vm.model.courseStartDateMonth === null ||
                vm.model.courseStartDateYear === null) {
                vm.queryForm.courseStartDateDay.$setValidity(false);
                vm.queryForm.courseStartDateMonth.$setValidity(false);
                vm.queryForm.courseStartDateYear.$setValidity(false);
                vm.courseStartDateMissingError = true;
                validated = false;
            } else if (!moment(vm.getFullCourseStartDate(), DATE_VALIDATE_FORMAT, true).isValid()) {
                vm.courseStartDateInvalidError = true;
                validated = false;
            }


            // validate the course end date
            if (vm.model.courseEndDateDay === null ||
                vm.model.courseEndDateMonth === null ||
                vm.model.courseEndDateYear === null) {
                vm.queryForm.courseEndDateDay.$setValidity(false);
                vm.queryForm.courseEndDateMonth.$setValidity(false);
                vm.queryForm.courseEndDateYear.$setValidity(false);
                vm.courseEndDateMissingError = true;
                validated = false;
            } else if (!moment(vm.getFullCourseEndDate(), DATE_VALIDATE_FORMAT, true).isValid()) {
                vm.courseEndDateInvalidError = true;
                validated = false;
            }

            // set the course length based on the start and end dates
            vm.model.courseLength = Math.ceil(vm.getCourseLength());
            if (vm.model.courseLength <= 0) {
                // course length must be greater than zero
                // negative would indicate that the end date was before the start data
                vm.courseLengthInvalidError = true;
                validated = false;
            }

            // make sure that the student type display string is correctly set for the results screens
            vm.model.studentTypeChecked = (vm.model.studentType == 'doctorate') ? STUDENT_TYPE_DOCTORATE_DISPLAY : STUDENT_TYPE_NON_DOCTORATE_DISPLAY;


            if (!vm.model.doctorate) {
                if (vm.model.totalTuitionFees === '' || vm.model.totalTuitionFees === null) {
                    vm.queryForm.totalTuitionFees.$setValidity(false);
                    vm.totalTuitionFeesMissingError = true;
                    validated = false;
                } else if (vm.model.totalTuitionFees !== null && !(NUMBER_REGEX.test(vm.model.totalTuitionFees))) {
                    vm.queryForm.totalTuitionFees.$setValidity(false);
                    vm.totalTuitionFeesInvalidError = true;
                    validated = false;
                }

                if (vm.model.tuitionFeesAlreadyPaid === '' || vm.model.tuitionFeesAlreadyPaid === null) {
                    vm.queryForm.tuitionFeesAlreadyPaid.$setValidity(false);
                    vm.tuitionFeesAlreadyPaidMissingError = true;
                    validated = false;
                } else if (vm.model.tuitionFeesAlreadyPaid !== null && !(NUMBER_REGEX.test(vm.model.tuitionFeesAlreadyPaid))) {
                    vm.queryForm.tuitionFeesAlreadyPaid.$setValidity(false);
                    vm.tuitionFeesAlreadyPaidInvalidError = true;
                    validated = false;
                }
            }

            if (vm.model.accommodationFeesAlreadyPaid === '' || vm.model.accommodationFeesAlreadyPaid === null) {
                vm.queryForm.accommodationFeesAlreadyPaid.$setValidity(false);
                vm.accommodationFeesAlreadyPaidMissingError = true;
                validated = false;
            } else if (vm.model.accommodationFeesAlreadyPaid !== null) {
                if (!(NUMBER_REGEX.test(vm.model.accommodationFeesAlreadyPaid)) || vm.model.accommodationFeesAlreadyPaid > 1265) {
                    vm.queryForm.accommodationFeesAlreadyPaid.$setValidity(false);
                    vm.accommodationFeesAlreadyPaidInvalidError = true;
                    validated = false;
                }
            }

            if (vm.model.numberOfDependants === '' || vm.model.numberOfDependants === null) {
                vm.queryForm.numberOfDependants.$setValidity(false);
                vm.numberOfDependantsMissingError = true;
                validated = false;
            } else if (vm.model.numberOfDependants !== null && !(WHOLE_NUMBER_REGEX.test(vm.model.numberOfDependants))) {
                vm.queryForm.numberOfDependants.$setValidity(false);
                vm.numberOfDependantsInvalidError = true;
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


        vm.submitStudentType = function () {
            if (validateStudentTypeForm()) {
                vm.model.studentTypeChecked = vm.model.studentType == 'doctorate' ? STUDENT_TYPE_DOCTORATE_DISPLAY : STUDENT_TYPE_NON_DOCTORATE_DISPLAY;
                vm.model.doctorate = vm.model.studentType == 'doctorate' ? true : false;
                if (vm.model.doctorate) {
                    $location.path('/financial-status-query-doctorate');
                } else {
                    $location.path('/financial-status-query-non-doctorate');
                }
                // initialise after all functions etc are defined
                initialise();
            } else {
                vm.validateError = true;
            }
        }

        vm.next = function () {
            $location.path('/financial-status-query');
        }


        function validateStudentTypeForm() {
            var validated = true;
            clearErrors();

            if (vm.model.studentType == null || vm.model.studentType === '') {
                vm.queryForm.studentType.$setValidity(false);
                vm.studentTypeMissingError = true;
                validated = false;
            }

            return validated;
        }


        // initialise after all functions etc are defined
        initialise();
    }

})();
