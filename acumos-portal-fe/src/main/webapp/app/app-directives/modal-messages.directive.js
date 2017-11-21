app.directive(
				'alertMessage',
				function() {
					return {
						restrict : 'EA',
						scope : {
							msg : '=',
							icon : '=',
							styleclass : '=',
						/* closePopup :'&', */
						},
						template : '<div class="c-alert {{styleclass}}">'
								+ '<div class="c-alert-inner">'
								+ '<span class="ds-icon icon-validate" ng-if="icon.length<1">{{icon}}</span>'
								+ '<span class="material-icons" ng-if="icon.length>1">{{icon}}</span>' 
								+ '<span class="alert-text">{{msg}}</span>'
								+ '</div>'
								+ '<button class="mdl-button mdl-js-button mdl-button--icon ds-btnclose1">'
								/*+ '<i class="material-icons">close</i>'*/
								+ '</button>' + '</div>'

					}
				});
app.directive(
		'alertMessagePrivate',
		function() {
			return {
				restrict : 'EA',
				scope : {
					msg : '=',
					icon : '=',
					styleclass : '=',
				/* closePopup :'&', */
				},
				template : '<div class="c-alert {{styleclass}}">'
						+ '<div class="c-alert-inner">'
						+ '<span class="ds-icon icon-validate" ng-if="icon.length<1">{{icon}}</span>'
						+ '<span class="material-icons" ng-if="icon.length>1">{{icon}}</span>' 
						+ '<span class="alert-text">{{msg}}</span>'
						+ '</div>'
						+ '<button class="mdl-button mdl-js-button mdl-button--icon ds-btnclose1">'
						/*+ '<i class="material-icons">close</i>'*/
						+ '</button>' + '</div>'

			}
		});


app.directive(
		'alertMessageCompany',
		function() {
			return {
				restrict : 'EA',
				scope : {
					msg : '=',
					icon : '=',
					styleclass : '=',
				/* closePopup :'&', */
				},
				template : '<div class="c-alert {{styleclass}}">'
						+ '<div class="c-alert-inner">'
						+ '<span class="ds-icon icon-validate" ng-if="icon.length<1">{{icon}}</span>'
						+ '<span class="material-icons" ng-if="icon.length>1">{{icon}}</span>' 
						+ '<span class="alert-text">{{msg}}</span>'
						+ '</div>'
						+ '<button class="mdl-button mdl-js-button mdl-button--icon ds-btnclose1">'
						/*+ '<i class="material-icons">close</i>'*/
						+ '</button>' + '</div>'

			}
		});


app.directive(
		'alertMessagePublic',
		function() {
			return {
				restrict : 'EA',
				scope : {
					msg : '=',
					icon : '=',
					styleclass : '=',
				/* closePopup :'&', */
				},
				template : '<div class="c-alert {{styleclass}}">'
						+ '<div class="c-alert-inner">'
						+ '<span class="ds-icon icon-validate" ng-if="icon.length<1">{{icon}}</span>'
						+ '<span class="material-icons" ng-if="icon.length>1">{{icon}}</span>' 
						+ '<span class="alert-text">{{msg}}</span>'
						+ '</div>'
						+ '<button class="mdl-button mdl-js-button mdl-button--icon ds-btnclose1">'
						/*+ '<i class="material-icons">close</i>'*/
						+ '</button>' + '</div>'

			}
		});


// the classes
/* style ----------- icon class
 * c-success ------- 
 * c-warning ------- report_problem
 * c-error --------- report_problem
 * c-info ---------- info_outline
 */

//usage

/* 
 * 
$scope.msg = "Solution is already shared with "+ $scope.sharedWithUserName;
$scope.icon = 'info_outline';
$scope.styleclass = 'c-info';
$scope.showAlertMessage = true;
$timeout(function() {
	$scope.showAlertMessage = false;
}, 3500);

*/