config-app-spring.xml(function(angular) {
	'use strict';

	var app = angular.module('mvp1App', ['ngSanitize']);

	app.controller('Mvp1Ctrl', function($scope, $http) {
		console.log('---mvp1App-----Mvp1Ctrl--------');
		initAll($http, $scope);
	});

})(window.angular);