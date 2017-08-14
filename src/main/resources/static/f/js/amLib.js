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
	$scope.config_all.init = function(config_obj_key){
		var config_obj = $scope[config_obj_key]
		console.log(config_obj);
		var obj_autoSave = config_obj['autoSave'];
		if(obj_autoSave){
			var init_autoSave = {
				change_count: 0
				,fn_change_count : function() { 
					console.log(this);
					this.change_count++; }
				,save_count:0
				,fn_timeout_autoSave:null
				,fn_autoSave : function() {
//					console.log('--fn_auto_save-------------- ' + this.change_count);
					if(this.change_count > $scope.config_all.maxChangeForAutoSave){
						this.fn_httpSave();
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
			console.log(config_obj_key);
			var config_object = $scope[config_obj_key];
			console.log(config_object);
			$scope.$watch(config_obj_key + '.autoSave.change_count', function(newValue){
				console.log(config_obj_key + '.autoSave.change_count = ' + newValue);
//				$scope[obj_autoSave.config_object_name].autoSave.fn_autoSave();
				config_object.autoSave.fn_autoSave();
			});
		}
	}
	//autoSave block END
	
	console.log('/r/principal');
	$http.get('/r/principal').then(function(response) {
		$scope.principal = response.data;
		console.log($scope.principal);
	});
	$scope.fnPrincipal={
		hasRole:function(r){
//			console.log(r);
			var hasRole = false;
			if($scope.principal.principal){
				angular.forEach($scope.principal.principal.authorities, function(value, index){
//				console.log(value);
					if(value.authority==r){
						hasRole = true;
					}
				});
			}
			return hasRole;
		}
	}
	
	//modal dialog open/close
	$scope.modalMspList = function (id_of_element) {
		document.getElementById(id_of_element).style.display='block';
		if('id01_msp_list'==id_of_element){
			$http.get('/r/msp_list').then( function(response) {
				$scope.msp_list = response.data.msp_list;
				console.log($scope.msp_list);
			});
		}
	}

	$scope.closeModalDialog = function (id_of_element) {
		document.getElementById(id_of_element).style.display='none';
	}

	//modal dialog open/close END
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
console.log(window.location)
var pageanchorStr = (''+window.location).split('!')[1];
if(!pageanchorStr){
	pageanchorStr = (''+window.location).split('#')[1];
}
console.log(pageanchorStr)
if(pageanchorStr){
	pageanchorStr = pageanchorStr.substr(1)
	angular.forEach(pageanchorStr.split("&"), function(value, index){
		var par = value.split("=");
		pageanchors[par[0]] = par[1];
	});
}
console.log(pageanchors)
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
