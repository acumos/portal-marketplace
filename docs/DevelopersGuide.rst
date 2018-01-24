==================================
Acumos Portal MarketPlace Document
==================================

1. Introduction**

========================

This is the developers guide to MarketPlace.

**\**1.1 What is MarketPlace\?*\***

Acumos provides a toolkit-independent 'App Store', called
a Marketplace for:

1. Data-powered decision making and artificial intelligence software
models.

2. It provides a means to securely share AI microservices along with
information on how they perform, such as ratings, popularity statistics
and user-provided reviews to apply crowd sourcing to software
development..

3. The platform provides integration between model developers and
applications in order to automate the process of user feedback,
exception handling and software updates..

**\**1.2 Target Users*\***

This guide is targeted towards the open source user community that:

1. Intends to understand the functionality of the MarketPlace.

**\**1.3 MarketPlace – High level Architecture*\***

.. image:: images/media/image1.png


**\**1.4 MarketPlace Backend API’s*\***

-  .. rubric:: Admin Service
      :name: admin-service

-  Auth Service

-  Market Place Catalog Service

-  Notification Services

-  Oauth User Service

-  Publish Solution Service

-  Push And Pull Solution Service

-  User Role Services

-  User Service Services

-  Validation Status Services

-  Web Based Onboarding Services

**\**1.5 MarketPlace Flow Structure:**

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
          to add review.   Buttons needed “Download” & “Deploy to
          Cloud”. Social Media Sharing options also need to be
          displayed.**Deploy to Cloud should only provide MS Azure
          option.

      6.  Clicking on either of “Download”, “Deploy to Cloud” or “Add
          review” should prompt user to SIGN IN.

      7.  If User is already signed in, then clicking on:

      8.  “Download” should download the Machine Learning Solution to
          user laptop/computer.

      9.  “Deploy to Cloud” should prompt details about MS Azure (Inputs
          TBD \**)

      10. “Add Review Comment” with text in the comment field should add
          the new comment.

   3. **Page Name:** My Models

      11. Under Manage Models Menu, Options available are: “Add new
          Model”, “My Models” “Delete a Model”

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

      16. Under Manage Models Menu, Options available are: “Add new
          Model”, “My Models” “Delete a Model”

      17. **User Authentication Required:** Yes

      18. **Navigation Menu:** Market Place, Manage Models, Docs,
          Notification, My Profile, Log Out

      19. **Page Content:** If User has clicked on newly added Machine
          Learning Solution that does not have any Title/Description etc
          then Machine Learning Solution Landing Page with fields for
          Title, Description, API Usage (Input & Output swagger UI
          format to test API), Images/Videos will be displayed where
          User can add all the information using WYSIWYG editor.

   1. User can save and view the preview of the Solutions like it would
      display on the Market Place.

    4.2 Once Saved, User can then Submit the Solution for publishing to
    Public Market Place or Company Market Place by clicking buttons
    “Publish to Public Market Place” and “Publish to Company Market
    Place”. Clicking on these two buttons will kick off the
    Certification Process**\* which would allow the Solution to be able
    to publish on Company Market Place i.e local Market Place and it
    would also be allowed to be published on Public Market Place.

    4.4User would also be able to Share the Solutions with individuals
    or group or communities within the local Acumos instance i.e Company
    Acumos by clicking on “Share with Team” which will open a pop up to
    lookup for the User/Group/Communities.

    4.4 Certification Process requirements is TBD and once available ,
    the user experience/Wireframes can be discusses later.

1.7 User Account Signup Flow :

.. image:: images/media/image2.png

1.7 User Account Login Flow :|image2|

1.9 Market Place Catalog Flow :

.. image:: images/media/image4.png

1.10 Model Detail Page Flow :

.. image:: images/media/image5.png

-  2. Market Place Catalog Service

**2.1 Operation Name**

    - Solutions

**- Trigger**

This API is used to gets a list of Published Solutions for Market Place
Catalog.

**-** **Request**

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

**- Response **

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

**2.2 Operation Name**

- Get Solution/Model
~~~~~~~~~~~~~~~~~~~~

**- Trigger:**

    Gets a Solution Detail for the given SolutionId. Same API can be
    used for both Solution Owner view as well as General user. API will
    return isOwner as true if the user is owner of the solution.

**-** **Request:**

    {

    “solutionId”: “04cd7d58-16df-4a13-81da-99ca8d5701d3”

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

**2.3 Operation Name**

- Share Solution/Model
~~~~~~~~~~~~~~~~~~~~~~

**- Trigger:**

    `Gets models shared for the given
    userId. <http://localhost:8083/swagger-ui.html#!/market-place-catalog-service-controller/getMySharedModelsUsingGET>`__

**-** **Request:**

    {

    “userId”: “173cad03-7527-42c5-81cc-35bac96cbf05”

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

**2.4 Operation Name**

- Get Rating
~~~~~~~~~~~~

**- Trigger:**

`Gets the rating for the solution given by different
user. <http://localhost:8083/swagger-ui.html#!/market-place-catalog-service-controller/getMySharedModelsUsingGET>`__

**-** **Request:**

    {

    “solutionId”: “06cdcc30-8725-4c2a-98ec-3219f2964206”

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

**2.5 Operation Name**

- Create Favorite
~~~~~~~~~~~~~~~~~

 **- Trigger:** 
~~~~~~~~~~~~~~~~

`Create favorite for
solution <http://localhost:8083/swagger-ui.html#!/market-place-catalog-service-controller/getSearchSolutionsUsingGET>`__
given by the end users.

**-** **Request:**

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
