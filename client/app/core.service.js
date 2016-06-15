(function () {
    'use strict';

    angular
        .module('app.core')
        .factory('restService', restService);

    restService.$inject = ['$http', '$q'];
    /* @ngInject */
    function restService($http, $q) {
        return {
            checkFinancialStatus : checkFinancialStatus
        };
        function checkFinancialStatus(accountNumber, sortCode, totalFundsRequired, toDate) {
            var url = '/pttg/financialstatusservice/v1/accounts/' + sortCode +'/' + accountNumber + '/dailybalancestatus';
            return $http.get(url, {
                                      params: {
                                          totalFundsRequired: totalFundsRequired,
                                          toDate: toDate
                                      }
                                  })

                .then(
                    function success(response) { return response.data },
                    function error(response) { throw response }
                );

        }
    }
})();
