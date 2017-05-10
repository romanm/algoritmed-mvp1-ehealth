(function(angular) {
	'use strict';

	var app = angular.module('mvp1App', ['ngSanitize']);

	app.controller('AccordionCtrl', function ($scope) {
		this.expandItem = function (o){
			o.expand = !o.expand;
		}
	});

	app.controller('ProbeCtrl', function ($scope) {
		var i = 0;
		this.title = 'Some title ' + i + $scope.myHTML;
	});

	app.controller('Mvp1Ctrl', function($scope, $http, $filter) {
		console.log('---mvp1App-----Mvp1Ctrl--------');
		initAll($http, $scope, $filter);
	});

})(window.angular);