
package org.restcomm.protocols.ss7.map.api.datacoding;

import java.io.Serializable;

import org.restcomm.protocols.ss7.map.api.smstpdu.CharacterSet;
import org.restcomm.protocols.ss7.map.api.smstpdu.DataCodingSchemaMessageClass;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public interface CBSDataCodingScheme extends Serializable {

    int getCode();

    CBSDataCodingGroup getDataCodingGroup();

    CBSNationalLanguage getNationalLanguageShiftTable();

    CharacterSet getCharacterSet();

    DataCodingSchemaMessageClass getMessageClass();

    boolean getIsCompressed();

}
