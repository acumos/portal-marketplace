
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
DSController.$inject = ['$scope','$http','$filter','$q','$window','$rootScope','$mdDialog','$state','$stateParams'];

function DSController($scope,$http,$filter,$q,$window,$rootScope,$mdDialog ,$state,$stateParams) {
	componentHandler.upgradeAllRegistered();
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
		
	$scope.checkProbe = function(){
		alert("Probe has been checked in");
	}
	$scope.checkboxDisable = true;
	$scope.activeInactivedeploy = true;
    $scope.userDetails = JSON.parse(localStorage
                                    .getItem("userDetail"));
    if($scope.userDetails == null){alert("Please sign in to application");$state.go('home');return;}
    $scope.validationState = true;
    $scope.searchbox = true;
    $scope.showSearch = function(){
        $scope.searchbox = false;
    };
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
    var extras = false;
    //var dataBrokerOutput = new Map();
    // document.getElementById("showHide").className = "disnone";
    var operations = []; var messages = [];
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
        // alert('handleDragOver');
        e.preventDefault(); // Necessary. Allows us to drop.
        e.dataTransfer.dropEffect = 'move';  // See the section
        // on the
        // DataTransfer
        // object.
        return false;
    };
    $scope.handleDrop = function(e){


        $scope.canvas = true;
        e.preventDefault();
        var bound = _diagram.root().node().getBoundingClientRect();
        var pos = _diagram.invertCoord([e.clientX - bound.left,
                                        e.clientY - bound.top]);
        var type = e.dataTransfer.getData('text/plain');


        var max = 0;
        /*
         * var remVrsn = type.replace(/[^0-9\.]+/g, ""); remVrsn =
         * '('+remVrsn +')'; type = type.replace(remVrsn,'');
         */
        // var res = type.split('').reverse().join('');
        //res = res.replace(/\(/,'&').split('&');
        var res = type.split("(");
        var getver = res[1].split(")");
        var ver = getver[0];
        // var type = res[0];
        ver = '('+ver+')';
        type = type.replace(ver,'');
        //type = res[1].split('').reverse().join('');
        console.log(type);
        var typeModel = type+'+'+getver[0];

        _drawGraphs.nodeCrossfilter().all().forEach(function(n) {
            var nodeType = n.type.name;
            var nodeTypeLength = nodeType.length;
            var lastChar = nodeType.slice(-1);
            if(isNaN(lastChar)){
                var number = n.nodeId.match(/[0-9]+$/);
                if(!number)
                    return; // currently all ids will be type +
                // number
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
            nodeId: type + (max+1),
            type: {"name": type}
        };
        data.name = data.nodeId;
        $scope.nodeName=data.name;
        // $scope.nodeNameUI=$scope.nodeName;
        var nodeId = '',nodeVersion = '';
        $http.get(_catalog.fModelUrl(_components.get(type))).success(function(tgif) {
        	
            console.log("tgif :");
            console.log(tgif);

            nodeId = _components.get(type).solutionId;
            nodeVersion = _components.get(type).version;
            $scope.solutionDetails=_components.get(type);
            $scope.showProperties=null;
            console.log($scope.solutionDetails);
            $scope.packageName= JSON.stringify(tgif.self.name);
            $scope.requireCalls= tgif.services.calls;
            $scope.capabilityProvides=tgif.services.provides;
            var url= build_url(options.protobuf, {
                userId: get_userId(),
                solutionId :  nodeId,
                version : nodeVersion
            });
            $http.get(url).success(function(proto){
                console.log(proto);
               
                $scope.protoNode=proto;
                var protoJson=proto;
                jsonProtoNode.set($scope.solutionDetails.solutionName,protoJson);
            });

            var requirementJson=[], capabilityJson=[];
            // get requirements
            var check_isValid_calls ='';
            var check_isValid_provides= '';
            angular.forEach(tgif.services.calls, function(value, key) {
                if(value.request.format.length !== 0){
                    check_isValid_calls = value.request.format[0].messageName;

                    var reqObj = value.request.format;

                    // console.log(reqObj);
                    // reqOperation.push(value.config_key);
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
                    // console.log(reqObj);
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

             console.log("capabiluity"+angular.toJson(capabilityJson));
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
            // based on solutionDetails, we can change this type
            switch($scope.solutionDetails.toolKit){
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
                    /*
					 * name: data.name, nodeId: data.id, nodeSolutionId : '',
					 * nodeVersion : '', nodeSolutionId : nodeId, nodeVersion :
					 * nodeVersion, // properties: JSON.stringify([]),
					 * requirements: JSON.stringify(requirementJson), type :
					 * type, capabilities: JSON.stringify(capabilityJson),
					 * ndata: JSON.stringify(ndata)
					 */
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
                    /*
					 * name: data.name, nodeId: data.id, nodeSolutionId : '',
					 * nodeVersion : '', //nodeIdCnt.toString(), //type:
					 * JSON.stringify({type: type}), nodeSolutionId : nodeId,
					 * nodeVersion : nodeVersion, //properties:
					 * JSON.stringify([]), // properties: JSON.stringify([]),
					 * requirements: JSON.stringify(requirementJson), type :
					 * type, capabilities: JSON.stringify(capabilityJson),
					 * ndata: JSON.stringify(ndata)
					 */
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

            // Json formating start
            // Json formating end
            // console.log(angular.toJson(nodeDetails));
            $http.post(url,nodeDetails)
                .success(function(response) {
                    $scope.cleardis= false;
                    // $scope.deleteDis= false;
                    nodeIdCnt++;
                    $scope.checkboxDisable = false;
                    // console.log(_catalog.ports(data.id,
                    // def.requirements, def.capabilities));
                    // console.log(def.requirements);
                    _ports = _ports.concat(_catalog.ports(data.nodeId, data.modelName, def.requirements, def.capabilities, def.extras));

                    update_ports();
                    _drawGraphs.createNode(pos, data);
                    set_dirty(true);
                }).error(function(response){
                    alert("Please click on New to create a new Solution");
                });
        });
    };

    /*
	 * $http.get(_catalog.fModUrl(_components.get(type))).success(function(proto){
	 * $scope.nodeProtobuf=proto; $scope.messageName=proto.messages.messageName;
	 * $scope.argumentList = proto.messages.argumentList;
	 * 
	 * angular.forEach($scope.argumentList, function(value, key) {
	 * $scope.msgDetails=value.firstToken+' '+value.type+' '+value.name+' =
	 * '+value.tag; });
	 * 
	 * });
	 */
    function reset(){
        $scope.saveState.noSaves = true;
        $('#deleteHide').hide();
        $scope.cleardis = true;
    }
    function resetValid(){

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
                    $scope.validationState = true;
                    $scope.saveState.noSaves = true;
                    $scope.checkboxDisable = true;
                   $scope.myCheckbox = false;
                    $scope.activeInactivedeploy = true;
                    $scope.console = null;
                    reset();
                    if (parameter == 'new'){
                        $scope.titlemsg ="New Solution";
                        $scope.msg = "Create a new Composite Solution";
                        $scope.showok = true;
                        $scope.showpopup();
                    }
                    $('#validateActive').removeClass('active');
                    $('#validateActive').removeClass('enabled');
                    $('#consoleMsg').removeClass('console-successmsg');
                    $('#consoleMsg').removeClass('console-errormsg');
                    //$('#consoleMsg').html('');
                    $scope.down = true;

                    // $scope.deleteState.noDeletes = null;
                    display_solution(_solution);
                    load_catalog();
                    _solutionId = '';

                }
                var userId = get_userId(),
                    url = build_url(options.create, {userId: userId});
                /*
				 * if(parameter == 'new'){ $scope.titlemsg ="New Solution";
				 * $scope.msg = "Create a new Composite Solution"; $scope.showok =
				 * true; $scope.showpopup(); // alert("Create a new Composite
				 * Solution");
				 *  }
				 */
                $(".ds-grid-bg").css("background", "url('../images/grid.png')");
                $scope.closeDisabledCheck = !$scope.closeDisabledCheck;
                countComSol += 1;
                $scope.namedisabled = false;
                changeNode = new Object();
                // console.log(changeNode);
                $http.post(url)
                    .success(new_solution);
            });}
    };
    $scope.loadSolution = function(entry) {
        if(entry.toolKit === 'CP' || entry.toolKit === 'DS') {
            /*
			 * var url = build_url(options.read, { userId:get_userId(),
			 * solutionId: entry.solutionId, version: entry.version });
			 */
            var url = build_url(options.read, {
                userId:get_userId(),
                solutionId: entry.solutionId,
                version: entry.version
            });
            var changeNode = new Object();
            $http.get(url)
                .success(function(result) {
                    console.log(result);
                    if(result.probeIndicator == 'true'){
                    	$scope.myCheckbox = true;
                    }else{
                    	$scope.myCheckbox = false;
                    }
                    $scope.checkboxDisable = false;
                    $scope.cleardis = false;
                    // $scope.deleteDis= false;
                    $scope.namedisabled = true;$scope.canvas = true;
                    _solutionId = entry.solutionId;
                    $scope.solutionName = result.cname;
                    $scope.solutionVersion = result.version;
                    // $scope.storeSolutionName = result.cname;
                    _solution = result;_dirty = true;
                    _solution.nodes.forEach(function(n) {
                        if(n.ndata && n.ndata.fixed && n.ndata.px !== undefined && n.ndata.py !== undefined)
                            n.fixedPos = {x: +n.ndata.px, y: +n.ndata.py};
                    });
                    $(".ds-grid-bg").css("background", "url('../images/grid.png')");
                    $scope.closeDisabled = false;
                    display_solution(_solution);
                }).error(function(result){
                    alert("Cannot load the solution");
                });
        }
    };
    $scope.storesolutionName ='';
    var duplicateSol = false;
    $scope.saveSolution = function() {
        if(!_dirty && duplicateSol == false)
            return;
	        duplicateSol = false;
        if(!$scope.solutionName) {
            // alert("Please fill all mandatory fields");
            set_focus('input-name');
            return;
        }
        if(!$scope.solutionDescription) {
            // alert("Please fill all mandatory fields");
            set_focus('input-description');
            return;
        }
        if(!$scope.solutionVersion) {
            // alert("Please fill all mandatory fields");
            set_focus('input-version');
            return;
        }
        $scope.storesolutionName = $scope.solutionName;
        save_solution($scope.solutionName)
            .then(function() {
                $scope.namedisabled = true;
                load_catalog();
                _dirty = false;
            });
    };
    $scope.deleteSolution = function(val) {
        var url ='';
        url = build_url(options.deleteCompositeSolution, {
            userid:get_userId(),
            solutionid :val.solutionId,  // solutionid :
            // _solutionId,
            version :  val.version,  // version :
            // $scope.solutionVersion
        });
        $http.post(url)
            .success(function(result) {

                if(result.success == "true"){
                    load_catalog().success(load_initial_solution);
                    $scope.msg= "Solution is deleted successfully";
                    $scope.titlemsg ="Delete Solution";
                    // alert("Solution is deleted successfully");
                    solutionPrPB();$scope.closePoup();
                    $scope.showpopup();
                    if(_solutionId == val.solutionId){
                        $scope.clearSolution();
                        $scope.solutionDetails = false;$scope.solutionDescription = '';
                        _cid = '';_solutionId = '';$scope.solutionName = '';$scope.namedisabled = false;$scope.solutionVersion = '';
                        // code
                    }
                }
                else if(result.success == "false"){
                    $scope.msg= "Solution is not deleted";
                    $scope.titlemsg ="Delete Solution";
                    $scope.closePoup();
                    $scope.showpopup();
                }
            });

        /*
         * if($scope.solutionName && confirm('Really delete solution "' +
         * $scope.solutionName + '"?'))
         * delete_solution($scope.solutionName).then(function(cat2) {
         * load_catalog(); });
         */

    };


    var qs = querystring.parse();
    var urlBase = baseURL + '/dsce/';
    var options = Object.assign({
    	base:"dsce/dsce/",
        // base: urlBase,
        //base: 'http://localhost:8088/dsce/',
    	//base: 'http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8088/dsce/',
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
    function tgif_reqcap_to_tosca(rc) {

        return {
            "capability": {
                "id": "",
                "name": `${rc.request.format}+${rc.request.version}+${rc.response.format}+${rc.response.version}`
            },
            "id": "",
            "name": "",
            "relationship": "",
            "target": {
                "description": "",
                "name": ""
            },
            "target_type": "Node"
        };
    }

    function is_wildcard_type(type) {
    	if(type !== "script"){
    		return type[0].messageName === 'ANY'; // replace with correct
													// checks on proto-json
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

    // var _toolkits = [{"toolkitCode":"CP","toolkitName":"Composite
    // Solution"},{"toolkitCode":"H2","toolkitName":"H2O"},{"toolkitCode":"RC","toolkitName":"RCloud"},{"toolkitCode":"SK","toolkitName":"Scikit-Learn"},{"toolkitCode":"TF","toolkitName":"TensorFlow"}];
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
                return model.category === 'null' ? "others" : model.category;
                // return model.toolKit === 'null' ? null :
                // toolKitName(model.toolKit);
            },
            fModelToolKit: function(model) {
                // console.log("toolkit"+model.toolKit);
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
            	// if(extras.length !==0){
            if(extras != undefined){
	                return requirements.map((req,i) => ({
	                    nodeId: nid,
	                    portname: req.capability.id+'+'+JSON.stringify(removeMsgNames(req.capability.name))+'+req'+i,
	                    type: is_wildcard_type(req.capability.name) ? null : JSON.stringify(removeMsgNames(req.capability.name)),
	                    fullType: req.capability.name,
	                    originalType: req.capability.name,
	                    shortname: req.capability.id,
	                    bounds: outbounds
	                })).concat(
	                    capabilities.map((cap,i) => ({
	                        nodeId: nid,
	                        portname: cap.target.id+'+'+JSON.stringify(removeMsgNames(cap.target.name))+'+cap'+i,//
	                        type: is_wildcard_type(cap.target.name) ? null : JSON.stringify(removeMsgNames(cap.target.name)),
	                        fullType: cap.target.name,
	                        originalType: cap.target.name,
	                        shortname: cap.target.id,
	                        bounds: inbounds
	                    }))).concat(
	                    		extras.map((ext,i) => ({
	                    			nodeId: nid,
	                    			portname: 'xtra'+i,
	                    			type: is_wildcard_type(ext) ? null : ext,
	                    			originalType: ext,
	                    			bounds: xtrabounds
	                    		})));
	                    
            	} else {
            		return requirements.map((req,i) => ({
	                    nodeId: nid,
	                    portname: req.capability.id+'+'+JSON.stringify(removeMsgNames(req.capability.name))+'+req'+i,
	                    type: is_wildcard_type(req.capability.name) ? null : JSON.stringify(removeMsgNames(req.capability.name)),
	                    fullType: req.capability.name,
	                    originalType: req.capability.name,
	                    shortname: req.capability.id,
	                    bounds: outbounds
	                })).concat(
	                    capabilities.map((cap,i) => ({
	                        nodeId: nid,
	                        portname: cap.target.id+'+'+JSON.stringify(removeMsgNames(cap.target.name))+'+cap'+i,//
	                        type: is_wildcard_type(cap.target.name) ? null : JSON.stringify(removeMsgNames(cap.target.name)),
	                        fullType: cap.target.name,
	                        originalType: cap.target.name,
	                        shortname: cap.target.id,
	                        bounds: inbounds
	                    })));
            	}
            }/*
				 * , fModUrl: function(model){ return
				 * build_url(options.protobuf, { userId:get_userId(),
				 * solutionId: this.fModelId(model), version:
				 * this.fModelVersion(model) }); }
				 */

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
        var userDetail = localStorage.getItem("userDetail");
        if(!userDetail)
            return null;
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
            // $scope.validationState = false;
            //$scope.deleteState.noDeletes=true;

            // $scope.saveState.descStyle.opacity = 1;
            $scope.saveState.desc = message || 'solution has changes';
        } else {
            $scope.saveState.noSaves = false;
            //$scope.validationState = false;
            //$scope.deleteState.noDeletes=true;
            // $scope.saveState.descStyle.opacity = 0.5;
            $scope.saveState.desc = message || 'solution is saved';
        }
    }

    //
    // CANVAS
    //

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
        var port_flat = dc_graph.flat_group.make(_ports, d => d.nodeId + '/' + d.portname);

        _diagram
            .portDimension(port_flat.dimension).portGroup(port_flat.group);
    }

    function display_solution(solution) {
        $('#deleteHide').hide();
        $scope.databroker.$invalid = false;
        $scope.validationState = true;
        $scope.activeInactivedeploy = true;
        $scope.console = null;
        $('#validateActive').removeClass('active');
        $('#validateActive').removeClass('enabled');
        $('#consoleMsg').removeClass('console-successmsg');
        $('#consoleMsg').removeClass('console-errormsg');
        //$('#consoleMsg').html('');
        $scope.down = true;
        var script = [];
        
        _diagram.child('fix-nodes')
            .clearFixes();
        console.log(solution);
        var nodes = solution.nodes || (console.warn('no nodes in composite solution!'), []),
            edges = solution.relations || [];
        _ports = [];
        console.log(nodes);
        var i=0;
        nodes.forEach(function(n) {
            // console.log(n);
        	script = [];
            var lastChar = n.nodeId.slice(-1);
            var res = n.nodeId.split(lastChar);
          // var properties = n.
//            if(n.properties.length > 0 || n.type.name == "DataBroker"){
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
                console.log(proto);
                // $scope.protoNode=proto;

                protoJsonRead.set(res[0],proto);
                console.log(protoJsonRead);
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
        
        
        edges.forEach(function(e){
            var srcPort = _diagram.getPort(e.sourceNodeId, null, e.sourceNodeRequirement),
                tarPort = _diagram.getPort(e.targetNodeId, null, e.targetNodeCapability);
            wildcardPorts.copyType(e, srcPort, tarPort);
            console.log(srcPort);
            console.log(tarPort);
            nodes.forEach(function(n){
            	if((n.nodeId === e.sourceNodeId || n.nodeId === e.targetNodeId) && n.type.name === "DataBroker"){
            		if(srcPort.orig.value.bounds === outbounds){
                    	targetTableCreate(srcPort);
                    } else if(tarPort.orig.value.bounds === outbounds){
                    	targetTableCreate(tarPort);
                    }
            	}
            });
            
        });
        console.log(_ports);
        _ports.forEach(function(p){
        	var tarPort = _diagram.getPort(p.nodeId, null, p.portname);
        	console.log(tarPort);
        });
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
            
            // need to check the if condition to satisfy the databroker
            if(n.type.name === "DataBroker"){
            	$scope.nodeIdDB = n.nodeId;
            	angular.forEach(n.properties, function(value, key){
            		if(value.data_broker_map != null){
            			$scope.scriptText = value.data_broker_map.script;
            			$scope.dbType = value.data_broker_map.data_broker_type;
            			$scope.fileUrl = value.data_broker_map.target_system_url;
            			$scope.firstRow = value.data_broker_map.first_row;
            			if(!$scope.userImage){
            				$scope.userImageNew =value.data_broker_map.local_system_data_file_path;
            			}
            			$scope.readSourceTable = value.data_broker_map.map_inputs;
            			if($scope.dbType == 'csv'){
            				$scope.dataShow = $scope.readSourceTable;
            			}
            			$scope.checkedRead = true;
            			$scope.uncheckedRead = false;
            			$scope.readSolution = true;
            		}
            	});
            }
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
                if(result.errorCode){alert("Solution not saved");}
                else if(result.duplicate){alert(result.duplicate); duplicateSol = true;}
                else if(result.alert){
                    set_dirty(false, 'saved at ' + d3.time.format('%X')(new Date()));
                    /*
					 * if(confirm(result.alert +'?')){ $scope.updateOldSol =
					 * true; $scope.solutionNameSave = name;
					 * save_solution(name); $scope.updateOldSol = false; }
					 */
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
                    //    $scope.saveState.noSaves = true;
                    $('#validateActive').removeClass('active');
                    $('#validateActive').addClass('enabled');
                    $scope.showpopup();

                    solutionPrPB();
                    // set_dirty(true, 'saved at ' + d3.time.format('%X')(new
					// Date()));
                    $scope.saveState.noSaves = true;
                    _solutionId = result.solutionId;
                    $scope.solutionIdvalidate = result.solutionId;
                }
                // alert(JSON.stringify(result))
            });
        });
    }
    $scope.validateCompSolu = function(){
        var args = {
            userId: get_userId(),
            solutionName: $scope.solutionName,
            solutionId: $scope.solutionIdvalidate,
            version:$scope.solutionVersion
        };                     var url = build_url(options.validate, args);
        $('#validateActive').removeClass('enabled');
        $scope.validationState = true;

        return $http.post(url).success(function(result) {
            console.log(result);
            if(result.success == "true"){
            	 
                $scope.validationState = true;
                $scope.activeInactivedeploy = false;
                $scope.solutionIdDeploy = $scope.solutionIdvalidate;
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
        return save_solution(_catalog, name);    // this needs to be
        // checked
    }
    function delete_solution(name) {
        // not implemented
    }

    //
    // PROPERTIES PANE
    //

    function print_value(v) {
        if(!v || ['string','number','boolean'].indexOf(typeof v) !== -1)
            return v.toString();
        else
            return JSON.stringify(v);
    }
    function display_properties(url) {

        /* $scope.showIcon=[]; */
        $scope.initIndex=false;
        $scope.setAction = function(index){
            if(index==$scope.selectedIndex){
                $scope.selectedIndex=-1;
            } else{
                $scope.selectedIndex=index;
                $scope.initIndex=true;

            }

            /*
			 * $scope.initIndex=!$scope.initIndex; $scope.selectedIndex=index;
			 */
        };

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
        /*
         * else { content.style('visibility', 'hidden'); }
         */
    }
    function display_data_mapper(nodeId, wilds) {
        console.log(wilds);
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
                sort: (a,b) => a.node.order - b.node.order
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
        var ports = lnodes.map(n => ({
            nodeId: n.id,
            side: 'out',
            bounds: moutbounds
        })).concat(rnodes.map(n => ({
            nodeId: n.id,
            side: 'in',
            bounds: minbounds
        })));
        var nodes = parentNodes.concat(lnodes, rnodes);
        console.log(nodes);
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

        console.log(edges);
        var node_flat = dc_graph.flat_group.make(nodes, n => n.id),
            edge_flat = dc_graph.flat_group.make(edges, e => e.sourcename),
            port_flat = dc_graph.flat_group.make(ports, p => p.nodeId + '/' + p.side);

        var sdRegex = /^(source|dest)/;
        var layout = dc_graph.flexbox_layout()
        //.logStuff(true)
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
            .nodeShape(n => layout.keyToAddress()(mapper.nodeKey()(n)).length < 2 ? 'nothing' : 'rounded-rect')
            .nodeStrokeWidth(0)
            .nodeTitle(null)
            .fitStrategy('zoom')
            .nodeLabelAlignment(n => /^source/.test(n.key) ? 'right' : 'left')
            .nodeLabelPadding({x: 10, y: 0})
            .edgesInFront(true)
            .edgeSourcePortName('out')
            .edgeTargetPortName('in')
            .edgeLabel(null)
            .portNodeKey(p => p.value.nodeId)
            .portName(p => p.value.side)
            .portBounds(p => p.value.bounds)
            .portElastic(false);

        mapper.child('validate', dc_graph.validate('data mapper'));
        mapper.child('place-ports', dc_graph.place_ports());
        //mapper.child('troubleshoot', dc_graph.troubleshoot());

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
                // console.log(edge);
                /*
				 * console.log(lnodes); console.log(rnodes); console.log(lport);
				 */

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
                          }
            		};
               /* var fieldMap = {
                    input_field_message_name: lport.fullType[0].messageName,
                    input_field_tag_id: lnodeDetails.tag,
                    map_action: "add",
                    output_field_message_name: rport.fullType[0].messageName,
                    output_field_tag_id: rnodeDetails.tag
                };*/
                /*console.log(fieldMap);*/
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
                console.log(params);
                /*var dataConnector = {"fieldMap": fieldMap};*/
                url = build_url(options.modifyNode, params);
                /*return $http.post(url,angular.toJson(fieldMap))*/
                return $http.post(url,dataConnector)
                    .then(function(response){
                        $scope.saveState.noSaves = false;
                        _dirty = true;
                        //console.log(Promise.resolve(edge));
                        return Promise.resolve(edge);
                    });
                //return Promise.resolve(edge);


                // // return $http promise instead
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
                    /*
					 * for(var j=0;j<rnodes.length;j++){ if(rnodes[j].id ===
					 * edgeDetails.target.orig.key){ rnodeDetails = rnodes[j]; } }
					 * for(var i=0;i<lnodes.length;i++){ if(lnodes[i].id ===
					 * edgeDetails.source.orig.key){ lnodeDetails = lnodes[i]; } }
					 * var fieldMap = { input_field_message_name:
					 * lport.fullType[0].messageName, input_field_tag_id:
					 * lnodeDetails.tag, map_action: "delete",
					 * output_field_message_name: "", output_field_tag_id: "" };
					 * console.log(fieldMap); if(_solutionId){ params = {
					 * userid: get_userId(), solutionId: _solutionId, version:
					 * $scope.solutionVersion, nodeid: lport.nodeId }; } else
					 * if(_cid){ params = { userid: get_userId(), cid: _cid,
					 * nodeid: lport.nodeId }; } console.log(params); url =
					 * build_url(options.modifyNode, params); console.log(url);
					 */
                    /* return $http.post(url,angular.toJson(fieldMap)); */
                });
                return Promise.all(promises)
                    .then(function(responses){
                        $scope.saveState.noSaves = false;
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
    $scope.getProperties=function(solutionHover){


        // var typeHover =
        // _diagram.getNode(solutionHover).value.type;
        var compsHover = _catalog.models().filter(function(comp) {
            return _catalog.fModelName(comp) === solutionHover;
        });
        $scope.nodeNameUI=null;
        $scope.showDataBroker = null;
        $scope.showDataMapper = null;
        $scope.solutionDetails=compsHover[0];
        $scope.showProperties=null;
        $scope.showLink=null;
        if(compsHover[0].toolKit != 'CP'){
            // $scope.solutionDetails=compsHover[0];
            if(compsHover.length === 1)
                display_properties(_catalog.fModelUrl(compsHover[0]));
        } else
            // $scope.solutionDetails=null;
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
  
    // used for copying the messages into "any" port
    var wildcardPorts = dc_graph.wildcard_ports({
        get_type: p => p.orig.value.type,
        set_type: function(p1, p2) {
            if(p2) {
                p1.orig.value.type = p2.orig.value.type;
                p1.orig.value.fullType = p2.orig.value.fullType;
            }
            else p1.orig.value.type = p1.orig.value.fullType = null;
        },
        is_wild: p => is_wildcard_type(p.orig.value.originalType),
        update_ports: update_ports
    });

    function initialize_canvas() {
        _diagram = dc_graph.diagram('#canvas');
        _diagram // use width and height of parent, <section
        // droppable=true>
            .width(function(element) { return element.parentNode.getBoundingClientRect().width; })
            .height(function(element) { return element.parentNode.getBoundingClientRect().height; })
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
            .edgeLabel(e => e.value.linkName || '')
            .nodePadding(20)
            .nodeLabel(function(n) { return n.value.name; })
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
               /*
				 * var lastChar = n.key.slice(-1); //var res = n.key.split("(");
				 * var res = n.key.split(lastChar); //console.log(res[0]);
				 * 
				 */       
            	
            	var res = n.key.slice(0, -1);
            	var nodeDet=_components.get(res);
                //console.log(nodeDet);
                if(/DataMapper/.test(nodeDet.solutionName))
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
                /*
				 * var lastChar = d.key.slice(-1); var res =
				 * d.key.split(lastChar);
				 */
            	var res = d.key.slice(0, -1);
            	var nodeDet=_components.get(res);
               // var nodeDet=_components.get(res[0]);
                //var nodeDet = _components.get(d.value.type.name);
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
            .nodeFixed(function(n) { return n.value.fixedPos; })
            .edgeStroke('#777')
            .portNodeKey(p => p.value.nodeId)
            .portName(p => p.value.portname)
            .portBounds(p => p.value.bounds)
            .edgeSourcePortName(e => e.value.sourceNodeRequirement)
            .edgeTargetPortName(e => e.value.targetNodeCapability);

        _diagram.child('validate', dc_graph.validate('design canvas'));
        _diagram.child('keyboard', dc_graph.keyboard().disableFocus(true));
        _diagram.content('text-with-icon', dc_graph.with_icon_contents(dc_graph.text_contents(), 35, 35));

        _diagram.child('place-ports', dc_graph.place_ports());

        var symbolPorts = dc_graph.symbol_port_style()
            .portSymbol(p => p.orig.value.type)
            .portColor(p => p.orig.value.type)
            .colorScale(d3.scale.ordinal().range(
                // colorbrewer qualitative scale
                d3.shuffle(
                    ['#e41a1c','#377eb8','#4daf4a','#984ea3','#ff7f00','#eebb22','#a65628','#f781bf'] // 8-class
                    // set1
                    // ['#1b9e77','#d95f02','#7570b3','#e7298a','#66a61e','#e6ab02','#a6761d','#666666']
                    // // 8-class dark2
                )))
            .portBackgroundFill(p => p.value.bounds === inbounds ? 'white' : 'black');
        
        var letterPorts = dc_graph.symbol_port_style()
    	.content(dc_graph.symbol_port_style.content.letter())  
        .outlineStrokeWidth(1)  
        .symbol('S')  
        .symbolScale(x => x)  
        .color('black')  
        .colorScale(null); 
        
        _diagram
            .portStyle('symbols', symbolPorts)
            /* .portStyleName('symbols'); */
            .portStyle('letters', letterPorts)  
            .portStyleName(function(p) {
            	return (p.value.type === "script") ? 'letters' : 'symbols';  
            	});  

        var portMatcher = dc_graph.match_ports(_diagram, symbolPorts);

        portMatcher.isValid(
            (sourcePort, targetPort) =>
                wildcardPorts.isValid(sourcePort, targetPort) &&
                sourcePort.orig.value.bounds !== xtrabounds &&  
                targetPort.orig.value.bounds !== xtrabounds &&  
                sourcePort.orig.value.bounds !== targetPort.orig.value.bounds);

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



                console.log("e======"+angular.toJson(e));
                console.log("tport " + tport.name + " " + angular.toJson(tport.orig));
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
                // need to check for databroker
                /* if(sport.) */
               if(sport.node.orig.value.modelName == "DataBroker" || sport.node.orig.value.type.name == 'DataBroker' || tport.node.orig.value.modelName == "DataBroker" ||tport.node.orig.value.type.name == 'DataBroker' ){
                	properties = {};
                	if(sport.orig.value.bounds === outbounds){
                		targetTableCreate(tport);
                	} else if(tport.orig.value.bounds === outbounds){
                		targetTableCreate(sport);
                	}
                } else {
                
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
                        }
                    };
                    //properties = [angular.toJson(properties)];
                    // console.log(properties);
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
                        }
                    };
                    //properties = [angular.toJson(properties)];
                    //console.log(angular.toJson(properties));
                } else{
                    properties = {};
                }
              }/* else {
            	  properties = {};
              }*/
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

                return $http.post(url,angular.toJson(properties))
                    .then(function(response) {
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
            // console.log(nodeId);
            var node = _diagram.getNode(nodeId);

            $scope.saveState.noSaves = false;
            _dirty = true;
            // changeNode.set(nodeId,node.value);

            // $scope.nodeNameUI = node.value.name;
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
                    // cid:_cid,
                    solutionid:_solutionId,
                    linkid: edgeId,
                    linkname:text,
                    // version : $scope.solutionVersion,
                });
            } else {
                url = build_url(options.modifyLink, {
                    userid: get_userId(),
                    cid:_cid,
                    linkid: edgeId,
                    linkname:text,
                    // version : $scope.solutionVersion,

                });
            }
            return $http.post(url).then(function(response) {
                _dirty = true;
                $scope.saveState.noSaves = false;
                return text;
            });
        });
        _diagram.child('label-edges', label_edges);

        var select_ports = dc_graph.select_ports({
            outlineFill: p => p.value.bounds === inbounds ? '#cfd' : '#8db',
            outlineStroke: '#4a2',
            outlineStrokeWidth: 2
        }).multipleSelect(false);
        _diagram.child('select-ports', select_ports);

        var select_nodes_group = dc_graph.select_things_group('select-nodes-group', 'select-nodes');
        var select_edges_group = dc_graph.select_things_group('select-edges-group', 'select-edges');
        var select_ports_group = dc_graph.select_things_group('select-ports-group', 'select-ports');
        select_nodes_group.on('set_changed.show-info', function(nodes) {
        	$scope.showProperties = false;
            // console.log(nodes);
            if(nodes.length == 0){$('#deleteHide').hide();deleteShow = 0}else {$('#deleteHide').show();deleteShow = 1;};
            // $scope.deleteState.noDeletes=true;
            setTimeout(function() {
                if(nodes.length>1)
                    throw new Error('not expecting multiple select');
                else if(nodes.length === 1) {
                    select_edges_group.set_changed([], false);
                    select_ports_group.set_changed([], true);

                    var selectedNodeId = _diagram.getNode(nodes[0]).key;
                    var lastChar = selectedNodeId.slice(-1);
                    var res = selectedNodeId.split(lastChar);
                    var type = res[0];
                    var comps = _catalog.models().filter(function(comp) {
                        return _catalog.fModelName(comp) === type;
                    });
                    $scope.solutionDetails=comps[0];
                    // console.log(changeNode);
                    // console.log(Object.keys(changeNode).length);
                    if(Object.keys(changeNode).length !== 0 && changeNode[nodes[0]]){
                        $scope.nodeNameUI = changeNode[nodes[0]];
                    } else{
                        $scope.nodeNameUI = nodes[0];
                    }
                    $scope.showProperties=null;
                    $scope.showLink = null;
                    // detect if node has wildcard ports
                   /* var wilds = _ports.filter(function(p) {
                        return p.nodeId === nodes[0] && is_wildcard_type(p.originalType);
                    });*/
                    //console.log(wilds);
                    switch($scope.solutionDetails.toolKit){
                    case 'BR': 
                    	$scope.showDataBroker = true;
                    	$scope.showDataMapper = false;
                    	$scope.solutionDetails = null;
                    	$scope.$apply();
                    	break;
                    case 'TC':
                    	$scope.showDataBroker = false;
                    	$scope.showDataMapper = false;
                    	$scope.$apply();
                    	display_properties(_catalog.fModelUrl(comps[0]));
                    	break;
                    default:
                    	// detect if node has wildcard ports
                         var wilds = _ports.filter(function(p) {
                             return p.nodeId === nodes[0] && is_wildcard_type(p.originalType);
                         });
                    	if(wilds.length) {
                    		$scope.showDataBroker = false;
                    		$scope.showDataMapper = true;
                    		$scope.solutionDetails = null;
                    		$scope.$apply();
                    		display_data_mapper(nodes[0], wilds);
                    	}
                    	else if(comps.length === 1) {
                    		$scope.showDataBroker = false;
                    		$scope.showDataMapper = null;
                    		//$scope.solutionDetails = true;
                    		$scope.$apply();
                    		display_properties(_catalog.fModelUrl(comps[0]));
                    	}
                    }
                    $scope.tabChange = 0;
                } else display_properties(null);
            }, 0);
        });
        select_edges_group.on('set_changed.show-info', function(edges) {
            // $scope.deleteState.noDeletes=true;
            /*console.log(edges);*/

            if(edges.length === 1) {
                select_nodes_group.set_changed([], false);
                select_ports_group.set_changed([], true);
                // getEdge should give you enough info to look
                // up properties you want for edges
                // console.log(_diagram.getEdge(edges[0]));
                setTimeout(function() {
                    var edge = _diagram.getWholeEdge(edges[0]);
                    if(edge !== null){
                        /*console.log(edgeType);*/
                        $scope.linkDetails = edge;
                        $scope.showDataMapper = null;
                        $scope.solutionDetails = null;
                        $scope.showProperties = null;
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
            console.log(ports);
            if(ports.length>0) {
            	$scope.nodeIdDB = ports[0].node;
                select_nodes_group.set_changed([], false);
                select_edges_group.set_changed([], true);
                var p = _diagram.getPort(ports[0].node, null, ports[0].name);
                $scope.portDets = p;
                console.log(p);
                // console.log(p.orig.value.bounds === inbounds ?
                // 'input' : 'output', p.orig.value.type);
                // display matching models for port here
                if(p.orig.value.bounds === inbounds){
                    portType = "input";
                } else if(p.orig.value.bounds === outbounds){
                	if(p.node.orig.value.modelName === "DataBroker" || p.node.orig.value.type.name === "DataBroker"){
                		if(p.edges.length > 0 || p.orig.value.type !== null)
                			portType = "output";
                		else 
                			portType = "DBoutput";
                	}
                	else {
                		portType = "output";
                	}
                } else {
                	portType = "script";
                }
                
                if(portType === "script"){	
                     	// code for UI for script port and output port of data
						// broker
                	$scope.showScript();
                     	
                } else if(portType === "DBoutput"){
                	// code for output port of data broker
                	$scope.selectOutputMessage(p);
                	
                	
                } else {
                var selectedNodeId = ports[0].node;
                var res = selectedNodeId.slice(0,-1);
              //  var res = selectedNodeId.split(lastChar);
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
                //var keySave = portType+'+'+port_info;

                // console.log(port_info);
                // display matching models for port here
                /*var url = '';*/
                console.log($scope.solutionVersion);
/*                console.log(nodeVersion);*/
                var url = build_url(options.getMatchingModels, {
                    userid: get_userId(),
                    solutionid: $scope.nodeSolutionDetails.solutionId,
                    version : $scope.nodeSolutionDetails.version,
                    portType: portType,
                    protobufJsonString: port_info
                });

                $http.get(url).success(function(data){
                    // console.log(data);
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
                    // $scope.matchModels = matchingModels;
                    setTimeout(function() {
                        $scope.matchModels = matchingModels;
                        $scope.tabChange = 1;
                        $scope.clicked = true;
                        $scope.$apply();
                    }, 0);
                })
                    .error(function(data){
                        var matchingModels = [];
                        setTimeout(function() {
                            $scope.matchModels = matchingModels;
                            $scope.tabChange = 1;
                            $scope.clicked = true;
                            $scope.$apply();
                        }, 0);

                    });
            }
            } else display_properties(null);
        });

        $scope.deleteNodeEdge=function(value){
            // if(confirm('Do you want to delete the node/link"' +
            // $scope.solutionName + '"?'))
            delete_nodes.deleteSelection();
            delete_edges.deleteSelection();
            // alert("Solution is deleted successfully");
            $scope.closePoup();
        };

        var delete_nodes = dc_graph.delete_nodes("nodeId")
            .crossfilterAccessor(function(chart) {
                return _drawGraphs.nodeCrossfilter();
            })
            .dimensionAccessor(function(chart) {
                return _diagram.nodeDimension();
            })
            .onDelete(function(nodes) {
                console.log(nodes);
                $scope.NodeName=nodes[0];
                $scope.removename = nodes[0].slice(0, -1); //check
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
                        // after the back-end has accepted the
                        // deletion, we can remove unneeded
                        // ports
                        _ports = _ports.filter(p => p.nodeId !== nodes[0]);
                        update_ports();
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
                console.log(edges);
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
                        // after the back-end has accepted the
                        // deletion, we can remove unneeded
                        // ports
                        _ports = _ports.filter(p => p.edges !== edges[0]);
                        update_ports();
                        return wildcardPorts.resetTypes(_diagram, edges);
                    });
            });
        _diagram.child('delete-edges', delete_edges);

        /*
         * var operations =
         * [$scope.protoNode.protobuf_json.service.listOfOperations.operatioName];
         * var messages =
         * [$scope.protoNode.protobuf_json.service.listOfOperations.listOfInputMessages.inputMessageName,protoNode.protobuf_json.service.listOfOperations.listOfOutputMessages.outPutMessageName]
         * console.log(operations); console.log(messages);
         */
        /*
		 * var url= build_url(options.protobuf, { userId: get_userId(),
		 * solutionId : _solutionId, version : $scope.solutionVersion });
		 * $http.get(url).success(function(proto){ $scope.protoNode=proto; });
		 */

        function input_generate_operation(d) {
            // console.log(jsonProto);
            operations=[]; messages=[];var i=0;var m=0;
            if(jsonProtoMap){
                operations = d.orig.value.shortname;
                for(var l=0;l<jsonProtoMap.length;l++){
                    messages[m++] = jsonProtoMap[l].messageName;
                }

            } else if(jsonProto){
                /*jsonProto=jsonProto[0];*/

                //console.log(jsonProto);


                angular.forEach(jsonProto.protobuf_json.service.listOfOperations, function(value, key) {
                    // console.log(d.orig.value.shortname);
                    // console.log(value.operatioName);
                    if(d.orig.value.shortname === value.operationName){
                        operations = value.operationName;
                        angular.forEach(value.listOfInputMessages,function(value1,key1){
                            // console.log(value1);
                            messages[i++] = [value1.inputMessageName];
                        });

                    }
                    // console.log(operations);
                    // console.log(messages);
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
                /*jsonProto=jsonProto[0];*/

                //console.log(jsonProto);

                angular.forEach(jsonProto.protobuf_json.service.listOfOperations, function(value, key) {
                    if(d.orig.value.shortname === value.operationName){
                        operations = value.operationName;
                        angular.forEach(value.listOfOutputMessages,function(value1,key1){
                            messages[i++] = [value1.outPutMessageName];
                        });
                        // messages =
                        // [value.listOfOutputMessages[0].outPutMessageName];
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
                    //console.log(jsonProtoMap);
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
                // I don't entirely understand how d3-tip is
                // calculating position
                // this attempts to keep position fixed even
                // though size of g.port is changing
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
                            /*angular.forEach(jsonProtoMap[l].messageargumentList, function(value, key){
                                messageJson[i++]=value.role+' '+value.type+' '+value.name+' = '+value.tag;
                            });*/
                            angular.forEach(jsonProtoMap[l].messageargumentList, function(value1, key1) {
                            	//debugger;
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
                            				/*
											 * complexProtoMap.push(value2.complexType.messageName);
											 * angular.forEach(complexProtoMap,function(val,ky){
											 * 
											 * });
											 */
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
//                            		complexProtoMap[i++]= [
//                            			$scope.complexMessageDet.push(complexJson)
//                            		];
//                            		console.log('complexProtoMap - '+angular.toJson(complexProtoMap));
                            		}
                            	
                            	
                                messageJson[i++]=value1.role+' '+value1.type+' '+value1.name+' = '+value1.tag;
                                
                            });
                            
                        }
                    }

                } else{

            		var i = 0;
                    angular.forEach(jsonProto.protobuf_json.listOfMessages, function(value, key) {
                        if(operations+'_'+value.messageName === id){
                            // console.log(id);
                            $scope.messageDet=value.messageName;
                            
                            // console.log(jsonProto);
                            // console.log(value.messageargumentList);
                            angular.forEach(value.messageargumentList, function(value1, key1) {
                            	//debugger;
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
                            				/*
											 * complexProtoMap.push(value2.complexType.messageName);
											 * angular.forEach(complexProtoMap,function(val,ky){
											 * 
											 * });
											 */
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
//                            		complexProtoMap[i++]= [
//                            			$scope.complexMessageDet.push(complexJson)
//                            		];
//                            		console.log('complexProtoMap - '+angular.toJson(complexProtoMap));
                            		}
                            	
                            	
                                messageJson[i++]=value1.role+' '+value1.type+' '+value1.name+' = '+value1.tag;
                                
                            });
                            

                        }
                    });
                }
                $scope.complexProtoJson = [];
                $scope.complexProtoJson = $scope.complexProtoMap;
                console.log(complexMapArray);
                $scope.complexMapArrayUI=complexMapArray;
                /* angular.forEach(data.items, function(value, key) {}); */
               /*
				 * var typeName = '',valueType = '';
				 * angular.forEach(jsonProto.protobuf_json.listOfMessages[0].messageargumentList,
				 * function(value, key) { typeName = value.messageName;
				 * angular.forEach(value, function(value, key) { typeName =
				 * value.messageName;
				 * 
				 * });
				 * 
				 * });
				 */
               // $scope.getComplexProtoJson =
				// angular.toJson($scope.complexProtoJson[0]);
               /*
				 * for(var key in $scope.getComplexProtoJson) { alert("Key: " +
				 * key + " value: " + $scope.getComplexProtoJson[key]); }
				 */
                /*
				 * for(i=0; i<= $scope.complexProtoJson.length; i++){
				 * console.log(complexProtoMap[i]); }
				 */
                $scope.messageUI=messageJson;
                $scope.showDataBroker = null;
                $scope.showDataMapper = null;
                if($scope.messageUI){
                    $scope.showProperties=true;
                }else
                    $scope.showProperties=false;
                if($scope.complexProtoJson.size!=0){
                	$scope.complexType =true;
                }else
                	$scope.complexType = false;
                $scope.solutionDetails=null;
                $scope.showLink=null;
                $scope.tabChange = 0;
                $scope.$apply();
            });

        _diagram.child('port-tips', port_tips);

        function nodeTypeOnHover(d){
        	/*
			 * var nodeTypeDisp = d.orig.key; var lastChar =
			 * nodeTypeDisp.slice(-1); nodeTypeDisp =
			 * nodeTypeDisp.split(lastChar); var rest = nodeTypeDisp.slice(0,-1)
			 * console.log('node==========='+ rest); return rest;
			 */
        	 var nodeTypeDispTest = d.orig.value.name;
        	 return nodeTypeDispTest;
        }
      
        var node_tips = dc_graph.tip({namespace: 'node-tips'})
            .delay(200)
            .selection(dc_graph.tip.select_node())
            .content(function(d, k) {
                //console.log(d);
                k(nodeTypeOnHover(d));

            })
            .offset(function(){
                // I don't entirely understand how d3-tip is
                // calculating position
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
                // cid:_cid,
                solutionid:_solutionId,
                nodeid: nodeId,
                nodename: name,
                // version : $scope.solutionVersion,
                ndata: JSON.stringify(ndata)

            });
        } else {
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                cid:_cid,
                nodeid: nodeId,
                nodename: name,
                // version : $scope.solutionVersion,
                ndata: JSON.stringify(ndata)

            });
        }
        $scope.nodeNameUI = name;
        changeNode[nodeId] = name;
        $scope.saveState.noSaves = false;
        _dirty = true;
        //console.log("console"+angular.toJson($http.post(url)));
        return $http.post(url);

    }

    $scope.showInput = false;
    $scope.toggleShowInput = function()
    {
        $scope.showInput = !$scope.showInput;
    };

    function load_catalog() {
        return get_catalog()
            .success(function(data) {
                // console.log(angular.toJson(data));

                angular.forEach(data.items, function(value, key) {
                    if(data.items.solutionName != "Text_Class_09102017_IST2"){

                    }
                    data.items[key].description = $(data.items[key].description).text();
                });
                _catalog = catalog_readers[options.catformat](data);
                
                _components = d3.map(_catalog.models(), _catalog.fModelName);

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
                console.log(models);
                // unique by name
                // models = d3.map(models,
                // _catalog.fModelName).values(

                $scope.palette.categories = d3.nest().key(_catalog.fModelCategory)
                    .sortKeys(d3.ascending)
                    .entries(models);
                $http({
                    method : 'GET',
                    url : '/api/filter/modeltype'
                }).success(function(data, status, headers, config) {
                    $scope.categoryNames = data.response_body;
                    // console.log($scope.categoryNames);
                }).error(function(data, status, headers, config) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error
                    // status.
                });

                $http({
                    method : 'GET',
                    url : '/api/filter/toolkitType'
                }).success(function(data, status, headers, config) {
                    $scope.toolKitTypes = data.response_body;
                    // console.log($scope.toolKitTypes);
                }).error(function(data, status, headers, config) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error
                    // status.
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
                    console.log(status);
                });
                /* $scope.showIcon=[]; */
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
                    /*
                     * $scope.initIndex=!$scope.initIndex;
                     * $scope.selectedIndex=index;
                     */
                };
            }).error(function(response){

                console.log("checking the Json.parse Error");
            });
    }


    function load_initial_solution() {
        var catsol;
        if(options.solutionId)
            catsol = _catalog.models().find(sol => sol.solutionId === options.solutionId);
        else if(options.solutionName)
            catsol = _catalog.models().find(sol => sol.solutionName === options.solutionName);
        if(catsol)
            $scope.loadSolution(catsol);
        /*
         * else $scope.newSolution();
         */
    }
    // Canvas height/width dynamically
    // var widthCanvas = ($('#dsgridbg').width())-20,heightCanvas =
    // ($('#dsgridbg').height())-20;
    // alert(widthCanvas);alert(heightCanvas);
    initialize_canvas();
    load_catalog().success(load_initial_solution);
    $scope.newSolution('old');
    // Fuction for closing composite solution
    $scope.closeComSol = function(){
        reset();

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
                    // alert("Composite Solution is closed");
                	$scope.showDataBroker = false;
                    $scope.showDataMapper = null;
                    $scope.solutionDetails = null;
                    $scope.showProperties = null;
                    $scope.showLink=null;
                    $scope.myCheckbox = false;
                    $scope.checkboxDisable = true;
                    $scope.clearSolution();
                    $scope.namedisabled = false;$scope.closeDisabled = true;
                    $scope.solutionName = '';$scope.solutionVersion = '';$scope.solutionDescription = '';_cid = '';_solutionId = '';
                    //$(".ds-grid-bg").css("background", "none");
                })
                .error(function(response){
                    console.log(response)
                });
        }
    };
    $scope.SaveSolutionFirst = function(val){
        if(val == 'yes' && $scope.CloseOrNew == 'closeSol'){
            $scope.showPrerenderedDialog();
        }
        else if(val == 'no' && $scope.CloseOrNew == 'closeSol'){_dirty = false;$scope.closeComSol();$scope.closePoup();$scope.checkboxDisable = true;}
        else if(val == 'yes' && $scope.CloseOrNew == 'new'){
            $scope.showPrerenderedDialog();
        }
        else if(val == 'no' && $scope.CloseOrNew == 'new'){_dirty = false;$scope.newSolution('new');$scope.closePoup();}
    };

    // Fuction for closing All Open composite solution
    $scope.clearSolution = function(){


        // code
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
                $scope.showDataMapper = null;
                $scope.solutionDetails = null;
                $scope.showProperties = null;
                $scope.showLink=null;

                $('#deleteHide').hide();
                /*
				 * var empty = {"cname": $scope.solutionName,"version":
				 * $scope.solutionVersion,"cid": _cid,"solutionId":
				 * _solutionId,"ctime": '',"mtime": "","nodes": [],"relations":
				 * []}; display_solution(empty);$scope.solutionDetails = false;
				 * _cid = '';_solutionId = '';$scope.solutionName =
				 * '';$scope.namedisabled = false;$scope.solutionVersion = '';
				 */

            })
            .error(function(response){
                $('#deleteHide').hide();
                $scope.cleardis = true;
                console.log(response)
            });
        var empty = {"cname": $scope.solutionName,"version": $scope.solutionVersion,"cid": _cid,"solutionId": _solutionId,"ctime": '',"mtime": "","nodes": [],"relations": []};
        display_solution(empty);$scope.solutionDetails = false;$scope.solutionDescription = '';
    };

    // Enable/Disable close/closeAll button
    /*
     * $scope.$watch(function(closeDisabled) {
     *
     *
     * });
     */
    $scope.down=false;
    $scope.closeDrawer =function(){
        if(($scope.left&&$scope.right&&$scope.down) || (!$scope.left&&!$scope.right&&!$scope.down)){
            $scope.right = !$scope.right;$scope.left=!$scope.left;$scope.down=!$scope.down;
            angular.element('.ds-grid-bg section').width('100%');angular.element('.ds-grid-bg section').height('100%');
            angular.element('svg').width('100%');angular.element('svg').height('100%');
        }
        else {
            $scope.right = false;$scope.left = false;$scope.down=false;
            angular.element('.ds-grid-bg section').width('100%');angular.element('.ds-grid-bg section').height('100%');
            angular.element('svg').width('100%');angular.element('svg').height('100%');
        }};
    $scope.$watch('closeDisabledCheck', function(newValue, oldValue) {
        $scope.closeAllDisabled = true;$scope.closeDisabled = true;
        if(countComSol == 1 && countComSol != 0)$scope.closeDisabled = false;else $scope.closeDisabled = true;
        if(countComSol > 1 && countComSol != 0)$scope.closeAllDisabled = false;else $scope.closeAllDisabled = true;
    });
    $scope.showPrerenderedDialog = function(ev) {
        $scope.solutionDescription ="";
        $mdDialog.show({
            contentElement: '#myDialog',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
   /* $scope.saveSolutionDialog = function(ev){
    	$scope.solutionDescription = "";
    	$mdDialog.show({
    		contentElement: '#myDialog',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
    	});
    };*/
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
    
    $scope.closePoupscript = function(){
    	$mdDialog.hide();
       /* $scope.dbType = '';
        $scope.fileUrl = '';
        $scope.scriptText = '';
        $scope.databroker.$setPristine();
        $scope.databroker.$setUntouched();*/
    }
    $scope.closePoup = function(){

        $mdDialog.hide();
        
    };

    $scope.closeSavePoup = function(){
    	/*$scope.solutionName = "untitled";*/
        $mdDialog.hide();

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
        //updateOldSol = false;
    };
    function solutionPrPB(){
        var tempArr = ['PR','PB','OR'],url = '';
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
                    else $scope.privateCS = data.items;
                    angular.forEach($scope.publicOR, function(value1, key1) {
                        $scope.publicCS.push(value1);
                        console.clear();
                        console.log($scope.publicCS.length);
                    })
                })
                .error(function(response){

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
    			 $scope.msg = "Probe added successfully";
    			 $scope.saveState.noSaves = false;
                $scope.showpopup();
    		 } else{
    			 $scope.msg = "Probe removed";
    			 $scope.saveState.noSaves = false;
    			 _dirty = true;
                 $scope.showpopup();
    		 }
             
         });
    }
    
    $scope.showScript = function(ev) {
    	
        $mdDialog.show({
            contentElement: '#myDialogScript',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
    $scope.showSourceTable = false;
    $scope.defaultvalue = '';

    $scope.processData = function(){
    	/*var x = document.getElementById("fileRead").value;
    	console.log(x);*/
    	console.log($scope.dbType);
    	console.log($scope.firstRow);
    	if($scope.dbType == 'csv'){
	    	var uploaded=$scope.fileContent;
	    	var delimeter;
	    	$scope.errormsg = '';
	    	
	    	//get file separator
	    	if(uploaded.search('\r\n') != -1){
			    var dataShow = uploaded.split('\r\n');
			    delimeter = '|';	
	    	}else if(uploaded.search('\n') != -1){
			   var dataShow = uploaded.split('\n');
			   delimeter = ',' ;
	    	} else {
	    	
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
			  //$scope.dataShow = tabledata;
			  $scope.readSolution = false;
			  $scope.saveScript();
	    	} else {
	    		//$scope.databroker.$invalid = true;
	    		$scope.errormsg ="Common file separator(| or , ) is missing , Please upload correct csv file";
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
    	$scope.readSolution = false;
    	$scope.saveScript();
    	
    }
    
    	
    function parseError(col){
    	if((col.indexOf("|") == -1 && col.indexOf(",") == -1) ){
			return false;
		} else {
			return true;
		}
    	
    }
	 // var col = dataShow[0];
	//  var tabledata = col.split(',');
		//$scope.dataShow = tabledata;
    	//console.log($scope.dataShow);
    	
    	//$scope.closePoup();
    	/*$scope.showProperties = false;
    	$scope.solutionDetails = false;
    	$scope.showSourceTable = true;*/
    }
    
    $scope.saveScript = function(){
    	var scriptInput = $scope.scriptText;
    	/*if(!$scope.scriptText) {
            // alert("Please fill all mandatory fields");
            set_focus('input-name');
            return;
        }*/
    	// Needs to be uncommented and correct values need to be passed
    	var url;
    	$scope.localurl = '';
    	 if($scope.userImage){
    		 $scope.localurl = $scope.userImage.name;
    	 } else {
    		 $scope.localurl = null;
    	 }
    	var data = {
    			"databrokerMap": {
    			    "script": $scope.scriptText,
    			    "csv_file_field_separator": $scope.delimeterchar?$scope.delimeterchar:null, //check
    			    "data_broker_type": $scope.dbType,
    			    "first_row": $scope.firstRow?$scope.firstRow:null,
    			    "local_system_data_file_path":$scope.localurl, //need to complete
    			    "target_system_url": $scope.fileUrl,
    			    "map_action": null,
    			    "map_inputs": null,
    			    "map_outputs": null
    			  },
    			  "fieldMap":null
    		}
        if(_solutionId){
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                // cid:_cid,
                solutionid:_solutionId,
                nodeid: $scope.nodeIdDB,
                nodename: name,
                // version : $scope.solutionVersion,
                // data_broker_script: $scope.scriptText
               // dataConnector:data

            });
        } else {
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                cid:_cid,
                nodeid: $scope.nodeIdDB,
                nodename: name,
                // version : $scope.solutionVersion,
                // data_broker_script: $scope.scriptText
                // dataConnector:data

            });
        }
        
    	$http.post(url,data)
        .success(function(result) {
        	console.log(result);
        	if(result.success === 'true'){
        		/*$scope.msg = "Script added successfully";*/
        		$scope.closeSavePoup();
        		$scope.saveState.noSaves = false;
        		//$scope.processData();
    			/*$scope.showpopup();*/
        	}else{
        		$scope.saveState.noSaves = true;
        	}
        })
        .error(function(response){
        	
        });
        
        
    }
    $scope.selectOutputMessage = function(p,ev) {
    	var dataBrokerOutput = new Map();
    	if($('.node').length > 0){
    	$scope.portDets = p;
    	if(jsonProtoNode.size > 1){
    		angular.forEach(jsonProtoNode, function(value, key){
    			angular.forEach(value.protobuf_json.service.listOfOperations, function(value1, key1){
    				angular.forEach(value1.listOfInputMessages,function(value2,key2){
                        // console.log(value1);
    					if(value2.inputMessageName !== "ANY"){
    						angular.forEach(value.protobuf_json.listOfMessages, function(value3,key3){
    							if(value3.messageName === value2.inputMessageName){
    								dataBrokerOutput[value1.operationName+"("+value2.inputMessageName+")"]=value3;
    							}
    						});
    					}
    						
    						
                    });
    			});
    		});
    	} else if(protoJsonRead.size > 1){
    		angular.forEach(protoJsonRead, function(value, key){
    			angular.forEach(value.protobuf_json.service.listOfOperations, function(value1, key1){
    				angular.forEach(value1.listOfInputMessages,function(value2,key2){
                        // console.log(value1);
    					if(value2.inputMessageName !== "ANY"){
    						angular.forEach(value.protobuf_json.listOfMessages, function(value3,key3){
    							if(value3.messageName === value2.inputMessageName){
    								dataBrokerOutput[value1.operationName+"("+value2.inputMessageName+")"]=value3;
    							}
    						});
    					}
    						
    						
                    });
    			});
    		});
    	}
    	
    	$scope.dataBrokerSelectOutput = dataBrokerOutput;
        $mdDialog.show({
            contentElement: '#myDialogSelectOutput',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    	}
    };
    
    $scope.selectedDBOutput = function(){
    	
    	angular.forEach(_ports, function(value,key){
    		console.log(value.originalType[0]);
    		if(value.shortname+"("+value.originalType[0].messageName+")" === $scope.selectedOutput){
    			$scope.portDets.orig.value.type = value.type;
    			$scope.portDets.orig.value.shortname = value.shortname;
    			$scope.portDets.orig.value.fullType = value.fullType;
    		}
    		console.log($scope.portDets);
    	});
    	/*
		 * if($scope.selectedOutput !== null){ p.orig.value.type =
		 * [dataBrokerOutput[$scope.selectedOutput]]; p.orig.value.fullType =
		 * [dataBrokerOutput[$scope.selectedOutput]]; }
		 */
    	update_ports();
    	targetTableCreate($scope.portDets);
    	dc.redrawAll();
    	$scope.closePoup();
    }
    
    function targetTableCreate(portDetails){    	
    	//console.log($scope.portDets.orig.value.fullType[0].messageargumentList);
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
    	console.log(targetTable);
    	$scope.targetMapTable = targetTable;
    	dc.redrawAll();
    	$scope.closePoup();
    }
    
    $scope.showMappingTable = function(ev) {
        $mdDialog.show({
            contentElement: '#myDialogMapping',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        });
    };
    
/*    var checkFieldMap = new Map();
    $scope.mapCheckField = function(d){
    	checkFieldMap.set(d,this.checkfield?"yes":"no");
    }
    
    var fieldNameMap = new Map();
    $scope.mapFieldName = function(d){
    	console.log(d);
    	console.log(this.checkfield);
    	fieldNameMap.set(d,this.data)// need to check
    }
    
    var fieldTypeMap = new Map();
    $scope.mapFieldType = function(d){
    	fieldTypeMap.set(d,this.fieldType);
    };
    
    var tagMap = new Map();
    $scope.mappingTag = function(d){
    	tagMap.set(d,this.mapTag);
    };*/
    
    var checkFieldMap = new Map();
    $scope.mapCheckField = function(index){
    	checkFieldMap.set(index,this.checkfield?"yes":"no");
    }
    
    var fieldNameMap = new Map();
    $scope.mapFieldName = function(index){
    	fieldNameMap.set(index,this.data)// need to check
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
/*    	if($scope.readSolution === true){
    		for(var i=0;i<$scope.dataShow.length;i++){
    			if(fieldNameMap.get(i) === undefined){
    				fieldNameMap.set(i,$scope.dataShow[i].name);
    			}
    			if(checkFieldMap.get(i) === undefined){
    				checkFieldMap.set(i,$scope.dataShow[i].checked);
    			}
    			if(fieldTypeMap.get(i) === undefined){
    				fieldTypeMap.set(i,$scope.dataShow[i].type);
    			}
    			if(tagMap.get(i) === undefined){
    				tagMap.set(i,$scope.dataShow[i].tag);
    			}
    		}
    		
        		angular.forEach($scope.dataShow, function(valueData,keyData){
    	    		mapInput.push({"input_field": {
    	    			"name": fieldNameMap.get(keyData),
    	    			"type": fieldTypeMap.get(keyData),
    	    			"checked": checkFieldMap.get(keyData) === "yes"?"YES":"NO",
    	    			"mapped_to_field": tagMap.get(keyData)
    	    		}});
    	    	});
        	
    	} else{*/
    	for(var i=0; i < $scope.dataShow.length; i++){
        	if(fieldNameMap.get(i) === null || fieldNameMap.get(i) === undefined){
        		fieldNameMap.set(i,$scope.dataShow[i]);
        	}
        }
    	
    	if($scope.dbType === "csv"){
    		angular.forEach($scope.dataShow, function(valueData,keyData){
	    		mapInput.push({"input_field": {
	    			"name": fieldNameMap.get(keyData),
	    			"type": fieldTypeMap.get(keyData)?fieldTypeMap.get(keyData):"null",
	    			"checked": checkFieldMap.get(keyData) === "yes"?"YES":"NO",
	    			"mapped_to_field": tagMap.get(keyData)?tagMap.get(keyData):"null"
	    		}});
	    	});
    	} else if($scope.dbType === "image"){
    		angular.forEach($scope.dataImage, function(valueData,keyData){
    			mapInput.push({"input_field": {
	    			"name": valueData.Fieldname,
	    			"type": valueData.FieldType,
	    			"checked": checkFieldMap.get(keyData) === "yes"?"YES":"NO",
	    			"mapped_to_field": tagMap.get(keyData)?tagMap.get(keyData):"null"
	    		}});
    		});
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
    	console.log(mapOutput);
    	var url;
    	
    	var data = {
    			"databrokerMap": {
    			    "script": null,
    			    "csv_file_field_separator": null, //check
    			    "data_broker_type": null,
    			    "first_row": null,
    			    "local_system_data_file_path": null, //need to complete
    			    "target_system_url": null,
    			    "map_action": null,
    			    "map_inputs": mapInput,
    			    "map_outputs": mapOutput
    			  },
    			  "fieldMap":null
    		}
        if(_solutionId){
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                // cid:_cid,
                solutionid:_solutionId,
                nodeid: $scope.nodeIdDB,
                nodename: name,
                // version : $scope.solutionVersion,
                // data_broker_script: $scope.scriptText
               // dataConnector:data

            });
        } else {
            url = build_url(options.modifyNode, {
                userid: get_userId(),
                cid:_cid,
                nodeid: $scope.nodeIdDB,
                nodename: name,
                // version : $scope.solutionVersion,
                // data_broker_script: $scope.scriptText
                // dataConnector:data

            });
        }
        
    	$http.post(url,data)
        .success(function(result) {
        	console.log(result);
        	if(result.success === 'true'){        		
        		$scope.saveState.noSaves = false;
        		$scope.closePoup();
        	}else{
        		$scope.saveState.noSaves = true;
        	}
        })
        .error(function(response){
        	
        });
        
    }
    //$scope.showFileOption = false;
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
}
