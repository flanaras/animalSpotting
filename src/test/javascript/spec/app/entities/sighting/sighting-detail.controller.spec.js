'use strict';

describe('Controller Tests', function() {

    describe('Sighting Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockSighting, MockUser, MockAnimal;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockSighting = jasmine.createSpy('MockSighting');
            MockUser = jasmine.createSpy('MockUser');
            MockAnimal = jasmine.createSpy('MockAnimal');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Sighting': MockSighting,
                'User': MockUser,
                'Animal': MockAnimal
            };
            createController = function() {
                $injector.get('$controller')("SightingDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'animalSpottingApp:sightingUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
