<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# encrypt-string-plugin Changelog

## Unreleased

## 1.2.2-beta.2 - 2024-11-05

### Fixed

- some specials chars in password are lost when deciphering text (#20)

## 1.2.1 - 2024-10-09

### Fixed

- replace deprecated usage of jetbrains api (#17)

## 1.2.0 - 2024-02-08

### Added

- Remember password option
- icon for line marker indicates if the stored password is valid or not
- checks that the selected text is encapsulated and auto-select the "ENC(...)" checkbox.
- when an error occurs during decrypt, a notification is shown in the ide.
- Line marker on yaml file with encrypted string (with ENC(...))

## 1.2.0-beta.3 - 2024-02-02

### Added

- Remember password option
- icon for line marker indicates if the stored password is valid or not
- checks that the selected text is encapsulated and auto-select the "ENC(...)" checkbox.
- when an error occurs during decrypt, a notification is shown in the ide.
- Line marker on yaml file with encrypted string (with ENC(...))

## 1.2.0-beta.2 - 2024-01-30

### Added

- checks that the selected text is encapsulated and auto-select the "ENC(...)" checkbox.
- when an error occurs during decrypt, a notification is shown in the ide.
- Line marker on yaml file with encrypted string (with ENC(...))

## 1.2.0-beta - 2024-01-25

### Added

- when an error occurs during decrypt, a notification is shown in the ide.
- Line marker on yaml file with encrypted string (with ENC(...))

## 1.1.0 - 2023-07-05

## 0.2.0

### Added

- new cipher algorithms
- remember preferred options (by project)

## 0.1.0 2022-08-30

### Added

- Actions to encrypt or decrypt selected string.
- the popup asks for password, algorithm and if we want to wrap/unwrap with "ENC()"
