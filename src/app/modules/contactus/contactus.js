/* global angular moment */

var contactusModule = angular.module('hod.contactus', ['ui.router'])

// #### ROUTES #### //
contactusModule.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
  // define a route for the results operation
  $stateProvider.state({
    name: 'contactus',
    url: '/contactus',
    title: 'Financial Status : Contact Us',
    parent: 'default',
    views: {
      'content@': {
        templateUrl: 'modules/contactus/contactus.html',
        controller: 'ContactUsCtrl'
      }
    }
  })
}])

contactusModule.controller('ContactUsCtrl', ['$scope', function ($scope) {
  var safe = function (str) {
    return encodeURIComponent(str)
  }

  $scope.email = 'provingthingsfsps@homeoffice.gsi.gov.uk'

  $scope.showForm = true
  $scope.contactDetails = {}
  $scope.whatToShow = ''
  $scope.issueOptions = [
    { value: 'security', label: 'Security - example: report suspected misuse of the service' },
    { value: 'functionality', label: 'Functionality - example: unable to fill in sections of the form' },
    { value: 'performance', label: 'Performance - example: web pages are slow to load' },
    { value: 'other', label: 'Other' }
  ]

  $scope.conf = {
    other: {
      classes: {'form-control-1-4': false},
      required: false
    }
  }

  // $scope.newSearch = function () {
  //   FamilymigrationService.reset()
  //   $state.go('familymigration')
  // }

  $scope.contactusSubmit = function (valid) {
    if (valid) {
      var data = $scope.contactDetails
      var body = '# Contact from PTTG FSPS #\n\n'
      body += moment().format('YYYY-MM-DD HH:mm:ss')
      body += '\n\n## Kind of issue ##\n' + data.issueType.toUpperCase()
      body += '\n\n## Time of incident ##\n' + data.time
      body += '\n\n## Affected users ##\n' + data.users
      body += '\n\n## Comments ##\n' + data.other
      window.location = 'mailto:' + $scope.email + '?body=' + safe(body) + '&subject=' + safe(data.issueType.toUpperCase() + ' PTTG-FSPS')
      $scope.showForm = false
    }
  }
}])
