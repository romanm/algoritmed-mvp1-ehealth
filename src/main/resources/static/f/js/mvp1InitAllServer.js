function eduTocken($scope){
//	console.log(localStorage);
	var TOKEN_KEY = "jwtToken";

	function setJwtToken(token) {
		localStorage.setItem(TOKEN_KEY, token);
	}

}

function initConfig($scope, response){
//	console.log('----initConfig---------------');
	eduTocken($scope)
	$scope.config = response.data;
	
	$scope.config.doctype = {'patientMenu':[9,10,11,8]
		,'keys':{
			'8':{'pathLast':'doctor','ukr':'Запис лікуючого лікаря'}
			,'9':{'pathLast':'ultrasound','ukr':'УЗД'}
			,'10':{'pathLast':'rentgen','ukr':'Рентген'}
			,'11':{'pathLast':'ECG','ukr':'ЕКГ'}
		}
	};
	//console.log($scope.config);
}

function initSeekAll($http, $scope, $filter, $timeout){
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
	$scope.$watch("icpc.codeMouseOverDropdown.child.id", function(newIcd10Long){
		if(newIcd10Long && newIcd10Long.length>0){
			var newIcd10 = newIcd10Long.split('.')[0];
			console.log(newIcd10);
			if(!$scope.icpc.icd10extension)
				$scope.icpc.icd10extension = {};
			if(!$scope.icpc.icd10extension[newIcd10]){
				var url = '/r/meddoc/icd10InIcpc2/' + newIcd10;
				console.log(url);
				$http.get(url).then(function(response) {
					$scope.icpc.icd10extension[newIcd10] = response.data;
					console.log($scope.icpc.icd10extension);
					console.log($scope.icpc.icd10extension[newIcd10]);
				});
			}
		}
	});
	var fnTimeoutIcpcCodeMouseOver;
	$scope.$watch("icpc.codeMouseOver", function(newIcpc2CodeValue){
		$timeout.cancel(fnTimeoutIcpcCodeMouseOver); //does nothing, if timeout alredy done
		fnTimeoutIcpcCodeMouseOver = $timeout(function(){ //Set timeout
			if(newIcpc2CodeValue && newIcpc2CodeValue.length>0){
				$scope.icpc.codeMouseOverDropdown = {'id':newIcpc2CodeValue};
				if(!$scope.icpc.extension)
					$scope.icpc.extension = {};
				var url = '/r/meddoc/icpc2CodeExtention/' + newIcpc2CodeValue;
				console.log(url);
				$http.get(url).then(function(response) {
					$scope.icpc.extension[newIcpc2CodeValue] = response.data;
					console.log($scope.icpc.extension);
					console.log($scope.icpc.extension[newIcpc2CodeValue]);
				});
			}
		},$scope.config_all.timeout.delay.seekMouseOver);
	});
	$scope.$watch("icdConf.icdSeekContent", function handleChange( newValue, oldValue ) {
		if('code'==newValue){
			$scope.seekIcdDb();
		}
	});
	$scope.$watch("icdConf.selectedItem", function handleChange( newValue, oldValue ) {
		if($scope.protocol && $scope.protocol.config.dd.openDatadictionaryDialog){
			console.log("------------------------------");	
			console.log($scope.protocol.dbUuid.uuid_dbid);
			newValue.protocolId = $scope.protocol.dbUuid.uuid_dbid;
			console.log(newValue);
			
			var url = '/r/addDataDictionary';
			console.log(url);
			$http.post(url, newValue).then(function(response) {
				console.log(response.data);
				$scope.protocol.fn.initDataDictionary(response.data);
			});
		}else
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
	$scope.seekIcpc2Db = function(){
		console.log($scope.icdConf);
		if($scope.icdConf.search.length>1){
			var url = '/r/meddoc/icpc2Code/'+$scope.icdConf.search;
			console.log(url);
			$http.get(url).then( function(response) {
				$scope.icpc2Db = response.data;
				$scope.icpc2Db.codeIndex = [];
				angular.forEach($scope.icpc2Db.meddocIcpc2CodeLimit, function(icpc2, key) {
					$scope.icpc2Db.codeIndex.push(icpc2.code)
				});
				console.log($scope.icpc2Db);
				console.log($scope.icpc2Db.codeIndex.indexOf('r03'.toUpperCase()));
			});
		}
	};
	$scope.seekIcdDb = function(){
		if($scope.icdConf.search.length>1){
			var url = '/r/meddoc/icdCode/'+$scope.icdConf.search;
			console.log(url);
			$http.get(url).then( function(response) {
				$scope.icdDb = response.data;
				console.log($scope.icdDb);
			});
		}
	};
}

function initAllServer($http, $scope, $filter, $timeout){
//	console.log('----initAllServer---------------');
	initSeekAll($http, $scope, $filter, $timeout);

	// for menu colored
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
