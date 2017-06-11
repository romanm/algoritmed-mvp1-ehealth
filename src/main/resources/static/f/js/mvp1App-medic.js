(function(angular) {
	'use strict';

	var app = angular.module('mvp1App', ['ngSanitize','ngFileSaver']);

	app.controller('Mvp1Ctrl', function($scope, $http, $filter, $timeout, FileSaver, Blob) {
		console.log('---mvp1App-----Mvp1Ctrl--------');
		initAll($http, $scope, $filter, $timeout, FileSaver, Blob);
	});

	app.controller('AccordionCtrl', function ($scope) {
		this.expandItem = function (o){
			o.expand = !o.expand;
		}
	});

})(window.angular);