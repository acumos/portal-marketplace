/*
===============LICENSE_START=======================================================
Acumos  Apache-2.0
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
angular
    .module('designStudio',['ui.bootstrap'])
    .component(
        'designStudio',
        {
            templateUrl : './app/design-studio/design-studio.template.html',
            controller : DSController
        }
    );
DSController.$inject = ['$scope','$http','$filter','$q','$window','$rootScope','$mdDialog','$state','$stateParams','$injector','browserStorageService'];

function DSController($scope,$http,$filter,$q,$window,$rootScope,$mdDialog ,$state,$stateParams,$injector, browserStorageService) {
	componentHandler.upgradeAllRegistered();
	$scope.is_ie = false || !!document.documentMode;

	$scope.userDetails = JSON.parse(browserStorageService.getUserDetail());
	if($scope.userDetails === null){
		var modalService = $injector.get('$mdDialog'); 
		modalService.show({
			 templateUrl: '../app/header/sign-in-promt-modal-box.html',
			 clickOutsideToClose: true,
			 controller : function DialogController($scope ) {
				 $scope.closeDialog = function() {
					 modalService.hide();
					 $rootScope.showAdvancedLogin();
			    	 $state.go('home');
		     } }
			});
	} else{
	$scope.datatype = ['int','string','float','boolean','long','byte'];
	if($scope.dbType === "json"){
		$('#upload').removeClass('disp');
		$('#upload').addClass('disp-active');
	} else if($scope.dbType === "csv"){
		$('#selectFirstRow').removeClass('disp');
		$('#selectFirstRow').addClass('disp-active');
		$('#upload').removeClass('disp');
		$('upload').addClass('disp-active');
	} 
	const ALERT = "Alert";
	const TOOLCOLOR = "#C29DD6";
	const WHITE = "white";
	const COLLATOR = "Collator";
	const SPLITTER = "Splitter";
	$scope.checkboxDisable = true;
	$scope.activeInactivedeploy = true;
    $scope.validationState = true;
    $scope.searchbox = true;
    $scope.showSearch = function(){
        $scope.searchbox = false;
    };
    $scope.readSolutionArray = [];
    $scope.readSolution = false;
    $('#deleteHide').hide();var deleteShow = 0;
    function enanleDisable(){
        var numItems = $('.node').length;
        if(numItems > 1){
        	 $scope.checkboxDisable = false;
            $scope.cleardis= false;
        }else {
            $scope.cleardis= true;
            $scope.checkboxDisable = true; 
        }
    }
    enanleDisable();

    var pathArray = location.href.split( '/' );
    var protocol = pathArray[0];
    var host = pathArray[2];
    var baseURL = protocol + '//' + host;
    var nodeIdCnt = 1, countComSol = 0; var jsonProto= null;
    var protoJsonRead = new Map();var changeNode = new Object();
    var jsonProtoNode= new Map(); var jsonProtoMap;
    var dataMaps = new Map();
    var enteredOk = false;
    var savedSolution = false;
    var readScriptDetails = null;
    var scriptEnteredDetails = null;
    var collateDetails = null;
    var splitDetails = null;
    var extras = false;
    var operations = []; var messages = [];
    $scope.collateSourceTables = [];
    $scope.collateTargetTables = [];
    $scope.collateTags = [];
    $scope.collateSchemes = [];
    $scope.collateSelectedDetails = [];
    $scope.splitSelectedDetails = [];
    $scope.collateErrors = [];
    $scope.splitSourceTables = [];
    $scope.splitTargetTables = [];
    $scope.splitTags = [];
    $scope.splitSchemes = [];
    $scope.splitErrors = [];
    $scope.palette = {categories: []};
    $scope.saveState = {
        desc: "solution is unchanged",
        noSaves: true,
        noDeletes: true,
        descStyle: {
            opacity: 0.5
        }
    };
    $scope.handleDragStart = function(e){
        this.style.opacity = '0.4';
        e.dataTransfer.setData('text/plain', this.innerHTML);
    };
    $scope.handleDragEnd = function(e){
        var dataText = e.dataTransfer.getData('text/plain');
        this.style.opacity = '1.0';
    };
    $scope.handleDragOver = function (e) {
    	e.preventDefault(); // Necessary. Allows us to drop.
        e.dataTransfer.dropEffect = 'move';  // See the section
        return false;
    };
    $scope.handleDrop = function(e){
    	$scope.canvas = true;
        e.preventDefault();
        var bound = _diagram.root().node().getBoundingClientRect();
        var pos = _diagram.invertCoord([e.clientX - bound.left,
                                        e.clientY - bound.top]);
        var type = e.dataTransfer.getData('text/plain');


        var max = 0; var getrev; var revision; var revRemove;
        var res = type.split("(");
        var getver = res[1].split(")");
        var rev = getver[1].split("[");
        type = type.replace(rev[0],'');
        if(rev.length > 1){
        	getrev = rev[1].split("]");
        	type = type.replace(getrev[1],'');
        	revision = getrev[0];
        	revRemove = '['+revision+']';
        } else{
        	revRemove = "<!---->";
        }
        var ver = getver[0];
        var verRemove = '('+ver+')';
        type = type.replace(verRemove,'');
        type = type.replace(revRemove,'');
        type = type.slice(0,-1);
        var typeModel = type+'+'+getver[0];
        _drawGraphs.nodeCrossfilter().all().forEach(function(n) {
            var nodeType = n.type.name;
            var nodeTypeLength = nodeType.length;
            var lastChar = nodeType.slice(-1);
            if(isNaN(lastChar)){
                var number = n.nodeId.match(/[0-9]+$/);
                if(!number)
                    return; // currently all ids will be type +
                	number = number[0];
                	var type2 = n.nodeId.slice(0, -number.length);
                if(type2 === type && +number > max)
                    max = +number;
            } else {
                var nodeid = n.nodeId.split(nodeType);
                var number = nodeid[1];
                var type2 = n.nodeId.slice(0, nodeTypeLength);
                if(type2 === type && +number > max)
                    max = +number;
            }
        });
        var data = {
        	nodeVersion: ver,
            nodeId: type + (max+1),
            type: {"name": type},
            name: type + (max+1)
        };
        $scope.readSolutionArray[data.nodeId] = false;
        $scope.nodeName=data.name;
        var nodeId = '',nodeVersion = ver;
        if(revision !== undefined){
        	var nodeRevision = revision.slice(0,4);
        	$scope.selectedIndex = type+ver+nodeRevision;
        }
        else
        	$scope.selectedIndex = type+ver;
        $http.get(_catalog.fModelUrl(_components.get(type+'+'+nodeVersion))).success(function(tgif) {
            nodeId = _components.get(type+'+'+nodeVersion).solutionId;
            $scope.solutionDetails=_components.get(type+'+'+nodeVersion);
            $scope.showProperties=null;
            $scope.packageName= JSON.stringify(tgif.self.name);
            $scope.requireCalls= tgif.services.calls;
            $scope.capabilityProvides=tgif.services.provides;
            var url= build_url(options.protobuf, {
                userId: get_userId(),
                solutionId :  nodeId,
                version : nodeVersion
            });
            $http.get(url).success(function(proto){
                $scope.protoNode=proto;
                var protoJson=proto;
                jsonProtoNode.set($scope.solutionDetails.solutionName,protoJson);
            }).error(function(response){
            	document.getElementById("loading").style.display = "none";
            	$scope.titlemsg = ALERT;
            	$scope.msg = "Cannot drop this item onto the canvas, Error details: Failed to fetch the ProtoBuf JSON for "+type+" ("+nodeVersion+")";
            	$scope.showpopup();
            });

            var requirementJson=[], capabilityJson=[];
            // get requirements
            var check_isValid_calls ='';
            var check_isValid_provides= '';
            angular.forEach(tgif.services.calls, function(value, key) {
                if(value.request.format.length !== 0){
                    check_isValid_calls = value.request.format[0].messageName;
                    var reqObj = value.request.format;
                    var reqOperation=value.config_key;
                    requirementJson.push({
                        "name": "",
                        "relationship": "",
                        "id": "",
                        "capability": {
                            "name":reqObj,
                            "id": reqOperation
                        },
                        "target": {"name": "","description": ""},
                        "target_type": "Node"}
                                        );
                }
            });
            //get capabilities
            angular.forEach(tgif.services.provides, function(value, key) {
                if(value.request.format.length !== 0){
                    check_isValid_provides = value.request.format[0].messageName;
                    var capObj = value.request.format;
                    var capOperation=value.route;
                    capabilityJson.push({
                        "id": "",
                        "name":"",
                        "target": {
                            "name":capObj,
                            "id": capOperation
                        },
                        "target_type": "Capability",
                        "properties":null
                    });
                }
            });
            var def = {
                "id": tgif.self.name,
                "name": tgif.self.name,
                "ndata": {},
                "capabilities": capabilityJson,
                "requirements": requirementJson,
                "extras": []
            };
            data.capabilities = capabilityJson;
            data.requirements = requirementJson;
            var url = "", nodeDetails;
            var ndata = {
                fixed: true,
                px: pos[0],
                py: pos[1]
            };
            switch($scope.solutionDetails.toolKit){
            case 'CO':
            	type = "Collator";
            	def.extras = [];
            	$scope.collateScheme = null;
            	$scope.collateSchemes[data.nodeId] = [];
            	$scope.collateSelectedDetails[data.nodeId] = [];
            	$scope.collateSourceTables[data.nodeId] = [];
            	$scope.collateTargetTables[data.nodeId] = [];
            	$scope.collateTags[data.nodeId] = [];
            	$scope.collateErrors[data.nodeId] = [];
            	break;
            case 'SP':
            	type = "Splitter";
            	def.extras = [];
            	$scope.splitScheme = null;
            	$scope.splitSchemes[data.nodeId] = [];
            	$scope.splitSelectedDetails[data.nodeId] = [];
            	$scope.splitSourceTables[data.nodeId] = [];
            	$scope.splitTargetTables[data.nodeId] = [];
            	$scope.splitTags[data.nodeId] = [];
            	$scope.splitErrors[data.nodeId] = [];
            	break;
            case 'BR': 
            	type = "DataBroker";
            	def.extras = ["script"];
            	$scope.scriptText = null;
            	delete $scope.dbType;
    			delete $scope.fileUrl;
    			delete $scope.localurl;
    			delete $scope.userImage;
    			delete $scope.userImageNew;
    			$scope.firstRow = null;
            	$scope.selectedOutput = null;
            	break;
            case 'TC':
            	type = "TrainingClient";
            	def.extras = [];
            	break;
            default:
            	if(check_isValid_calls === "ANY" && check_isValid_provides === "ANY"){
                    type = "DataMapper";
                }else{
                    type = "MLModel";
                }
            	def.extras = [];
            }
            data.modelName = type;
            
            if(_solutionId){
                url = build_url(options.addNode, {
                    userId: get_userId(),
                    solutionId :  _solutionId,
                    version : nodeVersion
                   
                });

                nodeDetails = {
                    'name' : data.name,
                    'nodeId' : data.nodeId,
                    'requirements' : requirementJson,
                    'type' : {"name": type},
                    'nodeSolutionId' : nodeId,
                    'nodeVersion' : nodeVersion,
                    'capabilities' : capabilityJson,
                    'ndata' : ndata,
                    'properties' : []
                };

            }
            else if(_cid){
            	url = build_url(options.addNode, {
                    userId: get_userId(),
                    cid: _cid
                   
                });
                nodeDetails = {
                    'name' : data.name,
                    'nodeId' : data.nodeId,
                    'nodeSolutionId' : nodeId,
                    'nodeVersion' : nodeVersion,
                    'requirements' : requirementJson,
                    'type' : {"name": type},
                    'capabilities' : capabilityJson,
                    'ndata' : ndata,
                    'properties' : []
                };
            }
            document.getElementById("loading").style.display = "block";
            $http.post(url,nodeDetails)
                .success(function(response) {
                	document.getElementById("loading").style.display = "none";
                    $scope.cleardis= false;
                    nodeIdCnt++;
                    $scope.checkboxDisable = false;
                    $scope.activeInactivedeploy = true;
                    _ports = _ports.concat(_catalog.ports(data.nodeId, data.modelName, def.requirements, def.capabilities, def.extras));
                    update_ports();
                    _drawGraphs.createNode(pos, data);
                    set_dirty(true);
                }).error(function(response){
                	document.getElementById("loading").style.display = "none";
                	$scope.titlemsg = ALERT;
                	$scope.msg = "Please click on New to create a new Solution";
                	$scope.showpopup();
                });
        }).error(function(response){
        	document.getElementById("loading").style.display = "none";
        	$scope.titlemsg = ALERT;
        	$scope.msg = "Cannot drop this item onto the canvas. <br/> Error details: "+response;
        	$scope.showpopup();
        });
    };

    function reset(){
        $scope.saveState.noSaves = true;
        _dirty = false;
        $('#deleteHide').hide();
        $scope.cleardis = true;
        $scope.validationState = true;
        $scope.saveState.noSaves = true;
        _dirty = false;
        $scope.checkboxDisable = true;
        $scope.myCheckbox = false;
        $scope.activeInactivedeploy = true;
        $scope.console = null;
        $scope.readSolutionArray = [];
        $scope.readSolution = false;
        enteredOk = false;
        savedSolution = false;
        $scope.selectedIndex = null;
        $scope.scriptEntered = false;
        $scope.enableCollateMap = false;
        $scope.collateSelect = false;
        $scope.enableSplitMap = false;
        $scope.collateScheme = null;
        $scope.splitScheme = null;
        $scope.splitSelect = false;
        $scope.collateSourceMapTable = null;
        $scope.collateSourceTables = [];
        $scope.collateTargetMapTable = null;
        $scope.collateTargetTables = [];
        $scope.splitSourceMapTable = null;
        $scope.splitSourceTables = [];
        $scope.splitTargetMapTable = null;
        $scope.splitTargetTables = [];
        $scope.collateSchemes = [];
        $scope.collateSelectedDetails = [];
    	$scope.collateTags = [];
    	$scope.collateErrors = [];
    	$scope.collateSchemeChange = false;
    	$scope.splitSchemeChange = false;
    	$scope.splitSchemes = [];
    	$scope.splitSelectedDetails = [];
    	$scope.splitTags = [];
    	$scope.splitErrors = [];
        $scope.collateErrorMap = new Map();
        $scope.selectedCollateMapping = new Map();
        $scope.splitErrorMap = new Map();
        $scope.solutionIdDeploy = null;
        $scope.matchModels = [];
        $scope.cancelRead = [];
        $scope.showDataBroker = null;
        $scope.showDataMapper = null;
        $scope.showCollator = null;
    	$scope.showSplitter = null;
        $scope.solutionDetails = null;
        $scope.showProperties = null;
        $scope.showLink=null;
        jsonProtoNode = new Map();
        protoJsonRead = new Map();

        $('#validateActive').removeClass('active');
        $('#validateActive').removeClass('enabled');
        $('#consoleMsg').removeClass('console-successmsg');
        $('#consoleMsg').removeClass('console-errormsg');
        $scope.down = true;
        
    }
    
    $scope.newSolution = function(parameter) {
    	if(_dirty && $scope.solutionName != null){$scope.CloseOrNew = 'new';$scope.showsaveConfirmationPopup(); }
        else{
            maybe_save_solution().then(function(cat2) {
                function new_solution(result) {
                    _cid = result.cid;
                    _solution = {nodes: [], edges: []};
                    $scope.solutionName = null;
                    $scope.solutionDescription = null;
                    $scope.solutionVersion = null;
                    
                    reset();
                    if (parameter == 'new'){
                        $scope.titlemsg ="New Solution";
                        $scope.msg = "Create a new Composite Solution";
                        $scope.showok = true;
                        $scope.showpopup();
                    }
       
                    display_solution(_solution);
                    load_catalog();
                    _solutionId = '';
                }
                var userId = get_userId(),
                    url = build_url(options.create, {userId: userId});
                $(".ds-grid-bg").css("background", "url('../images/grid.png')");
                $scope.closeDisabledCheck = !$scope.closeDisabledCheck;
                countComSol += 1;
                $scope.namedisabled = false;
                changeNode = new Object();
                $scope.canvas=false;
                $http.post(url)
                    .success(new_solution);
                
            });}
    };
    $scope.loadSolution = function(entry) {
    	$scope.solDet = entry;
    	$scope.loadSol = true;
    	if(_dirty){$scope.CloseOrNew = 'closeSol';$scope.showsaveConfirmationPopup(); }
    	else{
	        if(entry.toolKit === 'CP' || entry.toolKit === 'DS') {
	            var url = build_url(options.read, {
	                userId:get_userId(),
	                solutionId: entry.solutionId,
	                version: entry.version
	            });
	            $scope.selectedIndex = entry.solutionName+entry.version;
	            var changeNode = new Object();
	            $http.get(url)
	                .success(function(result) {  
	                	
	                    //validflag
	                	if(result.error){
	                		/*$scope.clearSolution();*/
	                		$scope.titlemsg = ALERT;
	                    	$scope.msg = result.error;
	                    	$scope.showpopup();
	                	} else{
	                		var cdump = JSON.parse(result.payload);
	                    if(cdump.validSolution){
	                    	$scope.activeInactivedeploy = false;
	                    	$scope.validationState = true;
	                    	$scope.solutionIdDeploy = cdump.solutionId;
	                    }else{
	                    	$scope.activeInactivedeploy = true;
	                    	$scope.validationState = false;
	                    }
	                    
	                    if(cdump.probeIndicator == 'true'){
	                    	$scope.myCheckbox = true;
	                    }else{
	                    	$scope.myCheckbox = false;
	                    }
	                    savedSolution = true;
	                    $scope.checkboxDisable = false;
	                    $scope.cleardis = false;
	                    $scope.namedisabled = true;$scope.canvas = true;
	                    _solutionId = entry.solutionId;
	                    
	                    $scope.solutionName = cdump.cname;
	                    $scope.solutionVersion = cdump.version;
	                    $scope.solutionDescription = result.description;
	                    _solution = cdump;
	                    _solution.nodes.forEach(function(n) {
	                        if(n.ndata && n.ndata.fixed && n.ndata.px !== undefined && n.ndata.py !== undefined)
	                            n.fixedPos = {x: +n.ndata.px, y: +n.ndata.py};
	                    });
	                    $(".ds-grid-bg").css("background", "url('../images/grid.png')");
	                    $scope.closeDisabled = false;
	                    display_solution(_solution);
	                	}
	                }).error(function(result){
	                	$scope.titlemsg = ALERT;
	                	$scope.msg = "Cannot load the solution";
	                	$scope.showpopup();
	                });
	        }
	        $scope.loadSol = false;
        }
    	
    };
    $scope.storesolutionName ='';
    var duplicateSol = false;
    $scope.saveSolution = function() {
        if(!_dirty && duplicateSol == false)
            return;
	        duplicateSol = false;
        if(!$scope.solutionName) {
            set_focus('input-name');
            return;
        }
       
        if(!$scope.solutionVersion) {
            set_focus('input-version');
            return;
        }
        $scope.storesolutionName = $scope.solutionName;
        save_solution($scope.solutionName)
            .then(function() {
                $scope.namedisabled = true;
                load_catalog();
                _dirty = false;
                savedSolution = true;
            });
    };
    
    $scope.deleteSolution = function(val) {
        var url ='';
        url = build_url(options.deleteCompositeSolution, {
            userid:get_userId(),
            solutionid :val.solutionId,  // solutionid :
            version :  val.version,  // version :
        });
        $http.post(url)
            .success(function(result) {
            	if(result.success == "true"){
                    load_catalog().success(load_initial_solution);
                    $scope.msg= "Solution is deleted successfully";
                    $scope.titlemsg ="Delete Solution";
                    $scope.myCheckbox = false;
                    $scope.checkboxDisable = true;
                    solutionPrPB();$scope.closePoup();
                    $scope.showpopup();
                    if(_solutionId == val.solutionId){
                        $scope.clearSolution();
                        $scope.solutionDetails = false;$scope.solutionDescription = '';
                        _cid = '';_solutionId = '';$scope.solutionName = '';$scope.namedisabled = false;$scope.solutionVersion = '';
                    }
                }
                else if(result.success == "false"){
                    $scope.msg= "Solution is not deleted";
                    $scope.titlemsg ="Delete Solution";
                    $scope.closePoup();
                    $scope.showpopup();
                }
            });
    };

    var qs = querystring.parse();
    var urlBase = baseURL + '/dsce/';
    var options = Object.assign({
    	base:"dsce/dsce/",
    	catalog: 'solution/getSolutions',
        typeinfo: 'artifact/fetchJsonTOSCA',
        create: 'solution/createNewCompositeSolution',
        addNode: 'solution/addNode',
        addLink: 'solution/addLink',
        save: 'solution/saveCompositeSolution',
        validate:'solution/validateCompositeSolution',
        setProbe:'solution/setProbeIndicator',
        read: 'solution/readCompositeSolutionGraph',
        catformat: 'acumos',
        solution: '',
        deleteNode : 'solution/deleteNode',
        deleteLink : 'solution/deleteLink',
        closeCompositeSolution  : 'solution/closeCompositeSolution ',
        modifyNode : 'solution/modifyNode',
        modifyLink : 'solution/modifyLink',
        deleteCompositeSolution : 'solution/deleteCompositeSolution' ,
        clearCompositeSolution : 'solution/clearCompositeSolution',
        getCompositeSolutions :'solution/getCompositeSolutions',
        protobuf: 'artifact/fetchProtoBufJSON',
        getMatchingModels: 'solution/getMatchingModels'
    }, qs);

    function build_url(verb, params) {
        return options.base + verb + '?' + Object.keys(params).map(function(k) {
            return k + '=' + encodeURIComponent(params[k]);
        }).join('&');
    }

    function is_wildcard_type(type) {
    	if(type !== "script"){
    		return type[0].messageName === 'ANY'; // replace with correct
    	} else{
    		return false;
    	}
    }
    function removeMsgNames(msgType) {
        return msgType.map(function(msg) {
            return msg.messageargumentList ? msg.messageargumentList.map(function(arg) {
                return {
                    role: arg.role,
                    tag: arg.tag,
                    type: arg.type
                };
            }) : [];
        });
    }

    function acumos_catalog_reader(catalog) {
        var _toolKitNames = null;
        function toolKitName(code) {
            if(!_toolKitNames)
                _toolKitNames = _toolkits.reduce(function(map, entry) {
                    map[entry.toolkitCode] = entry.toolkitName;
                    return map;
                }, {});
            return _toolKitNames[code];
        };
        function verstring(reqid, reqver, resid, resver) {
            return reqid + '@' + reqver + '-' + resid + '@' + resver;
        }
        return {
            models: function() {
                return catalog.items;
            },
            composites: function() {
                return [];
            },
            fModelId: function(model) {
                return model.solutionId;
            },
            fModelVersion: function(model) {
                return model.version;
            },
            fModelName: function(model) {
                return model.solutionName;
            },
            fModelKey: function(model) {
            	return model.solutionName+'+'+model.version;
            },
            fModelCategory: function(model) {
                return model.category === undefined ? "others" : model.category;
            },
            fModelToolKit: function(model) {
                return (!model || model.toolKit === 'null') ? null : model.toolKit;
            },
            fModelUrl: function(model) {
                if(this.fModelToolKit(model) === 'DS') {
                    return build_url(options.read, {
                        userId:get_userId(),
                        solutionId: this.fModelId(model),
                        version: this.fModelVersion(model)
                    });
                } else {
                    return build_url(options.typeinfo, {
                        userId:get_userId(),
                        solutionId: this.fModelId(model),
                        version: this.fModelVersion(model)
                    });
                }
            },
            fCompositeId: function(comp) {
                return comp.name;
            },
            fTypeName: function(type) {
                return type.name;
            },
            ports: function(nid, ntype, requirements, capabilities, extras) {
            if(ntype === "DataBroker"){
            	return requirements.map(function(req,i){ return {
                    nodeId: nid,
                    portname: req.capability.id+'+'+JSON.stringify(removeMsgNames(req.capability.name))+'+req'+i,
                    type: is_wildcard_type(req.capability.name) ? null : JSON.stringify(removeMsgNames(req.capability.name)),
                    fullType: req.capability.name,
                    originalType: req.capability.name,
                    shortname: req.capability.id,
                    bounds: outbounds,
                    nodeType: ntype
                };}).concat(
                    		extras.map(function(ext,i){ return {
                    			nodeId: nid,
                    			portname: 'xtra'+i,
                    			type: is_wildcard_type(ext) ? null : ext,
                    			originalType: ext,
                    			bounds: xtrabounds,
                    			nodeType: ntype
                    		};}));
	                    
            	} else {
            		return requirements.map(function(req,i){ return {
	                    nodeId: nid,
	                    portname: req.capability.id+'+'+JSON.stringify(removeMsgNames(req.capability.name))+'+req'+i,
	                    type: is_wildcard_type(req.capability.name) ? null : JSON.stringify(removeMsgNames(req.capability.name)),
	                    fullType: req.capability.name,
	                    originalType: req.capability.name,
	                    shortname: req.capability.id,
	                    bounds: outbounds,
	                    nodeType: ntype
	                };}).concat(
	                    capabilities.map(function(cap,i) { return {
	                        nodeId: nid,
	                        portname: cap.target.id+'+'+JSON.stringify(removeMsgNames(cap.target.name))+'+cap'+i,//
	                        type: is_wildcard_type(cap.target.name) ? null : JSON.stringify(removeMsgNames(cap.target.name)),
	                        fullType: cap.target.name,
	                        originalType: cap.target.name,
	                        shortname: cap.target.id,
	                        bounds: inbounds,
	                        nodeType: ntype
	                    };}));
            	}
            }
        };
    }

    var catalog_readers = {
        'acumos': acumos_catalog_reader
    };

    function set_focus(id) {
        var element = window.document.getElementById(id);
        if(element)
            element.focus();
    }

    function get_userId() {
        var userDetail = browserStorageService.getUserDetail();
        if(!userDetail){
        	var modalService = $injector.get('$mdDialog'); 
		modalService.show({
			 templateUrl: '../app/header/sign-in-promt-modal-box.html',
			 clickOutsideToClose: true,
			 controller : function DialogController($scope ) {
				 $scope.closeDialog = function() {
					 modalService.hide();
					 $rootScope.showAdvancedLogin();
			    	 $state.go('home');
		     } }
			});
        }
        if(typeof userDetail === 'string')
            userDetail = JSON.parse(userDetail);
        return userDetail[1];
    }

    function get_catalog() {
        var userId = get_userId();
        return $http.get(build_url(options.catalog, {userId: userId}));
    }

    // canvas
    var _diagram, _rendered = false, _drawGraphs, _solution,  _ports = [];
    var _dirty = false;
    var _cid, _solutionId;
    // palette
    var _catalog, _components, models; var matchingModels = [];

    function set_dirty(whether, message) {
        _dirty = whether;
        if(whether) {
            $scope.saveState.noSaves = false;
            $scope.saveState.desc = message || 'solution has changes';
        } else {
            $scope.saveState.noSaves = true;
            $scope.saveState.desc = message || 'solution is saved';
        }
    }
    
    function redraw_promise(diagram) {
        return new Promise(function(resolve, reject) {
            diagram.on('end', function() {
                resolve();
            });
            diagram.redraw();
        });
    }

    var lbounds = [Math.PI*5/6, -Math.PI*5/6], rbounds = [-Math.PI/6, Math.PI/6],
        dbounds = [Math.PI/6, Math.PI*5/6], ubounds = [-Math.PI*5/6, -Math.PI/6];
    var inbounds, outbounds,xtrabounds;
    if(options.TB) {
        inbounds = ubounds;
        outbounds = dbounds;
        xtrabounds = [Math.PI, Math.PI]; 
    } else  {
        inbounds = lbounds;
        outbounds = rbounds;
        xtrabounds = [-Math.PI/2, -Math.PI/2]; 
    }
    function update_ports() {
        var port_flat = dc_graph.flat_group.make(_ports, function(d) { return (d.nodeId + '/' + d.portname) });

        _diagram
            .portDimension(port_flat.dimension).portGroup(port_flat.group);
    }

    function display_solution(solution) {
        $('#deleteHide').hide();
        $scope.databroker.$invalid = false;
        $scope.console = null;
        $('#validateActive').removeClass('active');
        $('#validateActive').removeClass('enabled');
        $('#consoleMsg').removeClass('console-successmsg');
        $('#consoleMsg').removeClass('console-errormsg');
        $scope.down = true;
        var script = [];
        
        _diagram.child('fix-nodes')
            .clearFixes();
        var nodes = solution.nodes || (console.warn('no nodes in composite solution!'), []),
            edges = solution.relations || [];
        _ports = [];
        /*$scope.readSolutionArray = true;*/
        var i=0;
        nodes.forEach(function(n) {
        	script = [];
            var lastChar = n.nodeId.slice(-1);
            var res = n.nodeId.split(lastChar);
            if(n.type.name == "DataBroker"){
            	script = ["script"];
            }
            _ports = _ports.concat(_catalog.ports(n.nodeId, n.type.name,n.requirements, n.capabilities, script));
            var url= build_url(options.protobuf, {
                userId: get_userId(),
                solutionId :  n.nodeSolutionId,
                version : n.nodeVersion
            });
            $http.get(url).success(function(proto){
                protoJsonRead.set(res[0],proto);
             });
        });

        var node_flat = dc_graph.flat_group.make(nodes, function(d) { return d.nodeId; }),
            edge_flat = dc_graph.flat_group.make(edges, function(d) { return d.linkId; });

        _diagram
            .nodeDimension(node_flat.dimension).nodeGroup(node_flat.group)
            .edgeDimension(edge_flat.dimension).edgeGroup(edge_flat.group);
        update_ports();
        _drawGraphs
            .nodeCrossfilter(node_flat.crossfilter)
            .edgeCrossfilter(edge_flat.crossfilter);

        if(!_rendered) {
            _diagram.render();
            _rendered = true;
        } else _diagram.redraw();
        
        $scope.userImage ='';
        nodes.forEach(function(n){
            if(n.type.name === "DataMapper"){
                var DM = new Map();
                angular.forEach(n.properties, function(value, key){
                    angular.forEach(value.data_map.map_inputs, function(value1, key1){
                        angular.forEach(value1.input_fields, function(value2, key2){
                            if(value2.mapped_to_message !== "")
                                DM.set(value2.tag, value2.mapped_to_field);
                        });
                    });
                });
                dataMaps.set(n.nodeId, DM);
            }
            
            if(n.type.name === "DataBroker"){
            	$scope.uploadFileRequired = false;
            	$scope.nodeIdDB = n.nodeId;
            	$scope.readSolutionArray[n.nodeId] = true;
            	angular.forEach(n.properties, function(value, key){
            		if(value.data_broker_map != null){
            			
            			$scope.scriptText = value.data_broker_map.script;
            			$scope.dbType = value.data_broker_map.data_broker_type;
            			$scope.fileUrl = value.data_broker_map.target_system_url;
            			$scope.firstRow = value.data_broker_map.first_row;
            			$scope.databaseName = value.data_broker_map.database_name;
 	    				$scope.tableName = value.data_broker_map.table_name;
 	    				$scope.jdbcDatasource = value.data_broker_map.jdbc_driver_data_source_class_name;
            			if(!$scope.userImage){
            				$scope.userImageNew =value.data_broker_map.local_system_data_file_path;
            			}
            			$scope.readSourceTable = value.data_broker_map.map_inputs;
            			if($scope.dbType == 'csv' || $scope.dbType == "sql"){
            				$scope.dataShow = $scope.readSourceTable;
            			} else if($scope.dbType == "image"){
            				$scope.dataImage = $scope.readSourceTable;
            			}
            			readScriptDetails = {'dbtype':value.data_broker_map.data_broker_type,
            					'targeturl':value.data_broker_map.target_system_url,
            					'script':value.data_broker_map.script,
            					'firstrow':value.data_broker_map.first_row,
            					'localfile':value.data_broker_map.local_system_data_file_path,
            					'databasename': value.data_broker_map.database_name,
            					'tablename': value.data_broker_map.table_name,
            					'jdbcdatasource': value.data_broker_map.jdbc_driver_data_source_class_name,
            					'mapInputs':value.data_broker_map.map_inputs};
            			$scope.readSolutionMapping();
            			$scope.checkedRead = true;
            			$scope.uncheckedRead = false;
            			$scope.readSolutionArray[n.nodeId] = true;
            		}
            	});
            }
            
            if(n.type.name === "Collator"){
            	$scope.readSolutionArray[n.nodeId] = true;
            	
            	angular.forEach(n.properties, function(value, key){
            		if(value.collator_map !== null){

            			$scope.collateSchemes[n.nodeId] = value.collator_map.collator_type;
            			if($scope.collateSchemes[n.nodeId] === "Parameter-based"){
            				$scope.collateTargetTables[n.nodeId] = [];
            				$scope.collateSourceTables[n.nodeId] = [];
	            			readCollatorMapping(value.collator_map.map_inputs,n.nodeId);
	            			
	            			collateDetails = { "collatorType": value.collator_map.collator_type,
    								"mapInputs": value.collator_map.map_inputs,
    								"mapOutputs": value.collator_map.map_outputs};
            			} else{
            				collateDetails = { "collatorType": value.collator_map.collator_type};
            			}
            			
            		}
            	});
            }
            
            if(n.type.name === "Splitter"){
            	$scope.readSolutionArray[n.nodeId] = true;
            	angular.forEach(n.properties, function(value, key){
            		if(value.splitter_map !== null){
            			$scope.splitSchemes[n.nodeId] = value.splitter_map.splitter_type;
            			if($scope.splitSchemes[n.nodeId] === "Parameter-based"){
            				$scope.splitTargetTables[n.nodeId] = [];
            				$scope.splitSourceTables[n.nodeId] = [];
            				readSplitterMapping(value.splitter_map.map_outputs, n.nodeId);
            				splitDetails = {"splitterType" : value.splitter_map.splitter_type,
        							"mapInputs" : value.splitter_map.map_inputs,
        							"mapOutputs" : value.splitter_map.map_outputs};
            			} else{
            				splitDetails = {"splitterType" : value.splitter_map.splitter_type};
            			}
            		}
            	});
            }
        });
        
        edges.forEach(function(e){
            var srcPort = _diagram.getPort(e.sourceNodeId, null, e.sourceNodeRequirement),
                tarPort = _diagram.getPort(e.targetNodeId, null, e.targetNodeCapability);
            
            wildcardPorts.copyType(e, srcPort, tarPort);
            nodes.forEach(function(n){
            	if((n.nodeId === e.sourceNodeId || n.nodeId === e.targetNodeId) && n.type.name === "DataBroker"){
            		if(srcPort.orig.value.bounds === outbounds){
                    	targetTableCreate(srcPort);
                    } else if(tarPort.orig.value.bounds === outbounds){
                    	targetTableCreate(tarPort);
                    }
            	}
            	
            	if(n.nodeId === e.sourceNodeId && n.type.name === "Collator" && n.properties[0].collator_map.collator_type === "Parameter-based"){
            		if(srcPort.orig.value.bounds === outbounds)
            			createCollateTargetTable(tarPort,srcPort);
            	}
            	
            	if(n.nodeId === e.targetNodeId && n.type.name === "Collator" && n.properties[0].collator_map.collator_type === "Parameter-based"){
            		if(tarPort.orig.value.bounds === inbounds)
            			createCollateSourceTable(srcPort,tarPort);
            	}
            	
            	if(n.nodeId === e.sourceNodeId && n.type.name === "Splitter" && n.properties[0].splitter_map.splitter_type === "Parameter-based"){
            		if(srcPort.orig.value.bounds === outbounds)
            			createSplitTargetTable(tarPort,srcPort);
            	}
            	
            	if(n.nodeId === e.targetNodeId && n.type.name === "Splitter" && n.properties[0].splitter_map.splitter_type === "Parameter-based"){
            		if(tarPort.orig.value.bounds === inbounds)
            			createSplitSourceTable(srcPort,tarPort);
            	}
            	
            });
            
        });
        
    }

    //
    // SAVE AREA
    // & loading composite solutions
    //
    var updateOldSol = false;
    $scope.valiateCompSolu = function(){
    };
    function save_solution(name) {

        _solution.nodes = _drawGraphs.nodeCrossfilter().all();
        _solution.edges = _drawGraphs.edgeCrossfilter().all();
        var userId = get_userId();

        if(!userId)
            throw new Error('not logged in!');
        if(!_cid && !_solutionId)
            throw new Error('trying to save but not initialized');
        return _diagram.child('fix-nodes').fixAllNodes().then(function() {
            var args = {
                userId: userId,
                solutionName: $scope.solutionName,
                version: $scope.solutionVersion,
                description: $scope.solutionDescription,
                ignoreLesserVersionConflictFlag: false
            };
            if(_solutionId)
                args.solutionId = _solutionId;
            else if(_cid)
                args.cid = _cid;
            if(updateOldSol){args.ignoreLesserVersionConflictFlag = true}
            var url = build_url(options.save, args);
            return $http.post(url).success(function(result) {
                // result.duplicate = 'duplicate';
                if(result.errorCode){
                	$scope.titlemsg = ALERT;
                	$scope.msg= "Solution not saved "; 
                	$scope.showpopup();
                	enteredOk = true;
                	}
                else if(result.duplicate){
                	$scope.titlemsg = ALERT;
                	$scope.msg = result.duplicate; 
                	duplicateSol = true; 
                	$scope.showpopup(); 
                	enteredOk = true;
                }
                else if(result.alert){
                    set_dirty(false, 'saved at ' + d3.time.format('%X')(new Date()));
                    solutionNameSave = name;
                    updateOldSol = true;
                    $scope.myDialogOldVersionSave();
                }
                else {
                    $scope.closePoup();
                    $scope.titlemsg = "Save Solution";
                    updateOldSol = false;
                    $scope.msg = "Solution saved successfully";
                    $scope.validationState = false;
                    $('#validateActive').removeClass('active');
                    $('#validateActive').addClass('enabled');
                    $scope.showpopup();
                    solutionPrPB();
                    $scope.saveState.noSaves = true;
                    _dirty = false;
                    _solutionId = result.solutionId;
                }
            });
        });
    }
    
    $scope.validateCompSolu = function(){
        var args = {
            userId: get_userId(),
            solutionName: $scope.solutionName,
            solutionId: _solutionId,
            version:$scope.solutionVersion
        };                     
        var url = build_url(options.validate, args);
        $('#validateActive').removeClass('enabled');
        $scope.validationState = true;

        return $http.post(url).success(function(result) {
            if(result.success == "true"){
            	 
                $scope.validationState = true;
                $scope.activeInactivedeploy = false;
                $scope.solutionIdDeploy = _solutionId;
                $('#validateActive').removeClass('enabled');
                $('#validateActive').addClass('active');
                $('#consoleMsg').addClass('console-successmsg');
                $('#consoleMsg').removeClass('console-errormsg');
                $scope.console = "Valid composite Solution";
            } else {
            	$scope.activeInactivedeploy = true;
                $scope.validationState =false;
                $('#consoleMsg').removeClass('console-successmsg');
                $('#consoleMsg').addClass('console-errormsg');
                $scope.console = result.errorDescription;
            }
            $scope.down = false;
        });

    };
    function maybe_save_solution() {
    	if(_dirty){$scope.showsaveConfirmationPopup();$scope.CloseOrNew = 'new';return}
        var deferred = $q.defer();
        if(!_dirty || !confirm('Current solution is unsaved - save it now?')) {
            deferred.resolve(undefined);
            return deferred.promise;
        }
        if(!$scope.solutionName)
            $scope.solutionName = prompt('Enter a solution name', 'Solution');
        if(!$scope.solutionName) {
            deferred.resolve(undefined);
            return deferred.promise;
        }
        return save_solution(_catalog, name);   
    }
    
    function print_value(v) {
        if(!v || ['string','number','boolean'].indexOf(typeof v) !== -1)
            return v.toString();
        else
            return JSON.stringify(v);
    }
    
    function display_properties(url) {
        $scope.initIndex=false;
        $scope.listNavs=[];
        $scope.navsKeys=[];
        var content = d3.select('#properties-content');
        if(url) {
            $http.get(url).success(function(comp) {
                var table = d3.select('#properties-table');

                $scope.packageName= JSON.stringify(comp.self.name);
                $scope.capabilityProvides=comp.services.provides;
                $scope.requireCalls=comp.services.calls;
                
                var keys = Object.keys(comp).sort();
                $scope.listNavs=comp;
                $scope.navsKeys=keys;
            });
        }
    }
    
    function display_data_mapper(nodeId, wilds) {
        if(wilds.length !== 2) {
            console.warn('expecting exactly two ports on data mapper');
            return;
        }
        if(wilds[0].bounds === wilds[1].bounds) {
            console.warn('expecting data mapper ports to be on opposite sides');
            return;
        }
        // create graph data from the port type info
        var lport, rport;
        if(wilds[0].bounds === outbounds) {
            rport = wilds[0];
            lport = wilds[1];
        } else {
            lport = wilds[0];
            rport = wilds[1];
        }

        var lnodes = [], rnodes = [];
        if(lport.fullType && lport.fullType[0].messageName !== 'ANY') {
            // take nodes from type in first message
            lnodes = lport.fullType[0].messageargumentList.map(function(type, i) {
                var typeparts = type.role ? [type.role, type.type] : [type.type];
                return {
                    id: 'source' + (i+1),
                    label: [i+1, type.name, typeparts.join(' ')],
                    tag: type.tag
                };
            });
        }
        if(rport.type && rport.fullType[0].messageName !== 'ANY') {
            rnodes = rport.fullType[0].messageargumentList.map(function(type, i) {
                var typeparts = type.role ? [type.role, type.type] : [type.type];
                return {
                    id: 'dest' + (i+1),
                    label: [i+1, type.name, typeparts.join(' ')],
                    tag: type.tag
                };
            });
        }
        var vertical = false;
        var parentNodes = [
            {
                id: 'top',
                flexDirection: vertical ? 'column' : 'row',
                justifyContent: 'space-around',
                sort: function(a,b) { return a.node.order - b.node.order}
            },
            {
                order: 1,
                id: 'col-source',
                flexDirection: vertical ? 'row' : 'column',
                justifyContent: 'flex-start',
                alignItems: 'flex-end',
                flex: 1
            },
            {
                order: 1.5,
                id: 'col-center',
                flex: 0.5,
                width: 0,
                minWidth: 15,
                maxWidth: 75
            },
            {
                order: 2,
                id: 'col-dest',
                flexDirection: vertical ? 'row' : 'column',
                justifyContent: 'flex-start',
                alignItems: 'flex-start',
                flex: 1
            }
        ];
        var mlbounds = [Math.PI-1, Math.PI+1], mrbounds = [-1,1],
            mubounds = [-Math.PI/2-1, -Math.PI/2+1], mdbounds = [Math.PI/2-1, Math.PI/2+1];
        var minbounds, moutbounds;
        if(vertical) {
            minbounds = mubounds;
            moutbounds = mdbounds;
        } else  {
            minbounds = mlbounds;
            moutbounds = mrbounds;
        }
        var ports = lnodes.map(function(n) { return {
            nodeId: n.id,
            side: 'out',
            bounds: moutbounds
        };}).concat(rnodes.map(function(n){ return {
            nodeId: n.id,
            side: 'in',
            bounds: minbounds
        };}));
        var nodes = parentNodes.concat(lnodes, rnodes);
        var edges=[];
        var sourcenameId, targetnameId;
        var DM = dataMaps.get(nodeId);
        if(!DM)
            dataMaps.set(nodeId, DM = new Map());
        angular.forEach(DM, function(value, key){
            var l = lnodes.find(function(n) {
                return n.tag === key;
            }), r = rnodes.find(function(n) {
                return n.tag === value;
            });
            edges.push({
                sourcename: l && l.id,
                targetname: r && r.id
            });
        });

        var node_flat = dc_graph.flat_group.make(nodes, function(n){return n.id}),
    		edge_flat = dc_graph.flat_group.make(edges, function(e){ return e.sourcename}),
    		port_flat = dc_graph.flat_group.make(ports, function(p){return p.nodeId + '/' + p.side});

        var sdRegex = /^(source|dest)/;
        var layout = dc_graph.flexbox_layout()
            .addressToKey(function(ad) {
                switch(ad.length) {
                case 0: return 'top';
                case 1: return 'col-' + ad[0];
                case 2: return ad[0] + ad[1];
                default: throw new Error('not expecting more than depth 2');
                }
            })
            .keyToAddress(function(key) {
                if(key==='top') return [];
                else if(/^col-/.test(key)) return [key.split('col-')[1]];
                else if(sdRegex.test(key)) {
                    return [
                        sdRegex.exec(key)[1],
                        +key.split(sdRegex)[2]
                    ];
                } else throw new Error('couldn\'t parse key: ' + key);
            });

        var mapper = dc_graph.diagram('#data-mapper', 'mapper')
            .layoutEngine(layout)
            .minHeight(50).minWidth(50)
            .width(null).height(null) // calculate from div
            .transitionDuration(250)
            .mouseZoomable(false)
            .nodeDimension(node_flat.dimension).nodeGroup(node_flat.group)
            .edgeDimension(edge_flat.dimension).edgeGroup(edge_flat.group)
            .portDimension(port_flat.dimension).portGroup(port_flat.group)
            .nodeShape(function(n) {return layout.keyToAddress()(mapper.nodeKey()(n)).length < 2 ? 'nothing' : 'rounded-rect'})
            .nodeStrokeWidth(0)
            .nodeTitle(null)
            .fitStrategy('zoom')
            .nodeLabelAlignment(function(n){return /^source/.test(n.key) ? 'right' : 'left'})
            .nodeLabelPadding({x: 10, y: 0})
            .edgesInFront(true)
            .edgeSourcePortName('out')
            .edgeTargetPortName('in')
            .edgeLabel(null)
            .portNodeKey(function(p){return p.value.nodeId})
            .portName(function(p){ return p.value.side})
            .portBounds(function(p){return p.value.bounds})
            .portElastic(false);

        mapper.child('validate', dc_graph.validate('data mapper'));
        mapper.child('place-ports', dc_graph.place_ports());

        var circlePorts = dc_graph.symbol_port_style()
            .portSymbol(null)
            .outlineStroke('black')
            .outlineStrokeWidth(1)
            .displacement(0)
            .smallRadius(2)
            .mediumRadius(4)
            .largeRadius(6);

        mapper.portStyle('circles', circlePorts)
            .portStyleName('circles');

        var drawGraphs = dc_graph.draw_graphs({
            idTag: 'id',
            sourceTag: 'sourcename',
            targetTag: 'targetname',
            select_nodes_group: 'select-mapper-nodes-group',
            select_edges_group: 'select-mapper-edges-group'
        })
            .usePorts(true)
            .clickCreatesNodes(false)
            .edgeCrossfilter(edge_flat.crossfilter)
            .addEdge(function(edge) {
                var source = mapper.getNode(edge.sourcename),
                    target = mapper.getNode(edge.targetname);
                dataMaps.get(nodeId).set(source.value.tag, target.value.tag);
                // call API to modify node accordingly
                var params, rnodeDetails, lnodeDetails, url;

                for(var j=0;j<rnodes.length;j++){
                    if(rnodes[j].id === edge.targetname){
                        rnodeDetails = rnodes[j];
                    }
                }
                for(var i=0;i<lnodes.length;i++){
                    if(lnodes[i].id === edge.sourcename){
                        lnodeDetails = lnodes[i];
                    }
                }
                
                var dataConnector = {
            			"databrokerMap": null,
            			 "fieldMap":{
                              "input_field_message_name": lport.fullType[0].messageName,
                              "input_field_tag_id": lnodeDetails.tag,
                              "map_action": "add",
                              "output_field_message_name": rport.fullType[0].messageName,
                              "output_field_tag_id": rnodeDetails.tag
                          },
                          "collatorMap": null,
            			  "splitterMap": null
            		};
              
                if(_solutionId){
                    params = {
                        userid: get_userId(),
                        solutionid: _solutionId,
                        version: $scope.solutionVersion,
                        nodeid: lport.nodeId
                    };
                } else if(_cid){
                    params = {
                        userid: get_userId(),
                        cid: _cid,
                        nodeid: lport.nodeId
                    };
                }

                url = build_url(options.modifyNode, params);
               
                return $http.post(url,dataConnector)
                    .then(function(response){
                        $scope.saveState.noSaves = false;
                        $scope.validationState = true;
                        $scope.activeInactivedeploy = true;
                        _dirty = true;
                       
                        return Promise.resolve(edge);
                    });
                });

        mapper.child('draw-graphs', drawGraphs);

        var select_edges = dc_graph.select_edges({
            edgeStroke: 'lightblue',
            edgeStrokeWidth: 3
        }, {
            select_edges_group: 'select-mapper-edges-group'
        }).multipleSelect(false);
        mapper.child('select-edges', select_edges);

        var select_edges_group = dc_graph.select_things_group('select-mapper-edges-group', 'select-edges');
        var delete_edges = dc_graph.delete_things(select_edges_group, 'delete-edges',  'sourcename')
            .crossfilterAccessor(function(chart) {
                return edge_flat.crossfilter;
            })
            .dimensionAccessor(function(chart) {
                return mapper.edgeDimension();
            })
            .onDelete(function(edges) {
                var promises = edges.map(function(eid) {
                    var edgeDetails;
                    var params, rnodeDetails, lnodeDetails, url;

                    edgeDetails = mapper.getWholeEdge(eid);
                    dataMaps.get(nodeId).delete(edgeDetails.source.orig.value.tag);
                    
                });
                return Promise.all(promises)
                    .then(function(responses){
                        $scope.saveState.noSaves = false;
                        $scope.validationState = true;
                        $scope.activeInactivedeploy = true;
                        _dirty = true;
                        return Promise.resolve(edges);
                    });
            });
        mapper.child('delete-edges', delete_edges);

        var oppositeMatcher = dc_graph.match_opposites(mapper, {
            edgeStroke: 'orangered'
        }, {
            delete_edges: delete_edges
        });
        drawGraphs.conduct(oppositeMatcher);
        mapper.render();
    }
    
    $scope.getProperties=function(solutionHover,clickVersion,clickRevision){
    	
        var compsHover = _catalog.models().filter(function(comp) {
            return _catalog.fModelName(comp) === solutionHover;
        });
        $scope.nodeNameUI=null;
        $scope.showDataBroker = null;
        $scope.showDataMapper = null;
        $scope.showCollator = null;
    	$scope.showSplitter = null;
        $scope.showProperties=null;
        $scope.showLink=null;
        compsHover.forEach(function(n){
        	if(n.solutionName === solutionHover && n.version === clickVersion && n.solutionRevisionId === clickRevision)
        		$scope.solutionDetails=n;
        });
        
        $scope.selectedIndex = $scope.solutionDetails.solutionName+$scope.solutionDetails.version+$scope.solutionDetails.solutionRevisionId.substring(0,4);
        if(compsHover[0].toolKit != 'CP'){
           if(compsHover.length === 1)
                display_properties(_catalog.fModelUrl(compsHover[0]));
        } else
            $scope.packageName=null;
    };

    var _ionicons = {
        AlarmGenerator: 'app/design-studio/img/ios-bell.svg',
        Classifier: 'app/design-studio/img/stats-bars.svg',
        Aggregator: 'app/design-studio/img/network.svg',
        Predictor: 'app/design-studio/img/android-bulb.svg',
        Recommender: 'app/design-studio/img/thumbsup.svg',
        DataMapper: 'app/design-studio/img/shuffle.svg',
        DS: 'app/design-studio/img/soup-can.svg',
        Training: 'app/design-studio/img/loop.svg',
        Others: 'app/design-studio/img/aperture.svg',
        Images: 'app/design-studio/img/images.svg'

    };

    var layout = dc_graph.cola_layout()
        .baseLength(5)
        .groupConnected(true)
        .handleDisconnected(false)
        .flowLayout({
            axis: options.rankdir==='TB' ? 'y' : 'x',
            minSeparation: options.rankdir==='TB' ? function(e) {
                return (e.source.height + e.target.height) / 2 + layout.ranksep();
            } : function(e) {
                return (e.source.width + e.target.width) / 2 + layout.ranksep();
            }
        });
  
    function makeRepeatedComplexType(msg) {
    		return {
    			messageName : "collateOutput",
    			messageargumentList : [{
    				role: "repeated",
    				name: "prediction",
    				tag: "1",
    				type: msg.messageName,
    				complexType: {"messageName": msg.messageName,
    								"messageargumentList": msg.messageargumentList}
    			}]
			};
    } 
    
    function makeTypeFromComplex(msgType){
    	return msgType.map(function(msg) {
    		var tagLocal = 1;
            return msg.messageargumentList ? msg.messageargumentList.map(function(arg) {
                return {
                    role: arg.role,
                    tag: arg.tag.substring(arg.tag.length-1),
                    type: arg.type
                };
            }) : [];
        });
    }
    // used for copying the messages into "any" port
    var wildcardPorts = dc_graph.wildcard_ports({
        get_type: function(p){return p.orig.value.type},
        set_type: function(p1, p2) {
        	if(p1.node.orig.value.type.name === "Collator"){
        		if($scope.collateSchemes[p1.node.orig.key] === "Array-based" && $scope.readSolutionArray[p1.node.orig.key] === false){
	        		if(p1.orig.value.bounds === outbounds){
	        			_ports.forEach(function(port){
	            			if(port.nodeId === p1.node.orig.key && port.bounds !== p1.orig.value.bounds){
	            				if(p2) {
	                                p1.orig.value.type = p2.orig.value.type;
	                                p1.orig.value.fullType = p2.orig.value.fullType;
	                                port.type = JSON.stringify(makeTypeFromComplex([p2.orig.value.originalType[0].messageargumentList[0].complexType]));
	                                port.fullType = [p2.orig.value.originalType[0].messageargumentList[0].complexType];
	                            } else {
	                            	p1.orig.value.type = null;
	                            	p1.orig.value.fullType = p1.orig.value.originalType;
	                            }
	            			} 
	        			});
	        		} else {
	        			_ports.forEach(function(port){
	            			if(port.nodeId === p1.node.orig.key && port.bounds !== p1.orig.value.bounds){
	            				
	            				if(p2) {
	            					var portMsg = makeRepeatedComplexType(p2.orig.value.originalType[0]);
	                                p1.orig.value.type = p2.orig.value.type;
	                                p1.orig.value.fullType = p2.orig.value.fullType;
	                                port.type = JSON.stringify(removeMsgNames([portMsg]));
	                                port.fullType = [portMsg];
	                            } else {
	                            	p1.orig.value.type = null;
	                            	p1.orig.value.fullType = p1.orig.value.originalType;
	                            }
	            			}
	        			});
	        		}
        		} else if($scope.collateSchemes[p1.node.orig.key] === "Parameter-based"){
        			if(p1.orig.value.bounds === outbounds){
	        			if(p2) {
	    	                p1.orig.value.type = p2.orig.value.type;
	    	                p1.orig.value.fullType = p2.orig.value.fullType;
	    	            }
	    	            else {
	    	            	p1.orig.value.type = null;
	    	            	p1.orig.value.fullType = p1.orig.value.originalType;
	    	            }
        			}
        		} else{
    	        	if(p2) {
    	                p1.orig.value.type = p2.orig.value.type;
    	                p1.orig.value.fullType = p2.orig.value.fullType;
    	            }
    	            else {
    	            	p1.orig.value.type = null;
    	            	p1.orig.value.fullType = p1.orig.value.originalType;
    	            }
            	}
        	} else if(p1.node.orig.value.type.name === "Splitter"){
        		if($scope.splitSchemes[p1.node.orig.key] === "Copy-based" && $scope.readSolutionArray[p1.node.orig.key] === false){
	        		_ports.forEach(function(port){
	        			if(port.nodeId === p1.node.orig.key && port.bounds !== p1.orig.value.bounds){
	        				if(p2) {
	                            p1.orig.value.type = p2.orig.value.type;
	                            p1.orig.value.fullType = p2.orig.value.fullType;
	                            port.type = p2.orig.value.type;
	                            port.fullType = p2.orig.value.fullType;
	                        } else {
	                        	p1.orig.value.type = null;
	                        	p1.orig.value.fullType = p1.orig.value.originalType;
	                        }
	        			}
	        		});
        		} else if($scope.splitSchemes[p1.node.orig.key] === "Parameter-based"){
        			if(p1.orig.value.bounds === inbounds){
	        			if(p2) {
	    	                p1.orig.value.type = p2.orig.value.type;
	    	                p1.orig.value.fullType = p2.orig.value.fullType;
	    	            }
	    	            else {
	    	            	p1.orig.value.type = null;
	    	            	p1.orig.value.fullType = p1.orig.value.originalType;
	    	            }
        			}
        		} else{
    	        	if(p2) {
    	                p1.orig.value.type = p2.orig.value.type;
    	                p1.orig.value.fullType = p2.orig.value.fullType;
    	            }
    	            else {
    	            	p1.orig.value.type = null;
    	            	p1.orig.value.fullType = p1.orig.value.originalType;
    	            }
            	}
        	} else{
	        	if(p2) {
	                p1.orig.value.type = p2.orig.value.type;
	                p1.orig.value.fullType = p2.orig.value.fullType;
	            }
	            else {
	            	p1.orig.value.type = null;
	            	p1.orig.value.fullType = p1.orig.value.originalType;
	            }
        	}
        },
        is_wild: function(p) {return is_wildcard_type(p.orig.value.originalType)},
        update_ports: update_ports
    });

    function initialize_canvas() {
        _diagram = dc_graph.diagram('#canvas');
        _diagram // use width and height of parent, <section
        // droppable=true>
        	.width(null)
        	.height(null)
            .layoutEngine(layout)
            .timeLimit(500)
            .margins({left: 5, top: 5, right: 5, bottom: 5})
            .transitionDuration(1000)
            .fitStrategy('zoom')
            .restrictPan(true)
            .stageTransitions('insmod')
            .autoZoom('always')
            .enforceEdgeDirection(options.rankdir || 'LR')
            .edgeSource(function(e) { return e.value.sourceNodeId; })
            .edgeTarget(function(e) { return e.value.targetNodeId; })
            .edgeArrowhead(null)
            .edgeLabel(function(e){return e.value.linkName || ''})
            .nodePadding(20)
            .nodeLabel(function(n) {
            	if(n.value.name === "") 
            		return n.value.nodeId;
            	else
            		return n.value.name; })
            .nodeLabelPadding({x: 5, y: 0})
            .nodeTitle(null)
            .nodeStrokeWidth(1)
            .nodeStroke(function(n){
                var edgeStr = _drawGraphs.edgeCrossfilter().all();
                var edgeSearch = []; var i=0;
                angular.forEach(edgeStr, function(value, key) {
                    edgeSearch[i++] = value.sourceNodeId;
                    edgeSearch[i++] = value.targetNodeId;
                });
                for(i = 0; i < edgeSearch.length; i++) {
                    if(n.key === edgeSearch[i]){
                        return ('#777');
                    }
                }
                return ('orangered');
            })
            .edgeStroke('#777')
            .nodeShape(function(n){
            	var res = n.key.slice(0, -1);
            	var nodeDet=_components.get(res+'+'+n.value.nodeVersion);
            	if(nodeDet.toolKit === "CO" || nodeDet.toolKit === "SP" || nodeDet.toolKit === "BR" || /DataMapper/.test(nodeDet.solutionName))
            		return {shape: 'rounded-rect', rx: 0, ry: 0};
                else{
                    if(nodeDet.category === "DS" || nodeDet.category === "DT")
                        return {shape: 'ellipse'};
                    else
                        return {shape: 'rounded-rect'};
                }
            })
            .nodeContent('text-with-icon')
            .nodeIcon(function(d) {
            	var res = d.key.slice(0, -1);
            	var nodeDet=_components.get(res+'+'+d.value.nodeVersion);
                var nodeName = nodeDet.solutionName;

                if(nodeDet.category === "DS")
                    if(nodeDet.solutionName == "ZIPDataBroker"){
                		return _ionicons["Training"];
                	}else{
                		return _ionicons["DS"];
                	}
                else{
                    if(nodeName.indexOf('Image') > -1 || nodeName.indexOf('image') > -1)
                        return _ionicons["Images"];
                    else if(nodeName.indexOf('Training') > -1)
                        return _ionicons["Training"];
                    else if(nodeName === "Classifier" || nodeName === "Predictor" || nodeName === "Aggregator" || nodeName === "DataMapper" || nodeName === "AlarmGenerator")
                        return _ionicons[nodeName];
                    else
                        return _ionicons["Others"];
                }

            })
            .nodeFill(function(n) {
            	var res = n.key.slice(0, -1);
            	var nodeDet=_components.get(res+'+'+n.value.nodeVersion);
            	if(nodeDet.toolKit === "CO" || nodeDet.toolKit === "SP" || nodeDet.toolKit === "BR" || /DataMapper/.test(nodeDet.solutionName))
            		return TOOLCOLOR;
            	else 
            		return WHITE;
            })
            .nodeFixed(function(n) { return n.value.fixedPos; })
            .edgeStroke('#777')
            .portNodeKey(function(p){return p.value.nodeId})
            .portName(function(p){return p.value.portname})
            .portBounds(function(p) {return p.value.bounds})
            .edgeSourcePortName(function(e) {return e.value.sourceNodeRequirement})
            .edgeTargetPortName(function(e){return e.value.targetNodeCapability});

        _diagram.child('validate', dc_graph.validate('design canvas'));
        _diagram.child('keyboard', dc_graph.keyboard().disableFocus(true));
        _diagram.content('text-with-icon', dc_graph.with_icon_contents(dc_graph.text_contents(), 35, 35));
        _diagram.child('place-ports', dc_graph.place_ports());
        
        var symbolPorts = dc_graph.symbol_port_style()
            .portSymbol(function(p){return p.orig.value.type})
            .portColor(function(p){return p.orig.value.type})
            .colorScale(d3.scale.ordinal().range(
                // colorbrewer qualitative scale
                d3.shuffle(
                    ['#e41a1c','#377eb8','#4daf4a','#984ea3','#ff7f00','#eebb22','#a65628','#f781bf'] // 8-class
                    
                )))
            .portBackgroundFill(function(p){return p.value.bounds === inbounds ? 'white' : 'black'});
        
        var letterPorts = dc_graph.symbol_port_style()
    	.content(dc_graph.symbol_port_style.content.letter())  
        .outlineStrokeWidth(1)  
        .symbol('S')  
        .symbolScale(function(x){return x})  
        .color('black')  
        .colorScale(null)
        .portBackgroundFill(WHITE);
        
        var starPorts = dc_graph.symbol_port_style()
        .content(dc_graph.symbol_port_style.content.letter())
        .portSymbol('>')
        .symbolScale(function(x){return x})
        .color('black')
        .colorScale(null)
        .portBackgroundFill(function(p){return p.value.bounds === inbounds ? 'white' : 'black'});
        
        _diagram
            .portStyle('symbols', symbolPorts)
            .portStyle('letters', letterPorts)
            .portStyle('star', starPorts)
            .portStyleName(function(p) {
            	if((p.value.nodeType === "Collator" && p.value.bounds === inbounds) || (p.value.nodeType === "Splitter" && p.value.bounds === outbounds))
            		return 'star';
            	else
            		return (p.value.type === "script") ? 'letters' : 'symbols';  
            	});  

        var portMatcher = dc_graph.match_ports(_diagram, symbolPorts);

        portMatcher.isValid(
        		function(sourcePort, targetPort) {
        			if(targetPort.node.orig.value.type.name === COLLATOR){
        				var m = sourcePort.orig.value.originalType[0].messageargumentList[0];
        				if($scope.collateSchemes[targetPort.node.orig.key] === "Array-based"){
	        				
	        				if(targetPort.orig.value.bounds === outbounds){  					
	        					if(m.complexType && m.role === "repeated"){
	    							return wildcardPorts.isValid(sourcePort, targetPort) &&
				                        sourcePort.orig.value.bounds !== xtrabounds &&  
				                        targetPort.orig.value.bounds !== xtrabounds &&  
				                        sourcePort.orig.value.bounds !== targetPort.orig.value.bounds &&
				                        targetPort.edges.length === 0;
	    						}
	        				} else {
	        					if(m.complexType === undefined && m.role !== "repeated"){
		        					return wildcardPorts.isValid(sourcePort, targetPort) &&
			    		                sourcePort.orig.value.bounds !== xtrabounds &&  
			    		                targetPort.orig.value.bounds !== xtrabounds &&  
			    		                sourcePort.orig.value.bounds !== targetPort.orig.value.bounds;
		        						}
		        				}
        				} else if($scope.collateSchemes[targetPort.node.orig.key] === "Parameter-based"){
        					if(targetPort.orig.value.bounds === outbounds){
		        					return wildcardPorts.isValid(sourcePort, targetPort) &&
					                    sourcePort.orig.value.bounds !== xtrabounds &&  
					                    targetPort.orig.value.bounds !== xtrabounds &&  
					                    sourcePort.orig.value.bounds !== targetPort.orig.value.bounds &&
					                    targetPort.edges.length === 0;
        					} else {
	        						return wildcardPorts.isValid(sourcePort, targetPort) &&
		    		                sourcePort.orig.value.bounds !== xtrabounds &&  
		    		                targetPort.orig.value.bounds !== xtrabounds &&  
		    		                sourcePort.orig.value.bounds !== targetPort.orig.value.bounds;
        					}
        				}
        			} else if(sourcePort.node.orig.value.type.name === "Collator"){
        				var m = targetPort.orig.value.originalType[0].messageargumentList[0];
        				if($scope.collateSchemes[sourcePort.node.orig.key] === "Array-based"){
	        				
	        				if(sourcePort.orig.value.bounds === outbounds){  					
	        					if(m.complexType && m.role === "repeated"){
	    							return wildcardPorts.isValid(sourcePort, targetPort) &&
				                        sourcePort.orig.value.bounds !== xtrabounds &&  
				                        targetPort.orig.value.bounds !== xtrabounds &&  
				                        sourcePort.orig.value.bounds !== targetPort.orig.value.bounds &&
				                        sourcePort.edges.length === 0;
	    						}
	        				} else {
	        					if(m.complexType === undefined && m.role !== "repeated"){
		        					return wildcardPorts.isValid(sourcePort, targetPort) &&
			    		                sourcePort.orig.value.bounds !== xtrabounds &&  
			    		                targetPort.orig.value.bounds !== xtrabounds &&  
			    		                sourcePort.orig.value.bounds !== targetPort.orig.value.bounds;
		        				}
		        			}
        				} else if($scope.collateSchemes[sourcePort.node.orig.key] === "Parameter-based"){
        					if(sourcePort.orig.value.bounds === outbounds){
		        					return wildcardPorts.isValid(sourcePort, targetPort) &&
					                    sourcePort.orig.value.bounds !== xtrabounds &&  
					                    targetPort.orig.value.bounds !== xtrabounds &&  
					                    sourcePort.orig.value.bounds !== targetPort.orig.value.bounds &&
					                    sourcePort.edges.length === 0;
        					} else {
	        						return wildcardPorts.isValid(sourcePort, targetPort) &&
			    		                sourcePort.orig.value.bounds !== xtrabounds &&  
			    		                targetPort.orig.value.bounds !== xtrabounds &&  
			    		                sourcePort.orig.value.bounds !== targetPort.orig.value.bounds;
        					} 
        				}
        			} else if(targetPort.node.orig.value.type.name === "Splitter"){
        				if($scope.splitSelect){
        					 if(targetPort.orig.value.bounds === inbounds)
		        				return wildcardPorts.isValid(sourcePort, targetPort) &&
			                        sourcePort.orig.value.bounds !== xtrabounds &&  
			                        targetPort.orig.value.bounds !== xtrabounds &&  
			                        sourcePort.orig.value.bounds !== targetPort.orig.value.bounds &&
			                        targetPort.edges.length === 0;
        					 else{
        						 return wildcardPorts.isValid(sourcePort, targetPort) &&
		    		                sourcePort.orig.value.bounds !== xtrabounds &&  
		    		                targetPort.orig.value.bounds !== xtrabounds &&  
		    		                sourcePort.orig.value.bounds !== targetPort.orig.value.bounds;
        					 }
        				}
        			} else if(sourcePort.node.orig.value.type.name === "Splitter"){
        				if($scope.splitSelect){
        					if(sourcePort.orig.value.bounds === inbounds)
        						return wildcardPorts.isValid(sourcePort, targetPort) &&
    	                        sourcePort.orig.value.bounds !== xtrabounds &&  
    	                        targetPort.orig.value.bounds !== xtrabounds &&  
    	                        sourcePort.orig.value.bounds !== targetPort.orig.value.bounds &&
    	                        sourcePort.edges.length === 0;
        					else{
        						return wildcardPorts.isValid(sourcePort, targetPort) &&
	    		                sourcePort.orig.value.bounds !== xtrabounds &&  
	    		                targetPort.orig.value.bounds !== xtrabounds &&  
	    		                sourcePort.orig.value.bounds !== targetPort.orig.value.bounds;
        					}
        				}
        				
        			} else {
	                    return wildcardPorts.isValid(sourcePort, targetPort) &&
		                    sourcePort.orig.value.bounds !== xtrabounds &&  
		                    targetPort.orig.value.bounds !== xtrabounds &&  
		                    sourcePort.orig.value.bounds !== targetPort.orig.value.bounds}});

        _drawGraphs = dc_graph.draw_graphs({
            idTag: 'nodeId',
            edgeIdTag: 'linkId',
            labelTag: 'name',
            sourceTag: 'sourceNodeId',
            targetTag: 'targetNodeId'
        })
            .clickCreatesNodes(false)
            .usePorts(symbolPorts)
            .conduct(portMatcher)
            .addEdge(function(e, sport, tport) {
                // reverse edge if it's going from requirement to
                // capability
                if(sport.orig.value.bounds === inbounds) {
                    console.assert(tport.orig.value.bounds === outbounds);
                    var t;
                    t = sport;
                    sport = tport;
                    tport = t;
                    t = e.sourceNodeId;
                    e.sourceNodeId = e.targetNodeId;
                    e.targetNodeId = t;
                    e.linkName = null;
                }
                e.sourceNodeRequirement = sport.name;
                e.targetNodeCapability = tport.name;
                var params, url =  '';
                var properties, map_json = [];

               if(sport.node.orig.value.modelName == "DataBroker" || sport.node.orig.value.type.name == 'DataBroker' || tport.node.orig.value.modelName == "DataBroker" ||tport.node.orig.value.type.name == 'DataBroker' ){
                	properties = {};
                	if(sport.orig.value.bounds === outbounds){
                		targetTableCreate(tport);
                	} else if(tport.orig.value.bounds === outbounds){
                		targetTableCreate(sport);
                	}
                } else if(sport.node.orig.value.type.name === COLLATOR){
                	if(sport.orig.value.bounds === outbounds){
                		properties = {
                                "data_map":null,
                                "data_broker_map": null,
                                "collator_map": {
                                	"collator_type": null,
                                	"output_message_signature": JSON.stringify(tport.orig.value.originalType[0]),
                                	"map_inputs": null,
                                	"map_outputs": null
                                },
                                "splitter_map": null

                		};
                		if(sport.orig.value.type !== null){
                			sport.orig.value.fullType = tport.orig.value.originalType;
                		}
                		if($scope.collateScheme === "Parameter-based"){
                			createCollateTargetTable(tport,sport);
                		}
                	} else{
                		properties = {};
                	}
                } else if(tport.node.orig.value.type.name === COLLATOR){
                	properties = {};
                	if($scope.collateScheme === "Parameter-based"){
                		createCollateSourceTable(sport,tport);
                	}
                } else if(tport.node.orig.value.type.name === SPLITTER) {
                	if(tport.orig.value.bounds === inbounds){
                		properties = {
                                "data_map":null,
                                "data_broker_map": null,
                                "collator_map": null,
                                "splitter_map": {
                                	"splitter_type": null,
                                	"input_message_signature": JSON.stringify(sport.orig.value.originalType[0]),
                                	"map_inputs": null,
                                	"map_outputs": null
                                }

                		};
                		if($scope.splitScheme === "Parameter-based"){
                			createSplitSourceTable(sport,tport);
                		}
                	} else{
                		properties = {};
                	}
                } else if(sport.node.orig.value.type.name === SPLITTER){
                	properties = {};
                	if($scope.splitScheme === "Parameter-based"){
            			createSplitTargetTable(tport,sport);
            		}
                }
                else {
                
                	if(is_wildcard_type(sport.orig.value.originalType)){
                    var capabilities = tport.node.orig.value.capabilities;
                    capabilities.forEach(function(capability) {
                        if(capability.target.id == tport.orig.value.shortname) {
                            capability.target.name.forEach(function(message) {
                                var input_flds = [];
                                message.messageargumentList.forEach(function(argument) {
                                    input_flds.push({
                                        "tag" : argument.tag,
                                        "role" : argument.role,
                                        "name" : argument.name,
                                        "type" : argument.type});
                                });
                                map_json.push({
                                    message_name: message.messageName,
                                    output_fields: input_flds
                                });
                            });
                        }
                    });

                    properties= {
                        "data_map": {
                            "map_inputs": [],
                            "map_outputs": map_json
                        },
                        "data_broker_map": null,
                        "collator_map": null,
                        "splitter_map": null
                    };
                }
                else if(is_wildcard_type(tport.orig.value.originalType)){
                    var requirements = sport.node.orig.value.requirements;
                    requirements.forEach(function(requirement) {
                        if(requirement.capability.id === sport.orig.value.shortname) {
                            requirement.capability.name.forEach(function(message) {
                                var input_flds = [];
                                message.messageargumentList.forEach(function(argument){
                                    input_flds.push({
                                        "tag" : argument.tag,
                                        "role" : argument.role,
                                        "name" : argument.name,
                                        "type" : argument.type,
                                        "mapped_to_message" : "",
                                        "mapped_to_field" : ""
                                    });
                                });
                                map_json.push({
                                    "message_name": message.messageName,
                                    "input_fields": input_flds
                                });
                            });
                        }
                    });

                    properties = {
                        "data_map": {
                            "map_inputs": map_json,
                            "map_outputs": []
                        },
                        "data_broker_map": null,
                        "collator_map": null,
                        "splitter_map": null
                    };
                    
                } else{
                    properties = {};
                }
              }
                if(_solutionId){
                    params = {
                        userId:get_userId(),

                        version : $scope.solutionVersion,
                        solutionId: _solutionId,
                        linkId: e.linkId,
                        sourceNodeName: e.sourceNodeId,
                        sourceNodeId: e.sourceNodeId,
                        targetNodeName: e.targetNodeId,
                        targetNodeId: e.targetNodeId,
                        sourceNodeRequirement: e.sourceNodeRequirement,
                        targetNodeCapabilityName: e.targetNodeCapability
                    };
                }
                else if(_cid){
                    params = {
                        userId:get_userId(),

                        cid: _cid,
                        linkId: e.linkId,
                        sourceNodeName: e.sourceNodeId,
                        sourceNodeId: e.sourceNodeId,
                        targetNodeName: e.targetNodeId,
                        targetNodeId: e.targetNodeId,
                        sourceNodeRequirement: e.sourceNodeRequirement,
                        targetNodeCapabilityName: e.targetNodeCapability
                    };
                }

                if(e.linkName)
                    params.linkName = e.linkName;
                url = build_url(options.addLink, params);
                document.getElementById("loading").style.display = "block";
                return $http.post(url,angular.toJson(properties))
                    .then(function(response) {
                    	document.getElementById("loading").style.display = "none";
                    	$scope.activeInactivedeploy = true;
                        set_dirty(true);
                        return wildcardPorts.copyType(e, sport, tport);
                    });
            });

        _diagram.child('draw-graphs', _drawGraphs);

        var select_nodes = dc_graph.select_nodes({
            nodeStroke: '#4a2',
            nodeStrokeWidth: 3,
            nodeLabelFill: '#141'
        }).multipleSelect(false);
        _diagram.child('select-nodes', select_nodes);

        var move_nodes = dc_graph.move_nodes();
        _diagram.child('move-nodes', move_nodes);

        var fix_nodes = dc_graph.fix_nodes()
            .strategy(dc_graph.fix_nodes.strategy.last_N_per_component(Infinity))
            .fixNode(function(nodeId, pos) {
                var node = _diagram.getNode(nodeId);

                return modify_node(nodeId, node.value.name, pos)
                    .then(function(response) {
                        return pos;
                    });
            });
        _diagram.child('fix-nodes', fix_nodes);

        var label_nodes = dc_graph.label_nodes({
            labelTag: 'name',
            align: 'left'
        }).changeNodeLabel(function(nodeId, text) {
            var node = _diagram.getNode(nodeId);
            console.log(node);
            var allNodes = _drawGraphs.nodeCrossfilter().all();
            allNodes.forEach(function(n){
            	if(n.type.name === "Collator"){
            		if($scope.collateSourceTables[n.nodeId] !== undefined){
            			angular.forEach($scope.collateSourceTables[n.nodeId], function(value,key){
            				if(value.modelName === node.value.nodeId){
            					value.modelName = text;
            				}
            			});
            			console.log($scope.collateSourceTables[n.nodeId]);
            		} 
            	}
            	
            	if(n.type.name === "Splitter"){
            		if($scope.splitTargetTables[n.nodeId] !== undefined){
            			angular.forEach($scope.splitTargetTables[n.nodeId], function(value,key){
            				if(value.modelName === node.value.nodeId){
            					value.modelName = text;
            				}
            			});
            			console.log($scope.splitTargetTables[n.nodeId]);
            		} 
            	}
            });
            $scope.saveState.noSaves = false;
            $scope.validationState = true;
            $scope.activeInactivedeploy = true;
            _dirty = true;
            return modify_node(nodeId, text, node.value.fixedPos)
                .then(function(response) {
                		return text;
                });
        });
        _diagram.child('label-nodes', label_nodes);

        var select_edges = dc_graph.select_edges({
            edgeStroke: '#4a2',
            edgeStrokeWidth: 3
        }).multipleSelect(false);
        _diagram.child('select-edges', select_edges);

        var label_edges = dc_graph.label_edges({
            labelTag: 'linkName',
            align: 'center'
        }).changeEdgeLabel(function(edgeId, text) {
            var url = '';
            if(_solutionId){
                url = build_url(options.modifyLink, {
                    userid: get_userId(),
                    solutionid:_solutionId,
                    linkid: edgeId,
                    linkname:text,
                });
            } else {
                url = build_url(options.modifyLink, {
                    userid: get_userId(),
                    cid:_cid,
                    linkid: edgeId,
                    linkname:text,
                });
            }
            return $http.post(url).then(function(response) {
                _dirty = true;
                $scope.activeInactivedeploy = true;
                $scope.saveState.noSaves = false;
                $scope.validationState = true;
                _dirty = true;
                return text;
            });
        });
        _diagram.child('label-edges', label_edges);

        var select_ports = dc_graph.select_ports({
            outlineFill: function(p) {return p.value.bounds === inbounds ? '#cfd' : '#8db'},
            outlineStroke: '#4a2',
            outlineStrokeWidth: 2
        }).multipleSelect(false);
        _diagram.child('select-ports', select_ports);

        var select_nodes_group = dc_graph.select_things_group('select-nodes-group', 'select-nodes');
        var select_edges_group = dc_graph.select_things_group('select-edges-group', 'select-edges');
        var select_ports_group = dc_graph.select_things_group('select-ports-group', 'select-ports');
        select_nodes_group.on('set_changed.show-info', function(nodes) {
        	$scope.showProperties = false;
            if(nodes.length == 0){$('#deleteHide').hide();deleteShow = 0}else {$('#deleteHide').show();deleteShow = 1;};
            setTimeout(function() {
                if(nodes.length>1)
                    throw new Error('not expecting multiple select');
                else if(nodes.length === 1) {
                    select_edges_group.set_changed([], false);
                    select_ports_group.set_changed([], true);
             
                    var selectedNodeId = _diagram.getNode(nodes[0]).key;
                    $scope.nodeIdDB = selectedNodeId;
                    var n = selectedNodeId.length;
                    var type = selectedNodeId.substring(0,n-1);
                    var comps = _catalog.models().filter(function(comp) {
                        return _catalog.fModelName(comp) === type;
                    });
                    $scope.solutionDetails=comps[0];
                    
                    if(Object.keys(changeNode).length !== 0 && changeNode[nodes[0]]){
                        $scope.nodeNameUI = changeNode[nodes[0]];
                    } else{
                        $scope.nodeNameUI = nodes[0];
                    }
                    $scope.showProperties=null;
                    $scope.showLink = null;

                    switch($scope.solutionDetails.toolKit){
                    case 'BR': 
                    	$scope.showDataMapper = null;
                    	$scope.solutionDetails = null;
                    	$scope.showCollator = null;
                    	$scope.showSplitter = null;
                    	$scope.showDataBroker = true;
                    	if($scope.readSolutionArray[$scope.nodeIdDB])
                    		$scope.readSolution = $scope.readSolutionArray[$scope.nodeIdDB];
                    	$scope.$apply();
                    	break;
                    case 'TC':
                    	$scope.showDataBroker = null;
                    	$scope.showDataMapper = null;
                    	$scope.showCollator = null;
                    	$scope.showSplitter = null;
                    	$scope.$apply();
                    	display_properties(_catalog.fModelUrl(comps[0]));
                    	break;
                    case 'CO':
                    	$scope.showDataBroker = null;
                    	$scope.solutionDetails = null;
                    	$scope.showDataMapper = null;
                    	$scope.showSplitter = null;
                    	$scope.showCollator = true;
                    	if($scope.readSolutionArray[$scope.nodeIdDB])
                    		$scope.readSolution = $scope.readSolutionArray[$scope.nodeIdDB];
                    	if(!$scope.readSolutionArray[$scope.nodeIdDB]){
	                    	$scope.collateScheme = $scope.collateSchemes[$scope.nodeIdDB];
	                    	if($scope.collateScheme.length !== 0 ){
	                    		$scope.collateSelect = true;
	                    		if($scope.collateScheme !== "Array-based")
	                    			$scope.enableCollateMap = true;
	                    		else
	                    			$scope.enableCollateMap = false;
	                    	} else{
	                    		$scope.collateSelect = false;
	                    		$scope.enableCollateMap = false;
	                    	}
	                    	if($scope.collateSourceTables[$scope.nodeIdDB] !== undefined)
	                    		$scope.collateSourceMapTable = $scope.collateSourceTables[$scope.nodeIdDB];
	                    	if($scope.collateTargetTables[$scope.nodeIdDB] !== undefined)
	                    		$scope.collateTargetMapTable = $scope.collateTargetTables[$scope.nodeIdDB];
	                    	collateTagMap = $scope.collateTags[$scope.nodeIdDB];
	                    	$scope.collateErrorMap = $scope.collateErrors[$scope.nodeIdDB];
                    	} else{
                    		$scope.collateScheme = $scope.collateSchemes[$scope.nodeIdDB];
                    		if($scope.collateScheme.length !== 0 ){
	                    		$scope.collateSelect = true;
	                    		if($scope.collateScheme !== "Array-based")
	                    			$scope.enableCollateMap = true;
	                    		else
	                    			$scope.enableCollateMap = false;
	                    	} else{
	                    		$scope.collateSelect = false;
	                    		$scope.enableCollateMap = false;
	                    	}
                    		if($scope.collateSourceTables[$scope.nodeIdDB] !== undefined)
	                    		$scope.collateSourceMapTable = $scope.collateSourceTables[$scope.nodeIdDB];
	                    	if($scope.collateTargetTables[$scope.nodeIdDB] !== undefined)
	                    		$scope.collateTargetMapTable = $scope.collateTargetTables[$scope.nodeIdDB];
	                    	$scope.readCollateTagMap = $scope.collateTags[$scope.nodeIdDB];
	                    	collateTagMap = $scope.collateTags[$scope.nodeIdDB];
	                    	$scope.collateErrorMap = $scope.collateErrors[$scope.nodeIdDB];
                    	}
                    	$scope.$apply();
                    	break;
                    case 'SP':
                    	$scope.showDataBroker = null;
                    	$scope.solutionDetails = null;
                    	$scope.showDataMapper = null;
                    	$scope.showCollator = null;
                    	$scope.showSplitter = true;
                    	if($scope.readSolutionArray[$scope.nodeIdDB])
                    		$scope.readSolution = $scope.readSolutionArray[$scope.nodeIdDB];
                    	if(!$scope.readSolutionArray[$scope.nodeIdDB]){
	                    	$scope.splitScheme = $scope.splitSchemes[$scope.nodeIdDB];
	                    	if($scope.splitScheme.length !== 0 ){
	                    		$scope.splitSelect = true;
	                    		if($scope.splitScheme !== "Copy-based")
	                    			$scope.enableSplitMap = true;
	                    		else
	                    			$scope.enableSplitMap = false;
	                    	} else{
	                    		$scope.splitSelect = false;
	                    		$scope.enableSplitMap = false;
	                    	}
	                    	if($scope.splitSourceTables[$scope.nodeIdDB] !== undefined)
	                    		$scope.splitSourceMapTable = $scope.splitSourceTables[$scope.nodeIdDB];
	                    	if($scope.splitTargetTables[$scope.nodeIdDB] !== undefined)
	                    		$scope.splitTargetMapTable = $scope.splitTargetTables[$scope.nodeIdDB];
	                    	splitTagMap = $scope.splitTags[$scope.nodeIdDB];
	                    	$scope.splitErrorMap = $scope.splitErrors[$scope.nodeIdDB];
                    	} else{
                    		$scope.splitScheme = $scope.splitSchemes[$scope.nodeIdDB];
                    		if($scope.splitScheme.length !== 0 ){
	                    		$scope.splitSelect = true;
	                    		if($scope.splitScheme !== "Copy-based")
	                    			$scope.enableSplitMap = true;
	                    		else
	                    			$scope.enableSplitMap = false;
	                    	} else{
	                    		$scope.splitSelect = false;
	                    		$scope.enableSplitMap = false;
	                    	}
                    		if($scope.splitSourceTables[$scope.nodeIdDB] !== undefined)
	                    		$scope.splitSourceMapTable = $scope.splitSourceTables[$scope.nodeIdDB];
	                    	if($scope.splitTargetTables[$scope.nodeIdDB] !== undefined)
	                    		$scope.splitTargetMapTable = $scope.splitTargetTables[$scope.nodeIdDB];
	                    	$scope.readSplitTagMap = $scope.splitTags[$scope.nodeIdDB];
	                    	splitTagMap = $scope.splitTags[$scope.nodeIdDB];
	                    	$scope.splitErrorMap = $scope.splitErrors[$scope.nodeIdDB];
                    	}
                    	$scope.$apply();
                    	break;
                    default:
                    	// detect if node has wildcard ports
                         var wilds = _ports.filter(function(p) {
                             return p.nodeId === nodes[0] && is_wildcard_type(p.originalType);
                         });
                    	if(wilds.length) {
                    		$scope.showDataBroker = null;
                    		$scope.solutionDetails = null;
                    		$scope.showCollator = null;
                        	$scope.showSplitter = null;
                    		$scope.showDataMapper = true;
                    		$scope.$apply();
                    		display_data_mapper(nodes[0], wilds);
                    	}
                    	else{
                    		$scope.showDataBroker = null;
                    		$scope.showDataMapper = null;
                    		$scope.showCollator = null;
                        	$scope.showSplitter = null;
                    		display_properties(_catalog.fModelUrl(comps[0]));
                    		$scope.$apply();
                    		
                    	}
                    }
                    $scope.tabChange = 0;
                } else display_properties(null);
            }, 0);
        });
        select_edges_group.on('set_changed.show-info', function(edges) {
            if(edges.length === 1) {
                select_nodes_group.set_changed([], false);
                select_ports_group.set_changed([], true);
                // getEdge should give you enough info to look
                // up properties you want for edges
                setTimeout(function() {
                    var edge = _diagram.getWholeEdge(edges[0]);
                    if(edge !== null){
                        $scope.linkDetails = edge;
                        $scope.showDataMapper = null;
                        $scope.showDataBroker = null;
                        $scope.solutionDetails = null;
                        $scope.showProperties = null;
                        $scope.showCollator = null;
                    	$scope.showSplitter = null;
                        $scope.showLink=true;
                        $scope.$apply();
                    }
                }, 0);

            } else display_properties(null);

            if(edges.length != 1 && deleteShow==0){$('#deleteHide').hide();}
            else {$('#deleteHide').show();}
        });
      
        select_ports_group.on('set_changed.show-info', function(ports) {
            var portType, port_info;
            if(ports.length>0) {
            	$scope.nodeIdDB = ports[0].node;
                select_nodes_group.set_changed([], false);
                select_edges_group.set_changed([], true);
                var p = _diagram.getPort(ports[0].node, null, ports[0].name);
                $scope.portDets = p;
                if(p.orig.value.bounds === inbounds){
                    portType = "input";
                } else if(p.orig.value.bounds === outbounds){
                	portType = "output";
                } else {
                	portType = "script";
                }
                
                if(portType === "script"){	
                	$scope.showScript();
                } else {
                var selectedNodeId = ports[0].node;
                var res = selectedNodeId.slice(0,-1);
                var type = res;
                var comps = _catalog.models().filter(function(comp) {
                    return _catalog.fModelName(comp) === type;
                });
                $scope.nodeSolutionDetails=comps[0];
                var nodetype=p.orig.value.type;

                if(nodetype !== null){
                    var res = nodetype.split("[");
                    var getver = res[2].split("]");
                    var ver = getver[0];

                    port_info = '['+ver+']';
                } else port_info = null;
                var url = build_url(options.getMatchingModels, {
                    userid: get_userId(),
                    solutionid: $scope.nodeSolutionDetails.solutionId,
                    version : $scope.nodeSolutionDetails.version,
                    portType: portType,
                    protobufJsonString: port_info
                });
                document.getElementById("loading").style.display = "block";
                $http.get(url,{headers: {'Access-Control-Allow-Origin': '*','Access-Control-Request-Method': 'GET','Access-Control-Allow-Headers':'application/x-www-form-urlencoded'},cache: true}).success(function(data){
                    var i=0;
                    var matchingModels = [];

                    angular.forEach(data.matchingModels, function(value,key){
                        angular.forEach(models,function(value1,key1){
                            if(value.matchingModelName === value1.solutionName)
                            	if(matchingModels.indexOf(value1) == -1){
                            		matchingModels[i++] = value1;
                           	}
                        });
                    });
                    setTimeout(function() {
                        $scope.matchModels = matchingModels;
                        $scope.tabChange = 1;
                        $scope.clicked = true;
                        $scope.$apply();
                    }, 0);
                    document.getElementById("loading").style.display = "none";
                })
                    .error(function(data){
                        var matchingModels = [];
                        setTimeout(function() {
                            $scope.matchModels = matchingModels;
                            $scope.tabChange = 1;
                            $scope.clicked = true;
                            $scope.$apply();
                        }, 0);
                        document.getElementById("loading").style.display = "none";
                    });
            }
            } else display_properties(null);
        });

        $scope.deleteNodeEdge=function(value){
            delete_nodes.deleteSelection();
            delete_edges.deleteSelection();
            $scope.closePoup();
            $scope.showDataBroker = null;
            $scope.showDataMapper = null;
            $scope.showCollator = null;
        	$scope.showSplitter = null;
            $scope.solutionDetails = null;
            $scope.showProperties = null;
            $scope.showLink=null;
        };

        var delete_nodes = dc_graph.delete_nodes("nodeId")
            .crossfilterAccessor(function(chart) {
                return _drawGraphs.nodeCrossfilter();
            })
            .dimensionAccessor(function(chart) {
                return _diagram.nodeDimension();
            })
            .onDelete(function(nodes) {
                $scope.NodeName=nodes[0];
                $scope.removename = nodes[0].slice(0, -1);
                $scope.saveState.noSaves = false;
                _dirty = true;
                var url = '';
                if(_solutionId){
                    url = build_url(options.deleteNode, {
                        userId: get_userId(),
                        solutionId : _solutionId,
                        nodeId: nodes[0],
                        version : $scope.solutionVersion
                    });
                }
                else{
                    url = build_url(options.deleteNode, {
                        userId: get_userId(),
                        cid : _cid,
                        nodeId: nodes[0],
                    });
                }
                return $http.post(url)
                    .then(function(response) {
                    	jsonProtoNode.delete($scope.removename);
                        $('#deleteHide').hide();
                        $scope.validationState = true;
                        $scope.activeInactivedeploy = true;
                        // after the back-end has accepted the
                        // deletion, we can remove unneeded
                        // ports
                        _ports = _ports.filter(function(p){return p.nodeId !== nodes[0]});
                        update_ports();
                        _ports.forEach(function(p){
                        	if(p.type == "script"){
                        		var dbNode = p.nodeId;
                        		_ports.forEach(function(port){
                        			if(port.nodeId === p.nodeId && port.bounds === outbounds && port.type === null){
                        				$scope.targetMapTable = null;
                        			}
                        		});
                        	}
                        	
                        	if(p.nodeType === "Collator"){
                        		if(p.bounds === outbounds && p.type === null){
                        			$scope.collateTargetTables[p.nodeId] = [];
                        			$scope.collateTags[p.nodeId] = [];
                        			$scope.collateErrors[p.nodeId] = [];
                        		} else if(p.bounds === inbounds){
                        		
	                        		var collateLength = 0;
	                                var index = -1;
	                                collateSourceTable = $scope.collateSourceTables[p.nodeId];
	                                collateTagMap = $scope.collateTags[p.nodeId];
	                                var collateError = [];
	                                collateError = $scope.collateErrors[p.nodeId];
	                                collateSourceTable.forEach(function(c){
	                                	if(c.modelName === nodes[0]){
	                                		if(index > collateSourceTable.indexOf(c) || index === -1)
	                                			index = collateSourceTable.indexOf(c);
	                                		collateLength++;
	                                	}
	                                	
	                                });
	                                collateSourceTable.splice(index,collateLength);
									collateTagMap.splice(index,collateLength);
									for(var a=0;a<collateLength;a++)
										collateError.delete(index++);
	                                $scope.collateSourceTables[p.nodeId] = collateSourceTable;
	                                $scope.collateTags[p.nodeId] = collateTagMap;
	                                $scope.collateErrors[p.nodeId] = collateError;
	                        	}
                        	}
                        	
                        	if(p.nodeType === "Splitter"){
                        		if(p.bounds === inbounds && p.type === null){
                        			$scope.splitSourceTables[p.nodeId] = [];
                        			$scope.splitTags[p.nodeId] = [];
                        			$scope.splitErrors[p.nodeId] = [];
                        		}
                                var splitLength = 0;
                        		var index = -1;
                        		splitTargetTable = $scope.splitTargetTables[p.nodeId];
                                splitTargetTable.forEach(function(s){
                                	if(s.modelName === nodes[0]){
                                		if(index > splitTargetTable.indexOf(s) || index === -1)
                                			index = splitTargetTable.indexOf(s);
                                		splitLength++;
                                		
                                	}
                                });
                                splitTargetTable.splice(index,splitLength);
                                $scope.splitTargetTables[p.nodeId] = splitTargetTable;
                        	}
                        });
                        
                        enanleDisable();
                        return nodes;
                    });

            });
        _diagram.child('delete-nodes', delete_nodes);

        var delete_edges = dc_graph.delete_things(select_edges_group, 'delete-edges', 'linkId')
            .crossfilterAccessor(function(chart) {
                return _drawGraphs.edgeCrossfilter();
            })
            .dimensionAccessor(function(chart) {
                return _diagram.edgeDimension();
            })
            .onDelete(function(edges) {
                $scope.NodeName=edges[0];
                $scope.saveState.noSaves = false;
                _dirty = true;
                var url = '';
                if(_solutionId){
                    url = build_url(options.deleteLink, {
                        userId: get_userId(),
                        solutionId : _solutionId,
                        linkId: edges[0],
                        version : $scope.solutionVersion
                    });
                }
                else{
                    url = build_url(options.deleteLink, {
                        userId: get_userId(),
                        cid : _cid,
                        linkId: edges[0]
                    });
                }
                return $http.post(url)
                    .then(function(response) {
                        $('#deleteHide').hide();
                        $scope.activeInactivedeploy = true;
                        $scope.validationState = true;
                        // after the back-end has accepted the
                        // deletion, we can remove unneeded
                        // ports
                        var edge = _diagram.getWholeEdge(edges[0]);
                        
                        _ports = _ports.filter(function(p) {return p.edges !== edges[0]});
                        update_ports();
                        wildcardPorts.resetTypes(_diagram, edges);
                        _ports.forEach(function(port){
                        	if(port.type == "script"){
                        		var dbNode = port.nodeId;
                        		_ports.forEach(function(dbport){
                        			if(dbport.nodeId === port.nodeId && dbport.bounds === outbounds && dbport.type === null){
                        				$scope.targetMapTable = null;
                        			}
                        		});
                        	}
                        	
                        	if(port.nodeType === "Collator"){
                        		if(port.bounds === outbounds && port.type === null){
                        			$scope.collateTargetTables[port.nodeId] = [];
                        			$scope.collateTags[port.nodeId] = [];
                        			$scope.collateErrors[port.nodeId] = [];
                        		}
                        		var collateLength = 0;
                                var index = -1;
                                collateSourceTable = $scope.collateSourceTables[port.nodeId];
                                collateSourceTable.forEach(function(c){
                                	if(c.modelName === edge.orig.value.sourceNodeId){
                                		if(index > collateSourceTable.indexOf(c) || index === -1)
                                			index = collateSourceTable.indexOf(c);
                                		collateLength++;
                                	}
                                	
                                });
                                collateSourceTable.splice(index,collateLength);
                                $scope.collateSourceTables[port.nodeId] = collateSourceTable;
                        	}
                        	
                        	if(port.nodeType === "Splitter"){
                        		if(port.bounds === inbounds && port.type === null){
                        			$scope.splitSourceTables[port.nodeId] = [];
                        			$scope.splitTags[port.nodeId] = [];
                        			$scope.splitErrors[port.nodeId] = [];
                        		}
                        		var splitLength = 0;
                                var index = -1;
                                splitSourceTable = $scope.splitTargetTables[port.nodeId];
                                splitTargetTable.forEach(function(s){
                                	if(s.modelName === edge.orig.value.targetNodeId){
                                		if(index > splitTargetTable.indexOf(s) || index === -1)
                                			index = splitTargetTable.indexOf(s);
                                		splitLength++;
                                		
                                	}
                                });
                                splitTargetTable.splice(index,splitLength);
                                $scope.splitTargetTables[port.nodeId] = splitTargetTable;
                        	}
                        });
                        return edges;
                    });
            });
        _diagram.child('delete-edges', delete_edges);

        function input_generate_operation(d) {
            operations=[]; messages=[];var i=0;var m=0;
            if(jsonProtoMap){
                operations = d.orig.value.shortname;
                for(var l=0;l<jsonProtoMap.length;l++){
                    messages[m++] = jsonProtoMap[l].messageName;
                }

            } else if(jsonProto){
                angular.forEach(jsonProto.protobuf_json.service.listOfOperations, function(value, key) {
                    if(d.orig.value.shortname === value.operationName){
                        operations = value.operationName;
                        angular.forEach(value.listOfInputMessages,function(value1,key1){
                            messages[i++] = [value1.inputMessageName];
                        });
                    }
                });
            }
            var op = operations,
                msgs = messages;

            return op + '(' + msgs.map(function(msg) {
                return '<a style="color:white" href="#" class="tip-link" id="' + op + '_' + msg + '">' + msg + '</a>';
            }).join(', ') + ')';
        }

        function output_generate_operation(d) {
            operations=[]; messages=[];var i=0; var m=0;
            if(jsonProtoMap){
                operations = d.orig.value.shortname;
                for(var l=0;l<jsonProtoMap.length;l++){
                    messages[m++] = jsonProtoMap[l].messageName;
                }
            } else if(jsonProto){
                angular.forEach(jsonProto.protobuf_json.service.listOfOperations, function(value, key) {
                    if(d.orig.value.shortname === value.operationName){
                        operations = value.operationName;
                        angular.forEach(value.listOfOutputMessages,function(value1,key1){
                            messages[i++] = [value1.outPutMessageName];
                        });
                    }

                });
            }
            var op = operations,
                msgs = messages;

            return op + '(' + msgs.map(function(msg) {
                return '<a style="color:white" href="#" class="tip-link" id="' + op + '_' + msg + '">' + msg + '</a>';
            }).join(', ') + ')';
        }

        var port_tips = dc_graph.tip()
            .delay(200)
            .clickable(true)
            .selection(dc_graph.tip.select_port())
            .content(function(d, k) {
            	console.log(d);
                if(is_wildcard_type(d.orig.value.originalType)){
                    jsonProtoMap = d.orig.value.fullType;
                    jsonProto = null;
                } else{
                    jsonProtoMap = null;
                    angular.forEach(jsonProtoNode,function(value,key){
                        if(key === d.node.orig.value.type.name){
                            jsonProto = jsonProtoNode.get(key);
                        }

                    });
                    console.assert(!Object.keys(jsonProtoNode).length || jsonProto === jsonProtoNode.get(d.node.orig.value.type.name));

                    if(protoJsonRead.size !==0 ){
                        angular.forEach(protoJsonRead,function(value,key){
                            var lastChar = d.orig.value.nodeId.slice(-1);
                            var res = d.orig.value.nodeId.split(lastChar);
                            var nodeKey = res[0];
                            if(key === nodeKey)
                                jsonProto = protoJsonRead.get(key);
                        });
                    }
                }
                if(d.orig.value.bounds === inbounds){
                    k(input_generate_operation(d));
                } else{
                    k(output_generate_operation(d));
                }

            })
            .offset(function() {
                return [this.getBBox().height / 2 - 20, 0];
            })
            .linkCallback(function(id) {
                var messageJson=[];
                var i=0;var complexProto=[];var j=0;$scope.complexProtoMap = new Map();var complexArray =0;
                var complexMapArray=[];
                if(jsonProtoMap){
                    for(var l=0;l<jsonProtoMap.length;l++){
                        if(operations+'_'+jsonProtoMap[l].messageName === id){
                            $scope.messageDet = jsonProtoMap[l].messageName;
                           
                            angular.forEach(jsonProtoMap[l].messageargumentList, function(value1, key1) {
                            	var c=0;var complexJson =[]; var m=0; var s=0;var complexMessage = [];var complexMessageSignature = [];
                            	if(value1.complexType){
                            		complexArray++;
                            		$scope.complexMessageDet = value1.complexType.messageName;
                            		
                            		angular.forEach(value1.complexType.messageargumentList, function(value2, key2) {
                            			
                            			var a=0;var complexSubJson=[];
                            			if(value2.complexType){
                            				angular.forEach(value2.complexType.messageargumentList, function(value3, key3) {
                            					var temp = value3.tag.split(".");
                            					value3.tag=temp[temp.length-1];
                            					complexSubJson[a++] = value3.role+' '+value3.type+' '+value3.name+' = '+value3.tag;
                            				});
                            				complexMapArray.push({
                            					"messageName" : value2.complexType.messageName,
                            					"message" : complexSubJson
                            				});
                            				$scope.complexProtoMap.set(value2.complexType.messageName,complexSubJson);
                            				
                            			}
                            			var temp = value2.tag.split(".");
                    					value2.tag=temp[temp.length-1];
                            			complexJson[c++] = value2.role+' '+value2.type+' '+value2.name+' = '+value2.tag;
                            		});
                            		complexMapArray.push({
                    					"messageName" : value1.complexType.messageName,
                    					"message" : complexJson
                    				});
                            		$scope.complexProtoMap.set($scope.complexMessageDet,complexJson);

                            		}
                            	
                            	
                                messageJson[i++]=value1.role+' '+value1.type+' '+value1.name+' = '+value1.tag;
                                
                            });
                            
                        }
                    }

                } else{

            		var i = 0;
                    angular.forEach(jsonProto.protobuf_json.listOfMessages, function(value, key) {
                        if(operations+'_'+value.messageName === id){
                            $scope.messageDet=value.messageName;
                            angular.forEach(value.messageargumentList, function(value1, key1) {
                            	var c=0;var complexJson =[]; var m=0; var s=0;var complexMessage = [];var complexMessageSignature = [];
                            	if(value1.complexType){
                            		complexArray++;
                            		$scope.complexMessageDet = value1.complexType.messageName;
                            		
                            		angular.forEach(value1.complexType.messageargumentList, function(value2, key2) {
                            			
                            			var a=0;var complexSubJson=[];
                            			if(value2.complexType){
                            				angular.forEach(value2.complexType.messageargumentList, function(value3, key3) {
                            					var temp = value3.tag.split(".");
                            					value3.tag=temp[temp.length-1];
                            					complexSubJson[a++] = value3.role+' '+value3.type+' '+value3.name+' = '+value3.tag;
                            				});
                            				complexMapArray.push({
                            					"messageName" : value2.complexType.messageName,
                            					"message" : complexSubJson
                            				});
                            				$scope.complexProtoMap.set(value2.complexType.messageName,complexSubJson);
                            				
                            			}
                            			var temp = value2.tag.split(".");
                    					value2.tag=temp[temp.length-1];
                            			complexJson[c++] = value2.role+' '+value2.type+' '+value2.name+' = '+value2.tag;
                            		});
                            		complexMapArray.push({
                    					"messageName" : value1.complexType.messageName,
                    					"message" : complexJson
                    				});
                            		$scope.complexProtoMap.set($scope.complexMessageDet,complexJson);

                            		}
                            	
                            	
                                messageJson[i++]=value1.role+' '+value1.type+' '+value1.name+' = '+value1.tag;
                                
                            });
                            

                        }
                    });
                }
                $scope.complexProtoJson = [];
                $scope.complexProtoJson = $scope.complexProtoMap;
                $scope.complexMapArrayUI=complexMapArray;
                $scope.messageUI=messageJson;
                $scope.showDataBroker = null;
                $scope.showDataMapper = null;
                $scope.showCollator = null;
            	$scope.showSplitter = null;
                $scope.solutionDetails=null;
                $scope.showLink=null;
                if($scope.messageUI){
                    $scope.showProperties=true;
                }else
                    $scope.showProperties=false;
                if($scope.complexProtoJson.size!=0){
                	$scope.complexType =true;
                }else
                	$scope.complexType = false;
                $scope.tabChange = 0;
                $scope.$apply();
            });

        _diagram.child('port-tips', port_tips);

        function nodeTypeOnHover(d){
        	 var nodeTypeDispTest; 
        	 if(d.orig.value.name === "")
        		 nodeTypeDispTest = d.orig.value.nodeId;
        	 else
        		 nodeTypeDispTest = d.orig.value.name;
        	 return nodeTypeDispTest;
        }
      
        var node_tips = dc_graph.tip({namespace: 'node-tips'})
            .delay(200)
            .selection(dc_graph.tip.select_node())
            .content(function(d, k) {
                k(nodeTypeOnHover(d));

            })
            .offset(function(){
                // this attempts to keep position fixed even
                // though size of g.port is changing
                return [this.getBBox().height / 2 - 20, 0];
            });
        _diagram.child('node-tips', node_tips);
    }

    function modify_node(nodeId, name, pos) {

        var ndata = {};
        if(pos) {
            ndata.fixed = true;
            ndata.px = pos.x;
            ndata.py = pos.y;
        }
        else
            ndata.fixed = false;
        var url;
        if(_solutionId){
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                solutionid:_solutionId,
                nodeid: nodeId,
                nodename: name,
                ndata: JSON.stringify(ndata)

            });
        } else {
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                cid:_cid,
                nodeid: nodeId,
                nodename: name,
                ndata: JSON.stringify(ndata)

            });
        }
        $scope.nodeNameUI = name;
        changeNode[nodeId] = name;
        $scope.saveState.noSaves = false;
        $scope.validationState = true;
        $scope.activeInactivedeploy = true;
        _dirty = true;
        return $http.post(url);
}

    $scope.showInput = false;
    $scope.toggleShowInput = function()
    {
        $scope.showInput = !$scope.showInput;
    };
    
    document.getElementById("loading").style.display = "block";
    function load_catalog() {
    	
        return get_catalog()
            .success(function(data) {
            	document.getElementById("loading").style.display = "none";
                angular.forEach(data.items, function(value, key) {
                    if(data.items.solutionName != "Text_Class_09102017_IST2"){

                    }
                    data.items[key].description = $(data.items[key].description).text();
                });
                _catalog = catalog_readers[options.catformat](data);
                
                _components = d3.map(_catalog.models(), _catalog.fModelKey);
                
                // PALETTE
                // throw out any models which don't have a category,
                // to avoid "null drawer"
                // also throw out composite solutions, for now -
                // we're unable to load them
                // (maybe we could display them but it would be a
                // little misleading)
                // also throw out iris, because, well, it's iris
                models = _catalog.models().filter(function(model) {
                    return _catalog.fModelCategory(model) &&
                        _catalog.fModelName(model) !== 'iris';
                });
                
                $scope.palette.categories = d3.nest().key(_catalog.fModelCategory)
                    .sortKeys(d3.ascending)
                    .entries(models);
                $http({
                    method : 'GET',
                    url : '/api/filter/modeltype'
                }).success(function(data, status, headers, config) {
                    $scope.categoryNames = data.response_body;
                }).error(function(data, status, headers, config) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error
                    // status.
                	$scope.categoryNames = [];
                });

                $http({
                    method : 'GET',
                    url : '/api/filter/toolkitType'
                }).success(function(data, status, headers, config) {
                    $scope.toolKitTypes = data.response_body;
                }).error(function(data, status, headers, config) {
                	// called asynchronously if an error occurs
                    // or server returns response with an error
                    // status.
                	$scope.toolKitTypes = [];
                });

                $http({
                    method : 'GET',
                    url : '/api/filter/accesstype'
                }).success(function(data, status, headers, config) {
                    $scope.accessTypes = data.response_body;
                }).error(function(data, status, headers, config) {
                	// called asynchronously if an error occurs
                    // or server returns response with an error
                    // status.
                	$scope.accessTypes = [];
                });
                $scope.initIndexLeft=false;
                $scope.showOther = true;
                $scope.setActionLeft = function(index){
                    if(index==$scope.selectedIndexLeft){
                        $scope.selectedIndexLeft=-1;
                    } else{
                        $scope.selectedIndexLeft=index;
                        $scope.initIndexLeft=true;
                        $scope.showOther = false;

                    }
                    
                };
            }).error(function(response){
            	$scope.palette.categories=[];
            		
            });
    }


    function load_initial_solution() {
        var catsol;
        if(options.solutionId)
            catsol = _catalog.models().find(function(sol){return sol.solutionId === options.solutionId});
        else if(options.solutionName)
            catsol = _catalog.models().find(function(sol){return sol.solutionName === options.solutionName});
        if(catsol)
            $scope.loadSolution(catsol);
    }
    initialize_canvas();
    load_catalog().success(load_initial_solution);
    $scope.newSolution('old');
    // Function for closing composite solution
    $scope.closeComSol = function(){

        if(_dirty){$scope.CloseOrNew = 'closeSol';$scope.showsaveConfirmationPopup();}
        else {
            var url = "";
            if(_solutionId){
                url = build_url(options.closeCompositeSolution , {
                    userId: get_userId(),
                    solutionId :  _solutionId,
                    solutionVersion : $scope.solutionVersion
                });
            }
            else if(_cid){
                url = build_url(options.closeCompositeSolution , {
                    userId: get_userId(),
                    cid: _cid,
                });
            }

            $http.post(url)
                .success(function(response) {
                    reset();
                	
                    $scope.clearSolution();
                    $scope.namedisabled = false;$scope.closeDisabled = true;
                    $scope.solutionName = '';$scope.solutionVersion = '';$scope.solutionDescription = '';_cid = '';_solutionId = '';
                    $scope.canvas =false;
                    
                })
                .error(function(response){
                	$scope.titlemsg = ALERT;
                	$scope.msg = "Could not close the solution";
                	$scope.showPopup();
                });
        }
    };
    $scope.SaveSolutionFirst = function(val){
        if(val == 'yes' && $scope.CloseOrNew == 'closeSol'){
            $scope.showPrerenderedDialog();
        }
        else if(val == 'no' && $scope.CloseOrNew == 'closeSol'){
        	_dirty = false;
        	if($scope.clearSol) 
        		$scope.clearSolution();
        	else if($scope.loadSol)
        		$scope.loadSolution($scope.solDet);
        	else
        		$scope.closeComSol();
        	$scope.closePoup();$scope.checkboxDisable = true;}
        else if(val == 'yes' && $scope.CloseOrNew == 'new'){
            $scope.showPrerenderedDialog();
        }
        else if(val == 'no' && $scope.CloseOrNew == 'new'){_dirty = false;$scope.newSolution('new');$scope.closePoup();}
    };

    window.onbeforeunload = function(){if(_dirty) return 'Unsaved';}
    
    // Function for closing All Open composite solution
    $scope.clearSolution = function(){
    	$scope.clearSol = true;
    	if(_dirty){$scope.CloseOrNew = 'closeSol';$scope.showsaveConfirmationPopup();}
        else {
        var url = "";
        if(_solutionId){
            url = build_url(options.clearCompositeSolution , {
                userId: get_userId(),
                solutionId : _solutionId,
                solutionVersion : $scope.solutionVersion
            });
        }
        else if(_cid){
            url = build_url(options.clearCompositeSolution , {
                userId: get_userId(),
                cid: _cid,
            });
        }

        $http.post(url)
            .success(function(response) {
                $scope.cleardis= true;
                $('#deleteHide').hide();
                $scope.canvas=false;

            })
            .error(function(response){
                $('#deleteHide').hide();
                $scope.cleardis = true;
            });
        reset();
        var empty = {"cname": $scope.solutionName,"version": $scope.solutionVersion,"cid": _cid,"solutionId": _solutionId,"ctime": '',"mtime": "","nodes": [],"relations": []};
        display_solution(empty);
        $scope.solutionDescription = '';
        $scope.clearSol = false;
        }
    };

    $scope.down=false;
    $scope.closeDrawer =function(){
    	if($scope.left && $scope.right && $scope.down){
    		$scope.left = $scope.right = $scope.down = false;
    	} else{
    		$scope.left = $scope.right = $scope.down = true;
    	}
       
    	setTimeout(function() {
    		_diagram
        		.width(null)
        		.height(null)
        		.redraw();
            $scope.$apply();
        }, 1000);
        
        };
    $scope.$watch('closeDisabledCheck', function(newValue, oldValue) {
        $scope.closeAllDisabled = true;$scope.closeDisabled = true;
        if(countComSol == 1 && countComSol != 0)$scope.closeDisabled = false;else $scope.closeDisabled = true;
        if(countComSol > 1 && countComSol != 0)$scope.closeAllDisabled = false;else $scope.closeAllDisabled = true;
    });
    $scope.showPrerenderedDialog = function(ev) {
        $mdDialog.show({
            contentElement: '#myDialog',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
  
    $scope.showsaveConfirmationPopup = function(ev) {
        $mdDialog.show({
            contentElement: '#myDialogSave',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    $scope.showpopup = function(ev) {
        $mdDialog.show({
            contentElement: '#myDialogPopup',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    $scope.showdeletePopup = function(ev,val) {
        $scope.solutionToDelete = val;
        $scope.solutionVersion = val.version;
        $mdDialog.show({
            contentElement: '#myDialogdelete',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
    $scope.resetScript = function(){
    	if($scope.readSolutionArray[$scope.nodeIdDB]){
    		if($scope.dbType === "Select Type" || $scope.dbType === "" || $scope.dbType === null || $scope.dbType === undefined)
    			$scope.dbType = "Select Type";
    		else
    			$scope.dbType = readScriptDetails.dbtype;
    		
    		if($scope.fileUrl === "" || $scope.fileUrl === null || $scope.fileUrl === undefined)
    			$scope.fileUrl = "";
    		else
    			$scope.fileUrl = readScriptDetails.targeturl;
    		
    		if($scope.scriptText === "" || $scope.scriptText === null || $scope.scriptText === undefined)
    			$scope.scriptText = "";
    		else
    			$scope.scriptText = readScriptDetails.script;
    		
    		if($scope.firstRow === "" || $scope.firstRow === null || $scope.firstRow === undefined)
    			$scope.firstRow = "";
    		else 
    			$scope.firstRow = readScriptDetails.firstrow;
    		
    		if($scope.userImage === "" || $scope.userImage === null || $scope.userImage === undefined)
    			$scope.userImage = "";
    		else{
    			$scope.userImage = undefined;
				$scope.userImageNew =readScriptDetails.localfile;
    		}
    		
    		if($scope.databaseName === "" || $scope.databaseName === null || $scope.databaseName === undefined)
    			$scope.databaseName = "";
    		else 
    			$scope.databaseName = readScriptDetails.databasename;
    		
    		if($scope.tableName === "" || $scope.tableName === null || $scope.tableName === undefined)
    			$scope.tableName = "";
    		else 
    			$scope.tableName = readScriptDetails.tablename;
    		
    		if($scope.jdbcDatasource === "Select JDBC Class name" || $scope.jdbcDatasource === "" || $scope.jdbcDatasource === null || $scope.jdbcDatasource === undefined)
    			$scope.jdbcDatasource = "Select JDBC Class name";
    		else 
    			$scope.jdbcDatasource = readScriptDetails.jdbcdatasource;    		
    		
    	} else if($scope.scriptEntered){
    		$scope.dbType = scriptEnteredDetails.dbtype;
    		$scope.fileUrl = scriptEnteredDetails.targeturl;
    		$scope.scriptText = scriptEnteredDetails.script;
    		$scope.firstRow = scriptEnteredDetails.firstrow;
    		$scope.userImage = scriptEnteredDetails.localfile;
    		$scope.databaseName = scriptEnteredDetails.databasename;
			$scope.tableName = scriptEnteredDetails.tablename;
			$scope.jdbcDatasource = scriptEnteredDetails.jdbcdatasource;

    	} else{
    		$scope.dbType = "Select Type";
    		$scope.fileUrl = "";
    		$scope.scriptText = "";
    		$scope.firstRow = "";
    		$scope.userImage="";
    		$scope.databaseName = "";
			$scope.tableName = "";
			$scope.jdbcDatasource = "Select JDBC Class name";
    	}
    }
    $scope.closePoupscript = function(){
    	if(readScriptDetails){
    		$scope.readSolutionArray[$scope.nodeIdDB] = true;
    	}
    	$scope.resetScript();
    	
    	$scope.dbValue = undefined;$scope.dbtype='';
    	 $scope.databroker.$setPristine();
         $scope.databroker.$setUntouched();
         $scope.databroker.$rollbackViewValue();
         $mdDialog.cancel();
    	
    }
    
    $scope.closeMappingPopup = function(){
    	$mdDialog.cancel();
    	if($scope.readSolutionArray[$scope.nodeIdDB]){
    		$scope.readSourceTable = readScriptDetails.mapInputs;
    	}
    };
    
    $scope.closePoup = function(){
    	if(enteredOk){
    		$scope.showPrerenderedDialog();
    		enteredOk = false;
    	}
        $mdDialog.cancel();
        
    };

    $scope.closeSavePoup = function(){
    	if(!savedSolution){
    		$scope.solutionName = undefined;
    		$scope.solutionVersion = "";
    		$scope.solutionDescription = "";
    	}
        $mdDialog.cancel();

    };
    $scope.showDeleteNodeLink = function(ev){
        $mdDialog.show({
            contentElement: '#myDialogNodeLink',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    $scope.myDialogOldVersionSave = function(ev){
        $mdDialog.show({
            contentElement: '#myDialogOldSave',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    var solutionNameSave = '';
    $scope.updateMySolution = function(){
        updateOldSol=true;
        save_solution(solutionNameSave);
    };
    function solutionPrPB(){
        var tempArr = ['PR','PB','OR'],url = '';
        $scope.publicCS = [];
        angular.forEach(tempArr, function(value, key) {
            url = build_url(options.getCompositeSolutions, {
                userId: get_userId(),
                visibilityLevel : tempArr[key]
            });
            $http.get(url)
                .success(function(data) {
                	if(tempArr[key] == 'PB'){
                        $scope.publicCS = data.items
                        }
                    else if(tempArr[key] == 'OR'){
                        $scope.publicOR = data.items
                    }
                    else {
                    	$scope.privateCS = data.items;
                    	$scope.publicOR = [];
                    }
                    angular.forEach($scope.publicOR, function(value1, key1) {
                        $scope.publicCS.push(value1);
                        console.clear();
                    })
                })
                .error(function(response){
                	$scope.publicCS = [];
                });
        });

    }
    solutionPrPB();
    $scope.setProbe = function(setProbeStatus){
    	if(_solutionId){
            var args = {
            	userId: get_userId(),
            	solutionId: _solutionId,
            	version: $scope.solutionVersion,
            	probeIndicator: setProbeStatus
            };
        } else if(_cid){
            var args = {
            	userId: get_userId(),
                cid: _cid,
                version: '1',
                probeIndicator: setProbeStatus
            };
        }
    	var url = build_url(options.setProbe, args);
    	 return $http.post(url).success(function(result) {
    		 if(setProbeStatus == true){
    			 $scope.titlemsg = ALERT;
    			 $scope.msg = "Probe added successfully";
    			 $scope.saveState.noSaves = false;
    			 $scope.validationState = true;
                 $scope.activeInactivedeploy = true;
    			 _dirty = true;
                $scope.showpopup();
    		 } else{
    			 $scope.titlemsg = ALERT;
    			 $scope.msg = "Probe removed";
    			 $scope.saveState.noSaves = false;
    			 $scope.validationState = true;
                 $scope.activeInactivedeploy = true;
    			 _dirty = true;
                 $scope.showpopup();
    		 }
             
         });
    }
    
    $scope.showScript = function(ev) {
    	if($scope.userImage !== undefined || $scope.userImage === ""){
	        var fileName = $scope.userImage.name;
	        if(fileName !== undefined){
	        	if($scope.dbType === 'csv'){
			        if (fileName.split(".")[1].toUpperCase() !== "CSV")
			            $scope.enableScript = true;
			        else
			        	$scope.enableScript = false;
	        	} else if($scope.dbType === 'sql'){
	        		if (fileName.split(".")[1].toUpperCase() !== "SQL")
			            $scope.enableScript = true;
			        else
			        	$scope.enableScript = false;
	        	}
	        } else
	        	$scope.enableScript = false;
        } else
        	$scope.enableScript = false;
        $mdDialog.show({
            contentElement: '#myDialogScript',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
    $scope.showSourceTable = false;
    $scope.defaultvalue = '';

    $scope.checkFileName = function(){
    	
        if($scope.userImage !== undefined || $scope.userImage === ""){
        	$scope.readSolutionArray[$scope.nodeIdDB] = false;
        	$scope.readSolution = false;
	        var fileName = $scope.userImage.name;
	        if(fileName !== undefined){
	        	if($scope.dbType === 'csv'){
			        if (fileName.split(".")[1].toUpperCase() !== "CSV")
			            $scope.enableScript = true;
			        else
			        	$scope.enableScript = false;
	        	} else if($scope.dbType === 'sql'){
	        		if (fileName.split(".")[1].toUpperCase() !== "SQL")
			            $scope.enableScript = true;
			        else
			        	$scope.enableScript = false;
	        	}
	        } else
	        	$scope.enableScript = false;
        } else
        	$scope.enableScript = false;
    }
    
    $scope.processData = function(){
    	$scope.scriptEntered = true;
    	scriptEnteredDetails={'dbtype':$scope.dbType,
    						'targeturl':"",
    						'script':"",
    						'firstrow':$scope.firstRow,
    						'localfile':$scope.userImage,
    						'databasename': $scope.databaseName,
        					'tablename': $scope.tableName,
        					'jdbcdatasource': $scope.jdbcDatasource};
    	if($scope.dbType == 'csv'){
    		if($scope.fileContent !== undefined){
    			$scope.readSolutionArray[$scope.nodeIdDB] = false;
    			checkFieldMap = new Map();
        		fieldNameMap = new Map();
        		fieldTypeMap = new Map();
        		tagMap = new Map();
    		} else{
    			
    		}
	    	var uploaded=$scope.fileContent;
	    	var delimeter;
	    	$scope.errormsg = '';
	    	
	    	//get file separator
	    	if(uploaded.indexOf('|') != -1){
			    var dataShow = uploaded.split('\n');
			    delimeter = '|';	
	    	}else if(uploaded.indexOf(',') != -1){
			   var dataShow = uploaded.split('\n');
			   delimeter = ',' ;
	    	}else if(uploaded.indexOf(';') != -1){
	    		var dataShow = uploaded.split('\n');
	    		delimeter = ';' ;
	    	}
	    	
	    	var col = dataShow[0];
	    	var statusParse = parseError(col);		 
	    	var tabledata = col.split(delimeter);
	    	$scope.dataShow = [];
	    	if(statusParse){
			  $scope.delimeterchar = delimeter;
			  if($scope.firstRow == "contains_field_names"){
				  $scope.dataShow = tabledata;
			  } else if($scope.firstRow == "contains_data"){
				  for(var a=0;a<tabledata.length;a++){
					  $scope.dataShow[a] = "C"+(a+1);
				  }
			  }

			  $scope.saveScript();
	    	} else {
	    		$scope.titlemsg = ALERT;
	    		$scope.msg ="CSV file field separator(| or , ) is missing , Please upload correct csv file";
                $scope.showpopup();
	    	}
		  
    } else if($scope.dbType === 'image'){
    	$scope.dataImage= [
    	       {
    	            "Fieldname" : "Mime Type",
    	            "FieldType" : "String",
    	        },
    	        {
    	            "Fieldname" : "Image Binary",
    	            "FieldType" : "Byte",
    	        }
    	    ]
    	$scope.saveScript();
    	
    } else if($scope.dbType === 'sql'){
    	var uploaded=JSON.stringify($scope.fileContent);
    	var res = uploaded.split("PRIMARY KEY");
    	res = res[0].split("`");
    	$scope.dataShow = [];
    	var dataTable = [];
    	for(var ind=1; ind < res.length; ind++){
    		if(ind % 2 === 1){
    			dataTable[Math.floor(ind/2)] = res[ind];
    		}
    	}
    	$scope.tableName = dataTable[0];
    	for (var dat = 1; dat < dataTable.length; dat++){
    		$scope.dataShow[dat-1] = dataTable[dat];
    	}
    	console.log($scope.dataShow);
    	
    	$scope.saveScript();
    }
    	
    function parseError(col){
    	if((col.indexOf("|") == -1 && col.indexOf(",") == -1 && col.indexOf(";") == -1)){
			return false;
		} else {
			return true;
		}
    	
    }
	
    }
    
    $scope.saveScript = function(){
    	var scriptInput = "";
    	var url;
    	var data;
    	$scope.localurl = '';
    	 if($scope.userImage){
    		 $scope.localurl = $scope.userImage.name;
    	 } else {
    		 $scope.localurl = null;
    	 }
    	 if($scope.dbType === 'csv'){
	    	data = {
	    			"databrokerMap": {
	    			    "script": "",
	    			    "csv_file_field_separator": $scope.delimeterchar?$scope.delimeterchar:null, 
	    			    "data_broker_type": $scope.dbType,
	    			    "first_row": $scope.firstRow?$scope.firstRow:null,
	    			    "local_system_data_file_path":$scope.localurl?$scope.localurl:$scope.userImageNew, 
	    			    "target_system_url":"",
	    			    "database_name": "",
	    				"table_name": "",
	    				"jdbc_driver_data_source_class_name": "",
	    				"user_id": "",
	    				"password": "",
	    				"protobufFile":"",
	    			    "map_action": null,
	    			    "map_inputs": null,
	    			    "map_outputs": null
	    			  },
	    			  "fieldMap":null,
	    			  "collatorMap": null,
	    			  "splitterMap": null
	    		}
    	 } else if($scope.dbType === 'sql'){
    		 data = {
 	    			"databrokerMap": {
 	    			    "script": "",
 	    			    "csv_file_field_separator": "", 
 	    			    "data_broker_type": $scope.dbType,
 	    			    "first_row": "",
 	    			    "local_system_data_file_path":$scope.localurl?$scope.localurl:$scope.userImageNew, 
 	    			    "target_system_url":"",
 	    			    "database_name": $scope.databaseName,
 	    				"table_name": $scope.tableName,
 	    				"jdbc_driver_data_source_class_name": $scope.jdbcDatasource,
 	    				"user_id": "",
 	    				"password": "",
 	    				"protobufFile":"",
 	    			    "map_action": null,
 	    			    "map_inputs": null,
 	    			    "map_outputs": null
 	    			  },
 	    			  "fieldMap":null,
 	    			  "collatorMap": null,
 	    			  "splitterMap": null
 	    		}
    	 } else if($scope.dbType === 'image'){
    		 data = {
  	    			"databrokerMap": {
  	    			    "script": "",
  	    			    "csv_file_field_separator": null, 
  	    			    "data_broker_type": $scope.dbType,
  	    			    "first_row": null,
  	    			    "local_system_data_file_path":null, 
  	    			    "target_system_url":"",
  	    			    "database_name": null,
  	    				"table_name": null,
  	    				"jdbc_driver_data_source_class_name": null,
  	    				"user_id": null,
  	    				"password": null,
  	    				"protobufFile":"",
  	    			    "map_action": null,
  	    			    "map_inputs": null,
  	    			    "map_outputs": null
  	    			  },
  	    			  "fieldMap":null,
  	    			  "collatorMap": null,
  	    			  "splitterMap": null
  	    		}
    	 }
        if(_solutionId){
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                solutionid:_solutionId,
                nodeid: $scope.nodeIdDB,
                nodename: $scope.nodeNameUI
            });
        } else {
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                cid:_cid,
                nodeid: $scope.nodeIdDB,
                nodename: $scope.nodeNameUI
            });
        }
        
    	$http.post(url,data)
        .success(function(result) {
        	if(result.success === 'true'){
        		$scope.saveState.noSaves = false;
        		$scope.validationState = true;
                $scope.activeInactivedeploy = true;
        		_dirty = true;
        		$scope.closePoup();
        	}else{
        		$scope.saveState.noSaves = true;
        		_dirty = false;
        	}
        })
        .error(function(response){
        	$scope.titlemsg = ALERT;
        	$scope.msg = "Could not save the script details";
        	$scope.showpopup();
        });
            
    }
  
    function targetTableCreate(portDetails){    	
    	var targetTable=[];
    	angular.forEach(portDetails.orig.value.fullType[0].messageargumentList, function(value,key){
    		if(value.complexType){
    			angular.forEach(value.complexType.messageargumentList, function(value1,key1){
    				if(value1.complexType){
    					angular.forEach(value1.complexType.messageargumentList, function(value2,key2){
    						targetTable.push({"tag":value2.tag,
    							"name": value2.name,
    							"type": value2.type,
    							"role": value2.role,
    							"parent": value1.complexType.messageName,
    							"parentRole": value1.role,
    							"grandParent": value.complexType.messageName,
    							"grandParentRole": value.role,
    							"greatGrandParent": portDetails.orig.value.fullType[0].messageName});
    					});
    				} else{
    					targetTable.push({"tag":value1.tag,
							"name": value1.name,
							"type": value1.type,
							"role": value1.role,
							"parent": value.complexType.messageName,
							"parentRole": value.role,
							"grandParent": portDetails.orig.value.fullType[0].messageName,
							"greatGrandParent": ""});
    				}
    			});
    		} else{
    			targetTable.push({"tag":value.tag,
    								"name": value.name,
    								"type": value.type,
    								"role": value.role,
    								"parent": portDetails.orig.value.fullType[0].messageName,
    								"grandParent": "",
    								"greatGrandParent": ""});
    		}
    	});
    	$scope.targetMapTable = targetTable;
    	dc.redrawAll();
    	$scope.closePoup();
    }
    
    $scope.showMappingTable = function(ev) {
    	$scope.disableDone = false;
    	if($scope.readSolutionArray[$scope.nodeIdDB]){
    		if($scope.targetMapTable === null || $scope.targetMapTable === undefined || $scope.readSourceTable === null || $scope.readSourceTable === undefined){
        		$scope.disableDone = true;
        	}
    	} else {
    		if($scope.targetMapTable === undefined || $scope.dataShow === undefined){
        		$scope.disableDone = true;
        	}
    	}
    	
        $mdDialog.show({
            contentElement: '#myDialogMapping',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
    $scope.readSolutionMapping = function(){
    	var checkFieldMap = new Map();
    	var fieldNameMap = new Map(); var fieldTypeMap = new Map(); var tagMap = new Map();
    	angular.forEach($scope.readSourceTable, function(value,key){
    		checkFieldMap.set(key,value.input_field.checked);
    		fieldNameMap.set(key,value.input_field.name);
    		fieldTypeMap.set(key,value.input_field.type);
    		tagMap.set(key,value.input_field.mapped_to_field);
    	});
    	
    };
    
    var checkFieldMap = new Map();
    $scope.mapCheckField = function(index){
    	if($scope.readSolutionArray[$scope.nodeIdDB]){
    		checkFieldMap.set(index,checkFieldMap.get(index) === "YES"?"NO":"YES");
    		if(checkFieldMap.get(index) === "NO"){
    			$scope.readSourceTable[index].input_field.type = "null";
    			$scope.readSourceTable[index].input_field.mapped_to_field = "null";
    			fieldTypeMap.set(index,null);
    			tagMap.set(index,null);
    		}
    		$scope.readSourceTable[index].input_field.checked = checkFieldMap.get(index);
    	}else{
    	checkFieldMap.set(index,this.checkfield?"YES":"NO");
    	if(!this.checkfield){
    		fieldTypeMap.set(index,null);
    		this.fieldType = "Select Type";
    		tagMap.set(index,null);
    		this.mapTag = "Select Tag";
    	}
    	}
    }
    
    var fieldNameMap = new Map();
    $scope.mapFieldName = function(index){
    	fieldNameMap.set(index,this.data)
    }
    
    var fieldTypeMap = new Map();
    $scope.mapFieldType = function(index){
    	fieldTypeMap.set(index,this.fieldType);
    };
    
    var tagMap = new Map();
    $scope.mappingTag = function(index){
    	tagMap.set(index,this.mapTag);
    };
    
    $scope.mappingsSave = function(){
    	var mapOutput = []; var mapInput = [];
    	if($scope.readSolutionArray[$scope.nodeIdDB]){
    		mapInput = $scope.readSourceTable;        	
    	} else{
    	for(var i=0; i < $scope.dataShow.length; i++){
        	if(fieldNameMap.get(i) === null || fieldNameMap.get(i) === undefined){
        		fieldNameMap.set(i,$scope.dataShow[i]);
        	}
        }
    	
    	if($scope.dbType === "csv" || $scope.dbType === "sql"){
    		angular.forEach($scope.dataShow, function(valueData,keyData){
	    		mapInput.push({"input_field": {
	    			"name":fieldNameMap.get(keyData),
	    			"type": fieldTypeMap.get(keyData)?fieldTypeMap.get(keyData):"null",
	    			"checked": checkFieldMap.get(keyData) === "YES"?"YES":"NO",
	    			"mapped_to_field": tagMap.get(keyData)?tagMap.get(keyData):"null"
	    		}});
	    	});
    	} else if($scope.dbType === "image"){
    		angular.forEach($scope.dataImage, function(valueData,keyData){
    			mapInput.push({"input_field": {
	    			"name": valueData.Fieldname,
	    			"type": valueData.FieldType,
	    			"checked": checkFieldMap.get(keyData) === "YES"?"YES":"NO",
	    			"mapped_to_field": tagMap.get(keyData)?tagMap.get(keyData):"null"
	    		}});
    		});
    	}
    	}
    	var targetTableCdump = $scope.targetMapTable;
    	angular.forEach(targetTableCdump, function(value,key){
    		if(value.greatGrandParent !== ""){
	    		mapOutput.push({"output_field": {
	    			"tag": value.tag,
	    			"name": value.name,
	    			"type_and_role_hierarchy_list": [
	    				{
	    					"name": value.type,
	    					"role": value.role
	    				},
	    				{
	    					"name": value.parent,
	    					"role": value.parentRole
	    				},
	    				{
	    					"name": value.grandParent,
	    					"role": value.grandParentRole
	    				},
	    				{
	    					"name": value.greatGrandParent,
	    					"role": "null"
	    				}
	    			]
	    		}});
    		} else if(value.grandParent !== ""){
    			mapOutput.push({"output_field": {
	    			"tag": value.tag,
	    			"name": value.name,
	    			"type_and_role_hierarchy_list": [
	    				{
	    					"name": value.type,
	    					"role": value.role
	    				},
	    				{
	    					"name": value.parent,
	    					"role": value.parentRole
	    				},
	    				{
	    					"name": value.grandParent,
	    					"role": "null"
	    				}
	    			]
    			}});
    		} else if(value.parent !== ""){
    			mapOutput.push({"output_field": {
	    			"tag": value.tag,
	    			"name": value.name,
	    			"type_and_role_hierarchy_list": [
	    				{
	    					"name": value.type,
	    					"role": value.role
	    				},
	    				{
	    					"name": value.parent,
	    					"role": "null"
	    				}
	    			]
    			}});
    		} else {
    			mapOutput.push({"output_field": {
	    			"tag": value.tag,
	    			"name": value.name,
	    			"type_and_role_hierarchy_list": [
	    				{
	    					"name": value.type,
	    					"role": null
	    				}
	    			]
    			}});
    		}
    	});
    	var url;
    	if($scope.readSolutionArray[$scope.nodeIdDB]){
    		var data = {
        			"databrokerMap": {
        			    "script": null,
        			    "csv_file_field_separator": null,
        			    "data_broker_type": null,
        			    "first_row": null,
        			    "local_system_data_file_path": null,
        			    "target_system_url": null,
        			    "database_name": null,
  	    				"table_name": null,
  	    				"jdbc_driver_data_source_class_name": null,
  	    				"user_id": null,
  	    				"password": null,
  	    				"protobufFile":null,
        			    "map_action": null,
        			    "map_inputs": $scope.readSourceTable,
        			    "map_outputs": mapOutput
        			  },
        			  "fieldMap":null,
        			  "collatorMap": null,
        			  "splitterMap": null
        		};
    	} else{
    	var data = {
    			"databrokerMap": {
    			    "script": null,
    			    "csv_file_field_separator": null,
    			    "data_broker_type": null,
    			    "first_row": null,
    			    "local_system_data_file_path": null,
    			    "target_system_url": null,
    			    "database_name": null,
	    			"table_name": null,
	    			"jdbc_driver_data_source_class_name": null,
	    			"user_id": null,
	    			"password": null,
	    			"protobufFile":null,
    			    "map_action": null,
    			    "map_inputs": mapInput,
    			    "map_outputs": mapOutput
    			  },
    			  "fieldMap":null,
    			  "collatorMap": null,
    			  "splitterMap": null
    		};
    	}
        if(_solutionId){
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                solutionid:_solutionId,
                nodeid: $scope.nodeIdDB,
                nodename: $scope.nodeNameUI
            });
        } else {
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                cid:_cid,
                nodeid: $scope.nodeIdDB,
                nodename: $scope.nodeNameUI
            });
        }
        
    	$http.post(url,data)
        .success(function(result) {
        	if(result.success === 'true'){        		
        		$scope.saveState.noSaves = false;
        		$scope.validationState = true;
                $scope.activeInactivedeploy = true;
        		_dirty = true;
        		$scope.closePoup();
        	}else{
        		$scope.saveState.noSaves = true;
        		_dirty = false;
        	}
        })
        .error(function(response){
        	$scope.titlemsg = ALERT;
        	$scope.msg = "Could not save the mapping details";
        	$scope.showpopup();
        });
        
    }

    $scope.mapDBType = function(dbType){
        if(dbType === "csv" || dbType === "image" || dbType === "json"){
               $scope.fileUrl = "file://";
               $scope.userImage = undefined;
               $scope.enableScript = false;
               $scope.uploadFileRequired = true;
        }else{
               $scope.fileUrl = "http://";
               $scope.userImage = undefined;
               $scope.enableScript = false;
               $scope.uploadFileRequired = true;
        }
        $scope.readSolutionArray[$scope.nodeIdDB]=false;
     }
    
    $scope.cancelRead = [];
    $scope.changeRead = function(){
    	$scope.cancelRead[$scope.nodeIdDB] = $scope.readSolutionArray[$scope.nodeIdDB];
    	if($scope.readSolutionArray[$scope.nodeIdDB]){
    		$scope.readSolutionArray[$scope.nodeIdDB] = false;
    		$scope.readSolution = false;
    	}		
    }
    
    $scope.changeReadCollate = function(){
    	
    	if($scope.collateScheme.length !== 0){
    		var srcPortDetails,tarPortDetails;
	    	_ports.forEach(function(p){
	    		if(p.nodeId === $scope.nodeIdDB){
	    			if(p.bounds === inbounds){
	    				srcPortDetails = _diagram.getPort($scope.nodeIdDB, null, p.portname);
	    			} else if(p.bounds === outbounds){
	    				tarPortDetails = _diagram.getPort($scope.nodeIdDB, null, p.portname);
	    			}
	    		}
	    	});
	    	
	    	if(srcPortDetails.edges.length === 0 && tarPortDetails.edges.length === 0)
    			$scope.collateSchemeChange = false;
    		else
    			$scope.collateSchemeChange = true;
    	}
    	
    	if(!$scope.collateSchemeChange){
	    	$scope.cancelRead[$scope.nodeIdDB] = $scope.readSolutionArray[$scope.nodeIdDB];
	    	if($scope.readSolutionArray[$scope.nodeIdDB]){
	    		$scope.readSolutionArray[$scope.nodeIdDB] = false;
	    		$scope.readSolution = false;
	    	}	
	    	
	    	if($scope.collateScheme !== null || $scope.collateScheme !== undefined || $scope.collateScheme !== "")
	    		$scope.enableCollateDone = true;
	    	else if($scope.collateScheme !== $scope.collateSelectedDetails[$scope.nodeIdDB])
	    		$scope.enableCollateDone = true;
    	}
    	
    }
    
    $scope.changeReadSplit = function(){
    	
    	if($scope.splitScheme.length !== 0){
    		var srcPortDetails,tarPortDetails;
	    	_ports.forEach(function(p){
	    		if(p.nodeId === $scope.nodeIdDB){
	    			if(p.bounds === inbounds){
	    				srcPortDetails = _diagram.getPort($scope.nodeIdDB, null, p.portname);
	    			} else if(p.bounds === outbounds){
	    				tarPortDetails = _diagram.getPort($scope.nodeIdDB, null, p.portname);
	    			}
	    		}
	    	});
	    	
	    	if(srcPortDetails.edges.length === 0 && tarPortDetails.edges.length === 0)
    			$scope.splitSchemeChange = false;
    		else
    			$scope.splitSchemeChange = true;
    	}
    	
    	if(!$scope.splitSchemeChange){
	    	$scope.cancelRead[$scope.nodeIdDB] = $scope.readSolutionArray[$scope.nodeIdDB];
	    	if($scope.readSolutionArray[$scope.nodeIdDB]){
	    		$scope.readSolutionArray[$scope.nodeIdDB] = false;
	    		$scope.readSolution = false;
	    	}	
	    	
	    	 if($scope.splitScheme !== null || $scope.splitScheme !== undefined || $scope.splitScheme !== "")
	    			$scope.enableSplitDone = true;
	    		else if($scope.splitScheme !== $scope.splitSelectedDetails[$scope.nodeIdDB])
	    			$scope.enableSplitDone = true;
    	}
    }
   
	
    $('#optionshow').hide();
    $('#myFile').change( function(event) {
		 var filename = $("#myFile").val().split('.');
		 var ext = filename[1];
		 if(ext === 'csv'){
			 $('#optionshow').show();
		 } else{
			 $('#optionshow').hide();
		 } 	 
	});
    
    $scope.showCollatorSelection = function(ev) {
    	$scope.enableCollateDone = false;
    	$scope.collateSchemeChange = false;
    	
        $mdDialog.show({
            contentElement: '#myDialogCollatorSelector',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
    $scope.showSplitterSelection = function(ev) {
    	$scope.enableSplitDone = false;
    	$scope.splitSchemeChange = false;
    	
        $mdDialog.show({
            contentElement: '#myDialogSplitterSelector',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
    $scope.showCollatorMap = function(ev){
    	$mdDialog.show({
            contentElement: '#myDialogCollatorMap',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
    $scope.showSplitterMap = function(ev){
    	$mdDialog.show({
            contentElement: '#myDialogSplitterMap',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
    $scope.closePopupCollatorSelector = function(){
    	if($scope.cancelRead[$scope.nodeIdDB] !== undefined)
    		$scope.readSolutionArray[$scope.nodeIdDB] = $scope.cancelRead[$scope.nodeIdDB];
    	if($scope.readSolutionArray[$scope.nodeIdDB]){
    		$scope.collateScheme = collateDetails.collatorType;
    	} else if($scope.collateSelect){
    		$scope.collateScheme = $scope.collateSelectedDetails[$scope.nodeIdDB];
    		$scope.enableCollateDone = false;
    	} else{
    		$scope.collateScheme = "";
    		$scope.enableCollateDone = false;
    	}
    	$scope.collatorSelector.$setPristine();
         $scope.collatorSelector.$setUntouched();
         $mdDialog.cancel();	
    };
    
    $scope.closePopupSplitterSelector = function(){
    	if($scope.cancelRead[$scope.nodeIdDB] !== undefined)
    		$scope.readSolutionArray[$scope.nodeIdDB] = $scope.cancelRead[$scope.nodeIdDB];
    	if($scope.readSolutionArray[$scope.nodeIdDB]){
    		$scope.splitScheme = splitDetails.splitterType;
    	} else if($scope.splitSelect){
    		$scope.splitScheme = $scope.splitSelectedDetails[$scope.nodeIdDB];
    		$scope.enableSplitDone = false;
    	} else{
    		$scope.splitScheme = "";
    		$scope.enableSplitDone = false;
    	}
    	$scope.collatorSelector.$setPristine();
        $scope.splitterSelector.$setUntouched();
        $mdDialog.cancel();	
    };
    
    $scope.closeMappingCollatorPopup = function(){
    	$mdDialog.cancel();
    	if($scope.readSolutionArray[$scope.nodeIdDB]){
    		readCollatorMapping(collateDetails.mapInputs, $scope.nodeIdDB);
    	} 
    };
    
    $scope.closeMappingSplitterPopup = function(){
    	$mdDialog.cancel();
    	if($scope.readSolutionArray[$scope.nodeIdDB]){
    		readSplitterMapping(splitDetails.mapOutputs, $scope.nodeIdDB);
    	}
    };
    
    $scope.processCollatorSelection = function(){
    	var url;
    	$scope.collateSelect = true;
    	$scope.enableCollateDone = false;
    	$scope.collateSelectedDetails[$scope.nodeIdDB] = $scope.collateScheme;
    	$scope.collateSchemes[$scope.nodeIdDB] = $scope.collateScheme;
    	if($scope.collateScheme === "Array-based"){
    		$scope.enableCollateMap = false;
    	} else if($scope.collateScheme === "Parameter-based"){
    		$scope.enableCollateMap = true;
    	}
    	
    		var data = {
        			"databrokerMap": null,
        			  "fieldMap":null,
        			  "collatorMap": {
        				  "collator_type": $scope.collateScheme,
        				  "output_message_signature": null,
        				  "map_inputs": null,
        				  "map_outputs": null
        			  },
        			  "splitterMap": null
        		}
            if(_solutionId){
                url = build_url(options.modifyNode, {
                    userid: get_userId(),
                    solutionid:_solutionId,
                    nodeid: $scope.nodeIdDB,
                    nodename: $scope.nodeNameUI
                });
            } else {
                url = build_url(options.modifyNode, {
                    userid: get_userId(),
                    cid:_cid,
                    nodeid: $scope.nodeIdDB,
                    nodename: $scope.nodeNameUI
                });
            }
            
        	$http.post(url,data)
            .success(function(result) {
            	if(result.success === 'true'){
            		$scope.saveState.noSaves = false;
            		$scope.validationState = true;
                    $scope.activeInactivedeploy = true;
            		_dirty = true;
            		$scope.closePoup();
            	}else{
            		$scope.saveState.noSaves = true;
            		_dirty = false;
            	}
            })
            .error(function(response){
            	$scope.titlemsg = ALERT;
            	$scope.msg = "Could not save the Collator scheme details";
            	$scope.showpopup();
            });
    };
    
    $scope.processSplitterSelection = function(){
    	var url;
    	$scope.splitSelect = true;
    	$scope.enableSplitDone = false;
    	$scope.splitSelectedDetails[$scope.nodeIdDB] = $scope.splitScheme;
    	$scope.splitSchemes[$scope.nodeIdDB] = $scope.splitScheme;
    	if($scope.splitScheme === "Copy-based"){
    		$scope.enableSplitMap = false;
    	} else if($scope.splitScheme === "Parameter-based"){
    		$scope.enableSplitMap = true;
    	}
    	
    		var data = {
        			"databrokerMap": null,
        			  "fieldMap":null,
        			  "collatorMap": null,
        			  "splitterMap": {
        				  "splitter_type": $scope.splitScheme,
        				  "input_message_signature": null,
        				  "map_inputs": null,
        				  "map_outputs": null
        			  }
        		}
            if(_solutionId){
                url = build_url(options.modifyNode, {
                    userid: get_userId(),
                    solutionid:_solutionId,
                    nodeid: $scope.nodeIdDB,
                    nodename: $scope.nodeNameUI
                });
            } else {
                url = build_url(options.modifyNode, {
                    userid: get_userId(),
                    cid:_cid,
                    nodeid: $scope.nodeIdDB,
                    nodename: $scope.nodeNameUI
                });
            }
            
        	$http.post(url,data)
            .success(function(result) {
            	if(result.success === 'true'){
            		$scope.saveState.noSaves = false;
            		$scope.validationState = true;
                    $scope.activeInactivedeploy = true;
            		_dirty = true;
            		$scope.closePoup();
            	}else{
            		$scope.saveState.noSaves = true;
            		_dirty = false;
            	}
            })
            .error(function(response){
            	$scope.titlemsg = ALERT;
            	$scope.msg = "Could not save splitter scheme details";
            	$scope.showpopup();
            });
    };
    
    var collateTargetTable = [];
    function createCollateTargetTable(outputPort,inputPort){
    	collateTargetTable = [];
    	angular.forEach(outputPort.orig.value.originalType[0].messageargumentList, function(value,key){
    		collateTargetTable.push({"tag": value.tag,
    								"name": value.name,
    								"type": value.type,
    								"role": value.role});
    	});

    	$scope.collateTargetTables[inputPort.node.orig.key] = collateTargetTable;
    	if($scope.collateSourceTables[inputPort.node.orig.key] === null){
    		$scope.collateSourceTables[inputPort.node.orig.key] = [];
    	}
    	
    }
    
    var collateSourceTable = [];
    function createCollateSourceTable(inputPort,outputPort){
    	collateSourceTable = $scope.collateSourceTables[outputPort.node.orig.key];
    	angular.forEach(inputPort.orig.value.originalType[0].messageargumentList, function(value, key){
    		collateSourceTable.push({"modelName": inputPort.node.orig.value.name,
    								"messageSignature": JSON.stringify(inputPort.orig.value.originalType[0]),
    								"tag": value.tag,
    								"name": value.name,
    								"type": value.type,
    								"role": value.role});
    	});

    	$scope.collateSourceTables[outputPort.node.orig.key] = collateSourceTable;
    	if($scope.collateTargetTables[outputPort.node.orig.key] === null){
    		$scope.collateTargetTables[outputPort.node.orig.key] = [];
    	}
    }
    
    var splitTargetTable=[];
    function createSplitTargetTable(outputPort,inputPort){
    	splitTargetTable = $scope.splitTargetTables[inputPort.node.orig.key];
    	angular.forEach(outputPort.orig.value.originalType[0].messageargumentList, function(value,key){
    		splitTargetTable.push({"modelName": outputPort.node.orig.value.name,
    								"messageSignature": JSON.stringify(outputPort.orig.value.originalType[0]),
    								"tag": value.tag,
    								"name": value.name,
    								"type": value.type,
    								"role": value.role});
    	});

    	$scope.splitTargetTables[inputPort.node.orig.key] = splitTargetTable;
    	if($scope.splitSourceTables[inputPort.node.orig.key] === null)
    		$scope.splitSourceTables[inputPort.node.orig.key] = [];    	
    }
    
    var splitSourceTable = [];
    function createSplitSourceTable(inputPort,outputPort){
    	splitSourceTable = [];
    	angular.forEach(inputPort.orig.value.originalType[0].messageargumentList, function(value, key){
    		splitSourceTable.push({	"tag": value.tag,
    								"name": value.name,
    								"type": value.type,
    								"role": value.role});
    	});
    	$scope.splitSourceTables[outputPort.node.orig.key] = splitSourceTable;
    	if($scope.splitTargetTables[outputPort.node.orig.key] === null)
    		$scope.splitTargetTables[outputPort.node.orig.key] = [];
    }
    
    function createCollateSourceRead(mapInput,nodeId){
    	collateSourceTable = [];
    	angular.forEach(mapInput, function(value,key){
    		collateSourceTable.push({"modelName": value.input_field.source_name,
    								"messageSignature": value.input_field.message_signature,
									"tag": value.input_field.parameter_tag,
									"name": value.input_field.parameter_name,
									"type": value.input_field.parameter_type,
									"role": value.input_field.parameter_role});
    	});
    	$scope.collateSourceTables[nodeId] = collateSourceTable;
    }
    
    function createCollateTargetRead(mapOutput,nodeId){
    	collateTargetTable = [];
    	angular.forEach(mapOutput, function(value,key){
    		collateTargetTable.push({"tag": value.output_field.parameter_tag,
									"name": value.output_field.parameter_name,
									"type": value.output_field.parameter_type,
									"role": value.output_field.parameter_role});
    	});
    	$scope.collateTargetTables[nodeId] = collateTargetTable;
    }
    
    function readCollatorMapping(mapInput,nodeId){
    	collateTagMap = [];
    	collateErrorIndicator = new Map();
    	angular.forEach(mapInput, function(value,key){
    		collateTagMap[key]=value.input_field.mapped_to_field;    		
    		collateErrorIndicator.set(key,value.input_field.error_indicator);
    	});
    	$scope.collateTags[nodeId] = collateTagMap;
    	$scope.readCollateTagMap = collateTagMap;
    	$scope.collateErrors[nodeId] = collateErrorIndicator;
    	$scope.collateErrorMap = collateErrorIndicator;
    }
    
    function createSplitSourceRead(mapInput,nodeId){
    	splitSourceTable = [];
    	angular.forEach(mapInput, function(value,key){
    		splitSourceTable.push({"tag": value.input_field.parameter_tag,
									"name": value.input_field.parameter_name,
									"type": value.input_field.parameter_type,
									"role": value.input_field.parameter_role});
    	});
    	$scope.splitSourceTables[nodeId] = splitSourceTable;
    }
    
    function createSplitTargetRead(mapOutput,nodeId){
    	splitTargetTable = [];
    	angular.forEach(mapOutput, function(value,key){
    		splitTargetTable.push({"modelName": value.output_field.target_name,
    							    "messageSignature": value.output_field.message_signature,    			
    							    "tag": value.output_field.parameter_tag,
									"name": value.output_field.parameter_name,
									"type": value.output_field.parameter_type,
									"role": value.output_field.parameter_role});
    	});
    	$scope.splitTargetTables[nodeId] = splitTargetTable;
    }
    
    function readSplitterMapping(mapOutput,nodeId){
    	splitTagMap = [];
    	splitErrorIndicator = new Map();
    	angular.forEach(mapOutput, function(value, key){
    		splitTagMap[key] = value.output_field.mapped_to_field;
    		splitErrorIndicator.set(key, value.output_field.error_indicator);
    	});
    	$scope.splitTags[nodeId] = splitTagMap;
    	$scope.readSplitTagMap = splitTagMap;
    	$scope.splitErrors[nodeId] = splitErrorIndicator;
    	$scope.splitErrorMap = splitErrorIndicator;
    }
    
    var collateTagMap = new Map();
    var collateErrorIndicator = new Map();
    
    $scope.collateMappingTag = function(index){
    	collateTagMap = $scope.collateTags[$scope.nodeIdDB];
    	if($scope.readSolutionArray[$scope.nodeIdDB])
    		collateTagMap[index] = $scope.readCollateTagMap[index];
    	else
    		collateTagMap[index] = this.collateMapTag;
        var occurrences = { };
        var collateErrorIndicator = new Map();
        
        for (var i = 0, j = collateTagMap.length; i < j; i++) {
        	if(collateTagMap[i] !== undefined)
        		occurrences[collateTagMap[i]] = (occurrences[collateTagMap[i]] || 0) + 1;
        }
        
        angular.forEach($scope.collateSourceMapTable, function(value,key){
        	angular.forEach(occurrences, function(value1,key1){
        		if(value1 > 1 && key1!=="null"){
        			angular.forEach($scope.collateSourceMapTable, function(value4,key4){
        				if(key1 === collateTagMap[key4])
            				collateErrorIndicator.set(key4, "False");
        			});
        			
        		} else{
        			var keepGoing = true;
        			angular.forEach($scope.collateTargetMapTable, function(value2,key2){
        				
        				if(keepGoing){
        					if(collateTagMap[key] === value2.tag && value.role === value2.role && value.type === value2.type){
        						keepGoing = false;
        						angular.forEach($scope.collateSourceMapTable, function(value3,key3){
        							if(value.modelName === value3.modelName){
        								collateErrorIndicator.set(key, "True");
        								if(collateTagMap[key3] === undefined)
        									collateErrorIndicator.set(key3, "True");
        								
        							}	
        						});
        					} else{
        						if(collateErrorIndicator.get(key) === undefined)
        							collateErrorIndicator.set(key, "False");
        					}
        				}
        			});
        		}
        	});
        });
       $scope.collateTags[$scope.nodeIdDB] = collateTagMap;
       $scope.collateErrors[$scope.nodeIdDB] = collateErrorIndicator;
        $scope.collateErrorMap = collateErrorIndicator;
    };
    
    var splitTagMap = new Map();
    var splitErrorIndicator = new Map();
    
    $scope.splitMappingTag = function(index){
    	splitTagMap = $scope.splitTags[$scope.nodeIdDB];
    	if($scope.readSolutionArray[$scope.nodeIdDB])
    		splitTagMap[index] = $scope.readSplitTagMap[index];
    	else
    		splitTagMap[index] = this.splitMapTag;
        var splitErrorIndicator = new Map();
        
	    for(var st = 0; st < splitTagMap.length-1 ; st++){
	    	for(var stm = st+1;stm <splitTagMap.length;stm++){
	       		if(splitTagMap[st] === splitTagMap[stm]){
	        		if($scope.splitTargetMapTable[st].modelName === $scope.splitTargetMapTable[stm].modelName){
		        		splitErrorIndicator.set(st, "False");
		        		splitErrorIndicator.set(stm, "False");
	        		} 
	        	}
	        }
	    }
	    angular.forEach($scope.splitTargetMapTable, function(value,key){
	       	if(splitErrorIndicator.get(key) === undefined){
        		var keepGoing = true;
        		angular.forEach($scope.splitSourceMapTable, function(value2,key2){
        		
        			if(keepGoing){
        				if(splitTagMap[key] === value2.tag && value.role === value2.role && value.type === value2.type){
        					keepGoing = false;
        					angular.forEach($scope.splitTargetMapTable, function(value3,key3){
        						if(value.modelName === value3.modelName){
        							splitErrorIndicator.set(key, "True");
        							if(splitTagMap[key3] === undefined)
        								splitErrorIndicator.set(key3, "True");
        								
       							}	
       						});
       					} else{
       						if(splitErrorIndicator.get(key) === undefined)
       							splitErrorIndicator.set(key, "False");
       					}
       				}
       			});
        	}
        });
        $scope.splitTags[$scope.nodeIdDB] = splitTagMap;
        $scope.splitErrors[$scope.nodeIdDB] = splitErrorIndicator;
        $scope.splitErrorMap = splitErrorIndicator;
        
    };
    
    $scope.collateMappingsSave = function(){
    	var url;
    	var collateMapInputs = []; var collateMapOutputs = [];
    	$scope.selectedCollateMapping = collateTagMap;
    	$scope.selectedCollateError = $scope.collateErrorMap;
	    	angular.forEach($scope.collateSourceMapTable, function(value,key){
	    		collateMapInputs.push({ "input_field": {
	    							"source_name": value.modelName,
	    							"message_signature": value.messageSignature,
	    							"parameter_name": value.name,
	    							"parameter_type": value.type,
	    							"parameter_role": value.role,
	    							"parameter_tag": value.tag,
	    							"mapped_to_field": collateTagMap[key] ? collateTagMap[key]: "null",
	    							"error_indicator": $scope.collateErrorMap.get(key) === "True" ? "True" : "False" }});
	    	});
	    	
	    	angular.forEach($scope.collateTargetMapTable, function(value1,key1){
	    		collateMapOutputs.push({"output_field": {
	    							  "parameter_tag": value1.tag,
	    							  "parameter_name": value1.name,
	    							  "parameter_type": value1.type,
	    							  "parameter_role": value1.role	}});
	    	});
    	
    	var data = {
    			"databrokerMap": null,
    			  "fieldMap":null,
    			  "collatorMap": {
    				  "collator_type": null,
    				  "output_message_signature": null,
    				  "map_inputs": collateMapInputs,
    				  "map_outputs": collateMapOutputs
    			  },
    			  "splitterMap": null
    		}
        if(_solutionId){
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                solutionid:_solutionId,
                nodeid: $scope.nodeIdDB,
                nodename: $scope.nodeNameUI });
        } else {
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                cid:_cid,
                nodeid: $scope.nodeIdDB,
                nodename: $scope.nodeNameUI });
        }
        
    	$http.post(url,data)
        .success(function(result) {
        	if(result.success === 'true'){
        		$scope.saveState.noSaves = false;
        		$scope.validationState = true;
                $scope.activeInactivedeploy = true;
        		_dirty = true;
        		$scope.closePoup();
        	}else{
        		$scope.saveState.noSaves = true;
        		_dirty = false;
        	}
        })
        .error(function(response){
        	$scope.titlemsg = ALERT;
        	$scope.msg = "Could not save the Collator Mapping details";
        	$scope.showpopup();
        });
    	
    };
    
    $scope.splitMappingsSave = function(){
    	var url;
    	var splitMapInputs = []; var splitMapOutputs = [];
    	$scope.selectedSplitMapping = splitTagMap;
    	$scope.selectedSplitError = $scope.splitErrorMap;
    	angular.forEach($scope.splitSourceMapTable, function(value,key){
    		splitMapInputs.push({ "input_field": {
    							"parameter_name": value.name,
    							"parameter_type": value.type,
    							"parameter_role": value.role,
    							"parameter_tag": value.tag }});
    		});
    	
    	angular.forEach($scope.splitTargetMapTable, function(value1,key1){
    		splitMapOutputs.push({"output_field": {
    							  "target_name": value1.modelName,
    							  "message_signature": value1.messageSignature,
    							  "parameter_tag": value1.tag,
    							  "parameter_name": value1.name,
    							  "parameter_type": value1.type,
    							  "parameter_role": value1.role,
    							  "mapped_to_field": splitTagMap[key1] ? splitTagMap[key1]:null,
    		    				  "error_indicator": $scope.splitErrorMap.get(key1) === "True" ? "True" : "False" }});
    	});
    	
    	var data = {
    			"databrokerMap": null,
    			  "fieldMap":null,
    			  "collatorMap": null,
    			  "splitterMap": {
    				  "splitter_type": null,
    				  "input_message_signature": null,
    				  "map_inputs": splitMapInputs,
    				  "map_outputs": splitMapOutputs
    			  }
    		}
        if(_solutionId){
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                solutionid:_solutionId,
                nodeid: $scope.nodeIdDB,
                nodename: $scope.nodeNameUI });
        } else {
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                cid:_cid,
                nodeid: $scope.nodeIdDB,
                nodename: $scope.nodeNameUI });
        }
        
    	$http.post(url,data)
        .success(function(result) {
        	if(result.success === 'true'){
        		$scope.saveState.noSaves = false;
        		$scope.validationState = true;
                $scope.activeInactivedeploy = true;
        		_dirty = true;
        		$scope.closePoup();
        	}else{
        		$scope.saveState.noSaves = true;
        		_dirty = false;
        	}
        })
        .error(function(response){
        	$scope.titlemsg = ALERT;
        	$scope.msg = "Could not save the Split Mapping details";
        	$scope.showpopup();
        });
    };
   
}
}



