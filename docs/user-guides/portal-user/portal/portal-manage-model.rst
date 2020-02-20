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

================
Managing a Model
================
The Model Detail page may not show very much information if the model has not 
been published. To add a description, documents and details, click on the **Manage My Model** button at the top. 

    .. image:: ../images/portal/models_manageMyModelBtn.png
       :width: 75%

A new page loads with MANAGEMENT OPTIONS on the left.

    .. image:: ../images/portal/models_manageMyModel.png
       :width: 75%


Sharing a Model with a Team
===========================

A Modeler can share a model with anyone who has an account on his/her local
Acumos. When you share a model with a collaborator, you make that Modeler a
co-owner of the model. This means they have all the same capabilities as
the original owner. An overview is shown below.

    .. image:: ../images/portal/models_shareWithTeamJourney.png
       :width: 75%


The steps to share are as follows:

1. First, select the **Share with Team** tab under **MANAGEMENT OPTIONS**

    .. image:: ../images/portal/models_shareWithTeamTab.png

2. Next, where you see **Find a user to Share with**, type in the user ID
   of the person with whom you wish to share. You will need to get that user
   ID from them. The UI will show suggestions based on the characters
   you have typed. Once you have located the correct person, select the
   **Share** button

    .. image:: ../images/portal/models_shareWithTeamScreen.png


3. The **Share with Team** will update. You will see that your
   model is shared and you have added collaborators. 

    .. image:: ../images/portal/models_shareWithTeamDone.png


The collaborator will receive a notification that a new model has
been shared with him/her.

Manage Publisher / Authors
==========================
Model owners have the ability to add different publisher name and the details of additional authors

    .. image:: ../images/portal/models_manageAuthors.png

After you fill in the required fields, click **Add author**.


Publishing a Model
==================

The publisher can create the number of catalogs in his instance, and using access level option 
he can restrict it to display in the marketplace. 

Users can publish one model in different catalogs and they can customize the information based 
on their needs. There are two types of access level Acumos has provided.

1. Public: Using this access level option publisher can allow his catalog access in the marketplace for the users. There is no condition to accessing these catalogs for the user whether they are logged-in or not.

2. Restricted: Using this access level option publisher can restrict the catalog access for the marketplace users. For accessing these catalogs user has to login to Acumos. 
   
The presentation of the models may be different in the marketplace to meet the needs of the 
different communities. For example, a user may wish to provide company-specific details to 
their colleagues inside their Company instance. This may include proprietary information, 
documents or details that are only relevant to colleagues using the Company instance. Information 
published to the restricted catalogs is contained within the company firewall.


Publishing to the Catalog
-------------------------

The publishing process is summarized here.

    .. image:: ../images/portal/models_publishLocalJourney.png


Specific steps:

#. From the My Models page, select the model of interest, open the Model Detail Page and click on Manage My Model at the top

#. Select Publish to Marketplace

#. Select the catalog from the drop-down. Catalog will display according to the role assigned to the user.

#. Click on Model Description and describe your model in terms that your users will understand and wish to use it

#. Click on Model License Profile and add/update/select the license profile.

#. Click on Model Category. Select a Category and Toolkit type from the dropdown box

#. Select Model Documents and add any useful documents, such as release notes or detailed instructions that will help your users

#. Click on Model Tags. Either select one of the system tags or add your own. Any tags you add will become available for other users to select as well.

#. You have completed the first step for publishing. Now click on Submit for Publication. This will launch a series of back end steps that will prepare your model for publication

#. The publishing workflow may consist of several steps configured by the Acumos Admin. Some instances may require manual review.

#. Once the publishing process is complete, all the workflow icons will be highlighted and the model will be available in the Company Marketplace


Note: If user have not added author details in the model before publishing, a continous message "You cannot publish the model without entering the author name. Please add author name in the "Manage Publisher/Authors" page to publish it" will display and Submit to Publication button showing disabled.
 
	.. image:: ../images/portal/models_author.PNG
  

Un-publishing the model from the catalog
----------------------------------------

Specific steps:

#. From the My Models page, select the model from the **MY PUBLISHED MODELS** list, open the Model Detail Page and click on Manage My Model at the top

#. Select Publish to Marketplace

#. Select the catalog from the right hand side drop-down (Catalog(s) of published model) which you want to un-publish from it.

#. Click on Un-publish button.

	.. image:: ../images/portal/unpublishing_model.png
