function initMSPtest ($http, $scope, $filter, $timeout, FileSaver, Blob){
	console.log('----initMSP-test---------------');
	if('testMvpMedic' == $scope.pagePath.last()){
//		console.log('----initTestVariables---------------');
		initTestVariables($scope, $http, FileSaver, Blob);
		if($scope.param.doc_id){
			$scope.readMsp($scope.param.doc_id);
		}else{
			$scope.newMsp();
		}

		$scope.legal_entities = function () {
			console.log('----legal_entities-----Реєстрація----------');
			$scope.config_msp.legal_entities($scope.api__legal_entities);
		}
	}
}

initTestVariables = function($scope, $http, FileSaver, Blob){
	console.log(FileSaver);
	console.log(Blob);
	var reader = new FileReader();
	console.log(reader);
	
	if(!$scope.config_msp)
		$scope.config_msp = {};
	//"edrpou": "38782323",
	$scope.config_msp = {
		'menu_registry':{'name':'data','step':'digitalsign'
			,'steps':{
				'data':{'text':'Ввести дані','short':'Введеня даних'}
				,'digitalsign':{'text':'Накласти електроний підпис (ЕЦП)','short':'ЕЦП'}
				,'registry':{'text':'Відправити в центральний реєстр','short':'Реєстр'}
			}
		}
		, legal_entities:function(data){
			$http.post('/r/legal_entities', data).then(function(response) {
				console.log("legal_entities-----Реєстрація OK");
				$scope.response__legal_entities = response.data;
				console.log($scope.response__legal_entities);
			});
		}
		, uploadFileChange:function(ele){
			console.log(ele.files[0]);
			var reader=new FileReader();
			reader.onload = function(e) {
				var p7sFile=e.target.result;
				var p7sObj = JSON.parse(p7sFile);
				console.log(p7sObj);
				$scope.config_msp.legal_entities(p7sObj);
			}
			reader.readAsText(ele.files[0]);
		}
		, uploadFileSrc:{'q':'1'}
		, registryMspFileName:function(){
			return 'registry_MSP_'+$scope.api__legal_entities.doc_id + '.json';
		}
		, mspToSave:function(){
			var fileName = this.registryMspFileName();
			console.log(fileName);
			var data = JSON.stringify($scope.api__legal_entities, null, '\t');
			var blob = new Blob([data], {type: 'text/json;charset=utf-8'});
			console.log(blob);
			FileSaver.saveAs(blob, fileName);
		}
		,'autoSave':{
			'obj_path':['api__legal_entities']
			,'config_object_name':'config_msp'
			,'fn_httpSave':function(){
				console.log('--config_msp.autoSave.fn_httpSave--------------');
				console.log(this);
				$http.post('/r/saveMsp', $scope.api__legal_entities).then(function(response) {
					console.log(response.data);
					if(!$scope.api__legal_entities.doc_id){
						$scope.api__legal_entities.doc_id = response.data.doc_id
					}
				});
			}
		}
	}
	$scope.config_all.init($scope.config_msp);

	$scope.config_msp.registry_field_name_ua = {
		'api__legal_entities':{
			'name':'назва ГПМД'
			,'short_name':'коротка назва'
			,'public_name':'публічна назва'
			,'public_name':'публічна назва'
			,"type": "тип",
			"owner_property_type": "тип власності",
			"legal_form": "легальна форма",
			"edrpou": "ЄДРПОУ",
		}
	}

	console.log($scope.config_msp);

	$scope.modalMspList = function () {
		document.getElementById('id01_msp_list').style.display='block';
		$http.get('/r/msp_list').then( function(response) {
			$scope.msp_list = response.data.msp_list;
			console.log($scope.msp_list);
		});
	}
	$scope.closeMsp = function (msp_id) {
		document.getElementById('id01_msp_list').style.display='none';
	}
	$scope.readMsp = function (msp_id) {
		console.log(msp_id)
		$http.get('/r/read_msp/'+msp_id).then( function(response) {
			$scope.api__legal_entities = response.data.docbody;
			console.log($scope.api__legal_entities);
			$scope.closeMsp();
		});
	}
	$scope.newMsp = function(){
		//$scope.api__legal_entities = angular.copy($scope.tmp_api__legal_entities);
		$scope.api__legal_entities = $scope.tmp_api__legal_entities;
		if(document.getElementById('id01_msp_list'))
			$scope.closeMsp();
	}
	$scope.tmp_api__legal_entities = {
		"name": "Hello World! - Клініка Адоніс",
		"short_name": "Адоніс",
		"public_name": "Адоніс",
		"type": "MSP",
		"owner_property_type": "STATE",
		"legal_form": "140",
		"edrpou": "1560503515",
		"kveds": ["86.10"],
		"addresses": [
			{
				"type": "RESIDENCE",
				"country": "UA",
				"area": "Житомирська",
				"region": "Бердичівський",
				"settlement": "Київ",
				"settlement_type": "CITY",
				"settlement_id": "43432432",
				"street_type": "STREET",
				"street": "вул. Ніжинська",
				"building": "15",
				"apartment": "23",
				"zip": "02090"
			}
			],
		"phones": [
			{
				"type": "MOBILE",
				"number": "+380503410870"
			}
		],
		"email": "m.miliaieva@gmail.com",
		"owner": {
			"first_name": "Петро",
			"last_name": "Іванов",
			"second_name": "Миколайович",
			"tax_id": "3015492565",
			"birth_date": "1991-08-19",
			"birth_place": "Вінниця, Україна",
			"gender": "MALE",
			"email": "m.miliaieva@gmail.com",
			"documents": [
				{
					"type": "PASSPORT",
					"number": "120518"
				}
				],
				"phones": [
					{
						"type": "MOBILE",
						"number": "+380503410870"
					}
					],
					"position": "Голова правління"
		},
		"medical_service_provider": {
			"licenses": [
				{
					"license_number": "fd123443",
					"issued_by": "Кваліфікацйна комісія",
					"issued_date": "1991-08-19",
					"expiry_date": "1991-08-19",
					"kved": "86.10",
					"what_licensed": "реалізація наркотичних засобів"
				}
				],
				"accreditation": {
					"category": "FIRST",
					"issued_date": "1991-08-19",
					"expiry_date": "1991-08-19",
					"order_no": "fd123443",
					"order_date": "1991-08-19"
				}
		},
		"security": {
			"redirect_uri": "redirect_uri"
		},
		"public_offer": {
			"consent": true,
			"consent_text": "Consent text"
		}
	};

}
