package com.cosmin.wsgateway.api.mappers;

import java.util.Collection;
import java.util.stream.Collectors;

public interface RepresentationMapper<R, D> {
    default Collection<D> toModels(Collection<R> representations) {
        return representations.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    D toModel(R representation);

    default Collection<R> toRepresentations(Collection<D> domains) {
        return domains.stream()
                .map(this::toRepresentation)
                .collect(Collectors.toList());
    }

    R toRepresentation(D domain);
}
