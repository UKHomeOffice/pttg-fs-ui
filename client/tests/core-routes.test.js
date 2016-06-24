describe('Testing routes', function () {
    beforeEach(module('app.core'));

    var location, route, rootScope;

    beforeEach(inject(
        function (_$location_, _$route_, _$rootScope_) {
            location = _$location_;
            route = _$route_;
            rootScope = _$rootScope_;
        }));


    describe('Starting route', function () {
        beforeEach(inject(
            function ($httpBackend) {
                $httpBackend.expectGET('views/financial-status-query.html')
                    .respond(200);
            }));

        it('should load the query page on entry', function () {
            location.path('/');
            rootScope.$digest();
            expect(route.current.templateUrl).toBe('views/financial-status-query.html')
        });
    });

    describe('Default (unrecognised) route', function () {
        beforeEach(inject(
            function ($httpBackend) {
                $httpBackend.expectGET('views/404.html')
                    .respond(200);
            }));

        it('should load 404 (not found) for unregistered urls', function () {
            location.path('/unknown');
            rootScope.$digest();
            expect(route.current.templateUrl).toBe('views/404.html')
        });
    });

    describe('Result route - pass', function () {
        beforeEach(inject(
            function ($httpBackend) {
                $httpBackend.expectGET('views/financial-status-result-pass.html')
                    .respond(200);
            }));

        it('should load results page', function () {
            location.path('/financial-status-result-pass');
            rootScope.$digest();
            expect(route.current.templateUrl).toBe('views/financial-status-result-pass.html')
        });
    });

    describe('Result route - not-pass', function () {
        beforeEach(inject(
            function ($httpBackend) {
                $httpBackend.expectGET('views/financial-status-result-not-pass.html')
                    .respond(200);
            }));

        it('should load results page', function () {
            location.path('/financial-status-result-not-pass');
            rootScope.$digest();
            expect(route.current.templateUrl).toBe('views/financial-status-result-not-pass.html')
        });
    });

    describe('No record route', function () {
        beforeEach(inject(
            function ($httpBackend) {
                $httpBackend.expectGET('views/financial-status-no-record.html')
                    .respond(200);
            }));

        it('should load no record page', function () {
            location.path('/financial-status-no-record');
            rootScope.$digest();
            expect(route.current.templateUrl).toBe('views/financial-status-no-record.html')
        });
    });


});
