package core.framework.jpa.eclipselink.common;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.JPAConversionManager;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

/**
 * @author ebin
 */
public class ExtendConversionManager extends JPAConversionManager {
    public static final Class<ZonedDateTime> ZONED_DATE_TIME = ZonedDateTime.class;

    protected static ExtendConversionManager defaultManager;

    public static ConversionManager getDefaultManager() {
        if (defaultManager == null) {
            ExtendConversionManager.setDefaultManager(new ExtendConversionManager());
            defaultManager.setShouldUseClassLoaderFromCurrentThread(true);
        }
        return defaultManager;
    }

    public static void setDefaultManager(ExtendConversionManager theManager) {
        defaultManager = theManager;
    }

    @Override
    public <T> T convertObject(Object sourceObject, Class<T> javaClass) throws ConversionException {
        if (sourceObject == null) {//Let the parent handle default null values
            return super.convertObject(null, javaClass);
        } else if (javaClass == null || javaClass == CoreClassConstants.OBJECT || sourceObject.getClass() == javaClass) {
            return (T) sourceObject;
        }
        try {
            if (javaClass == ExtendConversionManager.ZONED_DATE_TIME) {
                return (T) convertObjectToZonedDateTime(sourceObject);
            }
        } catch (ConversionException ce) {
            throw ce;
        } catch (Exception e) {
            throw ConversionException.couldNotBeConverted(sourceObject, javaClass, e);
        }
        try {
            return super.convertObject(sourceObject, javaClass);
        } catch (ConversionException ex) {
            if (sourceObject.getClass() == CoreClassConstants.STRING) {
                return super.convertObject(((String) sourceObject).trim(), javaClass);
            }
            throw ex;
        }
    }

    protected ZonedDateTime convertObjectToZonedDateTime(Object sourceObject) throws ConversionException {
        ZonedDateTime zonedDateTime = null;

        if (sourceObject instanceof ZonedDateTime) {
            return (ZonedDateTime) sourceObject;
        }

        if (sourceObject instanceof String) {
            zonedDateTime = java.time.ZonedDateTime.parse(((String) sourceObject).replace(' ', 'T'), Helper.getDefaultDateTimeFormatter());
        } else if (sourceObject instanceof java.sql.Timestamp) {
            zonedDateTime = ((java.sql.Timestamp) sourceObject).toLocalDateTime().atZone(ZoneId.systemDefault());
        } else if (sourceObject instanceof java.util.Date date) {
            // handles sql.Time, sql.Date
            zonedDateTime = date.toInstant().atZone(ZoneId.systemDefault());
        } else if (sourceObject instanceof Calendar cal) {
            zonedDateTime = cal.toInstant().atZone(ZoneId.systemDefault());
        } else if (sourceObject instanceof Long) {
            zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond((Long) sourceObject), ZoneId.systemDefault());
        } else {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.TIME_LDATETIME);
        }

        return zonedDateTime;
    }
}
