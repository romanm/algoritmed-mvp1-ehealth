function initAll ($http, $scope){
	console.log('----initAll---------------');

	$scope.pagePath = window.location.href.split('?')[0].split('/').splice(4);
	if($scope.pagePath.last() && $scope.pagePath.last().length==0) $scope.pagePath.pop();

	if('cabinet' == $scope.pagePath.last()){
		$scope.patient = {'patient_pib':'','patient_address':''};

		$scope.seekPatient = function (){
			console.log($scope.patient.patient_pib);
			console.log($scope.patient.patient_pib.length);
			if($scope.patient.patient_pib.length > 1){
				$http.get('/r/seekPatientFromInsurance/' + $scope.patient.patient_pib).then(
					function(response) {
						console.log(response);
//						$scope.dataInsurancePatients = response.data.insurancePatients;
						$scope.dataInsurancePatients = response.data;
						console.log($scope.dataInsurancePatients);
					}, function(response) {
						console.log(response);
					}
				);
			}
		}

		$http.get('/r/medical/patients').then(
			function(response) {
				$scope.medicPatients = response.data.medicPatients;
				console.log($scope.medicPatients);
			}
			, function(response) {
				console.log(response);
			}
		);
		
	}


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
		});

	// for menu colored
	$scope.menuHomeClicked = function(k){
		return k == $scope.pagePath[0];
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
 * */

