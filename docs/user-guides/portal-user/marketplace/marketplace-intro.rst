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

The Acumos Marketplace is designed to make it easy to discover, explore, and use AI models. 
In addition to displaying models from the local platform's catalog, the Public Marketplace 
can be configured to display models from peer Public Marketplaces. Users may view the details 
of a peer Model. However, users aren't able to work with, download, or deploy a peer Model 
without first requesting access to use that model. Public Marketplace peer relationships 
are set up and managed by the administrators of each Acumos instance.


The Marketplace only displays Public catalog models to users who have not logged in.
 There is no option to display models from marketplaces other than Public.


    .. image:: ../images/portal/marketplace_mainViewLoggedIn.png
       :width: 75%


The Marketplace has three main views to logged-in users:

- My Favorite Catalogs: In the marketplace only shows models which have been selected as a favorite catalog from the account setting page. 
- Public Catalogs: All public catalog models are displayed in the marketplace.
- Restricted Catalogs: Logged in user can view the restricted catalogs in the marketplace


After you have used models on the Marketplace, you can share your experiences
by using the comments and ratings capabilities on the Marketplace.


    .. image:: ../images/portal/marketplace_mainViewNotLoggedIn.png
       :width: 75%


1) Select **my favorite catalogs** to display only the favorite catalog models in the marketplace, or select **ALL Catalog** to view all the catalog models there.
2) Click a **Select Favorite Catalog** button to select your favorite catalogs list from the account setting page.
3) Select an option from the **Sort By drop down** to sort the displayed models
4) Select one or more checkboxes to **Filter By Category** **clicking the Show All** link display additional categories if they exist; see the `Filtering by Category` section for details
5) Change from grid view to list view by selecting the corresponding icon
6) Select an option from the **Showing** drop down to change the number of models displayed on a page 
7) Page navigation
8) Click a model's image to access the **Model Detail** page
9) **SITE ADMIN** and **PUBLISH REQUEST** are menu items only available to users with those roles

10) Model search; see the `Searching by Keyword`_ section for details

11) Click the **Bell** icon to review your `notifications`
12) Click the down arrow next to your name to access **Account Settings**
13) **Help** and **Log Out**   

Searching by Keyword
====================
You can search models in the marketplace to find keyword matches in the
following fields: name, description, author, publisher, solution ID and
revision ID.  To search the Marketplace by keywords, follow these steps:

#. Enter keywords in the search field near the top of the left navigation bar
#. Hit return or click the search icon to start the search
#. The result of your query is shown, with only the models that meet
   your search criteria

Filtering by Category
=====================
To filter your view of the Marketplace by Category, follow these
steps:

1. From the Marketplace left inner menu, select **Show All** to show all
   categories
2. Click on a Category to select it
3. The screen is updated with only models that have your selected
   Category

Filtering by Tag
================

To search the Marketplace using Tags, follow these steps:

1. From the Marketplace left inner menu, click on the Tag of interest
2. The Tag will become highlighted
3. The screen is updated with only the models that have your selected
   tag

Filtering by Catalogs
=====================

User has two option to filter catalogs.

1. Selecting **All Catalogs** , user can able see all public catalog models in the marketplace
2. Selecting **My Favorite Catalogs** , user can see only favorite catalog models in the marketplace

	.. image:: ../images/portal/marketplace_filtering_by_catalogs.png
		:width: 75%

