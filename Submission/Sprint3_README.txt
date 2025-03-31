Link to blog: https://www.blogger.com/u/1/blog/posts/4325067390036061742
Link to GitHub:https://github.com/mark3334/COMP30880_ComicCodex

Team name: ComicCodex
Members;
    Name: 	Mark Kirwan		    Student Number: 22382686
    Name:   Guanqiao Han  	    Student Number: 21209757
    Name:	Abdullah Shinwari	Student Number: 22496944


Running `main` will execute the method `comicXMLGenerator.SceneExporter()`, which will generate an XML file in the `Resources` folder. This file is the result we need for this sprint.
Run jar file by cd to COMP30880_ComicCodex/Submission then java -jar Sprint3executable.jar

TranslationFile class is now a singleton which reads and write to a
source_target language dictionary.
ConfigurationFile class is a singleton which can be used to get the value of
any key from the config.txt file. It uses the FileParser class to read in the
config.txt file into a hashmap.
The goal of FileParser is to be able to read/write from file to hashmap
hopefully this read/write method can be improved using interfaces?
as we can define the object to read a line in for example a (key,value) pair
or a VignetteSchema object.

comicXMLGenerator class generates a xml string from a given

VignetteManager maintains a list of VignetteSchemas from the word_asset_mapping file
The object VignetteSchema is defined in VignetteSchema which has a toString method.

Helper class may be able to be removed as it has little use in the current version.

The OpenAIClient class has not been extensively modified yet. We plan to make it a Singleton and extend it to LLMClient so that users can choose the LLM model they want to use, rather than being limited to OpenAI.

XML document generation:
Added Figure and FigureData. Figure is an entity class used to construct characters that appear in the comic, while FigureData serves as a repository for all possible traits of a figure. The generateRandomFigure method in Figure can generate a character with random features.
Added comicXMLGenerator, which contains the method generateSceneXML to create the XML file we need. The sceneExporter method outputs the content to a .txt file.
Add the SceneGeneratorRegistry class, which is used to determine which preset generation method should be used to generate the required XML file based on the input schema.
The SceneGenerator contains four generation classes that implement the SceneGeneratorInterface. The interface defines the matches method to check for a match and the generate method to generate the XML text.
