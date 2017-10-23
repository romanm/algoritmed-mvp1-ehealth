function initMSPtest($http, $scope, $filter, $timeout, Blob){
	console.log('----initMSP-test---------------'+$scope.pagePath.last());
	if('registry' == $scope.pagePath.last()
	|| 'testMvpMedic' == $scope.pagePath.last()
	|| 'human-resources-department' == $scope.pagePath.last()
	|| 'moz-declaration-edit' == $scope.pagePath.last()
	|| 'moz-declaration' == $scope.pagePath.last()
	){
//		console.log('----initTestVariables---------------');
		initTestVariables($scope, $http, Blob);
		init_config_info($scope, $http);

		$scope.legal_entities = function () {
			console.log('----legal_entities-----Реєстрація----------');
			$scope.config_msp.legal_entities($scope.api__legal_entities);
		}
	}
	if('registry' == $scope.pagePath.last()){
		$scope.config_msp_all.admin_msp.dialogs.msp_data_form.fn_read_msp();
	}
}

initTestMvpCalendar = function($scope, $http, $filter, $timeout){
	$scope.basicCalendar = {
		dayPart:{
			list:['day','week','month','4day','termin']
			,itemNames_ua:['День','Неділя','Місяць','4 дні','Терміни']
			,item:'day'
		}
		,hoursOfWork:[]
		,daysOfWeek:[0,1,2,3,4,5,6]
		,monthWeek:[0,1,2,3,4]
		,todayDate:new Date()
		,getDateWithHour:function(h){
			return new Date(1,1,1,h)
		}
		,isToday:function(dt){
			var d = new Date(dt)
			return true
				&& d.getDate()==this.todayDate.getDate()
				&& d.getMonth()==this.todayDate.getMonth()
				&& d.getFullYear()==this.todayDate.getFullYear()
		}
		,dayOfMonthOfWeekMonth:function(w,d){
			var addDay = this.dayOfWeekMonth(w,d);
			var d1 = new Date(this.firstDateOfMonthFirstWeek());
			d1 = d1.setDate(d1.getDate() + addDay);
			return d1;
		}
		,dayOfWeekMonth:function(w,d){
			return w*this.daysOfWeek.last()+d+w;
		}
		,firstDateOfMonthFirstWeek:function(d){
			var d1 = this.firstDateOfMonth(d);
			d1 = d1.setDate(d1.getDate() - d1.getDay() + 1);
			return d1;
		}
		,firstDateOfMonth:function(d){
			if(!d) d = this.todayDate;
			return new Date(d.getFullYear(),d.getMonth(),1);
		}
		,init:function(){
			for (var i = 7; i < 24; i++) {
				this.hoursOfWork.push(i);
			}
		}
	};
	$scope.basicCalendar.todayTime = $scope.basicCalendar.todayDate.getTime();
	$scope.basicCalendar.init();
//	console.log($scope.basicCalendar);
}

initTestAddress = function($scope, $http, $filter){
	$scope.mvpAddress = {};
	$scope.mvpAddress.config = {
		listOpen:{}
		,date:{
			month_days:[
				[1,2,3,4,5,6,7]
				,[8,9,10,11,12,13,14]
				,[15,16,17,18,19,20,21]
				,[22,23,24,25,26,27,28]
				,[29,30,31]
			]
			,month3_names:{
				nominative:'січ_лют_бер_кві_тра_чер_лип_сер_вер_жов_лис_гру'.split('_')
			}
			,month_names:{
				nominative:'січень_лютий_березень_квітень_травень_червень_липень_серпень_вересень_жовтень_листопад_грудень'.split('_'),
				accusative:'січня_лютого_березня_квітня_травня_червня_липня_серпня_вересня_жовтня_листопада_грудня'.split('_')
			}
			,getMonth:function(o,p){
				var d = new Date(o['d2e_'+p]);
				return 1 + d.getMonth();
			}
			,getFullYear:function(o,p){
				var d = new Date(o['d2e_'+p]);
				return d.getFullYear();
			}
			,getDay:function(o,p){
				var d = new Date(o['d2e_'+p]);
				return d.getDate();
			}
			,initDates:function(){
//				$scope.api__legal_entities;
				//console.log($scope.api__legal_entities.owner);
				var thisObj = this;
				this.initDate($scope.api__legal_entities.owner, 'birth_date',thisObj);
				var msp = $scope.api__legal_entities.medical_service_provider;
				thisObj.initDate(msp.accreditation, 'issued_date',thisObj);
				thisObj.initDate(msp.accreditation, 'expiry_date',thisObj);
				thisObj.initDate(msp.accreditation, 'order_date',thisObj);
				angular.forEach(msp.licenses , function(v, k){
					thisObj.initDate(v, 'issued_date',thisObj);
					thisObj.initDate(v, 'expiry_date',thisObj);
					thisObj.initDate(v, 'active_from_date',thisObj);
				});
			}
			,initObjectDate:function(o,p){
				var thisObj = this;
				thisObj.initDate(o,p,thisObj);
				angular.forEach(o , function(v, k){
					if(k.indexOf('d2e_')>=0){
						if(k.indexOf(p)>=0){
						o[k]=null;
						}
					}
				});
				console.log(o)
			}
			,initDate:function(o,p,thisObj){
				if(!o[p]){
					var d = new Date();
					var n = d.toISOString();
					o[p] = n;
				}
				var dateObj = o[p];
				dateObj = dateObj.split('T')[0];
				var ymd = dateObj.split('-');
				var d = new Date(ymd[0],ymd[1]-1,ymd[2])
				thisObj.setDateParam(d,o,p);
			}
			,yearList:[]
			,initYearsLists:function(){
				this.yearList=[];
				var d = new Date(),
					y = d.getFullYear(),
					cy = y,
					maxOldYear = 100;
//				console.log(y);
				for (var i = 0; i < 5; i++)
					if((y-i)%5==0){
						cy = y-i;
						break;
					}
//				console.log(cy);
				var j=0, y5=[];
				for (var i = 0; i < maxOldYear; i++){
					if(j++==0) y5=[];
					y5.push(cy-i);
					if(j==5){
						j=0;
//						console.log(y5);
						this.yearList.push(y5);
					}
				}
//				console.log(this.yearList);
			}
			,changeYear:function(y,o,p){
				var dt = new Date(o['d2e_'+p]);
				dt.setFullYear(y);
				this.setDateParamAll(dt,o,p);
			}
			,changeMonth2:function(o,p){
				var dt = new Date(o['d2e_'+p]);
				dt.setMonth(o['d2e_month_'+p]);
				this.setDateParamAll(dt,o,p);
			}
			,changeMonth:function(m,o,p){
//				console.log(m);
				var dt = new Date(o['d2e_'+p]);
				dt.setMonth(m-1);
				this.setDateParamAll(dt,o,p);
			}
			,changeDay:function(d,o,p){
				console.log(d);
				console.log(p);
				console.log(o['d2e_'+p]);
				var dt = new Date(o['d2e_'+p]);
				console.log(dt);
				dt.setDate(d);
				console.log(dt);
				this.setDateParamAll(dt,o,p);
			}
			,setDateParamAll:function(d,o,p){
//				var dtt = d.getFullYear()+'-'+d.getMonth()+'-'+d.getDate();
//				var dtt = d.toLocaleFormat('%Y-%m-%d');
				var dtt = $filter('date')(d, 'yyyy-MM-dd');
				console.log(dtt);
				o[p] = dtt;
				this.setDateParam(d,o,p);
			}
			,setDateParam:function(d,o,p){
//				d.setDate(d.getDate() + 1);
//				console.log(d.getTime());
				o['d2e_'+p] = d.getTime();
				o['d2e_day_'+p] = d.getDate();
				o['d2e_month_'+p] = ''+d.getMonth();
				o['d2e_year_'+p] = d.getFullYear();
//				console.log(o);
			}
		}
		,edit:{
			dates_accreditation:['issued_date','expiry_date','order_date']
			,dates_license:['issued_date','expiry_date','active_from_date']
			,date_types:{
				issued_date:{p:'д.видачі.'}
				,expiry_date:{p:'д.закінчення.'}
				,order_date:{p:'д.замовлення.'}
				,active_from_date:{p:'активний з д.'}
			}
			,license:-1
			,openLicense:function(index){
				if(this.license==index) this.license=-1;
				else this.license=index;
			}
			,address:-1
			,openAddress:function(index){
				if(this.address==index) this.address=-1;
				else this.address=index;
			}
			,clickAddressArea:function(vNew, a){
				console.log(vNew);
				console.log(a);
				var keyToKey = {
					area:'name'
				}
				this.setAddressParamaters(vNew, a, keyToKey);
				$scope.mvpAddress.fn.seekInRegions = null;
			},setAddressParamaters:function(vNew, a, keyToKey){
				angular.forEach(keyToKey, function(v, k){
					console.log(k+'='+v+'/'+a[k]+'/'+vNew[v]);
					a[k] = vNew[v];
				});
			},clickAddress:function(vNew, a){
				/*
				'region':'region'
					,'district':'district'
				 * */
				console.log(vNew);
				var keyToKey = {
					'region':'region'
					,'settlement':'name'
					,'settlement_type':'type'
					,'settlement_id':'id'
					,'id':'id'
				}
				this.setAddressParamaters(vNew, a, keyToKey);
				console.log('------------------');
				console.log(a);
				/*
				var keysToChange = ['region', 'settlement', 'settlement_type', 'settlement_id'];
				console.log('------------------');
				angular.forEach(a, function(v, k){
					if(keysToChange.indexOf(k)>=0){
						console.log(k+'='+v+'/'+vNew[k]);
					}else{
						console.log(k+'='+v);
					}
				});
				console.log('------------------');
				angular.forEach(vNew, function(v, k){
					console.log(k+'='+v);
				});
				Жильбер Огюстан Николя
				 * */
			}
		}
		,fields_not_edit:['doctype', 'doc_id', 'parent_id', 'created', 'docbody_id', 'updated', 'update_sql']
		,isNotEditField:function(k){ return !(this.fields_not_edit.indexOf(k)>=0); }
		,upListElement:function(list, $index){
			var s_o = list.splice($index, 1)[0];
			list.splice(0 ,0 ,s_o);
		}
		,toMinusListElement:function(key, index){
			if(this['index_to_delete_'+key]==index){
				this['index_to_delete_'+key]=null;
			}else{
				this['index_to_delete_'+key]=index;
			}
		}
		,minusListElement:function(v, index, key){
			v.splice(index,1);
			if(this['index_to_delete_'+key]==index){
				this['index_to_delete_'+key]=null;
			}
		}
		,plusListElement:function(o, list_name){
			if(!o[list_name]) o[list_name]=[];
			var np = JSON.parse(JSON.stringify(this['template_'+list_name]));
			o[list_name].push(np)
		}
		,plusListElement2:function(v){
			var np = JSON.parse(JSON.stringify(v[0]));
			v.push(np)
		}
		,upPhone:function(v, $index){
			this.upListElement(v, $index);
		}
		,minusPhone:function(v, $index){
			v.splice($index,1);
		}
		,plusPhone:function(k,o){
			if(!o[k]) o[k]=[];
			var np = JSON.parse(JSON.stringify(this.phone_template));
			o[k].push(np)
		}
		,template_licenses:{}
		,template_specialities:{}
		,template_qualifications:{}
		,template_educations:{}
		,template_documents:{type:null, number:null}
		,phone_template:{type:"MOBILE", number:""}
		,phone_types:['LAND_LINE', 'MOBILE']
		,document_types:['PASSPORT']
		,street_types:['STREET']
		,address_types:['REGISTRATION','RESIDENCE']
		,employee_documents:['educations','qualifications','specialities']
		,employee_documents2:['educations','qualifications','specialities','science_degree']
		,accreditation_types:['FIRST','SECOND','THIRD']
		,type:{
			VILLAGE:'с.'
			,TOWNSHIP:'місте́чко'
			,CITY:'м.'
			,PASSPORT:'паспорт'
			,LAND_LINE:'тел.'
			,MOBILE:'моб.'
			,STREET:'вул.'
			,REGISTRATION:'регістрація'
			,RESIDENCE:'резіденція'
			,FIRST:'первинка'
			,SECOND:'вторинна'
			,THIRD:'тритинна'
			,educations:'освіта'
			,qualifications:'кваліфікація'
			,specialities:'спеціалізація'
			,science_degree:'наукова ступінь'
		}
		,initCCDictionaries:function(){
			console.log($scope.doc_dictionaries);
			console.log($scope.doc_dictionaries.data);
			$scope.doc_dictionaries.keys = {};
			angular.forEach($scope.doc_dictionaries.data, function(v, k){
				$scope.doc_dictionaries.keys[v.name] = k;
			});
//			console.log($scope.doc_dictionaries.response.keys);
			//console.log(this.selectDictionary('GENDER').values);
		}
		,selectDictionary:function(k){
			if(!$scope.doc_dictionaries)
				return ;
//			var i = $scope.doc_dictionaries.response.keys[k];
//			var v = $scope.doc_dictionaries.response.data[i];
			var i = $scope.doc_dictionaries.keys[k];
			var v = $scope.doc_dictionaries.data[i];
			return v;
		}
	};

	$scope.mvpAddress.config.date.initYearsLists();

	$scope.mvpAddress.data = {choose:{}, uri_prefix:'/r/gcc'};
	$scope.$watch('mvpAddress.fn.kveds.kvedToEdit', function(newValue){
		if(!newValue) return;
		console.log(newValue);
		$scope.api__legal_entities.kveds[$scope.mvpAddress.fn.kveds.index]
			= newValue;
	});
	$scope.$watch('mvpAddress.fn.seekInRegions', function(newValue){
		//console.log($scope.mvpAddress.data.regions);
		if(!newValue) return;
		console.log(newValue);
		console.log($scope.api__legal_entities.addresses[$scope.mvpAddress.config.edit.address]);
		var area = $scope.api__legal_entities.addresses[$scope.mvpAddress.config.edit.address].area;
		console.log(area);
		var url = $scope.mvpAddress.data.uri_prefix
		+'/uaddresses/settlements?name='+newValue;
//		if($scope.mvpAddress.data.region.name){
		if($scope.mvpAddress.data.region){
			console.log(1);
			url +='&region='+$scope.mvpAddress.data.region.name;
		}
//+'/uaddresses/search/settlements?name='+newValue+'&region='+area;
//		+'/uaddresses/search/settlements?settlement_name='+newValue+'&region='+area;
//		+'/uaddresses/search/settlements?settlement_name='+newValue+'&region=хмельницька';
		console.log(url);
		$http.get(url).then( function(response) {
			console.log(response.data.response.data);
			$scope.mvpAddress.config.seek_addresses = response.data.response.data;
			console.log($scope.mvpAddress.config.seek_addresses);
/*
			console.log($scope.mvpAddress.data);
			console.log($scope.mvpAddress.data.choose);
			var r = $scope.mvpAddress.fn.element.region();
			console.log(r);
			r.seek_addresses = response.data.response.data;
			console.log(r.seek_addresses);
 * */
		});
		$scope.mvpAddress.fn.list.regions();
	});
	
	$scope.mvpAddress.fn = {
		seekInRegions:null,
		kveds:{
			index:-1
			,kvedToEdit:null
			,minusKved:function(){
				console.log(this.index);
				console.log($scope.api__legal_entities.kveds);
				$scope.api__legal_entities.kveds.splice(this.index, 1);
			}
			,addKved:function(){
				$scope.api__legal_entities.kveds.push('');
				this.editLast();
			}
			,editLast:function(){
				this.openToEdit(
					$scope.api__legal_entities.kveds[this.index]
					,$scope.api__legal_entities.kveds.length - 1
				);
			}
			,openToEdit:function(kveds, index){
				this.kvedToEdit=kveds[index];
				this.index=index;
			}
		},
		element:{
			district:function(){
				var r = this.region();
				if(r)
					if($scope.mvpAddress.data.choose.region.district)
						return r.districts[$scope.mvpAddress.data.choose.region.district.index];
			},
			region:function(){
				if($scope.mvpAddress.data.choose)
					if($scope.mvpAddress.data.choose.region)
						if($scope.mvpAddress.data.choose.region.index)
							return $scope.mvpAddress.data.regions[$scope.mvpAddress.data.choose.region.index];
			}
		},
		list:{
			district:function(d){
				var r = $scope.mvpAddress.fn.element.region();
				if(!$scope.mvpAddress.data.choose.region.district)
					$scope.mvpAddress.data.choose.region.district = {};
				$scope.mvpAddress.data.choose.region.district.index
					= r.districts.indexOf(d)
			},
			districts:function(region){
				$scope.mvpAddress.data.region = region;
				var region_index = $scope.mvpAddress.data.regions.indexOf(region)
				if(!$scope.mvpAddress.data.choose.region)
					$scope.mvpAddress.data.choose.region = {};
				if($scope.mvpAddress.data.choose.region.index != region_index){
					$scope.mvpAddress.data.choose.region.index = region_index;
					delete $scope.mvpAddress.data.choose.region.district;
				}
				var url = '/r/gcc/uaddresses/details/region/'+region.id+'/districts';
				console.log(url+'  - '+!region.districts);
				if(!region.districts){
					$http.get(url).then( function(response) {
						region.districts = response.data.response.data;
					});
				}
			},
			regions:function(){
				$scope.mvpAddress.config.listOpen.regions
					= !$scope.mvpAddress.config.listOpen.regions;
				if(!$scope.mvpAddress.data.regions){
//					var url = '/r/gcc/uaddresses/search/regions';
					var url = '/r/gcc/uaddresses/regions?limit=30';
					console.log(url)
					$http.get(url).then( function(response) {
						$scope.mvpAddress.data.regions = response.data.response.data;
					});
				}else{
					console.log($scope.mvpAddress.data.regions);
				}
			}
		}
	};
}

read_dictionaries = function($scope, $http) {
	var url_dictionaries = '/f/config/msp/dictionaries.json';
	$http.get(url_dictionaries).then( function(response) {
		$scope.doc_dictionaries = response.data;
		$scope.mvpAddress.config.initCCDictionaries();
	});
}

init_config_info = function($scope, $http){
	read_dictionaries($scope, $http);
	$scope.commonDbRest = {
		read_sql_with_param:function(params,fn){
			console.log(params);
			$http.get('/r/read_sql_with_param', {params:params}).then(fn);
		}
		,update_sql_with_param:function(data,fn){
			//console.log(data);
			$http.post('/r/update_sql_with_param', data).then(fn);
		}
	}
	$scope.config_info = {
		is_msp_selected:function(msp){return this.is_o_selected('msp_table_selected', msp);}
		,read_msp_employee:function(msp_id){this.read_o('/r/read_msp_employee/'+msp_id, 'msp_employee');}
		,read_msp0_doctors:function(){
			if(!$scope.principal.user_msp || !$scope.principal.user_msp[0]) return;
			var msp_id = $scope.principal.user_msp[0].msp_id;
			$scope.commonDbRest.read_sql_with_param(
			{sql:'sql.medical.selectDoctorByMsp'
				, msp_id:$scope.principal.user_msp[0].msp_id
			},function(response){
				$scope.config_info.msp_doctors = response.data;
				//console.log($scope.config_info.msp_doctors);
			});
		}
		,click_msp:function(msp){
			this.click_o(
				'msp_table_selected', msp, '/r/read_msp_employee/'+msp.msp_id, 'msp_employee');
		}
		,is_msp_employee_selected:function(me){return this.is_o_selected('msp_employee_selected', me);}
		,run_with_principal:function(run_fn){
			if(!$scope.principal){
				$http.get('/r/principal').then(function(response) {
					if(!$scope.principal)
						$scope.principal = response.data;
					run_fn();
				});
			}else
				run_fn();
		}
		,show_o:function(o){
			console.log(o);
		}
		,click_msp_employee:function(msp_employee){
			this.click_o(
				'msp_employee_selected', msp_employee, '/r/read_docbody/'+msp_employee.person_id, 'msp_employee_doc');
			$scope.commonDbRest.read_sql_with_param(
			{sql:'sql.db1.user.msp'
				, user_id:msp_employee.person_id
			},function(response) {
				msp_employee.msps_ids = [];
				msp_employee.msps = response.data.list;
				angular.forEach(msp_employee.msps, function(v){
					msp_employee.msps_ids.push(v.msp_id)
				});
				console.log(msp_employee.msps_ids);
			});
		}
		,is_o_selected:function(this_o_selected_name, o){ return this[this_o_selected_name]==o;}
		,click_o:function(this_o_selected_name, o, url, read_o_name){
			//var thisO = this[this_o_name];
			if(this.is_o_selected(this_o_selected_name, o)){
				this[this_o_selected_name]=null;
				return;
			}
			this[this_o_selected_name]=o;
			this.read_o(url, read_o_name);
		}
		,afterRead_msp_employee_doc:function(){//msp_employee
			console.log(this.msp_employee_doc);
			var party = this.msp_employee_doc.party;
			party.last_name = party.family_name;
			$scope.doc_employee = this.msp_employee_doc;
			if($scope.doc_employee['keys_dates'])
				angular.forEach($scope.config_employee.keys_dates, function(v){
					if(!$scope.doc_employee.docbody[v]){
						console.log(v);
						var sd = new Date();
						$scope.doc_employee.docbody[v]=sd.getTime() ;
						$scope.mvpAddress.config.date.setDateParam(sd,$scope.doc_employee.docbody, v);
					}
				});
		}
		,read_o:function(url, read_o_name){
			var thisObj = this;
			console.log(url+' / '+read_o_name);
			$http.get(url).then(function(response){
				if(response.data.docbody)
					thisObj[read_o_name] = response.data.docbody;
				else
					thisObj[read_o_name] = response.data;
//				console.log(thisObj);
//				console.log(read_o_name);
//				console.log(thisObj[read_o_name]);
//				console.log('afterRead_'+read_o_name);
				if(thisObj['afterRead_'+read_o_name])
					thisObj['afterRead_'+read_o_name]();
//				console.log(read_o_name);
			});
			$scope.fnPrincipal.fn_readDbRoles();
}	}	}

init_info = function($scope, $http){
	read_msp_list($http, $scope);
	//init_config_info($scope, $http);
}

initTestVariables = function($scope, $http, Blob){
	//console.log(Blob);
	
	if(!$scope.config_msp)
		$scope.config_msp = {};

	//"edrpou": "38782323",
	$scope.config_msp = {
		menu_registry:{name:'data', step:'data'
			,steps:{
				data:{text:'Ввести дані',short:'Введеня даних'}
				,digitalsign:{text:'Накласти електроний підпис (ЕЦП)', short:'ЕЦП'}
				,registry:{text:'Відправити в центральний реєстр', short:'Реєстр'}
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
		, openGroup:function(k){
			if(!this.openedGroup)
				this.openedGroup = []
			if(this.openedGroup.indexOf(k)>-1){
				this.openedGroup.splice(this.openedGroup.indexOf(k), 1);
			}else{
				this.openedGroup.push(k);
			}
			if('kveds'==k){
				if($scope.mvpAddress.fn.kveds.index<0){
					$scope.mvpAddress.fn.kveds.editLast();
				}
			}
		}
		, getFieldName:function(k, kp1){
//			console.log(k)
			var translateObject = $scope.config_msp.registry_field_name_ua.api__legal_entities;
			if(kp1)
				translateObject = translateObject.children[kp1];
			var fn = translateObject[k];
			if(!fn)
				return k;
			fn = fn.substr(0,1).toUpperCase() + fn.substr(1);
			return fn;
		}
		, registryMspFileName:'registry_MSP_???'
		, setRegistryMspFileName:function(){
			this.registryMspFileName='registry_MSP_'+$scope.api__legal_entities.doc_id + '.json';
		}
		, mspToSave:function(){
			var a = document.createElement("a");
			document.body.appendChild(a);
			a.style = "display: none";
//			a.download = this.registryMspFileName();
			a.download = this.registryMspFileName;
			var dataJson = $scope.api__legal_entities;
			['doc_id','doctype','parent_id','created','docbody_id','updated'].forEach(function(k){
				delete dataJson[k]
			});
			var json = JSON.stringify(dataJson),
				blob = new Blob([json], {type: "octet/stream"}),
			url = window.URL.createObjectURL(blob);
			a.href = url;
			a.click();
			window.URL.revokeObjectURL(url);
			delete a;
//			'obj_path':['api__legal_entities'],
		}
		,autoSave:{
			fn_httpSave:function(){
				console.log('--autoSave.fn_httpSave--------------');
				$http.post('/r/saveMsp', $scope.api__legal_entities).then(function(response) {
					console.log(response.data);
					if(!$scope.api__legal_entities.doc_id){
						$scope.api__legal_entities.doc_id = response.data.doc_id
					}
				});
			}
		}
	}
	$scope.config_all.init('config_msp');
	$scope.config_personRegistry = {
		data:{
			template:{party:{}, doctor:{}}
			,error:{requiredField:{},party:{requiredField:{}}}
			,validate:{
				requiredField:{
					username:{}
					,password:{}
					,password_control:{}
					,party:{requiredField:{
						first_name:{}
						,last_name:{}
						,second_name:{}
					}}
				}
			}
		}
		,validToSave:true
		,fn_validField:function(k, data_template_o, error){
			console.log(k+' / ');
			if(!data_template_o[k]){
				$scope.config_personRegistry.validToSave = false;
				console.log(k+' / '+$scope.config_personRegistry.validToSave);
				error.requiredField[k]={empty:true};
			}else{
				error.requiredField[k]={empty:false};
			}
		}
		,autoSave:{
			fn_validToSave:function(){
				$scope.config_personRegistry.validToSave = true;
				angular.forEach($scope.config_personRegistry.data.validate.requiredField
				, function(v, k){
					if(v.requiredField){
						angular.forEach(v.requiredField, function(v2, k2){
							$scope.config_personRegistry.fn_validField(k2
								,$scope.config_personRegistry.data.template[k]
								,$scope.config_personRegistry.data.error[k]
							);
						});
					}else{
						$scope.config_personRegistry.fn_validField(k
							,$scope.config_personRegistry.data.template
							,$scope.config_personRegistry.data.error
						);
					}
				});
				console.log($scope.config_personRegistry.data.error);
				if($scope.config_personRegistry.data.template.password_control){
					if($scope.config_personRegistry.data.template.password_control
					==$scope.config_personRegistry.data.template.password){
						$scope.config_personRegistry.data.error.false_password_control = false;
					}else{
						$scope.config_personRegistry.data.error.false_password_control = true;
					}
				}
				if($scope.config_personRegistry.data.template.username){
					$http.post('/r/checkUsername'
					, $scope.config_personRegistry.data.template.username).then(function(response) {
						$scope.config_personRegistry.data.error.checkUsername = response.data;
					});
				}
				if($scope.config_personRegistry.validToSave){
					add_employee_info($scope.config_personRegistry.data.template.party
					, $scope.config_personRegistry.data.template);
				}
				console.log($scope.config_personRegistry.data.template);
				return $scope.config_personRegistry.validToSave;
			}
			,fn_httpSave:function(){
				console.log('--config_personRegistry.autoSave.fn_httpSave--------------');
				console.log($scope.config_personRegistry.data.template);
				if(this.fn_validToSave()){
					$http.post('/r/savePersonRegistry', $scope.config_personRegistry.data.template)
					.then(function(response) {
						console.log(response.data);
						if(!$scope.config_personRegistry.data.template.doc_id){
							$scope.config_personRegistry.data.template.doc_id = response.data.doc_id
						}
					});
				}
			}
		}
	}
	$scope.config_all.init('config_personRegistry');
	/*
	$scope.config_personregistry = {
		data_template:{party:{}}
		,autoSave:{
			fn_httpSave:function(){
				console.log('--config_personregistry.autoSave.fn_httpSave--------------');
				var data_template = $scope.config_personregistry.data_template;
				var validToSave = this.fn_validToSave(data_template);
				if(validToSave){
					$http.post('/r/savePersonRegistry', data_template).then(function(response) {
						console.log(response.data);
						if(!data_template.doc_id){
							data_template.doc_id = response.data.doc_id
						}
					});
				}
			}
			,requiredField:['username', 'password']
			,requiredPartField:['first_name','last_name','second_name']
			,error:{requiredField:{},requiredPartField:{}}
			,fn_validToSave:function(data){
				var validToSave = true;
				var thisError = this.error;
				angular.forEach(this.requiredField, function(v, i){
					console.log(v + ' / ' + data[v]);
					console.log(!data[v]);
					if(!data[v]){
						validToSave = false;
						thisError.requiredField[v]={empty:true};
					}else{
						thisError.requiredField[v]={empty:false};
					}
				});
				console.log(this.error);
				if(data.password_control){
					console.log(data.password_control);
					console.log(data.password);
					if(data.password_control==data.password){
						thisError.false_password_control = false;
					}else{
						thisError.false_password_control = true;
					}
				}
				if(data.username){
					$http.post('/r/checkUsername', data.username).then(function(response) {
						thisError.checkUsername = response.data;
						console.log(thisError);
					});
				}
				if(validToSave){
					add_employee_info(data, data);
				}
				console.log(data);
				return validToSave;
			}
		}
	}
	$scope.config_all.init('config_personregistry');
	 * */

	$scope.config_reception = {
		queue:{
			remove_from_queue:function($index){
				var params ={sql:'sql.queue.remove_from_queue'
					, doc_id:$scope.config_reception.queue_today[$index].doc_id
					, msp_id:$scope.principal.user_msp[0].msp_id
				};
				$scope.config_reception.queue.add_seek_queue_date_params(params, new Date());
				$scope.commonDbRest.update_sql_with_param(params ,$scope.config_reception.queue.fn_queue_today);
			} 
			,add_to_queue:function(patient,employee){
				var currentDate = new Date();
				var params = {sql:'sql.queue.add_to_queue'
					, lotOfNewIds:2
					, msp_id:$scope.principal.user_msp[0].msp_id
					, patient_id:patient.patient_id
					, employee_id:employee.person_id
					, begin_queue:currentDate
				}
				$scope.config_reception.queue.add_seek_queue_date_params(params, currentDate);
				$scope.commonDbRest.update_sql_with_param(params ,$scope.config_reception.queue.fn_queue_today);
			}
			,fn_queue_today:function(response){
				$scope.config_reception.queue_today = response.data.list3;
				$scope.mvpAddress.config['index_to_delete_queue']=null;
			}
			,add_seek_queue_date_params:function(params,date){
				params.begin_queue_year = date.getFullYear();
				params.begin_queue_month = 1 + date.getMonth();
				params.begin_queue_dayOfMonth = date.getDate();
			} 
		}
		,dialogs:{
			seek_patient:{
				name:'Пошук пацієнта'
			}
			,new_patient:{
				name:'Новий пацієнт'
				,save:function(){
					$scope.config_reception.autoSave.fn_httpSave();
				}
				,empty_form:function(){
					$scope.config_reception.patient_data={};
				}
			}
		}
		,fn_queue_today:function(){
			if(!$scope.principal.user_msp || !$scope.principal.user_msp[0]) return
			var params = {sql:'sql.queue.queue_today'
				, msp_id:$scope.principal.user_msp[0].msp_id
			};
			$scope.config_reception.queue.add_seek_queue_date_params(params, new Date());
			$scope.commonDbRest.read_sql_with_param(params
			,function(response){
				$scope.config_reception.queue_today = response.data.list;
			});
		}
		,fn_seek_patient:function(){
			//console.log(this.seek_patient);
			$scope.commonDbRest.read_sql_with_param(
			{sql:'sql.medical.seekMspPatient'
				, q:'%'+this.seek_patient+'%'
				, msp_id:$scope.principal.user_msp[0].msp_id
			},function(response) {
				$scope.config_reception.msp_patients = response.data;
				//console.log($scope.config_reception.msp_patients);
			});
		}
		,seek_msp_patients:function(){
			if(!$scope.principal.user_msp || !$scope.principal.user_msp[0]) return
			$scope.commonDbRest.read_sql_with_param(
			{sql:'sql.medical.selectPatientByMsp'
				,msp_id:$scope.principal.user_msp[0].msp_id
			},function(response) {
				$scope.config_reception.msp_patients = response.data;
				//console.log($scope.config_reception.msp_patients);
			});
		}
		,patient_data:{}
		,data_support:{
			error:{requiredField:{}}
			,validate:{
				requiredField:{
					last_name:{}
					,first_name:{}
					,second_name:{}
					,gender:{}
				}
			}
		}
		,validToSave:false
		,clickSave:function(){
			this.autoSave.fn_httpSave(true);
		}
		,autoSave:{
			fn_httpSave:function(clickSave){
				console.log('-- config_reception.autoSave.fn_httpSave --');
				if($scope.config_all.validate($scope.config_reception, 'patient_data')){
					$scope.config_reception.patient_data.clickSave=false;
					if(clickSave)
						$scope.config_reception.patient_data.clickSave=true;
					console.log($scope.principal.user_msp[0].doc_id);
					var patient_data = 
						$scope.config_reception.patient_data.addAllPropertyFrom({
							 msp_id:$scope.principal.user_msp[0].msp_id
							,patient_pib:$scope.config_reception.patient_data.last_name
							+' '+$scope.config_reception.patient_data.first_name
							+' '+$scope.config_reception.patient_data.second_name
						});
					console.log(patient_data);
					$http.post('/r/savePatient', patient_data).then(function(response) {
						console.log(response.data);
						console.log($scope.config_reception.patient_data);
						$scope.config_reception.patient_data=response.data;
						
					});
				}
			}
		}
	}
	$scope.config_all.init('config_reception');

	$scope.config_declaration = {
		read_declaration:function(){
			var physician_id = $scope.principal.user_id;
			if($scope.param.id && $scope.principal.user_id){
				console.log("read declaration if exist");
				console.log(physician_id);
				console.log($scope.param.id);
				$scope.commonDbRest.read_sql_with_param(
				{sql:'sql.declaration.read_declaration'
					, physician_id:$scope.principal.user_id
					, patient_id:$scope.param.id
				}, function(response) {
					$scope.db_doc_declaration = response.data;
					if($scope.db_doc_declaration.docbody){
						$scope.doc_declaration = $scope.db_doc_declaration.docbody.docbody;
						console.log($scope.doc_declaration);
					}else{//declaration not exist
						$scope.config_declaration.read_declaration_template();
					}
				});
			}else{
				$scope.config_declaration.read_declaration_template();
			}
		}
		,init_new_declaration:function(){
			console.log($scope.patientById);
			console.log($scope.patientById.docbody);
			$scope.doc_declaration = {person:$scope.patientById.docbody};
			console.log($scope.doc_declaration);
		}
		,read_declaration_template:function(){
			var url_declaration = '/f/config/msp/declaration.json';
			console.log(url_declaration);
			$http.get(url_declaration).then(function(response){
				console.log(response.data);
				return
				$scope.doc_declaration = response.data.data[0];
				var ad = $scope.doc_declaration.legal_entity.addresses[0];
				$scope.doc_declaration.person.address = JSON.parse(JSON.stringify(ad));
//				console.log($scope.doc_declaration.person.address);
				$scope.doc_declaration.person.registry={};
				$scope.doc_declaration.person.registry.address = JSON.parse(JSON.stringify(ad));
//				console.log($scope.doc_declaration.person.registry);
				console.log($scope.doc_declaration);
				console.log($scope.doc_declaration.person);
			});
			if(!$scope.patientById){
				$scope.$watch('patientById', function(newValue){
					if(!newValue) return;
					$scope.config_declaration.init_new_declaration();
				});
			}else{
				$scope.config_declaration.init_new_declaration();
			}
		}
		,autoSave:{
			fn_httpSave:function(){
				console.log('--config_declaration.autoSave.fn_httpSave--------------');
				console.log($scope.patientById);
				var data = {};
				console.log($scope.db_doc_declaration);
				console.log($scope.db_doc_declaration.docbody);
				if($scope.db_doc_declaration.docbody
				&& $scope.db_doc_declaration.docbody.declaration_id
				){
					data = { sql:		'sql.declaration.add_to_declaration'
						, docbodyMap:	$scope.doc_declaration
						, docbody_id:	$scope.db_doc_declaration.docbody.declaration_id
						, patient_id:	$scope.patientById.patient_id
						, physician_id:	$scope.principal.user_id
					};
				}else{//insert
					data = { sql:		'sql.declaration.insert_declaration'
						, docbodyMap:	$scope.doc_declaration
						, patient_id:	$scope.patientById.patient_id
						, physician_id:	$scope.principal.user_id
						, lotOfNewIds:	1
					};
				}
				console.log(data);
				//$http.post('/r/saveDeclaration', data).then(function(response) {
				$http.post('/r/update_sql_with_param', data).then(function(response) {
					console.log(response.data);
					console.log(response.data.nextDbId1);
				console.log($scope.db_doc_declaration);
					if(response.data.nextDbId1){
						$scope.db_doc_declaration.docbody = {declaration_id:response.data.nextDbId1}
					}
					console.log($scope.db_doc_declaration.docbody);
					console.log($scope.db_doc_declaration.docbody.declaration_id);
				});
			}
		}
	}
	$scope.config_all.init('config_declaration');

	$scope.config_employee = {
		keys_dates:['start_date','end_date','inserted_at','updated_at']
		,keys_dates_declaration:['start_date','end_date','signed_at']
		,dates_col:2
		,dates_name:{
			start_date:{title:'дата початку'}
			,end_date:{title:'дата закінчення'}
			,signed_at:{title:'дата підпису'}
			,inserted_at:{title:'час першого запису'}
			,updated_at:{title:'час оновлення'}
		}
		,list_index_specialities:-1
		,list_index_qualifications:-1
		,list_index_educations:-1
		,fn_opened_index:function(index, list_name){
			if(this['list_index_'+list_name]==index) this['list_index_'+list_name]=-1;
			else this['list_index_'+list_name]=index;
			console.log(this['list_index_'+list_name]);
		}
		,fn:{
			clickable_edit_date:{
				key_opened:'key of data to edit'
				,open:function(k, v){
					if(['start_date','end_date'].indexOf(k)>=0){
						if(this.key_opened==k)
							this.key_opened=null;
						else
							this.key_opened=k;
						if(this.key_opened==k){
							//init to edit
							var dateObj = $scope.mvpAddress.config.date;
							console.log(k);
							console.log(v);
							console.log(v[k]);
							console.log(dateObj);
							dateObj.initDate(v, k, dateObj);
						}
					}
				}
			} 
		}
		,autoSave:{
			fn_httpSave:function(){
				console.log('--config_employee.autoSave.fn_httpSave--------------');
				// employee_info - для "картотеки лікарів"
//				var docbodyData = $scope.doc_employee.data; 
				//var docbodyData = $scope.doc_employee.docbody; 
				var docbodyData = $scope.doc_employee; 
				add_employee_info(docbodyData.party, $scope.doc_employee);
				$http.post('/r/saveEmployee', $scope.doc_employee).then(function(response){
					console.log(response.data);
					if(!$scope.doc_employee.doc_id){
						$scope.doc_employee.doc_id = response.data.doc_id
					}
				});
			}
		}
	}
	$scope.config_all.init('config_employee');
	
	$scope.config_msp.registry_field_name_ua = {
		api__legal_entities:{
			name:'назва ГПМД'
			,short_name:'коротка назва'
			,public_name:'публічне ім´я'
			,type: "тип",
			owner_property_type: "тип власності",
			legal_form: "легальна форма",
			edrpou: "ЄДРПОУ",
			email: "еМайл",
			kveds: "КВЕДи",
			addresses: "Адреси",
			phones: "телефони",
			owner: "власник",
			medical_service_provider: "провайдер медичних послуг",
			security: "Інтернет",
			security1: "security?",
			public_offer: "публічна пропозиція",
			children:{
				addresses: {
					type: "тип",
					country: "країна",
					area: "обл.",
					region: "р-н.",
					settlement_type: "тип селеща",
					settlement: "назва",
					settlement_id: "код",
					street_type: "тип вулиці",
					street: "вулиця",
					building: "дім",
					apartment: "кв.",
					zip: "поштовий індекс"
				},
			}
		}
	}

	$scope.newMsp = function(){
		console.log('$scope.newMsp');
		$scope.api__legal_entities = $scope.tmp_api__legal_entities;
	}

	$scope.tmp4_api__legal_entities = 
	{
			  "name": "Хмельницький міський центр первинної медико-санітарної допомоги Nr.3",
			  "short_name": "КНП Хмельницький міський ЦПМСД Nr.3",
			  "public_name": "Хмельницький міський центр первинної медико-санітарної допомоги Nr.3",
			  "type": "MSP",
			  "owner_property_type": "STATE",
			  "legal_form": "140",
			  "edrpou": "1560503515",
			  "kveds": [
			    "86.10"
			  ],
			  "addresses": [
			    {
			      "type": "RESIDENCE",
			      "country": "UA",
			      "area": "ХМЕЛЬНИЦЬКА",
			      "region": "ХМЕЛЬНИЦЬКА",
			      "settlement": "ХМЕЛЬНИЦЬКИЙ",
			      "settlement_type": "CITY",
			      "settlement_id": "6810100000",
			      "street_type": "STREET",
			      "street": "вул. Гагаріна",
			      "building": "13",
			      "apartment": "44",
			      "zip": "29000"
			    }
			  ],
			  "phones": [
			    {
			      "type": "MOBILE",
			      "number": "+380976428677"
			    }
			  ],
			  "email": "algoritmed.info@gmail.com",
			  "owner": {
			    "first_name": "Федір",
			    "last_name": "Міщенко",
			    "second_name": "Пилипович",
			    "tax_id": "3015492565",
			    "birth_date": "1942-09-21",
			    "birth_place": "Хмельницький, Україна",
			    "gender": "MALE",
			    "email": "algoritmed.info@gmail.com",
			    "documents": [
			      {
			        "type": "PASSPORT",
			        "number": "120518"
			      }
			    ],
			    "phones": [
			      {
			        "type": "MOBILE",
			        "number": "+380976428677"
			      }
			    ],
			    "position": "Голова правління"
			  },
			  "medical_service_provider": {
			    "licenses": [
			      {
			        "license_number": "Lic",
			        "issued_by": "Кваліфікацйна комісія",
			        "issued_date": "2014-08-19",
			        "expiry_date": "2020-08-19",
			        "kved": "86.10",
			        "what_licensed": "реалізація наркотичних засобів"
			      }
			    ],
			    "accreditation": {
			      "category": "FIRST",
			      "issued_date": "2014-08-19",
			      "expiry_date": "2020-08-19",
			      "order_no": "fd123443",
			      "order_date": "2020-08-19"
			    }
			  },
			  "security": {
			    "redirect_uri": "https://google.com.ua"
			  },
			  "public_offer": {
			    "consent": true,
			    "consent_text": "Consent text"
			  }
			}
	$scope.tmp_api__legal_entities = 
	{ name:'' , short_name:'' , public_name:''
		, type:'', owner_property_type:'', legal_form:''
		, email:''
		, edrpou:''
		, kveds:[]
		, addresses:[ { type:'', country:'', area:'', region:'', settlement:'', settlement_type:'', settlement_id:''
		, street_type:'', street:'', building:'' } ]
		, phones:[ { type:'', number:'' } ]
		, owner:{ first_name:'', last_name:'', second_name:'', tax_id:''
		, birth_date:null, birth_place:'', gender:'', email:'', position:''
		, documents:[ { type:'', number:'' } ], phones:[ { type:'', number:'' } ] }
		, medical_service_provider:{ 
			licenses:[ { license_number:'', issued_by:'', issued_date:''
				, expiry_date:'', active_from_date:'', what_licensed:'' }
				, {license_number:'', issued_by:'', issued_date:'', expiry_date:''
				, active_from_date:'', what_licensed:'' } ]
			, accreditation:{ category:'', issued_date:'', expiry_date:'', order_no:'', order_date:'' } }
		, security:{ redirect_uri:'' }, public_offer:{ consent:true, consent_text:'' } 
	}
	$scope.tmp2_api__legal_entities = {
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

add_employee_info = function(party_o, target_o){
	party_o.family_name=party_o.last_name;
	var employee_info = 
		party_o.last_name
	+ ' ' +party_o.first_name
	+ ' ' +party_o.second_name
	+ ' (' +party_o.birth_date + ')';
//				$scope.doc_employee.party.last_name
//				+ ' ' +$scope.doc_employee.party.first_name
//				+ ' ' +$scope.doc_employee.party.second_name
//				+ ' (' +$scope.doc_employee.party.birth_date + ')';
	console.log(employee_info);
	target_o.employee_info = employee_info;
	console.log(target_o.employee_info);
}

}

