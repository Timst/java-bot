# java-bot
An IRC bot

This project relies on the pircbot IRC library. It implements a bot with features such as 
* logging
* timezone conversions
* currency conversions
* sending messages to users when they connect
* etc.

Commands typically start with + (e.g. +memo). This bot is first and foremost developed for personal usage on some channels I 
frequent, which explains some of the most idiosyncratic things about it. Pull requests may be accepted if they don't conflict with
my personal usage.

## Building the bot
The project should import just fine in any IDE that can import the Maven pom.xml. 
Build the jar file by running `mvn clean compile assembly:single`
Requires Java 8+.

## Running the bot
Make a copy of the sample_config.properties to data/config.properties. 
Set the settings to values relevant to you. 
Run `java -jar yourjar.jar.`

## Implementing new commands
New commands and features can be added by implementing the CommandHandler interface.
Implementations need to be instantiated in the BotLogic class.
Except for UserDb in some cases, implementors only need to access the BotContext to handle and reply to commands.

### Reading command arguments
CommandMatcher helps in reading commands with multiple arguments.

### Storing and persisting settings and user data
The Bot provides a mechanism to store and retrieve user settings (UserDb). The settings are persisted to disk.
Const is used to read settings from the config file. 
While I do not respect this convention myself, it's best to name your settings based on the fully qualified class name or 
package name of the handler that will use it to avoid conflicts.

### Caching
The CachedResource class can be used for easily accessing resources and caching them.
