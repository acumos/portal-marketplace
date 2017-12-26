=========================================
Acumos Portal MarketPlace Document
=========================================

1.	Introduction
========================

         This is the User guide to MarketPlace. 

**1.1 What is MarketPlace\?**
	Acumos provides a toolkit-independent 'App Store', called a Marketplace for:

	1.	Data-powered decision making and artificial intelligence software models.

	2.	It provides a means to securely share AI microservices along with information on how they perform, such as ratings, popularity statistics and user-provided reviews to apply crowd sourcing to software development..

	3.	The platform provides integration between model developers and applications in order to automate the process of user feedback, exception handling and software updates..
	
**1.2	Target Users**
	This guide is targeted towards the open source user community that:

	1.	Intends to understand the functionality of the MarketPlace.




**1.3 MarketPlace – High level Architecture**
 	.. image: images/marketplace_architecture.jpg
	:alt: MarketPlace – High level Architecture

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

**1.5 MarketPlace Flow Structure 
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


1.6 User Account Signup Flow :

	.. image:images/Signup_Flow.jpg
	:alt: User Account Signup Flow
 
1.7 User Account Login Flow : 

	.. image:images/Login_Flow.jpg
	:alt: User Account Login Flow


1.8 Market Place Catalog Flow :
 
	.. image:images/Catalog_Flow.jpg
	:alt: Market Place Catalog Flow




1.9 Model Detail Page Flow :
 
	.. image:images/Model_Detail_Page_Flow.jpg
	:alt: Model Detail Page Flow


2. Market Place Home	
========================

**2.1 All About MarketPlace Home **

We are Moving to a Future where AI is at the Center of Software.
Acumos is the open-source framework for data scientists to build that future. 
In Market Place Home we will Perform following :

1.Add Your Model: This functionality will gives you to onboard any of your model like H20, TensorFlow, Scikitlearn, R, Java, etc. by using any of the following.
a. On-Boarding By Command
Description a:It will give you the description of the model which you want to Onboard ,how to start ,before you begin, description ,Installation and important commands corresponding to models.
b. On-Boarding By Web:
Description b: It has four steps to Onboard your model.

2. Under Market Place Home we have Explore Market Place functionality as well where we can explore the Acumos Marketplace. It is easy to discover, download & deploy.

3.We have Discover Acumos in Home Page .We can discover below things:
3a. Team up: Share, experiment & collaborate in an open source ecosystem of people, solutions and ideas.
3b. Marketplace: Acumos it the go-to site for data powered decision making. With an intuitive easy-to-use Marketplace & Design Studio. Acumos brings AI into the mainstream.
3c. Onboard with your preferred Toolkit:With a focus on interoperability, Acumos supports diverse AI toolkits. Onboarding tools are available for TensorFlow,SciKitlearn, Rcloud,H20 & generic java.
3d. Design Studio:Because Acumos Converts models to microservices, we can apply them for different problems & data sources.
3e.SDN & ONAP: Many marketplace solutions originated in the ONAP SDN Community and are configured to be directly deployed to SDC.


4.  Acumos Hackathon:  This is the upcoming feature in Acumos Hackathon.

Description :  Meet new colleagues and expand your Acumos skills.

5.Under Acumos Home Page we have success stories.
6.Acumos Home page has following quick links:
6a: Home
6b:Marketplace
6c:Modeler Resources
6d:Terms & Condition
6e:Documentation

3. MarketPlace	
========================

MarketPlace has search functionality which will allow user to search any model which is being Onboarded.
We have following Filter By Category Option by which we can easily search the Onboarded Model in Market Place.
•	Classification
•	Data Sources
•	Data Transformer 
•	Prediction
•	Regression



3.1 Downloading any Onboarded Model : 
We can download any model which is Onboarded.
We have following things when we download any model:
•	Description
•	 Signatures
•	Documents
•	Version History


3.2 Deploy Onboarded Model to Cloud : 

We can Deploy Onboarded Model to Cloud.

Following are the Platforms where we can Deploy the models to Cloud:
•	Microsoft Azure
•	Google Cloud Platform
•	rackspace
•	amazon webservices


   3.3 UI Layout for Deploying model to Microsoft Azure:
 
	.. image :images/deploy_on_azure.jpg
	:alt: Deploying model to Microsoft Azure


3.4 Add New Model to MarketPlace

We can add New Models in MarketPlace by using following
 
•	On-Boarding By Command Line
•	On-Boarding By Web

4. My Models	
========================

My Models has search functionality which will allow user to search any model which is being Onboarded it is like MarketPlace search functionality.
We have following Filter By Category Option by which we can easily search the Onboarded Model under My Models.
•	Classification
•	Data Sources
•	Data Transformer 
•	Prediction
•	Regression
•	Deleted models

4.1 Under My Models you can see :
•	My Unpublished Models
	.. image: images/unpublished_model.jpg
	:alt: My Unpublished Models

 



•	MY MODELS: Published to Company Market Place
	.. image: images/publish_to_company.jpg
	:alt: Published to Company Market Place
 


•	My Deleted Models
	.. image : images/deleted_model.jpg
	:alt: My Deleted Models
 




5. On-Boarding Model 

     ========================

The Acumos on-boarding process generates everything needed to create an executable microservice for your model and add it to the catalog.  Acumos uses Protobuf as a language-agnostic data format to provide a common description of the model data inputs and outputs.
The client library does the first step of the on-boarding process. This includes:  (1) introspection to assess the toolkit library versions and determine file types, (2) creation of a json description of the system (3) creation of the protobuf file, (4) file push to the Acumos on-boarding server.
Once your model is on-boarded, it will available in the PRIVATE section of the Acumos Marketplace.  Tools to manage and publish your model are available in the Acumos Portal.

5.1 We have to ways to On-board the Model :
•	On-Boarding By Command Line
•	On-Boarding By Web
5.2  Onboarding By Command Line: 
We can Onboard any model like H20,Tensorflow,Java,R,Scikit Learn by using command line.

Following are the steps that we need to follow like for TensorFlow following are the steps shown in UI:

 	.. image:: images/Tensor_Flow.jpg
	:alt: TensorFlow



5.3	On-Boarding By Web: 
We can Onboard any model by using web based On-Boarding

There are four steps for Performing Web based On-boarding
1.Choose Toolkit
We can choose any one Toolkit i.e. the model that we need to upload:
       .. image:: images/choose_toolkit.jpg
	:alt: Choose Toolkit

2.Download Client Library from Command Line:
Under this we have tick the checkbox Installation of the toolkit Library is completed.
 	.. image::images/Download_Client_Lib_Command_Line.jpg
	:alt: Download Client Library from Command Line

3.Upload Model Bundle:
We can upload model bundle here which is a zip file that contains following:
•	JSON file
•	Potofile
•	Model.zip 
 	.. image:: images/Upload_Model.jpg
	:alt: Upload Model Bundle

4. Model Information :
We can add the model Information under Model Name:
 	.. image:: images/Model_Information.jpg
	:alt: Model Information

































