//angular.module('MyApp',['ngMaterial', 'ngMessages', 'material.svgAssetsCache'])
//.controller('AppCtrl', function ($scope, $timeout, $mdSidenav, $log) {
angular.module('MyApp',['ngMaterial', 'ngMessages'])
.controller('AppCtrl', function ($scope, $timeout, $mdSidenav, $log) {
    
    $scope.toggleRight = buildToggler('right');
    $scope.isOpenRight = function(){
      return $mdSidenav('right').isOpen()};
      $scope.regions = ('Львівська Хмельницька Рівненська Київська Одеська Тернопільська').split(' ').map(function(region) {
        return {abbrev: region};
      });
 
    function debounce(func, wait, context) {
      var timer;

      return function debounced() {
        var context = $scope,
            args = Array.prototype.slice.call(arguments);
        $timeout.cancel(timer);
        timer = $timeout(function() {
          timer = undefined;
          func.apply(context, args);
        }, wait || 10);
      };
    }

    function buildToggler(navID) {
      return function() {
        $mdSidenav(navID)
          .toggle() 
      };
    }
  })

  .controller('RightCtrl', function ($scope, $timeout, $mdSidenav, $log) {
    $scope.close = function () {
      $mdSidenav('right').close()
        .then(function () {
        });
    };
  });
