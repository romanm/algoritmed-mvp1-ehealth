function initMSPtest($http, $scope, $filter, $timeout, Blob){
	console.log('----initMSP-test---------------'+$scope.pagePath.last());
	if('registry' == $scope.pagePath.last()
	||'testMvpMedic' == $scope.pagePath.last()
	){
//		console.log('----initTestVariables---------------');
		initTestVariables($scope, $http, Blob);
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

initTestMvpCalendar = function($scope, $http, $filter, $timeout){
	console.log('-----initTestMvpCalendar----------------');
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
	console.log($scope.basicCalendar);
}
initTestAddress = function($scope, $http){
	console.log('-----initTestAddress------------');
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
				$scope.api__legal_entities;
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
			,initDate:function(o,p,thisObj){
				var dateObj = o[p];
				dateObj = dateObj.split('T')[0];
				var ymd = dateObj.split('-');
				//console.log(ymd);
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
			,changeMonth:function(m,o,p){
//				console.log(m);
				var dt = new Date(o['d2e_'+p]);
				dt.setMonth(m-1);
				this.setDateParamAll(dt,o,p);
			}
			,changeDay:function(d,o,p){
				var dt = new Date(o['d2e_'+p]);
				dt.setDate(d);
				this.setDateParamAll(dt,o,p);
			}
			,setDateParamAll:function(d,o,p){
				var dtt = d.toLocaleFormat('%Y-%m-%d');
//				console.log(dtt);
				o[p] = dtt;
				this.setDateParam(d,o,p);
				$scope.config_msp.autoSave.fn_change_count();
			}
			,setDateParam:function(d,o,p){
//				d.setDate(d.getDate() + 1);
//				console.log(d.getTime());
				o['d2e_'+p] = d.getTime();
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
					'area':'name'
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
				var keyToKey = {
					'region':'district'
					,'settlement':'settlement_name'
					,'settlement_type':'type'
					,'settlement_id':'id'
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
				 * */
			}
		}
		,fields_not_edit:['doctype', 'doc_id', 'parent_id', 'created', 'docbody_id', 'updated']
		,isNotEditField:function(k){ return !(this.fields_not_edit.indexOf(k)>=0); }
		,minusPhone:function(v, index){
			v.splice(index,1);
			$scope.config_msp.autoSave.fn_change_count();
		}
		,plusPhone:function(v){
			var np = JSON.parse(JSON.stringify(v[0]));
			v.push(np)
			$scope.config_msp.autoSave.fn_change_count();
		}
		,phone_types:['LAND_LINE', 'MOBILE']
		,document_types:['PASSPORT']
		,street_types:['STREET']
		,address_types:['REGISTRATION','RESIDENCE']
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
		}
		,initCCDictionaries:function(){
//			console.log($scope.doc_dictionaries.response.data);
			$scope.doc_dictionaries.response.keys = {};
			angular.forEach($scope.doc_dictionaries.response.data, function(v, k){
				$scope.doc_dictionaries.response.keys[v.name] = k;
			});
//			console.log($scope.doc_dictionaries.response.keys);
			console.log(this.selectDictionary('GENDER').values);
		}
		,selectDictionary:function(k){
			var i = $scope.doc_dictionaries.response.keys[k];
			var v = $scope.doc_dictionaries.response.data[i];
			return v;
		}
	};

	$scope.mvpAddress.config.date.initYearsLists();

	$scope.mvpAddress.data = {choose:{}, uri_prefix:'/r/gcc'};
	/*
	 * */
	$scope.$watch('mvpAddress.fn.kveds.kvedToEdit', function(newValue){
		if(!newValue) return;
		console.log(newValue);
		$scope.api__legal_entities.kveds[$scope.mvpAddress.fn.kveds.index]
			= newValue;
	});
	$scope.$watch('mvpAddress.fn.seekInRegions', function(newValue){
		console.log($scope.mvpAddress.data.regions);
		if(!newValue) return;
		console.log(newValue);
		console.log($scope.api__legal_entities.addresses[$scope.mvpAddress.config.edit.address]);
		var area = $scope.api__legal_entities.addresses[$scope.mvpAddress.config.edit.address].area;
		console.log(area);
		var url = $scope.mvpAddress.data.uri_prefix
			+'/uaddresses/search/settlements?settlement_name='+newValue+'&region='+area;
//		+'/uaddresses/search/settlements?settlement_name='+newValue+'&region=хмельницька';
		console.log(url);
		$http.get(url).then( function(response) {
			console.log(response.data.response.data);
			$scope.mvpAddress.config.seek_addresses = response.data.response.data;
			console.log($scope.mvpAddress.config.seek_addresses);
			var r = $scope.mvpAddress.fn.element.region();
			console.log(r);
			r.seek_addresses = response.data.response.data;
			console.log(r.seek_addresses);
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
					var url = '/r/gcc/uaddresses/search/regions';
					console.log(url)
					$http.get(url).then( function(response) {
						$scope.mvpAddress.data.regions = response.data.response.data;
					});
				}
			}
		}
	};
}
initTestVariables = function($scope, $http, Blob){
	console.log(Blob);
	
	if(!$scope.config_msp)
		$scope.config_msp = {};

	//"edrpou": "38782323",
	$scope.config_msp = {
		'menu_registry':{'name':'data','step':'data'
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
			var translateObject = $scope.config_msp.registry_field_name_ua.api__legal_entities;
			if(kp1)
				translateObject = translateObject.children[kp1];
			var fn = translateObject[k];
			if(!fn)
				return k;
			fn = fn.substr(0,1).toUpperCase() + fn.substr(1);
			return fn;
		}
		, registryMspFileName:function(){
			return 'registry_MSP_'+$scope.api__legal_entities.doc_id + '.json';
		}
		, mspToSave:function(){
			var a = document.createElement("a");
			document.body.appendChild(a);
			a.style = "display: none";
			a.download = this.registryMspFileName();
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
			,'public_name':'публічне ім´я'
			,"type": "тип",
			"owner_property_type": "тип власності",
			"legal_form": "легальна форма",
			"edrpou": "ЄДРПОУ",
			"email": "еМайл",
			"kveds": "КВЕДи",
			"addresses": "Адреси",
			"phones": "телефони",
			"owner": "власник",
			"medical_service_provider": "провайдер медичних послуг",
			"security": "Інтернет",
			"security1": "security?",
			"public_offer": "публічна пропозиція",
			children:{
				"addresses": {
					"type": "тип",
					"country": "країна",
					"area": "обл.",
					"region": "р-н.",
					"settlement_type": "тип селеща",
					"settlement": "назва",
					"settlement_id": "код",
					"street_type": "тип вулиці",
					"street": "вулиця",
					"building": "дім",
					"apartment": "кв.",
					"zip": "поштовий індекс"
				},
			}
		}
	}

	console.log($scope.config_msp);
	$scope.config_employee = {
		keys_dates:['start_date','end_date','inserted_at','updated_at']
		,dates_col:2
		,dates_name:{
			start_date:{title:'дата початку'}
			,end_date:{title:'дата закінчення'}
			,inserted_at:{title:'час першого запису'}
			,updated_at:{title:'час оновлення'}
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
	}
	$scope.readMsp = function (msp_id) {
		console.log(msp_id)
		$http.get('/r/read_msp/'+msp_id).then( function(response) {
			$scope.api__legal_entities = response.data.docbody;
			console.log($scope.api__legal_entities);
			$scope.mvpAddress.config.date.initDates();
			$scope.closeMsp();
		});
		var url_employee = '/f/config/msp/employee.json';
		console.log(url_employee);
		$http.get(url_employee).then( function(response) {
			$scope.doc_employee = response.data;
			console.log($scope.doc_employee);
		});
		var url_dictionaries = '/f/config/msp/dictionaries.json';
		console.log(url_dictionaries);
		$http.get(url_dictionaries).then( function(response) {
			$scope.doc_dictionaries = response.data;
			$scope.mvpAddress.config.initCCDictionaries();
		});
	}
	$scope.newMsp = function(){
		//$scope.api__legal_entities = angular.copy($scope.tmp_api__legal_entities);
		$scope.api__legal_entities = $scope.tmp_api__legal_entities;
		if(document.getElementById('id01_msp_list'))
			$scope.closeMsp();
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
	{ "name":"КНП „Хмельницький міський центр первинної медико-санітарної допомоги Nr.3”"
		, "short_name":"КНП „Хмельницький міський ЦПМСД Nr.3”"
		, "public_name":"КНП „Хмельницький міський центр первинної медико-санітарної допомоги Nr.3”"
		, "type":"MSP", "owner_property_type":"PRIVATE", "legal_form":"150"
		, "edrpou":"1560503515"
		, "email":"algoritmed.info@gmail.com", "kveds":[ "86.10" ]
		, "addresses":[ { "type":"REGISTRATION", "country":"UA", "area":"ХМЕЛЬНИЦЬКА", "region":"ХМЕЛЬНИЦЬКА"
		, "settlement":"Хмельницький", "settlement_type":"CITY", "settlement_id":"cf312385-7788-4dde-ba22-f549462c17a0"
		, "street_type":"STREET", "street":"Гагаріна", "building":"13" }, { "type":"RESIDENCE", "country":"UA"
		, "area":"ХМЕЛЬНИЦЬКА", "region":"ХМЕЛЬНИЦЬКА", "settlement":"Хмельницький", "settlement_type":"CITY"
		, "settlement_id":"cf312385-7788-4dde-ba22-f549462c17a0", "street_type":"STREET", "street":"Гагаріна", "building":"34" } ]
		, "phones":[ { "type":"LAND_LINE", "number":"+380976428677" } ]
		, "owner":{ "first_name":"Федір", "last_name":"Міщенко", "second_name":"Пилипович", "tax_id":"1111111111"
		, "birth_date":"1942-09-21", "birth_place":"birth_place", "gender":"MALE", "email":"algoritmed.info@gmail.com", "position":"P3"
		, "documents":[ { "type":"PASSPORT", "number":"111" } ], "phones":[ { "type":"LAND_LINE", "number":"+380976428677" } ] }
		, "medical_service_provider":{ "licenses":[ { "license_number":"Lic", "issued_by":"issued_by", "issued_date":"2014-01-01"
		, "expiry_date":"2020-01-01", "active_from_date":"2020-01-01", "what_licensed":"what_licensed" }
		, { "license_number":"Lic2", "issued_by":"issued_by2", "issued_date":"2014-01-02", "expiry_date":"2020-01-02"
		, "active_from_date":"2020-01-02", "what_licensed":"what_licensed2" } ]
		, "accreditation":{ "category":"FIRST", "issued_date":"2014-01-03", "expiry_date":"2020-01-03", "order_no":"1235we", "order_date":"2020-01-03" } }
		, "security":{ "redirect_uri":"https://google.com.ua" }, "public_offer":{ "consent":true, "consent_text":"consent_text" } 
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

}
