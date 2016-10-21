(function() {
    'use strict';

    angular
        .module('animalSpottingApp')
        .controller('AnimalDeleteController',AnimalDeleteController);

    AnimalDeleteController.$inject = ['$uibModalInstance', 'entity', 'Animal'];

    function AnimalDeleteController($uibModalInstance, entity, Animal) {
        var vm = this;

        vm.animal = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Animal.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
