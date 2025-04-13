Link to blog: https://www.blogger.com/u/1/blog/posts/4325067390036061742
Link to GitHub: https://github.com/mark3334/COMP30880_ComicCodex

Team name: ComicCodex
Members;
    Name: 	Mark Kirwan		    Student Number: 22382686
    Name:   Guanqiao Han  	    Student Number: 21209757
    Name:	Abdullah Shinwari	Student Number: 22496944

navigate to Submissions folder and run  java -jar Sprint5executable.jar to execute the Main method

Main: takes in k random scenes from the input XML file
For each scene generates a narrative arc for it
Then creates dialogue for the scene.
Then a copy of the scene is made and the dialogue is added to it.
This scene is written to an output XML file and a corresponding version with translated panels
like in Sprint4 is then generated.

XML_Parser class is planned to be split into Xml reader/writer classes.

TranslationFile class was updated to ensure that the translate all method doesn't contain too many
characters this is based on the token limit of our API key which is less than the actual limit in order
to ensure the prompt won't be too long.

The narrative arc for a scene is the combination of sentences from each panel of the form
{figureName} : {balloonContent}. //TODO setting and below tags are not used - This was done at commit b3b2d5d41ba69571f5b3b3bc7dade255e76305d8
