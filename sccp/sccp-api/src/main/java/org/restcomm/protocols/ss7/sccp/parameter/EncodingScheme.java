package org.restcomm.protocols.ss7.sccp.parameter;

import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.restcomm.protocols.ss7.sccp.message.ParseException;

/**
 * Interface declaration for ES. Implementation should provide proper encoding/decoding.
 *
 * @author baranowb
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public interface EncodingScheme {

    EncodingSchemeType getType();

    /**
     * @return scheme code which will be used as part of GT, it must be 4 bites at most.
     */
    byte getSchemeCode();

    void encode(String digits, OutputStream outputStream) throws ParseException;

    String decode(InputStream inputStream) throws ParseException;

}
