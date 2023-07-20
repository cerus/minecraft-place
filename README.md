# r/place in Minecraft

> **r/place 2023 is live!** https://reddit.com/r/place
>
> Unofficial server: place.cerus.dev

r/place is a collaborative project and social experiment hosted on the social networking site Reddit. Originally launched on April Fools' Day 2017, it
has since been repeated again on April Fools' Day 2022, and is taking place for a third time on July 20, 2023. The experiment involves an online
canvas located at a subreddit called r/place, which registered users could edit by changing the color of a single pixel from a 16-color
palette. [Wikipedia](https://en.wikipedia.org/wiki/Place_(Reddit)), [Subreddit](https://www.reddit.com/r/place/)

![Gif of r/place in Minecraft (2022)](https://cerus.dev/img/rplace_gif.gif)

<hr>

## \> Update July 20 2023

r/place has opened again for a third time. This plugin has been updated to support the new "season". Just follow the installation instructions below.

<hr>

## How it works

The plugin connects to a Reddit websocket that controls the canvas. After authenticating the plugin is able to subscribe to canvas updates.

## Installation

**This plugin only works on 1.20.1 servers!**

1. Download the plugin [here](https://github.com/cerus/minecraft-place/releases/download/1.3.0/minecraft-place.jar)
2. Drop the plugin into your plugins folder
3. Download the [latest maps release](https://github.com/cerus/maps/releases/download/3.7.1/maps-plugin.jar) and drop it
   into your plugins folder
4. Restart your server
5. Open `plugins/minecraft-place/config.yml`
6. Edit the credentials and save
    1. See 'How to create a Reddit app' if you don't know how to get a client id and secret
7. Restart your server again
8. Build a 24 (width) by 16 (height) rectangle and place item frames ([like this](https://i.imgur.com/9jUYFZG.png))
9. Go to the lower left corner, look into the middle of the lower left itemframe and type `/maps createscreen`
10. Done! (Optional: Restart your server one last time)

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