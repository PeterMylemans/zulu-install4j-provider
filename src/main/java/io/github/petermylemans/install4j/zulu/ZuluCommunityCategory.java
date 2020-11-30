package io.github.petermylemans.install4j.zulu;

import com.install4j.jdk.spi.JdkCategoryNode;

import java.util.ArrayList;
import java.util.List;

public class ZuluCommunityCategory implements JdkCategoryNode {

    private final int majorVersion;
    private final List<ZuluCommunityReleaseNode> releaseNodes = new ArrayList<>();

    public ZuluCommunityCategory(final int majorVersion) {
        this.majorVersion = majorVersion;
    }

    @Override
    public List<ZuluCommunityReleaseNode> getChildren() {
        return releaseNodes;
    }

    @Override
    public String getDisplayName() {
        return "zulu" + majorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }
}
