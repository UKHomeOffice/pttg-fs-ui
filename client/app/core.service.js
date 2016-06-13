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
        function checkFinancialStatus(accountNumber, sortCode, totalFundsRequired, maintenancePeriodEndDate) {
            var url = 'incomeproving/v1/individual/financialstatus/funds';
            return $http.get(url, {
                                      params: { 
                                          accountNumber: accountNumber,
                                          sortCode : sortCode,
                                          totalFundsRequired: totalFundsRequired,
                                          maintenancePeriodEndDate: maintenancePeriodEndDate
                                      }
                                  })

                .then(
                    function success(response) { return response.data },
                    function error(response) { throw response }
                );

        }
    }
})();
