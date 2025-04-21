Link to blog: https://comp30880comiccodex.blogspot.com/
Link to GitHub: https://github.com/mark3334/COMP30880_ComicCodex

Team name: ComicCodex
Members:
    Name: 	Mark Kirwan		    Student Number: 22382686
    Name:   Guanqiao Han  	    Student Number: 21209757
    Name:	Abdullah Shinwari	Student Number: 22496944

navigate to Submissions folder and run  java -jar Sprint6executable.jar to execute the Main method

Xml reader/writer
The reader class takes in some file and generates a Document. It should be responsible for anything that doesn't modify the DOM
such as getting nodes from the Document or printing information about it.
The writer class handles editing a document such as adding translated panels, adding audio tags for each balloon content,
splitting panels that have two balloon texts.

AudioManager
This is a singleton class that reads in an indexes.txt into a hashmap to support amortized O(1) lookup for some text
to the index of its corresponding mp3 files.
It sends a request to the TTS if some text is not in the indexes.txt to generate the mp3 file and add it to the hashmap and indexes.txt file
It uses the Configuration file to access the paths.
It assumes there is a txt file directly under the audio folder.
It has methods to ensure the audio, mp3files folders exist. If they don't, it creates them.
NOTE - It does not support deletion of mp3 files

Splitting panels counts the number of balloon texts for some panel.
If the balloonsList.getLength() == 2 then:
It creates 2 copies of it one with the first balloon text and the other with the second.
It gets the parent of the original balloon and next sibling so that these can be accessed after the balloon is removed