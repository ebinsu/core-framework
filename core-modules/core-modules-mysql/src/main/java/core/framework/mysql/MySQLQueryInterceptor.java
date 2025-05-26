package core.framework.mysql;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Query;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerSession;
import core.framework.kernel.log.marker.ErrorCodeMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author ebin
 */
public class MySQLQueryInterceptor implements QueryInterceptor {
    // mysql will create new interceptor instance for every connection, so to minimize initialization cost
    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLQueryInterceptor.class);

    @Override
    public QueryInterceptor init(MysqlConnection connection, Properties properties, Log log) {
        return this;
    }

    @Override
    public <T extends Resultset> T preProcess(Supplier<String> sql, Query interceptedQuery) {
        return null;
    }

    @Override
    public boolean executeTopLevelOnly() {
        return true;
    }

    @Override
    public void destroy() {
    }

    @Override
    public <T extends Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {
        if (!Mysql.SUPPRESS_SLOW_SQL.get()) {
            boolean noIndexUsed = serverSession.noIndexUsed();
            boolean badIndexUsed = serverSession.noGoodIndexUsed();
            if (noIndexUsed || badIndexUsed) {
                String message = noIndexUsed ? "no index used" : "bad index used";
                String sqlValue = sql.get();
                LOGGER.warn(new ErrorCodeMarker("NO_INDEX_USED"), "{}, sql={}", message, sqlValue);
            }
        }
        return null;
    }
}
