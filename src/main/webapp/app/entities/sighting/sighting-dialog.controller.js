(function() {
    'use strict';

    angular
        .module('animalSpottingApp')
        .controller('SightingDialogController', SightingDialogController);

    SightingDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Sighting', 'User', 'Animal'];

    function SightingDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Sighting, User, Animal) {
        var vm = this;

        vm.sighting = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.animals = Animal.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.sighting.id !== null) {
                Sighting.update(vm.sighting, onSaveSuccess, onSaveError);
            } else {
                Sighting.save(vm.sighting, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('animalSpottingApp:sightingUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
