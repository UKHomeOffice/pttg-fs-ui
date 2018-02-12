/* global angular moment ga */

// git update-index --skip-worktree serenity.properties

var app = angular.module('hod.proving', [
  'ui.router',
  'ngAria',
  'hod.contactus',
  'hod.fs',
  'hod.forms',
  'hod.io',
  'hod.availability',
  'hod.logout'
])

app.constant('CONFIG', {
  api: '/pttg/financialstatus/v1/',
  timeout: 5000,
  retries: 0
})

app.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  $urlRouterProvider.otherwise('/fs/t4')

  $stateProvider.state({
    name: 'default',
    title: 'HOD',
    views: {
      'content': {}
    }
  })
}])

app.run(['$location', '$rootScope', '$window', '$timeout', '$state', 'AvailabilityService', function ($location, $rootScope, $window, $timeout, $state, AvailabilityService) {
  // see http://simplyaccessible.com/article/spangular-accessibility/

  AvailabilityService.setURL('availability')

  $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
    ga('set', 'page', $state.href(toState.name, toParams))
    ga('send', 'pageview')
  })

  var focusOnH1 = function () {
    // http://stackoverflow.com/questions/25596399/set-element-focus-in-angular-way
    // http://www.accessiq.org/news/features/2013/03/aria-and-accessibility-adding-focus-to-any-html-element
    $timeout(function () {
      var e = angular.element(document.querySelector('h1'))
      if (e[0]) {
        e[0].focus()
      }
    })
  }

  $rootScope.$on('focusOnH1', function (e) {
    focusOnH1()
  })

  $rootScope.$on('$viewContentLoaded', function (e) {
    focusOnH1()
  })
}])

app.filter('pounds', ['$filter', function ($filter) {
  return function (num) {
    return $filter('currency')(num, '£', 2)
  }
}])

app.filter('dateDisplay', function () {
  return function (date) {
    return moment(date, 'YYYY-MM-DD').format('DD/MM/YYYY')
  }
})

app.filter('sortDisplay', function () {
  return function (sortCode) {
    return sortCode.substr(0, 2) + '-' + sortCode.substr(2, 2) + '-' + sortCode.substr(4, 2)
  }
})
