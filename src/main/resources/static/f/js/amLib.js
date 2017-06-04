function initAllAlgoritmed ($http, $scope){
	$scope.param = parameters;
	$scope.pagePath = window.location.href.split('?')[0].split('/').splice(4);
	if($scope.pagePath.last() && $scope.pagePath.last().length==0) $scope.pagePath.pop();
	$scope.highlight = function(text, search){
		if (!search) return text;
		return text.replace(new RegExp(search, 'gi'), '<span class="w3-yellow">$&</span>');
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

if (!Array.prototype.last){
	Array.prototype.isArray = function(){
		return Array.isArray(this);
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
