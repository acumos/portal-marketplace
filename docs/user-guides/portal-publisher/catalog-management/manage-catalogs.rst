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

Manage Catalogs Overview
========================

The publisher can create the number of catalogs in his instance, and using access level 
option he can restrict it to display in the marketplace. Publishers can publish one model in different 
catalogs and they can customize the information based on their needs. 

There are two types of access level Acumos has provided.

#. Public : Using this access level option publisher can allow his catalog access in the marketplace for the users. There is no condition to accessing these catalogs for the user whether they are logged-in or not.

#. Restricted : Using this access level option publisher can restrict the catalog access for the marketplace users. For accessing these catalogs user has to login to Acumos. 

	.. image:: ../images/publish/manage-catalogs-overview.png
                  :width: 75%
	
Clicking on **Add New Catalog** button, publisher is able to create new catalog. 
Publisher has to enter the details while creating a new catalog

Catalog Name: Publisher can gives any name to his catalogs

Access level: Select this option to restricted your catalogs to be displayed in the marketplace or not.

Add Description: Publisher can provide small details or information about your catalog.

Self Publish: Using the Self Publish option publisher can decide to have an admin approval for Publishing the catalogs on the marketplace.

	.. image:: ../images/publish/add-new-catalog.png
                 :width: 75%
	
Clicking on **Edit** icon publisher is able to edit catalog details

	.. image:: ../images/publish/edit_catalog.png
                  :width: 75%

Delete Catalog
===============
The trash-icon(Delete icon) under *Action* column is used to delete a catalog. Delete icon is *Enabled* only when there are no models associated with that catalog. Delete icon is *Disabled* when there is a single model associated with that catalog. 

	.. image:: ../images/publish/delete-catalog.png
                 :width: 75%

Any catalog cannot be deleted if it falls under any one or more conditions listed below:

* If catalog is added as favorite catalog.
* If catalog has a model with pending publish request.
* If user granted access to peer for the catalog being deleted.

	.. image:: ../images/publish/delete-error-catalog.png
                 :width: 75%
