package info.bitrich.xchangestream.dsx.dto.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum DsxModeType {
    LIVE,
    DEMO;

    public static DsxModeType getMode(String event) {
        return Arrays.stream(DsxModeType.values())
                .filter(e -> StringUtils.equalsIgnoreCase(event, e.name()))
                .findFirst()
                .orElse(null);
    }
}
