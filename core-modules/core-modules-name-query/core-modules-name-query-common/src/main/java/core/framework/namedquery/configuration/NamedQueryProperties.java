package core.framework.namedquery.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "core.named-query")
public class NamedQueryProperties {
    private Integer defaultMaxReturnSize = 256;
    private Integer batchSize = 128;
    private PagingParameter pagingParameter = new PagingParameter();

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getDefaultMaxReturnSize() {
        return defaultMaxReturnSize;
    }

    public void setDefaultMaxReturnSize(Integer defaultMaxReturnSize) {
        this.defaultMaxReturnSize = defaultMaxReturnSize;
    }

    public PagingParameter getPagingParameter() {
        return pagingParameter;
    }

    public void setPagingParameter(PagingParameter pagingParameter) {
        this.pagingParameter = pagingParameter;
    }
}
