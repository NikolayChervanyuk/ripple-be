package com.mobi.ripple_be.util;

import reactor.core.publisher.Mono;

public interface Authorable {

    Mono<Boolean> isAuthorized(String objectId);

}
