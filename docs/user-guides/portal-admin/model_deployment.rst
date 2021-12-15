.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
.. http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

.. _model-deployment:

============================
Model Deployment Automation
============================

This tab “model deployment automation” must be used to trigger the launch of a Jenkins Job on an external Jenkins server, please refers to the “model deployment project : https://wiki.acumos.org/display/MM/Model+Deployment+project ”  wiki page to set up properly all the following parameters:

-  Location of the jenkins server : Ip adress and port in the form : "http://X.X.X.X:Y"

-  Name tof the jenkins jobs : The Name of your jenkins jobs

-  Name of the parameter : Name of the parameter you used to trigered your Jenkins Jobs remotly

-  Value of the parameter : In the very mast version of Elpis (end of 2021) this parameter has been removed. Acumos is able to retrieve this parameter (model docker URI) and send it to Jenkins

-  Jenkins login : your Jenkins login

-  Jenkins security token : your jenkins token

				.. image:: images/model_deployment.jpg
	               			  :width: 75%
