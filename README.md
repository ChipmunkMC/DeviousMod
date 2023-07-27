# DeviousMod v3 [![DeviousMod Discord](https://discordapp.com/api/guilds/1128986180575309924/widget.png?style=shield)](https://discord.gg/Bpd7ec7nft)

This is the premiere creative utility client.

## Features

- Crash prevention (decorated pot crash, double text array crash, null component exception)
- Freeze prevention (particle freeze, translation placeholder freeze, area effect cloud freeze, map icon count/name
  length freeze)
- Clientside/serverside kick prevention (tell kick, completion kick)
- Higher packet priority
- Watchdog (currently only watches the main thread)
- Chat tweaking (infinite chat, no "chat normalization")
- Username changer

## Modules

- ClientsideInventory (does not inform the server of any updates you make to your inventory unless necessary and ignores
  any updates it may make) (creative only)
- ItemLogger (logs the NBT of items that other players hold)
- CommandPlaceholders (executes every command suggestion)
- ConnectionErrorCanceller (ignores connection errors)
- Logos (just a watermark)
- Monitor (displays gamemode changes & server lag status)
- NoItemUseCooldown (removes the cooldown between using items)
- TrueSight (allows you to see invisible entities)
- OurWorldInNBT (experimental module which allows you to see the NBT of tile entities) (requires operator)

# Commands

- /bval - Use the hashing system of a Kaboom bot.
- /cnear - Displays the distance between you and every entity in your render distance.
- /cusername - Re/sets your username.
- /keyring - Manage your Kaboom bot keys.
- /getnbt - Displays the NBT of any items in your hand/offhand(s)
- /prettynbt - Pretty prints a given NBT tag

## Building

No builds are currently provided for your convenience. Please execute the following command after cloning this
repository, in the same directory as the cloned repository, depending on your operating
system, after configuring your environment variables to use a Java 17 JDK.

### Windows

```shell
.\gradlew.bat --no-daemon build
```

### Linux

```shell
./gradlew --no-daemon build
```

### Other

Please use the Linux build instructions.

## Usage

### Commands

DeviousMod does not opt to have a hackish command system like other clients. Instead, it uses the clientside Brigadier
command manager. You use commands with DeviousMod as you would with the server. Please note that there is a possibility
that incorrect command parameters will have the command you tried to execute sent to the server.

### Modules

Open RSHIFT to open the ClickGUI. You can enable, disable and configure the modules in their respective categories.
Click the arrow to open the settings of a module, if it has one, and click anywhere else on the module's button to
toggle it.

### Keyring

***WARNING: PLEASE ONLY USE THIS COMMAND IN SINGLEPLAYER TO AVOID SECRET LEAKING IF YOU MISTYPE THE COMMAND'S NAME!!!***

You can use the following command to add a bot's key to the keyring.

![/keyring add](./.github/assets/keyring_add.png)

The identifier parameter denotes the name you want to give to the key. This will be what you use in `/bval` when you use
the bot.

The algorithm parameter tells the client what hashing algorithm to use to compute the hash.

The template parameter tells the client what is to be hashed. Available templates:

- UUID - the UUID of your current session
- NAME - the username of your current session
- TIMESTAMP - The current timestamp, modified by the forgivenessSeconds parameter
- KEY - The shared secret specified by the key parameter
- COMMAND - The actual command you want to execute

The hash length parameter denotes how many bytes should be retained from the digest. Use -1 to not trim anything.

The forgiveness seconds parameter tells the client how many seconds it should remove from the timestamp. This is used in
authentication algorithms for allowing a person with a clock that isn't aligned exactly with the clock of the bot to use
the bot's privileged commands. Use 0 to disable.

The key parameter (is obviously) the shared secret of the bot.

### Example (HBot)

`/keyring add HBot sha-256 COMMAND;NAME;TIMESTAMP;KEY UTF-8 4 10 <KEY>`

![/keyring add HBot sha-256 COMMAND;NAME;TIMESTAMP;KEY UTF-8 4 10 KEY](./.github/assets/hbot_example.png)

## Prized Customer Testimonials

![Gir_nhll is such a shy little girl](./.github/assets/gir_nhll_testimonial.png)