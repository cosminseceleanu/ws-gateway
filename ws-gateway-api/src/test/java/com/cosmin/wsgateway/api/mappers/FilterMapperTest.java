package com.cosmin.wsgateway.api.mappers;

import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.api.representation.FilterRepresentation;
import com.cosmin.wsgateway.domain.Filter;
import com.cosmin.wsgateway.domain.filters.BlacklistHosts;
import com.cosmin.wsgateway.domain.filters.BlacklistIps;
import com.cosmin.wsgateway.domain.filters.WhitelistHosts;
import com.cosmin.wsgateway.domain.filters.WhitelistIps;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FilterMapperTest {
    private FilterMapper subject = new FilterMapper();

    @Test
    public void testToModel_representationIsNull_shouldReturnEmptyCollection() {
         var result = subject.toModel(null);

         assertTrue(result.isEmpty());
    }

    @Test
    public void testToModel_representationHasFilters_shouldReturnSetWithAllFilters() {
        var result = subject.toModel(FilterRepresentation.builder()
                .blacklistHosts(Set.of("blackHost1", "blackHost2"))
                .whitelistIps(Set.of("whitelistIp1", "whitelistIp2"))
                .build());

        assertTrue(result.contains(new BlacklistHosts(Set.of("blackHost1", "blackHost2"))));
        assertTrue(result.contains(new BlacklistIps(null)));
        assertTrue(result.contains(new WhitelistHosts(null)));
        assertTrue(result.contains(new WhitelistIps(Set.of("whitelistIp1", "whitelistIp2"))));
    }

    @Test
    public void testToRepresentation_modelFilters_shouldReturnARepresentation() {
        Set<Filter<?>> filters = Set.of(
                new BlacklistHosts(Collections.singleton("h1")),
                new WhitelistHosts(Collections.singleton("h2")),
                new WhitelistIps(Collections.singleton("ip1")),
                new BlacklistIps(Collections.singleton("ip2"))
        );
        var result = subject.toRepresentation(filters);

        assertEquals(Collections.singleton("h1"), result.getBlacklistHosts());
        assertEquals(Collections.singleton("h2"), result.getWhitelistHosts());
        assertEquals(Collections.singleton("ip1"), result.getWhitelistIps());
        assertEquals(Collections.singleton("ip2"), result.getBlacklistIps());
    }
}