# caramelChat

![GitHub Issues](https://img.shields.io/github/issues/LemonCaramel/caramelChat.svg)
![GitHub Tag](https://img.shields.io/github/tag/LemonCaramel/caramelChat.svg)

Provides an enhanced IME input experience in Minecraft.

---

## 📕 Introduction
caramelChat is modern chat mod inspired by the input method of [CocoaInput](https://github.com/Axeryok/CocoaInput).

## 💻 Compatibility
Currently, caramelChat uses [CocoaInput-lib](https://github.com/Korea-Minecraft-Forum/CocoaInput-lib).
It must be the same as the OS compatibility of CocoaInput.

We plan to use our Native library in caramelChat v2.0.
Compatibility will gradually improve.

|            OS             |         Compatibility         |
|:-------------------------:|:-----------------------------:|
|   **Windows** (x86_64)    |         🟢 Compatible         |
|    **Windows** (arm64)    |        🔴 Incompatible        |
|     **macOS** (Intel)     |         🟢 Compatible         |
| **macOS** (Apple Silicon) |         🟢 Compatible         |
|  **X11 Linux** (x86_64)   | 🟡 Incompatible in some cases |
|   **X11 Linux** (arm64)   |        🔴 Incompatible        |

Below is the ModLoader compatibility.

|     Platform     |    Support    |
|:----------------:|:-------------:|
|  Fabric / Quilt  | 🟢 Compatible |
| Forge / NeoForge | 🟢 Compatible |

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

## 🚀️ Contributing
All contributions are welcome regardless of Native or Java.

## 📜 License
caramelChat is licensed under GNU LGPLv3, a free and open-source license. For more information, please see the [license file](LICENSE).

When submitting pull requests to this repository, it is assumed that you are licensing your contribution under the
GNU LGPLv3, unless you state otherwise.
