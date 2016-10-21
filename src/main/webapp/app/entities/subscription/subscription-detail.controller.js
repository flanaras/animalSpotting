(function() {
    'use strict';

    angular
        .module('animalSpottingApp')
        .controller('SubscriptionDetailController', SubscriptionDetailController);

    SubscriptionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Subscription', 'User', 'Animal'];

    function SubscriptionDetailController($scope, $rootScope, $stateParams, previousState, entity, Subscription, User, Animal) {
        var vm = this;

        vm.subscription = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('animalSpottingApp:subscriptionUpdate', function(event, result) {
            vm.subscription = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
