(function() {
    'use strict';

    angular
        .module('animalSpottingApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sighting', {
            parent: 'entity',
            url: '/sighting?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'animalSpottingApp.sighting.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sighting/sightings.html',
                    controller: 'SightingController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sighting');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sighting-detail', {
            parent: 'entity',
            url: '/sighting/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'animalSpottingApp.sighting.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sighting/sighting-detail.html',
                    controller: 'SightingDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sighting');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Sighting', function($stateParams, Sighting) {
                    return Sighting.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'sighting',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('sighting-detail.edit', {
            parent: 'sighting-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sighting/sighting-dialog.html',
                    controller: 'SightingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Sighting', function(Sighting) {
                            return Sighting.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sighting.new', {
            parent: 'sighting',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sighting/sighting-dialog.html',
                    controller: 'SightingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                date: null,
                                longitude: null,
                                latitude: null,
                                count: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('sighting', null, { reload: 'sighting' });
                }, function() {
                    $state.go('sighting');
                });
            }]
        })
        .state('sighting.edit', {
            parent: 'sighting',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sighting/sighting-dialog.html',
                    controller: 'SightingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Sighting', function(Sighting) {
                            return Sighting.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('sighting', null, { reload: 'sighting' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sighting.delete', {
            parent: 'sighting',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sighting/sighting-delete-dialog.html',
                    controller: 'SightingDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Sighting', function(Sighting) {
                            return Sighting.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('sighting', null, { reload: 'sighting' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
