# 🆕 What's New in the Latest Version

The latest version brings a rich set of enhancements and new components that improve user control, flexibility, and overall experience.

---

## 🎨 New: Battery Graph Settings Panel

A full-featured interface has been added for customizing how battery data is visualized and managed.

- **Appearance Settings**
    - Select visual themes, line color, background, and axis color.
    - Language support for English and العربية.

- **Chart Settings**
    - Choose between Line Chart and Area Chart.
    - Enable or disable data points, tooltips, and grid lines.
    - Control visibility of X/Y axis labels.

- **Time & Zoom Settings**
    - Auto-update graph in real time.
    - Zoom dynamically into specific regions.
    - Set minimum and maximum zoom levels.

- **Battery Alerts**
    - Define alert threshold by percentage.
    - Choose custom color for alert indicators.

- **Save & Load Options**
    - Choose save format (CSV or JSON).
    - Auto-save based on number of records.

---

## 🔔 New: Notification Settings

Improved notification flexibility through customizable visual and text alerts.

- **Enable Text Alerts:** Show pop-up notifications for battery events.
- **Customize Alert Text:** Modify alert content for clarity and personalization.

---

## 🧲 New: Tray Icon Settings

Enhanced integration with the system tray for a seamless user experience.

- **Start with Tray Window:** Option to launch app minimized to tray.
- **Tray Icon Preview:** Real-time visual preview based on current theme.
- **About Sections:** Quick access to "About Tray Panel" and "About Tray Integration".

---

## 🔄 New: Update Settings Interface

A centralized section for managing app updates and version control.

- **Auto Update Options**
    - Check for updates automatically.
    - Download and install updates in the background.
    - Optional prompt before applying updates.
    - Auto-restart after successful update.

- **Version Info & Manual Control**
    - View current and latest version numbers.
    - Manually check for updates.
    - View detailed release notes.
    - Download updates manually.
    - Rollback to a previous version (if needed).

---

## 📖 New: Internal Mini Documentation Browser

A built-in advanced documentation browser has been introduced.

- Allows browsing internal help topics in tabbed format.
- Topics are listed in a sidebar and opened in dynamic tabs.
- Supports HTML-based internal and external documents.
- Includes scroll-to-section functionality and dark/light themes.
- Full isolation from main UI for focused reading and help access.

---

## 🐞 Bug Fixes

Several issues have been resolved to improve stability and reliability:

- **Windows Installer Freeze Fix:**  
  Resolved an issue where the silent installer would hang due to the application still running in the background. The setup process now forcefully closes the running instance before installation to ensure a smooth update.

- **Markdown Loading Issue:**  
  Fixed a problem where release notes (release_notes.md) failed to load if the app was executed from a compiled `.exe` file. The loader now dynamically determines the executable's path to correctly locate and read the markdown file.

- **Improved Java Process Termination (Windows):**  
  Replaced static taskkill approach with a PowerShell-based process lookup that accurately identifies and terminates only the Battorion-related Java process, avoiding unintended closures of unrelated Java applications.

- **Wine Installer Support (Linux/macOS):**  
  Added logic to detect and close Battorion processes launched under Wine before executing the silent installer, resolving update conflicts on non-Windows platforms.

---
Stay tuned for more enhancements in the next updates!