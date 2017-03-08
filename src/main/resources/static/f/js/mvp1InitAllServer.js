function initAllServer ($http, $scope){
	console.log('----initAllServer---------------');

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