package io.github.petermylemans.install4j.zulu;

import org.json.JSONArray;

import java.util.Objects;

public class ZuluVersion implements Comparable<ZuluVersion> {

    private int major;
    private int minor;
    private int security;
    private int patch;

    private int jdkMajor;
    private int jdkMinor;
    private int jdkSecurity;
    private int jdkPatch;

    public ZuluVersion(JSONArray zuluVersionArray, JSONArray jdkVersionArray) {
        major = zuluVersionArray.getInt(0);
        minor = zuluVersionArray.getInt(1);
        security = zuluVersionArray.getInt(2);
        patch = zuluVersionArray.getInt(3);
        jdkMajor = jdkVersionArray.getInt(0);
        jdkMinor = jdkVersionArray.getInt(1);
        jdkSecurity = jdkVersionArray.getInt(2);
        jdkPatch = jdkVersionArray.getInt(3);
    }

    // versionString is Zulu <zulu version> (OpenJDK <jdk version>)
    public ZuluVersion(String versionString) {
        final String[] configComponents = versionString.split(" ");
        if (configComponents.length > 1) {
            final String[] zuluVersionComponents = configComponents[1].split("[.-]");
            major = Integer.parseInt(zuluVersionComponents[0]);
            minor = Integer.parseInt(zuluVersionComponents[1]);
            security = Integer.parseInt(zuluVersionComponents[2]);
            if (zuluVersionComponents.length > 3) {
                patch = Integer.parseInt(zuluVersionComponents[3]);
            }
        }
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getSecurity() {
        return security;
    }

    public int getPatch() {
        return patch;
    }

    @Override
    public String toString() {
        return toConfigKey();
    }

    public String getZuluVersion() {
        return "" + major + "." + minor + "." + security + "." + patch;
    }

    public String getJDKVersion() {
        return "" + jdkMajor + "." + jdkMinor + "." + jdkSecurity + "." + jdkPatch;
    }

    public String toConfigKey() {
        return "Zulu " + major + "." + minor + "." + security + (patch > 0 ? "." + patch : "") + " (OpenJDK " + jdkMajor + "." + jdkMinor + "." + jdkSecurity + "." + jdkPatch + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZuluVersion that = (ZuluVersion) o;
        return major == that.major &&
                minor == that.minor &&
                security == that.security &&
                patch == that.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, security, patch);
    }


    @Override
    public int compareTo(ZuluVersion o) {
        final int byMajor = Integer.compare(major, o.major);
        if (byMajor != 0) {
            return byMajor;
        }

        final int byMinor = Integer.compare(minor, o.minor);
        if (byMinor != 0) {
            return byMinor;
        }

        final int bySecurity = Integer.compare(security, o.security);
        if (bySecurity != 0) {
            return bySecurity;
        }

        return Integer.compare(patch, o.patch);
    }

}
