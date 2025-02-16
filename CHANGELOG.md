# Changelog

## [2.3.0](https://github.com/derlin/bitdowntoc/compare/v2.2.1...v2.3.0) (2025-02-16)


### 🚀 Features

* add a toc-only option to only output the TOC ([ac152dc](https://github.com/derlin/bitdowntoc/commit/ac152dca58a98517311b4fcfa5971642dad8cff8))
* **web:** improve options by showing inputs on the right ([b5ed719](https://github.com/derlin/bitdowntoc/commit/b5ed719646fda21515637487491b00119c250449))

## [2.2.1](https://github.com/derlin/bitdowntoc/compare/v2.2.0...v2.2.1) (2025-01-26)


### 🐛 Bug Fixes

* support 4 backticks ([ccbbb65](https://github.com/derlin/bitdowntoc/commit/ccbbb65485a86a929cdf8042ac3fb94fe58c70ba))
* **web:** don't break UI on long lines ([fd85878](https://github.com/derlin/bitdowntoc/commit/fd85878411818b27cf68b5a1ece76a8acfbe4e1d))


### 🦀 Build and CI

* add CNAME to Pages ([28bf64f](https://github.com/derlin/bitdowntoc/commit/28bf64f36da4f588ba681a0a837b9208cdca4346))


### 🌈 Styling

* add hover text to icons and some buttons ([bf25ec5](https://github.com/derlin/bitdowntoc/commit/bf25ec5b9bfb2983ff0a425cb2a26f1fc4231a63))
* update favicons ([69b1b27](https://github.com/derlin/bitdowntoc/commit/69b1b27fe3d22e98f6b9cadb96bee1b5eb033392))

## [2.2.0](https://github.com/derlin/bitdowntoc/compare/v2.1.0...v2.2.0) (2024-07-14)


### 🚀 Features

* properly raise error on missing toc end ([22d5bed](https://github.com/derlin/bitdowntoc/commit/22d5bedf1185e89f68ba097fe9b9e4ef6c781b3c))


### 🐛 Bug Fixes

* properly handle duplicates above the TOC placeholder ([fb6b9c5](https://github.com/derlin/bitdowntoc/commit/fb6b9c52b29f933dc4bee09076fde141c2f247d3))
* update to Kotlin Multiplatform 2.0.0 ([f624c62](https://github.com/derlin/bitdowntoc/commit/f624c62ca5f029813876852a522b3885f9c409ce))


### 🦀 Build and CI

* upload maven package to GitHub Packages ([4d8ea40](https://github.com/derlin/bitdowntoc/commit/4d8ea40971b5cfa039ded8c6df30b069e53c258c))

## [2.1.0](https://github.com/derlin/bitdowntoc/compare/v2.0.1...v2.1.0) (2024-05-09)


### 🚀 Features

* add hashnode support ([7e4ea1c](https://github.com/derlin/bitdowntoc/commit/7e4ea1cd127e3ab2cf282c48ef0b2c6b1a8122b7))
* add indent-spaces option ([481b0a6](https://github.com/derlin/bitdowntoc/commit/481b0a69562700e9bca87a05900a57637d14876d))
* add syntax highlighting in web ([e323a23](https://github.com/derlin/bitdowntoc/commit/e323a23468df557902be064c1e5897b400d16d6a))
* honor system dark theme preferences ([236f4d5](https://github.com/derlin/bitdowntoc/commit/236f4d597c891c860a9ea8c16a17e64ac994c249))
* improve web UI layout ([778d6b6](https://github.com/derlin/bitdowntoc/commit/778d6b62af2164ef52af85e1e3ef85597dc39fa3))
* support GitLab 17.0 ([a71350a](https://github.com/derlin/bitdowntoc/commit/a71350a22d3869b878b3ec25bcbc948204fe2071))
* support reading from stdin ([ce0908b](https://github.com/derlin/bitdowntoc/commit/ce0908b7a5609096d5179b583012d66d0a1ec3b8))


### 🐛 Bug Fixes

* make --version work in jar ([2509a80](https://github.com/derlin/bitdowntoc/commit/2509a805f3b56334ea0ce591a0c9851343955098))
* weird textarea behavior ([42de885](https://github.com/derlin/bitdowntoc/commit/42de885a3c2628540453557f4460f34a3a35da99))


### 💬 Documentation

* add badges to readme ([98bb1ea](https://github.com/derlin/bitdowntoc/commit/98bb1eaec0d1c5215aae5fd1354274b27b504853))
* add brew install instructions ([11c0525](https://github.com/derlin/bitdowntoc/commit/11c05256a485aeb33b02801c371b7d94bb350ca6))
* fix typos in readme ([cd2a504](https://github.com/derlin/bitdowntoc/commit/cd2a504e7993b3c7f21726a48174ffd71c6e7051))


### 🦀 Build and CI

* always upload jar artifacts ([0a70b50](https://github.com/derlin/bitdowntoc/commit/0a70b506582e655341991be73b242e570864f6ed))
* automatically update homebrew tap on release ([96bd118](https://github.com/derlin/bitdowntoc/commit/96bd1182d9582294131690fc91d9ec1074e553d8))
* fix generate native ([cdfd471](https://github.com/derlin/bitdowntoc/commit/cdfd471a9036f440dd39f135d2a365d9c6a58a71))
* run build on pull request ([7aa36a9](https://github.com/derlin/bitdowntoc/commit/7aa36a9bad8b882cc118108c89ed07219e5e6311))

## [2.0.1](https://github.com/derlin/bitdowntoc/compare/v2.0.0...v2.0.1) (2024-03-09)


### 🐛 Bug Fixes

* better handle codeblocks in lists ([23aa065](https://github.com/derlin/bitdowntoc/commit/23aa065459712ea0567365f258f76008b5a7453d))
* handle bolds and italics with underscores ([de60ad8](https://github.com/derlin/bitdowntoc/commit/de60ad87be0645b70cb56a1b1af69fb78ede9d3e))
* remove println upon generate ([54e0b36](https://github.com/derlin/bitdowntoc/commit/54e0b36db7043ceb840c2ed016aa19ecd25b7cce))


### 🦀 Build and CI

* avoid duplicate build on pull request ([24fdb11](https://github.com/derlin/bitdowntoc/commit/24fdb11e7b8c00eda6ec317b011b8f4929135c3c))

## [2.0.0](https://github.com/derlin/bitdowntoc/compare/v1.2.0...v2.0.0) (2023-09-22)


### ⚠ BREAKING CHANGES

* rename BitBucket Server to generic

### 🚀 Features

* better support code blocks ([82e14e3](https://github.com/derlin/bitdowntoc/commit/82e14e35804c3721aa729c1c0f35562ea0c54f74))
* rename BitBucket Server to generic ([bdd5f71](https://github.com/derlin/bitdowntoc/commit/bdd5f71c5553612b6ffc6cb93e9a2548434f76b7))


### 🐛 Bug Fixes

* indent list with 3 spaces ([9682e73](https://github.com/derlin/bitdowntoc/commit/9682e73e29bf1bd3b1240d1874caa4a44021b957)), closes [#8](https://github.com/derlin/bitdowntoc/issues/8)

## [1.2.0](https://github.com/derlin/bitdowntoc/compare/v1.1.0...v1.2.0) (2023-08-02)


### 🚀 Features

* add new lines inside TOC and link to bitdowntoc sources ([3021ca0](https://github.com/derlin/bitdowntoc/commit/3021ca04ccb483e9601c96c629420df5671cdfd4)), closes [#9](https://github.com/derlin/bitdowntoc/issues/9) [#10](https://github.com/derlin/bitdowntoc/issues/10)
* add opensource tracking using posthog ([5930805](https://github.com/derlin/bitdowntoc/commit/5930805ca91c9848c21c95f55ce66c23e4734550))
* serve minified CSS ([9ce2b93](https://github.com/derlin/bitdowntoc/commit/9ce2b93b0349be552fc7380422351ab9e272fa0e))
* **web:** improve CSS for mobile ([42c96b0](https://github.com/derlin/bitdowntoc/commit/42c96b0f8a0fd56c72f6fddc69f8756747536109))


### 🐛 Bug Fixes

* compound emojis in dev.to profile ([088c08e](https://github.com/derlin/bitdowntoc/commit/088c08e297cc928e4a55e773a7342be6a7a55bce))
* emojis in dev.to profile (again) ([b90cc64](https://github.com/derlin/bitdowntoc/commit/b90cc64696066f70bf51e921db45e0c4152d92e7))
* handle emojis, HTML tags, markdown links and quotes properly ([8624d95](https://github.com/derlin/bitdowntoc/commit/8624d9599f5c6820308201ae75d1e25e3ec30efc))
* use built-in anchors on dev.to ([4044e1a](https://github.com/derlin/bitdowntoc/commit/4044e1ab029f142254052ac89b16bab98ba619fb))


### 💬 Documentation

* update README ([867f98c](https://github.com/derlin/bitdowntoc/commit/867f98c556b346fecbcf76e91d4a4a0b7a9e38f1))

## [1.1.0](https://github.com/derlin/bitdowntoc/compare/v1.0.0...v1.1.0) (2022-12-30)


### 🌈 Styling

* change text over markdown textview ([ebed501](https://github.com/derlin/bitdowntoc/commit/ebed501ad983dbe26fcfb50b0ce6e9f6c47ce254))


### 🚀 Features

* support anchor prefixes ([a05ad85](https://github.com/derlin/bitdowntoc/commit/a05ad85e3074a4e7e6c9f1c7aa714e748b3a2be7))


### 💬 Documentation

* update README and interface for anchors prefix ([71f04bb](https://github.com/derlin/bitdowntoc/commit/71f04bbafff2aa514c19c0c7e52095385c1511eb))

## 1.0.0 (2022-12-29)


### 🚀 Features

* support GitHub, Gitlab, BitBucket Server, dev.to and more (thanks to anchors in the markdown)
* allow to re-generate/update existing TOC
* allow to control TOC placement with the `[TOC]` marker
* add options for levels, characters used for the TOC, and more
* support both a CLI (jar or native executables) and a web version
* ...
