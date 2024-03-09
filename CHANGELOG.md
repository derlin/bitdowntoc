# Changelog

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
