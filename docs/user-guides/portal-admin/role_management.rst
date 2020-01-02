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

.. role_management.rst:

================
Role Management
================

Admin can create the roles and provide corresponding permissions required to that role.

				.. image:: images/role_management.PNG
	               			  :width: 75%

Selecting the Create New Role button will present a dialog in which roles can be defined, with the options:

-  Role Name (mandatory): must be unique, i.e. not already used as a role name

-  Module Permission (mandatory): Assignment of one or more of the following
   permissions:

   -  Access to the Design Studio

   -  Access to the Marketplace

   -  Access to Onboarding


				.. image:: images/create_role.PNG
	               			  :width: 75%

Admin can give **catalog permissions** too. Allowing the user with a particular role to access particular set of catalogs.
The **Select All** check-box allows the admin to assign the permissions of all catalogs to a particular role.

				.. image:: images/select_all.PNG
	                 		:width: 75%

**Create** button creates the role once all the mandatory fields like Role Name, Module Permission etc.. are filled.
 
				.. image:: images/create_button.PNG
	                		 :width: 75%

Once a role is created, it is displayed in a tabular format stating its permissions along with its actions.
	
				.. image:: images/role_table.PNG
	            			    :width: 75%


**Edit** Icon under the Action tab allows to edit the already assigned module permissions, catalog permissions and role name too.

				.. image:: images/edit_role.PNG
	                 		:width: 75%

The **Update** button updates the existing permissions with new permissions.

				.. image:: images/update_role.PNG
	               			 :width: 75%

The **Delete** Icon allows the admin to delete a particular Role along with their pemissions.

				.. image:: images/delete_role.PNG
	                		 :width: 75%
		
