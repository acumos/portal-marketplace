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

==================================
Portal Marketplace Developer Guide
==================================

What is MarketPlace?
====================

Acumos provides a toolkit-independent 'App Store', called
a Marketplace for:

   #. Data-powered decision making and artificial intelligence software
      models.

   #. It provides a means to securely share AI microservices along with
      information on how they perform, such as ratings, popularity statistics
      and user-provided reviews to apply crowd sourcing to software
      development..

   #. The platform provides integration between model developers and
      applications in order to automate the process of user feedback,
      exception handling and software updates..

Target Users
============

   This guide is targeted towards the open source user community that intends to understand the functionality of the MarketPlace.

MarketPlace - High level Architecture
=====================================

         .. image:: images/devguide/marketplace_architecture.jpg
            :alt: MarketPlace High level Architecture

MarketPlace Backend APIs
========================

- Admin Service
- Auth Service
- Market Place Catalog Service
- Notification Services
- Oauth User Service
- Publish Solution Service
- Push And Pull Solution Service
- User Role Services
- User Service Services
- Validation Status Services
- Web Based Onboarding Services

MarketPlace Flow Structure
==========================

   1. **Page Name:** Acumos Home Screen

      1. **User Authentication Required:** NO

      2. **Page Visibility to User:** ALL

      3. **Navigation Menu:** Market Place, Manage Models, Docs, SIGN IN ,
         SIGN UP

      4. **Page Content:** Featured Machine Learning Models/Solutions along
         with option to view all Solutions.

      5. When User open Acumos Page, He/she will be presented with Acumos Home
         Screen with Featured Machine Learning Solutions in catalog Format
         (Tiles)
      6. Admin Role  is created with name as Admin inside  Role tables in CDS DB.

   2. **Page Name:** Model/Solution Landing Page

      1.  **Navigation:** Acumos Home -> Market Place -> Model/Solution
          Landing Page

      2.  **User Authentication Required:** NO (Read Only), Yes (For
          Downloads, Deploy and to add Review Comment)

      3.  **Page Visibility:** ALL

      4.  **Navigation Menu:** Market Place, Manage Models, Docs, SIGN
          IN, SIGN UP

      5.  **Page Content:** Machine Learning Solution Landing Page with
          Title, Description, API Usage (Input & Output swagger UI
          format to test API), Images/Videos. Bottom of the screen
          should display ratings, reviews from other users and options
          to add review. Buttons needed <Download>  & <Deploy to
          Cloud>. Social Media Sharing options also need to be
          displayed. Deploy to Cloud should only provide MS Azure
          option.

      6.  Clicking on either of <Download>,<“Deploy to Clou> ” or<“Add
          review>  should prompt user to SIGN IN.

      7.  If User is already signed in, then clicking on:

      8.  <Download>  should download the Machine Learning Solution to
          user laptop/computer.

      9.  <Deploy to Cloud>  should prompt details about MS Azure (Inputs
          TBD)

      10. <Add Review Comment>  with text in the comment field should add
          the new comment.

   3. **Page Name:** My Models

      11. Under Manage Models Menu, Options available are: <Add new
          Model>,<“My Model>  <Delete a Model>

      12. **Navigation:** Acumos Home -> Manage Models -> My Models

      13. **User Authentication Required:** Yes

      14. **Navigation Menu:** Market Place, Manage Models, Docs,
          Notification, My Profile, Log Out

      15. **Page Content:** Machine Learning Solutions and Composite
          Solutions are displayed in a catalog format. Icons on these
          solutions should allow to distinguish Single Modelled
          Solutions, Composite Solutions, Unpublished, published (Public
          Market Place & Company Market Place) as well as newly created
          Solutions which does not have any title/description etc.

   4. **Page Name:** Manage Models - My Models - Model Landing Page

      16. Under Manage Models Menu, Options available are: <Add new
          Model>,<“My Model>  <Delete a Model>

      17. **User Authentication Required:** Yes

      18. **Navigation Menu:** Market Place, Manage Models, Docs,
          Notification, My Profile, Log Out

      19. **Page Content:** If User has clicked on newly added Machine
          Learning Solution that does not have any Title/Description etc
          then Machine Learning Solution Landing Page with fields for
          Title, Description, API Usage (Input & Output swagger UI
          format to test API), Images/Videos will be displayed where
          User can add all the information using WYSIWYG editor.

    4.1. User can save and view the preview of the Solutions like it would
    display on the Market Place.

    4.2 Once Saved, User can then Submit the Solution for publishing to
    Public Market Place or Company Market Place by clicking buttons
    <Publish to Public Market Place>  and <Publish to Company Market
    Place>. Clicking on these two buttons will kick off the
    Certification Process which would allow the Solution to be able
    to publish on Company Market Place i.e local Market Place and it
    would also be allowed to be published on Public Market Place.

    4.3 User would also be able to Share the Solutions with individuals
    or group or communities within the local Acumos instance i.e Company
    Acumos by clicking on <Share with Team>  which will open a pop up to
    lookup for the User/Group/Communities.

    4.4 Certification Process requirements is TBD and once available ,
    the user experience/Wireframes can be discusses later.

1.6 User Account Signup Flow :

            .. image:: images/devguide/Signup_Flow.jpg
               :alt: User Account Signup Flow

1.7 User Account Login Flow :

            .. image:: images/devguide/Login_Flow.jpg
               :alt: User Account Login Flow

1.8 Market Place Catalog Flow :

            .. image:: images/devguide/Catalog_Flow.jpg
               :alt: Market Place Catalog Flow

1.9 Model Detail Page Flow :

            .. image:: images/devguide/Model_Detail_Page_Flow.jpg
               :alt: Model Detail Page Flow


Market Place Catalog Service
============================

Operation Name - Solutions
--------------------------

**- Trigger**

This API is used to gets a list of Published Solutions for Market Place
Catalog.

**- Request**

{

"request_body": {

"accessType": "string",

"activeType": "string",

"description": "string",

"modelToolkitType": "string",

"modelType": "string",

"name": "string",

"page": 0,

"searchTerm": "string",

"size": 0,

"sortBy": "string",

"sortById": "string",

"sortingOrder": "string"

},

"request_from": "string",

"request_id": "string"

}

**- Response**

{

"status": null,

"status_code": 0,

"response_detail": "Solutions fetched Successfully",

"response_code": null,

"response_body": {

"content": [

{

"solutionId": "999",

"name": "s9",

"description": null,

"ownerId": "58fd6bbd-4894-4653-8a1e-2f41185e7971",

"ownerName": "vinayak shetage",

"active": true,

"accessType": "PB",

"created": 1513691632000,

"modified": 1513685062000,

"tookitType": "CP",

"pageNo": 1,

"size": 9,

"modelType": "DS",

"downloadCount": 12,

"solutionRating": 3,

"solutionRatingAvg": 2,

"viewCount": 14,

"ratingAverageTenths": 10,

"ratingCount": 20,

"companyModelCount": 10,

"deletedModelCount": 10,

"privateModelCount": 11,

"publicModelCount": 12

},

.

.

.

"error_code": "100"

}

Operation Name - Get Solution/Model
-----------------------------------

**- Trigger:**

    Gets a Solution Detail for the given SolutionId. Same API can be
    used for both Solution Owner view as well as General user. API will
    return isOwner as true if the user is owner of the solution.

**- Request:**

    {

    "solutionId":: "04cd7d58-16df-4a13-81da-99ca8d5701d3"

    }

**- Response:**

{

    "status": null,

    "status_code": 0,

    "response_detail": "Solutions fetched Successfully",

    "response_code": null,

    "response_body": {

    "solutionId": "04cd7d58-16df-4a13-81da-99ca8d5701d3",

    "name": "Data Mapper",

    "description": null,

    "ownerId": "173cad03-7527-42c5-81cc-35bac96cbf05",

    "ownerName": "pradip ahire",

    "active": true,

    "accessType": "PR",

    "created": 1512384166000,

    "modified": 1508513066000,

    "tookitType": "SK",

    "tookitTypeName": "Scikit-Learn",

    "pageNo": 1,

    "size": 9,

    "modelType": "DT",

    "modelTypeName": "Data Transformer",

    "downloadCount": 12,

    "solutionRating": 32,

    "viewCount": 12,

    "ratingAverageTenths": 0,

    "ratingCount": 22,

    "companyModelCount": 32,

    "deletedModelCount": 24,

    "privateModelCount": 14,

    "publicModelCount": 25

    },

    "error_code": "100"

}

Operation Name - Share Solution/Model
-------------------------------------

**- Trigger:**

    `Gets models shared for the given
    userId. <http://localhost:8083/swagger-ui.html#!/market-place-catalog-service-controller/getMySharedModelsUsingGET>`__

**- Request:**

    {

    "userId":: "173cad03-7527-42c5-81cc-35bac96cbf05"

    }

**- Response:**

{

"status": null,

"status_code": 0,

"response_detail": "Models shared with user fetched Successfully",

"response_code": null,

"response_body": [

{

"solutionId": "02a87750-7ba3-4ea7-8c20-c1286930f57c",

"name": "knnmodel_31102017_IST",

"description": "knnmodel_31102017_IST",

"ownerId": "173cad03-7527-42c5-81cc-35bac96cbf05",

"active": true,

"accessType": "PR",

"created": 1512120691000,

"modified": 1509441525000,

"pageNo": 0,

"size": 0,

"downloadCount": 45,

"solutionRating": 3,

"viewCount": 10,

"ratingCount": 12,

"companyModelCount": 22,

"deletedModelCount": 14,

"privateModelCount": 20,

"publicModelCount": 30

],

"error_code": "100"

}

Operation Name - Get Rating
---------------------------

**- Trigger:**

`Gets the rating for the solution given by different
user. <http://localhost:8083/swagger-ui.html#!/market-place-catalog-service-controller/getMySharedModelsUsingGET>`__

**- Request:**

    {

    "solutionId": "06cdcc30-8725-4c2a-98ec-3219f2964206"

    }

**- Response:**

{

"status": null,

"status_code": 200,

"response_detail": "Solutions fetched Successfully",

"response_code": null,

"response_body": {

"content": [

{

"created": 1513694393000,

"modified": null,

"solutionId": "06cdcc30-8725-4c2a-98ec-3219f2964206",

"userId": "173cad03-7527-42c5-81cc-35bac96cbf05",

"rating": 3,

"textReview": "cbcb"

}

],

"number": 0,

"size": 20,

"totalPages": 1,

"numberOfElements": 1,

"totalElements": 1,

"previousPage": false,

"first": true,

"nextPage": false,

"last": true,

"sort": null

},

"error_code": "100"

}

Operation Name - Create Favorite
--------------------------------

**- Trigger:** 

`Create favorite for
solution <http://localhost:8083/swagger-ui.html#!/market-place-catalog-service-controller/getSearchSolutionsUsingGET>`__
given by the end users.

**- Request:**

    {

    "request_body": {

    "solutionId": "093b29ea-8d6b-407e-b3e9-4d52964ba902",

    "userId": "173cad03-7527-42c5-81cc-35bac96cbf05"

    }

    }

**- Response:**

    {

    "status": null,

    "status_code": 0,

    "response_detail": "Successfully created solution favorite",

    "response_code": null,

    "response_body": null,

    "error_code": "100"

    }


Operation Name - Admin Service
------------------------------------------

**- Trigger:**

Gets the value of the MANIFEST.MF property Implementation-Version as written by maven.


**- Request:**

{
  "data": {},
  "status": 0
}

**- Response:**

{
  "status": 200,
  
  "data": "1.16.2-SNAPSHOT"
}

Operation Name - carouseConfigService 
------------------------------------------

**- Trigger:**

Gets list of Site configuration filtered with user's preferred tags.


**- Request:**

{
“userId”:: "<userID>"
}

**- Response:**

{
  "status": null,
  
  "status_code": 0,
  
  "response_detail": "getUserCarousalConfiguration fetched Successfully",
  
  "response_code": null,
  
  "response_body": [
    {
      "0": {
	  
        "name": "Test Slide",
		
        "headline": "Test Slide ok",
		
        "supportingContent": "<p>Just for test</p>",
		
        "textAling": "left",
		
        "number": "0",
		
        "slideEnabled": "true",
		
        "tagName": "13",
		
        "bgImageUrl": "ML_solution.jpg",
		
        "InfoImageUrl": "Layer_2.png",
		
        "links": {
		
          "enableLink": "true",
		  
          "primary": {},
		  
          "secondary": {}
        }
      }
    },
    {
      "1": {
        "name": "Test",
		
        "headline": "Testing",
		
        "supportingContent": "<p>TEst</p>",
		
        "textAling": "left",
		
        "number": "1",
		
        "slideEnabled": "false",
		
        "tagName": "1234",
		
        "links": {
		
          "enableLink": true,
		  
          "primary": {},
		  
          "secondary": {}
        }
      }
    },
    {
      "2": {
	  
        "name": "test14Sep18",
		
        "headline": "test",
		
        "supportingContent": "<p>test14Sep18</p>",
		
        "textAling": "left",
		
        "number": "2",
		
        "slideEnabled": "false",
		
        "tagName": "1WA_tag",
		
        "links": {
		
          "primary": {
		  
            "label": "test",
			
            "address": "marketPlace"
			
          },
          "secondary": {}
        }
      }
    },
    {
      "3": {
	  
        "name": "slide 5",
		
        "headline": "slide 5",
		
        "supportingContent": "<p>dfsdfdfsdf</p>",
		
        "textAling": "right",
		
        "number": "3",
		
        "slideEnabled": "false",
		
        "tagName": "1ww",
		
        "links": {
		
          "primary": {
		  
            "address": "modelerResource"
			
          },
		  
          "secondary": {
		  
            "address": "marketPlace"
			
          }
        }
      }
    }
  ],
  "content": null,
  
  "error_code": "100"
}

Operation Name - Enabling SignUp Service 
------------------------------------------
**- Trigger:**

Get SignUp Enabled and verify and return Success

**- Request:**
{

}



**- Response:**

{
  "status": true,
  
  "status_code": 200,
  
  "response_detail": "Success",
  
  "response_code": null,
  
  "response_body": "true",
  
  "content": null,
  
  "error_code": null
}


Operation Name  - Authentication Service
----------------------------------------
**- Trigger:**

This api is used to validate user by accepting there emailid or username.return success & JWT token if account created successfully  else an error message is returned:


**- Request:**

{
 "request_body": {

   "password": <"Password">

   "username": "<"Username">
 }
}

**- Response:**

{
  "loginPassExpire": false,
  
  "userAssignedRolesList": [
  
    {
      "created": 1536367599000,
	  
      "modified": 1538142743000,
	  
      "roleId": "497ddcfb-a15c-4729-8bf7-41a6ea4a33ed",
	  
      "name": "Publisher",
	  
      "active": true
    },
    {
      "created": 1513691459000,
	  
      "modified": 1538142622000,
	  
      "roleId": "8c850f07-4352-4afd-98b1-00cbceca569f",
	  
      "name": "Admin",
	  
      "active": true
    }
  ],
  "firstLogin": false,
  
   "jwtToken": <"Jwttoken">,
   
  "admin": true,
  
  "publisher": true
}

Operation Name - Login service using Authentication token
------------------------------------------------------------

**- Trigger:**

Allows User to login to the Platform using emailId or username. Returns Success & JWT Token if Account created successfully; else an error message is returned.

**- Request:**

{
 "request_body": {
   "password": <"password">,
   
   "username": <"username">
 }
}

**- Response:**

{
  "loginPassExpire": false,

  "userAssignedRolesList": null,

  "firstLogin": false,

  "firstName": "TechM",

  "lastName": "Dev",

  "emailId": "techm@techmahindra.com",

  "username": <"username">,

  "password": null,

  "active": "true",

  "lastLogin": 1539774948356,

  "created": 1520526238000,

  "modified": null,

  "userId": "<userID>",

  "loginName": null,

  "orgName": null,

  "picture": null,

  "jwttoken": null,

  "role": null,

  "roleId": null,

  "updatedRole": null,

  "updatedRoleId": null,

  "userIdList": null,

  "userNewRoleList": null,

  "userRolesList": null,

  "bulkUpdate": null,

  "apiTokenHash": null,

  "verifyToken": null,

  "status": null,

  "tags": null,

  "jwtToken": null,

  "admin": false,

  "publisher": false
}




Project Tools
=============

This micro service is a Spring-Boot application that for
Portal Market Backend onthe Acumos platform.
The first version listens only on localhost (127.0.0.1) & port 8080.

Tools required
--------------

- JDK 1.8
- Spring STS 3.8.x (https://spring.io/tools/sts/all)
- Git Shell (https://git-for-windows.github.io/) or SourceTree (https://www.sourcetreeapp.com/) for Cloning & pushing the code changes.
- Maven 3.x
- Proxy setup to download dependencies from open source repositories


How to Clone
------------

1. Open Source or GitShell Command Line Interface
2. Browse to your preferred directory and run below command:

git clone https://<userid>@gerrit.acumos.org/portal-marketplace.git

Note: replace with your user id.
3.  Once the repository is cloned.
You would be able to build the branch Locally by running below command:

 mvn clean install