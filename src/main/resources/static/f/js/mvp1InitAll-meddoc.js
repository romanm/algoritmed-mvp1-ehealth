function expandAll (o, expandO){
	angular.forEach(expandO, function(oToExpand, key) {
		oToExpand.expand = o.expandAll;
	});
}

function initAll ($http, $scope){
	console.log('----initAll---------------');
	initAllServer($http, $scope);
	$scope.pagePath = window.location.href.split('?')[0].split('/').splice(4);
	if($scope.pagePath.last() && $scope.pagePath.last().length==0) $scope.pagePath.pop();
	if('code' == $scope.pagePath.last()){
		console.log('----initAll------code---------');
//		$scope.seekParam = '';
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

