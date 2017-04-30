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

console.log("Object.prototype.length");
console.log(Array.prototype.last);
if (!Array.prototype.last){
	console.log("Object.prototype.length");
	Object.prototype.length = function(){
		return Object.keys(this).length;
	}
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
