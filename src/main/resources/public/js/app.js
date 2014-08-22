'use strict';

var module = angular.module('MarkLogicRESTClientApp',
    ['ngResource', 'ngRoute', 'toastr']
);

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
        getProducts:   {method: 'GET',  url: '/products.json', isArray: true},
        addProduct:    {method: 'POST', url: '/products' },
        removeProduct: {method: 'DELETE'}
    });
    service.prototype.isNew = function() {
        return (typeof(this.id) === 'undefined');
    };
    return service;
});

/* ======================================================================= */

module.controller('ProductListController', function($scope, MarkLogicService) {
    $scope.products = MarkLogicService.getProducts();
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

/* ======================================================================= */

function Hello($scope, $http) {
    $http.get('http://rest-service.guides.spring.io/greeting').
        success(function(data) {
            $scope.greeting = data;
        });
}