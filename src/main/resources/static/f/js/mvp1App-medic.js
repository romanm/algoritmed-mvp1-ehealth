(function(angular) {
	'use strict';

	var app = angular.module('mvp1App', ['ngSanitize', 'ngFileSaver']);
	app.config(['$compileProvider', function ($compileProvider) {
	  $compileProvider.debugInfoEnabled(false);
	}]);

	app.controller('Mvp1Ctrl', function($scope, $http, $filter, $timeout, Blob) {
		initAll($http, $scope, $filter, $timeout, Blob);
	});

console.log("-----for-----ForInitCtrl-----");

	app.controller('ForInitCtrl', function ($scope) {
		console.log("-----in-----ForInitCtrl-----");
	});

	app.controller('AccordionCtrl', function ($scope) {
		this.expandItem = function (o){
			o.expand = !o.expand;
		}
	});

})(window.angular);