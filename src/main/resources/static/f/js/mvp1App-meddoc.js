(function(angular) {
	'use strict';

	var app = angular.module('mvp1App', ['ngSanitize']);

	app.controller('Mvp1Ctrl', function($scope, $http, $filter, $timeout) {
		console.log('---mvp1App-----Mvp1Ctrl--------');
		initAll($http, $scope, $filter, $timeout);
	});

	app.controller('AccordionCtrl', function ($scope) {
		console.log('---AccordionCtrl------------');
		this.expandItem = function (o){
			console.log(o);
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