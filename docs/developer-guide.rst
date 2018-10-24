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

         .. image:: images/devguide/PORTAL_Architecture_V_1.jpg
            :alt: MarketPlace High level Architecture


         .. image:: images/devguide/PORTAL_Architecture_V13_Portal_2.jpg
            :alt: MarketPlace High level Architecture

MarketPlace Backend APIs
========================

- Admin Service
- Auth Service
- Market Place Catalog Service
- Notification Services
- Oauth User Service
- Publish Request Service
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


=================================
Market Place Catalog Service API
=================================


Get Solution Details
====================


``GET http://<host and optionally port>/solutions/{solutionId}``


Fetches Solution Detail for the given SolutionId.

Response Body Example:

.. code-block:: json

    {
        "accessType": "string",
        "active": true,
        "commentId": "string",
        "commentsCount": 0,
        "companyModelCount": 0,
        "created": "2018-10-17T19:23:59.773Z",
        "deletedModelCount": 0,
        "description": "string",
        "downloadCount": 0,
        "errorDetails": "string",
        "latestRevisionId": "string",
        "loginName": "string",
        "metadata": "string",
        "modelType": "string",
        "modelTypeName": "string",
        "modified": "2018-10-17T19:23:59.773Z",
        "name": "string",
        "onboardingStatusFailed": true,
        "ownerId": "string",
        "ownerListForSol": [
            {
                "active": "string",
                "admin": true,
                "apiTokenHash": "string",
                "bulkUpdate": "string",
                "created": "2018-10-17T19:23:59.773Z",
                "emailId": "string",
                "firstLogin": true,
                "firstName": "string",
                "jwtToken": "string",
                "jwttoken": "string",
                "lastLogin": "2018-10-17T19:23:59.773Z",
                "lastName": "string",
                "loginName": "string",
                "loginPassExpire": true,
                "modified": "2018-10-17T19:23:59.773Z",
                "orgName": "string",
                "password": "string",
                "picture": [
                    "string"
                ],
                "publisher": true,
                "role": "string",
                "roleId": "string",
                "status": "string",
                "tags": [
                    {
                        "tag": "Classification"
                    }
                ],
                "updatedRole": "string",
                "updatedRoleId": "string",
                "userAssignedRolesList": [
                    {
                        "active": false,
                        "created": "2018-10-17T19:23:59.773Z",
                        "modified": "2018-10-17T19:23:59.773Z",
                        "name": "My role",
                        "roleId": "12345678-abcd-90ab-cdef-1234567890ab"
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
                "username": "string",
                "verifyToken": "string"
            }
        ],
        "ownerName": "string",
        "pageNo": 0,
        "pendingApproval": true,
        "picture": [
            "string"
        ],
        "privateModelCount": 0,
        "publicModelCount": 0,
        "publisher": "string",
        "ratingAverageTenths": 0,
        "ratingCount": 0,
        "refreshInterval": 0,
        "revisions": [
            {
                "accessTypeCode": "PB",
                "authors": "My name\tMy contact",
                "created": "2018-10-17T19:23:59.773Z",
                "description": "string",
                "metadata": "string",
                "modified": "2018-10-17T19:23:59.773Z",
                "origin": "http://acumos.remote.com/a/b/c",
                "publisher": "My company",
                "revisionId": "12345678-abcd-90ab-cdef-1234567890ab",
                "solutionId": "12345678-abcd-90ab-cdef-1234567890ab",
                "sourceId": "12345678-abcd-90ab-cdef-1234567890ab",
                "userId": "12345678-abcd-90ab-cdef-1234567890ab",
                "validationStatusCode": "NV",
                "version": "v1.0"
            }
        ],
        "selector": "string",
        "size": 0,
        "solutionId": "string",
        "solutionRating": 0,
        "solutionRatingAvg": 0,
        "solutionTag": "string",
        "solutionTagList": [
            {
                "tag": "Classification"
            }
        ],
        "sortingOrder": "string",
        "threadId": "string",
        "threadList": [
            {
                "revisionId": "12345678-abcd-90ab-cdef-1234567890ab",
                "solutionId": "12345678-abcd-90ab-cdef-1234567890ab",
                "threadId": "12345678-abcd-90ab-cdef-1234567890ab",
                "title": "string"
            }
        ],
        "tookitType": "string",
        "tookitTypeName": "string",
        "validationStatusCode": "string",
        "viewCount": 0
    }


Update Solution
===============


``PUT http://<host and optionally port>/solutions/{solutionId}``


Update Solution Detail for the given SolutionId.

Request Body Example:

.. code-block:: json

    {
        "request_body": {
            "accessType": "string",
            "active": true,
            "description": "string",
            "modelType": "string",
            "modelTypeName": "string",
            "name": "string",
            "ownerId": "string",
            "picture": [
                "string"
            ],
            "solutionId": "string",
            "tookitType": "string",
            "validationStatusCode": "string",
        },
        "request_from": "string",
        "request_id": "string"
    }


Response Body Example:

.. code-block:: json

    {
        "accessType": "string",
        "active": true,
        "commentId": "string",
        "commentsCount": 0,
        "companyModelCount": 0,
        "created": "2018-10-17T19:23:59.773Z",
        "deletedModelCount": 0,
        "description": "string",
        "downloadCount": 0,
        "errorDetails": "string",
        "latestRevisionId": "string",
        "loginName": "string",
        "metadata": "string",
        "modelType": "string",
        "modelTypeName": "string",
        "modified": "2018-10-17T19:23:59.773Z",
        "name": "string",
        "onboardingStatusFailed": true,
        "ownerId": "string",
        "ownerListForSol": [
            {
                "active": "string",
                "admin": true,
                "apiTokenHash": "string",
                "bulkUpdate": "string",
                "created": "2018-10-17T19:23:59.773Z",
                "emailId": "string",
                "firstLogin": true,
                "firstName": "string",
                "jwtToken": "string",
                "jwttoken": "string",
                "lastLogin": "2018-10-17T19:23:59.773Z",
                "lastName": "string",
                "loginName": "string",
                "loginPassExpire": true,
                "modified": "2018-10-17T19:23:59.773Z",
                "orgName": "string",
                "password": "string",
                "picture": [
                    "string"
                ],
                "publisher": true,
                "role": "string",
                "roleId": "string",
                "status": "string",
                "tags": [
                    {
                        "tag": "Classification"
                    }
                ],
                "updatedRole": "string",
                "updatedRoleId": "string",
                "userAssignedRolesList": [
                    {
                        "active": false,
                        "created": "2018-10-17T19:23:59.773Z",
                        "modified": "2018-10-17T19:23:59.773Z",
                        "name": "My role",
                        "roleId": "12345678-abcd-90ab-cdef-1234567890ab"
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
                "username": "string",
                "verifyToken": "string"
            }
        ],
        "ownerName": "string",
        "pageNo": 0,
        "pendingApproval": true,
        "picture": [
            "string"
        ],
        "privateModelCount": 0,
        "publicModelCount": 0,
        "publisher": "string",
        "ratingAverageTenths": 0,
        "ratingCount": 0,
        "refreshInterval": 0,
        "revisions": [
            {
                "accessTypeCode": "PB",
                "authors": "My name\tMy contact",
                "created": "2018-10-17T19:23:59.773Z",
                "description": "string",
                "metadata": "string",
                "modified": "2018-10-17T19:23:59.773Z",
                "origin": "http://acumos.remote.com/a/b/c",
                "publisher": "My company",
                "revisionId": "12345678-abcd-90ab-cdef-1234567890ab",
                "solutionId": "12345678-abcd-90ab-cdef-1234567890ab",
                "sourceId": "12345678-abcd-90ab-cdef-1234567890ab",
                "userId": "12345678-abcd-90ab-cdef-1234567890ab",
                "validationStatusCode": "NV",
                "version": "v1.0"
            }
        ],
        "selector": "string",
        "size": 0,
        "solutionId": "string",
        "solutionRating": 0,
        "solutionRatingAvg": 0,
        "solutionTag": "string",
        "solutionTagList": [
            {
                "tag": "Classification"
            }
        ],
        "sortingOrder": "string",
        "threadId": "string",
        "threadList": [
            {
                "revisionId": "12345678-abcd-90ab-cdef-1234567890ab",
                "solutionId": "12345678-abcd-90ab-cdef-1234567890ab",
                "threadId": "12345678-abcd-90ab-cdef-1234567890ab",
                "title": "string"
            }
        ],
        "tookitType": "string",
        "tookitTypeName": "string",
        "validationStatusCode": "string",
        "viewCount": 0
    }


Get Solution Revisions
======================


``GET http://<host and optionally port>/solutions/{solutionId}/revisions``


Gets a list of Solution Revision from the Catalog of the local Acumos Instance.

Response Body Example:

.. code-block:: json

    {
        "status": true,
        "status_code": 0,
        "response_detail": "string",
        "response_code": "200",
        "response_body": [
            {
                "accessTypeCode": "PB",
                "authors": "My name\tMy contact",
                "created": "2018-10-17T19:23:59.801Z",
                "description": "string",
                "metadata": "string",
                "modified": "2018-10-17T19:23:59.801Z",
                "origin": "http://acumos.remote.com/a/b/c",
                "publisher": "My company",
                "revisionId": "12345678-abcd-90ab-cdef-1234567890ab",
                "solutionId": "12345678-abcd-90ab-cdef-1234567890ab",
                "sourceId": "12345678-abcd-90ab-cdef-1234567890ab",
                "userId": "12345678-abcd-90ab-cdef-1234567890ab",
                "validationStatusCode": "NV",
                "version": "v1.0"
            }
        ],
        "content": null,
        "error_code": null
    }


Get Solution Revisions Artifacts
================================


``GET http://<host and optionally port>/solutions/{solutionId}/revisions/{revisionId}``


Gets a list of Solution Revision Artifacts from the Catalog of the local Acumos Instance.

Response Body Example:

.. code-block:: json

    {
        "status": true,
        "status_code": 0,
        "response_detail": "success",
        "response_code": "200",
        "response_body": [
            {
                "artifactId": "12345678-abcd-90ab-cdef-1234567890ab",
                "artifactTypeCode": "MS",
                "created": "2018-10-17T19:23:59.807Z",
                "description": "string",
                "metadata": "string",
                "modified": "2018-10-17T19:23:59.807Z",
                "name": "My artifact",
                "size": 65536,
                "uri": "http://archive.company.com/artifacts/my_artifact_name",
                "userId": "12345678-abcd-90ab-cdef-1234567890ab",
                "version": "v1.0"
            }
        ],
        "content": null,
        "error_code": null
    }


Get Shared Users of Solution
================================


``GET http://<host and optionally port>/solution/userAccess/{solutionId}``


Gets a user access Detail for the given SolutionId.

Response Body Example:

.. code-block:: json

    {
       "status":null,
       "status_code":0,
       "response_detail":"Users for solution fetched Successfully",
       "response_code":null,
       "response_body":{
          "content":[
          ],
          "jwtToken":null,
          "responseObject":null,
          "async":null,
          "allTagsSet":null,
          "tags":null,
          "prefTags":null,
          "filteredTagSet":null,
          "userList":[
             {
                "active":"string",
                "admin":true,
                "apiTokenHash":"string",
                "bulkUpdate":"string",
                "created":"2018-10-17T19:23:59.683Z",
                "emailId":"string",
                "firstLogin":true,
                "firstName":"string",
                "jwtToken":"string",
                "jwttoken":"string",
                "lastLogin":"2018-10-17T19:23:59.683Z",
                "lastName":"string",
                "loginName":"string",
                "loginPassExpire":true,
                "modified":"2018-10-17T19:23:59.683Z",
                "orgName":"string",
                "password":"string",
                "picture":[
                   "string"
                ],
                "publisher":true,
                "role":"string",
                "roleId":"string",
                "status":"string",
                "tags":[
                   {
                      "tag":"Classification"
                   }
                ],
                "updatedRole":"string",
                "updatedRoleId":"string",
                "userAssignedRolesList":[
                   {
                      "active":false,
                      "created":"2018-10-17T19:23:59.683Z",
                      "modified":"2018-10-17T19:23:59.683Z",
                      "name":"My role",
                      "roleId":"12345678-abcd-90ab-cdef-1234567890ab"
                   }
                ],
                "userId":"string",
                "userIdList":[
                   "string"
                ],
                "userNewRoleList":[
                   "string"
                ],
                "userRolesList":{
                },
                "username":"string",
                "verifyToken":"string"
             }
          ],
          "threads":null,
          "commentsList":null,
          "commentsCount":0,
          "threadCount":0,
          "totalElements":0,
          "modelsSharedWithUser":null,
          "requestList":null,
          "pageCount":0,
          "privateModelCount":0,
          "publicModelCount":0,
          "companyModelCount":0,
          "deletedModelCount":0,
          "totalPages":1,
          "last":true,
          "size":0,
          "number":0,
          "sort":null,
          "first":true,
          "numberOfElements":0
       },
       "content":null,
       "error_code":"100"
    }


Add Shared User for Solution
============================


``POST http://<host and optionally port>/solution/userAccess/{solutionId}/add``


Adds  user access Detail for the given SolutionId.

Request Body Example:

.. code-block:: json

    {
        "request_body": [
            "<String userId>"
        ],
        "request_from": "string",
        "request_id": "string"
    }


Response Body Example:

.. code-block:: json

    {
       "content":{
          "active":"string",
          "admin":true,
          "apiTokenHash":"string",
          "bulkUpdate":"string",
          "created":"2018-10-17T19:23:59.691Z",
          "emailId":"string",
          "firstLogin":true,
          "firstName":"string",
          "jwtToken":"string",
          "jwttoken":"string",
          "lastLogin":"2018-10-17T19:23:59.691Z",
          "lastName":"string",
          "loginName":"string",
          "loginPassExpire":true,
          "modified":"2018-10-17T19:23:59.691Z",
          "orgName":"string",
          "password":"string",
          "picture":[
             "string"
          ],
          "publisher":true,
          "role":"string",
          "roleId":"string",
          "status":"string",
          "tags":[
             {
                "tag":"Classification"
             }
          ],
          "updatedRole":"string",
          "updatedRoleId":"string",
          "userAssignedRolesList":[
             {
                "active":false,
                "created":"2018-10-17T19:23:59.692Z",
                "modified":"2018-10-17T19:23:59.692Z",
                "name":"My role",
                "roleId":"12345678-abcd-90ab-cdef-1234567890ab"
             }
          ],
          "userId":"string",
          "userIdList":[
             "string"
          ],
          "userNewRoleList":[
             "string"
          ],
          "userRolesList":{
          },
          "username":"string",
          "verifyToken":"string"
       },
       "error_code":"string",
       "response_body":{
          "active":"string",
          "admin":true,
          "apiTokenHash":"string",
          "bulkUpdate":"string",
          "created":"2018-10-17T19:23:59.692Z",
          "emailId":"string",
          "firstLogin":true,
          "firstName":"string",
          "jwtToken":"string",
          "jwttoken":"string",
          "lastLogin":"2018-10-17T19:23:59.692Z",
          "lastName":"string",
          "loginName":"string",
          "loginPassExpire":true,
          "modified":"2018-10-17T19:23:59.692Z",
          "orgName":"string",
          "password":"string",
          "picture":[
             "string"
          ],
          "publisher":true,
          "role":"string",
          "roleId":"string",
          "status":"string",
          "tags":[
             {
                "tag":"Classification"
             }
          ],
          "updatedRole":"string",
          "updatedRoleId":"string",
          "userAssignedRolesList":[
             {
                "active":false,
                "created":"2018-10-17T19:23:59.692Z",
                "modified":"2018-10-17T19:23:59.692Z",
                "name":"My role",
                "roleId":"12345678-abcd-90ab-cdef-1234567890ab"
             }
          ],
          "userId":"string",
          "userIdList":[
             "string"
          ],
          "userNewRoleList":[
             "string"
          ],
          "userRolesList":{
          },
          "username":"string",
          "verifyToken":"string"
       },
       "response_code":"string",
       "response_detail":"string",
       "status":true,
       "status_code":0
    }


Find POrtal Solutions
=====================


``POST http://<host and optionally port>/portal/solutions``


Search the solutions according to the parameters.

Request Body Example:

.. code-block:: json

    {
       "request_body":{
          "accessTypeCodes":[
             "string"
          ],
          "active":true,
          "authorKeyword":"string",
          "descriptionKeyword":[
             "string"
          ],
          "modelTypeCodes":[
             "string"
          ],
          "nameKeyword":[
             "string"
          ],
          "ownerIds":[
             "string"
          ],
          "pageRequest":{
             "fieldToDirectionMap":{
             },
             "page":0,
             "size":0
          },
          "sortBy":"string",
          "sortById":"string",
          "tags":[
             "string"
          ],
          "userId":"string",
          "validationStatusCodes":[
             "string"
          ]
       },
       "request_from":"string",
       "request_id":"string"
    }


Response Body Example:

.. code-block:: json

    {
       "content":{
          "active":"string",
          "admin":true,
          "apiTokenHash":"string",
          "bulkUpdate":"string",
          "created":"2018-10-17T19:23:59.691Z",
          "emailId":"string",
          "firstLogin":true,
          "firstName":"string",
          "jwtToken":"string",
          "jwttoken":"string",
          "lastLogin":"2018-10-17T19:23:59.691Z",
          "lastName":"string",
          "loginName":"string",
          "loginPassExpire":true,
          "modified":"2018-10-17T19:23:59.691Z",
          "orgName":"string",
          "password":"string",
          "picture":[
             "string"
          ],
          "publisher":true,
          "role":"string",
          "roleId":"string",
          "status":"string",
          "tags":[
             {
                "tag":"Classification"
             }
          ],
          "updatedRole":"string",
          "updatedRoleId":"string",
          "userAssignedRolesList":[
             {
                "active":false,
                "created":"2018-10-17T19:23:59.692Z",
                "modified":"2018-10-17T19:23:59.692Z",
                "name":"My role",
                "roleId":"12345678-abcd-90ab-cdef-1234567890ab"
             }
          ],
          "userId":"string",
          "userIdList":[
             "string"
          ],
          "userNewRoleList":[
             "string"
          ],
          "userRolesList":{
          },
          "username":"string",
          "verifyToken":"string"
       },
       "error_code":"string",
       "response_body":{
          "active":"string",
          "admin":true,
          "apiTokenHash":"string",
          "bulkUpdate":"string",
          "created":"2018-10-17T19:23:59.692Z",
          "emailId":"string",
          "firstLogin":true,
          "firstName":"string",
          "jwtToken":"string",
          "jwttoken":"string",
          "lastLogin":"2018-10-17T19:23:59.692Z",
          "lastName":"string",
          "loginName":"string",
          "loginPassExpire":true,
          "modified":"2018-10-17T19:23:59.692Z",
          "orgName":"string",
          "password":"string",
          "picture":[
             "string"
          ],
          "publisher":true,
          "role":"string",
          "roleId":"string",
          "status":"string",
          "tags":[
             {
                "tag":"Classification"
             }
          ],
          "updatedRole":"string",
          "updatedRoleId":"string",
          "userAssignedRolesList":[
             {
                "active":false,
                "created":"2018-10-17T19:23:59.692Z",
                "modified":"2018-10-17T19:23:59.692Z",
                "name":"My role",
                "roleId":"12345678-abcd-90ab-cdef-1234567890ab"
             }
          ],
          "userId":"string",
          "userIdList":[
             "string"
          ],
          "userNewRoleList":[
             "string"
          ],
          "userRolesList":{
          },
          "username":"string",
          "verifyToken":"string"
       },
       "response_code":"string",
       "response_detail":"string",
       "status":true,
       "status_code":0
    }


Find User's Solutions
=====================


``POST http://<host and optionally port>"/user/solutions``


Search the solutions for a user filtered according to the parameters.

Request Body Example:

.. code-block:: json

    {
       "request_body":{
          "accessTypeCodes":[
             "string"
          ],
          "active":true,
          "authorKeyword":"string",
          "descriptionKeyword":[
             "string"
          ],
          "modelTypeCodes":[
             "string"
          ],
          "nameKeyword":[
             "string"
          ],
          "ownerIds":[
             "string"
          ],
          "pageRequest":{
             "fieldToDirectionMap":{
             },
             "page":0,
             "size":0
          },
          "sortBy":"string",
          "sortById":"string",
          "tags":[
             "string"
          ],
          "userId":"string",
          "validationStatusCodes":[
             "string"
          ]
       },
       "request_from":"string",
       "request_id":"string"
    }


Response Body Example:

.. code-block:: json

    {
       "content":{
          "active":"string",
          "admin":true,
          "apiTokenHash":"string",
          "bulkUpdate":"string",
          "created":"2018-10-17T19:23:59.691Z",
          "emailId":"string",
          "firstLogin":true,
          "firstName":"string",
          "jwtToken":"string",
          "jwttoken":"string",
          "lastLogin":"2018-10-17T19:23:59.691Z",
          "lastName":"string",
          "loginName":"string",
          "loginPassExpire":true,
          "modified":"2018-10-17T19:23:59.691Z",
          "orgName":"string",
          "password":"string",
          "picture":[
             "string"
          ],
          "publisher":true,
          "role":"string",
          "roleId":"string",
          "status":"string",
          "tags":[
             {
                "tag":"Classification"
             }
          ],
          "updatedRole":"string",
          "updatedRoleId":"string",
          "userAssignedRolesList":[
             {
                "active":false,
                "created":"2018-10-17T19:23:59.692Z",
                "modified":"2018-10-17T19:23:59.692Z",
                "name":"My role",
                "roleId":"12345678-abcd-90ab-cdef-1234567890ab"
             }
          ],
          "userId":"string",
          "userIdList":[
             "string"
          ],
          "userNewRoleList":[
             "string"
          ],
          "userRolesList":{
          },
          "username":"string",
          "verifyToken":"string"
       },
       "error_code":"string",
       "response_body":{
          "active":"string",
          "admin":true,
          "apiTokenHash":"string",
          "bulkUpdate":"string",
          "created":"2018-10-17T19:23:59.692Z",
          "emailId":"string",
          "firstLogin":true,
          "firstName":"string",
          "jwtToken":"string",
          "jwttoken":"string",
          "lastLogin":"2018-10-17T19:23:59.692Z",
          "lastName":"string",
          "loginName":"string",
          "loginPassExpire":true,
          "modified":"2018-10-17T19:23:59.692Z",
          "orgName":"string",
          "password":"string",
          "picture":[
             "string"
          ],
          "publisher":true,
          "role":"string",
          "roleId":"string",
          "status":"string",
          "tags":[
             {
                "tag":"Classification"
             }
          ],
          "updatedRole":"string",
          "updatedRoleId":"string",
          "userAssignedRolesList":[
             {
                "active":false,
                "created":"2018-10-17T19:23:59.692Z",
                "modified":"2018-10-17T19:23:59.692Z",
                "name":"My role",
                "roleId":"12345678-abcd-90ab-cdef-1234567890ab"
             }
          ],
          "userId":"string",
          "userIdList":[
             "string"
          ],
          "userNewRoleList":[
             "string"
          ],
          "userRolesList":{
          },
          "username":"string",
          "verifyToken":"string"
       },
       "response_code":"string",
       "response_detail":"string",
       "status":true,
       "status_code":0
    }


Get Authors of Solutions Revision
=================================


``GET http://<host and optionally port>/solution/{solutionId}/revision/{revisionId}/authors``


Get Authors of Solution Revision.

Response Body Example:

.. code-block:: json

    {
        "status": null,
        "status_code": 0,
        "response_detail": "Author fetched Successfully",
        "response_code": null,
        "response_body": [
            {
                "contact": "string",
                "name": "string"
            }
        ],
        "content": null,
        "error_code": "100"
    }


Add Authors to Solutions Revision
=================================


``PUT http://<host and optionally port>/solution/{solutionId}/revision/{revisionId}/authors``


Add Authors to Solution Revision.

Request Body Example:

.. code-block:: json

    {
        "request_body": {
            "contact": "string",
            "name": "string"
        },
        "request_from": "string",
        "request_id": "string"
    }


Response Body Example:

.. code-block:: json

    {
        "status": null,
        "status_code": 0,
        "response_detail": "Author Added Successfully",
        "response_code": null,
        "response_body": [
            {
                "contact": "string",
                "name": "string"
            }
        ],
        "content": null,
        "error_code": "100"
    }


Add Authors to Solutions Revision
=================================


``PUT http://<host and optionally port>/solution/{solutionId}/revision/{revisionId}/removeAuthor``


Remove Author from Solution Revision.

Request Body Example:

.. code-block:: json

    {
        "request_body": {
            "contact": "string",
            "name": "string"
        },
        "request_from": "string",
        "request_id": "string"
    }


Response Body Example:

.. code-block:: json

    {
        "status": null,
        "status_code": 0,
        "response_detail": "Author Added Successfully",
        "response_code": null,
        "response_body": [
            {
                "contact": "string",
                "name": "string"
            }
        ],
        "content": null,
        "error_code": "100"
    }


Get Documents for a solution Revision
=====================================


``GET http://<host and optionally port>/solution/{solutionId}/revision/{revisionId}/{accessType}/document``


Get Solution Revision Documents.


Response Body Example:

.. code-block:: json

    {
       "status":null,
       "status_code":0,
       "response_detail":"Fetched Documents Successfully",
       "response_code":null,
       "response_body":[
          {
             "created":"2018-10-17T19:23:59.729Z",
             "documentId":"12345678-abcd-90ab-cdef-1234567890ab",
             "modified":"2018-10-17T19:23:59.729Z",
             "name":"user-guide.rst",
             "size":65536,
             "uri":"http://nexus.company.com/group/version/document_name",
             "userId":"12345678-abcd-90ab-cdef-1234567890ab",
             "version":"v1.0"
          }
       ],
       "content":null,
       "error_code":"100"
    }


Add Document for a solution Revision
=====================================


``POST http://<host and optionally port>/solution/{solutionId}/revision/{revisionId}/{accessType}/document``


Add Solution Revision Documents.


Request Body Example: Binary File


Response Body Example:

.. code-block:: json

    {
       "status":null,
       "status_code":0,
       "response_detail":"Document Added Successfully",
       "response_code":null,
       "response_body":{
          "created":"2018-10-17T19:23:59.734Z",
          "documentId":"12345678-abcd-90ab-cdef-1234567890ab",
          "modified":"2018-10-17T19:23:59.734Z",
          "name":"user-guide.rst",
          "size":65536,
          "uri":"http://nexus.company.com/group/version/document_name",
          "userId":"12345678-abcd-90ab-cdef-1234567890ab",
          "version":"v1.0"
       },
       "content":null,
       "error_code":"100"
    }


Add/Update Solutions Revision Description
=========================================


``POST http://<host and optionally port>/solution/revision/{revisionId}/{accessType}/description``


Add/Update Solution Revision Description.

Request Body Example:

.. code-block:: json

    {
        "request_body": {
            "accessTypeCode": "string",
            "description": "string",
            "revisionId": "string"
        },
        "request_from": "string",
        "request_id": "string"
    }


Response Body Example:

.. code-block:: json

    {
        "status": null,
        "status_code": 0,
        "response_detail": "Description Fetched Successfully",
        "response_code": null,
        "response_body": {
            "description": "string",
            "revisionId": "string",
            "accessTypeCode": "string"
        },
        "content": null,
        "error_code": "100"
    }


Get Solution Revision Description
=================================


``GET http://<host and optionally port>/solution/revision/{revisionId}/{accessType}/description``


GET Solution Revision Description.


Response Body Example:

.. code-block:: json

    {
        "status": null,
        "status_code": 0,
        "response_detail": "Description Fetched Successfully",
        "response_code": null,
        "response_body": {
            "description": "string",
            "revisionId": "string",
            "accessTypeCode": "string"
        },
        "content": null,
        "error_code": "100"
    }


Remove Document for a solution Revision
=====================================


``DELETE http://<host and optionally port>/solution/{solutionId}/revision/{revisionId}/{accessType}/document/{documentId}``


Remove Solution Revision Documents.


Request Body Example: Binary File


Response Body Example:

.. code-block:: json

    {
       "status":null,
       "status_code":0,
       "response_detail":"Document Added Successfully",
       "response_code":null,
       "response_body":{
          "    ":"2018-10-18T19:23:59.739Z",
          "documentId":"string",
          "modified":"2018-10-18T19:23:59.739Z",
          "name":"user-guide.rst",
          "size":65536,
          "uri":"http://nexus.company.com/group/version/document_name",
          "userId":"string",
          "version":"string"
       },
       "content":null,
       "error_code":"100"
    }


==================================
Push And Pull Solution Service API
==================================


Download Solution Artifact
==========================


``GET http://<host and optionally port>/downloads/{solutionId}``


Download the dockerized Image Artifact of the Machine Learning Solution

Request Body Example:

.. code-block:: json

    {
        "request_body": {
            "solutionId" : "d1ef3a94-a5e0-482b-983f-ed4f25420b00",
            "artifactId" : "8f5bfb9f-c6ff-4860-a402-56c02fed040d",
            "revisionId" : "dd1c3fba-2ddb-4f0a-b864-da70642be71c",
            "userId"	 : "<userId>"
        }
    }


Response Body Example:

Binary file will be downloaded.


Upload the Model/Solution
==========================


``POST http://<host and optionally port>/model/upload/{userId}``


Upload the model to the server
	
Request Body Example:

Binary zip file 
	
Response Body Example:

Response Code : 200 Ok 

Model will be uploaded.

If the file is not in zip format or does not contain required files we will get error like below.
	 
Zip File does not contain required files D:\Docs\solution.zip

Zip File Required. Original File : D:\Docs\BLUEPRINT-E55671D6-A40E-4137-86FC-EDAE372AAAD3-1.0.1.json



Download the Solution Revision Document
=======================================


``GET http://<host and optionally port>/solution/revision/document/{documentId}``


Download the documents of the Solution.


Response Body Example:

The Supporting document of ML Solution will be downloaded.



============================
Publish Solution Service API
============================


Publish Solution
================


``PUT http://<host and optionally port>/publish/{solutionId}``


Publishes a given SolutionId for userId with selected visibility.

Request Body Example:

.. code-block:: json

    {
        "request_body": {
            "solutionId" : "345949aa-ecd4-418f-9257-88ed008ec303",
            "visibility" : "PB",
            "userId" : "<userId>",
            "revisionId" : "2cd69738-9d03-4c43-9c65-9f7d65abee23"
       }
    }


Response Body Example:

.. code-block:: json

   {
       "status": null,
       "status_code": 0,
       "response_detail": "<trackingId>",
       "response_code": null,
       "response_body": null,
       "content": null,
       "error_code": "100"
   }


=================
Admin Service API
=================


Get User Carousal Configuration
===============================


Gets list of Site configuration filtered with user's preferred tags.


``GET http://<host and optionally port>/admin/user/carouseConfig``


Response Body Example:

.. code-block:: json

    {
       "status":null,
       "status_code":0,
       "response_detail":"getUserCarousalConfiguration fetched Successfully",
       "response_code":null,
       "response_body":[
          {
             "0":{
                "name":"Test Slide",
                "headline":"Test Slide ok",
                "supportingContent":"<p>Just for test</p>",
                "textAling":"left",
                "number":"0",
                "slideEnabled":"true",
                "tagName":"13",
                "bgImageUrl":"ML_solution.jpg",
                "InfoImageUrl":"Layer_2.png",
                "links":{
                   "enableLink":"true",
                   "primary":{
                   },
                   "secondary":{
                   }
                }
             }
          },
          {
             "1":{
                "name":"Test",
                "headline":"Testing",
                "supportingContent":"<p>TEst</p>",
                "textAling":"left",
                "number":"1",
                "slideEnabled":"false",
                "tagName":"1234",
                "links":{
                   "enableLink":true,
                   "primary":{
                   },
                   "secondary":{
                   }
                }
             }
          },
          {
             "2":{
                "name":"test14Sep18",
                "headline":"test",
                "supportingContent":"<p>test14Sep18</p>",
                "textAling":"left",
                "number":"2",
                "slideEnabled":"false",
                "tagName":"1WA_tag",
                "links":{
                   "primary":{
                      "label":"test",
                      "address":"marketPlace"
                   },
                   "secondary":{
                   }
                }
             }
          },
          {
             "3":{
                "name":"slide 5",
                "headline":"slide 5",
                "supportingContent":"<p>dfsdfdfsdf</p>",
                "textAling":"right",
                "number":"3",
                "slideEnabled":"false",
                "tagName":"1ww",
                "links":{
                   "primary":{
                      "address":"modelerResource"
                   },
                   "secondary":{
                      "address":"marketPlace"
                   }
                }
             }
          }
       ],
       "content":null,
       "error_code":"100"
    }


Enabling SignUp Service 
=======================


``GET http://<host and optionally port>/admin/signup/enabled``


Get SignUp Enabled and verify and return Success

Response Body Example:

.. code-block:: json

    {
        "status": true,
        "status_code": 200,
        "response_detail": "Success",
        "response_code": null,
        "response_body": "true",
        "content": null,
        "error_code": null
    }


Authentication Service
======================


``POST http://<host and optionally port>/auth/jwtToken``


This api is used to validate user by accepting there emailid or username.return success & JWT token if account created successfully  else an error message is returned:


Request Body Example:

.. code-block:: json

    {
            "request_body": {
            "password": <"Password">
            "username": "<"Username">
         }
    }


Response Body Example:

.. code-block:: json

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


Login service using Authentication token
========================================


``POST http://<host and optionally port>/auth/login``


Allows User to login to the Platform using emailId or username. Returns Success & JWT Token if Account created successfully; else an error message is returned.

Request Body Example:

.. code-block:: json

    {
            "request_body": {
            "password": <"Password">
            "username": "<"Username">
         }
    }


Response Body Example:

.. code-block:: json

        {
       "loginPassExpire":false,
       "userAssignedRolesList":null,
       "firstLogin":false,
       "firstName":"Test",
       "lastName":"User",
       "emailId":"user@acumos.com",
       "username":<"username">,
       "password":null,
       "active":"true",
       "lastLogin":1539774948356,
       "created":1520526238000,
       "modified":null,
       "userId":"<userID>",
       "loginName":null,
       "orgName":null,
       "picture":null,
       "jwttoken":null,
       "role":null,
       "roleId":null,
       "updatedRole":null,
       "updatedRoleId":null,
       "userIdList":null,
       "userNewRoleList":null,
       "userRolesList":null,
       "bulkUpdate":null,
       "apiTokenHash":null,
       "verifyToken":null,
       "status":null,
       "tags":null,
       "jwtToken":null,
       "admin":false,
       "publisher":false
    }


Operation Name - Add User 
=========================

Add User from Admin


Request Body Example:

.. code-block:: json

 {

  "request_body": {
    "admin": "true"	
    "emailId": "<emailid>",
    "firstName": "<firstname>",
    "jwtToken": "<jwttoken>",
    "jwttoken": "<jwttoken>",
    "lastLogin": "2018-10-19T13:52:25.104Z",
    "lastName": "<lastname>",
    "loginName": "<loginname>",
    "loginPassExpire": true,
    "password": "<password>",
    "userId": "<userid>"
    "username": "<username>",
  }
 }

Response Body Example:

.. code-block:: json

 {
  "active": false,
  "created": "2018-10-19T13:52:25.097Z",
  "modified": "2018-10-19T13:52:25.097Z",
  "name": "My role",
  "roleId": "12345678-abcd-90ab-cdef-1234567890ab"
 }



Operation Name - create Config 
==============================

Create site configuration

Request Body Example:

.. code-block:: json

 {
  "request_body": {
    "configKey": "site_config_key_1",
    "configValue": "{ \"tag\" : \"value\" }",
    "userId": "<userid>"
  }
 }

Response Body Example:

.. code-block:: json

 {
  "configKey": "site_config_key_1",
  "configValue": "{ \"tag\" : \"value\" }",
  "created": "2018-10-19T13:52:25.117Z",
  "modified": "2018-10-19T13:52:25.117Z",
  "userId": "<userid>"
 }




Operation Name - Remove Config 
==============================


Remove Site Configuraion

Request Body Example:

.. code-block:: json

  {
  "request_body": {
  "configKey": "<configKey>"
  }
  
Response Body Example:

.. code-block:: json

  {
  "content": {},
  "error_code": "No Pages Found",
  "response_body": {},
  "status": true,
  "status_code": 0
}
  
  
Operation Name - List of Config
===============================

  
Gets list of Site configuration
  
Request Body Example:

.. code-block:: json

 {
  "request_body": { 
  "configKey": "<configKey>"
 }
}

 Response Body Example:

.. code-block:: json

  [ 
  {
    "configKey": "<configKey",
    "configValue": "<configValue>",
    "created": "2018-10-19T13:52:25.130Z",
    "modified": "2018-10-19T13:52:25.130Z",
    "userId": "<userId>"
  }
 ]


Operation Name - Update Config
==============================

 
Update site configuration
 
 
Request Body Example:

.. code-block:: json
 
  {

  "request_body": {
  
  }
  }
   
Response Body Example:

.. code-block:: json

  {
  "status": true, 
  "status_code": 200,
  "response_detail": "Success",
  "response_code": null,
  "response_body": "http://www.mycompany.com/",
  "content": null,
  "error_code": null
  }



Operation Name - Get Dashboard 
==============================

 
Get Dashboard URL
 
Request Body Example:

.. code-block:: json
 
 {
  "request_body": { 
  "fieldToDirectionMap": {},
  "page": 0,
  "size": 0
 }
}
    
 Response Body Example:

.. code-block:: json
  
  { 
  "status": null, 
  "status_code": 0,
  "response_detail": "Peers fetched Successfully",
  "response_code": null,
  "response_body": {
    "content": [
      { 
        "created": 1533653577000,
        "modified": 1533670420000,
        "peerId": "<peerId>",
        "name": "<name>",
        "subjectName": "sss",
        "description": "test",
        "apiUrl": "<apiURL>",
        "webUrl": "<webURL>",
        "contact1": "<Contact1>",
        "statusCode": "DC",
        "validationStatusCode": "PS",
        "local": false,
        "self": false
      },	  
      {
        "created": 1537798150000,
        "modified": 1538387700000,
        "peerId": "<peerID>",
        "name": "<name>",
        "subjectName": "www.NeerajTestPeer001.com",
        "description": "",
        "apiUrl": "<apiURL>",
        "webUrl": "<webURL>",
        "contact1": "<Contact1>",
        "statusCode": "DC",
        "validationStatusCode": "PS",
        "local": false,
        "self": false
      },
      {
        "created": 1533661440000,
        "modified": 1533670411000,
        "peerId": "<peerid>",
        "name": "Test",
        "subjectName": "http://test.com",
        "description": "",
        "apiUrl": "<apiURL>",
        "webUrl": "<webURL>",
        "contact1": "<contact1>",
        "statusCode": "DC",
        "validationStatusCode": "PS",
        "local": false,
        "self": false
     },  
  "content": null,
  "error_code": "100"
    }


Operation Name - Get Paginated List
===================================



Gets paginated list of All Peers

 
Request Body Example:

.. code-block:: json

 {
  "request_body": {
  "fieldToDirectionMap": {}, 
  "page": 5, 
  "size": 0
 }
 }


Response Body Example:

.. code-block:: json

 {
    "apiUrl": "<apiURL>",
    "contact1": "<Contact1>",
    "created": "2018-10-19T13:52:12.070Z",
    "description": "Page created SuccessFully",
    "local": true,
    "modified": "2018-10-19T13:52:12.070Z",
    "name": "<name>",	
    "peerId": "<PeerID>",
    "self": true,	
    "statusCode": "AC",	
    "subjectName": "peer.company.com"	
    "validationStatusCode": "NV",	
    "webUrl": "<WebURL>"	
 }
]

 
 
Operation Name - Add Peer 
=========================



Add a new peer


Request Body Example:

.. code-block:: json
  
  {
  
  "request_body": {  
    "apiUrl": "<apiurl>",	
    "contact1": "<contact1>",	
    "created": "2018-10-19T13:52:25.201Z",	
    "modified": "2018-10-19T13:52:25.201Z",	
    "name": "<name>",	
    "peerId": "<peerId>",	
    "self": true,	
    "statusCode": "AC",	
    "subjectName": "peer.company.com",	
    "validationStatusCode": "NV",	
    "webUrl": "string"	
  }
}

 Response Body Example:

.. code-block:: json

 {
  "apiUrl": "<apiurl>",
  "contact1": "<contact1>",
  "created": "2018-10-19T13:52:25.198Z",
   "description": "Peers created",
  "local": true, 
  "modified": "2018-10-19T13:52:25.198Z",
  "name": "<name>",
  "peerId": "<peerId>", 
  "self": true,
  "statusCode": "AC", 
  "subjectName": "peer.company.com", 
  "validationStatusCode": "NV",
  "webUrl": "<webUrl>" 
 }

 
Operation Name - Delete Peer 
============================



Remove Peer Subscription

Request Body Example:

.. code-block:: json

 {
  "request_body": {  
  "subID":"<subID>" 
  }
  }

Response Body Example:

.. code-block:: json
  
  {
  "content": {},  
  "error_code": "Not created ID",  
  "response_body": {}, 
  "response_code": "<response Code>",  
  "response_detail": "<response Details>", 
  "status": true,
  "status_code": 0
}


Operation Name - Get Requests 
=============================



Gets a list of Requests

Request Body Example:

.. code-block:: json

  {
  "request_body": {  
  "fieldToDirectionMap": {},
    "page": <pageNo>, 
  "size": <size>  
  }
 }
  
Response Body Example:

.. code-block:: json
 
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
      "commentId": "<commentID>",	  
      "created": "2018-10-19T13:52:12.152Z",
      "modified": "2018-10-19T13:52:12.152Z",	 
      "parentId": "<parentID>",	  
      "text": "Best model ever",	  
      "threadId": "12345678-abcd-90ab-cdef-1234567890ab",	  
      "userId": "<userID>"	  
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
  "jwtToken": "string",  
  "last": true,
  "modelsSharedWithUser": [
    {
      "active": false,
      "created": "2018-10-19T13:52:12.152Z",  
      "description": "string",	  
      "metadata": "string",	  
      "modelTypeCode": "CL",	  
      "modified": "2018-10-19T13:52:12.152Z",	  
      "name": "My solution",	  
      "origin": "string",	  
      "picture": [	  
       "string"
      ],	  
      "solutionId": "<SolutionID>",	  
      "sourceId": "<SourceID>",	  
      "tags": [	  
        {		
          "tag": "Classification"		  
        }		
      ],	  
      "toolkitTypeCode": "SK",	  
      "userId": "<userID>",	  
      "webStats": {
	          "downloadCount": 0,		
        "featured": true,		
        "lastDownload": "2018-10-19T13:52:12.152Z",		
        "ratingAverageTenths": 0,		
        "ratingCount": 0,		
        "solutionId": "string",		
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
	      "action": "string",	  
      "date": "2018-10-19T13:52:12.152Z",	  
      "requestId": "string",	  
      "requestType": "string",	  
      "requestedDetails": "string",	  
      "sender": "string",	  
      "status": "string"	  
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
        "created": "2018-10-19T13:52:12.152Z",		
        "modified": "2018-10-19T13:52:12.153Z",		
        "name": "My role",		
        "roleId": "12345678-abcd-90ab-cdef-1234567890ab"		
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
      "revisionId": "<revisionID>",	  
      "solutionId": "<solutionID>",	  
      "threadId": "<threadID>",	  
      "title": "<title>"	  
    }	
  ], 
  "totalElements": 0, 
  "totalPages": 0,  
  "userList": [ 
  {
      "active": "string",
      "admin": true, 
      "apiTokenHash": "<apitoken",  
      "bulkUpdate": "<bulkUpdate>",  
      "created": "2018-10-19T13:52:12.153Z",  
      "emailId": "string",  
      "firstLogin": true,	  
      "firstName": "string",	  
      "jwtToken": "string",	  
      "jwttoken": "string",	  
      "lastLogin": "2018-10-19T13:52:12.153Z",	  
      "lastName": "string",	  
      "loginName": "string",	  
      "loginPassExpire": true,	  
      "modified": "2018-10-19T13:52:12.153Z",	  
      "orgName": "string",	  
      "password": "string",	  
      "picture": [	  
        "string"		
      ],
      "publisher": true,	  
      "role": "string",	  
      "roleId": "string",	  
      "status": "string",	  
      "tags": [	  
        {		
          "tag": "Classification"		  
        }		
      ],  
      "userRolesList": {},	  
      "username": "<username>",	  
      "verifyToken": "<token>"	  
    }	
  ]  
 }


Operation Name  - Logout Service
================================



Allows Users to logout to the Platform . Returns Success & JWT Token if Account created successfully; else an error message is returned


Request Body Example:

.. code-block:: json

  {
 "request_body": {
   "password": "<password>",
   "username": "<username>"  
  }
 }

Response Body Example:

.. code-block:: json

 {
  "admin": true, 
  "firstLogin": true,  
  "jwtToken": "<jwttoken>",  
  "loginPassExpire": true,  
  "message": "Successfull logged out", 
  "publisher": true, 
  "resultCode": 0,
  "userAssignedRolesList": [  
    {
      "active": false,	  
      "created": "2018-10-22T09:35:54.399Z",  
      "modified": "2018-10-22T09:35:54.399Z",
      "name": "<name>",
      "roleId": "<roleID>"
   }
  ]
}


Operation Name  - check validation Status
=========================================



Provide the Validation status for the application

Request Body Example:

.. code-block:: json

  {
 "request_body": {
 
  }
 }

Response Body Example:

.. code-block:: json

 {
  "content": {},
  "error_code": "string",
  "response_body": {},
  "response_code": "<response>",
  "response_detail": "<detail>",
  "status": true,
  "status_code": 0
}
 
Operation Name  - Logout Service
================================

Allows Users to logout to the Platform . Returns Success & JWT Token if Account created successfully; else an error message is returned

Request Body Example:

.. code-block:: json

 {
 "request_body":{ 
   "password": "<password>",
   "username": "<username>" 
 }
 }

Response Body Example:

.. code-block:: json

 {
  "admin": true,
  "firstLogin": true,
  "jwtToken": "<jwttoken>",
  "loginPassExpire": true,
  "message": "Successfull logged out",
  "publisher": true,
  "resultCode": 0
  "userAssignedRolesList": [
    {
      "active": false,
      "created": "2018-10-22T09:35:54.399Z",
      "modified": "2018-10-22T09:35:54.399Z",
      "name": "<name>",
      "roleId": "<roleID>" 
    }
   ] 
 }


Operation Name  - Validation Status
===================================


Provide the Validation status for the application


Request Body Example:

.. code-block:: json


 {
 "request_body": {
 
 }
 }
 
Response Body Example:

.. code-block:: json

 
 {
  "content": {},
  "error_code": "string",
  "response_body": {},
  "response_code": "<response>",
  "response_detail": "<detail>",
  "status": true,
  "status_code": 0
  }
 

===============
Gateway Service
===============


Ping Gateway
============


``GET http://<host and optionally port>/gateway/ping/{peerId}``


Checks the connection to a gateway instance.


Response Body Example:

.. code-block:: json

    {
        "content": {},
        "error_code": "string",
        "response_body": {},
        "response_code": "string",
        "response_detail": "string",
        "status": true,
        "status_code": 400
    }


Gateway Solutions
=================


``POST http://<host and optionally port>/gateway/solutions``


Fetches all solutions from a gateway given provided categories or toolkit type.

Request Body Example:

.. code-block:: json

    {
        "peerSubscription" : {
            "request_body": {
                "accessType": "PB",
                "created": "2018-10-17T19:34:23.633Z",
                "maxArtifactSize": 0,
                "modified": "2018-10-17T19:34:23.633Z",
                "options": "{ \"jsonTag\" : \"jsonValue\" }",
                "peerId": "<peer id>",
                "processed": "1521202458867",
                "refreshInterval": 60,
                "scopeType": "RF",
                "selector": "{ \"modelTypeCode\" : \"CL\" }",
                "subId": 0,
                  "userId": "<user id>"
            },
            "request_from": "string",
            "request_id": "string"
        }
    }


Response Body Example:

.. code-block:: json

    {
        [
            {
                "accessType": "string",
                "active": true,
                "commentId": "string",
                "commentsCount": 0,
                "companyModelCount": 0,
                "created": "2018-10-17T19:34:23.749Z",
                "deletedModelCount": 0,
                "description": "string",
                "downloadCount": 0,
                "errorDetails": "string",
                "latestRevisionId": "string",
                "loginName": "string",
                "metadata": "string",
                "modelType": "string",
                "modelTypeName": "string",
                "modified": "2018-10-17T19:34:23.749Z",
                "name": "string",
                "onboardingStatusFailed": true,
                "ownerId": "string",
                "ownerListForSol": [
                    {
                        "active": "string",
                        "admin": true,
                        "apiTokenHash": "string",
                        "bulkUpdate": "string",
                        "created": "2018-10-17T19:34:23.749Z",
                        "emailId": "string",
                        "firstLogin": true,
                        "firstName": "string",
                        "jwtToken": "string",
                        "jwttoken": "string",
                        "lastLogin": "2018-10-17T19:34:23.749Z",
                        "lastName": "string",
                        "loginName": "string",
                        "loginPassExpire": true,
                        "modified": "2018-10-17T19:34:23.749Z",
                        "orgName": "string",
                        "password": "string",
                        "picture": [
                            "string"
                        ],
                        "publisher": true,
                        "role": "string",
                        "roleId": "string",
                        "status": "string",
                        "tags": [
                            {
                                "tag": "Classification"
                            }
                        ],
                        "updatedRole": "string",
                        "updatedRoleId": "string",
                        "userAssignedRolesList": [
                            {
                                "active": false,
                                "created": "2018-10-17T19:34:23.750Z",
                                "modified": "2018-10-17T19:34:23.750Z",
                                "name": "My role",
                                "roleId": "<role id>"
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
                        "username": "string",
                        "verifyToken": "string"
                    }
                ],
                "ownerName": "string",
                "pageNo": 0,
                "pendingApproval": true,
                "picture": [
                    "string"
                ],
                "privateModelCount": 0,
                "publicModelCount": 0,
                "publisher": "string",
                "ratingAverageTenths": 0,
                "ratingCount": 0,
                "refreshInterval": 0,
                "revisions": [
                    {
                        "accessTypeCode": "PB",
                        "authors": "My name\tMy contact",
                        "created": "2018-10-17T19:34:23.750Z",
                        "description": "string",
                        "metadata": "string",
                        "modified": "2018-10-17T19:34:23.750Z",
                        "origin": "<origin url>",
                        "publisher": "My company",
                        "revisionId": "<revision id>",
                        "solutionId": "<solution id>",
                        "sourceId": "<source id>",
                        "userId": "<user id>",
                        "validationStatusCode": "NV",
                        "version": "v1.0"
                    }
                ],
                "selector": "string",
                "size": 0,
                "solutionId": "string",
                "solutionRating": 0,
                "solutionRatingAvg": 0,
                "solutionTag": "string",
                "solutionTagList": [
                    {
                        "tag": "Classification"
                    }
                ],
                "sortingOrder": "string",
                "threadId": "string",
                "threadList": [
                    {
                        "revisionId": "<revision id>",
                        "solutionId": "<solution id>",
                        "threadId": "<thread id>",
                        "title": "string"
                    }
                ],
                "tookitType": "string",
                "tookitTypeName": "string",
                "validationStatusCode": "string",
                "viewCount": 0
            }
        ]
    }


Get Gateway Solution
====================


``GET http://<host and optionally port>/gateway/{solutionId}/solution/{peerId}``


Fetches a specific solution from a gateway.


Request Body Example:

.. code-block:: json

    {
        "solutionId" : "<solution id>",
        "peerId" : "<peer id>"
    }


Response Body Example:

.. code-block:: json

    {
        "accessType": "string",
        "active": true,
        "commentId": "string",
        "commentsCount": 0,
        "companyModelCount": 0,
        "created": "2018-10-17T19:34:23.749Z",
        "deletedModelCount": 0,
        "description": "string",
        "downloadCount": 0,
        "errorDetails": "string",
        "latestRevisionId": "string",
        "loginName": "string",
        "metadata": "string",
        "modelType": "string",
        "modelTypeName": "string",
        "modified": "2018-10-17T19:34:23.749Z",
        "name": "string",
        "onboardingStatusFailed": true,
        "ownerId": "string",
        "ownerListForSol": [
            {
                "active": "string",
                "admin": true,
                "apiTokenHash": "string",
                "bulkUpdate": "string",
                "created": "2018-10-17T19:34:23.749Z",
                "emailId": "string",
                "firstLogin": true,
                "firstName": "string",
                "jwtToken": "string",
                "jwttoken": "string",
                "lastLogin": "2018-10-17T19:34:23.749Z",
                "lastName": "string",
                "loginName": "string",
                "loginPassExpire": true,
                "modified": "2018-10-17T19:34:23.749Z",
                "orgName": "string",
                "password": "string",
                "picture": [
                    "string"
                ],
                "publisher": true,
                "role": "string",
                "roleId": "string",
                "status": "string",
                "tags": [
                    {
                        "tag": "Classification"
                    }
                ],
                "updatedRole": "string",
                "updatedRoleId": "string",
                "userAssignedRolesList": [
                    {
                        "active": false,
                        "created": "2018-10-17T19:34:23.750Z",
                        "modified": "2018-10-17T19:34:23.750Z",
                        "name": "My role",
                        "roleId": "<role id>"
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
                "username": "string",
                "verifyToken": "string"
            }
        ],
        "ownerName": "string",
        "pageNo": 0,
        "pendingApproval": true,
        "picture": [
            "string"
        ],
        "privateModelCount": 0,
        "publicModelCount": 0,
        "publisher": "string",
        "ratingAverageTenths": 0,
        "ratingCount": 0,
        "refreshInterval": 0,
        "revisions": [
            {
                "accessTypeCode": "PB",
                "authors": "My name\tMy contact",
                "created": "2018-10-17T19:34:23.750Z",
                "description": "string",
                "metadata": "string",
                "modified": "2018-10-17T19:34:23.750Z",
                "origin": "<origin url>",
                "publisher": "My company",
                "revisionId": "<revision id>",
                "solutionId": "<solution id>",
                "sourceId": "<source id>",
                "userId": "<user id>",
                "validationStatusCode": "NV",
                "version": "v1.0"
            }
        ],
        "selector": "string",
        "size": 0,
        "solutionId": "string",
        "solutionRating": 0,
        "solutionRatingAvg": 0,
        "solutionTag": "string",
        "solutionTagList": [
            {
                "tag": "Classification"
            }
        ],
        "sortingOrder": "string",
        "threadId": "string",
        "threadList": [
            {
                "revisionId": "<revision id>",
                "solutionId": "<solution id>",
                "threadId": "<thread id>",
                "title": "string"
            }
        ],
        "tookitType": "string",
        "tookitTypeName": "string",
        "validationStatusCode": "string",
        "viewCount": 0
    }




==================
LF CAS Service API
==================


Get cas/enabled
===============


``GET http://<host and optionally port>/cas/enabled``


This GET API is used to check if CAS (Linux Foundation) login is enabled or not.

Response Body Example:

.. code-block:: json

    {
        "status": true,
        "status_code": 200,
        "response_detail": "Success",
        "response_code": null,
        "response_body": "true",
        "content": null,
        "error_code": null
    }


Get cas/enabled
===============


``GET http://<host and optionally port>/cas/serviceValidate``


Gets the User Object from CAS api.

Response Body Example:

.. code-block:: json

    {
        "status":null,
        "status_code":200,
        "response_detail":"Validation status updated Successfully",
        "response_code":null,
        "response_body":null,
        "content":{
            "loginPassExpire":false,
            "userAssignedRolesList":null,
            "firstLogin":false,
            "firstName":"<firstName>",
            "lastName":"<lastName>",
            "emailId":"<emaiilid@email.com>",
            "username":"<username>",
            "password":null,
            "active":"true",
            "lastLogin":null,
            "created":1539959157000,
            "modified":null,
            "userId":"<userId>",
            "loginName":null,
            "orgName":null,
            "picture":null,
            "jwttoken":"jwtToken",
            "role":null,
            "roleId":null,
            "updatedRole":null,
            "updatedRoleId":null,
            "userIdList":null,
            "userNewRoleList":null,
            "userRolesList":null,
            "bulkUpdate":null,
            "apiTokenHash":"450756ad8e40467caeaef008ac988544",
            "verifyToken":null,
            "status":"Active",
            "tags":[
            ],
            "jwtToken":null,
            "admin":false,
            "publisher":false
        },
        "error_code":null
    }


Project Tools
=============

This micro service is a Spring-Boot application that for
Portal Market Backend on the Acumos platform.
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