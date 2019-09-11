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

.. _maintained-backup-logs:

======================
MAINTAINED BACKUP LOGS
======================

In Acumos, any user having administrative privileges can take and maintain backups of logs that are stored in server using an UI. In **SITE ADMIN** page a **Maintained Backup Logs** menu is available, it contains two tabs **BACKUP LOGS** and **ARCHIVED LOGS**.


**BACKUP LOGS**
===============

In **BACKUP LOGS** tab, there are **Create Backup** and **Archive** buttons.

                .. image:: images/admin-maintained-backup-logs.png
                 :width: 75%

When clicked on **Create Backup** button a pop-up is shown where a repository need to be selected, if no repository is available in elasticsearch then a new repository need to be created since backups are stored in form of repositories.

                .. image:: images/admin-create-backup.png
                 :width: 75%

Repository name need to entered in the pop-up appeared when **Create Repository** button of **Create Backup** pop-up is clicked.

                .. image:: images/admin-create-repository.png
                 :width: 75% 
  
Repositories are collection of Indices, the logs from server are converted into json format and  sent to elastic search of ELK in back-end, rows of that json file is treated as Indices. These Indices are created automatically from the data gathered through day to day activities performed on server.
**ADMIN** can select any number of Indices in a single repository where backup can be created.

                .. image:: images/admin-indices.png
                 :width: 75% 
  
Multiple backups can be grouped together if they belong to same repository. The **+** icon before the repository name will show the set of backup logs grouped under a single repository.
  
  .. image:: images/admin-expand-repository.png
                 :width: 75% 
  
Indices can be deleted as well. Deletion of Indices from the UI will permanently delete them from database.

                .. image:: images/admin-delete-indices.png
                 :width: 75% 

The icon of action on **MAINTAINED BACKUP LOGS** page will Archive each repository for future use.
  
                .. image:: images/admin-archive.png
                 :width: 75% 
 

**ARCHIVED LOGS**
=================

In **ARCHIVED LOGS** tab, there are **Restore** and **Delete** buttons which are used to restore and delete the archived repositories.

                .. image:: images/admin-archived.png
                 :width: 75% 
 
Archived repositories can be restored or deleted using the **Archived Logs Tab**. The *Restore* icon under actions will restore and the *Trash* icon will delete the repositories respectively. 
 
                .. image:: images/admin-restore.png
                 :width: 75% 
 
Once repositories deleted from archived logs they cannot be restored stating they are permanently deleted.

                .. image:: images/admin-delete-repository.png
                 :width: 75% 
