(function() {
    'use strict';

    angular
        .module('animalSpottingApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', 'Sighting', '$state'];

    function HomeController ($scope, Principal, LoginService, Sighting, $state) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.sighting = Sighting;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        loadSightings();

        function loadSightings () {
            Sighting.query({
                page: 0,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = ['id,' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            function onSuccess(data, headers) {
                window.sightings = data;
            }

            function onError(error) {
            }
        }

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }
    }
})();
