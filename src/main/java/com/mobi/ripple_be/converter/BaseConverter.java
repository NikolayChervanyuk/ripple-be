package com.mobi.ripple_be.converter;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseConverter<S, T> implements Converter<S, T> {

    @Lazy
    @Autowired
    private ConversionService conversionService;

    protected ConversionService conversionService() {
        return conversionService;
    }

}
