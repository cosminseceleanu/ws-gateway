package com.cosmin.wsgateway.api.mappers;

import com.cosmin.wsgateway.api.representation.AuthenticationRepresentation;
import com.cosmin.wsgateway.domain.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationMapper implements RepresentationMapper<AuthenticationRepresentation, Authentication> {
    @Override
    public Authentication toModel(AuthenticationRepresentation representation) {
        if (representation instanceof AuthenticationRepresentation.None) {
            return new Authentication.None();
        }
        if (representation instanceof AuthenticationRepresentation.Basic) {
            return new Authentication.Basic(
                    ((AuthenticationRepresentation.Basic) representation).getUsername(),
                    ((AuthenticationRepresentation.Basic) representation).getPassword()
            );
        }

        return new Authentication.Bearer(
                ((AuthenticationRepresentation.Bearer) representation).getAuthorizationServerUrl()
        );
    }

    @Override
    public AuthenticationRepresentation toRepresentation(Authentication domain) {
        if (domain instanceof Authentication.Basic) {
            return new AuthenticationRepresentation.Basic(
                    ((Authentication.Basic) domain).getUsername(),
                    ((Authentication.Basic) domain).getPassword()
            );
        }
        if (domain instanceof Authentication.Bearer) {
            return new AuthenticationRepresentation.Bearer(
                    ((Authentication.Bearer) domain).getAuthorizationServerUrl()
            );
        }

        return new AuthenticationRepresentation.None();
    }
}
