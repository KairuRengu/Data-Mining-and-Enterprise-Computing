angular.module('teamSelection', ['ngAnimate', 'ngSanitize', 'ui.bootstrap']);
angular.module('teamSelection').controller('playerController', function($scope, $http) {


  $scope.teamA = { name: "Raptors",
				   logo: "img/raptors.jpg",
				   players: ["Demar Derozen", "Kyle Lowry", "Jones Valaciunas", "Lucas Nogueira", "Pascal Siakam"],
				   picture: ["img/Demar-Derozen.jpg", "img/Kyle-Lowry.jpg", "img/Jones-Valanciunas.jpg", "img/Lucas-Nogueira.jpg", "img/Pascal-Siakam.jpg"],
				   number: [ "10", "7", "17", "92", "49"],
  };
  
  $scope.teamB = { name: "Bucks",
				   players: ["Giannis Antetokunmpo", "Jabari Parker", "Matthew Dellavedova", "Malcolm Brogdon", "Thon Maker"],
				   picture: ["img/Giannis-Antetokunmpo.jpg", "img/Jabari-Parker.jpg", "img/Matthew-Dellavedova.jpg", "img/Malcolm-Brogdon.jpg", "img/Thon-Maker.jpg"],
				   number: [ "34", "12", "8", "13", "14"],
  };
 
});