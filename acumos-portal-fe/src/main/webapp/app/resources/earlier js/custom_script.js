/*var app = angular.module("myApp", []); */

var app =  angular.module('myApp', ['ngMaterial', 'ngMessages']);

app.controller('AppCtrl', function($scope, $timeout, $mdSidenav, $log) {
			this.myDate = new Date();
			this.isOpen = false;
			
			
			
			    $scope.toggleLeft = buildDelayedToggler('left');
			    $scope.toggleRight = buildToggler('right');
			    $scope.isOpenRight = function(){
			      return $mdSidenav('right').isOpen();
			    };

			    /**
				 * Supplies a function that will continue to operate until the
				 * time is up.
				 */
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
			    
			    /**
				 * Build handler to open/close a SideNav; when animation
				 * finishes report completion in console
				 */
			    function buildDelayedToggler(navID) {
			      return debounce(function() {
			        // Component lookup should always be available since we are
					// not using `ng-if`
			        $mdSidenav(navID)
			          .toggle()
			          .then(function () {
			            $log.debug("toggle " + navID + " is done");
			          });
			      }, 200);
			    }

			    function buildToggler(navID) {
			      return function() {
			        // Component lookup should always be available since we are
					// not using `ng-if`
			        $mdSidenav(navID)
			          .toggle()
			          .then(function () {
			            $log.debug("toggle " + navID + " is done");
			          });
			      };
			    }
			  })
			  
			  
			  
			  app.controller('LeftCtrl', function ($scope, $timeout, $mdSidenav, $log) {
			    $scope.close = function () {
			      // Component lookup should always be available since we are
					// not using `ng-if`
			      $mdSidenav('left').close()
			        .then(function () {
			          $log.debug("close LEFT is done");
			        });

			    };
			  })
			  
			  app.controller('RightCtrl', function ($scope, $timeout, $mdSidenav, $log) {
			    $scope.close = function () {
			      // Component lookup should always be available since we are
					// not using `ng-if`
			      $mdSidenav('right').close()
			        .then(function () {
			          $log.debug("close RIGHT is done");
			        });
			    };
			  
		 })
	  

	app.controller('DemoCtrl', DemoCtrl);

	  function DemoCtrl ($timeout, $q, $log, $scope) {
		   $scope.states = ('AL AK AZ AR CA CO CT DE FL GA HI ID IL IN IA KS KY LA ME MD MA MI MN MS ' +
				    'MO MT NE NV NH NJ NM NY NC ND OH OK OR PA RI SC SD TN TX UT VT VA WA WV WI ' +
				    'WY').split(' ').map(function(state) {
				        return {abbrev: state};
				      });
				    
		   
	    var self = this;

	    self.simulateQuery = false;
	    self.isDisabled    = false;

	    // list of `state` value/display objects
	    self.states        = loadAll();
	    self.querySearch   = querySearch;
	    self.selectedItemChange = selectedItemChange;
	    self.searchTextChange   = searchTextChange;

	    self.newState = newState;

	    function newState(state) {
	      alert("Sorry! You'll need to create a Constitution for " + state + " first!");
	    }

	    // ******************************
	    // Internal methods
	    // ******************************

	    /**
		 * Search for states... use $timeout to simulate remote dataservice
		 * call.
		 */
	    function querySearch (query) {
	      var results = query ? self.states.filter( createFilterFor(query) ) : self.states,
	          deferred;
	      if (self.simulateQuery) {
	        deferred = $q.defer();
	        $timeout(function () { deferred.resolve( results ); }, Math.random() * 1000, false);
	        return deferred.promise;
	      } else {
	        return results;
	      }
	    }

	    function searchTextChange(text) {
	      $log.info('Text changed to ' + text);
	    }

	    function selectedItemChange(item) {
	      $log.info('Item changed to ' + JSON.stringify(item));
	    }

	    /**
		 * Build `states` list of key/value pairs
		 */
	    function loadAll() {
	      var allStates = 'Alabama, Alaska, Arizona, Arkansas, California, Colorado, Connecticut, Delaware,\
	              Florida, Georgia, Hawaii, Idaho, Illinois, Indiana, Iowa, Kansas, Kentucky, Louisiana,\
	              Maine, Maryland, Massachusetts, Michigan, Minnesota, Mississippi, Missouri, Montana,\
	              Nebraska, Nevada, New Hampshire, New Jersey, New Mexico, New York, North Carolina,\
	              North Dakota, Ohio, Oklahoma, Oregon, Pennsylvania, Rhode Island, South Carolina,\
	              South Dakota, Tennessee, Texas, Utah, Vermont, Virginia, Washington, West Virginia,\
	              Wisconsin, Wyoming';

	      return allStates.split(/, +/g).map( function (state) {
	        return {
	          value: state.toLowerCase(),
	          display: state
	        };
	      });
	    }

	    /**
		 * Create filter function for a query string
		 */
	    function createFilterFor(query) {
	      var lowercaseQuery = angular.lowercase(query);

	      return function filterFn(state) {
	        return (state.value.indexOf(lowercaseQuery) === 0);
	      };

	    }
	    
	    $scope.title1 = 'Button';
	    $scope.title4 = 'Warn';
	    $scope.isDisabled = true;

	    $scope.googleUrl = 'http://google.com';
	    
	    
	    
	    
	}
	  
	 app.controller('SelectHeaderController', function($scope, $element) {
	      $scope.vegetables = ['2147584' ,'2147583' ,'2137582' ,'2137581' ,'2147580', '2147579'];
	      $scope.allnames = ['Most Likes' ,'Fewest Likes' ,'Most Downloads' ,'Fewest Downloads' ,'Highest Reach', 'Lowest Reach'];
	      $scope.searchTerm;
	      $scope.clearSearchTerm = function() {
	        $scope.searchTerm = '';
	      };
	      // The md-select directive eats keydown events for some quick select
	      // logic. Since we have a search input here, we don't need that logic.
	      $element.find('input').on('keydown', function(ev) {
	          ev.stopPropagation();
	      });
	    });
		
		
//Modal box js start
app.controller('Modalctrl', function($scope, $mdDialog) {
  $scope.status = '  ';
  $scope.customFullscreen = false;

  $scope.showAlert = function(ev) {
    // Appending dialog to document.body to cover sidenav in docs app
    // Modal dialogs should fully cover application
    // to prevent interaction outside of dialog
    $mdDialog.show(
      $mdDialog.alert()
        .parent(angular.element(document.querySelector('#popupContainer')))
        .clickOutsideToClose(true)
        .title('This is an alert title')
        .textContent('You can specify some description text in here.')
        .ariaLabel('Alert Dialog Demo')
        .ok('Got it!')
        .targetEvent(ev)
    );
  };

  $scope.showConfirm = function(ev) {
    // Appending dialog to document.body to cover sidenav in docs app
    var confirm = $mdDialog.confirm()
          .title('Would you like to delete your debt?')
          .textContent('All of the banks have agreed to forgive you your debts.')
          .ariaLabel('Lucky day')
          .targetEvent(ev)
          .ok('Please do it!')
          .cancel('Sounds like a scam');

    $mdDialog.show(confirm).then(function() {
      $scope.status = 'You decided to get rid of your debt.';
    }, function() {
      $scope.status = 'You decided to keep your debt.';
    });
  };

  $scope.showPrompt = function(ev) {
    // Appending dialog to document.body to cover sidenav in docs app
    var confirm = $mdDialog.prompt()
      .title('What would you name your dog?')
      .textContent('Bowser is a common name.')
      .placeholder('Dog name')
      .ariaLabel('Dog name')
      .initialValue('Buddy')
      .targetEvent(ev)
      .ok('Okay!')
      .cancel('I\'m a cat person');

    $mdDialog.show(confirm).then(function(result) {
      $scope.status = 'You decided to name your dog ' + result + '.';
    }, function() {
      $scope.status = 'You didn\'t name your dog.';
    });
  };

  $scope.showAdvanced = function(ev) {
    $mdDialog.show({
      controller: DialogController,
      templateUrl: 'dialog1.tmpl.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      clickOutsideToClose:true,
      fullscreen: $scope.customFullscreen // Only for -xs, -sm breakpoints.
    })
    .then(function(answer) {
      $scope.status = 'You said the information was "' + answer + '".';
    }, function() {
      $scope.status = 'You cancelled the dialog.';
    });
  };

  $scope.showTabDialog = function(ev) {
    $mdDialog.show({
      controller: DialogController,
      templateUrl: 'tabDialog.tmpl.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      clickOutsideToClose:true
    })
        .then(function(answer) {
          $scope.status = 'You said the information was "' + answer + '".';
        }, function() {
          $scope.status = 'You cancelled the dialog.';
        });
  };

  $scope.showPrerenderedDialog = function(ev) {
    $mdDialog.show({
      contentElement: '#myDialog',
      parent: angular.element(document.body),
      targetEvent: ev,
      clickOutsideToClose: true
    });
  };

  function DialogController($scope, $mdDialog) {
    $scope.hide = function() {
      $mdDialog.hide();
    };

    $scope.cancel = function() {
      $mdDialog.cancel();
    };

    $scope.answer = function(answer) {
      $mdDialog.hide(answer);
    };
  }
});

 