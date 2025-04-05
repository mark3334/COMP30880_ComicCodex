Link to blog: https://www.blogger.com/u/1/blog/posts/4325067390036061742
Link to GitHub:https://github.com/mark3334/COMP30880_ComicCodex

Team name: ComicCodex
Members;
    Name: 	Mark Kirwan		    Student Number: 22382686
    Name:   Guanqiao Han  	    Student Number: 21209757
    Name:	Abdullah Shinwari	Student Number: 22496944


Main:

XML_Parser takes in a xml file and creates a Document from it. It can get the content of the speech balloons as a list of strings.
These strings can be translated using the translateAll method in TranslationFile which filters them
so that only the ones that haven't already been translated are sent to OpenAIClient TranslateAll method.

Once the verbs have all been translated, the addTranslatedPanels iterates over the panels to be duplicated
in the doc (the one with a balloon) and creates a clone of them with the text content of the clone being the translation
of the original text. This clone is then inserted into the doc.

A writeXML method can then be called to write this doc to an xml file in the
XML output folder with a name of Verbs_(TARGET_LANGUAGE)
