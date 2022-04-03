# r/place in Minecraft

Place is a collaborative project and social experiment hosted on the social networking site Reddit that began on April Fools' Day 2017 and was revived
again after 5 years on April Fools' Day 2022. The experiment involves an online canvas located at a subreddit called r/place, which registered users
could edit by changing the color of a single pixel from a 16-color palette. [Wikipedia](https://en.wikipedia.org/wiki/Place_(Reddit))
, [Subreddit](https://www.reddit.com/r/place/)

![Gif of r/place in Minecraft](https://cerus.dev/img/rplace_gif.gif)

## How it works

The plugin connects to a Reddit websocket that controls the canvas. After authenticating the plugin is able to subscribe to canvas updates.

## Installation

**This plugin only works on 1.16.5 - 1.18.2 servers!**

1. Drop the plugin into your plugins folder
2. Download the [latest experimental maps release](https://github.com/cerus/maps/releases/download/2.0.0-SNAPSHOT-pre3/maps-plugin.jar) and drop it
   into your plugins folder
3. Restart your server
4. Open `plugins/minecraft-place/config.yml`
5. Edit the credentials and save
    1. See 'How to create a Reddit app' if you don't know how to get a client id and secret
6. Restart your server again
7. Build a 16 by 8 rectangle and place item frames ([like this](https://i.imgur.com/kHjODX5.png))
8. Go to the lower left corner, look into the middle of the lower left itemframe and type `/maps createscreen`
9. Done! (Optional: Restart your server one last time)

Please note: The plugin will wait 10 seconds before rendering r/place after startup.

## How to create a Reddit app

1. Go to https://www.reddit.com/prefs/apps
2. At the bottom click 'create an app' / 'create another app'
3. Choose a random name, it doesn't matter
4. Select the 'script' type
5. Choose a random description, about url and redirect url

It should look like this:

![Image](https://i.imgur.com/GU8Rv4a.png)

Now create the app and copy the id and secret.

![Image](https://i.imgur.com/tDrtqTK.png)