package com.mobi.ripple_be.converter.modelentity;

import com.mobi.ripple_be.converter.BaseConverter;
import com.mobi.ripple_be.model.AppUserCredentialsModel;
import com.mobi.ripple_be.view.AppUserCredentialsView;
import org.springframework.stereotype.Component;

@Component
public class AppUserCredentialsViewToAppUserCredentialsModel
        extends BaseConverter<AppUserCredentialsView, AppUserCredentialsModel> {
    @Override
    public AppUserCredentialsModel convert(AppUserCredentialsView source) {
        return new AppUserCredentialsModel(
                source.getEmail(),
                source.getUsername(),
                source.getPassword()
        );
    }
}
