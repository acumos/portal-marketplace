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
DSController.$inject = ['$scope','$http','$filter','$q','$window'];

function DSController($scope,$http,$filter,$q,$window) {
	var pathArray = location.href.split( '/' );
	var protocol = pathArray[0];
	var host = pathArray[2];
	var baseURL = protocol + '//' + host;
	var jsonFormate = {};var requirementJson = [];var capabilityJson = [];var nodeIdCnt = 1, countComSol = 0;
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
                    //alert('handleDragOver');
                    e.preventDefault(); // Necessary. Allows us to drop.
                    e.dataTransfer.dropEffect = 'move';  // See the section on the DataTransfer object.
                    return false;
                };
                $scope.handleDrop = function(e){
                    e.preventDefault();
                    var bound = _diagram.root().node().getBoundingClientRect();
                    var pos = _diagram.invertCoord([e.clientX - bound.left,
                                                    e.clientY - bound.top]);
                    var type = e.dataTransfer.getData('text/plain');
                    var max = 0;
                    var remVrsn = type.replace(/[^0-9\.]+/g, "");
                    remVrsn = '('+remVrsn +')';
                    type = type.replace(remVrsn,'');
                    _drawGraphs.nodeCrossfilter().all().forEach(function(n) {
                        var number = n.id.match(/[0-9]+$/);
                        if(!number)
                            return; // currently all ids will be type + number
                        number = number[0];
                        var type2 = n.id.slice(0, -number.length);
                        if(type2 === type && +number > max)
                            max = +number;
                    });
                    var data = {
                        id: type + (max+1),	//needs to be checked
                        type: type
                    };
                    data.name = data.id;
                    /*console.log(_components.get(type));*/
                    $http.get(_catalog.fModelUrl(_components.get(type))).success(function(tgif) {
                    	
                    	console.log("tgif : ");console.log(angular.toJson(tgif));
                        $scope.solutionDetails=_components.get(type);
                        $scope.packageName= JSON.stringify(tgif.self.name);
                        $scope.requireCalls= tgif.services.calls;
                        $scope.capabilityProvides=tgif.services.provides;
                        jsonFormate = tgif;var reqObj = '';
                        requirementJson=[],capabilityJson=[];
                        //get requirements 
                        angular.forEach(jsonFormate.services.calls, function(value, key) {
                              reqObj = value.request.format+'+'+value.request.version+'+'+value.response.format+'+'+value.response.version;
                              requirementJson.push({
                                    "name": "",
                                    "relationship": "",
                                    "id": "",
                                    "capability": {
                                          "name":reqObj,
                                          "id": ""
                                    },
                                    "target": {"name": "","description": ""},
                                    "target_type": "Node"}
                                          )
                              });
                      //get capabilities
                        var capObj = '';
                        angular.forEach(jsonFormate.services.provides, function(value, key) {
                              capObj = value.request.format+'+'+value.request.version+'+'+value.response.format+'+'+value.response.version;
                              capabilityJson.push({
                                    "id": "",
                                    "name":"",
                                    "target": {
                                          "name":capObj,
                                        "id": ""
                                    },
                                    "target_type": "Capability",
                                    "properties":null
                              })
                        });
                        console.log(capObj);
                          var def = {
                            "id": tgif.self.name,
                            "name": tgif.self.name,
                            "ndata": {},
                            "capabilities": capabilityJson,
                            "requirements": requirementJson
                        };
                        //console.log(def);
                      /*$scope.packageName= JSON.stringify(def.name);
                        $scope.requireCalls= def.requirements;
                        $scope.capabilityProvides=def.capabilities;
                        console.log($scope.capabilityProvides);*/
                        /*
                        var url = build_url(options.addNode, {
                            userId: "1510e6c1-7879-4507-8e9d-73ea68dc2169",
                            cid: _cid,
                            name: data.id,
                            nodeId: "1234",
                            //type: JSON.stringify({type: type}),
                            //properties: JSON.stringify([]),
                            requirements: JSON.stringify(def.requirements),
                            capabilities: JSON.stringify(def.capabilities),
                            ndata: '{"ntype": "","px": 385.89287722216187,"py": 380.5962040115248,"radius": 10,"fixed": true}'
                        });
                        */

                       /* var url = build_url(options.addNode, {
                            userId: "1510e6c1-7879-4507-8e9d-73ea68dc2169",
                            cid: _cid,
                            name: data.id,
                            nodeId: "1234",
                            //type: JSON.stringify({type: type}),
                            //properties: JSON.stringify([]),
                            //requirements: '[{"name": "","relationship": "","id": "","capability": {"name":"calls.request.format+calls.request.version+calls.response.format+calls.response.version","id": ""},"target": {"name": "name-of-target-node-of-this-requirement-if-it-is-connected","description": ""},"target_type": "Node"},{"name": "","relationship": "","id": "","capability": {"name":"calls.request.format+calls.request.version+calls.response.format+calls.response.version","id": ""},"target": {"name": "name-of-target-node-of-this-requirement-if-it-is-connected","description": ""},"target_type": "Node"}]',
                          
                            capabilities: '[{"id": "","name": "","target": {"name": "name-of-node-capability","id": ""},"target_type": "Capability","properties":null},{"id": "","name": "","target": {"name": "name-of-node-capability","id": ""},"target_type": "Capability","properties": null}]',
                            ndata: '{"ntype": "","px": 385.89287722216187,"py": 380.5962040115248,"radius": 10,"fixed": true}'
                        });*/
                        var url = "";
                        var ndata = {
                            fixed: true,
                            px: pos[0],
                            py: pos[1]
                        };
                        if(_solutionId){                            
                        url = build_url(options.addNode, {
                            userId: get_userId(),
                            solutionId :  _solutionId,
                            version : $scope.solutionVersion,
                            name: data.name,
                            nodeId: data.id,
                            // nodeIdCnt.toString(),
                            
                            // properties: JSON.stringify([]),
                            requirements: JSON.stringify(requirementJson),
                            type : type,
                            capabilities: JSON.stringify(capabilityJson),
                            ndata: JSON.stringify(ndata)
                        });
                        }
                        else if(_cid){
                        	
                        url = build_url(options.addNode, {
                            userId: get_userId(),
                            cid: _cid,
                            name: data.name,
                            nodeId: data.id,
                            //nodeIdCnt.toString(),
                            //type: JSON.stringify({type: type}),
                            //properties: JSON.stringify([]),
                            // nodeIdCnt.toString(),
                            
                            // properties: JSON.stringify([]),
                            requirements: JSON.stringify(requirementJson),
                            type : type,
                            capabilities: JSON.stringify(capabilityJson),
                            ndata: JSON.stringify(ndata)
                        });
                      }
                        console.log(url);
                        //Json formating start
                        //Json formating end
                        $http.post(url)
                            .success(function(response) {
                              nodeIdCnt++;
                              console.log(def.requirements);
                              console.log(def.capabilities);
                             // console.log(_catalog.ports(data.id, def.requirements, def.capabilities));
                               _ports = _ports.concat(_catalog.ports(data.id, def.requirements, def.capabilities));
                               console.log(_ports);
                                update_ports();
                                _drawGraphs.createNode(pos, data);
                                set_dirty(true);
                            }).error(function(response){
                            	console.log("error in creating node");
                            });
                    });
                };
                $scope.newSolution = function() {debugger;
                    maybe_save_solution().then(function(cat2) {
                        function new_solution(result) {
                           _cid = result.cid;
                            _solution = {nodes: [], edges: []};
                            $scope.solutionName = null;
                            $scope.solutionDescription = null;
                            $scope.solutionVersion = null;
                            $scope.saveState.noDeletes = true;
                            display_solution(_solution);
                            load_catalog();
                            _solutionId = '';
                        }
                        var userId = get_userId(),
                            url = build_url(options.create, {userId: userId});
                        alert("Create a new Composite Solution");
                        $scope.closeDisabledCheck = !$scope.closeDisabledCheck;		
                        countComSol += 1;
                        $scope.namedisabled = false;
                        $http.post(url)
                            .success(new_solution);
                    });
                };
                $scope.loadSolution = function(entry) {
                    if(entry.toolKit === 'CP' || entry.toolKit === 'DS') {
                       /* var url = build_url(options.read, {
                              userId:get_userId(),
                            solutionId: entry.solutionId,
                            version: entry.version
                        });*/
                        var url = build_url(options.read, {
                              userId:get_userId(),
                            solutionId: entry.solutionId,
                            version: entry.version
                        });
                        console.log(url);
                        $http.get(url)
                            .success(function(result) {
                            	$scope.namedisabled = true;
                            	_solutionId = entry.solutionId;
                                $scope.solutionName = result.cname;
                                $scope.solutionVersion = result.version;
                                _solution = result;
                                _solution.nodes.forEach(function(n) {
                                    if(n.ndata && n.ndata.fixed)
                                        n.fixedPos = {x: n.ndata.px, y: n.ndata.py};
                                });
                                console.log(_solution);
                                $scope.closeDisabled = false;
                                display_solution(_solution);
                            });
                    }
                };
                $scope.saveSolution = function() {
                    if(!_dirty)
                        return;
                    if(!$scope.solutionName) {
                    	alert("Please fill all mandatory fields");
                        set_focus('input-name');
                        return;
                    }
                    if(!$scope.solutionDescription) {
                    	alert("Please fill all mandatory fields");
                        set_focus('input-description');
                        return;
                    }
                    if(!$scope.solutionVersion) {
                    	alert("Please fill all mandatory fields");
                        set_focus('input-version');
                        return;
                    }
                    save_solution($scope.solutionName)
                        .then(function() {
                        	$scope.namedisabled = true;
                            $scope.saveState.noDeletes = false;
                            load_catalog();
                        });
                };
                $scope.deleteSolution = function() {
                	var url ='';
                    url = build_url(options.deleteCompositeSolution, {
                    	userid:get_userId(),
                        solutionid : _solutionId,
                        version : $scope.solutionVersion
                  });
                   if($scope.solutionName && confirm('Do you want to delete solution "' + $scope.solutionName + '"?')){
                	  
                	  $http.post(url)
                      .success(function(result) {
                    	  if(result.success == "true"){
                    		  load_catalog().success(load_initial_solution);
                    		  alert("Solution is deleted successfully");
                          	var empty = {"cname": $scope.solutionName,"version": $scope.solutionVersion,"cid": _cid,"solutionId": _solutionId,"ctime": '',"mtime": "","nodes": [],"relations": []};
                          	display_solution(empty);$scope.solutionDetails = false;$scope.solutionDescription = '';
                          	_cid = '';_solutionId = '';$scope.solutionName = '';$scope.namedisabled = false;$scope.solutionVersion = '';
                          	//code
                          	
                    	  }
                    	  else if(result.success == "false"){
                    		  alert("Solution is not deleted");
                    	  }
                      });
                   }
                    /*if($scope.solutionName && confirm('Really delete solution "' + $scope.solutionName + '"?'))
                        delete_solution($scope.solutionName).then(function(cat2) {
                            load_catalog();
                        });*/
                    
                };


                var qs = querystring.parse();
                var urlBase = baseURL + '/dsce/';
                var options = Object.assign({
                	base:"dsce/dsce/",
                    //base: urlBase,
                    catalog: 'solution/getSolutions',
                    typeinfo: 'artifact/fetchJsonTOSCA',
                    create: 'solution/createNewCompositeSolution',
                    addNode: 'solution/addNode',
                    addLink: 'solution/addLink',
                    save: 'solution/saveCompositeSolution',
                    read: 'solution/readCompositeSolutionGraph',
                    catformat: 'acumos',
                    solution: '',
                    deleteNode : 'solution/deleteNode',
                    deleteLink : 'solution/deleteLink',
                    closeCompositeSolution  : 'solution/closeCompositeSolution ',  
                    modifyNode : 'solution/modifyNode',
                    modifyLink : 'solution/modifyLink',
                    deleteCompositeSolution : 'solution/deleteCompositeSolution' ,
                    clearCompositeSolution : 'solution/clearCompositeSolution ' 
                }, qs);

                function build_url(verb, params) {
                    return options.base + verb + '?' + Object.keys(params).map(function(k) {
                        return k + '=' + encodeURIComponent(params[k]);
                    }).join('&');
                }
                function tgif_reqcap_to_tosca(rc) {
                	console.log(rc);
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

                var _toolkits = [{"toolkitCode":"CP","toolkitName":"Composite Solution"},{"toolkitCode":"H2","toolkitName":"H2O"},{"toolkitCode":"RC","toolkitName":"RCloud"},{"toolkitCode":"SK","toolkitName":"Scikit-Learn"},{"toolkitCode":"TF","toolkitName":"TensorFlow"}];
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
                        fModelCategory: function(model) {
                            return model.category === 'null' ? "others" : model.category;
                            //return model.toolKit === 'null' ? null : toolKitName(model.toolKit);
                        },
                        fModelToolKit: function(model) {
                              return model.toolKit === 'null' ? null : model.toolKit;
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
                        ports: function(nid, requirements, capabilities) {
                              return requirements.map(req => ({
                                nodeId: nid,
                                portname: req.capability.name,
                                shortname: req.capability.name.split('+')[0].split('.').pop(),
                                bounds: rbounds
                            })).concat(
                                capabilities.map(cap => ({
                                    nodeId: nid,
                                    portname: cap.target.name,//
                                    shortname: cap.target.name.split('+')[0].split('.').pop(),
                                    bounds: lbounds
                                })));
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
                var _catalog, _components, _palette;

                function set_dirty(whether, message) {
                    _dirty = whether;
                    if(whether) {
                        $scope.saveState.noSaves = false;
                        $scope.saveState.descStyle.opacity = 1;
                        $scope.saveState.desc = message || 'solution has changes';
                    } else {
                        $scope.saveState.noSaves = false;
                        $scope.saveState.descStyle.opacity = 0.5;
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

                var lbounds = [Math.PI*5/6, -Math.PI*5/6], rbounds = [-Math.PI/6, Math.PI/6];
                function update_ports() {
                    var port_flat = dc_graph.flat_group.make(_ports, d => d.nodeId + '/' + d.portname);
                    console.log(port_flat);
                    _diagram
                        .portDimension(port_flat.dimension).portGroup(port_flat.group);
                }
                
                function display_solution(solution) {
                	console.log(angular.toJson(solution.nodes));
                  var nodes = solution.nodes || (console.warn('no nodes in composite solution!'), []),
                                          edges = solution.relations || [];
                    _ports = [];
                    nodes.forEach(function(n) {
                          _ports = _ports.concat(_catalog.ports(n.id, n.requirements, n.capabilities));
                    });
                    var node_flat = dc_graph.flat_group.make(nodes, function(d) { return d.id; }),
                        edge_flat = dc_graph.flat_group.make(edges, function(d) { return d.linkId; });
                    console.log(node_flat);
                    console.log(edge_flat);
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
                }
                
                /*function display_solution1(solution) {
                  console.log(solution);
                  var nodes = solution.nodes || (console.warn('no nodes in composite solution!'), []),
                        edges = solution.relations || [];
                    _readPorts = [];
                    _ports = [];
                    console.log(nodes);
                    console.log(edges);
                    nodes.forEach(function(n) {
                    	n.requirements=n.requirements.
                    	_ports = _ports.concat(_catalog.ports(n.id, n.requirements, n.capabilities));
                    	
                    });
                    var node_flat = dc_graph.flat_group.make(nodes, function(d) { console.log(d.name); return d.name; }),
                    	edge_flat = dc_graph.flat_group.make(edges, function(d) { return d.sourcename + '->' + d.targetname; });
                    console.log(node_flat);
                    console.log(edge_flat);
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
                }*/

                //
                // SAVE AREA
                // & loading composite solutions
                //
                var updateOldSol = false;
                function save_solution(name) {
                	console.log(name);
                  _solution.nodes = _drawGraphs.nodeCrossfilter().all();
                    _solution.edges = _drawGraphs.edgeCrossfilter().all();
                   var userId = get_userId();
                  
                    if(!userId)
                        throw new Error('not logged in!');
                    if(!_cid && !_solutionId)
                        throw new Error('trying to save but not initialized');
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
                    	if(result.errorCode){alert("Solution not saved");}
                    	else if(result.alert){
                    		set_dirty(false, 'saved at ' + d3.time.format('%X')(new Date()));
                    		if(confirm(result.alert +'?')){
                    			updateOldSol = true;
                    			save_solution(name);
                    			updateOldSol = false;
                    		}
                    	}
                    	else {
                    		alert("Solution saved succesfully");
                    		set_dirty(false, 'saved at ' + d3.time.format('%X')(new Date()));
                    		_solutionId = result.solutionId;
                    	}
                        //alert(JSON.stringify(result))
                        
                        
                    });
                }
                function maybe_save_solution() {
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
                    return save_solution(_catalog, name);	//this needs to be checked
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
                  
                  /*$scope.showIcon=[];*/
                  $scope.initIndex=false;
                  $scope.setAction = function(index){
                        if(index==$scope.selectedIndex){
                              $scope.selectedIndex=-1;
                        } else{
                              $scope.selectedIndex=index;
                              $scope.initIndex=true;
                              
                        }

                        /*$scope.initIndex=!$scope.initIndex;
                        $scope.selectedIndex=index;*/

                  }
                  
                  $scope.listNavs=[];
                  $scope.navsKeys=[];
                    var content = d3.select('#properties-content');
                    if(url) {
                        $http.get(url).success(function(comp) {
                              var table = d3.select('#properties-table');
                              $scope.packageName= JSON.stringify(comp.self.name);
                              $scope.capabilityProvides=comp.services.provides;
                              $scope.requireCalls=comp.services.calls;
                           /* var name = _catalog.fTypeName(comp);
                            delete comp.name;*/
                            /*content.style('visibility', 'visible');
                            d3.select('#selected-name')
                                .text(name);*/
                           /* var table = d3.select('#properties-table');
                            var keys = Object.keys(comp).sort();
                            var rows = table.selectAll('tr.property').data(keys);
                            rows.enter().append('tr').attr('class', 'property');
                            var cols = rows.selectAll('td').data(function(x) { return [x, print_value(comp[x])]; });
                            cols.enter().append('td');
                            cols.text(function(x) { return x; });*/
                              var keys = Object.keys(comp).sort();
                            $scope.listNavs=comp;
                            $scope.navsKeys=keys;
                        });
                    }
                    /*else {
                        content.style('visibility', 'hidden');
                    }*/
                }
                var _ionicons = {
                    AlarmGenerator: 'app/design-studio/img/ios-bell.svg',
                    Classifier: 'app/design-studio/img/stats-bars.svg',
                    Aggregator: 'app/design-studio/img/network.svg',
                    Predictor: 'app/design-studio/img/android-bulb.svg',
                    Recommender: 'app/design-studio/img/thumbsup.svg'
                };

                var layout = dc_graph.cola_layout()
                        .rankdir('LR')
                        .flowLayout({axis: 'x', minSeparation: function(e) {
                            return (e.source.width + e.target.width) / 2 + layout.ranksep();
                        }});

                function initialize_canvas() {
                    _diagram = dc_graph.diagram('#canvas');
                    _diagram
                        .width($('#canvas').width())
                        .height(1000) // oh how we love vertical space
                        .layoutEngine(layout)
                        .timeLimit(500)
                        .margins({left: 5, top: 5, right: 5, bottom: 5})
                        .transitionDuration(1000)
                        .fitStrategy('align_tl')
                        .restrictPan(true)
                        .stageTransitions('insmod')
                        .edgeSource(function(e) { return e.value.sourceNodeId; })
                        .edgeTarget(function(e) { return e.value.targetNodeId; })
                        .edgeArrowhead(null)
                        .edgeLabel(e => e.value.linkName || '')
                        .nodeLabel(function(n) { return n.value.name; })
                        .nodeLabelPadding({x: 5, y: 0})
                        .nodeTitle(null)
                        .nodeStrokeWidth(1)
                        .nodeStroke('#777')
                        .edgeStroke('#777')
                        .nodeShape({shape: 'rounded-rect'})
                        .nodeContent('text-with-icon')
                        .nodeIcon(function(d) {
                            return _ionicons[d.value.type];
                        })
                        .nodeFixed(function(n) { return n.value.fixedPos; })
                        .edgeStroke('#777')
                        .portNodeKey(p => p.value.nodeId)
                        .portName(p => p.value.portname)
                        .portBounds(p => p.value.bounds)
                        .edgeSourcePortName(e => e.value.sourceNodeRequirement)
                        .edgeTargetPortName(e => e.value.targetNodeCapability);
                    
                    _diagram.content('text-with-icon', dc_graph.with_icon_contents(dc_graph.text_contents(), 35, 35));

                    var symbolPorts = dc_graph.symbol_port_style()
                            .colorScale(d3.scale.ordinal().range(
                                // colorbrewer qualitative scale
                                d3.shuffle(
                                    ['#e41a1c','#377eb8','#4daf4a','#984ea3','#ff7f00','#eebb22','#a65628','#f781bf'] // 8-class set1
                                    //['#1b9e77','#d95f02','#7570b3','#e7298a','#66a61e','#e6ab02','#a6761d','#666666'] // 8-class dark2
                                )))
                            .portText(p => p.value.shortname);
                    _diagram
                        .portStyle('symbols', symbolPorts)
                        .portStyleName('symbols');

                    var portMatcher = dc_graph.match_ports(_diagram, symbolPorts);

                    // this isn't great - need semantic "side"
                    var oldIsValid = portMatcher.isValid();
                    portMatcher.isValid((source, target) => oldIsValid(source, target) &&
                                        source.orig.value.bounds !== target.orig.value.bounds);

                    _drawGraphs = dc_graph.draw_graphs({
                        idTag: 'id',
                        edgeIdTag: 'linkId',
                        labelTag: 'name',
                        sourceTag: 'sourceNodeId',
                        targetTag: 'targetNodeId'
                    })
                        .clickCreatesNodes(false)
                        .usePorts(symbolPorts)
                        .conduct(portMatcher)
                        .addEdge(function(e, sport, tport) {
                        	// reverse edge if it's going from requirement to capability
                            // again, not good, the bounds object comparison
                            // and also the modification of e
                              if(sport.orig.value.bounds === lbounds) {
                                console.assert(tport.orig.value.bounds === rbounds);
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
                            var url =  '';
                            if(_solutionId){                            
                                url = build_url(options.addLink, {
                                      userId:get_userId(),

                                      	version : $scope.solutionVersion,
                                        solutionId: _solutionId,
                                        linkName: e.linkName,
                                        linkId: e.linkId,
                                        sourceNodeName: e.sourceNodeId,
                                        sourceNodeId: e.sourceNodeId,
                                        targetNodeName: e.targetNodeId,
                                        targetNodeId: e.targetNodeId,
                                        sourceNodeRequirement: e.sourceNodeRequirement,
                                        targetNodeCapabilityName: e.targetNodeCapability,
                                        relationship: JSON.stringify([])
                                    });
                                }
                                else if(_cid){
                                	
                                url = build_url(options.addLink, {
                                      userId:get_userId(),
                                    
                                        cid: _cid,
                                        linkName: e.linkName,
                                        linkId: e.linkId,
                                        sourceNodeName: e.sourceNodeId,
                                        sourceNodeId: e.sourceNodeId,
                                        targetNodeName: e.targetNodeId,
                                        targetNodeId: e.targetNodeId,
                                        sourceNodeRequirement: e.sourceNodeRequirement,
                                        targetNodeCapabilityName: e.targetNodeCapability,
                                        relationship: JSON.stringify([])
                                    });
                              }
                            /*debugger;
                            var url = build_url(options.addLink, {
                              userId:get_userId(),
                            
                                cid: _cid,
                                linkName: e.linkId,
                                linkId: e.linkId,
                                sourceNodeName: e.sourceNodeId,
                                sourceNodeId: e.sourceNodeId,
                                targetNodeName: e.targetNodeId,
                                targetNodeId: e.targetNodeId,
                                sourceNodeRequirement: e.sourceNodeRequirement,
                                targetNodeCapabilityName: e.targetNodeCapability,
                                relationship: JSON.stringify([])
                            });
                            debugger;*/
                            return $http.post(url)
                                .then(function(response) {
                                    set_dirty(true);
                                    return e;
                                });
                        });

                    _diagram.child('draw-graphs', _drawGraphs);

                    var select_nodes = dc_graph.select_nodes({
                        nodeStroke: '#4a2',
                        nodeStrokeWidth: 3,
                        nodeLabelFill: '#141'
                    }).multipleSelect(false);
                    _diagram.child('select-nodes', select_nodes);

                    var move_nodes = dc_graph.move_nodes().fixNode(function(nodeId, pos) {
                        var node = _diagram.getNode(nodeId);
                        return modify_node(nodeId, node.value.name, pos)
                            .then(function(response) {
                                console.log(response);
                                return pos;
                            });
                    });
                    _diagram.child('move-nodes', move_nodes);

                    var label_nodes = dc_graph.label_nodes({
                        labelTag: 'name',
                        align: 'left'
                    }).changeNodeLabel(function(nodeId, text) {
                        var node = _diagram.getNode(nodeId);
                        console.log('modify node', node);
                        $scope.saveState.noSaves = false;
                        _dirty = true;
                        console.log(nodeId);
                        console.log(text);
                        return modify_node(nodeId, text, node.value.fixedPos)
                            .then(function(response) {
                                console.log(response);
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
                    console.log(edgeId);
                    console.log(text);
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
                        console.log(response);
                        return text;
                    });
                    });
                    _diagram.child('label-edges', label_edges);

                    var select_nodes_group = dc_graph.select_things_group('select-nodes-group', 'select-nodes');
                    var select_edges_group = dc_graph.select_things_group('select-edges-group', 'select-edges');
                    select_nodes_group.on('set_changed.show-info', function(nodes) {
                        setTimeout(function() {
                        if(nodes.length>1)
                            throw new Error('not expecting multiple select');
                        else if(nodes.length === 1) {
                            select_edges_group.set_changed([]); // selecting node clears selected edge
                            var type = _diagram.getNode(nodes[0]).value.type;
                            var comps = _catalog.models().filter(function(comp) {
                                return _catalog.fModelName(comp) === type;
                            });
                            $scope.solutionDetails=comps[0];
                            if(comps.length === 1)
                                display_properties(_catalog.fModelUrl(comps[0]));
                        } else display_properties(null);
                        //_palette.select(null);
                        }, 0);
                    });
                    select_edges_group.on('set_changed.show-info', function(edges) {
                        if(edges.length === 1) {
                            select_nodes_group.set_changed([]); // selecting edge clears selected node
                            // getEdge should give you enough info to look up properties you want for edges
                        } else display_properties(null);
                    });

                    var delete_nodes = dc_graph.delete_nodes()
                            .crossfilterAccessor(function(chart) {
                                return _drawGraphs.nodeCrossfilter();
                            })
                            .dimensionAccessor(function(chart) {
                                return _diagram.nodeDimension();
                            })
                            .onDelete(function(nodes) {
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
                                        // after the back-end has accepted the deletion, we can remove unneeded ports
                                        _ports = _ports.filter(p => p.nodeId !== nodes[0]);
                                        update_ports();
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
                                        linkId: edges[0],
                                    });
                            	}
                            	return $http.post(url)
                                    .then(function(response) {
                                        // after the back-end has accepted the deletion, we can remove unneeded ports
                                        _ports = _ports.filter(p => p.edges !== edges[0]);
                                        update_ports();
                                        return edges;
                                    });
                            });
                    _diagram.child('delete-edges', delete_edges);
                }

                function modify_node(nodeId, name, pos) {
                    var ndata = {};
                    if(pos) {
                        ndata.fixed = true;
                        ndata.px = pos.x;
                        ndata.py = pos.y;
                    }
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
                    return $http.post(url);
                }

                function load_catalog() {
                    return get_catalog()
                        .success(function(data) {
                        	angular.forEach(data.items, function(value, key) {
                        		data.items[key].description = $(data.items[key].description).text();
                        		});
                            _catalog = catalog_readers[options.catformat](data);
                            _components = d3.map(_catalog.models(), _catalog.fModelName);

                            // PALETTE
                            // throw out any models which don't have a category, to avoid "null drawer"
                            // also throw out composite solutions, for now - we're unable to load them
                            // (maybe we could display them but it would be a little misleading)
                            // also throw out iris, because, well, it's iris
                            var models = _catalog.models().filter(function(model) {
                                return _catalog.fModelCategory(model) &&
                                    _catalog.fModelName(model) !== 'iris';
                            });
                            // unique by name
                            //models = d3.map(models, _catalog.fModelName).values();
                            console.log(models);
                            $scope.palette.categories = d3.nest().key(_catalog.fModelCategory)
                                .sortKeys(d3.ascending)
                                .entries(models);
                             $http({
                                    method : 'GET',
                                    url : '/api/filter/modeltype',
                              }).success(function(data, status, headers, config) {
                                    $scope.categoryNames = data.response_body;
                              }).error(function(data, status, headers, config) {
                                    // called asynchronously if an error occurs
                                    // or server returns response with an error
                                    // status.
                              });
                            
                            $http({
                                    method : 'GET',
                                    url : '/api/filter/toolkitType',
                              }).success(function(data, status, headers, config) {
                                    $scope.toolKitTypes = data.response_body;
                              }).error(function(data, status, headers, config) {
                                    // called asynchronously if an error occurs
                                    // or server returns response with an error
                                    // status.
                              });
                            
                            $http({
                                    method : 'GET',
                                    url : '/api/filter/accesstype',
                              }).success(function(data, status, headers, config) {
                                    $scope.accessTypes = data.response_body;
                              }).error(function(data, status, headers, config) {
                                    // called asynchronously if an error occurs
                                    // or server returns response with an error
                                    // status.
                                    console.log(status);
                              });
                              /*$scope.showIcon=[];*/
                                    $scope.initIndexLeft=false;
                                    $scope.setActionLeft = function(index){
                                          if(index==$scope.selectedIndexLeft){
                                                $scope.selectedIndexLeft=-1;
                                          } else{
                                                $scope.selectedIndexLeft=index;
                                                $scope.initIndexLeft=true;
                              
                                          }
                                          /*$scope.initIndex=!$scope.initIndex;
                                          $scope.selectedIndex=index;*/
                                    }
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
                    /*else
                        $scope.newSolution();*/
                }
                initialize_canvas();
                load_catalog().success(load_initial_solution);
                
              //Fuction for closing composite solution
                $scope.closeComSol = function(){
		             if(_dirty){
		                	if(($scope.solutionName && confirm('Do you really want to close unsave solution "' + $scope.solutionName + '"?')) || (_cid && confirm('Do you really want to close unsave solution ?'))){
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
		                  console.log(url);
		                  $http.post(url)
		                  .success(function(response) {
		                	  	alert("Composite Solution is closed");
		                	  	$scope.namedisabled = false;$scope.closeDisabled = true;
		                  		$scope.solutionName = '';$scope.solutionVersion = '';$scope.solutionDescription = '';
		                  		$window.location.reload();
		                  		/*var empty = {"cname": $scope.solutionName,"version": $scope.solutionVersion,"cid": _cid,"solutionId": _solutionId,"ctime": '',"mtime": "","nodes": [],"relations": []};
		                      	display_solution(empty);$scope.solutionDetails = false;
		                      	_cid = '';_solutionId = '';$scope.solutionName = '';$scope.namedisabled = false;$scope.solutionVersion = '';*/
		                      	
		                  })
		                  .error(function(response){
		                	  console.log(response)
		                	  });
		                
		                	}
                }
		             else {
		            	 if(($scope.solutionName && confirm('Do you really want to close solution "' + $scope.solutionName + '"?')) || (_cid && confirm('Do you really wnat to close solution ?'))){
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
			                  console.log(url);
			                  $http.post(url)
			                  .success(function(response) {
			                	  	alert("Composite Solution is closed");
			                	  	$scope.namedisabled = false;$scope.closeDisabled = true;
			                  		$scope.solutionName = '';$scope.solutionVersion = '';$scope.solutionDescription = '';
			                  		$window.location.reload();
			                  		/*var empty = {"cname": $scope.solutionName,"version": $scope.solutionVersion,"cid": _cid,"solutionId": _solutionId,"ctime": '',"mtime": "","nodes": [],"relations": []};
			                      	display_solution(empty);$scope.solutionDetails = false;
			                      	_cid = '';_solutionId = '';$scope.solutionName = '';$scope.namedisabled = false;$scope.solutionVersion = '';*/
			                      	
			                  })
			                  .error(function(response){
			                	  console.log(response)
			                	  });
			                
			                	}
		             }
              }

                //Fuction for closing All Open composite solution
                $scope.clearSolution = function(){
                	debugger;
                	
                	//code
	                	var url = "";
	                    if(_solutionId){                            
		                    url = build_url(options.clearCompositeSolution , {
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
	                  console.log(url);
	                  $http.post(url)
	                  .success(function(response) {
	                	  debugger;
	                  		/*var empty = {"cname": $scope.solutionName,"version": $scope.solutionVersion,"cid": _cid,"solutionId": _solutionId,"ctime": '',"mtime": "","nodes": [],"relations": []};
	                      	display_solution(empty);$scope.solutionDetails = false;
	                      	_cid = '';_solutionId = '';$scope.solutionName = '';$scope.namedisabled = false;$scope.solutionVersion = '';*/
	                      	
	                  })
	                  .error(function(response){debugger;
	                	  console.log(response)
	                	  });
	                  var empty = {"cname": $scope.solutionName,"version": $scope.solutionVersion,"cid": _cid,"solutionId": _solutionId,"ctime": '',"mtime": "","nodes": [],"relations": []};
	                	display_solution(empty);$scope.solutionDetails = false;$scope.solutionDescription = '';
                	}
                
                // Enable/Disable close/closeAll button
                /*$scope.$watch(function(closeDisabled) {
                	debugger;
                });*/
                $scope.$watch('closeDisabledCheck', function(newValue, oldValue) {
                	  $scope.closeAllDisabled = true;$scope.closeDisabled = true;
                	  if(countComSol == 1 && countComSol != 0)$scope.closeDisabled = false;else $scope.closeDisabled = true;
                	  if(countComSol > 1 && countComSol != 0)$scope.closeAllDisabled = false;else $scope.closeAllDisabled = true;
                	});
          }
//    });