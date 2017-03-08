function initAll ($http, $scope){
	console.log('----initAll---------------');
	initAllServer($http, $scope);
	$scope.pagePath = window.location.href.split('?')[0].split('/').splice(4);
	if($scope.pagePath.last() && $scope.pagePath.last().length==0) $scope.pagePath.pop();
	if('code' == $scope.pagePath.last()){
		console.log('----initAll------code---------');
		$http.get('/f/config/icpc2/ICPC2-en.json').then(
				function(response) {
					$scope.icpc = response.data;
					console.log($scope.icpc);
				}, function(response) {
					console.error(response);
				}
			);
	}

	$http.get('/f/config/mvp1.algoritmed.meddoc.config.json').then(
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

if (!Array.prototype.last){
	Array.prototype.last = function(){
		return this[this.length - 1];
	}
	Array.prototype.forLast = function(){
		return this[this.length - 2];
	}
	Array.prototype.forForLast = function(){
		return this[this.length - 3];
	}
}

