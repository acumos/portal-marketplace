app
            .component(
                        'attGlobalLogon',
                        {
                        	  templateUrl : '/app/header/attGlobalLogin.html',
                              controller : function($uibModal, $scope,$rootScope,productService,$window,$http) {
                                    
                                    $ctrl = this;
                                   // $ctrl.ok = function(){
                                    	 $http({
                                             method : 'POST',
                                             url : 'http://localhost:8083/globalLogin',
                                             /*data : {"request_body":
                                             	{"request": "https://www.e-access.att.com/empsvcs/hrpinmgt/pagLogin/?sysName=ACUMOS&retURL=hxtp://localhost/attLogin.html", 
                                            	 "response": ""
                                            	}*/
                                             //}
                                             
                                       }).then(function successCallback(response) {
                                             console.log(response);
                                             

                                       }, function errorCallback(response) {
                                             console.log("Error: ", response);
                                             $ctrl.userPassInvalid = true;
                                       });
                                    }

                              //}
                        });

