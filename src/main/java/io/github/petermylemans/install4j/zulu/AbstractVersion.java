package io.github.petermylemans.install4j.zulu;

import org.json.JSONArray;

import java.util.Objects;

public class AbstractVersion implements Comparable<AbstractVersion> {

    private final int major;
    private final int minor;
    private final int revision;
    private int patch;

    public AbstractVersion(final JSONArray versionArray) {
        major = versionArray.getInt(0);
        minor = versionArray.getInt(1);
        revision = versionArray.getInt(2);
        patch = versionArray.getInt(3);
    }

    public AbstractVersion(final String versionString) {
        final String[] versionComponents = versionString.split("[.-]");
        major = Integer.parseInt(versionComponents[0]);
        minor = Integer.parseInt(versionComponents[1]);
        revision = Integer.parseInt(versionComponents[2]);
        if (versionComponents.length > 3) {
            patch = Integer.parseInt(versionComponents[3]);
        }
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRevision() {
        return revision;
    }

    public int getPatch() {
        return patch;
    }

    @Override
    public String toString() {
        return "" + major + "." + minor + "." + revision + (patch > 0 ? "." + patch : "");
    }

    public String asExactString() {
        return "" + major + "." + minor + "." + revision + "." + patch;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractVersion that = (AbstractVersion) o;
        return major == that.major &&
            minor == that.minor &&
            revision == that.revision &&
            patch == that.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, revision, patch);
    }


    @Override
    public int compareTo(final AbstractVersion o) {
        final int byMajor = Integer.compare(major, o.major);
        if (byMajor != 0) {
            return byMajor;
        }

        final int byMinor = Integer.compare(minor, o.minor);
        if (byMinor != 0) {
            return byMinor;
        }

        final int bySecurity = Integer.compare(revision, o.revision);
        if (bySecurity != 0) {
            return bySecurity;
        }

        return Integer.compare(patch, o.patch);
    }

}
