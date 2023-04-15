# Azul Zulu Community Provider (deprecated)

> **IMPORTANT:** This repository is now archived as similar functionality is now builtin when using more recent versions of install4j. See https://www.ej-technologies.com/resources/install4j/help/doc/concepts/jreBundles.html

This project implements install4j's JdkProvider interface to automate downloading and packaging JRE bundles into your installers.

## To build

Make sure you have Java JDK version 11 or later installed.
```
./mvnw clean package
```


## To install

Copy `zulu-install4j-provider-x.y.z.jar` to the extensions folder of install4j.

You can find this folder typically in:
 * Windows `C:\Program Files\install4j8\extensions`
 * MacOS `/Applications/install4j.app/Contents/Resources/app/extensions`
 * Linux `/opt/install4j8/extensions`
