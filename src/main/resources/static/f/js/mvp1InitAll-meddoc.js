function expandAll (o, expandO){
	angular.forEach(expandO, function(oToExpand, key) {
		oToExpand.expand = o.expandAll;
	});
}

function initAll ($http, $scope){
	console.log('----initAll---------------');
	initAllAlgoritmed($http, $scope);
	initAllServer($http, $scope);
	if('protocol' == $scope.pagePath.last()){
		$scope.amGenerateID = [];
		$scope.getAmGenerateID = function(){
			$http.get('/r/meddoc/amGenerateID').then(
				function(response) {
					$scope.amGenerateID = $scope.amGenerateID.concat(response.data);
				}
				, function(response) {
					console.log(response);
				}
			);
		};
		$scope.getAmGenerateID();
		if($scope.param.hid){
			var url = '/f/mvp1/meddoc/db/protocol.'+$scope.param.hid+'.json';
			console.log(url);
			$http.get(url).then(
				function(response) {
					$scope.protocol = response.data;
					initProtocol();
				}
				, function(response) {
					console.log(response);
				}
			);
		}
		
	}
	else
	if('code' == $scope.pagePath.last()){
		console.log('----initAll------code---------');
//		"preferred":'переважно' ,
		$scope.codeItemsTitle = {
		"inclusion":'включно',
		"exclusion":'виключно' ,
		"icd10":'мкх10' ,
		"criteria":'критерії' ,
		"note":'примітки', 
		"consider":'розглядати' 
		};
		$scope.codeItems = {};
		$scope.codeItemsEn = {};
//		$scope.seekParam = '';
		$scope.openIcPc2SubGroup = function(k2){
			if($scope.codeItems[k2]){
				$scope.codeItems[k2].open = !$scope.codeItems[k2].open;
			}else{
				var url = '/r/meddoc/openIcPc2SubGroup/' + k2;
				console.log(url);
				$http.get(url).then(
					function(response) {
						console.log(response.data);
						$scope.codeItems[k2] = response.data.openIcPc2SubGroup;
						$scope.codeItemsEn[k2] = response.data.openIcPc2SubGroupEn;
						$scope.codeItems[k2].open = true;
						console.log($scope.codeItems[k2]);
					}
					, function(response) {
						console.log(response);
					}
				);
			}
		}
		$scope.isICPCCodeInSeek = function(k2,v2){
			var isInSeek = true;
			if(k2){
				isInSeek
					= v2.toLowerCase().indexOf($scope.icpc.seekParam.toLowerCase())>=0 
					|| k2.toLowerCase().indexOf($scope.icpc.seekParam.toLowerCase())>=0;
			}
			return isInSeek;
		}
		$scope.getColor = function(key){
			var color = 'green';
			angular.forEach($scope.icpc.color, function(value, colorKey) {
				if(value.codeList.indexOf(key) >= 0){
					color = colorKey;
				}
			});
			return color;
		}
		$scope.isShowPartColor = function(k2){
			var isInSeek = true;
			if($scope.icpc.showPartColor){
				var color = $scope.getColor(k2);
				if(!$scope.icpc.color[color].showPartColor)
					isInSeek = false;
				else
					isInSeek = $scope.icpc.color[color].showPartColor;
			}
			return isInSeek;
		}
		$scope.clickShowPartColor = function(color){
			console.log(color + '-showPartColor-'+$scope.icpc.showPartColor);
			if(color){
				var isOneColorPart = false;
				angular.forEach($scope.icpc.color, function(value, colorKey) {
					if(value.showPartColor){
						isOneColorPart = true;
					}
				});
				$scope.icpc.showPartColor = isOneColorPart;
			}else{
				if(!$scope.icpc.showPartColor){
					angular.forEach($scope.icpc.color, function(value, colorKey) {
						value.showPartColor = false;
					});
				}
			}
		}
		$scope.containsSeekParam = function(o){
			if(o && o.name)
			return o.name.indexOf($scope.seekParam);
		}
		$scope.icpc2Laguage = function(lg){
			var url = '/f/config/icpc2/ICPC2-'+lg+'.json';
			console.log(url);
			$http.get(url).then(
				function(response) {
					$scope.icpc = response.data;
					$scope.icpc.seekParam = '';
					$scope.icpc.expandAll = true;
					expandAll($scope.icpc, $scope.icpc.group);
					$scope.icpc.groupKeys = [];
					angular.forEach($scope.icpc.group, function(value, key) {
						this.push(key);
					}, $scope.icpc.groupKeys);
					$scope.icpc.groupKeys1 = $scope.icpc.groupKeys.splice(0,9)
					console.log($scope.icpc);
				}, function(response) {
					console.error(response);
				}
			);
		}
//		$scope.icpc2Laguage('en');
		$scope.icpc2Laguage('ua');
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

	// initProtocol 
	var initProtocol = function(){
		console.log('----initProtocol------------');
		$scope.protocol.init = {'taskList':[]}
		angular.forEach($scope.protocol.process, function(value, key) {
			if('task' == value.type){
				$scope.protocol.init.taskList.push(key)
			}
		});
		console.log($scope.protocol);
		$scope.taskNumer = function(taskKey){
			return $scope.protocol.init.taskList.indexOf(taskKey) + 1;
		}
	};
	// initProtocol END

}
