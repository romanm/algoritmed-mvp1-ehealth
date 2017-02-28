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

	// for menu colored
	$scope.menuHomeClicked = function(k){
		return k == $scope.pagePath[0];
	}

	$scope.pagePath = window.location.href.split('?')[0].split('/').splice(4);
	if($scope.pagePath.last() && $scope.pagePath.last().length==0) $scope.pagePath.pop();

	// for steps path to root
	$scope.prevousPath = function(){
		var previousUrl = '/';
		if(!$scope.config[$scope.pagePath[0]] 
		|| 'home' == $scope.config[$scope.pagePath[0]].parent
		){
			//root
		}else{
			previousUrl = '/v/' + $scope.config[$scope.pagePath[0]].parent;
		}
		return previousUrl;
	}

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

