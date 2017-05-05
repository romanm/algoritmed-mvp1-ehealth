function expandAll (o, expandO){
	angular.forEach(expandO, function(oToExpand, key) {
		oToExpand.expand = o.expandAll;
	});
}

function initAll ($http, $scope){
	console.log('----initAll---------------');
	initAllAlgoritmed($http, $scope);
	initAllServer($http, $scope);
	$scope.saveProtocolDialog = function(){
//		console.log($scope.newProtocol);
		console.log($scope.protocol);
		var url = '/r/saveProtocol';
		console.log(url);
//		$http.post(url, $scope.newProtocol).then(
		$http.post(url, $scope.protocol).then(
			function(response) {
				console.log(response.data);
				var dbId = response.data.dbUuid.uuid_dbid;
				var url2 = '/v/protocol?dbId=' + dbId;
				console.log(url2);
				window.location.assign(url2)
//				window.open(url2);
				//window.open("https://www.w3schools.com");
			}
			, function(response) {
				console.log(response);
			}
		);
	}
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
		if($scope.param.dbId){
			var url = '/r/meddoc/dbProtocol/' + $scope.param.dbId;
			console.log(url);
			$http.get(url).then(
				function(response) {
					$scope.protocol = response.data;
					console.log($scope.protocol);
					initProtocol();
				}
				, function(response) {
					console.log(response);
				}
			);
		}else
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
	if('protocols' == $scope.pagePath.last()){
		console.log('----initAll------protocols---------');
		var url = '/r/meddoc/dbProtocolListe';
		$http.get(url).then(
			function(response) {
				console.log(response);
				console.log(response.data);
				$scope.dbProtocolListe = response.data.dbProtocolListe;
				console.log($scope.dbProtocolListe);
			}
			, function(response) {
				console.log(response);
			}
		);
		
		$scope.openNewProtocolDialog = function(){
			$scope.protocol = {"title":{"shortName":"","name":""}, "config":{"version":"0.0.1","menuWidth":3,"viewModel":"vm_0_0_1"} ,"process":{},"diagram_01":[]};
		}
	} else
	if('icd10' == $scope.pagePath.last()){
		console.log('----initAll----'+url+'-----------'+$scope.pagePath.last());
//		$scope.icd = icdUa.icd;
//		console.log($scope.icd);
//		var url = '/r/meddoc/icd';
		var url = '/f/mvp1/meddoc/db/icdUa.json';
		$http.get(url).then(
			function(response) {
				console.log(response.data);
				$scope.icd = response.data.icd;
				console.log($scope.icd);
			} , function(response) {
				console.log(response);
			}
		);
		/*
		 * */
	} else
	if('code' == $scope.pagePath.last() || 'icpc2' == $scope.pagePath.last()){
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
						response.data.openIcPc2SubGroup.demoIcpc2UaExclusion = 
							response.data.demoIcpc2UaExclusion;
						response.data.openIcPc2SubGroup.meddocIcpc2icd10Code = 
							response.data.meddocIcpc2icd10Code;
						$scope.codeItems[k2] = response.data.openIcPc2SubGroup;
//						$scope.codeItemsEn[k2] = response.data.openIcPc2SubGroupEn;
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
		if(!$scope.protocol.config.menuWidth){
			$scope.protocol.config.menuWidth = 3;
		}
		$scope.addDiagram01Element = function(objToEdit, k){
			var diagramElement = objToEdit[objToEdit.editKey.key];
			if(13 == k){
				diagramElement.push(
					{"class":"w3-row"
						,"childs":[
							{"class":"w3-third w3-container"}
							,{"class":"w3-twothird w3-container"}
						]
					}
				);
			}else
			if(14 == k){
				diagramElement.push(
					{"class":"w3-row"
						,"childs":[
							{"class":"w3-quarter"}
							,{"class":"w3-quarter"}
							,{"class":"w3-quarter"}
							,{"class":"w3-quarter"}
							]
					}
				);
			}
			console.log(diagramElement);
		}
		$scope.addProcessElement = function(objToEdit, k){
			$scope.protocol.process[$scope.amGenerateID[0]] 
				= {'type':'task','name':$scope.amGenerateID[0]};
			$scope.amGenerateID.splice(0,1);
			if($scope.amGenerateID.length < 2){
				$scope.getAmGenerateID();
			}
		}
		$scope.removeElement = function(objToEdit, k){
			var noRemoveList = "diagram_01,process,config,version,init,viewModel,menuWidth".split(",");
			if(noRemoveList.indexOf(k)>=0)
				return;
			objToEdit.splice(k,1);
		}
		
		$scope.addDiagram01Task = function(v, taskKey, editPath){
			console.log(v);
			console.log(taskKey);
			console.log(editPath);
			var	diagram01Element = {},
				listToAddElement = $scope.protocol.diagram_01
				elementToAdd = $scope.protocol.process[taskKey];
			console.log(elementToAdd);
			console.log(editPath.split(',').length);
			console.log(editPath.split(',').length>3);
			if(editPath.split(',').length > 3){// not diagram01 as parent
				if(!v.childs){
					v.childs = [];
				}
				listToAddElement = v.childs;
			}
			if('task' == elementToAdd.type){
				diagram01Element = {"ref":taskKey, "class":"w3-container w3-card-2 w3-center"};
			}
			console.log(diagram01Element);
			listToAddElement.push(diagram01Element);
			console.log(v);
		}
		$scope.getParentElement = function(editPath){
			var el = $scope, parentEl = el, parentKey = '';
			angular.forEach(editPath.split(','), function(value, key) {
				if(parentKey != 'childs')
					parentEl = el;
				el = el[value];
				parentKey = value;
			});
			return parentEl;
		}
		$scope.menuShow = function(o){
		}
		$scope.protocol.config.init = {'taskList':[]}
		angular.forEach($scope.protocol.process, function(value, key) {
			if('task' == value.type){
				$scope.protocol.config.init.taskList.push(key)
			}
		});
		console.log($scope.protocol);
		$scope.taskNumer = function(taskKey){
			return $scope.protocol.config.init.taskList.indexOf(taskKey) + 1;
		}
	};
	// initProtocol END

}
