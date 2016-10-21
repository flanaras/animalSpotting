(function() {
    'use strict';

    angular
        .module('animalSpottingApp')
        .controller('SightingDetailController', SightingDetailController);

    SightingDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Sighting', 'User', 'Animal'];

    function SightingDetailController($scope, $rootScope, $stateParams, previousState, entity, Sighting, User, Animal) {
        var vm = this;

        vm.sighting = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('animalSpottingApp:sightingUpdate', function(event, result) {
            vm.sighting = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
