package core.framework.jpa.eclipselink.mongodb.configuration;

/**
 * @author ebin
 */
public enum WriteConcern {
    FSYNC_SAFE,
    JOURNAL_SAFE,
    MAJORITY,
    NONE,
    NORMAL,
    REPLICAS_SAFE,
    SAFE
}
