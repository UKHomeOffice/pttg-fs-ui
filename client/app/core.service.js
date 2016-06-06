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
        function checkFinancialStatus(accountNumber) {
            var url = 'financialstatus/v1/greetings';
            return $http.get(url, {
                                      params: { accountNumber: accountNumber }
                                  })

                .then(
                    function success(response) { return response.data },
                    function error(response) { throw response }
                );

        }
    }
})();
