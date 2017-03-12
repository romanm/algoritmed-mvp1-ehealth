(function(angular) {
	'use strict';

	var app = angular.module('mvp1App', ['ngSanitize']);

	app.controller('Mvp1Ctrl', function($scope, $http) {
		console.log('---mvp1App-----Mvp1Ctrl--------');
		initAll($http, $scope);
	});
	

	app.controller('AccordionCtrl', function ($scope) {
		console.log('---AccordionCtrl------------');
		this.expandItem = function (o){
			o.expand = !o.expand;
		}

		this.expandAll = function (o, expandO){
			console.log('---AccordionCtrl-----expandAll--------'+ o.expandAll);
//			o.expandAll = !o.expandAll;
			expandAll(o, expandO);
			/*
			angular.forEach(expandO, function(oToExpand, key) {
				oToExpand.expand = o.expandAll;
			});
			 * */
		}

	});


})(window.angular);