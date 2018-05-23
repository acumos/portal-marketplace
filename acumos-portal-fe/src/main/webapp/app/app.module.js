/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
===================================================================================
Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
===================================================================================
This Acumos software file is distributed by AT&T and Tech Mahindra
under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
This file is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
===============LICENSE_END=========================================================
*/

'use strict';
var app = angular.module('AcumosApp', ['ui.router','ngMaterial', 'ngMdIcons','satellizer',
                                        'ngAnimate', 'ui.bootstrap','marketHome', 'signInModal', 'myModal',
                                        'marketPlace', 'manageModule','designStudio', 'modelResource','headerNav',
                                        'ngAnimate', 'modelDetails','ui.bootstrap','infinite-scroll','ngStorage',
                                        'modelEdit','ngTagsInput','resetPswd','userDetail','forgotPswd', 'ngSanitize',
                                        'angularFileUpload', 'imageupload','angular-jwt', 'peerConfig', 'ngQuill',
                                        'marketFooter','notificationModule', 'termsCondition', 'ui.carousel','admin','404Error','ngDragDrop','naif.base64'/*'socialLogin'*/]);

/*app.config(function(socialProvider){
	socialProvider.setGoogleKey("60271745908-m1laroii13m87c98o12n635cjj0ng516.apps.googleusercontent.com");
	socialProvider.setLinkedInKey("78e39872fnup60");
	socialProvider.setFbKey({appId: "499718273719208", apiVersion: "2.4"});
});*/



app.service('modalProvider',['$uibModal', function ($uibModal) {
	this.openPopupModal = function () {
	    var modalInstance = $uibModal.open({
	    	templateUrl: './app/header/sign-in.template.html',
	        controller: function($http, $scope){
	  
	   }
	    });
	    
	};
	this.closePopupModal = function(){
		$ctrl.$close({
			result : $ctrl.modalData
		});
	}
	}]);

app.filter('filterByTags', function () {
    return function (items, tags) {
      var filtered = [];
      (items || []).forEach(function (item) {
        var matches = tags.some(function (tag) {
          return (item.data1.indexOf(tag.text) > -1) ||
                 (item.data2.indexOf(tag.text) > -1);
        });
        if (matches) {
          filtered.push(item);
        }
      });
      return filtered;
    };
  });
angular
.module('AcumosApp').directive('resizer', function($document) {

	return function($scope, $element, $attrs) {

		$element.on('mousedown', function(event) {
			event.preventDefault();

			$document.on('mousemove', mousemove);
			$document.on('mouseup', mouseup);
		});

		function mousemove(event) {

			if ($attrs.resizer == 'vertical') {
				// Handle vertical resizer
				var x = event.pageX;

				if ($attrs.resizerMax && x > $attrs.resizerMax) {
					x = parseInt($attrs.resizerMax);
				}

				$element.css({
					left: x + 'px'
				});

				$($attrs.resizerLeft).css({
					width: x + 'px'
				});
				$($attrs.resizerRight).css({
					left: (x + parseInt($attrs.resizerWidth)) + 'px'
				});

			} else {
				// Handle horizontal resizer
				var y = window.innerHeight - event.pageY;

				$element.css({
					bottom: y + 'px'
				});

				$($attrs.resizerTop).css({
					bottom: (y + parseInt($attrs.resizerHeight)) + 'px'
				});
				$($attrs.resizerBottom).css({
					height: y + 'px'
				});
			}
		}

		function mouseup() {
			$document.unbind('mousemove', mousemove);
			$document.unbind('mouseup', mouseup);
		}
	};
});

angular
.module('AcumosApp').directive('draggable', function () {
	  return {
	    restrict: 'A',
	    link: function (scope, element, attrs) {
	      element[0].addEventListener('dragstart', scope.handleDragStart, false);
	      element[0].addEventListener('dragend', scope.handleDragEnd, false);
	    }
	  }
	});

angular
.module('AcumosApp').directive('fileDraggable', function () {
	  return {
	    restrict: 'A',
	    scope: {
    		imgsrc:"=",
       		imgpreview : "="
    		},
	    link: function (scope, element, attrs) {
	      element.bind('dragover', function (evt) {
                evt.stopPropagation()
                evt.preventDefault()
                var clazz = 'not-available'
                var ok = evt.dataTransfer && evt.dataTransfer.types && evt.dataTransfer.types.indexOf('Files') >= 0
           });
	    	function srcToFile(src, fileName, mimeType){
			    return (fetch(src)
			        .then(function(res){return res.arrayBuffer();})
			        .then(function(buf){return new File([buf], fileName, {type:mimeType});})
			    );
			}
	      element[0].addEventListener('dragend', function (event) {
	    	     
	    	     //scope.imgsrc = event.target.src;
	    	     srcToFile(event.target.src, event.target.src.split('/').pop(), 'image/png')
					.then(function(file){
					    var fd = new FormData();
					    fd.append('file1', file);
					    scope.imgsrc = file;
					    var reader = new FileReader();
                        var imgpath = new Image();
                        reader.readAsDataURL(scope.imgsrc);
                       reader.onload = function(loadEvent) {
                    	   	  imgpath.src = loadEvent.target.result;
                    	   	  scope.imgpreview = imgpath.src;
                              imgpath.onload = function(){
                                  scope.imgsrc.width = this.width;
                                  scope.imgsrc.height = this.height;
                              };
                       }
					});
	    	  }, false);
	    }
	  }
	});

angular
.module('AcumosApp').directive('droppable', function () {
	  return {
	    restrict: 'A',
	    link: function (scope, element, attrs) {
	      element[0].addEventListener('drop', scope.handleDrop, false);
	      element[0].addEventListener('dragover', scope.handleDragOver, false);
	     
	    }
	  }
	});

angular
.module('AcumosApp').directive('fileReader', function() {
	  return {
	    scope: {
	      fileReader:"="
	    },
	    link: function(scope, element) {
	      $(element).on('change', function(changeEvent) {
	        var files = changeEvent.target.files;
	        if (files.length) {
	          var r = new FileReader();
	          r.onload = function(e) {
	              var contents = e.target.result;
				  /*if(contents.search('\r\n') != -1){
					    var data = contents.split('\r\n');
				  }else{
					   var data = contents.split('\n');
					  
				  }
				
				  var col = data[0];
				  var tabledata = col.split(',');*/
	              var tabledata = contents;
	              scope.$apply(function () {
	                scope.fileReader = tabledata;
					scope.testing = contents;
	              });
	          };
	          
	          r.readAsText(files[0]);
	        }
	      });
	    }
	  };
	});
angular.module('AcumosApp').directive('expand', function () {
    return {
        restrict: 'A',
        controller: ['$scope', function ($scope) {
            $scope.$on('onExpandAll', function (event, args) {
                $scope.expanded = args.expanded;
            });
        }]
    };
});
angular.module('AcumosApp').directive('dExpandCollapse', function() {
return {
	      restrict: 'EA',
	      link: function(scope, element, attrs){
	    	  		$(element).click( function() {
	            	//var show = "false";
	            	$(element).find(".answer").slideToggle('200',function() {            
	                    // You may toggle + - icon     
	            		$(element).find("#expandCollapse").toggleClass('faqPlus faqMinus');
	            	});
	            	if($("div.answer:visible").length>1) { 
	  // You may toggle + - icon//$(this).parent().find("span.faqMinus").removeClass('faqMinus').addClass('faqPlus');
	            		$(this).siblings().find(".answer").slideUp('slow');	
	            	}
	            });
	      	}
	     }
});
angular
	.module('AcumosApp')
	.directive('validUsername', function() {
  return {
    require: 'ngModel',
    link: function(scope, elm, attrs, ctrl) {
        var validate = function(viewValue) {
        var isBlank = viewValue === ''
        //var invalidChars = !isBlank && !/^[A-z0-9]+$/.test(viewValue)
        var invalidChars = !isBlank && !/^[A-z0-9 ]+$/.test(viewValue)
        if(viewValue){
        	 var invalidLen = !isBlank && !invalidChars && (viewValue.length < 2 || viewValue.length > 20)
        }
        ctrl.$setValidity('isBlank', !isBlank)
        ctrl.$setValidity('invalidChars', !invalidChars)
        ctrl.$setValidity('invalidLen', !invalidLen)
        scope.usernameGood = !isBlank && !invalidChars && !invalidLen
                     return viewValue;
               };
               ctrl.$parsers.unshift(validate);
               ctrl.$formatters.push(validate);
               attrs.$observe('validUsername', function(comparisonModel){
                     return validate(ctrl.$viewValue);
               });

    }
  }
});
angular
.module('AcumosApp')
.directive('validName', function() {
return {
require: 'ngModel',
link: function(scope, elm, attrs, ctrl) {
    var validate = function(viewValue) {
    var isBlank = viewValue === ''
    //var invalidChars = !isBlank && !/^[A-z0-9]+$/.test(viewValue)
    var invalidChars = !isBlank && !/^[A-z0-9 ]+$/.test(viewValue)
    ctrl.$setValidity('isBlank', !isBlank)
    ctrl.$setValidity('invalidChars', !invalidChars)

    scope.usernameGood = !isBlank && !invalidChars
                 return viewValue;
           };
           ctrl.$parsers.unshift(validate);
           ctrl.$formatters.push(validate);
           attrs.$observe('validName', function(comparisonModel){
                 return validate(ctrl.$viewValue);
           });

}
}
});

angular
	.module('AcumosApp')
	.directive('validPassword', function() {
  return {
    require: 'ngModel',
    link: function(scope, elm, attrs, ctrl) {
     	var validate = function(viewValue) {
        var isBlank = viewValue === ''
        if(viewValue){
        	 var invalidLen = !isBlank && (viewValue.length < 8 || viewValue.length > 20)
        }
       var isWeak = !isBlank && !invalidLen && !/(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z])(?=.*[^a-zA-Z0-9])/.test(viewValue);
       var isLowerCaseLetter = !isBlank && !invalidLen && !/(?=.*[a-z])/.test(viewValue);
       var isUpperCaseLetter = !isBlank && !invalidLen && !/(?=.*[A-Z])/.test(viewValue);
       var isDigit = !isBlank && !invalidLen && !/(?=.*[0-9])/.test(viewValue);
       var isSpecialChar = !isBlank && !invalidLen && !/(?=.*[^a-zA-Z0-9])/.test(viewValue);
	    ctrl.$setValidity('isBlank', !isBlank);
	    ctrl.$setValidity('isWeak', !isWeak);
	    ctrl.$setValidity('invalidLen', !invalidLen);
	    ctrl.$setValidity('isDigit', !isDigit);
	    ctrl.$setValidity('isLowerCaseLetter', !isLowerCaseLetter);
	    ctrl.$setValidity('isUpperCaseLetter', !isUpperCaseLetter);
	    ctrl.$setValidity('isSpecialChar', !isSpecialChar);
        scope.passwordGood = !isBlank && !isWeak && !invalidLen
                     return viewValue;
               };
               ctrl.$parsers.unshift(validate);
               ctrl.$formatters.push(validate);
               attrs.$observe('validPassword', function(comparisonModel){
                     return validate(ctrl.$viewValue);
               });

    }
  }
});

angular
	.module('AcumosApp')
	.directive('validPasswordC', function() {
  return {
    require: 'ngModel',
    link: function(scope, elm, attrs, ctrl) {

		 var validate = function(viewValue, $scope) {
                  var isBlank = viewValue === ''
                  
        ctrl.$setValidity('isBlank', !isBlank)
        
        scope.passwordCGood = !isBlank 
                     return viewValue;
               };
               ctrl.$parsers.unshift(validate);
               ctrl.$formatters.push(validate);
               attrs.$observe('validPasswordC', function(comparisonModel){
                     return validate(ctrl.$viewValue);
               });

    }
  }
});


angular
	.module('AcumosApp')
	.directive('validFile', function () {
	return {
	    require: 'ngModel',
	    scope : {"error": "="},
	    link: function (scope, elem, attrs, ngModel) {
	    	var validFormats = ['jpg','jpeg','png','gif'];
	        elem.bind('change', function () {
	            validImage(false);
	            scope.$apply(function () {
	                ngModel.$render();
	            });
	        });
	        ngModel.$render = function () {
	            ngModel.$setViewValue(elem.val());
	        };
	        function validImage(bool) {
	            ngModel.$setValidity('extension', bool);
	        }
	        ngModel.$parsers.push(function(value) {
	            var ext = value.substr(value.lastIndexOf('.')+1);
	            var size = elem[0].files[0].size;
	            if(ext=='') return;
	            if(validFormats.indexOf(ext) == -1 && size >= 800){
	            	scope.error = true;
	                return value;
	            }
	            validImage(true);
	            scope.error = false;
	            return value;
	           
	        });
	    }
	  };
	});

angular
.module('AcumosApp')
.directive('compile', ['$compile', function ($compile) {
    return function(scope, element, attrs) {
        scope.$watch(
            function(scope) {
                // watch the 'compile' expression for changes
                return scope.$eval(attrs.compile);
            },
            function(value) {
                // when the 'compile' expression changes
                // assign it into the current DOM
                element.html(value);

                // compile the new DOM and link it to the current
                // scope.
                // NOTE: we only compile .childNodes so that
                // we don't get into infinite loop compiling ourselves
                $compile(element.contents())(scope);
            }
        );
    };
}])
app.directive("dynamicName",function($compile){
    return {
        restrict:"A",
        terminal:true,
        priority:1000,
        link:function(scope,element,attrs){
            element.attr('name', scope.$eval(attrs.dynamicName));
            element.removeAttr("dynamic-name");
            $compile(element)(scope);
        }
    }});

angular
.module('AcumosApp')
.directive("fileUpload", [function() {
	return {
        templateUrl:'./app/file-upload/file-upload-template.html',
        scope: {
            file: "=",
            filepreview: "=",
            filename: "=",
            icon: "=",
            accept: "@",
            uploadid: "@",
            size: "=", //in bytes
            imageerror: "=",
            imagetypeerror : "="
          },
        link: function (scope, element) {

            //scope.fileName = 'Browse';

            element.bind('change', function () {
                scope.$apply(function () {
                	//scope.fileinput = changeEvent.target.files[0];
                	scope.imageerror = false;
                    scope.fileinput = document.getElementById(scope.uploadid).files[0];
                    scope.file = scope.fileinput;
                    scope.fileType = scope.file.name.split('.').pop();
                    scope.validFormats = ['jpg','jpeg','png','gif'];
                    var reader = new FileReader();
					var imgpath = new Image();
                    reader.onload = function(loadEvent) {
                      scope.$apply(function() {
                    	console.log(scope.size);
						imgpath.src = loadEvent.target.result;
                        imgpath.onload = function(){
	                              scope.file.width = this.width;
	                              scope.file.height = this.height;
	                          };
                    	var size = scope.fileinput.size;
	                	if(scope.validFormats.indexOf(scope.fileType) == -1){
	    	            	scope.imagetypeerror = true;
	    	            	scope.imageerror = false;
	    	            	return true;
	    	            }else if(size >= scope.size){
	    	            	scope.imageerror = true;
	    	            	scope.imagetypeerror = false;
	    	            	return true;
	    	            }else{
	                    	scope.filepreview = loadEvent.target.result;
	                        scope.filename = scope.fileinput.name;
	                        scope.icon = true;
	    	            }
                      });
                    }
                    reader.readAsDataURL(scope.fileinput);
                });
            });

            scope.uploadFile = function(){
                var formData = new FormData();

                formData.append('file', document.getElementById('uploadFileInput').files[0]);

                // Add code to submit the formData  
            };
        }}
  }]);

angular
.module('AcumosApp').directive("fileDroppable", function () {
      return {
        restrict: 'A',
        scope: {
           file: "=",
           filepreview: "=",
           filename: "=",
          },
        link: function (scope, element, attrs) {
            element.bind('dragover', function (evt) {
                evt.stopPropagation()
                evt.preventDefault()
                var clazz = 'not-available'
                var ok = evt.dataTransfer && evt.dataTransfer.types && evt.dataTransfer.types.indexOf('Files') >= 0
           });
            element.bind('drop', function (evt) {
                //console.log('drop evt:', JSON.parse(JSON.stringify(evt.dataTransfer)))
                evt.stopPropagation()
                evt.preventDefault()
                   var files = evt.originalEvent.dataTransfer.files
                if (files.length > 0) {
                    scope.$apply(function(){
                        scope.file = files[0];
                        scope.filename = scope.file.name;
			var reader = new FileReader();
                        var imgpath = new Image();
                        reader.readAsDataURL(files[0]);
                        reader.onload = function(loadEvent) {
                        imgpath.src = loadEvent.target.result;
						scope.filepreview = imgpath.src;
                              imgpath.onload = function(){
                                  scope.file.width = this.width;
                                  scope.file.height = this.height;
                              };
                       }
                        
                    })
                }
           });
            
          element[0].addEventListener('drop', scope.handleDrop, false);
          element[0].addEventListener('dragover', scope.handleDragOver, false);
        
        }
      }
    });

angular
.module('AcumosApp').directive('ngEnter',function() {

	  var linkFn = function(scope,element,attrs) {
	    element.bind("keypress", function(event) {
	      if(event.which === 13) {
	        scope.$apply(function() {
	      scope.$eval(attrs.ngEnter);
	        });
	        event.preventDefault();
	      }
	    });
	  };

	  return {
	    link:linkFn
	  };
	});
//for logo validation
/*angular
.module('AcumosApp').directive(
		'uploadLogoModel',
		function($parse) {
			return {
				restrict : 'A', // the directive can be used as an
								// attribute only

				
				 * link is a function that defines functionality of
				 * directive scope: scope associated with the element
				 * element: element on which this directive used attrs:
				 * key value pair of element attributes
				 
				link : function(scope, element, attrs) {
					var model = $parse(attrs.uploadLogoModel), modelSetter = model.assign; // define
					scope.imageError = false;																		// a
																						// setter
																							// for
																							// demoFileModel

					// Bind change event on the element
					element.bind('change', function() {
						// Call apply on scope, it checks for value
						// changes and reflect them on UI
						//scope.$apply(function() {
							// set the model value
							angular.forEach(element[0].files, function (item) {
								var img = new Image();  
			                    var imgheight = 0;
			                    var imgwidth = 0;
			                    var imgurl = (URL || webkitURL).createObjectURL(item);

			                    img.src = imgurl;
			                    img.onload = function() {

			                        imgheight = img.height;
			                        imgwidth = img.width;
			                        //alert("Width: " + imgheight + "  height: " + imgwidth);

			                        var value = {
			                       // File Name 
			                        name: item.name,
			                        //File Size 
			                        size: item.size,
			                        //File URL to view 
			                        url: imgurl,
			                        // File Input Value 
			                        _file: item,

			                        width: imgwidth,

			                        height: imgheight,

			                        mystyle: {}
			                        };
			                        scope.$apply(function () {
			                            values.push(value);
			                        });      
			                        if(value.height >= 27){
			                        	scope.imageError = true;
			                        }else{
			                        	var size = element[0].files[0].size;
										scope.imageError = false;
					    	            modelSetter(scope, element[0].files[0]);
			                        }
			                    };
							//});
							
		    	            //return true;
						});
					});
				}
			}
		})*/