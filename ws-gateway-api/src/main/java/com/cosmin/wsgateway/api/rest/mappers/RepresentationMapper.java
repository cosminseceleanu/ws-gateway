package com.cosmin.wsgateway.api.rest.mappers;

public interface RepresentationMapper<R, D> {
    D toModel(R representation);

    R toRepresentation(D domain);
}
