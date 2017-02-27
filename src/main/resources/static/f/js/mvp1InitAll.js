function initAll ($http, $scope){
	console.log('----initAll---------------');
	$http.get('/f/config/mvp1.algoritmed.site.config.json').then(
		function(response) {
			$scope.config = response.data;
			console.log($scope.config);
			$scope.menuHomeIndex = [];
			angular.forEach($scope.config, function(v, i){
				if(v.parent == 'home'){
					$scope.menuHomeIndex.push(i);
				}
			});
			console.log($scope.menuHomeIndex);
		}, function(response) {
			console.error(response);
		}
	);
}