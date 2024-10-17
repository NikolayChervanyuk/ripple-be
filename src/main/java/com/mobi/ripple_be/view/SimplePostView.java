package com.mobi.ripple_be.view;

import java.time.Instant;

public interface SimplePostView {

    String getId();
    String getPostImageDir();
    String getAuthorId();
    Instant getCreationDate();
}
