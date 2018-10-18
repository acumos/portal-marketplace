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

Notification Service
====================

Operation Name - Notification Count
-----------------------------------

**- Trigger**

Gets Notifications count for Market Place Catalog.

**- Request**

    {  }

**- Response**

    {
    "count": "<count>",
    "end": "2018-10-17T18:54:27.130Z",
    "message": "<message>",
    "notificationId": "<notificationId>",
    "start": "2018-10-17T18:54:27.130Z",
    "title": "<title>",
    "url": "<url>"
    }

Operation Name - Create Notification
------------------------------------

**- Trigger**

Create notification

**- Request**
  
  {
  "mlpNotification" : {
    "request_body": {
	    "created": "2018-10-17T18:54:27.181Z",
	    "end": "1521202458867",
	    "message": "<message>",
	    "modified": "2018-10-17T18:54:27.181Z",
	    "msgSeverityCode": "LO",
	    "notificationId": "<Notification ID",
	    "start": "1521202458867",
	    "title": "<Notification title/subject>",
	    "url": "<url>"
      },
    "request_from": "<request from>",
    "request_id": "<request id>"
     }
    }

**- Response**

  {
  "accessType": "<access Type>",
  "active": true,
  "commentId": "<comment Id>",
  "commentsCount": <comments Count>,
  "companyModelCount": <companyModel Count>,
  "created": "2018-10-17T18:54:27.167Z",
  "deletedModelCount": <deleted Model Count>,
  "description": "<description>",
  "downloadCount": <download Count>,
  "errorDetails": "<errorDetails>",
  "latestRevisionId": "<latest Revision Id>",
  "loginName": "<login Name>",
  "metadata": "<metadata>",
  "modelType": "<model Type>",
  "modelTypeName": "<model Type Name>",
  "modified": "2018-10-17T18:54:27.167Z",
  "name": "<name>",
  "onboardingStatusFailed": true,
  "ownerId": "<owner Id>",
  "ownerListForSol": [
    {
      "active": "<active>",
      "admin": true,
      "apiTokenHash": "<apiToken Hash>",
      "bulkUpdate": "<bulk Update String>",
      "created": "2018-10-17T18:54:27.167Z",
      "emailId": "<emailId>",
      "firstLogin": true,
      "firstName": "<first Name>",
      "jwtToken": "jwt Token",
      "jwttoken": "jwt token",
      "lastLogin": "2018-10-17T18:54:27.167Z",
      "lastName": "<last Name>",
      "loginName": "<login Name>",
      "loginPassExpire": true,
      "modified": "2018-10-17T18:54:27.167Z",
      "orgName": "<org Name>",
      "password": "<password>",
      "picture": [
        "string"
      ],
      "publisher": true,
      "role": "<role>",
      "roleId": "<role Id>",
      "status": "<status>",
      "tags": [
        {
          "tag": "Classification"
        }
      ],
      "updatedRole": "<updated Role>",
      "updatedRoleId": "<updated Role Id>",
      "userAssignedRolesList": [
        {
          "active": false,
          "created": "2018-10-17T18:54:27.167Z",
          "modified": "2018-10-17T18:54:27.167Z",
          "name": "My role",
          "roleId": "<Role Id>"
        }
      ],
      "userId": "<user Id>",
      "userIdList": [
        "string"
      ],
      "userNewRoleList": [
        "string"
      ],
      "userRolesList": {},
      "username": "<user name>",
      "verifyToken": "<verify Token>"
    }
  ],
  "ownerName": "<owner Name>",
  "pageNo": 0,
  "pendingApproval": true,
  "picture": [
    "string"
  ],
  "privateModelCount": 0,
  "publicModelCount": 0,
  "publisher": "<publisher>",
  "ratingAverageTenths": 0,
  "ratingCount": 0,
  "refreshInterval": 0,
  "revisions": [
    {
      "accessTypeCode": "PB",
      "authors": "My name\tMy contact",
      "created": "2018-10-17T18:54:27.167Z",
      "description": "string",
      "metadata": "string",
      "modified": "2018-10-17T18:54:27.167Z",
      "origin": "http://acumos.remote.com/a/b/c",
      "publisher": "My company",
      "revisionId": "<revisionId>",
      "solutionId": "<solutionId>",
      "sourceId": "<source Id>",
      "userId": "<user Id>",
      "validationStatusCode": "NV",
      "version": "v1.0"
    }
  ],
  "selector": "<selector>",
  "size": 0,
  "solutionId": "<solution Id>",
  "solutionRating": 0,
  "solutionRatingAvg": 0,
  "solutionTag": "<solution Tag>",
  "solutionTagList": [
    {
      "tag": "Classification"
    }
  ],
  "sortingOrder": "<sorting Order>",
  "threadId": "<thread Id>",
  "threadList": [
    {
      "revisionId": "<revision Id>",
      "solutionId": "<solution Id>",
      "threadId": "<thread Id>",
      "title": "<title>"
    }
  ],
  "tookitType": "<tookit Type>",
  "tookitTypeName": "<tookit Type Name>",
  "validationStatusCode": "<validation Status Code>",
  "viewCount": 0
  }
    
Operation Name - Delete Notification
------------------------------------

**- Trigger**

Delete Notification.

**- Request**

   {
   "notificationId" :"<notification Id>"  
   }

**- Response**

    {
    "count": "<count>",
    "end": "2018-10-17T18:54:27.130Z",
    "message": "<message>",
    "notificationId": "<notificationId>",
    "start": "2018-10-17T18:54:27.130Z",
    "title": "<title>",
    "url": "<url>"
    }
    
Operation Name - Drop Notification
------------------------------------

**- Trigger**

Drop Notification.

**- Request**

   {
   "notificationId" :"<notification Id>",
   "userId" : "<user Id>"
   }

**- Response**

    {
    "count": "<count>",
    "end": "2018-10-17T18:54:27.130Z",
    "message": "<message>",
    "notificationId": "<notificationId>",
    "start": "2018-10-17T18:54:27.130Z",
    "title": "<title>",
    "url": "<url>"
    }
    
Operation Name - Notifications
------------------------------

**- Trigger**

Gets a list of Paginated Notifications for Market Place Catalog.

**- Request**

   {  }

**- Response**

    {
    "count": "<count>",
    "end": "2018-10-17T18:54:27.130Z",
    "message": "<message>",
    "notificationId": "<notificationId>",
    "start": "2018-10-17T18:54:27.130Z",
    "title": "<title>",
    "url": "<url>"
    }
  
Operation Name - Notification Preference By UserId
--------------------------------------------------

**- Trigger**

Notification Preference By UserId

**- Request**

   {
   "userId" : "<user Id>"
   }

**- Response**

  {
    "msgSeverityCode": "LO",
    "notfDelvMechCode": "EM",
    "userId": "<User ID>",
    "userNotifPrefId": 0
  }
  
Operation Name - Create Notification Preference
-----------------------------------------------

**- Trigger**

Create Notification Preference

**- Request**

  {
  "request_body": {
    "msgSeverityCode": "<msgSeverityCode>",
    "notfDelvMechCode": "<notfDelvMechCode>",
    "userId": "string",
    "userNotifPrefId": 0
     },
  "request_from": "request from",
  "request_id": "<request Id>"
  }

**- Response**

  {
    "msgSeverityCode": "LO",
    "notfDelvMechCode": "EM",
    "userId": "<User ID>",
    "userNotifPrefId": 0
  }
  
Operation Name - View Notification
----------------------------------

**- Trigger**

Notification viewed by user

**- Request**
  {
  "mlNotification" : "<notification Id>"
  "userId" : "<user Id>"
  }

**- Response**

  {
   Response: 
  }
  
Thread Service
====================

Operation Name - Create Comments Thread
---------------------------------------

**- Trigger**

Create Comments Thread

**- Request**

  {
  "mlpComment" :
  {
  "request_body": {
    "commentId": "<comment Id>",
    "created": "2018-10-17T16:09:53.261Z",
    "modified": "2018-10-17T16:09:53.261Z",
    "parentId": "<parent Id>",
    "text": "Best model ever",
    "threadId": "<thread Id>",
    "userId": "<user Id>"
   },
  "request_from": "<request from>",
  "request_id": "<request Id>"
  }
  }
  
**- Response**

  {
  "commentId": "<comment Id>",
  "created": "2018-10-17T16:09:53.259Z",
  "modified": "2018-10-17T16:09:53.259Z",
  "parentId": "<parent Id>",
  "text": "Best model ever",
  "threadId": "<thread Id>",
  "userId": "<user Id>"
  }

Operation Name - Delete Comments
--------------------------------

**- Trigger**

Delete Comments

**- Request**

  {
  "threadId" : "<thread Id>"
  "commentId" : "<comment Id>"
  }
  
**- Response**

  {
  "commentId": "<comment Id>",
  "created": "2018-10-17T16:09:53.259Z",
  "modified": "2018-10-17T16:09:53.259Z",
  "parentId": "<parent Id>",
  "text": "Best model ever",
  "threadId": "<thread Id>",
  "userId": "<user Id>"
  }
  

Operation Name - Update Comments
--------------------------------

**- Trigger**

Update Comments

**- Request**

  {
  "mlpComment" :
  {
  "request_body": {
    "commentId": "<comment Id>",
    "created": "2018-10-17T16:09:53.261Z",
    "modified": "2018-10-17T16:09:53.261Z",
    "parentId": "<parent Id>",
    "text": "Best model ever",
    "threadId": "<thread Id>",
    "userId": "<user Id>"
   },
  "request_from": "<request from>",
  "request_id": "<request Id>"
  }
  }
  
**- Response**

  {
  "commentId": "<comment Id>",
  "created": "2018-10-17T16:09:53.259Z",
  "modified": "2018-10-17T16:09:53.259Z",
  "parentId": "<parent Id>",
  "text": "Best model ever",
  "threadId": "<thread Id>",
  "userId": "<user Id>"
  }

Operation Name - Get Comments
--------------------------------

**- Trigger**

Get Comments

**- Request**

  {
  "threadId" : "<thread Id>",
  "commentId" : "<comment Id>"
  }
  
**- Response**

  {
  "commentId": "<comment Id>",
  "created": "2018-10-17T16:09:53.259Z",
  "modified": "2018-10-17T16:09:53.259Z",
  "parentId": "<parent Id>",
  "text": "Best model ever",
  "threadId": "<thread Id>",
  "userId": "<user Id>"
  }


Operation Name - Thread
-----------------------

**- Trigger**

Gets a list of Threads

**- Request**

  {
  "mlpthread" : 
  {
  "request_body": {
    "fieldToDirectionMap": {},
    "page": 0,
    "size": 0
  },
  "request_from": "<request from>",
  "request_id": "<request Id>"
  }
  }
  
**- Response**

  {
  "allTagsSet": [
    "string"
  ],
  "async": {
    "cancelled": true,
    "done": true
  },
  "commentsCount": 0,
  "commentsList": [
    {
      "commentId": "<Comment Id>",
      "created": "2018-10-17T16:09:53.281Z",
      "modified": "2018-10-17T16:09:53.281Z",
      "parentId": "<Parent Id>",
      "text": "Best model ever",
      "threadId": "<Thread Id>",
      "userId": "<User Id>"
    }
  ],
  "companyModelCount": 0,
  "content": [
    {}
  ],
  "deletedModelCount": 0,
  "filteredTagSet": [
    "string"
  ],
  "first": true,
  "jwtToken": "<jwt Token>",
  "last": true,
  "modelsSharedWithUser": [
    {
      "active": false,
      "created": "2018-10-17T16:09:53.281Z",
      "description": "string",
      "metadata": "<metadata>",
      "modelTypeCode": "CL",
      "modified": "2018-10-17T16:09:53.281Z",
      "name": "My solution",
      "origin": "<origin>",
      "picture": [
        "string"
      ],
      "solutionId": "<Solution Id>",
      "sourceId": "<Source Id>",
      "tags": [
        {
          "tag": "Classification"
        }
      ],
      "toolkitTypeCode": "SK",
      "userId": "<User Id>",
      "webStats": {
        "downloadCount": 0,
        "featured": true,
        "lastDownload": "2018-10-17T16:09:53.281Z",
        "ratingAverageTenths": 0,
        "ratingCount": 0,
        "solutionId": "<solution Id>",
        "viewCount": 0
      }
    }
  ],
  "number": 0,
  "numberOfElements": 0,
  "pageCount": 0,
  "prefTags": [
    {}
  ],
  "privateModelCount": 0,
  "publicModelCount": 0,
  "requestList": [
    {
      "action": "<action>",
      "date": "2018-10-17T16:09:53.281Z",
      "requestId": "<request Id>",
      "requestType": "<request Type>",
      "requestedDetails": "<requested Details>",
      "sender": "<sender>",
      "status": "<status>"
    }
  ],
  "responseObject": {
    "admin": true,
    "firstLogin": true,
    "jwtToken": "<jwt Token>",
    "loginPassExpire": true,
    "publisher": true,
    "userAssignedRolesList": [
      {
        "active": false,
        "created": "2018-10-17T16:09:53.281Z",
        "modified": "2018-10-17T16:09:53.281Z",
        "name": "My role",
        "roleId": "<Role ID>"
      }
    ]
  },
  "size": 0,
  "sort": {},
  "tags": [
    "string"
  ],
  "threadCount": 0,
  "threads": [
    {
      "revisionId": "<revisionId>",
      "solutionId": "<solutionId>",
      "threadId": "<threadId>",
      "title": "<title>"
    }
  ],
  "totalElements": 0,
  "totalPages": 0,
  "userList": [
    {
      "active": "string",
      "admin": true,
      "apiTokenHash": "<apiToken Hash>",
      "bulkUpdate": "<bulkUpdate>",
      "created": "2018-10-17T16:09:53.281Z",
      "emailId": "<emailId>",
      "firstLogin": true,
      "firstName": "<first Name>",
      "jwtToken": "<jw tToken>",
      "jwttoken": "<jwt token>",
      "lastLogin": "2018-10-17T16:09:53.281Z",
      "lastName": "<last Name>",
      "loginName": "<login Name>",
      "loginPassExpire": true,
      "modified": "2018-10-17T16:09:53.281Z",
      "orgName": "<org Name>",
      "password": "<password>",
      "picture": [
        "string"
      ],
      "publisher": true,
      "role": "<role>",
      "roleId": "<role Id>",
      "status": "<status>",
      "tags": [
        {
          "tag": "Classification"
        }
      ],
      "updatedRole": "<updated Role>",
      "updatedRoleId": "<updated RoleId>",
      "userAssignedRolesList": [
        {
          "active": false,
          "created": "2018-10-17T16:09:53.281Z",
          "modified": "2018-10-17T16:09:53.281Z",
          "name": "My role",
          "roleId": "<Role Id>"
        }
      ],
      "userId": "string",
      "userIdList": [
        "string"
      ],
      "userNewRoleList": [
        "string"
      ],
      "userRolesList": {},
      "username": "<user name>",
      "verifyToken": "<verifyToken>"
    }
  ]
  }


Operation Name - Thread Count
-----------------------------

**- Trigger**

Create Comments Thread

**- Request**

  {   }
  
**- Response**

  {
  "revisionId": "<revisionId>",
  "solutionId": "<solutionId>",
  "threadId": "<threadId>",
  "title": "<title>"
  }

Operation Name - Create Thread
------------------------------

**- Trigger**

Create Thread

**- Request**

  {
  "mlpthread" : {
  "request_body": {
    "revisionId": "<revisionId>",
    "solutionId": "<solutionId>",
    "threadId": "<threadId>",
    "title": "string"
  },
  "request_from": "<request from>",
  "request_id": "<request Id>"
  }
  }
  
**- Response**

  {
  "revisionId": "<revisionId>",
  "solutionId": "<solutionId>",
  "threadId": "<threadId>",
  "title": "<title>"
  }

Operation Name - Delete Thread
------------------------------

**- Trigger**

Delete Thread

**- Request**

  {
  "threadId" : "<thread Id>"
  }
  
**- Response**

  {
  "revisionId": "<revisionId>",
  "solutionId": "<solutionId>",
  "threadId": "<threadId>",
  "title": "<title>"
  }

  
Operation Name - Update Thread
------------------------------

**- Trigger**

Update Thread

**- Request**

  {
  "request_body": {
    "revisionId": "<revisionId>",
    "solutionId": "<solutionId>",
    "threadId": "<threadId>",
    "title": "<title>"
  },
  "request_from": "<request from>",
  "request_id": "<request Id>"
  }
  
**- Response**

  {
  "revisionId": "<revisionId>",
  "solutionId": "<solutionId>",
  "threadId": "<threadId>",
  "title": "<title>"
  }
  
Operation Name - Threads according to solution and revision id
--------------------------------------------------------------

**- Trigger**

Gets a list of Threads according to solution and revision id's

**- Request**

  {
  "solutionId": "<solution Id>",
  "revisionId": "<revision Id>",
  "restPageReq": "<restPageReq>"
  }
  
**- Response**

  {
  "allTagsSet": [
    "string"
  ],
  "async": {
    "cancelled": true,
    "done": true
  },
  "commentsCount": 0,
  "commentsList": [
    {
      "commentId": "<Comment id>",
      "created": "2018-10-17T16:09:53.311Z",
      "modified": "2018-10-17T16:09:53.311Z",
      "parentId": "<parent id>",
      "text": "Best model ever",
      "threadId": "<thread Id>",
      "userId": "<user id>"
    }
  ],
  "companyModelCount": 0,
  "content": [
    {}
  ],
  "deletedModelCount": 0,
  "filteredTagSet": [
    "string"
  ],
  "first": true,
  "jwtToken": "<jwt Token>",
  "last": true,
  "modelsSharedWithUser": [
    {
      "active": false,
      "created": "2018-10-17T16:09:53.311Z",
      "description": "<description>",
      "metadata": "<metadata>",
      "modelTypeCode": "CL",
      "modified": "2018-10-17T16:09:53.311Z",
      "name": "My solution",
      "origin": "<origin>",
      "picture": [
        "string"
      ],
      "solutionId": "<solution Id>",
      "sourceId": "<Source Id>",
      "tags": [
        {
          "tag": "Classification"
        }
      ],
      "toolkitTypeCode": "SK",
      "userId": "<user Id>",
      "webStats": {
        "downloadCount": 0,
        "featured": true,
        "lastDownload": "2018-10-17T16:09:53.311Z",
        "ratingAverageTenths": 0,
        "ratingCount": 0,
        "solutionId": "<solution Id>",
        "viewCount": 0
      }
    }
  ],
  "number": 0,
  "numberOfElements": 0,
  "pageCount": 0,
  "prefTags": [
    {}
  ],
  "privateModelCount": 0,
  "publicModelCount": 0,
  "requestList": [
    {
      "action": "<action>",
      "date": "2018-10-17T16:09:53.311Z",
      "requestId": "<request Id>",
      "requestType": "<request Type>",
      "requestedDetails": "<requested Details>",
      "sender": "<sender>",
      "status": "<status>"
    }
  ],
  "responseObject": {
    "admin": true,
    "firstLogin": true,
    "jwtToken": "string",
    "loginPassExpire": true,
    "publisher": true,
    "userAssignedRolesList": [
      {
        "active": false,
        "created": "2018-10-17T16:09:53.311Z",
        "modified": "2018-10-17T16:09:53.311Z",
        "name": "My role",
        "roleId": "<role Id>"
      }
    ]
  },
  "size": 0,
  "sort": {},
  "tags": [
    "string"
  ],
  "threadCount": 0,
  "threads": [
    {
      "revisionId": "<revisionId>",
      "solutionId": "<solutionId>",
      "threadId": "<threadId>",
      "title": "<title>"
    }
  ],
  "totalElements": 0,
  "totalPages": 0,
  "userList": [
    {
      "active": "string",
      "admin": true,
      "apiTokenHash": "<apiToken Hash>",
      "bulkUpdate": "<bulk Update>",
      "created": "2018-10-17T16:09:53.311Z",
      "emailId": "<email Id>",
      "firstLogin": true,
      "firstName": "<first Name>",
      "jwtToken": "<jwt Token>",
      "jwttoken": "<jwt token>",
      "lastLogin": "2018-10-17T16:09:53.311Z",
      "lastName": "<last Name>",
      "loginName": "<login Name>",
      "loginPassExpire": true,
      "modified": "2018-10-17T16:09:53.311Z",
      "orgName": "<org Name>",
      "password": "<password>",
      "picture": [
        "string"
      ],
      "publisher": true,
      "role": "<role>",
      "roleId": "<role Id>",
      "status": "<status>",
      "tags": [
        {
          "tag": "Classification"
        }
      ],
      "updatedRole": "<updated Role>",
      "updatedRoleId": "<updated RoleId>",
      "userAssignedRolesList": [
        {
          "active": false,
          "created": "2018-10-17T16:09:53.311Z",
          "modified": "2018-10-17T16:09:53.311Z",
          "name": "My role",
          "roleId": "<role Id>"
        }
      ],
      "userId": "<user Id>",
      "userIdList": [
        "string"
      ],
      "userNewRoleList": [
        "string"
      ],
      "userRolesList": {},
      "username": "<user name>",
      "verifyToken": "<verify Token>"
    }
  ]
}


  
Operation Name - Comments Count Thread
--------------------------------------

**- Trigger**

Get the count of Threads

**- Request**

  {
  "threadId": "<thread Id>"
  }
  
**- Response**

  {
  "revisionId": "<revisionId>",
  "solutionId": "<solutionId>",
  "threadId": "<threadId>",
  "title": "<Title>"
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