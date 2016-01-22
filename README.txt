Description
-----------

    * CommonTools includes Algorithm, HBaseSQLManager, Table and Scheduler classes.
    * CommonTools are used at Parser and RecommendationServlet.

Dependencies
------------

    * For Servlet and Parser there are no need for any dependencies. To artifacts just add CommonTools compile output.
    * For Scheduler You will need extracted phoenix client, extracted mymedialitejava and CommonTools compile output.

File locations
--------------

    * Model files for Algorithms are stored at: /usr/share/tomcat/models
        Model files are copied from scheduler folder: /home/edgaras/scheduler/models
        To change locations edit .sh files into scheduler folder.



Scheduler
---------

    * HOW-TO setup cron job:
        * To check running cron jobs into command line type: crontab -e
        * Press <i> to start editing
        * Write a job. Example of cron job: 1 0 * * *  ../../../home/edgaras/scheduler/mostpopular.sh
            * Tutorial: http://www.howtogeek.com/101288/how-to-schedule-tasks-on-linux-an-introduction-to-crontab-files/
        * Press <ESC>
        * Type ":w" and press <Enter>
        * To quit editor type ":q!" adn press <Enter>
            * How to Use the vi Editor: https://www.washington.edu/computing/unix/vi.html
        *NOTE: One job at one line!

    * Scheduler executes .sh file, which starts appropriate Algorithm training and model saving. After that copies model file to tomcat
    directory, where Servlet can use it.

Build
-----

    * NOTE: For CommonTools are used two different jars. One with dependencies, another without. For scheduler, must be used .jar with
    dependencies + at artifact creation should be created MANIFEST.MF (IDEA does it on own), where is located class with main method.

	Project build on:
	* IntelliJ IDEA 14.1.5
	* JDK 1.7

	Guides to build .jar for IntelliJ IDEA:
	* https://www.jetbrains.com/idea/help/opening-reopening-and-closing-projects.html#d1435176e136
	* https://www.jetbrains.com/idea/help/configuring-artifacts.html
	* https://www.jetbrains.com/idea/help/packaging-a-module-into-a-jar-file.html

	Step-by-step guide:
	* Open project
	1. On Welcome screen, click Open
	    * Or Main menu, choose File | Open
	2. In the Open Project dialog box, navigate to the desired *.ipr, .classpath, .project, or pom.xml, file
	    or to the folder marked with the IDEA icon, that contains project .idea directory.
    3. In the Select Path dialog box, select the directory that contains the desired project.
    4. Specify whether you want to open the project in a new frame, or close the current project and reuse the existing frame.

    * READ: https://www.jetbrains.com/idea/help/opening-reopening-and-closing-projects.html#d1435176e136

    * Manage the list of project artifacts
    1. Open the Project Structure settings.
    2. Click Artifacts to open the Artifacts page. The page shows all the artifacts that are available in the project.
        Manage the list using the toolbar buttons:
        * To create an artifact, click the New button + and choose the artifact type
            (JAR, WAR, EAR, Android, or an exploded directory) in the New drop-down list.
        * To remove an artifact, select it and click the Remove button -.
        * To view a list of artifacts in which the selected artifact is included, click the Find Usages button find.
        * To edit an artifact, select it and update its settings in the Artifact Layout pane that opens.

        ** Project Structure | Project Settings | Artifacts | Jar | Empty
        OR
        ** Project Structure | Project Settings | Artifacts | Jar | From modules with dependencies IF dependencies are needed

    * READ: https://www.jetbrains.com/idea/help/configuring-artifacts.html#d1640450e127

    * Configure an artifact
    1. In the list of artifacts, select the one to be configured. Its settings are displayed in the Artifact Layout pane.
    2. Specify the general settings of the artifact.
    3. Complete the artifact definition by following these general steps:
        * Configure the artifact structure.
        * Add resources to the artifact.
        * Arrange the elements included in the artifact.
        * If necessary, specify additional activities to be performed before and after building the artifact in the Pre-processing and Post-Processing tabs.

    * READ: https://www.jetbrains.com/idea/help/configuring-artifacts.html#d1640450e191

    * Build a JAR file from a module
    1. On the main menu, choose Build | Build Artifact.
    2. From the drop-down list, select the desired artifact of the type JAR.

    ** Build | Build Artifact

    READ: https://www.jetbrains.com/idea/help/packaging-a-module-into-a-jar-file.html

Contacts
--------

	* Author: Edgars Fjodorovs


