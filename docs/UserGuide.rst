=========================================
Acumos Portal MarketPlace Users Guide
=========================================


1. Introduction
======================

    This is a user’s guide that describes how to use the the Marketplace and Acumos Portal.

**1.1    What is the Marketplace?**

    The Acumos Marketplace is a web tool designed to bring data scientists together with users who wish to use their models.  It has the following key features:
    
    1.    Easy On-boarding of models.  The Acumos Portal enables modellers to easily on-board their AI models, document them, package them into reusable microservices, and publish them to either a local or public Marketplace.

    2.    Explore the possibilities of AI. The Marketplace enables users to explore, gathering high-level or detailed information about a model and how it is used. Users have access to extensive documentation, as well as ratings and comments from the greater community.
    
    3.    Access.  Once a model is selected, the Acumos Marketplace provides access to that model, either via downloading or by deploying it to any one of several commonly used cloud environments.

    4.    Compose. Users may also work with models in the Design Studio to chain them together to create new custom composite models to help solve their business problems. These composite models can be saved and managed (deployed, published) just like simple models.  (See Design Studio User Guide.) //link

    5.    Federation. The Acumos Marketplace is distributed.   Acumos supports federated relationships with other Acumos instances, allowing users to browse and procure models from federated partners.  Federation also creates a much larger available user pool for data scientists to share their models.

    6.    Training. Acumos provides methods to train models.  Data sources can be discovered and training process can be designed in the Acumos Training Studio.  (This is work in progress, and not available in release 1.0.)

**1.2    Target Users**

    The Marketplace is designed for users who wish to either explore and use machine learning models or data scientists who build models and wish to share them with a larger community of users.


2. Creating a Login on Acuomos
==================================================
    In order to use the full capabilities of Acuomos, the users must create a login on the Acumos Portal.
    
    The user may also complete a user profile. 
    
    Depending on the Acumos instance configuration, custom company login options may be supported. 
    
        All account management capabilities are available in the upper right corner of the header screen.
        
        .. image:: images/accountSettings.png
           :alt: User Account Settings 
        

2.1 User Account Signup Flow :

        .. image:: images/signUp_screen.png
           :alt: User Account Screen 

        .. image:: images/signIn_screen.png
           :alt: User Account Signup Flow

2.2 User Account Login Flow :

        .. image:: images/Login_Flow.jpg
           :alt: User Account Login Flow

2.3 Set Profile and Notification Preferences

        .. image:: images/accountSettings.png
           :alt: User Account Settings Form
           
     // (----We need a screen shot here when this work is implemented---)

3. Key Features of the Acumos Marketplace and Portal
==================================================

    The Acumos Marketplace is a place for builders and users of machine learning models to intersect and interact.  Users may view their LOCAL marketplace, which is restricted to users on their local instance, for example their company or department. They may also view the PUBLIC marketplace, where they can see models from other Acumos instances that have been published to the public.

**3.1  Models in Acumos are packaged for Reuse** 

    As a part of the on-boarding process, models are packaged into a containerized microservice that can be re-used by other components.  
    
    This is the `Acumos On-boarding Developers Guide <../../on-boarding/docs/index.html>`_.  (See more about on-boarding -here- LINK)

4. Marketplace and Portal Experience - for Users
==================================================

**4.1    Acumos Home**

    Users are welcomed to Acumos on the home page, showing a carousel highlighting Acumos features and uses.  Other parts of the page show featured or trending models, upcoming Acumos events and illustrations of how Acumos can help customize solutions in many domains.

    Users do not need to be logged into Acumos or have an account to see the Home or Marketplace Pages.

**4.2    Acumos Marketplace**

    1.    Users can discover models by browsing, direct search, or by applying any of a number of filter criteria to explore the marketplace. Models are presented on the Marketplace as "tiles", showing the Name, image, ratings and usage statistics. More information about the model is available by clicking on the tile to reach the MODEL DETAIL page. //make that a link.

    2.    The Market place has two main views:  LOCAL and PUBLIC.

        a.    The LOCAL marketplace only shows models which have been on-boarded by local users and published to the local marketplace.  In many cases, this can be thought of as the COMPANY marketplace, although large companies may wish to have more than one Acumos instances. (For example, a Research Acumos and a Customer Care Acumos.)

        b. The model documentation, including the image describing the model, the short description, the uploaded documents, and the model signatures, the ratings and comments are custom and private to the LOCAL instance so protected company information can be used.  (Users may duplicate this information when publishing to PUBLIC, but they must explicitly opt to do that.)

        c.    Models in the LOCAL marketplace can be shared by the owners with other users of that Acumos instance. (//see sharing //)

        d.  Models in the PUBLIC marketplace are visible to all Acumos instances with a federated relationship to the home instance, that is the original Acumos publish site. 
        
        //for more details, see Federation in Acumos

        e. Models in the PUBLIC marketplace have been cleared by the local administrator for publication according to requirements established by them.  (//see validation)

    3.    Browsing and Finding Models in the Marketplace
    There are many ways to find models that may fit your needs.
        a. Filter
            i)  category (classification, data sources, data transformation, prediction, regression
            ii) Rating
            iii) Filter by Peer Catalog

        b. Search
        	A search box is provided on Marketplace   

    4.    Saving a model to Favorites List
        Logged in users my click on the "heart" icon to denote a model they wish to add to their "favorites" list.  This list can be retrieved by <....> and also in the Design Studio.

**4.3    Acumos Model Detail Pages - Users**

    1.    Much more information about a model is available on the Model Detail Pages. Most of the information on these pages is contributed by the creator of the models 
    to showcase the model and tell potential users about it's capabilities and how to use it. Ratings and Comments are contributed by other users of this model.  
    The sections include:

        a. Introduction-Summary Description
        b. Ratings
        c. Comments
        d. Signatures:  the description of the model incoming data feed and output
        e. Documents: any supporting documentation the modeler wishes to include
        f. Version History:  //see model versioning


**4.4    Accessing Models for download and deploy from the Marketplace**

    1.  From the Model Detail Page, users may procure the model.

        a. If the model is on their local repository already, they are immediately available and the Deploy or Download button are activated in the upper right hand corner and users may download the model or deploy it to a cloud environment.  // link (see section X.Y for Deploy/Download Models).

            i) Models that were created locally, or previously imported to the local repo via a federation get-model request would be immediately available and no request process would be needed.

        b. If the chosen model is from a federated peer, and has never been moved to the local repo, it may not be immediately available and must be requested. //see Federation

         In that case, the tile on the Marketplace shows the message: "Request to Download", or the same message is shown on the Model Detail page in the upper right hand corner of the page.

         .. image:: images/RequestModel_tile.png
            :alt: Request Model Tile



         .. image:: images/requestModel_detailPage.png
            :alt: Request Model Detail Page

Clicking the "Request" button, initiates an import request to your local administrator to procure the model and move it to your local repo.  
Imported models are subject to local validation rules that are set by your local admin.  
This process may be immediate, or it may take some time, depending on what steps are required.  
You will be notified, according to your preferences, 
(//see notification preferences settings in profile - link-)


5. Marketplace and Portal Experience - for Modelers
==================================================
    The Acumos Portal is designed to enable modellers to easily on-board, document, package their AI models into reusable microservices.
    The modeler may test out the Acumos features in a private section of the portal or he/she may publish the models  either their local marketplace or distribute widely by publishing them to the public. 
    Modelers are typically subject-matter experts in their fields, so Acumos models come from a wide range of domains or applications.
    Models may be written in a number of popular programing languages or toolkits, including TensorFlow and R.
    
**5.1    Overview for Modelers**

    Three views: private, local and public

**5.2    On-Boarding Models**


    1.  Users can reach on-boarding page from HOME, MARKETPLACE or side navigation
    	For more information about on-boarding, see  This is the `Acumos On-boarding Developers Guide <../../on-boarding/docs/index.html>`_. <link>

    

**5.3    My Models Page**

	   Users may view all the models they have uploaded by accessing the My Models page.
   Models are organized by their visibility to others.  Models are sorted into four sections:  Unpublished, Published to Local (Company), Published to Public and Deleted.
   
   Clicking on any model image shows the Model Detail page for that model.
   
   If a model has not been published, the Model Detail page will not show very much information.  To add a description, documents and details for this section, select the "Manage My Model" page and choose one of the Publishing tabs.

    1. My Models: private view
    
        Initially, successfully on-boarded model will appear in my Models page in the UNPUBLISHED section.  These are visible only to you and any collaborators of that model (shared).  Partially on-boarded models (in process) are also displayed in this section but are shown greyed out until the on-boarding process is successfully completed.
        
    2. My Models: Published to Local view
    
        Models that have been published to Local, appear in the the LOCAL marketplace and are visible to anyone with an account on the local Acumos Instance.  
        
    3. My Models: Published to Public view
    
         Models that have been published to Public, appear in the the PUBLIC marketplace and may be viewed by users on Acumos instances that have a federated relationship with your local instance. 
        
    4. My Models: Deleted Models
    
        Initially, successfully on-boarded model will appear in my Models page in the UNPUBLISHED section.  These are visible only to the owner of that model and any collaborators of that model (via the "Share Model" process).  Partially on-boarded models (in process) are also displayed in this section but are shown greyed out until the on-boarding process is successfully completed.
        
    

**5.4    Manage My Model - page & capabilities**

    1. On-boarding
        overview (command line & web-onboarding) & link to on-boarding guide

    2.     Share Model with Team
        Users can share a model with anyone who has an account on your local Acumos. When you share a model with a collaborator, you make them a co-owner of the model.  This means they have all the same capabilities as the original owner.
        
        First, select the "Share with Team" tab under "Management Options".
        
        .. image:: images/share-1.jpg
           :alt: share with team: select box
           
        Next, where you see "find a user to share with", type in the user ID of the person you wish to share with - you will need to get that user ID from them.   The UI will show suggestions, based on the characters you have typed.  Once you have located the correct person, select the SHARE button.
        
        .. image:: images/share-2.jpg
           :alt: share with team: select user to share with, by user ID
           
Finally, the Sharing Popup will disappear and you can see that your model is shared and you have added them as a collaborator for that model. Click on each icon to see who you are currently sharing a model with.

The new collaborator will receive a notification that a new model has been shared with them.

If you wish to remove the sharing rights from someone, simply click on the small "X" next to their icon.
        
        .. image:: images/share-3.jpg
           :alt: share with team: sharing result
        

    3. Publishing a Model 
    
    Users may distribute their model by publishing it to either their LOCAL marketplace or to the PUBLIC marketplace.
    
    The presentation of the model may be different in each marketplace to meet the needs of the different communities.  For example, the user may wish to provide company-specific details to their colleagues inside their LOCAL instance.  This may include company proprietary information, documents or details that are only relevant to colleagues using the LOCAL instance.  Information published to LOCAL is contained within the company firewall.
    
    The modeller may wish to present their model to the PUBLIC in a more general way.
    
    Acumos provides two separate publishing workflows to meet this need.  
    
    If the publication information is the same for both marketplaces, there is a facility to simply use the same information.
    
    	A. Publishing to Local Marketplace
        Directions

    	B.  Publish to Public Marketplace
        Directions

    5. Export/Deploy to Cloud
        Download
        Deploy to Cloud

    6. View Downloads

    7. Reply to Comments

    8. Delete Model

    9. Model Version Control




6. Acumos Deploy/Export/Download
==================================================
    <some overview info>
**6.1    Overview**

7. Acumos Distributed Marketplace:  Federation
==================================================

**7.1    Overview of Federation**

    A key feature of the Acumos platform is it's distributed marketplace.  Each Acumos instance may create a federated (peer) relationship with any other Acumos instance, allowing individual models, all models from a category, or the full catalog of models to be included in the PUBLIC view of the marketplace.

    Federated relationships are set up and managed by the administers of each of Acumos instances.  (//see user guide for Admin: setting up federation)

    For example, if Acumos A has a federated relationship with Acumos B, say for the full catalog, then all the models that have been published to PUBLIC from users on Acumos B will be viewable in the PUBLIC marketplace of Acumos A.

    This federated relationship between A and B does not mean that all the models from B have been moved to the Acumos A repository and are immediately available from Acumos A.  Instead, the federated relationship simply allows users from A to browse the catalog from B and see all the documentation, public ratings and comments from the models in the public Acumos B's catalog. When a user from Acumos A discovers a model they would like to work with, they must request access to that model.

    Since granting that request means that new model from B would be imported to Acumos A's repo, the model must be subjected to the import rules set up for Acumos A.

**7.2    Browsing Federated Models**

    To see models from federated peers, choose the PUBLIC marketplace.  The default view is showing all public models from your local Acumos as well as all models from all peers.

    To restrict your view to a particular peer, or set of peers, adjust the filters found on the left side accordingly.


        .. image:: images/Public_Marketplace_peer.jpg
           :alt: Public Marketplace Peer

**7.3    Accessing Federated Models**
//see section 4.4




8. Acumos Composition:   Design Studio
==================================================
    <some overview info>
**8.1    Overview of Design Studio and Link to DS User Guide**

         `Link to Design Studio User Guide <../../design-studio/docs/index.html>`_.

9. Acumos Model Training: Training Studio
==================================================
    <some overview info>
**9.1    Overview of Training Plan**










