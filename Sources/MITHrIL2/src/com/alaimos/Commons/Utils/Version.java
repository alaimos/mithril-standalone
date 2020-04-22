package com.alaimos.Commons.Utils;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.Contract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * <p>Utility to compare version.</p>
 *
 * @author yclian
 * @version 20120507
 * @see <a href="https://gist.github.com/yclian/2627608">Version Comparator in Java</a>
 * @since 20120507
 */
public final class Version {

    private static final Pattern PATTERN_APPROXIMATION = compile("^([\\d\\.]+\\.)*(\\d+)$");

    private ComparableVersion mVersion;

    private Version(String v) {
        mVersion = new ComparableVersion(v);
    }

    @Contract("_ -> !null")
    public static Version compare(String v) {
        return new Version(v);
    }

    /**
     * <p>Return the result of
     * {@link Comparable#compareTo(Object)}. Very limited as it doesn't support {@code &gt;=}, {@code &lt;=} and {@code ~&gt;}.</p>
     *
     * @param v a version string
     * @return comparison
     */
    public int with(String v) {
        return mVersion.compareTo(new ComparableVersion(v));
    }

    public boolean eq(String v) {
        return with(v) == 0;
    }

    public boolean le(String v) {
        int c = with(v);
        return c == 0 || c == -1;
    }

    public boolean lt(String v) {
        return with(v) == -1;
    }

    public boolean ge(String v) {
        int c = with(v);
        return c == 0 || c == 1;
    }

    public boolean gt(String v) {
        return with(v) == 1;
    }

    /**
     * <p>Approximately greater than, inspired by (and works exactly like) RubyGems.</p>
     *
     * @param v a version
     * @return a comparison
     * @see <a href="http://docs.rubygems.org/read/chapter/16">RubyGems Manuals - Specifying Versions</a>
     */
    public boolean agt(String v) {
        return ge(v) && lt(approximateUpper(v));
    }

    private String approximateUpper(String v) {
        Matcher m = PATTERN_APPROXIMATION.matcher(v.split("\\.\\d+$")[0]);
        if (m.find()) {
            int i = Integer.parseInt(m.group(2));
            return (null != m.group(1) ? m.group(1) : "") + ++i;
        } else {
            return v;
        }
    }
}
