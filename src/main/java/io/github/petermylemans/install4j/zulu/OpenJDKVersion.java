package io.github.petermylemans.install4j.zulu;

import org.json.JSONArray;

public final class OpenJDKVersion extends AbstractVersion {

    public OpenJDKVersion(final JSONArray versionArray) {
        super(versionArray);
    }

    public OpenJDKVersion(final String versionString) {
        super(versionString);
    }
}
