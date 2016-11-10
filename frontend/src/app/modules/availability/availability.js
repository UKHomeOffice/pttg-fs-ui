/* jshint node: true */

'use strict';

var availabilityModule = angular.module('hod.availability', ['hod.io']);


availabilityModule.factory('AvailabilityService', ['IOService', function (IOService) {
  var url = '/availability';
  var interval = 1000;

  this.setURL = function (u) {
    url = u;
  };

  this.setInterval = function (i) {
    interval = i;
  };

  this.getConfig = function () {
    return {
      url: url,
      interval: interval
    };
  };

  return this;
}]);


availabilityModule.directive('hodAvailability', ['IOService', 'AvailabilityService', '$timeout', function (IOService, AvailabilityService, $timeout) {
  return {
    restrict: 'E',
    compile: function (element, attrs) {
      return function(scope, element, attrs, formCtrl) {
        scope.isAvailable = true;
        var conf = AvailabilityService.getConfig();
        var setAvailability = function (a) {
          scope.isAvailable = a;
          scope.$applyAsync();

          if (!a && conf.interval) {
            // if unavailable and a polling interval is set then retry!
            $timeout(function() {
              testAvailability();
            }, conf.interval);
          }
        };

        // test the availabilty end-point
        var testAvailability = function () {
          IOService.get(conf.url).then(function (res) {
            var ok = (res.status === 200) ? true: false;
            setAvailability(ok);
          }, function (err) {
            setAvailability(false);
          });
        };

        // start the test process
        testAvailability();
      };
    },
    scope: {
      status: '=',
    },
    templateUrl: 'modules/availability/availability.html'
  }
}]);
