function initAll ($http, $scope, $filter, $timeout, Blob){

	initAllAlgoritmed($http, $scope, $filter, $timeout);
	
	var url = '/f/config/mvp1.algoritmed.medic.config.json';
	$http.get(url).then( function(response) {
		initConfig($scope, response);
		$scope.menuHomeIndex = [];
		angular.forEach($scope.config, function(v, i){
			if(v.parent == 'home'){
				$scope.menuHomeIndex.push(i);
			}
		});
		initAllServer($http, $scope, $filter, $timeout);
	});
	$scope.readCentralProtocols = function(){
		console.log("readCentralProtocols");
		var url = $scope.security_prefix+'/seekProtocolFromMeddoc/0';
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

	$scope.msp_divisions={
		remove_division:function(divisionIndex){
			console.log(divisionIndex);
			console.log(this.divisions[divisionIndex].content);
			var data = { sql:'sql.doc.delete'
					, doc_id:this.divisions[divisionIndex].content.doc_id
				};
			console.log(data);
			$http.post($scope.security_prefix+'/update_sql_with_param', data).then(function(response) {
				console.log(response.data);
			});
			$scope.mvpAddress.config.minusListElement(this.divisions, divisionIndex, 'division');
		}
		,read_selectByMsp:function(msp_id){
			$scope.config_info.run_with_principal(function(){
				$scope.commonDbRest.read_sql_with_param(
					{sql:'sql.divisions.selectByMsp'
					, msp_id:msp_id
					},function(response) {
						//				console.log(response.data)
						$scope.msp_divisions.selectByMsp=response.data;
						//				console.log($scope.msp_divisions.selectByMsp)
						$scope.msp_divisions.selectByMsp.list.forEach(function(divisionFromDB){
							var divisionFromDB_content = JSON.parse(divisionFromDB.docbody);
							console.log(divisionFromDB_content);
							if(!divisionFromDB_content.doc_id)
								divisionFromDB_content.doc_id = divisionFromDB.docbody_id
								$scope.msp_divisions.addDivisionElement
								({
									content:divisionFromDB_content
								});
						});
					}, function(response){
						console.error(':( -- sql.divisions.selectByMsp')
					});
			});
		}
		,divisions:[]
		,addDivisionElement:function(divisionElement){
			divisionElement.autoSave = this.autoSave; 
			var config_obj_key = 'msp_divisions.divisions['+this.divisions.length+']';
			this.divisions.push(divisionElement);
			$scope.config_all.initObj(divisionElement, config_obj_key);
		}
		,plusDivisionElement:function(){
			this.addDivisionElement
			({
				content:{ addresses:[ { country:'UA'} ] }
			});
		}
		,autoSave:{
			fn_httpSave:function(clickSave){
				console.log('-- msp_divisions.autoSave.fn_httpSave --');
				var index_to_edit_division = $scope.mvpAddress.config.index_to_edit_division;
				console.log(index_to_edit_division);
				var divisionElement = $scope.msp_divisions.divisions[index_to_edit_division];
				console.log(divisionElement);
				console.log(divisionElement.content);
				console.log($scope.api__legal_entities);
				var data = {};
				if(divisionElement.content.doc_id){//update
					data = { sql:		'sql.docbody.update'
						, docbodyMap:	divisionElement.content
						, docbody_id:	divisionElement.content.doc_id
					};
				}else{//insert
					data = { sql:		'sql.division.insert_declaration'
						, docbodyMap:	divisionElement.content
						, msp_id:		$scope.api__legal_entities.doc_id
						, lotOfNewIds:	1
					};
				}
				console.log(data);
				$http.post($scope.security_prefix+'/update_sql_with_param', data).then(function(response) {
					console.log(response.data);
					if(!divisionElement.content.doc_id)
						divisionElement.content.doc_id=response.data.nextDbId1;
				});
			}
		}
	};
	$scope.config_msp_all={
		opened_dialog:'назва відкритого віконця діалогу'
		,fn_open_dialog:function(dialog_name){
			console.log(dialog_name);
			if(this.opened_dialog==dialog_name)
				this.opened_dialog='закрити';
			else
				this.opened_dialog=dialog_name;
			//after dialog open action
			if('personal_data'==this.opened_dialog){
				this.personal_page.dialogs.personal_data.open_dialog();
			}else
			if('seek_patient'==this.opened_dialog){
				console.log(dialog_name);
				$scope.config_reception.seek_msp_patients();
			}else
			if('msp_signature'==this.opened_dialog){
				$scope.config_msp.setRegistryMspFileName();
			}else
			if('doctors_cards'==this.opened_dialog){
				this.human_resources_department.dialogs.doctors_cards.open_dialog();
			}else
			if('msp_data_form'==$scope.config_msp_all.opened_dialog){
				console.log(1);
				$scope.config_msp_all.admin_msp.dialogs.msp_data_form.fn_read_msp();
				console.log(2);
			}
		}
		,admin_msp:{
			dialogs:{
				msp_data_form:{
					name:'Данні ЛЗ'
					,fn_read_msp2:function(){
//						console.log($scope.fnPrincipal.hasAdminMSPRole());
						if($scope.fnPrincipal.hasAdminMSPRole()){
							if($scope.principal.user_msp[0]){
//								console.log($scope.principal.user_msp[0].msp_id);
								$scope.readMsp($scope.principal.user_msp[0].msp_id);
							}else{
								$scope.newMsp();
							}
						}else{
//							console.log($scope.fnPrincipal);
//							console.log($scope.fnPrincipal.hasAdminMSPRole());
//							console.log($scope.principal);
						}
					}
					,fn_read_eh_api_divisions_msp:function(){
						console.log('/eh/api/divisions');
						$http.get('/eh/api/divisions').then(
						function(response) {
							console.log('/eh/api/divisions');
							$scope.eh_divisions = response.data.response;
							console.log($scope.eh_divisions);
							$scope.eh_divisions.division_ids = {};
							angular.forEach($scope.eh_divisions.data, function(v, i){
								$scope.eh_divisions.division_ids[v.external_id] = i;
							});
						},function(){
							console.error(': ---not ready----/eh/api/divisions-----')
						})
					}
					,fn_read_msp:function(){
						var thisObj = this;
						if($scope.param.doc_id){
							$scope.readMsp($scope.param.doc_id);
						}else{ 
							//if(!$scope.principal){
							read_principal($http, $scope, thisObj, 'fn_read_msp2');
						}
					}
				}
				,msp_signature:{
					name:'Реєстрація ЛЗ'
				}
				,msp_divisions:{
					name:'Підрозділи'
				}
				,capitatio_prognosis:{
					name:'Прогноз капітації'
				}
				,msp_declaration:{
					name:'Декларації'
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
						var url = $scope.security_prefix+'/remove_employee_msp';
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
					,fn_click_seek_msp:function(msp, add_o){
						var url = $scope.security_prefix+'/add_employee_msp';
						console.log(url);
						msp.addAllPropertyFrom(add_o)
						$http.post(url, msp).then( function(response) {
							console.log(response.data);
							read_principal($http, $scope);
						});
					}
					,fn_seek_msp:function(){
						var url = $scope.security_prefix+'/seek_msp/'+this.seek_msp;
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
						$scope.config_info.read_o($scope.security_prefix+'/read_docbody/'+person_id,'msp_employee_doc');
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
				var url = $scope.security_prefix+'/update_sql_with_param';
				console.log(url);
				var data = {sql:'sql.db1.user_role.delete_by_username_and_role', username:e.username, role:role.role_id};
				console.log(data);
				$http.post(url, data).then( function(response) {
					console.log(response.data);
				});
			}
			,fn_add_login:function(e, role_id){
				var url = $scope.security_prefix+'/add_user_role';
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
				$scope.config_info.msp_employee_readDbCount=0;
				$scope.commonDbRest.read_sql_with_param(
				{sql:'sql.without_msp_employee.list'
				},function(response) {
					$scope.config_info.msp_employee.msp_employee_list=response.data.list;
					$scope.config_info.msp_employee.readDbCount++;
				});
				$scope.commonDbRest.read_sql_with_param(
				{sql:'sql.without_msp_employee.role.list'
				},function(response) {
					$scope.config_info.msp_employee.msp_employee_role_list=response.data.list;
					$scope.config_info.msp_employee.readDbCount++;
				});
			}
			,dialogs:{
				doctors_cards:{
					name:'Картотека лікарів'
					,thead_names:{
						employee_id:{name:'Nr'}
						,employee_info:{name:'картка лікаря'}
					}
					,url:$scope.security_prefix+'/employee_list'
					,open_dialog:function(){
						if(!$scope.principal.user_msp
						|| !$scope.principal.user_msp[0]
						){
							return;
						}
						var msp_id = $scope.principal.user_msp[0].msp_id; 
						$scope.config_info.read_msp_employee(msp_id);
				}	}
				,new_doctor:{
					name:'Зареєструвати нового співробітника'
					,name2:'Зареєструвати нового лікаря'
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
		,cabinet_part_selected:function(cabinet_part_list){
			if('Календар'		==cabinet_part_list.name
			&& 'testMvpCalendar' == $scope.pagePath.last()
			) return true;
			else if('Веденя хворого'	==cabinet_part_list.name
			&& $scope.param.cabinet_part
			) return true;
			else if('Декларація'==cabinet_part_list.name
			&& 'moz-declaration-edit' == $scope.pagePath.last()
			) return true;
			else if('Амбулаторна картка'==cabinet_part_list.name
			&& 'patient' == $scope.pagePath.last()
			&& !$scope.param.cabinet_part
			) return true;
			return false;
		}
		,cabinet_part_list:[
			{name:'Календар'			,url:'/v/testMvpCalendar?'				,fa:'fa-calendar'}
			,{name:'Амбулаторна картка'	,url:'/v/patient?'						,fa:'fa-pencil'}
			,{name:'Веденя хворого'		,url:'/v/patient?cabinet_part=protocol'	,fa:'fa-stethoscope'}
			,{name:'Декларація'			,url:'/v/moz-declaration-edit?'			,fa:'fa-handshake-o'}
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
						,suffix:'в програму: medic.algoritmed.com'
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
					,ROLE_WAITING_FOR_CONFIRMATION:{name:'Заявка на користування програмою: medic.algoritmed.com',fns:['J']}
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

	$scope.config_info.msp_employee_readDbCount=0;
	$scope.$watch('config_info.msp_employee_readDbCount', function(newValue){
		if(newValue==2){
			console.log($scope.config_info.msp_employee.msp_employee_list);
			console.log($scope.config_info.msp_employee.msp_employee_role_list);
			angular.forEach($scope.config_info.msp_employee.msp_employee_list, function(v, i){
				$scope.config_info.msp_employee.users[v.person_id]=i;
			});
			angular.forEach($scope.config_info.msp_employee.msp_employee_role_list
			, function(v, i){
				var person_id_index = $scope.config_info.msp_employee.users[v.user_id];
				var person = $scope.config_info.msp_employee.msp_employee_list[person_id_index];
				if(!person.roles) person.roles=[];
				person.roles.push(v);
			});
			console.log($scope.config_info.msp_employee.msp_employee_list);
		}
	});


	console.log('---initAll---	' + $scope.pagePath.last());
	if('testMvpPatient' == $scope.pagePath.last()){
		$scope.ehealthapi1 = {
			keys:{
				pip:"ПІП"
				,birth_date:"дата народження"
				,addresses:"Адреса"
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
		$scope.config_msp_all.opened_dialog='msp_declaration';
		$scope.config_msp_all.opened_dialog='msp_signature';
		$scope.config_msp_all.opened_dialog='msp_data_form';
		$scope.config_msp_all.opened_dialog='msp_divisions';
		if($scope.config_msp_all.admin_msp.dialogs[hash]){
			$scope.config_msp_all.opened_dialog=hash;
		}
		if('msp_declaration'==$scope.config_msp_all.opened_dialog){
			$scope.$watch('principal.user_msp', function(newValue){ if(!newValue) return;
				//console.log($scope.principal.user_msp[0].msp_id);
				console.log($scope.principal);
				console.log($scope.principal.user_msp);
				$scope.commonDbRest.read_sql_with_param(
				{sql:'sql.declaration.all_patient_physician_declaration'
				},function(response) {
					$scope.config_msp.declaration_list = response.data.list;
					console.log($scope.config_msp.declaration_list);
				});
			});
		}else
		if('msp_signature'==$scope.config_msp_all.opened_dialog){
			$scope.config_msp_all.admin_msp.dialogs.msp_data_form.fn_read_msp();
		}else
		if('msp_divisions'==$scope.config_msp_all.opened_dialog){
			$scope.config_msp_all.admin_msp.dialogs.msp_data_form.fn_read_msp();
			$scope.config_msp_all.admin_msp.dialogs.msp_data_form.fn_read_eh_api_divisions_msp();
		}else
		if('msp_data_form'==$scope.config_msp_all.opened_dialog){
			$scope.config_msp_all.admin_msp.dialogs.msp_data_form.fn_read_msp();
		}
	}else
	if('reception' == $scope.pagePath.last()){
		initTestVariables($scope, $http, Blob);
		initTestMvpCalendar($scope, $http, $filter, $timeout);
//		$scope.config_msp_all.opened_dialog='new_patient';
		$scope.config_info.run_with_principal($scope.config_reception.seek_msp_patients);
		$scope.config_info.run_with_principal($scope.config_reception.fn_queue_today);
		$scope.mvpAddress.config.date.initObjectDate($scope.config_reception.patient_data, 'birth_date');
	}else
	if('personal-page' == $scope.pagePath.last()){
		initTestVariables($scope, $http, Blob);
		//init_config_info($scope, $http);
//		$scope.config_msp_all.opened_dialog='personal_data';
		$scope.config_msp_all.opened_dialog='personal_area';
		if($scope.config_msp_all.opened_dialog=='personal_data'){
			$scope.config_msp_all.personal_page.dialogs.personal_data.open_dialog();
		}
	}else
	if('testMvpCalendar' == $scope.pagePath.last()){
		initTestMvpCalendar($scope, $http, $filter, $timeout);
	}else
	if('registry' == $scope.pagePath.last()
	|| 'human-resources-department' == $scope.pagePath.last()
	){
		initMSPtest($http, $scope, $filter, $timeout, Blob);
//		$scope.config_msp_all.opened_dialog='new_doctor';
		$scope.config_msp_all.opened_dialog='doctors_cards';
		if('doctors_cards'==$scope.config_msp_all.opened_dialog){
			if(!$scope.principal){ 
				read_principal($http, $scope
				, $scope.config_msp_all.human_resources_department.dialogs.doctors_cards, 'open_dialog');
			}else{
				$scope.config_msp_all.human_resources_department.dialogs.doctors_cards.open_dialog();
			}
		}
	}else
	if('testMvpMedic' == $scope.pagePath.last()
	||'moz-declaration-edit' == $scope.pagePath.last()
	||'moz-declaration' == $scope.pagePath.last()
	){
		initMSPtest($http, $scope, $filter, $timeout, Blob);
		$scope.config_info.run_with_principal($scope.config_declaration.read_declaration);
	}else
	if('info' == $scope.pagePath.last()){
		console.log('----------info-------------- ');
		init_info($scope, $http);
	}else
	if('testMvpAddress' == $scope.pagePath.last()){
	}else
	if('protocol' == $scope.pagePath.last()){
	console.log('----initAll-----------' + $scope.pagePath.last());
		if($scope.param.cdbId){
			var url = $scope.security_prefix+'/central/dbProtocol/' + $scope.param.cdbId;
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
		
	} else if('patient' == $scope.pagePath.last()){
		var fnTimeoutAutoSaveHistory;
		autoSaveHistory = function (ph, rightSave){
			ph.docbody.autoSaveChangeCount=0;
			var url = $scope.security_prefix+'/autoSaveHistory';
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
				var url = $scope.security_prefix+'/addHistoryRecord';
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
			var url = $scope.security_prefix+'/savePatientHistoryRecord';
			console.log(url);
			$http.post(url, ph).then( function(response) {
				console.log(response.data);
			});
		}
		$scope.removePatientHistoryRecord = function (ph){
			console.log(ph);
			var url = $scope.security_prefix+'/removePatientHistoryRecord';
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
			var url = $scope.security_prefix+'/newPatientHistoryRecord';
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
	}else if('cabinet' == $scope.pagePath.last()){
		console.log($scope.param);
		if($scope.param.physician_id){
			$scope.commonDbRest.read_sql_with_param(
			{sql:			'sql.employee.byId'
			, employee_id:	$scope.param.physician_id
			},function(response) {
				console.log(response.data);
				$scope.physician=response.data.list[0];
				console.log($scope.physician);
			});
		}
		$scope.patient = {patient_pib:'',patient_address:''};
		$scope.seekPatient = function (){
			console.log($scope.patient.patient_pib);
			console.log($scope.patient.patient_pib.length);
			if($scope.patient.patient_pib.length > 1){
				$http.get($scope.security_prefix+'/seekPatientFromInsurance/' + $scope.patient.patient_pib).then(
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

		initTestMvpCalendar($scope, $http, $filter, $timeout);
		initTestVariables($scope, $http, Blob);
		$scope.config_info.run_with_principal($scope.config_reception.fn_queue_today);
		$scope.config_info.run_with_principal($scope.config_reception.seek_msp_patients);
		$scope.config_info.run_with_principal(function(){
			if(!$scope.principal.user){
				$http.get($scope.security_prefix+'/medical/patients').then(
				function(response) {
					$scope.medicPatients = response.data.medicPatients;
					console.log($scope.medicPatients);
				});
			}
		});
	}else{
	}
	
	console.log($scope.config_msp_all.opened_dialog);
	
	$scope.openUrl = function(url){
		console.log(url)
		window.location.href = url;
	}
	if($scope.param.doctor_index){
		$scope.config_msp_all.doctor_index = $scope.param.doctor_index;
		console.log($scope.config_msp_all);
	}

	if($scope.param.id){
		var url = $scope.security_prefix+'/medical/patient/'+$scope.param.id;
		console.log(url);
		$http.get(url).then( function(response) {
			$scope.patientById = response.data.patientById;
			console.log($scope.patientById);
			$scope.patientById.docbody = JSON.parse($scope.patientById.docbody);
		});
	}
}
