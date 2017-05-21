function initAll ($http, $scope, $filter, $timeout){
	console.log('----initAll---------------');
	initAllAlgoritmed($http, $scope);
	initAllServer($http, $scope, $filter, $timeout);
	console.log('----initAll---------------' + $scope.pagePath.last());
	$scope.readCentralProtocols = function(){
		console.log("readCentralProtocols");
		var url = '/r/seekProtocolFromMeddoc/0';
		$http.get(url).then(
			function(response) {
				console.log(response.data);
				$scope.dbCentralProtocolListe = response.data.protocolBourse.dbProtocolListe;
				console.log($scope.dbCentralProtocolListe);
			}, function(response) {
				console.log(response);
			}
		);
	}
	if('testMvpPatient' == $scope.pagePath.last()){
		console.log('----initAll---------------' + $scope.pagePath.last());
		$scope.ehealthapi1 = {
				"keys":{
					"pip":"ПІП"
					,"birth_date":"дата народження"
					,"addresses":"Адреса"
				}
		};
		console.log($scope.ehealthapi1);
		$scope.urlPatient1 = 'https://private-anon-318b831b7e-ehealthapi1.apiary-mock.com/persons/1';
		console.log($scope.urlPatient1);
		$http.get($scope.urlPatient1).then(
			function(response) {
				console.log(response.data);
				$scope.ehealthapi1.patient = response.data.data;
				console.log($scope.ehealthapi1.patient);
			}
			, function(response) {
				console.log(response);
			}
		);
//		$scope.urlPatient1seek = 'https://private-anon-318b831b7e-ehealthapi1.apiary-mock.com/api/persons?first_name=%D0%86%D0%B2%D0%B0%D0%BD%D0%BE%D0%B2';
		$scope.urlPatient1seek = 'https://private-anon-318b831b7e-ehealthapi1.apiary-mock.com/api/persons?first_name=Петро';
		console.log($scope.urlPatient1seek);
		$http.get($scope.urlPatient1seek).then(
			function(response) {
				console.log(response.data);
				$scope.ehealthapi1.patientSeek = response.data.data;
				console.log($scope.ehealthapi1.patientSeek);
			}
			, function(response) {
				console.log(response);
			}
		);
		
	}else
	if('testMvpMedic' == $scope.pagePath.last()){
		console.log('----initAll---------------' + $scope.pagePath.last());
		
	}else
	if('protocol' == $scope.pagePath.last()){
	console.log('----initAll-----------' + $scope.pagePath.last());
		if($scope.param.cdbId){
			var url = '/r/central/dbProtocol/' + $scope.param.cdbId;
			console.log(url);
			$http.get(url).then(
				function(response) {
					$scope.protocol = response.data.protocol;
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
		$scope.autoSaveHistory = function (ph){
			console.log(ph.toSave);
		}
		$scope.addHistoryRecordType = function (doctype, ph){
			if(!ph.toSave)
				ph.toSave = {'doctype':doctype, 'docbody':''}
			console.log(ph);
			console.log(doctype);
		}
		$scope.testDialog = function (ph){
			$scope.editPatientHistory = ph;
			if(!ph.docbody)
				ph.docbody = {'suspectedDiagnosis':[],'symptoms':[]};
			console.log(ph)
		};
		$scope.savePatientHistoryRecord = function (ph){
			console.log(ph);
			var url = '/r/savePatientHistoryRecord';
			console.log(url);
			$http.post(url, ph).then( function(response) {
				console.log(response.data);
			});
		}
		$scope.removePatientHistoryRecord = function (ph){
			console.log(ph);
			var url = '/r/removePatientHistoryRecord';
			console.log(url);
			var dbSaveObj = {'doc_id':ph.doc_id};
			$http.post(url, dbSaveObj).then( function(response) {
//				console.log(response.data.numberOfDeletedRows);
				if(response.data.numberOfDeletedRows>0){
					var phIndex = $scope.patientById.children.indexOf(ph);
					console.log(phIndex);
					$scope.patientById.children.splice(phIndex,1)
				}
			});
		};
		$scope.translateUa = {
			'suspectedDiagnosis':'підозра на діагноз'
			,'symptoms':'симптоми'
		};
		$scope.editPatientHistoryField = function (ph, k){
			ph.editField=k;
			$scope.editPatientHistory = ph;
		}
		$scope.removeSuspectedDiagnosis = function (v,sds){
			v.splice(v.indexOf(sds),1);
		}
		$scope.newPatientHistoryRecord = function (){
			console.log($scope.param);
			var url = '/r/newPatientHistoryRecord';
			console.log(url);
			var dbSaveObj = {'patientId':$scope.param.id};
			console.log(dbSaveObj);
			$http.post(url, dbSaveObj).then( function(response) {
				response.data.newPatientHistoryRecord.docbody = {'suspectedDiagnosis':[],'symptoms':[]};
				if(!$scope.patientById.children)
					$scope.patientById.children = [];
				$scope.patientById.children.splice(0,0,response.data.newPatientHistoryRecord);
			});
		}
		if($scope.param.id){
			var url = '/r/medical/patient/'+$scope.param.id;
			console.log(url);
			$http.get(url).then( function(response) {
				console.log(response.data);
				$scope.patientById = response.data.patientById;
				console.log($scope.patientById);
			});
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

}
