=========================================
Acumos Portal MarketPlace Developer’s Guide
=========================================

1.	Introduction
========================

         This is the developers guide to MarketPlace. 

**1.1 What is MarketPlace\?**
	Acumos provides a toolkit-independent 'App Store', called a Marketplace for:

	1.	Data-powered decision making and artificial intelligence software models.

	2.	It provides a means to securely share AI microservices along with information on how they perform, such as ratings, popularity statistics and user-provided reviews to apply crowd sourcing to software development..

	3.	The platform provides integration between model developers and applications in order to automate the process of user feedback, exception handling and software updates..
	
**1.2	Target Users**
	This guide is targeted towards the open source user community that:

	1.	Intends to understand the functionality of the MarketPlace.




**1.3 MarketPlace – High level Architecture**
 	.. image:: images/marketplace_architecture.jpg
	: alt:MarketPlace Architecture
	
**1.4 MarketPlace Backend API’s**

•	Admin Service 
•	Auth Service 
•	Market Place Catalog Service 
•	Notification Services
•	Oauth User Service 
•	Publish Solution Service 
•	Push And Pull Solution Service 
•	User Role Services
•	User Service Services
•	Validation Status Services
•	Web Based Onboarding Services

**1.5 MarketPlace Flow Structure:
1.5.1	Page Name: Acumos Home Screen
1.	User Authentication Required: NO
2.	Page Visibility to User: ALL
3.	Navigation Menu: Market Place, Manage Models, Docs, SIGN IN , SIGN UP
4.	Page Content: Featured Machine Learning Models/Solutions along with option to view all Solutions.
5.	When User open Acumos Page, He/she will be presented with Acumos Home Screen with Featured Machine Learning Solutions in catalog Format (Tiles)

1.5.2	Page Name: Model/Solution Landing Page
1	Navigation: Acumos Home -> Market Place -> Model/Solution Landing Page
2	User Authentication Required: NO (Read Only), Yes (For Downloads, Deploy and to add Review Comment)
3	Page Visibility: ALL
4	Navigation Menu: Market Place, Manage Models, Docs, SIGN IN, SIGN UP
5	Page Content: Machine Learning Solution Landing Page with Title, Description, API Usage (Input & Output swagger UI format to test API), Images/Videos. Bottom of the screen should display ratings, reviews from other users and options to add review.   Buttons needed “Download” & “Deploy to Cloud”. Social Media Sharing options also need to be displayed.**Deploy to Cloud should only provide MS Azure option.
6	Clicking on either of “Download”, “Deploy to Cloud” or “Add review” should prompt user to SIGN IN.
7	If User is already signed in, then clicking on:
8	“Download” should download the Machine Learning Solution to user laptop/computer.
9	“Deploy to Cloud” should prompt details about MS Azure (Inputs TBD **)
10	“Add Review Comment” with text in the comment field should add the new comment.

1.5.3	Page Name: My Models
1	Under Manage Models Menu, Options available are: “Add new Model”, “My Models” “Delete a Model”
2	Navigation: Acumos Home -> Manage Models -> My Models
3	User Authentication Required: Yes
4	Navigation Menu: Market Place, Manage Models, Docs, Notification, My Profile, Log Out
5	Page Content: Machine Learning Solutions and Composite Solutions are displayed in a catalog format. Icons on these solutions should allow to distinguish Single Modelled Solutions, Composite Solutions, Unpublished, published (Public Market Place & Company Market Place) as well as newly created Solutions which does not have any title/description etc.
1.5.4	Page Name: Manage Models - My Models - Model Landing Page
1	Under Manage Models Menu, Options available are: “Add new Model”, “My Models” “Delete a Model”
2	User Authentication Required: Yes
3	Navigation Menu: Market Place, Manage Models, Docs, Notification, My Profile, Log Out
4	Page Content: If User has clicked on newly added Machine Learning Solution that does not have any Title/Description etc then Machine Learning Solution Landing Page with fields for Title, Description, API Usage (Input & Output swagger UI format to test API), Images/Videos will be displayed where User can add all the information using WYSIWYG editor.
4.1	User can save and view the preview of the Solutions like it would display on the Market Place.
4.2 Once Saved, User can then Submit the Solution for publishing to Public Market Place or Company Market Place by clicking buttons “Publish to Public Market Place” and “Publish to Company Market Place”. Clicking on these two buttons will kick off the Certification Process*** which would allow the Solution to be able to publish on Company Market Place i.e local Market Place and it would also be allowed to be published on Public Market Place.
4.4User would also be able to Share the Solutions with individuals or group or communities within the local Acumos instance i.e Company Acumos by clicking on “Share with Team” which will open a pop up to lookup for the User/Group/Communities.
4.4 Certification Process requirements is TBD and once available , the user experience/Wireframes can be discusses later.


1.7 User Account Signup Flow :
	.. image:: images/Signup_Flow.jpg
	:alt: User Account Signup Flow
 
1.8 User Account Login Flow : 
	.. image:: images/Login_Flow.jpg
	: alt: User Account Login Flow


1.9 Market Place Catalog Flow :
	.. image:: images/Catalog_Flow.jpg
	: alt : Market Place Catalog Flow





1.10 Model Detail Page Flow :
	.. image:: images/Model_Detail_Page_Flow.jpg
	: alt : Model Detail Page Flow


•	2. Market Place Catalog Service 


2.1 Operation Name
- Solutions
- Trigger
This API is used to gets a list of Published Solutions for Market Place Catalog.
- Request

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

- Response 
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



2.2 Operation Name
-  Get Solution/Model

 - Trigger:
Gets a Solution Detail for the given SolutionId. Same API can be used for both Solution Owner view as well as General user. API will return isOwner as true if the user is owner of the solution.

-  Request:

{
“solutionId”:  “04cd7d58-16df-4a13-81da-99ca8d5701d3”
}

- Response:
	
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

2.3 Operation Name
- Share Solution/Model

 - Trigger:
Gets models shared for the given userId.

-  Request:

{
“userId”:  “173cad03-7527-42c5-81cc-35bac96cbf05”
}

- Response:

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

2.4 Operation Name
- Get Rating

 - Trigger:
Gets the rating for the solution given by different user.

-  Request:

{
“solutionId”:  “06cdcc30-8725-4c2a-98ec-3219f2964206”
}

- Response:
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

2.5 Operation Name
- Create Favorite

 - Trigger: 

Create favorite for solution given by the end users.

-  Request:
{
  "request_body": {
   "solutionId": "093b29ea-8d6b-407e-b3e9-4d52964ba902",
   "userId": "173cad03-7527-42c5-81cc-35bac96cbf05"
  }
}
- Response:
{
  "status": null,
  "status_code": 0,
  "response_detail": "Successfully created solution favorite",
  "response_code": null,
  "response_body": null,
  "error_code": "100"
}





2.6 Operation Name
- Create Rating

 - Trigger: 

Create rating for solution given by the end users.

-  Request:
{
  "request_body": {
   "solutionId": "093b29ea-8d6b-407e-b3e9-4d52964ba902",
   "userId": "173cad03-7527-42c5-81cc-35bac96cbf05"
  }
}
- Response:
{
  "status": null,
  "status_code": 0,
  "response_detail": "Successfully created solution rating",
  "response_code": null,
  "response_body": null,
  "error_code": "100"
}


2.7 Operation Name

- Delete Favorite

- Trigger: 

Delete favorite for solution

-  Request:
{
  "request_body": {
   "solutionId": "093b29ea-8d6b-407e-b3e9-4d52964ba902",
   "userId": "173cad03-7527-42c5-81cc-35bac96cbf05"
  }
}
- Response:
{
  "status": null,
  "status_code": 0,
  "response_detail": "Successfully deleted solution favorite",
  "response_code": null,
  "response_body": null,
  "error_code": "100"
}


2.8 Operation Name

- Revision for Model/Solution

- Trigger: 
Gets a list of Solution Revision from the Catalog of the local Acumos Instance .
-  Request:
{
   "solutionId": "093b29ea-8d6b-407e-b3e9-4d52964ba902",
}
 
- Response:

{
  "status": true,
  "status_code": 0,
  "response_detail": "success",
  "response_code": "200",
  "response_body": [
    {
      "created": 1513761161000,
      "modified": null,
      "revisionId": "111",
      "solutionId": "093b29ea-8d6b-407e-b3e9-4d52964ba902",
      "version": "1.5",
      "description": "test",
      "ownerId": "173cad03-7527-42c5-81cc-35bac96cbf05",
      "metadata": null
    }
  ],
  "error_code": null
}

2.9 Operation Name

- Tag for marketplace

- Trigger: 
Gets a list of tags for Market Place Catalog.

-  Request:
{
  "request_body": {
    "page": 1,
    "size": 9
  },
  "request_from": "string",
  "request_id": "string"
} 


- Response:
{
  "status": null,
  "status_code": 0,
  "response_detail": "Tags fetched Successfully",
  "response_body": {
    "content": [],    
    "tags": [
      "Test",”Test1”
    ],
    "filteredTagSet": null,
    "userList": null,
    "last": true,
    "totalElements": 0,
    "totalPages": 1,
    "size": 0,
    "number": 0,
    "sort": null,
    "numberOfElements": 0,
    "first": true
  },
  "error_code": "100"
}

2.10 Operation Name
- Create Tag for marketplace

- Trigger: 
Create tags for Market Place Catalog.

-  Request:

{
  "request_body": {
    "tag": "test3"
  },
  "request_from": "string",
  "request_id": "string"
}

- Response:
	{
  "status": null,
  "status_code": 0,
  "response_detail": "Tags created Successfully",
  "response_code": null,
  "response_body": {
    "tag": "test3"
  },
  "error_code": "100"
}

2.10 Operation Name
- Solution Count

- Trigger: 

Get all solution count..

-  Request:
	- Simple Http get call
- Response:
{
  "status": null,
  "status_code": 0,
  "response_detail": "Solutions count fetched Successfully",
  "response_code": null,
  "response_body": {     
    "viewCount": 0,
    "ratingAverageTenths": 0,
    "ratingCount": 0,
    "companyModelCount": 1,
    "privateModelCount": 17,
    "deletedModelCount": 0,
    "publicModelCount": 2
  },
  "error_code": "100"
}

2.11 Operation Name
Add User Access

- Trigger: 
Adds user access Detail for the given SolutionId.

-  Request:
Parameter : {
   "solutionId": "04cd7d58-16df-4a13-81da-99ca8d5701d3",
}

{
  "request_body": [
    "173cad03-7527-42c5-81cc-35bac96cbf05"
  ],
  "request_from": "string",
  "request_id": "string"
}
- Response:
{
  "status": null,
  "status_code": 0,
  "response_detail": "Users access for solution added Successfully",
  "response_code": null,
  "response_body": null,
  "error_code": "100"
}

2.12 Operation Name
Get User Access
- Trigger: 
Get user access Detail for the given SolutionId.

-  Request:
Parameter : {
   "solutionId": "04cd7d58-16df-4a13-81da-99ca8d5701d3",
}
- Response:
	{
  "status": null,
  "status_code": 0,
  "response_detail": "Users for solution fetched Successfully",
  "response_code": null,
  "response_body": {
    "content": [],
    "jwtToken": null,
    "responseObject": null,
    "async": null,
    "allTagsSet": null,
    "tags": null,
    "filteredTagSet": null,
    "userList": [
      {
        "loginPassExpire": false,
        "userAssignedRolesList": null,
        "firstName": "pradip",
        "lastName": "ahire",
        "emailId": "pradip@techm.com",
        "username": "pradip",
        "password": "$2a$10$GRUQCjYLnWm9Uh5SYbVkKeQEpGKkNEqf22GnwvyRd.fB6zHt1WUkO",
        "active": "true",
        "lastLogin": null,
        "created": 1512545576000,
        "modified": null,
        "userId": "173cad03-7527-42c5-81cc-35bac96cbf05",
        "loginName": null,
        "orgName": null,
        "picture": null,
        "jwttoken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmFkaXAiLCJyb2xlIjpudWxsLCJjcmVhdGVkIjoxNTEyNTQ1NTc2NjYzLCJleHAiOjE1MTMxNTAzNzYsIm1scHVzZXIiOnsiY3JlYXRlZCI6MTUxMjQ3MjMwNzAwMCwibW9kaWZpZWQiOjE1MTI0NzIzMDcwMDAsInVzZXJJZCI6IjE3M2NhZDAzLTc1MjctNDJjNS04MWNjLTM1YmFjOTZjYmYwNSIsImZpcnN0TmFtZSI6InByYWRpcCIsIm1pZGRsZU5hbWUiOm51bGwsImxhc3ROYW1lIjoiYWhpcmUiLCJvcmdOYW1lIjpudWxsLCJlbWFpbCI6InByYWRpcEB0ZWNobS5jb20iLCJsb2dpbk5hbWUiOiJwcmFkaXAiLCJsb2dpbkhhc2giOm51bGwsImxvZ2luUGFzc0V4cGlyZSI6bnVsbCwiYXV0aFRva2VuIjpudWxsLCJhY3RpdmUiOnRydWUsImxhc3RMb2dpbiI6bnVsbCwicGljdHVyZSI6bnVsbH19.iPmhXMakNbWhmYr9kPGSbUg_SFEuaJd2IhUJyp-B0M82WNcOnY8JhyqZZ2-3waO-PegSVIRH87PH2AWWe4cgFQ",
        "role": null,
        "roleId": null,
        "updatedRole": null,
        "updatedRoleId": null,
        "userIdList": null,
        "userNewRoleList": null,
        "userRolesList": null,
        "jwtToken": null
      }
    ],
    "last": true,
    "totalElements": 0,
    "totalPages": 1,
    "size": 0,
    "number": 0,
    "sort": null,
    "numberOfElements": 0,
    "first": true
  },
  "error_code": "100"
}

2.13 Operation Name
Get Favorite solution list
- Trigger: 
get a list of favorite solutions for particular userID.

-  Request:
 {
   "userId": " 173cad03-7527-42c5-81cc-35bac96cbf05 "
}
- Response:
{
  "status": null,
  "status_code": 0,
  "response_detail": "Favorite solutions  fetched Successfully",
  "response_code": null,
  "response_body": [
    {
      "solutionId": "093b29ea-8d6b-407e-b3e9-4d52964ba902",
      "name": "blanks_solution",
      "description": "blanks_solution desc.",
      "ownerId": "173cad03-7527-42c5-81cc-35bac96cbf05",      
      "active": true,
      "accessType": "PR",
      "created": 1512471739000,
      "modified": 1509348038000,
      "tookitType": "CP",   
      "pageNo": 1,
      "size": 9,
      "modelType": "PR",
      "downloadCount": 50,
      "solutionRating": 4,
      "solutionTag": null,
      "solutionTagList": null,
      "viewCount": 12,
      "ratingAverageTenths": 0,
      "ratingCount": 3,
      "companyModelCount": 12,
      "privateModelCount": 13,
      "deletedModelCount": 16,
      "publicModelCount": 20
    }
  ],
  "error_code": "100"
}

2.14 Operation Name
Get All solution for Manage model
- Trigger: 
Get All Solutions for the User for Manage Models Screen.

-  Request:
 {
   "userId": " 173cad03-7527-42c5-81cc-35bac96cbf05 "
}
{
  "request_body": {
    "accessType": "PB",
    "page": 0,
    "size": 9    
  },
  "request_from": "string",
  "request_id": "string"
}
- Response:
{
  "status": null,
  "status_code": 0,
  "response_detail": "Favorite solutions  fetched Successfully",
  "response_code": null,
  "response_body": [
    {
      "solutionId": "093b29ea-8d6b-407e-b3e9-4d52964ba902",
      "name": "blanks_solution",
      "description": "blanks_solution desc.",
      "ownerId": "173cad03-7527-42c5-81cc-35bac96cbf05",
      "ownerName": null,
      "metadata": null,
      "active": true,
      "accessType": "PR",
      "created": 1512471739000,
      "modified": 1509348038000,
      "tookitType": "CP",
      "tookitTypeName": null,
      "revisions": null,
      "loginName": null,
      "pageNo": 0,
      "size": 0,
      "sortingOrder": null,
      "modelType": "PR",
      "downloadCount": 50,
      "solutionRating": 4,
      "solutionTag": null,
      "solutionTagList": null,
      "viewCount": 12,
      "ratingAverageTenths": 0,
      "ratingCount": 3,
      "companyModelCount": 12,
      "privateModelCount": 13,
      "deletedModelCount": 16,
      "publicModelCount": 20
    }
  ],
  "error_code": "100"
}{
  "status": null,
  "status_code": 0,
  "response_detail": "Solutions fetched Successfully",
  "response_code": null,
  "response_body": {
    "content": [
      {
        "solutionId": "0b1510a2-2f0f-4e59-9783-1606e2e78072",
        "name": "AggregatorV1",
        "description": "AggregatorV1",
        "ownerId": "173cad03-7527-42c5-81cc-35bac96cbf05",
        "ownerName": "pradip ahire",
        "metadata": null,
        "active": true,
        "accessType": "PB",
        "created": 1512472147000,
        "modified": 1508819385000,
        "tookitType": "SK",
        "tookitTypeName": null,
        "revisions": null,
        "loginName": null,
        "pageNo": 0,
        "size": 0,
        "sortingOrder": null,
        "modelType": "DT",
      "downloadCount": 50,
      "solutionRating": 4,
      "solutionTag": null,
      "solutionTagList": null,
      "viewCount": 12,
      "ratingAverageTenths": 0,
      "ratingCount": 3,
      "companyModelCount": 12,
      "privateModelCount": 13,
      "deletedModelCount": 16,
      "publicModelCount": 20
      }
    ],
    "jwtToken": null,
    "responseObject": null,
    "async": null,
    "allTagsSet": null,
    "tags": null,
    "filteredTagSet": [],
    "userList": null,
    "last": true,
    "totalElements": 0,
    "totalPages": 1,
    "size": 0,
    "number": 0,
    "sort": null,
    "numberOfElements": 0,
    "first": true
  },
  "error_code": "100"
}

2.14 Operation Name
Get All Related Solution 
- Trigger: 
Get all related Solutions for the modelTypeId for Model Detail Screen.

-  Request:
{
  "request_body": {
    "accessType": "PB",
    "page": 0,
    "size": 9    
  },
  "request_from": "string",
  "request_id": "string"
}
- Response:
	{
  "status": null,
  "status_code": 0,
  "response_detail": "Solutions fetched Successfully",
  "response_code": null,
  "response_body": {
    "content": [
      {
        "solutionId": "2d968d79-c9d7-4170-9979-079cd9eecc1d",
        "name": "TestSolutionName",
        "description": "TestSolutionName",
        "ownerId": "83d5259f-48b7-4fe1-9fd6-d1166f8f3691",
        "ownerName": "nitin LAHURE",
        "metadata": null,
        "active": true,
        "accessType": "OR",
        "created": 1513763056000,
        "modified": 1509419787000,
        "tookitType": "CP",
        "tookitTypeName": null,
        "revisions": null,
        "loginName": null,
        "pageNo": 0,
        "size": 0,
        "sortingOrder": null,
        "modelType": "PR",
      "downloadCount": 50,
      "solutionRating": 4,
      "solutionTag": null,
      "solutionTagList": null,
      "viewCount": 12,
      "ratingAverageTenths": 0,
      "ratingCount": 3,
      "companyModelCount": 12,
      "privateModelCount": 13,
      "deletedModelCount": 16,
      "publicModelCount": 20
      },
      .
	  .
	  .
    ],
    "jwtToken": null,
    "responseObject": null,
    "async": null,
    "allTagsSet": null,
    "tags": null,
    "filteredTagSet": [],
    "userList": null,
    "last": true,
    "totalElements": 0,
    "totalPages": 1,
    "size": 0,
    "number": 0,
    "sort": null,
    "numberOfElements": 0,
    "first": true
  },
  "error_code": "100"
}

