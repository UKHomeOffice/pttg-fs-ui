/* global angular _ */

var ioModule = angular.module('hod.io', [])

ioModule.factory('IOService', ['$http', '$state', 'CONFIG', function ($http, $state, CONFIG) {
  var me = this

  this.getConf = function (conf) {
    var result = {}
    if (!conf) {
      return result
    }
    return _.extend(result, conf)
  }

  this.get = function (url, data, conf) {
    conf = me.getConf(conf)
    if (data) {
      conf.params = data
    }
    console.log('\nIO.get')
    console.log(url)
    console.log(data)
    console.log(conf)
    console.log('')

    var req = $http.get(CONFIG.api + url, conf)
    return req
  }

  this.put = function (url, data, conf) {
    conf = me.getConf(conf)
    return $http.put(CONFIG.api + url, data, conf)
  }

  this.post = function (url, data, conf) {
    conf = me.getConf(conf)
    return $http.post(CONFIG.api + url, data, conf)
  }

  this.delete = function (url, conf) {
    conf = me.getConf(conf)
    return $http.delete(CONFIG.api + url, conf)
  }

  return this
}])
