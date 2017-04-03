(function(angular) {
	'use strict';

	var app = angular.module('mvp1App', ['ngSanitize']);

	app.controller('Mvp1Ctrl', function($scope, $http) {
		console.log('---mvp1App-----Mvp1Ctrl--------');
		initAll($http, $scope);
	});

	app.controller('AccordionCtrl', function ($scope) {
		this.expandItem = function (o){
			o.expand = !o.expand;
		}
	});

	app.controller('ProbeCtrl', function ($scope) {
		var i = 0;
		this.title = 'Some title ' + i + $scope.myHTML;
	});

})(window.angular);