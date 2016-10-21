(function() {
    'use strict';

    angular
        .module('animalSpottingApp')
        .controller('SightingDeleteController',SightingDeleteController);

    SightingDeleteController.$inject = ['$uibModalInstance', 'entity', 'Sighting'];

    function SightingDeleteController($uibModalInstance, entity, Sighting) {
        var vm = this;

        vm.sighting = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Sighting.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
