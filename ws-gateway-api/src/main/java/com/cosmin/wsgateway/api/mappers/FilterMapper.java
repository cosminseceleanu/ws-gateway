package com.cosmin.wsgateway.api.mappers;

import com.cosmin.wsgateway.api.representation.FilterRepresentation;
import com.cosmin.wsgateway.domain.Filter;
import com.cosmin.wsgateway.domain.filters.BlacklistHosts;
import com.cosmin.wsgateway.domain.filters.BlacklistIps;
import com.cosmin.wsgateway.domain.filters.WhitelistHosts;
import com.cosmin.wsgateway.domain.filters.WhitelistIps;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FilterMapper implements RepresentationMapper<FilterRepresentation, Set<Filter<?>>> {
    @Override
    public Set<Filter<?>> toModel(FilterRepresentation representation) {
        if (representation == null) {
            return new LinkedHashSet<>();
        }
        return Set.of(
                new WhitelistHosts(representation.getWhitelistHosts()),
                new BlacklistHosts(representation.getBlacklistHosts()),
                new WhitelistIps(representation.getBlacklistIps()),
                new BlacklistIps(representation.getWhitelistIps())
        );
    }

    @Override
    public FilterRepresentation toRepresentation(Set<Filter<?>> domain) {
        FilterRepresentation representation = new FilterRepresentation();
        Map<String, Object> filterByNameAndValue = domain.stream()
                .collect(Collectors.toMap(Filter::name, Filter::value));
        representation.setBlacklistIps((Set<String>) filterByNameAndValue.get("blacklistIps"));
        representation.setWhitelistIps((Set<String>) filterByNameAndValue.get("whitelistIps"));
        representation.setWhitelistHosts((Set<String>) filterByNameAndValue.get("whitelistHosts"));
        representation.setBlacklistHosts((Set<String>) filterByNameAndValue.get("blacklistHosts"));

        return representation;
    }
}
