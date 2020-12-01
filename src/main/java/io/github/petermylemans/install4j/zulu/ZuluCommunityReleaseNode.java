package io.github.petermylemans.install4j.zulu;

import com.install4j.jdk.spi.JdkReleaseNode;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ZuluCommunityReleaseNode implements JdkReleaseNode {

    public ZuluCommunityReleaseNode(final ZuluVersion version) {
        this.version = version;
    }

    private final ZuluVersion version;
    private OpenJDKVersion openJDKVersion;
    private final Map<String, String> platformDownloads = new TreeMap<>();

    void addPlatform(final String url) {
        if (url.endsWith("macosx.tar.gz") || url.endsWith("macosx_x64.tar.gz")) {
            platformDownloads.put("macos-amd64", url);
        } else if (url.endsWith("win64.zip") || url.endsWith("win_x64.zip")) {
            platformDownloads.put("windows-amd64", url);
        } else if (url.endsWith("win_i686.zip")) {
            platformDownloads.put("windows-x86", url);
        } else if (url.endsWith("macosx_x64.zip")) {
            // Only add it if no tar.gz
            platformDownloads.putIfAbsent("macos-amd64", url);
        } else if (url.endsWith("linux_x64.tar.gz")) {
            platformDownloads.put("linux-amd64", url);
        } else if (url.endsWith("linux_aarch64.tar.gz")) {
            platformDownloads.put("linux-aarch64", url);
        }
    }

    public OpenJDKVersion getOpenJDKVersion() {
        return openJDKVersion;
    }

    public void setOpenJDKVersion(final OpenJDKVersion openjdkVersion) {
        this.openJDKVersion = openjdkVersion;
    }

    @Override
    public String getDownloadUrl(final String platform) {
        return platformDownloads.get(platform);
    }

    @Override
    public String getFileName(final String platform) {
        final String downloadUrl = getDownloadUrl(platform);
        return downloadUrl != null ? Paths.get(downloadUrl).getFileName().toString() : null;
    }

    @Override
    public String getConfigKey() {
        return version.toString();
    }

    @Override
    public List<String> getPlatforms() {
        return new ArrayList<>(platformDownloads.keySet());
    }

    @Override
    public String getDisplayName() {
        return "Zulu " + getVersion() + " (OpenJDK " + getOpenJDKVersion() + ")";
    }

    public ZuluVersion getVersion() {
        return version;
    }
}
