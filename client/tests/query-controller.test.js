describe('coreController', function () {

    var location, coreController, form, restService, scope, response, deferred;

    beforeEach(module("app.core"));
    beforeEach(module("templates")); //required for $templatecache to work

    beforeEach(inject(function ($rootScope, $controller, $location, $templateCache, $compile, $anchorScroll, $q) {
        scope = $rootScope.$new();
        location = $location;
        q = $q;

        restService = {
            checkFinancialStatus: function (accountNumber, sortCode, endDate, innerLondonBorough, studentType, courseLength, totalTuitionFees, tuitionFeesAlreadyPaid, accommodationFeesAlreadyPaid) {
            }
        };

        coreController = $controller('coreController as vm', {
            '$scope': scope,
            '$location': location,
            'restService': restService,
            '$anchorScroll': $anchorScroll
        });
        configureTemplates($compile, $templateCache);
        configureStudentTypeTemplate($compile, $templateCache);
    }));

    spyOnSuccessful = function () {
        spyOn(restService, 'checkFinancialStatus').and.callFake(function () {
            deferred = q.defer();
            deferred.resolve(response);
            return deferred.promise;
        });
    }

    spyOnAccountNotFound = function(){
        spyOn(restService, 'checkFinancialStatus').and.callFake(function() {
            deferred = q.defer();
            var error = {status:404, data:{error:{code: "404"}}};
            deferred.reject(error);
            return deferred.promise;
        });
    }

    configureTemplates = function ($compile, $templateCache) {
        templateHtml = $templateCache.get('client/views/financial-status-query.html')
        formElem = angular.element("<div>" + templateHtml + "</div>")
        $compile(formElem)(scope)
        form = scope.form
        scope.$apply()
    }

    configureStudentTypeTemplate = function ($compile, $templateCache) {
        templateHtml = $templateCache.get('client/views/financial-status-student-type.html')
        formElem = angular.element("<div>" + templateHtml + "</div>")
        $compile(formElem)(scope)
        form = scope.form
        scope.$apply()
    }

    it('is expected to be defined', function () {
        expect(coreController).toBeDefined();
    });


    it('is expected to get the maintenance period end date in ISO format', function () {
        coreController.model.endDateDay = 1;
        coreController.model.endDateMonth = 2;
        coreController.model.endDateYear = 2015;
        expect(coreController.getFullEndDate()).toEqual('2015-02-01')
    });

    it('is expected to format the maintenance period end date to DD/MM/YYYY', function () {
        coreController.model.endDateDay = 1;
        coreController.model.endDateMonth = 2;
        coreController.model.endDateYear = 2015;
        expect(coreController.formatEndDate()).toEqual('01/02/2015')
    });

    it('is expected to format the sort code with dashes', function () {
        coreController.model.sortCodeFirst = 11;
        coreController.model.sortCodeSecond = 22;
        coreController.model.sortCodeThird = 33;
        expect(coreController.getFullSortCode()).toEqual('11-22-33')
    });

    it('is expected to format the sort code without dashes', function () {
        coreController.model.sortCodeFirst = '11';
        coreController.model.sortCodeSecond = '22';
        coreController.model.sortCodeThird = '33';
        expect(coreController.getFullSortCodeDigits()).toEqual('112233')
    });

    it('is expected to format the date period checked', function () {
        coreController.model.periodCheckedFrom = '2015-01-01';
        coreController.model.periodCheckedTo = '2015-02-02';
        expect(coreController.getPeriodChecked()).toEqual('01/01/2015 to 02/02/2015')
    });

    it('is expected to format the full end date padding with zeroes', function () {
        coreController.model.endDateDay = '2';
        coreController.model.endDateMonth = '12';
        coreController.model.endDateYear = '2015';
        expect(coreController.getFullEndDate()).toEqual('2015-12-02')
    });

    it('is expected the form submits the correct data to the service', function () {
        spyOnSuccessful();

        initialiseModelWithValues();

        coreController.submit()

        expect(coreController.validateError).toBeFalsy();
        expect(restService.checkFinancialStatus).toHaveBeenCalled();
    });


    it('does not call service on validation failure - invalid end date', function () {
        spyOnSuccessful();

        initialiseModelWithValues();
        coreController.model.endDateDay = '99';

        coreController.submit()

        expect(coreController.validateError).toBeTruthy();
        expect(restService.checkFinancialStatus.calls.count()).toBe(0);
    });

    it('does not call service on validation failure - future end date', function () {
        spyOnSuccessful();

        initialiseModelWithValues();
        coreController.model.endDateYear = '2999';

        coreController.submit()

        expect(coreController.validateError).toBeTruthy();
        expect(restService.checkFinancialStatus.calls.count()).toBe(0);
    });

    it('does not call service on validation failure - studentType not specified', function () {

        coreController.model.studentType = undefined;
        coreController.submitStudentType()

        expect(coreController.validateError).toBeTruthy();
    });

    it('sets the student type checked display text', function () {
        spyOnSuccessful();

        response = {
            sortCode: 200203,
            accountNumber: '12345678',
            fundingRequirementMet: true,
            periodCheckedFrom: '2015-01-03',
            periodCheckedTo: '2015-01-30',
            minimum: 1
        }

        initialiseModelWithValues();

        coreController.submitStudentType()  // doctorate
        coreController.submit()
        scope.$digest()

        expect(coreController.validateError).toBeFalsy();
        expect(restService.checkFinancialStatus).toHaveBeenCalled();
        expect(coreController.model.studentTypeChecked).toBe("Tier 4 (General) student (doctorate extension scheme)");
    });


    it('sets returned data from service on the model ', function () {
        spyOnSuccessful();

        response = {
            sortCode: 200203,
            accountNumber: '12345678',
            fundingRequirementMet: true,
            periodCheckedFrom: '2015-01-03',
            periodCheckedTo: '2015-01-30',
            minimum: 1
        }

        initialiseModelWithValues();

        coreController.submit()
        scope.$digest()

        expect(coreController.model.fundingRequirementMet).toBe(true);
        expect(coreController.model.minimum).toBe(1);
        expect(coreController.model.periodCheckedFrom).toBe('2015-01-03');

        expect(restService.checkFinancialStatus.calls.count()).toBe(1);
    });

    it('sets returned minimum balance data from service on the model when not passed ', function () {
        spyOnSuccessful();

        response = {
            sortCode: 200203,
            accountNumber: '12345678',
            fundingRequirementMet: false,
            periodCheckedFrom: '2015-01-03',
            periodCheckedTo: '2015-01-30',
            minimum: 1,
            minimumBalanceDate: '2015-01-29',
            minimumBalanceValue: 100
        }

        initialiseModelWithValues();

        coreController.submit()
        scope.$digest()

        expect(coreController.model.fundingRequirementMet).toBe(false);
        expect(coreController.model.minimum).toBe(1);
        expect(coreController.model.periodCheckedFrom).toBe('2015-01-03');
        expect(coreController.model.minimumBalanceDate).toBe('2015-01-29');
        expect(coreController.model.minimumBalanceValue).toBe(100);

        expect(restService.checkFinancialStatus.calls.count()).toBe(1);
    });


    it('formats money to a precision of 2 decimal places with a pound sign', function(){
        expect(coreController.formatMoneyPoundsPence(500)).toBe("£500.00");
    });

    it('handles account not found from service', function(){
        spyOnAccountNotFound();
        spyOn(location, 'path');

        initialiseModelWithValues();

        coreController.submit();
        scope.$digest();

        expect(location.path).toHaveBeenCalledWith('/financial-status-no-record');
    });

    it('routes to student selection page for new search', function(){
        spyOn(location, 'path');

        coreController.newSearch();
        scope.$digest();

        expect(location.path).toHaveBeenCalledWith('/financial-status-student-type');
    });

    it('routes to pass page for pass', function(){
        spyOn(location, 'path');
        spyOnSuccessful();

        response = {
            sortCode: 200203,
            accountNumber: '12345678',
            fundingRequirementMet: true,
            periodCheckedFrom: '2015-01-03',
            periodCheckedTo: '2015-01-30',
            minimum: 1,
            dateFundsNotMet: '2015-01-29',
            amount: 100
        }

        initialiseModelWithValues();

        coreController.submit()
        scope.$digest()

        expect(location.path).toHaveBeenCalledWith('/financial-status-result-pass');
    });

    it('routes to not pass page for not pass', function(){
        spyOn(location, 'path');
        spyOnSuccessful();

        response = {
            sortCode: 200203,
            accountNumber: '12345678',
            fundingRequirementMet: false,
            periodCheckedFrom: '2015-01-03',
            periodCheckedTo: '2015-01-30',
            minimum: 1,
            dateFundsNotMet: '2015-01-29',
            amount: 100
        }

        initialiseModelWithValues();

        coreController.submit()
        scope.$digest()

        expect(location.path).toHaveBeenCalledWith('/financial-status-result-not-pass');
    });

    it('clears previous model on new search', function(){

        initialiseModelWithValues();

        coreController.newSearch();
        scope.$digest();

        expect(coreController.model.endDateDay).toBe('');
        expect(coreController.model.endDateMonth).toBe('');
        expect(coreController.model.endDateYear).toBe('');
        expect(coreController.model.numberOfDependants).toBe('');
        expect(coreController.model.courseLength).toBe('');
        expect(coreController.model.innerLondonBorough).toBe('');
    });

    function initialiseModelWithValues(){
        coreController.model.endDateDay = '30';
        coreController.model.endDateMonth = '1';
        coreController.model.endDateYear = '2015';
        coreController.model.accountNumber = '12345678';
        coreController.model.sortCodeFirst = '20';
        coreController.model.sortCodeSecond = '02';
        coreController.model.sortCodeThird = '03';
        coreController.model.innerLondonBorough = 'yes';
        coreController.model.studentType = 'doctorate';
        coreController.model.courseLength = 1;
        coreController.model.totalTuitionFees = 1;
        coreController.model.tuitionFeesAlreadyPaid = 1;
        coreController.model.accommodationFeesAlreadyPaid = 1;
        coreController.model.numberOfDependants = 1;
    }

});


