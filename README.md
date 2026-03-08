<div align="center">
  <p>
    <img src="https://assets.mythmc.ovh/social/logo-small.png?1">
    <h1>social</h1>
    <a href="https://github.com/myth-MC/social/releases/latest"><img src="https://img.shields.io/github/v/release/myth-MC/social" alt="Latest release" /></a>
    <a href="https://github.com/myth-MC/social/pulls"><img src="https://img.shields.io/github/issues-pr/myth-MC/social" alt="Pull requests" /></a>
    <a href="https://github.com/myth-MC/social/issues"><img src="https://img.shields.io/github/issues/myth-MC/social" alt="Issues" /></a>
    <a href="https://github.com/myth-MC/social/blob/main/LICENSE"><img src="https://img.shields.io/badge/license-GPL--3.0-blue.svg" alt="License" /></a>
    <br>
    Enhance your server's communication.
    <br>
    Modular, customizable and feature-packed.
  </p>
</div>

<details open="open">
  <summary>🧲 Quick navigation</summary>
  <ol>
    <li>
      <a href="#information">📚 Information</a>
    </li>
    <li>
      <a href="#installation">📥 Installation</a>
    </li>
    <li>
      <a href="#usage">🖊️ Usage</a>
    </li>
    <li>
      <a href="#credits">⭐️ Credits</a>
    </li>
  </ol>
</details>

<div id="information"></div>

# 📚 Information

**social 🦜** is a fully modular plug-in focused on **enhancing your server's communication** without affecting the gameplay experience. 
It is developed using modern APIs, providing a **simple but powerful tool** for server owners and developers. 
**Everything can be configured**, including messages.

## 🤔 Features

* ✨ Fully compatible with [MiniMessage](https://docs.advntr.dev/minimessage/index.html) and [PlaceholderAPI](https://wiki.placeholderapi.com) to have great-looking messages
* 🗣️ **Channel-based chat provider**. You can add or remove channels according to your server's needs. For example, you could have a global and a staff channel
* 😲 **Reactions** that appear above a player's head with nice animations
* 🧵 **Chat replies and threads**. Players can click on a message to reply to it.
* 🛡️ **Group chat channels**. Players can create their own group channel with `/group`. Server owners can see group messages with Social Spy.
* 😎 **Emoji support** in a very similar way as Discord: `:emoji_name:`
* ‼️ **Mentions** that work with usernames and nicknames.
* 🔗 **Server links**. Available since Minecraft 1.21, server links are a great way of sharing external resources with your players in the pause menu
* 📢 **Built-in announcements** with a configurable interval. Can broadcast messages through the action bar
* 🤫 **Private messages** with social spy for staff members
* 🖌️ **Advanced text formatting** with **bold**, _italics_, <ins>underline</ins> and more options
* 🤬 **Chat filters** that block IPs, URLs or words. Even in private messages
* 🌊 **Flood/spam prevention**
* ☕️ **Developer-friendly API** with countless possibilities. Developers can add custom channels, reactions, emojis, filters, keywords, formatters...
* 👀 **No dependencies**

## ⚠️ Compatibility chart

|                                                         | Compatible? | Version | Notes                                        |
|---------------------------------------------------------|-------------|---------|----------------------------------------------|
| [PaperMC](https://papermc.io/)                          | ✅          | 1.19+   | *1                                           |
| [PurpurMC](https://purpurmc.org/)                       | ✅          | 1.19+   | *1                                           |
| [Spigot](https://www.spigotmc.org)                      | ✅          | 1.19+   | *1                                           |
| [Bukkit](https://bukkit.org)                            | ✅          | 1.19+   | *1                                           |
| [Folia](https://papermc.io/software/folia)              | ✅          | 1.19+   | *1                                           |

*1: _Some features require the latest version_

## 🔌 Integrations

### 🏷️ [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)
- Compatible with every placeholder added by PlaceholderAPI.

### 💬 [DiscordSRV](https://github.com/DiscordSRV/DiscordSRV)
- Compatible with chat channels.

### 🐷 [banco](https://github.com/myth-MC/banco)
- Adds keyword `[balance]` to show balance amount in chat.

## ➕ Official add-ons

### 👤 [social-chatheadfont](https://github.com/myth-MC/social-chatheadfont-addon)
- Provides compatibility with [ChatHeadFont](https://github.com/OGminso/ChatHeadFont).

<div id="installation"></div>

# 📥 Installation

1. **Download the social jar file for your platform**. You can find the latest version on [our releases page](https://github.com/myth-MC/social/releases).
2. **Add the social jar file to your server's plugin folder**. Make sure to delete any older versions of social.
3. **Fully restart your server**. Type `/stop` and start the server again [instead of using `/reload`](https://madelinemiller.dev/blog/problem-with-reload/).

<div id="usage"></div>

# 🖊️ Usage

We have a simple guide on using **social** in our docs website: https://social.mythmc.ovh/docs

## ✏️ Key concepts
### 💬 Parsers
**social** provides a set of powerful built-in tools in the form of **parsers**. Parsers are rules that modify text accordingly. Here's a list of parser types:

|              | Description                                     | Format         | Triggerable by players? | Example                 |
|--------------|-------------------------------------------------|----------------|-------------------------|-------------------------|
| Filter       | Replaces a literal with '***'                   | _configurable_ | ✅                      | `192.168.1.1 ➡️ ***`    |
| Formatter    | Gives a special format to a literal             | _configurable_ | ✅                      | `**hi**      ➡️ `**hi** |
| Emoji        | Replaces a literal with a unicode input (emoji) | :emoji:        | ✅                      | `:smile:     ➡️ 😄`     |
| Keyword      | Replaces a literal with a specific component    | [keyword]      | ✅                      | `[balance]   ➡️ 10.4$`  |
| Placeholder  | Replaces a literal with a specific component    | $placeholder   | ❌                      | `$channel    ➡️ global` |

<div id="credits"></div>

# ⭐️ Credits

## 🫶 Special thanks _(in alphabetical order)_

| Username                                           | Contribution                              |
|----------------------------------------------------|-------------------------------------------|
| [@deltartz_](https://www.instagram.com/deltartz_/) | For her great work on the logo and assets |
| Jekyll                                             | For his bug reports and financial support |

## ☕️ The myth-MC Team _(in alphabetical order)_
| Username                           | Real name       | Role                 |
|------------------------------------|-----------------|----------------------|
| [@U8092](https://github.com/U8092) |                 | Lead developer       |

<hr>
