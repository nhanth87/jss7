package org.restcomm.protocols.ss7.map.api.primitives;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
<code>
ISDN-AddressString ::= AddressString (SIZE (1..maxISDN-AddressLength))
-- This type is used to represent ISDN numbers.
</code>
 *
 *
 * @author sergey vetyutnev
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public interface ISDNAddressString extends AddressString {

}
