<div align="center">
  <p>
    <img src="https://media.discordapp.net/attachments/1261075855183188085/1290694645860274246/New_Piskel.png?ex=66fd648f&is=66fc130f&hm=c080ce1ee5f8b38acbe367d7c13a2faae64ed55cf9f7496f1c184c20fbd9e57e&=&format=webp&quality=lossless&width=300&height=300">
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
* 😎 **Emoji support** in a very similar way as Discord: `:emoji_name:`
* 📢 **Built-in announcements** with a configurable interval. Can broadcast messages through the action bar
* 🤫 **Private messages** with social spy for staff members
* 🤬 **Chat filters** that block IPs, URLs or words. Even in private messages
* 🌊 **Flood/spam prevention**
* ☕️ **Developer-friendly API** with countless possibilities. Developers can add custom channels, reactions, emojis, filters, keywords...
* 👀 **No dependencies**

## ⚠️ Compatibility chart

|                                                         | Compatible? | Version | Notes                                        |
|---------------------------------------------------------|-------------|---------|----------------------------------------------|
| [PaperMC](https://papermc.io/)                          | ✅          | 1.21+   | Use the legacy version for 1.20 support      |
| [PurpurMC](https://purpurmc.org/)                       | ✅          | 1.21+   | Use the legacy version for 1.20 support      |
| [Spigot](https://www.spigotmc.org)                      | ✅          | 1.20+   | Consider using [PaperMC](https://papermc.io) |
| [Bukkit](https://bukkit.org)                            | ✅          | 1.20+   | Consider using [PaperMC](https://papermc.io) |
| [Folia](https://papermc.io/software/folia)              | ❌          |         |                                              |

## 🔌 Integrations

### 💭 [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- Automatically compatible with every placeholder added by PlaceholderAPI.

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

## 🔧 First run

When you run social for the very first time it will automatically generate two files:
* 'settings.yml' contains general settings
* 'messages.yml' contains configurable messages

You can disable any feature by modifying `settings.yml`

## ✏️ Key concepts
### 💬 Parsers
**social** provides a set of powerful built-in tools in the form of **parsers**. Parsers are rules that modify text accordingly. Here's a list of parser types:

|              | Description                                  | Format         | Triggerable by players? | Example                 |
|--------------|----------------------------------------------|----------------|-------------------------|-------------------------|
| Filter       | Replace expressions or words with '***'      | _configurable_ | ✅                      | `192.168.1.1 ➡️ ***`    |
| Emoji        | Replaces a word with a unicode input (emoji) | :emoji:        | ✅                      | `:smile:     ➡️ 😄`     |
| Keyword      | Replaces a word with a specific component    | [keyword]      | ✅                      | `[balance]   ➡️ 10.4$`  |
| Placeholder  | Replaces a word with a specific component    | $placeholder   | ❌                      | `$channel    ➡️ global` |

<div id="credits"></div>

# ⭐️ Credits

## 🫶 Special thanks _(in alphabetical order)_

| Username                                           | Contribution                              |
|----------------------------------------------------|-------------------------------------------|
| [@deltartz_](https://www.instagram.com/deltartz_/) | For her great work on the logo            |
| Jekyll                                             | For his bug reports and financial support |

## ☕️ The myth-MC Team _(in alphabetical order)_
| Username                           | Real name       | Role                 |
|------------------------------------|-----------------|----------------------|
| [@U8092](https://github.com/U8092) | N/A             | Lead developer       |
