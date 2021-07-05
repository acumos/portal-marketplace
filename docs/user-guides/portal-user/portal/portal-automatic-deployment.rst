.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

======================
Automatic Deployment
======================

The goal of this feature is to deploy a model automatically  in a target environment.
Automation of model deployment is based on an external Jenkins server fully managed by the Acumos users. 
Some specifics Jenkins jobs must be created by the Acumos user and then, based on some parameters 
that must be fullfiled by Acumos users (ip adress of the jenkins server, jenkins login and security token, etc ...), 
Acumos will trigger these Jenkins jobs. 
Deployment of the model is performed by the Jenkins Jobs, not by Acumos.

