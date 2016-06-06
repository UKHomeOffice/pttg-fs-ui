(function () {
    'use strict';

    var core = angular.module('app.core');
    core.config(routeConfig);
    routeConfig.$inject = ['$routeProvider', '$locationProvider'];

    /* @ngInject */

    function routeConfig($routeProvider, $locationProvider) {
        $locationProvider.html5Mode(false);
        $routeProvider
            .when('/', {
                redirectTo: '/financial-status-query'
            })
            .when('/financial-status-query', {
                templateUrl : 'views/financial-status-query.html'
            })
            .when('/financial-status-result', {
                templateUrl : 'views/financial-status-result.html'
            })
            .otherwise({
                templateUrl: 'views/404.html'
            });
    }
})();
