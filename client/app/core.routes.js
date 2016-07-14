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
                redirectTo: '/financial-status-student-type',
                controller: 'routeController as vm'
            })
            .when('/financial-status-student-type', {
                templateUrl : 'views/financial-status-student-type.html',
            })
            .when('/financial-status-query', {
                templateUrl : 'views/financial-status-query.html'
            })
            .when('/financial-status-result-pass', {
                templateUrl : 'views/financial-status-result-pass.html'
            })
            .when('/financial-status-result-not-pass', {
                templateUrl : 'views/financial-status-result-not-pass.html'
            })
            .when('/financial-status-no-record', {
                templateUrl : 'views/financial-status-no-record.html'
            })
            .otherwise({
                templateUrl: 'views/404.html'
            });
    }
})();
