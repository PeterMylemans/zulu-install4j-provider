package io.github.petermylemans.install4j.zulu;

import org.json.JSONArray;

public final class ZuluVersion extends AbstractVersion {

    public ZuluVersion(final JSONArray versionArray) {
        super(versionArray);
    }

    public ZuluVersion(final String versionString) {
        super(versionString);
    }
}
