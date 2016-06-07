describe('coreController', function(){

    var location, coreController, form, restService, scope, response, deferred;

    beforeEach(module("app.core"));

    beforeEach(inject(function($rootScope, $controller, $location, $templateCache, $compile,  $anchorScroll, $q) {
        scope=$rootScope.$new();
        location = $location;
        q = $q;

        restService = {
            checkFinancialStatus : function(accountNumber) {
            }
        };

        coreController = $controller('coreController as vm', { '$scope' : scope, '$location' : location, 'restService' : restService, '$anchorScroll' : $anchorScroll } );
        configureTemplates($compile, $templateCache);
    }));

    spyOnSuccessful = function(){
        spyOn(restService, 'checkFinancialStatus').and.callFake(function() {
            deferred = q.defer();
            deferred.resolve(response);
            return deferred.promise;
        });
    }

    configureTemplates = function($compile, $templateCache){
        templateHtml = $templateCache.get('client/views/financial-status-query.html')
        formElem = angular.element("<div>" + templateHtml + "</div>")
        $compile(formElem)(scope)
        form = scope.form
        scope.$apply()
    }

    it('is expected to be defined', function(){
        expect(coreController).toBeDefined();
    });

    it('is expected to get the applicant date of birth in ISO format', function(){
        coreController.model.applicantDateOfBirthDateDay='1';
        coreController.model.applicantDateOfBirthDateMonth='2';
        coreController.model.applicantDateOfBirthDateYear='2015';
        expect(coreController.getFullApplicantDateOfBirthDate()).toEqual('2015-02-01')
    });

    it('is expected to format the applicant date of birth to DD/MM/YYYY', function(){
        coreController.model.applicantDateOfBirthDateDay='1';
        coreController.model.applicantDateOfBirthDateMonth='2';
        coreController.model.applicantDateOfBirthDateYear='2015';
        expect(coreController.formatApplicantDateOfBirthDate()).toEqual('01/02/2015')
    });

    it('is expected to get the application raised date in ISO format', function(){
        coreController.model.applicationRaisedDateDay='1';
        coreController.model.applicationRaisedDateMonth='2';
        coreController.model.applicationRaisedDateYear='2015';
        expect(coreController.getFullApplicationRaisedDate()).toEqual('2015-02-01')
    });

    it('is expected to format the application raised date to DD/MM/YYYY', function(){
        coreController.model.applicationRaisedDateDay='1';
        coreController.model.applicationRaisedDateMonth='2';
        coreController.model.applicationRaisedDateYear='2015';
        expect(coreController.formatApplicationRaisedDate()).toEqual('01/02/2015')
    });

    it('is expected the form submits the correct data to the service', function() {
        spyOnSuccessful();

        coreController.model.applicantDateOfBirthDateDay='1';
        coreController.model.applicantDateOfBirthDateMonth='2';
        coreController.model.applicantDateOfBirthDateYear='2015';
        coreController.model.applicationRaisedDateDay='1';
        coreController.model.applicationRaisedDateMonth='2';
        coreController.model.applicationRaisedDateYear='2015';
        coreController.model.accountNumber='12345678';

        coreController.submit()

        expect(coreController.validateError).toBeFalsy();
        expect(restService.checkFinancialStatus).toHaveBeenCalled();
    });


   it('does not call service on validation failure - invalid applicant date of birth', function(){
        spyOnSuccessful();

        coreController.model.applicantDateOfBirthDateDay='1';
        coreController.model.applicantDateOfBirthDateMonth='2000';
        coreController.model.applicantDateOfBirthDateYear='2015';
        coreController.model.applicationRaisedDateDay='1';
        coreController.model.applicationRaisedDateMonth='2';
        coreController.model.applicationRaisedDateYear='2015';
        coreController.model.accountNumber='12345678';

        coreController.submit()

        expect(coreController.validateError).toBeTruthy();
        expect(restService.checkFinancialStatus.calls.count()).toBe(0);
    });

    it('sets returned data from service on the model ', function(){
       spyOnSuccessful();
       response = {greeting : "Hello 12345678"};

        coreController.model.applicantDateOfBirthDateDay='1';
        coreController.model.applicantDateOfBirthDateMonth='2';
        coreController.model.applicantDateOfBirthDateYear='2015';
        coreController.model.applicationRaisedDateDay='1';
        coreController.model.applicationRaisedDateMonth='2';
        coreController.model.applicationRaisedDateYear='2015';
        coreController.model.accountNumber='12345678';

       coreController.submit()
       scope.$digest()

       expect(coreController.model.greeting).toBe("Hello 12345678");

       expect(restService.checkFinancialStatus.calls.count()).toBe(1);
    });


});


