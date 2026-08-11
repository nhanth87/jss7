package org.restcomm.protocols.ss7.map;

import static org.testng.Assert.assertEquals;

import java.util.function.Supplier;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.EMLPPPriority;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.service.callhandling.InterrogationType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberStateChoice;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.LMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerTest;
import org.restcomm.protocols.ss7.map.primitives.SubscriberIdentityImpl;
import org.restcomm.protocols.ss7.map.service.callhandling.SendRoutingInformationRequestImpl;
import org.restcomm.protocols.ss7.map.service.callhandling.SendRoutingInformationResponseImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ProvideSubscriberLocationRequestImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ProvideSubscriberLocationResponseImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SendRoutingInfoForLCSRequestImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SendRoutingInfoForLCSResponseImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SubscriberLocationReportRequestImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SubscriberLocationReportResponseImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.AnyTimeInterrogationRequestImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.AnyTimeInterrogationResponseImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.ProvideSubscriberInfoRequestImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.ProvideSubscriberInfoResponseImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.RequestedInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.SubscriberInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.SubscriberStateImpl;
import org.testng.annotations.Test;

/**
 * Encode each GMLC/call-handling message (minimal + fullest existing vectors),
 * then decode with BerCursor on and off. Assert getter equality and full consume.
 * Vectors are inlined so this class does not compile the rest of the map-impl
 * test tree (pre-existing API drift).
 */
public class BerCursorGmlcDifferentialTest {

    private static final String PROPERTY = "jss7.asn.berCursorEnabled";

    @Test
    public void testEncodeDecodeBothModes() throws Exception {
        ISDNAddressStringImpl msisdn = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN,
                "553499775190");
        ISDNAddressStringImpl scf = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN,
                "553496629943");
        RequestedInfoImpl requestedInfo = new RequestedInfoImpl(true, true, null, false, null, false, false, false, false);
        AnyTimeInterrogationRequestImpl atiMin = new AnyTimeInterrogationRequestImpl(new SubscriberIdentityImpl(msisdn),
                requestedInfo, scf, null);
        AnyTimeInterrogationRequestImpl atiFull = new AnyTimeInterrogationRequestImpl(new SubscriberIdentityImpl(msisdn),
                requestedInfo, scf, MAPExtensionContainerTest.GetTestExtensionContainer());
        roundTrip("AnyTimeInterrogationRequest-min", atiMin, AnyTimeInterrogationRequestImpl::new);
        roundTrip("AnyTimeInterrogationRequest-full", atiFull, AnyTimeInterrogationRequestImpl::new);

        SubscriberStateImpl state = new SubscriberStateImpl(SubscriberStateChoice.assumedIdle, null);
        SubscriberInfoImpl si = new SubscriberInfoImpl(null, state, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null);
        AnyTimeInterrogationResponseImpl atiRespMin = new AnyTimeInterrogationResponseImpl(si, null);
        AnyTimeInterrogationResponseImpl atiRespFull = new AnyTimeInterrogationResponseImpl(si,
                MAPExtensionContainerTest.GetTestExtensionContainer());
        roundTrip("AnyTimeInterrogationResponse-min", atiRespMin, AnyTimeInterrogationResponseImpl::new);
        roundTrip("AnyTimeInterrogationResponse-full", atiRespFull, AnyTimeInterrogationResponseImpl::new);

        IMSIImpl imsi = new IMSIImpl("111222333444");
        RequestedInfoImpl locOnly = new RequestedInfoImpl(true, false, null, false, null, false, false, false, false);
        ProvideSubscriberInfoRequestImpl psiMin = new ProvideSubscriberInfoRequestImpl(imsi, null, locOnly, null, null);
        ProvideSubscriberInfoRequestImpl psiFull = new ProvideSubscriberInfoRequestImpl(imsi,
                new LMSIImpl(new byte[] { 11, 22, 33, 44 }), locOnly,
                MAPExtensionContainerTest.GetTestExtensionContainer(), EMLPPPriority.priorityLevel4);
        roundTrip("ProvideSubscriberInfoRequest-min", psiMin, ProvideSubscriberInfoRequestImpl::new);
        roundTrip("ProvideSubscriberInfoRequest-full", psiFull, ProvideSubscriberInfoRequestImpl::new);

        SubscriberInfoImpl psiSi = new SubscriberInfoImpl(null,
                new SubscriberStateImpl(SubscriberStateChoice.camelBusy, null), null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null);
        ProvideSubscriberInfoResponseImpl psiRespMin = new ProvideSubscriberInfoResponseImpl(psiSi, null);
        ProvideSubscriberInfoResponseImpl psiRespFull = new ProvideSubscriberInfoResponseImpl(psiSi,
                MAPExtensionContainerTest.GetTestExtensionContainer());
        roundTrip("ProvideSubscriberInfoResponse-min", psiRespMin, ProvideSubscriberInfoResponseImpl::new);
        roundTrip("ProvideSubscriberInfoResponse-full", psiRespFull, ProvideSubscriberInfoResponseImpl::new);

        ISDNAddressStringImpl sriMsisdn = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN,
                "29113123311");
        ISDNAddressStringImpl gmsc = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN,
                "49883700292");
        SendRoutingInformationRequestImpl sriMin = new SendRoutingInformationRequestImpl(3, sriMsisdn, gmsc,
                InterrogationType.forwarding, null);
        SendRoutingInformationRequestImpl sriFull = new SendRoutingInformationRequestImpl(3, sriMsisdn, gmsc,
                InterrogationType.forwarding, MAPExtensionContainerTest.GetTestExtensionContainer());
        roundTrip("SendRoutingInformationRequest-min", sriMin, () -> new SendRoutingInformationRequestImpl(3));
        roundTrip("SendRoutingInformationRequest-full", sriFull, () -> new SendRoutingInformationRequestImpl(3));
        decodeVector("SendRoutingInformationResponse-1", sriResp1(), () -> new SendRoutingInformationResponseImpl(3));
        decodeVector("SendRoutingInformationResponse-2", sriResp2(), () -> new SendRoutingInformationResponseImpl(3));

        decodeVector("SendRoutingInfoForLCSRequest-min", sriLcsReqMin(), SendRoutingInfoForLCSRequestImpl::new);
        decodeVector("SendRoutingInfoForLCSRequest-full", sriLcsReqFull(), SendRoutingInfoForLCSRequestImpl::new);
        decodeVector("SendRoutingInfoForLCSResponse-min", sriLcsRespMin(), SendRoutingInfoForLCSResponseImpl::new);
        decodeVector("SendRoutingInfoForLCSResponse-full", sriLcsRespFull(), SendRoutingInfoForLCSResponseImpl::new);

        decodeVector("ProvideSubscriberLocationRequest-min", pslReqMin(), ProvideSubscriberLocationRequestImpl::new);
        decodeVector("ProvideSubscriberLocationRequest-full", pslReqFull(), ProvideSubscriberLocationRequestImpl::new);
        decodeVector("ProvideSubscriberLocationResponse-min", pslRespMin(), ProvideSubscriberLocationResponseImpl::new);
        decodeVector("ProvideSubscriberLocationResponse-full", pslRespFull(), ProvideSubscriberLocationResponseImpl::new);

        decodeVector("SubscriberLocationReportRequest", slrReq(), SubscriberLocationReportRequestImpl::new);
        decodeVector("SubscriberLocationReportResponse", slrResp(), SubscriberLocationReportResponseImpl::new);
    }

    private static void roundTrip(String name, MAPAsnPrimitive encoded, Supplier<MAPAsnPrimitive> factory)
            throws Exception {
        AsnOutputStream aos = new AsnOutputStream();
        encoded.encodeAll(aos);
        decodeVector(name, aos.toByteArray(), factory);
    }

    private static void decodeVector(String name, byte[] wire, Supplier<MAPAsnPrimitive> factory) throws Exception {
        try {
            MAPAsnPrimitive ber = decode(wire, true, factory);
            MAPAsnPrimitive classic = decode(wire, false, factory);
            try {
                AsnOutputStream berOut = new AsnOutputStream();
                AsnOutputStream classicOut = new AsnOutputStream();
                ber.encodeAll(berOut);
                classic.encodeAll(classicOut);
                assertEquals(berOut.toByteArray(), classicOut.toByteArray(), name + " re-encode mismatch");
            } catch (org.restcomm.protocols.ss7.map.api.MAPException encodeOnly) {
                assertEquals(safeDesc(ber), safeDesc(classic), name + " getter mismatch");
            }
        } catch (Exception e) {
            throw new Exception(name + ": " + e.getMessage(), e);
        }
    }

    private static String safeDesc(MAPAsnPrimitive msg) {
        try {
            return msg.toString();
        } catch (RuntimeException e) {
            return msg.getClass().getName();
        }
    }

    private static MAPAsnPrimitive decode(byte[] wire, boolean berEnabled, Supplier<MAPAsnPrimitive> factory)
            throws Exception {
        String previous = System.getProperty(PROPERTY);
        System.setProperty(PROPERTY, Boolean.toString(berEnabled));
        try {
            AsnInputStream ais = new AsnInputStream(wire);
            ais.readTag();
            int before = ais.available();
            MAPAsnPrimitive msg = factory.get();
            msg.decodeAll(ais);
            assertEquals(ais.available(), 0, "unconsumed bytes ber=" + berEnabled + " before=" + before);
            return msg;
        } finally {
            if (previous == null)
                System.clearProperty(PROPERTY);
            else
                System.setProperty(PROPERTY, previous);
        }
    }

    // Existing public vectors from *Test classes (copied so those files need not compile).

    private static byte[] sriResp1() {
        return new byte[] { (byte) 163, 19, (byte) 137, 8, 16, 33, 2, 2, 16, -119, 34, -9, 4, 7, -111, -105, 114, 99, 80, 24,
                -7 };
    }

    private static byte[] sriResp2() {
        return new byte[] { (byte) 163, 24, (byte) 137, 8, 16, 33, 2, 2, 16, -119, 34, -9, 48, 12, (byte) 133, 7, -111, -105,
                114, 99, 80, 24, -7, (byte) 134, 1, 36 };
    }

    private static byte[] sriLcsReqMin() {
        return new byte[] { 0x30, 0x13, (byte) 0x80, 0x05, (byte) 0x91, 0x55, 0x16, 0x09, 0x70, (byte) 0xa1, 0x0a, (byte) 0x80,
                0x08, 0x27, (byte) 0x94, (byte) 0x99, 0x09, 0x00, 0x00, 0x00, (byte) 0xf7 };
    }

    private static byte[] sriLcsReqFull() {
        return new byte[] { 48, 60, -128, 5, -111, 85, 22, 9, 112, -95, 10, -128, 8, 39, -108, -103, 9, 0, 0, 0, -9, -94, 39,
                -96, 32, 48, 10, 6, 3, 42, 3, 4, 11, 12, 13, 14, 15, 48, 5, 6, 3, 42, 3, 6, 48, 11, 6, 3, 42, 3, 5, 21, 22, 23,
                24, 25, 26, -95, 3, 31, 32, 33 };
    }

    private static byte[] sriLcsRespMin() {
        return new byte[] { 0x30, 0x14, (byte) 0xa0, 0x09, (byte) 0x81, 0x07, (byte) 0x91, 0x55, 0x16, 0x28, (byte) 0x81, 0x00,
                0x70, (byte) 0xa1, 0x07, 0x04, 0x05, (byte) 0x91, 0x55, 0x16, 0x09, 0x00 };
    }

    private static byte[] sriLcsRespFull() {
        return new byte[] { 48, 89, -96, 9, -127, 7, -111, 85, 22, 40, -127, 0, 112, -95, 7, 4, 5, -111, 85, 22, 9, 0, -94, 39,
                -96, 32, 48, 10, 6, 3, 42, 3, 4, 11, 12, 13, 14, 15, 48, 5, 6, 3, 42, 3, 6, 48, 11, 6, 3, 42, 3, 5, 21, 22, 23,
                24, 25, 26, -95, 3, 31, 32, 33, -125, 5, 11, 12, 13, 14, 15, -124, 5, 21, 22, 23, 24, 25, -123, 5, 31, 32, 33,
                34, 35, -122, 5, 41, 42, 43, 44, 45 };
    }

    private static byte[] pslReqMin() {
        return new byte[] { 0x30, 0x41, 0x30, 0x03, (byte) 0x80, 0x01, 0x00, 0x04, 0x05, (byte) 0x91, 0x55, 0x16, 0x09, 0x70,
                (byte) 0xa0, 0x1b, (byte) 0x80, 0x01, 0x02, (byte) 0x83, 0x01, 0x00, (byte) 0xa4, 0x13, (byte) 0x80, 0x01,
                0x0f, (byte) 0x82, 0x0e, 0x6e, 0x72, (byte) 0xfb, 0x1c, (byte) 0x86, (byte) 0xc3, 0x65, 0x6e, 0x72,
                (byte) 0xfb, 0x1c, (byte) 0x86, (byte) 0xc3, 0x65, (byte) 0x82, 0x08, 0x27, (byte) 0x94, (byte) 0x99, 0x09,
                0x00, 0x00, 0x00, (byte) 0xf7, (byte) 0x86, 0x01, 0x01, (byte) 0xa7, 0x05, (byte) 0xa3, 0x03, 0x0a, 0x01, 0x00,
                (byte) 0x89, 0x02, 0x01, (byte) 0xfe };
    }

    private static byte[] pslReqFull() {
        return new byte[] { 48, -127, -93, 48, 3, -128, 1, 0, 4, 5, -111, 85, 22, 9, 112, -96, 27, -128, 1, 2, -125, 1, 0, -92,
                19, -128, 1, 15, -126, 14, 110, 114, -5, 28, -122, -61, 101, 110, 114, -5, 28, -122, -61, 101, -127, 0, -126,
                8, 39, -108, -103, 9, 0, 0, 0, -9, -125, 6, -111, 103, 69, 35, 1, -16, -124, 4, 31, 32, 33, 34, -123, 8, 33,
                67, 101, -121, 9, 33, 67, 101, -122, 1, 1, -89, 5, -93, 3, 10, 1, 0, -119, 2, 1, -2, -118, 1, 5, -117, 1, 6,
                -84, 12, -128, 1, 15, -127, 7, 120, 124, 62, -97, -41, -21, 27, -83, 6, -128, 1, 1, -127, 1, 0, -82, 13, -96,
                11, -96, 9, 48, 7, -128, 1, 0, -127, 2, 82, -16, -113, 5, 41, 42, 43, 44, 45, -112, 0, -79, 7, 2, 2, 0, -56, 2,
                1, 100, -78, 9, -95, 7, 48, 5, -128, 3, 51, 52, 53 };
    }

    private static byte[] pslRespMin() {
        return new byte[] { 48, 6, 4, 1, 99, -128, 1, 15 };
    }

    private static byte[] pslRespFull() {
        return new byte[] { 48, 59, 4, 1, 99, -128, 1, 15, -126, 1, 19, -125, 0, -124, 2, 11, 12, -123, 3, 15, 16, 17, -90, 7,
                -127, 5, 33, -15, 16, 8, -82, -121, 0, -120, 1, 0, -119, 4, 21, 22, 23, 24, -118, 0, -117, 2, 25, 26, -116, 1,
                29, -83, 8, -128, 6, -111, 68, 100, 102, -120, -8 };
    }

    private static byte[] slrReq() {
        return new byte[] { 48, -127, -92, 10, 1, 0, 48, 3, -128, 1, 2, 48, 8, 4, 6, -111, 68, 68, 84, 85, 85, -128, 6, -111,
                102, 102, 118, 119, 119, -127, 5, 33, 67, 21, 50, 84, -126, 8, 33, 67, 101, -121, 9, 33, 67, 101, -125, 6,
                -111, -120, -120, -104, -103, -103, -124, 6, -111, -120, -120, 8, 0, 0, -123, 1, 11, -122, 1, 5, -89, 4, -95,
                2, -128, 0, -120, 1, 12, -87, 14, 3, 2, 4, -128, -95, 8, 4, 6, -111, 68, 68, 84, 85, 85, -118, 1, 6, -117, 2,
                13, 14, -116, 3, 15, 16, 17, -83, 7, -127, 5, 34, -16, 33, 16, -31, -114, 5, 21, 22, 23, 24, 25, -113, 1, 7,
                -111, 0, -110, 0, -109, 1, 0, -108, 4, 26, 27, 28, 29, -107, 1, 9, -74, 6, 2, 1, 10, 2, 1, 11, -105, 0, -104,
                2, 31, 32, -103, 1, 33, -70, 8, -128, 6, -111, -111, -126, 115, 100, -11 };
    }

    private static byte[] slrResp() {
        return new byte[] { 48, 51, 48, 39, -96, 32, 48, 10, 6, 3, 42, 3, 4, 11, 12, 13, 14, 15, 48, 5, 6, 3, 42, 3, 6, 48, 11,
                6, 3, 42, 3, 5, 21, 22, 23, 24, 25, 26, -95, 3, 31, 32, 33, -128, 3, -111, 34, 34, -127, 3, -111, 17, 17 };
    }
}
