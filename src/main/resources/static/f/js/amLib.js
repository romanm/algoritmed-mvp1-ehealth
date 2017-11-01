function initAllAlgoritmed($http, $scope, $filter, $timeout){
	$scope.param = parameters;
	$scope.pagePath = window.location.href.split('?')[0].split('/').splice(4);
	if($scope.pagePath.last() && $scope.pagePath.last().length==0) $scope.pagePath.pop();

	$scope.highlight = function(text, search){
		if (!search) return text;
		return text.replace(new RegExp(search, 'gi'), '<span class="w3-yellow">$&</span>');
	}

	//autoSave block
	$scope.maxChangeForAutoSave=10;
	$scope.config_all = {'maxChangeForAutoSave':10};
	$scope.config_all.timeout = {'delay':{'seekMouseOver':780,'autoSaveTextTypingPause':2000}};
	$scope.config_all.modalDialog = {
		open:function(id_of_element){
			document.getElementById(id_of_element).style.display='block';
			if('id01_principal_msp_list'==id_of_element){
				console.log($scope.principal);
			}else
			if('id01_msp_list'==id_of_element){
				read_msp_list($http, $scope)
			}
		}
		,close:function(id_of_element){
			document.getElementById(id_of_element).style.display='none';
		}
	};
	$scope.config_all.validateField = function(k, data, error, objToValidate){
		if(!data[k]){
			objToValidate.validToSave = false;
			console.log(k+' / '+objToValidate.validToSave);
			error.requiredField[k]={empty:true};
		}else{
			error.requiredField[k]={empty:false};
		}
	}
	$scope.config_all.validate = function(objToValidate, key_data){
		objToValidate.validToSave = true;
		angular.forEach(objToValidate.data_support.validate.requiredField
			, function(v, k){
				if(v.requiredField){
				}else{
					$scope.config_all.validateField(k, objToValidate[key_data]
						, objToValidate.data_support.error, objToValidate);
				}
			}
		);
		return objToValidate.validToSave;
	}
	$scope.config_all.initObj = function(config_obj, config_obj_key){
		var obj_autoSave = config_obj['autoSave'];
		if(obj_autoSave){
			var init_autoSave = {
				change_count: 0
				,fn_change_count : function() { 
					this.change_count++; 
				}
				,save_count:0
				,fn_timeout_autoSave:null
				,fn_autoSave : function() {
//						console.log('--fn_auto_save-------------- ' + this.change_count);
					if(this.change_count > $scope.config_all.maxChangeForAutoSave){
						this.fn_httpSave();//call fn to save
						this.change_count = 0;
						this.save_count++;
					}
					$timeout.cancel(this.fn_timeout_autoSave);
					var aso = this;
					this.fn_timeout_autoSave = $timeout(function(){
						if(aso.change_count!=0){
							aso.fn_httpSave();
							aso.change_count = 0;
							this.save_count++;
						}
					}, $scope.config_all.timeout.delay.autoSaveTextTypingPause);
				}
			};
			for (var name in init_autoSave) { obj_autoSave[name] = init_autoSave[name]; }
//			var config_object_name = config_obj_key;
			//var config_object = $scope[config_obj_key];
			var config_object = config_obj;
			$scope.$watch(config_obj_key + '.autoSave.change_count', function(newValue){
//					console.log(config_obj_key + '.autoSave.change_count = ' + newValue);
//					$scope[obj_autoSave.config_object_name].autoSave.fn_autoSave();
				config_object.autoSave.fn_autoSave();
			});
		}
	}
	$scope.config_all.init = function(config_obj_key){
		var config_obj = $scope[config_obj_key]
		$scope.config_all.initObj(config_obj, config_obj_key);
	}
	//autoSave block END
	

	$scope.fnPrincipal={
		db_role:{ }
		,dbRoles:null
		,dbRolesMap:{}
		,fn_readDbRoles:function(){
			if(!this.dbRoles){
				var thisObj = this;
				$scope.commonDbRest.read_sql_with_param(
				{sql:'sql.roles.select'
				},function(response) {
					thisObj.dbRoles = response.data.list;
					angular.forEach(thisObj.dbRoles, function(v, i){
						thisObj.dbRolesMap[v.role_id] = v;
					});
					console.log(thisObj.dbRoles);
				});
			}
		}
		,myMaxRole:null
		,fn_myMaxRole:function(){
			if(!this.myMaxRole){
//				console.log(this.dbRolesMap);
				if(this.dbRolesMap){
					var thisObj = this;
//					console.log(this.dbRolesMap);
					this.myMaxRole = 0;
					angular.forEach($scope.principal.principal.authorities, function(v, i){
						var role_id = v.authority;
//						console.log(v.authority);
//						console.log(thisObj.dbRolesMap[v.authority]);
						var role_sort = thisObj.dbRolesMap[v.authority].role_sort;
//						console.log(role_sort);
						if(thisObj.myMaxRole<role_sort)
							thisObj.myMaxRole=role_sort;
					});
				}else{
					this.fn_readDbRoles();
				}
			}
			return this.myMaxRole;
		}
		,hasAdminMSPRole:function(){//доступ до створення MSP
			var hasHumanResourcesRole
			= this.hasRole('ROLE_HEAD_MSP')
			|| this.hasRole('ROLE_ADMIN_MSP')
			|| this.hasRole('ROLE_ADMIN_APP');
			return hasHumanResourcesRole;
		}
		,hasHumanResourcesRole:function(){//доступ до картотеки
			var hasHumanResourcesRole
			= this.hasRole('ROLE_HEAD_HUMAN_RESOURCES')
			|| this.hasRole('ROLE_HEAD_MSP')
			|| this.hasRole('ROLE_ADMIN_MSP')
			|| this.hasRole('ROLE_ADMIN_APP');
			return hasHumanResourcesRole;
		}
		,hasRole:function(r){
//			console.log(r);
			var hasRole = false;
			if($scope.principal && $scope.principal.principal){
				hasRole = this.hasLoginRole(r, $scope.principal.principal.authorities, 'authority');
				/*
				angular.forEach($scope.principal.principal.authorities, function(value, index){
//				console.log(value);
					if(value.authority==r){
						hasRole = true;
					}
				});
				 * */
			}
			return hasRole;
		}
		,hasLoginRole:function(r,r_o,r_a){
			if(!r_a) r_a='role_id';
			var hasRole = false;
			angular.forEach(r_o, function(value, index){
				if(value[r_a]==r){
					hasRole = true;
				}
			});
			return hasRole;
		}
	}
	
	//modal dialog open/close
	$scope.modalMspList = function (id_of_element) {
		document.getElementById(id_of_element).style.display='block';
		if('id01_msp_list'==id_of_element){
			read_msp_list($http, $scope)
		}
	}
	$scope.closeModalDialog = function (id_of_element) {
		document.getElementById(id_of_element).style.display='none';
	}

	//modal dialog open/close END

	$scope.readMsp = function (msp_id) {
//		console.log(msp_id)
		$scope.msp_divisions.selectByMsp(msp_id);
		$http.get('/r/read_msp/'+msp_id).then( function(response) {
			$scope.api__legal_entities = response.data.docbody;
			console.log('$scope.api__legal_entities');
//			console.log($scope.api__legal_entities);
			$scope.config_msp.setRegistryMspFileName();
//			console.log($scope.config_msp.registryMspFileName);
			$scope.mvpAddress.config.date.initDates();
			//$scope.closeMsp();
		});
//		var url_last_registry_error = '/f/tmp/response_'+msp_id+'.json';
		var url_last_registry_error = '/r/read_registry_response/'+msp_id;
		console.log(url_last_registry_error);
		$http.get(url_last_registry_error).then( function(response) {
			$scope.last_registry_error = response.data;
			$scope.last_registry_error.map = {};
			console.log($scope.last_registry_error);
			if($scope.last_registry_error.error)
			angular.forEach($scope.last_registry_error.error.invalid, function(v, i){
				var entity_path = v.entry.split('.');
//				console.log(entity_path)
//				console.log(entity_path[1]+'/'+entity_path[2])
				if(entity_path[2]){
					if(!$scope.last_registry_error.map[entity_path[1]]){
						$scope.last_registry_error.map[entity_path[1]] = {};
					}
					if(entity_path[3]){
						if(!$scope.last_registry_error.map[entity_path[1]][entity_path[2]]){
							$scope.last_registry_error.map[entity_path[1]][entity_path[2]] = {};
						}
						if(entity_path[4]){
//							console.log(entity_path[2]+'.'+entity_path[3]+'.'+entity_path[4])
							if(!$scope.last_registry_error.map[entity_path[1]][entity_path[2]][entity_path[3]]){
								$scope.last_registry_error.map[entity_path[1]][entity_path[2]][entity_path[3]] = {};
							}
							$scope.last_registry_error.map[entity_path[1]][entity_path[2]][entity_path[3]][entity_path[4]] = v;
						}else
							$scope.last_registry_error.map[entity_path[1]][entity_path[2]][entity_path[3]] = v;
					}else
							$scope.last_registry_error.map[entity_path[1]][entity_path[2]] = v;
				}else
							$scope.last_registry_error.map[entity_path[1]] = v;
			});
			console.log($scope.last_registry_error);
		});
		var url_employee = '/f/config/msp/employee.json';
		console.log(url_employee);
		$http.get(url_employee).then( function(response) {
			$scope.doc_employee = response.data;
			console.log($scope.doc_employee);
		});
		read_dictionaries($scope, $http);
	}
	
	//read_principal($http, $scope);
	initTestAddress($scope, $http, $filter);
	init_config_info($scope, $http);
	$scope.config_info.run_with_principal($scope.config_info.read_msp0_doctors);
}

function read_msp_list($http, $scope) {
	$http.get('/r/msp_list').then( function(response) {
		$scope.msp_list = response.data.msp_list;
		console.log($scope.msp_list);
	});
}

function read_principal($http, $scope, fn_o, fn_p) {
	if(!$scope.principal){
//		console.log('/r/principal');
		$http.get('/r/principal').then(function(response) {
			if(!$scope.principal){
				$scope.principal = response.data;
				console.log($scope.principal);
			}
			if(fn_o&&fn_p) fn_o[fn_p]();
		});
	}else{
		console.log('/r/principal непотрібно');
		if(fn_o&&fn_p) fn_o[fn_p]();
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
var pageanchors = {};
//console.log(window.location)
var pageanchorStr = (''+window.location).split('!')[1];
if(!pageanchorStr){
	pageanchorStr = (''+window.location).split('#')[1];
}
//console.log(pageanchorStr)
if(pageanchorStr){
	pageanchorStr = pageanchorStr.substr(1)
	angular.forEach(pageanchorStr.split("&"), function(value, index){
		var par = value.split("=");
		pageanchors[par[0]] = par[1];
	});
}
//console.log(pageanchors)
//console.log((''+window.location).split('!')[1].substr(1))

if (!Array.prototype.last){
	Array.prototype.isArray = function(){
		return Array.isArray(this);
	}
	Array.prototype.contains = function(k){
		return this.indexOf(k) > - 1;
	}
	Array.prototype.last = function(){
		var last_split1 = this[this.length - 1];
		if(last_split1){
			var last_split2 = last_split1.split('#')[0];
			return last_split2;
		}
		return;
	}
	Array.prototype.forLast = function(){
		return this[this.length - 2];
	}
	Array.prototype.forForLast = function(){
		return this[this.length - 3];
	}
	Object.prototype.isW3Row = function(){
		return this.class.indexOf('w3-row')>=0;
	}
	Object.prototype.addAllPropertyFrom = function(add_o){
		if(add_o){
			var thisObj = this;
			angular.forEach(add_o, function(v, k){
				thisObj[k] = v;
			});
		}
		return this;
	}
	Object.prototype.keys = function(){
		return Object.keys(this);
	}
	Object.prototype.isObject = function(){
//		return (typeof this)!='string' && Object.keys(this).length>0;
		var keys = Object.keys(this);
		return (typeof this)!='string' && keys.length>0 && keys[0]!=0; 
	}
}

var ALPHABET = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';

var ID_LENGTH = 4;

var amGenerateID = function() {
	var rtn = '';
	for (var i = 0; i < ID_LENGTH; i++) {
		rtn += ALPHABET.charAt(Math.floor(Math.random() * ALPHABET.length));
	}
	return rtn;
}
