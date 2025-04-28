Link to blog: https://comp30880comiccodex.blogspot.com/
Link to GitHub: https://github.com/mark3334/COMP30880_ComicCodex

Team name: ComicCodex
Members:
    Name: 	Mark Kirwan		    Student Number: 22382686
    Name:   Guanqiao Han  	    Student Number: 21209757
    Name:	Abdullah Shinwari	Student Number: 22496944

navigate to Submissions folder and run  java -jar Sprint7executable.jar to execute the Main method


--- a zipped directory called SampleComic is available in the Submission folder
 containing a comprehensive sample comic (the .xml specification, all audio files referenced in the .xml)

Mark:
The creation of the full comic lesson should involve getting some k random scenes from
the verb, story (sprint6finalaudio), left and whole scene files.
Another group member generated the left and whole scenes files without providing the method to do this. This wasn't done dynamically
(based on the current target language) like was asked of them. So I had redesign original plan to fit this.
Originally, I thought I could do a general remove Translated panels method from left, whole but the structure of whole_scenes xml
was done differently. So the plan had to be changed once again to simply overwrite the Document doc of the whole_scenes.xml to change
every third balloon node to the translation of the previous one.

Story:
XML_parser.main() creates the Sprint5_DialogueOutput.xml and Sprint5_InterwovenOutput.xml files which are then
used to create the Sprint 6 file with the createSprint6File method.
Conjugation:
The createConjugationLesson creates the translated verbs with audio xml file.
Left, Whole:
The writeTranslatedVignettes uses untranslated left scenes created using the remove panels method.
Then adds translations and audio to the doc and then writes it to an xml file.
The whole_scenes is taken in as a Document and then overriden replacing every third balloon node to the translation of the previous one.

There is no simple method for generating left_scenes.xml and whole_scenes.xml even though I asked for there to be ...

Currently, we only have 3 RPM with the current API key as I cannot contact the group member.
It takes 48 hours for a new paid account to get the higher RPM (1,000) of a Tier 1 key for gpt-mini.
who owns the account with our previous API key. So the program takes a long time to generate all the mp3 files.
