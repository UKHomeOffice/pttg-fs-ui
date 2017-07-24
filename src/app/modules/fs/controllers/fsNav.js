/* global angular */

'use strict'

var fsModule = angular.module('hod.fs')

fsModule.controller('FsNavCtrl', ['$rootScope', '$scope', '$state', '$location', 'FsService', 'FsInfoService', function ($rootScope, $scope, $state, $location, FsService, FsInfoService) {
  $scope.tiers = FsInfoService.getTiers()
  $scope.showDebug = ($location.host() === '127.0.0.1')
  FsService.reset()
  $scope.setBank = function (accountNumber) {
    var fs = FsService.getApplication()
    fs.accountNumber = accountNumber
    fs.sortCode = '010616'
    fs.dob = '1974-05-13'
  }
}])
