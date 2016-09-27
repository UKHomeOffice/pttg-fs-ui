var app = angular.module('hod.proving', [
  'ui.router',
  'ngAria',
  'hod.financialstatus',
  'hod.forms',
  'hod.io'
]);


app.constant('CONFIG', {
  api: '/'//'http://127.0.0.1:3001/'//$('html').data('api')
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
  // $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams, options) {
  //   console.log(toState, toParams);
  //   // console.log($location);
  //   // console.log($state.href(toState.name, toParams));
  //   var u = $state.href(toState.name, toParams);

  //   ga('set', 'page', u);
  //   ga('send', 'pageview');

  // });


  $rootScope.$on('$viewContentLoaded', function (e) {
    // http://stackoverflow.com/questions/25596399/set-element-focus-in-angular-way

    // http://www.accessiq.org/news/features/2013/03/aria-and-accessibility-adding-focus-to-any-html-element
    // ga('send', 'pageview',
    $timeout(function() {
      var e = angular.element(document.querySelector('#pageTitle'));
      if (e[0]) {
        e[0].focus();
      }
    });

  });
}]);


