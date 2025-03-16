# Sprint1_README File

## Overview
The **OpenAIClient** is the basic Java application that interfaces with the OpenAI API to provide an interactive chatbot experience. It maintains a conversation history, sends user prompts to the API, and retrieves responses from OpenAI's language model.

## Libraries
- org.json.JSONArray & org.json.JSONObject: JSONObject is used to create and parse JSON data and JSONArray processes JSON arrays.
- java.nio.charset.StandardCharsets: Set character encoding, used here for sending JSON request and reading API response.

## Features
- Communicates with OpenAI API via HTTP POST requests.
- Maintains a conversation history of up to 5 messages.
- Parses JSON responses to extract chatbot replies.

## Structure
The OpenAIClient class is the main class which is responsible for sending and receiving JSONobjects and creating and parsing them with strings. The client gets the api_key and other parameters from the config.txt file using the ConfigurationFile Class which has methods for getting value by key and creating a hashmap from the Config.txt file

## command line invocation:
To run the file do java -jar Sprint1executable.jar
Type in your prompt and type 'exit' to quit the program..

## Notice：
- Avoid unnecessary keyboard input while waiting
- You need to provide your own config.txt file. A valid config.txt file should follow the format below:
  "language" : "Spanish"
  "API_KEY" : Your API_KEY
  "ORG_KEY" : Your ORG_KEY
  "MODEL" : "gpt-4o-mini"
  "COMPLETIONS_URL" : "https://api.openai.com/v1/chat/completions"
  "EMBEDDINGS_URL" : "https://api.openai.com/v1/embeddings"
  "MODELS_URL" : "https://api.openai.com/v1/models"
  "USER_MODE" : "True"

