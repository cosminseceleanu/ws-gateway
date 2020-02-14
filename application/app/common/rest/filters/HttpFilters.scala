package common.rest.filters

import javax.inject.Inject
import play.api.http.{DefaultHttpFilters, EnabledFilters}
import play.filters.gzip.GzipFilter

class HttpFilters @Inject()(
    defaultFilters: EnabledFilters,
    gzip: GzipFilter,
    log: LoggingFilter
) extends DefaultHttpFilters(defaultFilters.filters :+ gzip :+ log: _*)
