function initAll ($http, $scope){
	console.log('----initAll---------------');

	$scope.param = parameters;
	$scope.pagePath = window.location.href.split('?')[0].split('/').splice(4);
	if($scope.pagePath.last() && $scope.pagePath.last().length==0) $scope.pagePath.pop();

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

var parameters = {};
if(window.location.search){
//	$.each(window.location.search.split("?")[1].split("&"), function(index, value){
	angular.forEach(window.location.search.split("?")[1].split("&"), function(value, index){
		var par = value.split("=");
		parameters[par[0]] = par[1];
	});
}
