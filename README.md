Skyblock-Public
=======================

This project contains certain internal components of the Skyblock plugin that have been open-sourced. This allows the public to freely contribute to any of the features provided in this repository.

**Please note:** This project in its current state will not compile on its own due to its dependency on private Skyblock source code. _There is no API currently._
 
Contributors
------------
* [lavuh](https://github.com/lavuh) (Project development)

Feel free to contribute any necessary changes or fixes!

Features
--------
- `Parent/Public Module`: Packaged up upon compilation, combined with private source code to produce a runnable plugin.
- `Chronology Module`: Contains all current Skyblock chronology tasks. 
- `Minigames Module`: Contains all current Skyblock minigames.

Compiling
---------
Compiling is not recommended at this current stage. You will need to install [Maven](https://maven.apache.org/) to compile the following:
* Spigot/CraftBukkit libraries from [BuildTools](https://www.spigotmc.org/wiki/buildtools/)
* Floating-Anvil libraries, which are not open source. There is no API currently.

Once you have these libraries compiled, on your commandline, type the following.
```
cd /path/to/Skyblock-Public
mvn clean install
```
Maven automatically downloads the other required dependencies.
Output JAR will be placed in the `/Skyblock-Public/target` folder. 
>This output is merged with the private source code when the Skyblock plugin is compiled.