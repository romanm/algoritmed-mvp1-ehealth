function initAll ($http, $scope){
	console.log('----initAll---------------');
	
	$scope.pagePath = window.location.href.split('?')[0].split('/').splice(4);

	
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
	

	// for steps path to root
	$scope.prevousPath = function(){
		if($scope.pagePath.length==1){
			if($scope.config){
				return '/v/' + $scope.config[$scope.pagePath[0]].parent;
			}
		}else
		if($scope.pagePath.length<1){
			return '/';
		}
		var pp = $scope.pagePath.slice();
		pp.pop();
		var previousUrl = '/v/'+pp.toString().replace(',','/');
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

