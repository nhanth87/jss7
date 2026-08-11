package org.restcomm.protocols.ss7.map.ber;

import java.util.concurrent.TimeUnit;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.SubscriberIdentityImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.AnyTimeInterrogationRequestImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.RequestedInfoImpl;

/**
 * Isolated JMH for GMLC BER vs classic decode. Run from map-ber-jmh:
 * {@code mvn -pl map/map-ber-jmh package && java -jar map/map-ber-jmh/target/map-ber-benchmarks.jar}
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
@State(Scope.Thread)
public class MapBerJmh {

    private byte[] wire;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        ISDNAddressStringImpl msisdn = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN,
                "553499775190");
        ISDNAddressStringImpl scf = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN,
                "553496629943");
        RequestedInfoImpl requestedInfo = new RequestedInfoImpl(true, true, null, false, null, false, false, false, false);
        AnyTimeInterrogationRequestImpl ati = new AnyTimeInterrogationRequestImpl(new SubscriberIdentityImpl(msisdn),
                requestedInfo, scf, null);
        AsnOutputStream aos = new AsnOutputStream();
        ati.encodeAll(aos);
        this.wire = aos.toByteArray();
    }

    @Benchmark
    public AnyTimeInterrogationRequestImpl decodeBer() throws Exception {
        System.setProperty("jss7.asn.berCursorEnabled", "true");
        return decodeOnce();
    }

    @Benchmark
    public AnyTimeInterrogationRequestImpl decodeClassic() throws Exception {
        System.setProperty("jss7.asn.berCursorEnabled", "false");
        return decodeOnce();
    }

    private AnyTimeInterrogationRequestImpl decodeOnce() throws Exception {
        AsnInputStream ais = new AsnInputStream(wire);
        ais.readTag();
        AnyTimeInterrogationRequestImpl msg = new AnyTimeInterrogationRequestImpl();
        msg.decodeAll(ais);
        return msg;
    }
}
