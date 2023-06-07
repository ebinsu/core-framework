package core.framework.jpa.eclipselink.mongodb.support;

import core.framework.jpa.eclipselink.common.ExtendConversionManager;
import jakarta.resource.cci.MappedRecord;
import org.eclipse.persistence.eis.EISAccessor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.nosql.adapters.mongo.MongoPlatform;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * @author ebin
 */
public class ExtendMongoPlatform extends MongoPlatform {

    @Override
    public void setValueInRecord(String key, Object value, MappedRecord record, EISAccessor accessor) {
        Object recordValue = value;
        if ((value instanceof BigDecimal b)) {
            recordValue = getConversionManager().convertObject(
                b.setScale(2, RoundingMode.HALF_UP).doubleValue(),
                ClassConstants.DOUBLE
            );
        } else if (value instanceof BigInteger b) {
            recordValue = getConversionManager().convertObject(
                BigDecimal.valueOf(b.doubleValue()).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                ClassConstants.DOUBLE
            );
        } else if (value instanceof Float f) {
            recordValue = getConversionManager().convertObject(
                BigDecimal.valueOf(f).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                ClassConstants.DOUBLE
            );
        } else if (value instanceof Double d) {
            recordValue = getConversionManager().convertObject(
                BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                ClassConstants.DOUBLE
            );
        }
        record.put(key, recordValue);
    }

    @Override
    public ConversionManager getConversionManager() {
        if (conversionManager == null) {
            //Clone the default to allow customers to easily override the conversion manager
            conversionManager = (ConversionManager) ExtendConversionManager.getDefaultManager().clone();
        }
        return conversionManager;
    }
}
