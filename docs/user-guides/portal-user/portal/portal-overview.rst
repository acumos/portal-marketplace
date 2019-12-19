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

========
Overview
========

The Acumos Portal is designed to enable Modelers to easily on-board AI models and
associated document/license. For modles built with Java, Python or R language,
modelers have the possibility to package them into reusable microservices.

A Modeler may test out the Acumos features in a personal
private/unpublished section of the Marketplace. Additionally, a Modeler may publish
the models to the Company Marketplace or to the Public Marketplace for wider distribution.

Modelers are typically subject-matter experts in their fields, so Acumos
models come from a wide range of domains and applications.

Models may be written in a number of popular programming languages or
toolkits, including Java, R, Python(Scikit Learn, Keras, Tensor FLow), ONNX and PFA. It is 
also possible to on-board models pre-dockerized outside Acumos.

All of the models that a user has on-boarded can be viewed from the :doc:`My
Models <portal-my-models>` page. Depending on their history, the models may exist in one
for three sections: MY UNPUBLISHED MODELS,PUBLISHED TO MARKETPLACE, and MY DELETED MODELS    .

Models published to Company are visible only to account holders on your local 
Acumos instance. This can be thought of as “inside the instance firewall” 
– typically viewable by close collaborators. Models published to Public are 
available to outside Acumos instances. The set of peers that may have access to 
Public models is determined by your local Administrator.

Private/Unpublished models are visible only to the Modeler. However, a Modeler 
does have the option to share a model with a specific user who has an account 
on the same Acumos instance.
