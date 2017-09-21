function initAll ($http, $scope, $filter, $timeout, Blob){
	console.log('----initAll---------------');

	initAllAlgoritmed($http, $scope, $filter, $timeout);
//	console.log('----initAll---------------' + $scope.pagePath.last());
	
	var url = '/f/config/mvp1.algoritmed.medic.config.json';
//	console.log(url);
	$http.get(url).then( function(response) {
		initConfig($scope, response);
		$scope.menuHomeIndex = [];
		angular.forEach($scope.config, function(v, i){
			if(v.parent == 'home'){
				$scope.menuHomeIndex.push(i);
			}
		});
		//console.log($scope.config);
		initAllServer($http, $scope, $filter, $timeout);
	});
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

	$scope.config_msp_all={
		opened_dialog:'назва відкритого віконця діалогу'
		,fn_open_dialog:function(dialog_name){
			console.log(dialog_name);
			if(this.opened_dialog==dialog_name)
				this.opened_dialog='закрити';
			else
				this.opened_dialog=dialog_name
			if('personal_data'==this.opened_dialog){
				this.personal_page.dialogs.personal_data.open_dialog();
			}else
			if('doctors_cards'==this.opened_dialog){
				this.human_resources_department.dialogs.doctors_cards.open_dialog();
			}
		}
		,admin_msp:{
			dialogs:{
				msp_data_form:{
					name:'Данні ЛЗ'
					,fn_read_msp2:function(){
						if($scope.fnPrincipal.hasAdminMSPRole()){
							if($scope.principal.user_msp[0]){
								console.log($scope.principal.user_msp[0].msp_id);
								$scope.readMsp($scope.principal.user_msp[0].msp_id);
								console.log($scope.principal.user_msp[0].msp_id);
							}else{
								$scope.newMsp();
							}
						}else{
							console.log($scope.fnPrincipal);
							console.log($scope.fnPrincipal.hasAdminMSPRole());
							console.log($scope.principal);
						}
					}
					,fn_read_msp:function(){
						var thisObj = this;
						if($scope.param.doc_id){
							$scope.readMsp($scope.param.doc_id);
						}else if(!$scope.principal){
							read_principal($http, $scope, thisObj, 'fn_read_msp2');
						}
					}
				}
				,msp_signature:{
					name:'Реєстрація'
				}
				,capitatio_prognosis:{
					name:'Прогноз капітації'
				}
			}
		}
		,personal_page:{
			dialogs:{
				personal_area:{
					name:'Особистий майданчик'
					,seek_msp:null
					,msp_id_to_delete:null
					,fn_remove_msp:function(){
						console.log(this.msp_id_to_delete);
						var url = '/r/remove_employee_msp';
						console.log(url);
						$http.post(url, this.msp_id_to_delete).then( function(response) {
							console.log(response.data);
							read_principal($http, $scope);
						});
					}
					,fn_minus_msp:function(msp){
//						var answer = alert('Ви впевнені, що хочете виділити ЛПЗ');
						if(this.msp_id_to_delete==msp.doc_id){
							this.msp_id_to_delete=null;
						}else{
							this.msp_id_to_delete=msp.doc_id
						}
					}
					,fn_click_seek_msp:function(msp){
						console.log(msp);
						var url = '/r/add_employee_msp';
						console.log(url);
						$http.post(url, msp).then( function(response) {
							console.log(response.data);
							read_principal($http, $scope);
						});
					}
					,fn_seek_msp:function(){
						var url = '/r/seek_msp/'+this.seek_msp;
						$http.get(url).then( function(response) {
							$scope.seek_msp = response.data;
							console.log($scope.seek_msp);
						});
					}
				}
				,personal_data:{
					name:'Персональні данні'
					,open_dialog:function(){
						if(!$scope.principal || !$scope.principal.user)
							return;
						var person_id = $scope.principal.user.person_id; 
						console.log('personal_data '+ person_id);
						$scope.config_info.read_o('/r/read_docbody/'+person_id,'msp_employee_doc');
					}
				}
		}	}
		,human_resources_department:{
			mouseover_role:null
			,fn_i_allow_change_role:function(role){
				var myMaxRole = $scope.fnPrincipal.fn_myMaxRole();
				var allow_change_role = myMaxRole>=role.role_sort;
				return allow_change_role;
			}
			,fn_plus_role:function(e, role){
				console.log(role);
				console.log(role.role_id);
				this.fn_add_login(e, role.role_id);
			}
			,fn_minus_role:function(e, role){
				console.log(role);
				var url = '/r/update_sql_with_param';
				console.log(url);
				var data = {sql:'sql.db1.user_role.delete_by_username_and_role', username:e.username, role:role.role_id};
				console.log(data);
				$http.post(url, data).then( function(response) {
					console.log(response.data);
				});
			}
			,fn_add_login:function(e, role_id){
				var url = '/r/add_user_role';
				console.log(url);
				var data = {username:e.username, role_id:role_id};
				console.log(data);
				$http.post(url, data).then( function(response) {
					console.log(response.data);
				});
			}
			,extendRoles:false
			,fn_click_extendRoles:function(){
				this.extendRoles=!this.extendRoles;
//				console.log(this.extendRoles);
//				console.log($scope.fnPrincipal.dbRoles);
				if(!$scope.fnPrincipal.dbRoles){
					console.log();
					$scope.fnPrincipal.fn_readDbRoles();
				}
				var myMaxRole = $scope.fnPrincipal.fn_myMaxRole();
//				console.log(myMaxRole);

			}
			,fn_opened_card_name:function(){
				if(!$scope.config_info.msp_employee_doc)
					return;
//				var name = $scope.config_info.msp_employee_doc.docbody.party.last_name;
				var name = $scope.config_info.msp_employee_doc.party.last_name;
				return name;
			}
			,choose_user_msp:function(user_msp, modalDialogData){
				if(0==user_msp) return;
//				console.log(user_msp);
				var u_m = $scope.principal.user_msp.splice(user_msp,1);
				$scope.principal.user_msp.splice(0,0,u_m[0]);
				this.dialogs.doctors_cards.open_dialog();
//				$scope.config_all.modalDialog.close(modalDialogData.id)
			}
			,choose_user_without_msp:function(){
				$scope.config_info.msp_employee.users={};
				$http.get('/r/read_sql_with_param', {params:{sql:'sql.without_msp_employee.list'}}
				).then(function(response) {
					$scope.config_info.msp_employee.msp_employee_list=response.data.list;
					angular.forEach($scope.config_info.msp_employee.msp_employee_list, function(v, i){
						$scope.config_info.msp_employee.users[v.person_id]=i;
					});
					$http.get('/r/read_sql_with_param', {params:{sql:'sql.without_msp_employee.role.list'}}
					).then(function(response) {
						angular.forEach(response.data.list, function(v, i){
							var person_id_index = $scope.config_info.msp_employee.users[v.user_id];
							var person = $scope.config_info.msp_employee.msp_employee_list[person_id_index];
							if(!person.roles) person.roles=[];
							person.roles.push(v);
						});
						console.log($scope.config_info.msp_employee.msp_employee_list);
					});
				});

			}
			,dialogs:{
				doctors_cards:{
					name:'Картотека лікарів'
					,thead_names:{
						employee_id:{name:'Nr'}
						,employee_info:{name:'картка лікаря'}
					}
					,url:'/r/employee_list'
					,open_dialog:function(){
						if(!$scope.principal.user_msp[0]){
							return;
						}
						var msp_id = $scope.principal.user_msp[0].msp_id; 
						$scope.config_info.read_msp_employee(msp_id);
				}	}
				,new_doctor:{
					name:'Зареєструвати нового лікаря'
				}
				,opened_card:{
					name:'Відкрита картка:'
		}	}	}
		,registry_dialog_open:true
		,registry_msp_dialog_open:false
		,registry_doctor_dialog_open:false
		,registry_patient_dialog_open:true
		,msp_index:0
		,msp_list:[
			{msp_public_name:'цПМСД Nr1 "Поділля - Дубово"'}
			,{msp_public_name:'цПМСД  Nr2 "Поділля - Зарічанська"'}
			,{msp_public_name:'цПМСД "Поділля - Стара Синява"'}
		]
		,cabinet_part_index:0
		,cabinet_part_list:[
			{name:'Календар',url:'/v/testMvpCalendar'}
			,{name:'Амбулаторна картка',url:'/v/patient?id=3'}
			,{name:'Веденя хворого',url:'/v/patient?id=3&cabinet_part=protocol'}
		]
		,diagnostic_cabinet_list:[
			{name:'УЗД',cabinet_nr:12}
			,{name:'Рентген кабінет',cabinet_nr:15}
			,{name:'ЕКГ',cabinet_nr:17}
			,{name:'Лабораторія',cabinet_nr:25}
		]
		,doctor_index:0
		,doctor_list:[
			{name:'Раппопорт В.П. ',cabinet_nr:3}
			,{name:'Семашко М.О. ',cabinet_nr:8}
			,{name:'Боткін С.П. ',cabinet_nr:9}
		]
		,info:{
			login:{
				page_design:{
					head:{
						text:'Вхід'
						,suffix:'в програму'
						,text1:'Реєстрація'
						,suffix1:'в програмі'
					}
					
				}
				,fn_role:function(){
					/*
					var hrd = $scope.config_msp_all.human_resources_department;
					if(!hrd.dbRoles){
						console.log(hrd.dbRoles);
						console.log(this.role);
					}
					 * */
					return this.role;
				}
				,role:{
					ROLE_USER:{name:'Лікар',fns:['A','E','B','H']}
					,ROLE_REGISTRY_NURSE:{name:'м/с Реєстратури',fns:['A','H']}
					,ROLE_HEAD_HUMAN_RESOURCES:{name:'Зав.Кадрами',fns:['C','B','D']}
					,ROLE_ADMIN_MSP:{name:'Адмін. Лікарні',fns:['A','B','C','D','E','H']}
					,ROLE_HEAD_MSP:{name:'Гол.Лікар',fns:['C','B','D','I']}
					,ROLE_ADMIN_REGION:{name:'Адмін. Регіону',fns:['F']}
					,ROLE_ADMIN_APP:{name:'Адмін.програми',fns:['A','B','C','D','E','F','H','I']}
					,ROLE_WAITING_FOR_CONFIRMATION:{name:'Заявка на логін',fns:['J']}
				}
				,fns:{
					A:{name:'Заведеня хворого'}
					,B:{name:'Підписання декларації - eHealth'}
					,C:{name:'Реєстрація лікаря - eHealth'}
					,D:{name:'Реєстрація лікувального закладу - eHealth'}
					,E:{name:'Вести хворого'}
					,F:{name:'Підтвердження існування лікувального закладу'}
					,H:{name:'Запис пацієнта до лікаря'}
					,I:{name:'Звільненя лікаря'}
					,J:{name:'Очікування підтвердження доступу'}
				}
			}
		}
	};


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
		$http.get($scope.urlPatient1).then( function(response) {
			console.log(response.data);
			$scope.ehealthapi1.patient = response.data.data;
			console.log($scope.ehealthapi1.patient);
		});
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
	if('admin-msp' == $scope.pagePath.last()){
		initTestVariables($scope, $http, Blob);
		initTestAddress($scope, $http, $filter);
		$scope.config_msp_all.opened_dialog='msp_data_form';
		$scope.config_msp_all.admin_msp.dialogs.msp_data_form.fn_read_msp();
	}else
	if('personal-page' == $scope.pagePath.last()){
		initTestVariables($scope, $http, Blob);
		initTestAddress($scope, $http, $filter);
		init_config_info($scope, $http);
		$scope.config_msp_all.opened_dialog='personal_data';
//		$scope.config_msp_all.opened_dialog='personal_area';
		console.log($scope.config_msp_all.opened_dialog);
		if($scope.config_msp_all.opened_dialog=='personal_data'){
			console.log(2);
			$scope.config_msp_all.personal_page.dialogs.personal_data.open_dialog();
		}
	}else
	if('testMvpCalendar' == $scope.pagePath.last()){
		initTestMvpCalendar($scope, $http, $filter, $timeout);
	}else
	if('registry' == $scope.pagePath.last()
	||'human-resources-department' == $scope.pagePath.last()
	){
		initMSPtest($http, $scope, $filter, $timeout, Blob);
		initTestAddress($scope, $http, $filter);
	}else
	if('testMvpMedic' == $scope.pagePath.last()
	||'moz-declaration-edit' == $scope.pagePath.last()
	||'moz-declaration' == $scope.pagePath.last()
	){
		initMSPtest($http, $scope, $filter, $timeout, Blob);
	}else
	if('info' == $scope.pagePath.last()){
		console.log('----------info-------------- ');
		init_info($scope, $http);
		initTestAddress($scope, $http, $filter);
	}else
	if('testMvpAddress' == $scope.pagePath.last()){
		initTestAddress($scope, $http, $filter);
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
		var fnTimeoutAutoSaveHistory;
		autoSaveHistory = function (ph, rightSave){
			ph.docbody.autoSaveChangeCount=0;
			var url = '/r/autoSaveHistory';
			console.log(url +"/" + rightSave);
			$http.post(url, ph).then(function(response) {
				console.log(response.data);
				ph.docbody.autoSaveCount++;
				if(rightSave)
					ph.docbody.autoSaveCount=0;
			});
		}
		$scope.saveHistory = function (ph){ autoSaveHistory(ph, true); }
		$scope.autoSaveHistory = function (ph){
			if(!ph.docbody.autoSaveCount)
				ph.docbody.autoSaveCount=0;
			if(!ph.docbody.autoSaveChangeCount)
				ph.docbody.autoSaveChangeCount=0;
			ph.docbody.autoSaveChangeCount++;
			console.log(ph.docbody);
			console.log(ph.docbody.html);
			if(ph.docbody.autoSaveChangeCount>$scope.config_all.maxChangeForAutoSave){
				autoSaveHistory(ph);
			}
			$timeout.cancel(fnTimeoutAutoSaveHistory);
			fnTimeoutAutoSaveHistory = $timeout(function(){
				console.log('fnTimeoutAutoSaveHistory start');
				if(ph.docbody.autoSaveChangeCount!=0){
					console.log('fnTimeoutAutoSaveHistory is to save');
					autoSaveHistory(ph);
				}
			}, $scope.config_all.timeout.delay.autoSaveTextTypingPause);
		}
		$scope.addHistoryRecord = function (doctype, ph){
			var doctypeList = {'doctor':8,'ultrasound':9,'rentgen':10,'ECG':11}
			console.log(ph);
			console.log(ph.children);
			console.log(doctype);
			console.log(doctypeList);
			console.log(doctypeList[doctype]);
			console.log(!doctypeList[doctype]);
			if(doctypeList[doctype]){
				if(!ph.toSave)
					ph.toSave = {'doctype':doctypeList[doctype], 'docbody':{'html':''}, 'parent_id':ph.doc_id}
				console.log(ph.toSave);
				var url = '/r/addHistoryRecord';
				console.log(url);
				$http.post(url, ph.toSave).then(function(response) {
					if(!ph.children)
						ph.children = [];
					delete ph.toSave;
					ph.children.splice(0,0,response.data);
					console.log(ph);
					console.log(ph.children);
				});
			}
		}
		
		$scope.testDialog = function (ph){
			/*
			$scope.editPatientHistory = ph;
			if(!ph.docbody)
				ph.docbody = {'suspectedDiagnosis':[],'symptoms':[]};
			 * */
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
		$scope.newPatientHistoryRecord2 = function (){
			console.log("------newPatientHistoryRecord2-------------");
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
				$scope.patientById = response.data.patientById;
				console.log($scope.patientById);
			});
		}
	}else
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
	}else{
	}
	
	$scope.openUrl = function(url){
		console.log(url)
		window.location.href = url;
	}
	if($scope.param.doctor_index){
		$scope.config_msp_all.doctor_index = $scope.param.doctor_index;
		console.log($scope.config_msp_all);
	}

}
