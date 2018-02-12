/* global angular _ moment ga */
var formsModule = angular.module('hod.forms', ['ui.validate'])

/**
  * utiltity function to ensure some default key/values are present
  * in the attribute object - the original attribute object is modifed
  * @param object current angular attribute object
  * @param object defaults object key-value pairs of defaults
  * @return void the original attribute object is modifed
*/
var defaultAttrs = function (attrs, defaults) {
  _.each(defaults, function (def, key) {
    if (attrs[key] === undefined) {
      attrs.$set(key, def)
    }
  })

  // force an attribute name - this function is used exclusively for form input type structures
  if (!attrs['name']) {
    attrs.$set('name', getInputName(attrs))
  }
}

// use a global counter to ensure uniqueness when generating an input name
var nameIndexCounter = 0

/**
  * try to determine a suitable name for an input element
  * if no name is specified in the attributes then create one from the label
  * @param object angular directive attribute object
  * @return string the name to use
*/
var getInputName = function (attrs) {
  if (attrs.name) {
    return attrs.name
  }

  if (attrs.label) {
    var n = attrs.label.replace(/[^a-zA-Z]/g, '').toLowerCase() + nameIndexCounter++
    return n
  }
  return 'input' + nameIndexCounter++
}

var lcFirst = function (str) {
  return str.substr(0, 1).toLowerCase() + str.substr(1)
}

var inQ = function (str) {
  return '"' + str + '"'
}

formsModule.factory('FormValidatorsService', [function () {
  var me = this
  var defs = {}
  this.defineValidator = function (id, func) {
    defs[id] = func
  }

  this.getValidator = function (id) {
    return defs[id]
  }

  // return (_.isUndefined(val) || String(val).length === 0) ? false: true;
  this.defineValidator('required', function (val, scope) {
    return !(_.isUndefined(val) || String(val).length === 0)
    // return (_.isUndefined(val) && String(val).length !== 0)
  })

  this.defineValidator('length', function (val, scope) {
    return (val.length === Number(scope.config.length))
  })

  this.defineValidator('numeric', function (val, scope) {
    var num = Number(val)
    return (!_.isNaN(num))
  })

  this.defineValidator('max', function (val, scope) {
    var num = Number(val)
    return (num <= Number(scope.config.max))
  })

  this.defineValidator('min', function (val, scope) {
    var num = Number(val)
    return (num >= Number(scope.config.min))
  })

  return me
}])

/**
  * create the FormsService static to handle global form functions
*/
formsModule.factory('FormsService', ['$rootScope', 'FormValidatorsService', function ($rootScope, FormValidatorsService) {
  var register = {}
  var me = this
  this.registerForm = function (f) {
    var id = f.getId()
    register[id] = f
  }

  /**
    * look at the scope and configuration of a specific element
    * if it has specific error messages defined for that element
    * then return them otherwise return a default message
  */
  this.getError = function (err, scope) {
    // default errors
    var errorObj = {
      err: err,
      summary: 'The ' + inQ(scope.label) + ' is invalid',
      msg: 'Enter a valid ' + inQ(scope.label)
    }

    if (scope.config && scope.config.errors && scope.config.errors[err]) {
      // are any specific messages supplied for the summary or msg
      if (scope.config.errors[err].summary) {
        errorObj.summary = scope.config.errors[err].summary
      }

      if (scope.config.errors[err].msg) {
        errorObj.msg = scope.config.errors[err].msg
      }
    }

    return errorObj
  }

  /**
    * element directives hod-text and hod-number share 99% of the same code
    * so define everything here and both directives can use it
  */
  this.getStandardTextDirective = function (conf) {
    return {
      restrict: 'E',
      require: '^^hodForm',
      scope: {
        field: '=?',
        hint: '@hint',
        name: '@name',
        id: '@id',
        label: '@label',
        config: '=?'
      },
      transclude: true,
      templateUrl: (conf.type === 'textarea') ? 'modules/forms/forms-textarea.html' : 'modules/forms/forms-text.html',
      compile: function (element, attrs) {
        defaultAttrs(attrs, {name: '', hint: '', label: ''})
        return function (scope, element, attrs, formCtrl) {
          scope.type = conf.type
          scope.displayError = ''
          scope.validators = {}
          if (!_.isObject(scope.config)) {
            scope.config = {}
          }

          // set the default configs
          scope.config = angular.merge({
            id: attrs.name,
            hidden: false,
            type: conf.type,
            required: true,
            errors: {
              numeric: {
                msg: 'Not numeric',
                summary: attrs.label + ' is not numeric'
              },
              max: {
                msg: 'Exceeds the maximum',
                summary: attrs.label + ' exceeds the maximum'
              },
              min: {
                msg: 'Below the minimum',
                summary: attrs.label + ' below the minimum'
              }
            },
            classes: {
              'form-control-1-4': true
            },
            prefix: '',
            suffix: ''
          }, scope.config)

          // register this component with the form controller
          formCtrl.addObj(scope)

          // set the maxlength
          if (scope.config.length) {
            scope.maxlength = scope.config.length
          } else if (scope.config.max) {
            scope.maxlength = scope.config.max.toString().length
          } else {
            scope.maxlength = ''
          }

          scope.getInput = function () {
            return formCtrl.getForm()[attrs.name]
          }

          if (scope.config.required) {
            scope.validators['required'] = FormValidatorsService.getValidator('required')
          }

          if (scope.config.type === 'number') {
            scope.validators['numeric'] = FormValidatorsService.getValidator('numeric')
          }

          if (scope.config.length) {
            scope.validators['length'] = FormValidatorsService.getValidator('length')
          }

          if (scope.config.min) {
            scope.validators['min'] = FormValidatorsService.getValidator('min')
          }

          if (scope.config.max) {
            scope.validators['max'] = FormValidatorsService.getValidator('max')
          }

          scope.validfunc = function (val) {
            var validate = function () {
              if (scope.hidden) {
                // if hidden we can't allow this item to trigger an invalid form
                return true
              }

              if (scope.config.validate) {
                var custom = scope.config.validate(val, scope)
                if (_.isObject(custom)) {
                  return custom
                }
                if (custom === false) {
                  return me.getError('invalid', scope)
                }
              }

              var unacceptable = _.findKey(scope.validators, function (func) {
                return (!func(val, scope))
              })

              if (unacceptable) {
                return me.getError(unacceptable, scope)
              }

              return true
            }

            var result = validate()
            // $rootScope.$applyAsync();

            if (result === true) {
              scope.error = {code: '', summary: '', msg: ''}
              scope.getInput().$setValidity('text', true)
              return true
            }

            scope.error = result
            scope.getInput().$setValidity('text', false)
            return false
          }
        }
      }
    }
  }

  this.trackFormSubmission = function (formScope) {
    var errcount = 0
    var errcountstring = ''
    _.each(formScope.objs, function (o) {
      if (o.config.hidden) {
        return
      }
      if (o.error && o.error.msg) {
        errcount++
        ga('send', 'event', formScope.name, 'validation', o.config.id)
      }
    })
    errcountstring = '' + errcount
    while (errcountstring.length < 3) {
      errcountstring = '0' + errcountstring
    }

    ga('send', 'event', formScope.name, 'errorcount', errcountstring)
  }

  return this
}])

/**
  * A directive for the overall hod-form tag
*/
formsModule.directive('hodForm', ['$anchorScroll', 'FormsService', function ($anchorScroll, FormsService) {
  return {
    restrict: 'E',
    transclude: true,
    scope: {
      name: '@',
      submit: '=',
      displayErrors: '@',
      id: '@'
    },
    templateUrl: 'modules/forms/forms.html',

    controller: ['$scope', '$element', '$attrs', '$window', '$timeout', function ($scope, $element, $attrs, $window, $timeout) {
      var me = this
      var objs = $scope.objs = {}
      $scope.errorList = []

      defaultAttrs($attrs, { id: 'hodForm', name: 'hodForm', displayErrors: false })

      this.getId = function () {
        return $attrs.id
      }

      FormsService.registerForm(this)

      this.getForm = function () {
        return $scope[$attrs['name'] + 'Form']
      }

      this.getScope = function () {
        return $scope
      }

      this.addObj = function (obj) {
        objs[obj.config.id] = obj
      }

      this.validateForm = function () {
        var errorList = []

        // check each component of the form
        _.each(objs, function (obj) {
          var inp = obj.getInput()

          if (obj.config.hidden || _.isUndefined(inp)) {
            return
          }

          obj.validfunc(inp.$viewValue)
          if (obj.type === 'radio') {

          }

          if (inp.$valid) {
            // clear the components error message
            obj.displayError = ''
          } else if (obj.error.msg === '') {
            // NO ERROR MESSAGE?
            obj.displayError = ''
          } else {
            // show the message within the component
            obj.displayError = obj.error.msg
            var a
            switch (obj.type) {
              case 'textarea':
              case 'text':
              case 'number':
                a = obj.config.id
                break

              case 'date':
                a = obj.config.id + 'Day'
                break

              case 'sortcode':
                a = obj.config.id + 'Part1'
                break

              case 'radio':
                a = obj.config.id + '-' + obj.options[0].value + '-label'
                break

              case 'checkboxes':
                a = obj.config.options[0].id + '-label'
                break

              default:
                a = obj.config.id + '0'
            }

            // add the error to the list of summary errors for the top of the page
            errorList.push({id: obj.id, msg: obj.error.summary, code: obj.error.errorCode, anchor: a})
          }
        })
        if (angular.toJson(errorList) !== angular.toJson($scope.errorList)) {
          $scope.errorList = errorList
        }
        return errorList.length
      }

      $scope.errorClicked = function (anchor) {
        var e = angular.element(document.getElementById(anchor))
        if (e[0]) {
          e[0].focus()
        }
      }

      $scope.submitForm = function () {
        var isValid = (me.validateForm() === 0)
        $scope.$applyAsync()
        FormsService.trackFormSubmission($scope)

        if (isValid) {
          $scope.showErrors = !isValid
        } else {
          $scope.showErrors = !isValid
          $timeout(function () {
            var e = angular.element(document.getElementById('validation-error-summary-heading'))
            if (e[0]) {
              e[0].focus()
            }
          })
        }

        if ($scope.submit) {
          $scope.submit(isValid, $scope, me)
        }
      }
    }]
  }
}])

formsModule.directive('hodText', ['FormsService', function (FormsService) {
  return FormsService.getStandardTextDirective({type: 'text'})
}])

formsModule.directive('hodTextarea', ['FormsService', function (FormsService) {
  return FormsService.getStandardTextDirective({type: 'textarea'})
}])

formsModule.directive('hodNumber', ['FormsService', function (FormsService) {
  return FormsService.getStandardTextDirective({type: 'number'})
}])

formsModule.directive('hodRadio', ['FormsService', function (FormsService) {
  return {
    restrict: 'E',
    require: '^^hodForm',
    scope: {
      field: '=',
      hint: '@hint',
      name: '@name',
      label: '@label',
      config: '=?',
      options: '=?'
    },
    // transclude: true,
    templateUrl: 'modules/forms/forms-radio.html',
    compile: function (element, attrs) {
      defaultAttrs(attrs, {hint: '', label: '', inline: false})
      return function (scope, element, attrs, formCtrl, transclude) {
        scope.type = 'radio'
        scope.displayError = ''

        if (!scope.config) {
          scope.config = {}
        }

        if (_.isArray(scope.config.options) && !_.isArray(attrs.options)) {
          scope.options = scope.config.options
        }

        if (typeof attrs.required === 'string') {
          scope.config.required = (attrs.required !== 'false')
        }

        // set the default configs
        scope.config = angular.merge({
          id: attrs.name,
          hidden: false,
          inline: false,
          required: true,
          label: scope.label || '',
          // options: [{label: 'Please select', value: 0}],
          errors: {
            required: {
              summary: 'The ' + inQ(scope.label) + ' option is invalid',
              msg: 'Select an option'
            }
          }
        }, scope.config)
        //
        formCtrl.addObj(scope)

        scope.getSelectedOption = function () {
          return _.findWhere(scope.options, {value: scope.field})
        }

        scope.validfunc = function (val) {
          var selected = scope.getSelectedOption(val)
          var validate = function () {
            if (scope.config.required && _.isUndefined(selected)) {
              // it is required (do this test before val is still a string)
              scope.getInput().$valid = false
              scope.getInput().$invalid = true
              return FormsService.getError('required', scope)
            }

            scope.getInput().$valid = true
            scope.getInput().$invalid = false
            return true
          }

          var result = validate()
          if (result === true) {
            scope.error = {code: '', summary: '', msg: ''}
            return true
          }

          scope.error = result
          return false
        }

        scope.isSelected = function (opt) {
          return opt.value === scope.field
        }

        scope.getInput = function () {
          return formCtrl.getForm()[attrs.name]
        }

        scope.radioClick = function (opt) {
          scope.field = opt.value
          scope.$applyAsync()
          if (scope.config.onClick) {
            scope.config.onClick(opt, scope)
          }
        }
      }
    }
  }
}])

formsModule.directive('hodCheckbox', ['FormsService', function (FormsService) {
  return {
    restrict: 'E',
    require: '^^hodForm',
    scope: {
      field: '=',
      hint: '@hint',
      name: '@name',
      label: '@label',
      config: '=?'
    },
    // transclude: true,
    templateUrl: 'modules/forms/forms-checkbox.html',
    compile: function (element, attrs) {
      defaultAttrs(attrs, {hint: '', label: '', inline: false})
      return function (scope, element, attrs, formCtrl, transclude) {
        scope.checked = (scope.field === true)
        scope.field = scope.checked
        scope.checkboxClick = function () {
          scope.checked = !scope.checked
          scope.field = scope.checked
          scope.$applyAsync()
        }
      }
    }
  }
}])


formsModule.directive('hodCheckboxes', ['FormsService', function (FormsService) {
  return {
    restrict: 'E',
    require: '^^hodForm',
    scope: {
      field: '=',
      hint: '@hint',
      name: '@name',
      label: '@label',
      config: '=?'
    },
    // transclude: true,
    templateUrl: 'modules/forms/forms-checkboxes.html',
    compile: function (element, attrs) {
      defaultAttrs(attrs, {hint: '', label: '', inline: false})
      return function (scope, element, attrs, formCtrl, transclude) {
        _.each(scope.config.options, function (opt) {
          opt.checked = !!opt.checked
          scope.field[opt.id] = !!scope.field[opt.id]
        })

        formCtrl.addObj(scope)
        scope.type = 'checkboxes'

        scope.getInput = function () {
          return formCtrl.getForm()[scope.config.options[0].id]
        }

        scope.validfunc = function () {
          var result
          if (_.isObject(scope.config.validate)) {
            result = scope.config.validate(scope.config.options, scope)
          } else {
            result = true
          }

          if (result === true) {
            scope.error = {code: '', summary: '', msg: ''}
            scope.getInput().$setValidity('text', true)
            return true
          }

          scope.error = result
          scope.getInput().$setValidity('checkboxes', false)
          return false
        }

        scope.checkBoxChange = function (opt) {
          scope.field[opt.id] = opt.checked
          scope.$applyAsync()
        }
      }
    }
  }
}])

formsModule.directive('hodDate', ['FormsService', function (FormsService) {
  return {
    restrict: 'E',
    require: '^^hodForm',
    scope: {
      field: '=',
      hint: '@hint',
      name: '@name',
      label: '@label',
      config: '=?'
    },
    transclude: true,
    templateUrl: 'modules/forms/forms-date.html',
    compile: function (element, attrs) {
      defaultAttrs(attrs, {hint: '', label: '', required: true})

      return function (scope, element, attrs, formCtrl) {
        scope.type = 'date'
        scope.displayError = ''

        if (!scope.config) {
          scope.config = {}
        }

        if (typeof attrs.required === 'string') {
          scope.config.required = (attrs.required !== 'false')
        }

        // set the default configs
        scope.config = angular.merge({
          id: attrs.name,
          hidden: false,
          inline: false,
          required: true,
          errors: {
            max: {
              msg: 'Date is after the max date',
              summary: inQ(attrs.label) + ' is invalid'
            },
            min: {
              msg: 'Date is before the min date',
              summary: inQ(attrs.label) + ' is invalid'
            }
          }
        }, scope.config)

        formCtrl.addObj(scope)

        scope.getInput = function () {
          return formCtrl.getForm()[attrs.name + 'Year']
        }

        scope.getData = function (input) {
          var data = {day: '', month: '', year: ''}
          if (typeof input === 'string') {
            var bits = input.split('-')
            if (Number(bits[0])) {
              data.year = Number(bits[0])
            }

            if (Number(bits[1])) {
              data.month = Number(bits[1])
            }

            if (Number(bits[2])) {
              data.day = Number(bits[2])
            }
          }
          return data
        }

        scope.data = scope.getData(scope.field)

        scope.dateChanged = function () {
          if (scope.config.hidden) {
            return
          }
          scope.updateFieldValue()
          _.defer(function () {
            scope.validfunc()
          })
        }

        scope.updateFieldValue = function () {
          if (scope.isBlank()) {
            scope.field = ''
          } else {
            var mom = moment(scope.data.year + '-' + scope.data.month + '-' + scope.data.day, 'YYYY-MM-DD')
            scope.field = mom.format('YYYY-MM-DD')
          }
        }

        scope.isValid = function () {
          var validDay = (Number(scope.data.day) >= 1 && Number(scope.data.day) <= 31)
          var validMonth = (Number(scope.data.month) >= 1 && Number(scope.data.month) <= 12)
          var validYear = (Number(scope.data.year) >= 1000)
          return (validDay && validMonth && validYear)
        }

        scope.isBlank = function () {
          return ((scope.data.day + '' + scope.data.month + '' + scope.data.year).length === 0)
        }

        scope.validfunc = function () {
          if (scope.config.hidden) {
            return true
          }

          var validate = function () {
            if (scope.config.validate) {
              var custom = scope.config.validate(scope.field, scope)
              if (_.isObject(custom)) {
                return custom
              }
              if (custom === false) {
                return FormsService.getError('invalid', scope)
              }
            }

            if (scope.isBlank()) {
              if (scope.config.required) {
                // field is blank but is required
                return FormsService.getError('required', scope)
              } else {
                // field is blank but this is not a required field
                return true
              }
            }

            if (!scope.isValid(scope.field)) {
              return FormsService.getError('invalid', scope)
            }

            if (scope.config.max) {
              var maxDate = moment(scope.field, 'YYYY-MM-DD')
              var inputDate = moment(scope.config.max, 'YYYY-MM-DD')
              if (inputDate.isBefore(maxDate)) {
                return FormsService.getError('max', scope)
              }
            }

            if (scope.config.min) {
              var minDate = moment(scope.field, 'YYYY-MM-DD')
              inputDate = moment(scope.config.min, 'YYYY-MM-DD')
              if (inputDate.isAfter(minDate)) {
                return FormsService.getError('min', scope)
              }
            }

            return true
          }

          var result = validate()

          if (result === true) {
            scope.error = {code: '', summary: '', msg: ''}
            scope.getInput().$setValidity('date', true)
            return true
          }
          scope.getInput().$setValidity('date', false)
          scope.error = result
          return false
        }

        scope.validfunc()
      }
    }
  }
}])

formsModule.directive('hodSortcode', ['FormsService', function (FormsService) {
  return {
    restrict: 'E',
    require: '^^hodForm',
    transclude: true,
    templateUrl: 'modules/forms/forms-sortcode.html',
    scope: {
      field: '=',
      hint: '@hint',
      name: '@name',
      label: '@label',
      config: '=?'
    },
    compile: function (element, attrs) {
      defaultAttrs(attrs, {hint: '', label: '', required: true})
      return function (scope, element, attrs, formCtrl) {
        scope.type = 'sortcode'
        scope.displayError = ''

        if (!scope.config) {
          scope.config = {}
        }

        if (typeof attrs.required === 'string') {
          scope.config.required = (attrs.required !== 'false')
        }

        // set the default configs
        scope.config = angular.merge({
          id: attrs.name,
          hidden: false,
          inline: false,
          required: true,
          errors: {
            // required: {
            //   msg: 'Required',
            //   summary: attrs.label + ' is required'
            // },
            // invalid: {
            //   msg: 'Invalid',
            //   summary: attrs.label + ' is invalid'
            // }
          }
        }, scope.config)

        //
        formCtrl.addObj(scope)

        scope.getInput = function () {
          return formCtrl.getForm()[attrs.name + 'Part1']
        }

        scope.getData = function (input) {
          var data = {}
          if (typeof input === 'string') {
            data.part1 = input.substr(0, 2)
            data.part2 = input.substr(2, 2)
            data.part3 = input.substr(4, 2)
          }

          return data
        }

        scope.data = scope.getData(scope.field)

        scope.itChanged = function () {
          var str = scope.data.part1 + '' + scope.data.part2 + '' + scope.data.part3
          scope.field = str
          scope.validfunc()
        }

        scope.isValid = function () {
          var pt1 = Number(scope.data.part1)
          var pt2 = Number(scope.data.part2)
          var pt3 = Number(scope.data.part3)

          if (scope.field.length !== 6) {
            return false
          }

          return ((pt1 > 0 && pt1 <= 99) &&
                  (pt2 > 0 && pt2 <= 99) &&
                  (pt3 > 0 && pt3 <= 99))
        }

        scope.isBlank = function () {
          return (scope.field && scope.field.length === 0)
        }

        scope.validfunc = function () {
          if (scope.config.hidden) {
            return true
          }
          var validate = function () {
            if (scope.isBlank()) {
              if (scope.config.required) {
                // field is blank but is required
                return FormsService.getError('required', scope)
              } else {
                // field is blank but this is not a required field
                return true
              }
            }

            if (!scope.isValid(scope.field)) {
              return FormsService.getError('invalid', scope)
            }

            return true
          }

          var result = validate()
          if (result === true) {
            scope.error = {code: '', summary: '', msg: ''}
            scope.getInput().$setValidity('sortcode', true)
            return true
          }
          scope.getInput().$setValidity('sortcode', false)
          scope.error = result
          return false
        }

        _.defer(function () {
          scope.validfunc()
        })
      }
    }
  }
}])

formsModule.directive('hodSubmit', [function () {
  return {
    restrict: 'E',
    require: '^^hodForm',
    compile: function (element, attrs) {
      if (attrs.value === undefined) {
        attrs.$set('value', 'Submit')
      }

      return function (scope, element, attrs, formCtrl) {
        scope.type = 'submit'
        // var formEl = formCtrl.getForm();
        scope.shouldDisable = function () {
          return (attrs.disabled === 'true' || attrs.disabled === 1)
        }
      }
    },
    scope: {
      value: '@value'

    },
    templateUrl: 'modules/forms/forms-submit.html'
  }
}])
