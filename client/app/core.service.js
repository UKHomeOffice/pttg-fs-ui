(function () {
    'use strict';

    angular
        .module('app.core')
        .factory('restService', restService);

    restService.$inject = ['$http', '$q'];
    /* @ngInject */
    function restService($http, $q) {
        return {
            checkFinancialStatus: checkFinancialStatus
        };

        function checkFinancialStatus(
            accountNumber,
            sortCode,
            toDate,
            inLondon,
            studentType,
            courseLength,
            totalTuitionFees,
            tuitionFeesAlreadyPaid,
            accommodationFeesAlreadyPaid,
            numberOfDependants) {
            var url = '/pttg/financialstatusservice/v1/accounts/' + sortCode + '/' + accountNumber + '/dailybalancestatus';
            return $http.get(url, {
                params: {
                    toDate: toDate,
                    inLondon: inLondon,
                    studentType: studentType,
                    courseLength: courseLength,
                    totalTuitionFees: totalTuitionFees,
                    tuitionFeesAlreadyPaid: tuitionFeesAlreadyPaid,
                    accommodationFeesAlreadyPaid: accommodationFeesAlreadyPaid,
                    numberOfDependants: numberOfDependants,
                }
            })

                .then(
                    function success(response) {
                        return response.data
                    },
                    function error(response) {
                        throw response
                    }
                );

        }
    }
})();
