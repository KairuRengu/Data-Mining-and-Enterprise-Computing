(function(angular) {
  'use strict';
angular.module('httpExample',  ['ngAnimate', 'ngSanitize', 'ui.bootstrap'])
  .config(['$sceDelegateProvider', function($sceDelegateProvider) {
    // We must whitelist the JSONP endpoint that we are using to show that we trust it
    $sceDelegateProvider.resourceUrlWhitelist([
      'self',
      'https://angularjs.org/**'
    ]);
  }])
  .controller('FetchController', ['$scope', '$http', '$templateCache',
    function($scope, $http) {
     var _selected;

  $scope.selected = undefined;

  $scope.ngModelOptionsSelected = function(value) {
    if (arguments.length) {
      _selected = value;
    } else {
      return _selected;
    }
  };

  $scope.modelOptions = {
    debounce: {
      default: 500,
      blur: 250
    },
    getterSetter: true
  };
  
  $scope.teams = [ {
    "teamId": 1610612737,
    "abbreviation": "ATL",
    "teamName": "Atlanta Hawks",
    "simpleName": "Hawks",
    "location": "Atlanta"
  },{
    "teamId": 1610612738,
    "abbreviation": "BOS",
    "teamName": "Boston Celtics",
    "simpleName": "Celtics",
    "location": "Boston"
  },
  {
    "teamId": 1610612751,
    "abbreviation": "BKN",
    "teamName": "Brooklyn Nets",
    "simpleName": "Nets",
    "location": "Brooklyn"
  },
  {
    "teamId": 1610612766,
    "abbreviation": "CHA",
    "teamName": "Charlotte Hornets",
    "simpleName": "Hornets",
    "location": "Charlotte"
  },
  {
    "teamId": 1610612741,
    "abbreviation": "CHI",
    "teamName": "Chicago Bulls",
    "simpleName": "Bulls",
    "location": "Chicago"
  },
  {
    "teamId": 1610612739,
    "abbreviation": "CLE",
    "teamName": "Cleveland Cavaliers",
    "simpleName": "Cavaliers",
    "location": "Cleveland"
  },
  {
    "teamId": 1610612742,
    "abbreviation": "DAL",
    "teamName": "Dallas Mavericks",
    "simpleName": "Mavericks",
    "location": "Dallas"
  },
  {
    "teamId": 1610612743,
    "abbreviation": "DEN",
    "teamName": "Denver Nuggets",
    "simpleName": "Nuggets",
    "location": "Denver"
  },
  {
    "teamId": 1610612765,
    "abbreviation": "DET",
    "teamName": "Detroit Pistons",
    "simpleName": "Pistons",
    "location": "Detroit"
  },
  {
    "teamId": 1610612744,
    "abbreviation": "GSW",
    "teamName": "Golden State Warriors",
    "simpleName": "Warriors",
    "location": "Golden State"
  },
  {
    "teamId": 1610612745,
    "abbreviation": "HOU",
    "teamName": "Houston Rockets",
    "simpleName": "Rockets",
    "location": "Houston"
  },
  {
    "teamId": 1610612754,
    "abbreviation": "IND",
    "teamName": "Indiana Pacers",
    "simpleName": "Pacers",
    "location": "Indiana"
  },
  {
    "teamId": 1610612746,
    "abbreviation": "LAC",
    "teamName": "Los Angeles Clippers",
    "simpleName": "Clippers",
    "location": "Los Angeles"
  },
  {
    "teamId": 1610612747,
    "abbreviation": "LAL",
    "teamName": "Los Angeles Lakers",
    "simpleName": "Lakers",
    "location": "Los Angeles"
  },
  {
    "teamId": 1610612763,
    "abbreviation": "MEM",
    "teamName": "Memphis Grizzlies",
    "simpleName": "Grizzlies",
    "location": "Memphis"
  },
  {
    "teamId": 1610612748,
    "abbreviation": "MIA",
    "teamName": "Miami Heat",
    "simpleName": "Heat",
    "location": "Miami"
  },
  {
    "teamId": 1610612749,
    "abbreviation": "MIL",
    "teamName": "Milwaukee Bucks",
    "simpleName": "Bucks",
    "location": "Milwaukee"
  },
  {
    "teamId": 1610612750,
    "abbreviation": "MIN",
    "teamName": "Minnesota Timberwolves",
    "simpleName": "Timberwolves",
    "location": "Minnesota"
  },
  {
    "teamId": 1610612740,
    "abbreviation": "NOP",
    "teamName": "New Orleans Pelicans",
    "simpleName": "Pelicans",
    "location": "New Orleans"
  },
  {
    "teamId": 1610612752,
    "abbreviation": "NYK",
    "teamName": "New York Knicks",
    "simpleName": "Knicks",
    "location": "New York"
  },
  {
    "teamId": 1610612760,
    "abbreviation": "OKC",
    "teamName": "Oklahoma City Thunder",
    "simpleName": "Thunder",
    "location": "Oklahoma City"
  },
  {
    "teamId": 1610612753,
    "abbreviation": "ORL",
    "teamName": "Orlando Magic",
    "simpleName": "Magic",
    "location": "Orlando"
  },
  {
    "teamId": 1610612755,
    "abbreviation": "PHI",
    "teamName": "Philadelphia 76ers",
    "simpleName": "76ers",
    "location": "Philadelphia"
  },
  {
    "teamId": 1610612756,
    "abbreviation": "PHX",
    "teamName": "Phoenix Suns",
    "simpleName": "Suns",
    "location": "Phoenix"
  },
  {
    "teamId": 1610612757,
    "abbreviation": "POR",
    "teamName": "Portland Trail Blazers",
    "simpleName": "Trail Blazers",
    "location": "Portland"
  },
  {
    "teamId": 1610612758,
    "abbreviation": "SAC",
    "teamName": "Sacramento Kings",
    "simpleName": "Kings",
    "location": "Sacramento"
  },
  {
    "teamId": 1610612759,
    "abbreviation": "SAS",
    "teamName": "San Antonio Spurs",
    "simpleName": "Spurs",
    "location": "San Antonio"
  },
  {
    "teamId": 1610612761,
    "abbreviation": "TOR",
    "teamName": "Toronto Raptors",
    "simpleName": "Raptors",
    "location": "Toronto"
  },
  {
    "teamId": 1610612762,
    "abbreviation": "UTA",
    "teamName": "Utah Jazz",
    "simpleName": "Jazz",
    "location": "Utah"
  },
  {
    "teamId": 1610612764,
    "abbreviation": "WAS",
    "teamName": "Washington Wizards",
    "simpleName": "Wizards",
    "location": "Washington"
  }];
      $scope.fetch = function() {
        $scope.code = null;
        $scope.response = null;
		var postData = {
        teamA : this.teamA.simpleName,
        teamB : this.teamB.simpleName
};
        $http({
       method: 'POST',
       url: 'http://localhost:8080/wildfly-helloworld-rs/rest/fight',
       data: postData,
       headers: {
            'Content-Type': 'application/json'
		}
		}).then(function(response) {
			console.log($scope.teamA);
            $scope.status = response.status;
            $scope.data = response.data;
			alert(response.data);
          }, function(response) {
            $scope.data = response.data || 'Request failed';
            $scope.status = response.status;
        });
      };

      $scope.updateModel = function(method, url) {
        $scope.method = method;
        $scope.url = url;
      };
    }]);
})(window.angular);

/*
Copyright 2017 Google Inc. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at http://angular.io/license
*/