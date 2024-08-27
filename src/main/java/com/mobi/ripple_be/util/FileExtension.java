package com.mobi.ripple_be.util;

public enum FileExtension {
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    RAR("rar"),
    ZIP("zip"),
    UNKNOWN("unknown");

    FileExtension(String extensionName) {
        this.extensionName = extensionName;
    }

    public final String extensionName;
}
