package com.worldpay.util;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;
import org.joda.time.DateTime;

import java.util.Date;


/**
 * Utility class for standard methods
 */
public final class WorldpayUtil {

    private WorldpayUtil() {
    }

    /**
     * Serialize the worldpayAdditionalInfo object so that it can be passed into the commands without adjusting the
     * method structure all the way through
     * @param additionalInfo info to be serialized
     * @return String representation of the object encoded using Base 64 encoding
     */
    public static String serializeWorldpayAdditionalInfo(final WorldpayAdditionalInfoData additionalInfo) {
        final byte[] additionalInfoBytes = SerializationUtils.serialize(additionalInfo);
        return new Base64().encodeToString(additionalInfoBytes);
    }

    /**
     * Deserialize a serialized worldpayAdditionalInfo object
     * @param serializedObject Object to be deserialized
     * @return worldpayAdditionalInfo object
     */
    public static WorldpayAdditionalInfoData deserializeWorldpayAdditionalInfo(final String serializedObject) {
        // Convert to bytes
        final byte[] additionalInfoBytes = new Base64().decode(serializedObject);
        // Deserialize to an object
        return (WorldpayAdditionalInfoData) SerializationUtils.deserialize(additionalInfoBytes);
    }

    public static Date createDateInPast(final int days) {
        return new DateTime().minusDays(days).toDate();
    }
}
