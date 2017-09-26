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
		console.log(k+' / ');
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
		console.log(objToValidate);
		angular.forEach(objToValidate.data_support.validate.requiredField
			, function(v, k){
				if(v.requiredField){
				}else{
					$scope.config_all.validateField(k, objToValidate[key_data]
						, objToValidate.data_support.error, objToValidate);
				}
			}
		);
		console.log(objToValidate);
		return objToValidate.validToSave;
	}
	$scope.config_all.init = function(config_obj_key){
		var config_obj = $scope[config_obj_key]
		var obj_autoSave = config_obj['autoSave'];
		if(obj_autoSave){
			var init_autoSave = {
				change_count: 0
				,fn_change_count : function() { this.change_count++; }
				,save_count:0
				,fn_timeout_autoSave:null
				,fn_autoSave : function() {
//					console.log('--fn_auto_save-------------- ' + this.change_count);
					if(this.change_count > $scope.config_all.maxChangeForAutoSave){
						this.fn_httpSave();//call fn to save
						this.change_count = 0;
						this.save_count++;
					}
					$timeout.cancel(this.fn_timeout_autoSave);
					var aso = this;
					this.fn_timeout_autoSave = $timeout(function(){
//						console.log('fn_timeout_autoSave start');
						if(aso.change_count!=0){
							console.log('fn_timeout_autoSave is to save');
							aso.fn_httpSave();
							aso.change_count = 0;
							this.save_count++;
						}
					}, $scope.config_all.timeout.delay.autoSaveTextTypingPause);
				}
			};
			for (var name in init_autoSave) { obj_autoSave[name] = init_autoSave[name]; }
			var config_object_name = config_obj_key;
			var config_object = $scope[config_obj_key];
			$scope.$watch(config_obj_key + '.autoSave.change_count', function(newValue){
				console.log(config_obj_key + '.autoSave.change_count = ' + newValue);
//				$scope[obj_autoSave.config_object_name].autoSave.fn_autoSave();
				config_object.autoSave.fn_autoSave();
			});
		}
	}
	//autoSave block END
	

	$scope.fnPrincipal={
		db_role:{ }
		,dbRoles:null
		,dbRolesMap:{}
		,fn_readDbRoles:function(){
//			console.log(this.dbRoles);
			if(!this.dbRoles){
//				console.log();
				var thisObj = this;
				$http.get('/r/read_sql_with_param', {params:{sql:'sql.roles.select'}}
				).then(function(response) {
					thisObj.dbRoles = response.data.list;
//					console.log(thisObj.dbRoles);
					angular.forEach(thisObj.dbRoles, function(v, i){
						thisObj.dbRolesMap[v.role_id] = v;
					});
//					console.log(thisObj.dbRolesMap);
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
			if(!r_a)
				r_a='role_id';
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
		console.log(msp_id)
		$http.get('/r/read_msp/'+msp_id).then( function(response) {
			$scope.api__legal_entities = response.data.docbody;
			console.log($scope.api__legal_entities);
			$scope.mvpAddress.config.date.initDates();
			//$scope.closeMsp();
		});
		var url_employee = '/f/config/msp/employee.json';
		console.log(url_employee);
		$http.get(url_employee).then( function(response) {
			$scope.doc_employee = response.data;
			console.log($scope.doc_employee);
		});
		var url_declaration = '/f/config/msp/declaration.json';
		console.log(url_declaration);
		$http.get(url_declaration).then( function(response) {
			$scope.doc_declaration = response.data.data[0];
			var ad = $scope.doc_declaration.legal_entity.addresses[0];
			$scope.doc_declaration.person.address = JSON.parse(JSON.stringify(ad));
			console.log($scope.doc_declaration.person.address);
			$scope.doc_declaration.person.registry={};
			$scope.doc_declaration.person.registry.address = JSON.parse(JSON.stringify(ad));
			console.log($scope.doc_declaration.person.registry);
		});
		read_dictionaries($scope, $http);
	}
	
	read_principal($http, $scope);
}

function read_msp_list($http, $scope) {
	$http.get('/r/msp_list').then( function(response) {
		$scope.msp_list = response.data.msp_list;
		console.log($scope.msp_list);
	});
}

function read_principal($http, $scope, fn_o, fn_p) {
	console.log($scope.principal);
	if(!$scope.principal){
		console.log('/r/principal');
		$http.get('/r/principal').then(function(response) {
			if(!$scope.principal){
				$scope.principal = response.data;
				console.log($scope.principal);
			}
			console.log(fn_o);
			if(fn_o&&fn_p) fn_o[fn_p]();
		});
	}else{
		console.log('/r/principal непотрібно');
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
		return this[this.length - 1];
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
		var keys = Object.keys(this);
		return keys.length>0 && keys[0]!=0; 
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
