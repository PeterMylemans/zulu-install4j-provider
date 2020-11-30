# Azul Zulu Community Provider

This project implements install4j's JdkProvider interface to automate downloading and packaging JRE bundles into your installers.

## To build

Make sure that you have Java JDK version 11 or later installed
```
./mvnw clean package
```


## To install

Copy `zulu-install4j-provider-x.y.z.jar` to the extensions folder of install4j.

You can find this folder typically in:
 * Windows `C:\Program Files\install4j8\extensions`
 * MacOS `/Applications/install4j.app/Contents/Resources/app/extensions`
 * Linux `/opt/install4j8/extensions`
