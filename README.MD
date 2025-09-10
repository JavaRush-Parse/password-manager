# Password Manager

<img alt="JavaRush University" src="https://img.shields.io/badge/java-rush-orange?style=for-the-badge&color=orange&cacheSeconds=3600">

## Overview

**Password Manager** is a Java-based desktop (or CLI) application for securely storing and managing user credentials. It aims to provide a lightweight, intuitive solution for keeping passwords organized and encrypted.

---

## 🆕 Current Features

- Securely **add**, **view**, **edit**, and **delete** credentials (e.g. site, username, password).
- **Store data locally** using encrypted storage or via a protected file.
- **Master password** support for unlocking the vault.
- Basic **search functionality** for quick entry lookup.

##  Future Enhancements

- Import/export vault in JSON or CSV formats.
- Support for **password categories** or tags.
- **Password strength meter** and suggestions.
- Cross-platform GUI with JavaFX or Swing.
- Integration with **cloud storage** (e.g. Dropbox, Google Drive).
- Option for **auto-fill** in browsers (via plugin or extension).

---

##  Tech Stack & Tools

- **Java** (version X or higher)
- Build system: **Maven** (see `pom.xml`)
- Optional: Docker (via `compose.yaml`) for environment setup
- **JUnit** for testing (if applicable)
- IDE recommendations: IntelliJ IDEA, Eclipse, VS Code

---

##  Running Locally

1. **Clone the repository**  
   ```bash
   git clone https://github.com/JavaRush-Parse/password-manager.git
   cd password-manager

## File Structure
```
password-manager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── javrush/
│   │   │           └── passwordmanager/
│   │   │               ├── Main.java          
│   │   │               ├── model/             
│   │   │               ├── service/           
│   │   │               ├── repository/        
│   │   │               └── utils/           
│   │   └── resources/                      
│   └── test/
│       └── java/                              
│
├── .gitignore
├── pom.xml                                    
├── README.md                                 
└── LICENSE (optional)
```
### Authors:

- @oleksandr-jr
- @mykhailokasiian14
- @AGuliaiev
- @MetaM0rf-student
