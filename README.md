# ABOUT ISSUE (regarding this fork)

[upstream pull request](https://github.com/LemonCaramel/caramelChat/pull/45)

caramelChat mod is based on native library CocoaInput-lib and this library is have some issues in linux x11 environments.
This fork is using patched CocoaInput-lib library from [CocoaInput-lib pr](https://github.com/Korea-Minecraft-Forum/CocoaInput-lib/pull/5).
but this is temporary solution, not resolving issues completely

Provides an enhanced IME input experience in Minecraft.

<img src="common/src/main/resources/icon.png" width="96" alt="caramelChat Icon"/>

# caramelChat

---

## 📕 Introduction
caramelChat is modern chat mod inspired by the input method of [CocoaInput](https://github.com/Axeryok/CocoaInput).

## 💻 Compatibility
Currently, caramelChat uses [CocoaInput-lib](https://github.com/Korea-Minecraft-Forum/CocoaInput-lib).
It must be the same as the OS compatibility of CocoaInput.

We plan to use our Native library in caramelChat v2.0.
Compatibility will gradually improve.

|             OS              |         Compatibility         |
|:---------------------------:|:-----------------------------:|
|    **Windows** (x86_64)     |         🟢 Compatible         |
|     **Windows** (arm64)     |        🔴 Incompatible        |
|      **macOS** (Intel)      |         🟢 Compatible         |
|  **macOS** (Apple Silicon)  |         🟢 Compatible         |
|   **Linux X11** (x86_64)    | 🟡 Incompatible in some cases |
|    **Linux X11** (arm64)    |        🔴 Incompatible        |
| **Linux Wayland** (x86_64)  | 🟡 Incompatible in some cases |
|  **Linux Wayland** (arm64)  |        🔴 Incompatible        |

Below is the ModLoader compatibility.

|    Platform    |    Support    |
|:--------------:|:-------------:|
| Fabric / Quilt | 🟢 Compatible |
|     Forge      | 🟢 Compatible |
|    NeoForge    | 🟢 Compatible |

## 🛠️ Troubleshooting (macOS 14.0+)
If you are using macOS Sonoma or later versions, you may experience the following issue:
- Some characters are skipped when typing very quickly.
- The client crashes when a system key is pressed (e.g., input source switch).

Most of the causes are due to the Input Tooltip added in Sonoma. However, Apple has not provided an API to disable it.

![macOS Sonoma Indicator](https://github.com/LemonCaramel/caramelChat/assets/45729082/e1d34917-1892-4cb6-aa3f-38fdab58fad9)


You can disable the Input Tooltip system-wide through the following guide.

Open the Terminal and enter the following command:
```Bash
sudo mkdir -p /Library/Preferences/FeatureFlags/Domain
sudo /usr/libexec/PlistBuddy -c "Add 'redesigned_text_cursor:Enabled' bool false" /Library/Preferences/FeatureFlags/Domain/UIKit.plist
```
And then, reboot your Macintosh. This will return you to the input environment from before Sonoma.

## 🛠️ Troubleshooting (Linux Wayland)

In some Linux distributions that use the Wayland protocol, crashes may occur.
This happens because Xwayland is present in the system, causing GLFW to attempt to run based on X11.

Fortunately, there are third-party mods available to address this issue.

**WayGL:** [Modrinth](https://modrinth.com/mod/waygl), [Github](https://github.com/wired-tomato/WayGL)

## 🛠️ Troubleshooting (Linux Fcitx5)

Fcitx5 may not work without using the "On The Spot" style.

1. Assuming your configuration program is `fcitx5-config-qt`, Head to `Fcitx Configuration -> Addons`, find `X Input Method Frontend` and click the cog button in its row.
2. Enable the `Use On The Spot Style (Needs restarting)` option then reboot.
3. It should be working now.

## 🚀️ Contributing
All contributions are welcome regardless of Native or Java.

## 📜 License
caramelChat is licensed under GNU LGPLv3, a free and open-source license. For more information, please see the [license file](LICENSE).

When submitting pull requests to this repository, it is assumed that you are licensing your contribution under the
GNU LGPLv3, unless you state otherwise.
