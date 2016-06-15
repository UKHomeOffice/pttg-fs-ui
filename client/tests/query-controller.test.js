describe('coreController', function () {

    var location, coreController, form, restService, scope, response, deferred;

    beforeEach(module("app.core"));

    beforeEach(inject(function ($rootScope, $controller, $location, $templateCache, $compile, $anchorScroll, $q) {
        scope = $rootScope.$new();
        location = $location;
        q = $q;

        restService = {
            checkFinancialStatus: function (accountNumber, sortCode, totalFundsRequired, endDate) {
            }
        };

        coreController = $controller('coreController as vm', {
            '$scope': scope,
            '$location': location,
            'restService': restService,
            '$anchorScroll': $anchorScroll
        });
        configureTemplates($compile, $templateCache);
    }));

    spyOnSuccessful = function () {
        spyOn(restService, 'checkFinancialStatus').and.callFake(function () {
            deferred = q.defer();
            deferred.resolve(response);
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

    it('is expected to be defined', function () {
        expect(coreController).toBeDefined();
    });


    it('is expected to get the maintenance period end date in ISO format', function () {
        coreController.model.endDateDay = '1';
        coreController.model.endDateMonth = '2';
        coreController.model.endDateYear = '2015';
        expect(coreController.getFullendDate()).toEqual('2015-02-01')
    });

    it('is expected to format the maintenance period end date to DD/MM/YYYY', function () {
        coreController.model.endDateDay = '1';
        coreController.model.endDateMonth = '2';
        coreController.model.endDateYear = '2015';
        expect(coreController.formatendDate()).toEqual('01/02/2015')
    });

    it('is expected the form submits the correct data to the service', function () {
        spyOnSuccessful();

        coreController.model.endDateDay = '1';
        coreController.model.endDateMonth = '2';
        coreController.model.endDateYear = '2015';
        coreController.model.accountNumber = '12345678';
        coreController.model.sortCodeFirst = '20';
        coreController.model.sortCodeSecond = '02';
        coreController.model.sortCodeThird = '03';
        coreController.model.totalFundsRequired = '1';

        coreController.submit()

        expect(coreController.validateError).toBeFalsy();
        expect(restService.checkFinancialStatus).toHaveBeenCalled();
    });


    it('does not call service on validation failure - invalid application date', function () {
        spyOnSuccessful();

        coreController.model.endDateDay = '99';
        coreController.model.endDateMonth = '2';
        coreController.model.endDateYear = '2015';
        coreController.model.accountNumber = '12345678';
        coreController.model.sortCodeFirst = '20';
        coreController.model.sortCodeSecond = '02';
        coreController.model.sortCodeThird = '03';
        coreController.model.totalFundsRequired = '1';

        coreController.submit()

        expect(coreController.validateError).toBeTruthy();
        expect(restService.checkFinancialStatus.calls.count()).toBe(0);
    });

    it('does not call service on validation failure - future application date', function () {
        spyOnSuccessful();

        coreController.model.endDateDay = '1';
        coreController.model.endDateMonth = '2';
        coreController.model.endDateYear = '2999';
        coreController.model.accountNumber = '12345678';
        coreController.model.sortCodeFirst = '20';
        coreController.model.sortCodeSecond = '02';
        coreController.model.sortCodeThird = '03';
        coreController.model.totalFundsRequired='1';

        coreController.submit()

        expect(coreController.validateError).toBeTruthy();
        expect(restService.checkFinancialStatus.calls.count()).toBe(0);
    });

    it('sets returned data from service on the model ', function () {
        spyOnSuccessful();

        response = {sortCode: 200203, accountNumber: 12345678, fundingRequirementMet: true, periodCheckedFrom: 2015-01-03, periodCheckedTo: 2015-01-30, threshold: 1}

        coreController.model.endDateDay = '30';
        coreController.model.endDateMonth = '1';
        coreController.model.endDateYear = '2015';
        coreController.model.accountNumber = '12345678';
        coreController.model.sortCodeFirst = '20';
        coreController.model.sortCodeSecond = '02';
        coreController.model.sortCodeThird = '03';
        coreController.model.totalFundsRequired='1';

        coreController.submit()
        scope.$digest()

        expect(coreController.model.fundingRequirementMet).toBe(true);

        expect(restService.checkFinancialStatus.calls.count()).toBe(1);
    });


});


