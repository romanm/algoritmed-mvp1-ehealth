(function(angular) {
	'use strict';

	var app = angular.module('mvp1App', ['ngSanitize', 'ngFileSaver']);

	app.controller('Mvp1Ctrl', function($scope, $http, $filter, $timeout, Blob) {
		initAll($http, $scope, $filter, $timeout, Blob);
	});

	app.controller('AccordionCtrl', function ($scope) {
		this.expandItem = function (o){
			o.expand = !o.expand;
		}
	});

})(window.angular);