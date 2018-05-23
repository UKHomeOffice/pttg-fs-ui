/* global angular moment */

var logoutModule = angular.module('hod.logout', [])

// #### ROUTES #### //
logoutModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the results operation
  $stateProvider.state({
    name: 'logout',
    url: '/logout',
    title: 'Financial Status : Sign out',
    // parent: 'familymigration',
    views: {
      'content@': {
        templateUrl: 'modules/logout/logout.html',
        controller: 'LogoutCtrl'
      }
    }
  })
}])

logoutModule.controller('LogoutCtrl', ['$scope', '$http', '$window', function ($scope, $http, $window) {
  $http.get('/logout').then(function (result) {
    var logoutUrl = (result && result.data) ? result.data.logout : ''
    if (logoutUrl) {
      $window.location = logoutUrl
    }
  }).catch(function (e) {
    var logoutUrl = window.location.protocol + '//' + window.location.hostname
    if (window.location.port !== '80' && window.location.port !== '443') {
      logoutUrl += ':' + window.location.port
    }
    $window.location = logoutUrl
  })
}])
