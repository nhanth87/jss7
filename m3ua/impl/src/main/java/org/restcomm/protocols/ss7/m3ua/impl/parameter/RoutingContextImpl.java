package org.restcomm.protocols.ss7.m3ua.impl.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.restcomm.protocols.ss7.m3ua.parameter.Parameter;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;

/**
 *
 * @author amit bhayani
 *
 */
@JacksonXmlRootElement(localName = "routingContext")
public class RoutingContextImpl extends ParameterImpl implements RoutingContext {

    private static final String ARRAY_SIZE = "size";
    private static final String ROUTING_CONTEXT = "rc";

    @JacksonXmlElementWrapper(localName = "rcs")
    @JacksonXmlProperty(localName = "rc")
    private List<Long> rcs = null;
    private byte[] value;

    public RoutingContextImpl() {
        this.tag = Parameter.Routing_Context;
    }

    protected RoutingContextImpl(byte[] value) {
        this.tag = Parameter.Routing_Context;

        int count = 0;
        int arrSize = 0;
        rcs = new ArrayList<>();

        while (count < value.length) {
            long rc = 0;
            rc |= value[count++] & 0xFF;
            rc <<= 8;
            rc |= value[count++] & 0xFF;
            rc <<= 8;
            rc |= value[count++] & 0xFF;
            rc <<= 8;
            rc |= value[count++] & 0xFF;
            rcs.add(rc);
        }

        this.value = value;
    }

    protected RoutingContextImpl(long[] routingContexts) {
        this.tag = Parameter.Routing_Context;
        rcs = new ArrayList<>();
        for (long rc : routingContexts) {
            rcs.add(rc);
        }
        encode();
    }

    private void encode() {
        // create byte array taking into account data, point codes and indicators;
        this.value = new byte[(rcs.size() * 4)];
        int count = 0;
        int arrSize = 0;
        // encode routing context
        while (count < value.length) {
            long rc = rcs.get(arrSize++);
            value[count++] = (byte) (rc >>> 24);
            value[count++] = (byte) (rc >>> 16);
            value[count++] = (byte) (rc >>> 8);
            value[count++] = (byte) (rc);
        }
    }

    @JacksonXmlElementWrapper(localName = "rcs")
    @JacksonXmlProperty(localName = "rc")
    public long[] getRoutingContexts() {
        if (this.rcs == null && this.value != null) {
            // Reconstruct rcs from value if available
            int count = 0;
            rcs = new ArrayList<>();
            while (count < value.length) {
                long rc = 0;
                rc |= value[count++] & 0xFF;
                rc <<= 8;
                rc |= value[count++] & 0xFF;
                rc <<= 8;
                rc |= value[count++] & 0xFF;
                rc <<= 8;
                rc |= value[count++] & 0xFF;
                rcs.add(rc);
            }
        }
        if (this.rcs == null) {
            return new long[0];
        }
        long[] result = new long[this.rcs.size()];
        for (int i = 0; i < this.rcs.size(); i++) {
            result[i] = this.rcs.get(i);
        }
        return result;
    }

    @Override
    protected byte[] getValue() {
        if (value == null && rcs != null && !rcs.isEmpty()) {
            encode();
        }
        return value;
    }

    @Override
    public String toString() {
        return String.format("RoutingContext rc=%s", Arrays.toString(getRoutingContexts()));
    }

}
