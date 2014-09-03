'use strict';

var module = angular.module('MarkLogicRESTClientApp', [
    'ngResource',
    'ngRoute',
    'toastr',
    'ui.bootstrap',
    'ngTagsInput'
]);

module.config(function($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: '/views/list.html',
            controller: 'ProductListController'
        })
        .when('/search', {
            templateUrl: '/views/search.html',
            controller: 'ApplicationController'
        })
        .when('/products/new', {
            templateUrl: '/views/create.html',
            controller: 'ProductCreateController'
        })
        .when('/products/:sku', {
            templateUrl: '/views/detail.html',
            controller: 'ProductDetailController'
        })
        .otherwise({
            redirectTo: '/'
        });
});

module.factory('MarkLogicService', function ($resource) {
    var service = $resource('/products/:sku.json', {id: '@sku'}, {
        getProduct:    {method: 'GET'},
        /* Note: search results contain facets beside the products */
        getProducts:   {method: 'GET',  url: '/products.json', isArray: false},
        addProduct:    {method: 'POST', url: '/products' },
        removeProduct: {method: 'DELETE'}
    });
    service.prototype.isNew = function() {
        return (typeof(this.id) === 'undefined');
    };
    return service;
});

/* ======================================================================= */

module.controller('ProductListController', function($scope, $modal, $log, MarkLogicService, toastr) {
    $scope.searchresult = MarkLogicService.getProducts();
    $scope.totalItems = 40;  // FIXME
    $scope.currentPage = 1;  // FIXME

    $scope.confirmDeletion = function (product) {
        var modalInstance = $modal.open({
            templateUrl: 'confirmDeletionModal.html',
            controller: ModalInstanceCtrl,
            size: 'sm',
            resolve: {
                product: function () {
                    return product;
                }
            }
        });
        // As soon the user made a decision
        modalInstance.result.then(function(product) {
            $log.info('Will now delete product: ' + product.sku);
            MarkLogicService.removeProduct({sku: product.sku});
            toastr.success("Deleted product '" + product.name + "'");
            // (in case paging is not an issue, delete directly)
            //  var index = $scope.products.indexOf(product);
            //  $scope.products.splice(index, 1);
            $scope.products = MarkLogicService.getProducts();
        }, function () {
            $log.info('Modal dismissed.');
        });
    }
});

var ModalInstanceCtrl = function($scope, $modalInstance, product) {
    $scope.product = product;
    $scope.ok = function () {
        $modalInstance.close(product);
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};

module.controller('ProductSearchController', function($scope, $http, $log, MarkLogicService) {
    /* Method called by typeahead function */
    $scope.findMatchingProducts = function (val) {
        return MarkLogicService.getProducts({name: val}).$promise.then(function (result) {
            var products = [];
            angular.forEach(result.products, function (item) {
                products.push(item.name);
            });
            $log.info(" findMatchingProducts returned: ", products);
            return products;
        });
    };
});

module.controller('ProductDetailController', function($scope, $routeParams, $location, MarkLogicService) {
    var sku = $routeParams.sku;
    $scope.product = MarkLogicService.getProduct({sku: sku});
});

module.controller('ProductCreateController', function($scope, $location, MarkLogicService, toastr) {

    $scope.product = new MarkLogicService();

    $scope.save = function () {
        $scope.product.$addProduct(function (product, headers) {
            toastr.success("Created new product");
            $location.path('/');
        });
    };
});
