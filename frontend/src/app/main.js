var app = angular.module('hod.proving', [
  'ui.router',
  'ngAria',
  'hod.financialstatus',
  'hod.forms',
  'hod.io'
]);


app.constant('CONFIG', {
  api: '/',
  timeout: 20000,
  retries: 0,
  polling: {
    enabled: true,
    interval: 1000,
  }
});


app.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
  $urlRouterProvider.otherwise('/financial-status-student-type');

  $stateProvider.state({
    name: 'default',
    title: 'HOD',
    views: {
      'content': {
      },
    },
  });
}]);


app.run(['$location', '$rootScope', '$window', '$timeout', '$state', function($location, $rootScope, $window, $timeout, $state) {
  // see http://simplyaccessible.com/article/spangular-accessibility/

  $rootScope.$on('$viewContentLoaded', function (e) {
    // http://stackoverflow.com/questions/25596399/set-element-focus-in-angular-way

    // http://www.accessiq.org/news/features/2013/03/aria-and-accessibility-adding-focus-to-any-html-element
    $timeout(function() {
      var e = angular.element(document.querySelector('h1'));
      if (e[0]) {
        e[0].focus();
      }
    });

  });
}]);


app.filter('pounds', ['$filter', function ($filter) {
  return function (num) {
    return $filter('currency')(num, '£', 2);
  }
}]);


app.filter('dateDisplay', function () {
  return function (date) {
    return moment(date, 'YYYY-MM-DD').format('DD/MM/YYYY');
  };
});


app.filter('sortDisplay', function () {
  return function (sortCode) {
    return sortCode.substr(0, 2) + '-' + sortCode.substr(2, 2) + '-' + sortCode.substr(4, 2);
  };
});
