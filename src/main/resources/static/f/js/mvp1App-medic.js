(function(angular) {
	'use strict';

	var app = angular.module('mvp1App', ['ngSanitize']);

	app.controller('Mvp1Ctrl', function($scope, $http, $filter, $timeout) {
		console.log('---mvp1App-----Mvp1Ctrl--------');
		initAll($http, $scope, $filter, $timeout);
	});

	app.controller('AccordionCtrl', function ($scope) {
		this.expandItem = function (o){
			o.expand = !o.expand;
		}
	});

})(window.angular);