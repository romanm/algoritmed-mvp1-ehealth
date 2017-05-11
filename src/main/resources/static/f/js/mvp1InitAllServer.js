function initSeekAll($http, $scope, $filter){
	$scope.icdConf = {search:'','icdSeekContent':'code'};
//		$scope.icdConf = {search:'','icdSeekContent':'tree'};
	$scope.seekIcdPopup = function(){
		if($scope.icdConf.icdSeekContent=='code'){
			$scope.seekIcdDb()
		}
	};
	$scope.calcFilteredChilds = function(o){
		var fc = $filter('filter')(o.childs, $scope.icdConf.search)
		if(fc)
			return fc.length;
	};
	$scope.isOpenedChilds = function(itemO){
		var isOpenedChilds = itemO.childs && itemO.open;
		if(!isOpenedChilds){
			if(typeof itemO.open == "undefined"){
				if($scope.icdConf.search.length >= 2){
					var calcFilteredChilds = $scope.calcFilteredChilds(itemO);
					if( calcFilteredChilds == 0)
						isOpenedChilds = false;
					else
					if(calcFilteredChilds <= 5)
						isOpenedChilds = true;
				}
			}
		}
		return isOpenedChilds;
	};
	$scope.$watch("icdConf.selectedItem", function handleChange( newValue, oldValue ) {
		console.log(newValue);
		if($scope.editPatientHistory){
			console.log($scope.editPatientHistory);
			if(newValue.icd_id){
				$scope.editPatientHistory.docbody.suspectedDiagnosis.splice(0,0,{
					'icd_id':newValue.icd_id
					,'icd_code':newValue.icd_code
					,'icd_name':newValue.icd_name
				});
			}
			console.log($scope.editPatientHistory.docbody);
		}
	});
	$scope.clickIcdItem = function(item){
		console.log(item);
		if(item.icd_code.indexOf('-')<0){
			$scope.icdConf.selectedItem = item;
			var url = '/r/meddoc/icdChildren/' + item.icd_id;
			$http.get(url).then( function(response) {
				item.children = response.data.icdChildren;
				console.log(item.children);
			});
		}
	}
	$scope.readIcdJson = function(){
		if(!$scope.icd){
	//		var url = '/r/meddoc/icd';
			var url = '/f/mvp1/meddoc/db/icdUa.json';
			$http.get(url).then(function(response){
				$scope.icd = response.data.icd;
			});
		}
	};
	$scope.seekIcdDb = function(){
		console.log($scope.icdConf.search);
		if($scope.icdConf.search.length>1){
			var url = '/r/meddoc/icdCode/'+$scope.icdConf.search;
			console.log(url);
			$http.get(url).then(
				function(response) {
					$scope.icdDb = response.data;
					console.log($scope.icdDb);
				} , function(response) {
					console.log(response);
				}
			);
		}
	};
}

function initAllServer($http, $scope, $filter){
	console.log('----initAllServer---------------');
	initSeekAll($http, $scope, $filter);

	// for menu colored
	console.log('for menu colored');
	$scope.menuHomeClicked = function(k){
		var menuHomeClicked = k == $scope.pagePath.last();
		if(!menuHomeClicked){
			var lastPage = $scope.config[$scope.pagePath.last()];
			if(lastPage)
				menuHomeClicked = k == lastPage.parent;
		}
		return menuHomeClicked;
	}

	if('protocol' == $scope.pagePath.last()){
		console.log('----initAll-----------' + $scope.pagePath.last());
		$scope.openAddDialog = function(objToEdit, k){
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

/*
if (!Array.prototype.last){
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
 * */
