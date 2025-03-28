Link to blog: https://www.blogger.com/u/1/blog/posts/4325067390036061742
Link to GitHub:https://github.com/mark3334/COMP30880_ComicCodex

Team name: ComicCodex
Members;
    Name: 	Mark Kirwan		    Student Number: 22382686
    Name:   Guanqiao Han  	    Student Number: 21209757
    Name:	Abdullah Shinwari	Student Number: 22496944


//TODO to run the project ... run main?

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

Main class //TODO

Helper class may be able to be removed as it has little use in the current version.

OpenAIClient //TODO

