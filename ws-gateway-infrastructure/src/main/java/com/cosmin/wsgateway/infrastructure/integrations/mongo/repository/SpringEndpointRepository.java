package com.cosmin.wsgateway.infrastructure.integrations.mongo.repository;

import com.cosmin.wsgateway.infrastructure.integrations.mongo.document.EndpointDocument;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringEndpointRepository extends ReactiveCrudRepository<EndpointDocument, String> {
}
