function initAllServer ($http, $scope){
	console.log('----initAllServer---------------');
	if('protocol' == $scope.pagePath.last()){
		console.log('----initAll-----------' + $scope.pagePath.last());
		$scope.openAddDialog = function(objToEdit, k){
			console.log(k);
			console.log(objToEdit[k]);
			console.log(objToEdit);
			console.log(Array.isArray(objToEdit));
			if(Array.isArray(objToEdit)){
				objToEdit[k].openAddDialog =
					!objToEdit[k].openAddDialog;
			}else{
				objToEdit.openAddDialog =
					(objToEdit.openAddDialog == k) ? "":k;
			}
		}
		$scope.menuWidthMinus = function(){
			if($scope.protocol.config.menuWidth>1)
				$scope.protocol.config.menuWidth--;
		}
		$scope.menuWidthPlus = function(){
			if($scope.protocol.config.menuWidth<(12/2))
				$scope.protocol.config.menuWidth++;
		}
		$scope.isEditKey = function(objToEdit, k){
			if(!objToEdit.editKey)
				return false;
			return objToEdit.editKey.key == k;
		}
		$scope.editObjPart = function(objToEdit, k){
			if($scope.isEditKey(objToEdit, k)){
				objToEdit.editKey = null;
			}else if('dbUuid' == k){
			}else if('viewModel' == k){
			}else{
				if(!objToEdit.editKey || !(typeof objToEdit.editKey === 'object'))
					objToEdit.editKey = {};
				objToEdit.editKey.key = k;
			}
		}
		$scope.menuType = function(o){
			if(Array.isArray(o))
				return 'list';
			if(typeof o === 'object')
				return 'object';
		}
	}
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
