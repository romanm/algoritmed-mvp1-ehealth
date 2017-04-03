function initAll ($http, $scope){
	console.log('----initAll---------------');
	initAllAlgoritmed($http, $scope);
	initAllServer($http, $scope);
	if('protocol' == $scope.pagePath.last()){
		console.log($scope.param);
		if($scope.param.hid){
			var url = '/f/mvp1/meddoc/db/protocol.'+$scope.param.hid+'.json';
			console.log(url);
			$http.get(url).then(
					function(response) {
						console.log(response.data);
						$scope.protocol = response.data;
						console.log($scope.protocol);
					}
					, function(response) {
						console.log(response);
					}
			);
		}
		
	}
	else
	if('patient' == $scope.pagePath.last()){
		console.log($scope.param);
		if($scope.param.id){
			var url = '/r/medical/patient/'+$scope.param.id;
			console.log(url);
			$http.get(url).then(
				function(response) {
					console.log(response.data);
					$scope.patientById = response.data.patientById;
					console.log($scope.patientById);
				}
				, function(response) {
					console.log(response);
				}
			);
		}
	}
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

	$http.get('/f/config/mvp1.algoritmed.medic.config.json').then(
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

}
