package org.restcomm.protocols.ss7.map.service.lsm;

import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.lsm.ExtGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.GeographicalInformationImpl;

/**
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ExtGeographicalInformationImpl extends OctetStringBase implements ExtGeographicalInformation {

    private static double koefHAlat = Math.pow(2.0, 31) / 90;
    private static double koefHAlon = Math.pow(2.0, 31) / 180;
    private static double[] hiAccUncertaintyTable = initHiAccUncertaintyTable();
    private static double[] hiAccExtUncertaintyTable = initHiAccExtUncertaintyTable();

    private static double[] initHiAccUncertaintyTable() {
        double[] res = new double[255];

        double c = 0.3;
        double x = 0.02;
        for (int i = 1; i < 255; i++) {
            res[i] = c * (Math.pow(1 + x, i) - 1);
        }

        return res;
    }

    private static double[] initHiAccExtUncertaintyTable() {
        double[] res = new double[255];

        double c = 0.3;
        double x = 0.02594;
        for (int i = 1; i < 255; i++) {
            res[i] = c * (Math.pow(1 + x, i) - 1);
        }

        return res;
    }

    public ExtGeographicalInformationImpl() {
        super(1, 20, "ExtGeographicalInformation");
    }

    protected ExtGeographicalInformationImpl(int minLength, int maxLength, String _PrimitiveName) {
        super(minLength, maxLength, _PrimitiveName);
    }

    public ExtGeographicalInformationImpl(byte[] data) {
        super(1, 20, "ExtGeographicalInformation", data);
    }

    protected ExtGeographicalInformationImpl(int minLength, int maxLength, String _PrimitiveName, byte[] data) {
        super(minLength, maxLength, _PrimitiveName, data);
    }

    public ExtGeographicalInformationImpl(TypeOfShape typeOfShape, double latitude, double longitude, double uncertainty,
            double uncertaintySemiMajorAxis, double uncertaintySemiMinorAxis, double angleOfMajorAxis, int confidence,
            int altitude, double uncertaintyAltitude, int innerRadius, double uncertaintyRadius, double offsetAngle,
            double includedAngle) throws MAPException {
        super(1, 20, "ExtGeographicalInformation");

        initData(typeOfShape, latitude, longitude, uncertainty, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis,
                angleOfMajorAxis, confidence, altitude, uncertaintyAltitude, innerRadius, uncertaintyRadius, offsetAngle,
                includedAngle, 0, 0);
    }

    public ExtGeographicalInformationImpl(TypeOfShape typeOfShape, double latitude, double longitude, double uncertainty,
                double uncertaintySemiMajorAxis, double uncertaintySemiMinorAxis, double angleOfMajorAxis, int confidence,
                int altitude, double uncertaintyAltitude, int innerRadius, double uncertaintyRadius,
                double offsetAngle, double includedAngle, int horConfidence, int vertConfidence) throws MAPException {
        super(1, 20, "ExtGeographicalInformation");

        initData(typeOfShape, latitude, longitude, uncertainty, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis,
                angleOfMajorAxis, confidence, altitude, uncertaintyAltitude, innerRadius, uncertaintyRadius, offsetAngle,
                includedAngle, horConfidence, vertConfidence);
    }

    protected void initData(TypeOfShape typeOfShape, double latitude, double longitude, double uncertainty,
            double uncertaintySemiMajorAxis, double uncertaintySemiMinorAxis, double angleOfMajorAxis, int confidence,
            int altitude, double uncertaintyAltitude, int innerRadius, double uncertaintyRadius, double offsetAngle,
            double includedAngle, int horConfidence, int vertConfidence) throws MAPException {

        if (typeOfShape == null) {
            throw new MAPException("typeOfShape parameter is null");
        }

        boolean negativeSign = false;

        switch (typeOfShape) {
            case EllipsoidPointWithUncertaintyCircle:
                this.initData(8, typeOfShape, latitude, longitude);
                data[7] = (byte) GeographicalInformationImpl.encodeUncertainty(uncertainty);
                break;

            case EllipsoidPointWithUncertaintyEllipse:
                this.initData(11, typeOfShape, latitude, longitude);
                data[7] = (byte) GeographicalInformationImpl.encodeUncertainty(uncertaintySemiMajorAxis);
                data[8] = (byte) GeographicalInformationImpl.encodeUncertainty(uncertaintySemiMinorAxis);
                data[9] = (byte) (angleOfMajorAxis);
                data[10] = (byte) confidence;
                break;

            case EllipsoidPointWithAltitudeAndUncertaintyEllipsoid:
                this.initData(14, typeOfShape, latitude, longitude);
                if (altitude < 0) {
                    negativeSign = true;
                    altitude = -altitude;
                }
                if (altitude > 0x7FFF)
                    altitude = 0x7FFF;
                if (negativeSign)
                    altitude |= 0x8000;
                data[7] = (byte) ((altitude & 0xFF00) >> 8);
                data[8] = (byte) (altitude & 0xFF);
                data[9] = (byte) GeographicalInformationImpl.encodeUncertainty(uncertaintySemiMajorAxis);
                data[10] = (byte) GeographicalInformationImpl.encodeUncertainty(uncertaintySemiMinorAxis);
                data[11] = (byte) (angleOfMajorAxis / 2);
                data[12] = (byte) GeographicalInformationImpl.encodeUncertaintyAltitude(uncertaintyAltitude); // FIXME
                data[13] = (byte) confidence;
                break;

            case EllipsoidArc:
                this.initData(13, typeOfShape, latitude, longitude);
                if (innerRadius > 0x7FFF)
                    innerRadius = 0x7FFF;
                data[7] = (byte) ((innerRadius & 0xFF00) >> 8);
                data[8] = (byte) (innerRadius & 0xFF);
                data[9] = (byte) GeographicalInformationImpl.encodeUncertainty(uncertaintyRadius);
                data[10] = (byte) (offsetAngle);
                data[11] = (byte) (includedAngle);
                data[12] = (byte) confidence;
                break;

            case EllipsoidPoint:
                this.initData(7, typeOfShape, latitude, longitude);
                break;

            case HighAccuracyEllipsoidPointWithUncertaintyEllipse:
                this.initHiAccData(13, typeOfShape, latitude, longitude);
                data[9] = (byte) encodeHiAccUncertainty(uncertaintySemiMajorAxis);
                data[10] = (byte) encodeHiAccUncertainty(uncertaintySemiMinorAxis);
                data[11] = (byte) (angleOfMajorAxis);
                data[12] = (byte) confidence;
                break;

            case HighAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid:
                this.initHiAccData(18, typeOfShape, latitude, longitude);
                data[9] = (byte) ((altitude & 0x3F0000) >> 16);
                data[10] = (byte) ((altitude & 0xFF00) >> 8);
                data[11] = (byte) (altitude & 0xFF);
                data[12] = (byte) encodeHiAccUncertainty(uncertaintySemiMajorAxis);
                data[13] = (byte) encodeHiAccUncertainty(uncertaintySemiMinorAxis);
                data[14] = (byte) (angleOfMajorAxis / 2);
                data[15] = (byte) (horConfidence & 0x7F);
                data[16] = (byte) GeographicalInformationImpl.encodeUncertaintyAltitude(uncertaintyAltitude);
                data[17] = (byte) (vertConfidence & 0x7F);
                break;

            case HighAccuracyEllipsoidPointWithScalableUncertaintyEllipse:
                this.initHiAccData(13, typeOfShape, latitude, longitude);
                data[9] = (byte) encodeHiAccUncertainty(uncertaintySemiMajorAxis);
                data[10] = (byte) encodeHiAccUncertainty(uncertaintySemiMinorAxis);
                data[11] = (byte) (angleOfMajorAxis);
                if (confidence < 0) {
                    negativeSign = true;
                    confidence = -confidence;
                }
                if (confidence > 0x7FFF)
                    confidence = 0x7FFF;
                if (negativeSign)
                    confidence |= 0x8000;
                data[12] = (byte) (confidence & 0xFF);
                break;

            case HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid:
                this.initHiAccData(18, typeOfShape, latitude, longitude);
                data[9] = (byte) ((altitude & 0x3F0000) >> 16);
                data[10] = (byte) ((altitude & 0xFF00) >> 8);
                data[11] = (byte) (altitude & 0xFF);
                data[12] = (byte) encodeHiAccUncertainty(uncertaintySemiMajorAxis);
                data[13] = (byte) encodeHiAccUncertainty(uncertaintySemiMinorAxis);
                data[14] = (byte) (angleOfMajorAxis / 2);
                if (horConfidence < 0) {
                    negativeSign = true;
                    horConfidence = - horConfidence;
                }
                if (horConfidence > 0x7FFF)
                    horConfidence = 0x7FFF;
                if (negativeSign)
                    horConfidence |= 0x8000;
                data[15] = (byte) (horConfidence & 0xFF);
                data[16] = (byte) GeographicalInformationImpl.encodeUncertaintyAltitude(uncertaintyAltitude);
                if (vertConfidence < 0) {
                    negativeSign = true;
                    vertConfidence = -vertConfidence;
                }
                if (vertConfidence > 0x7FFF)
                    vertConfidence = 0x7FFF;
                if (negativeSign)
                    vertConfidence |= 0x8000;
                data[17] = (byte) (vertConfidence & 0xFF);
                break;

            default:
                throw new MAPException("typeOfShape parameter has bad value");
        }
    }

    private void initData(int len, TypeOfShape typeOfShape, double latitude, double longitude) {
        this.data = new byte[len];
        this.data[0] = (byte) (typeOfShape.getCode() << 4);
        GeographicalInformationImpl.encodeLatitude(data, 1, latitude);
        GeographicalInformationImpl.encodeLongitude(data, 4, longitude);
    }

    private void initHiAccData(int len, TypeOfShape typeOfShape, double latitude, double longitude) {
        this.data = new byte[len];
        this.data[0] = (byte) (typeOfShape.getCode() << 4);
        encodeHiAccLatitude(data, 1, latitude);
        encodeHiAccLongitude(data, 5, longitude);
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public TypeOfShape getTypeOfShape() {
        if (this.data == null || this.data.length < 1)
            return null;

        return TypeOfShape.getInstance((this.data[0] & 0xFF) >> 4);
    }

    @Override
    public double getLatitude() {
        if (this.data == null || this.data.length < 7)
            return 0;

        return GeographicalInformationImpl.decodeLatitude(this.data, 1);
    }

    @Override
    public double getLongitude() {
        if (this.data == null || this.data.length < 7)
            return 0;

        return GeographicalInformationImpl.decodeLongitude(this.data, 4);
    }

    @Override
    public double getHiAccLatitude() {
        if (this.data == null || this.data.length < 9)
            return 0;

        return decodeHiAccLatitude(this.data, 1);
    }

    @Override
    public double getHiAccLongitude() {
        if (this.data == null || this.data.length < 9)
            return 0;

        return decodeHiAccLongitude(this.data, 5);
    }

    @Override
    public double getUncertainty() {
        if (this.getTypeOfShape() != TypeOfShape.EllipsoidPointWithUncertaintyCircle || this.data == null
                || this.data.length != 8)
            return 0;

        return GeographicalInformationImpl.decodeUncertainty(this.data[7]);
    }

    @Override
    public double getUncertaintySemiMajorAxis() {
        TypeOfShape typeOfShape = this.getTypeOfShape();
        if (typeOfShape != null) {
            switch (typeOfShape) {
                case EllipsoidPointWithUncertaintyEllipse:
                    if (this.data == null || this.data.length != 11)
                        return 0;
                    return GeographicalInformationImpl.decodeUncertainty(this.data[7]);

                case EllipsoidPointWithAltitudeAndUncertaintyEllipsoid:
                    if (this.data == null || this.data.length != 14)
                        return 0;
                    return GeographicalInformationImpl.decodeUncertainty(this.data[9]);

                case HighAccuracyEllipsoidPointWithUncertaintyEllipse:
                case HighAccuracyEllipsoidPointWithScalableUncertaintyEllipse:
                    if (this.data == null || this.data.length != 13)
                        return 0;
                    return decodeHiAccUncertainty(this.data[9]);

                case HighAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid:
                case HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid:
                    if (this.data == null || this.data.length != 18)
                        return 0;
                    return decodeHiAccUncertainty(this.data[12]);
            }
        }
        return 0;
    }

    @Override
    public double getUncertaintySemiMinorAxis() {
        TypeOfShape typeOfShape = this.getTypeOfShape();
        if (typeOfShape != null) {
            switch (typeOfShape) {
                case EllipsoidPointWithUncertaintyEllipse:
                    if (this.data == null || this.data.length != 11)
                        return 0;
                    return GeographicalInformationImpl.decodeUncertainty(this.data[8]);

                case EllipsoidPointWithAltitudeAndUncertaintyEllipsoid:
                    if (this.data == null || this.data.length != 14)
                        return 0;
                    return GeographicalInformationImpl.decodeUncertainty(this.data[10]);

                case HighAccuracyEllipsoidPointWithUncertaintyEllipse:
                case HighAccuracyEllipsoidPointWithScalableUncertaintyEllipse:
                    if (this.data == null || this.data.length != 13)
                        return 0;
                    return decodeHiAccUncertainty(this.data[10]);

                case HighAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid:
                case HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid:
                    if (this.data == null || this.data.length != 18)
                        return 0;
                    return decodeHiAccUncertainty(this.data[13]);
            }
        }
        return 0;
    }

    @Override
    public double getAngleOfMajorAxis() {
        TypeOfShape typeOfShape = this.getTypeOfShape();
        if (typeOfShape != null) {
            switch (typeOfShape) {
                case EllipsoidPointWithUncertaintyEllipse:
                    if (this.data == null || this.data.length != 11)
                        return 0;
                    return (data[9] & 0xFF);

                case EllipsoidPointWithAltitudeAndUncertaintyEllipsoid:
                    if (this.data == null || this.data.length != 14)
                        return 0;
                    return (data[11] & 0xFF) * 2;

                case HighAccuracyEllipsoidPointWithUncertaintyEllipse:
                    if (this.data == null || this.data.length != 13)
                        return 0;
                    return (data[11] & 0xFF);

                case HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid:
                    if (this.data == null || this.data.length != 18)
                        return 0;
                    return (data[14] & 0xFF) * 2;
            }
        }

        return 0;
    }

    @Override
    public int getConfidence() {
        TypeOfShape typeOfShape = this.getTypeOfShape();
        if (typeOfShape != null) {
            switch (typeOfShape) {
                case EllipsoidPointWithUncertaintyEllipse:
                    if (this.data == null || this.data.length != 11)
                        return 0;
                    return this.data[10];

                case EllipsoidPointWithAltitudeAndUncertaintyEllipsoid:
                    if (this.data == null || this.data.length != 14)
                        return 0;
                    return this.data[13];

                case EllipsoidArc:
                case HighAccuracyEllipsoidPointWithUncertaintyEllipse:
                    if (this.data == null || this.data.length != 13)
                        return 0;
                    return this.data[12];

                case HighAccuracyEllipsoidPointWithScalableUncertaintyEllipse:
                    if (this.data == null || this.data.length != 13)
                        return 0;
                    int octet12 = data[12] & 0xFF;
                    int sign = 1;
                    if ((octet12 & 0x80) != 0) {
                        sign = -1;
                        octet12 = octet12 & 0x7F;
                    }
                    return octet12 * sign;
            }
        }

        return 0;
    }

    @Override
    public int getHorizontalConfidence() {
        TypeOfShape typeOfShape = this.getTypeOfShape();
        if (typeOfShape != null) {
            switch (typeOfShape) {
                case HighAccuracyEllipsoidPointWithScalableUncertaintyEllipse:
                    if (this.data == null || this.data.length != 18)
                        return 0;
                    return data[15] & 0x7F;
                case HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid:
                    if (this.data == null || this.data.length != 18)
                        return 0;
                    int octet15 = data[15] & 0xFF;
                    int sign = 1;
                    if ((octet15 & 0x80) != 0) {
                        sign = -1;
                        octet15 = octet15 & 0x7F;
                    }
                    return octet15 * sign;
            }
        }
        return 0;
    }

    @Override
    public int getVerticalConfidence() {
        TypeOfShape typeOfShape = this.getTypeOfShape();
        if (typeOfShape != null) {
            switch (typeOfShape) {
                case HighAccuracyEllipsoidPointWithScalableUncertaintyEllipse:
                    if (this.data == null || this.data.length != 18)
                        return 0;
                    return data[17] & 0x7F;
                case HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid:
                    if (this.data == null || this.data.length != 18)
                        return 0;
                    int octet17 = data[17] & 0xFF;
                    int sign = 1;
                    if ((octet17 & 0x80) != 0) {
                        sign = -1;
                        octet17 = octet17 & 0x7F;
                    }
                    return octet17 * sign;
            }
        }
        return 0;
    }

    @Override
    public int getAltitude() {
        if (this.getTypeOfShape() != TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid || this.data == null
                || this.data.length != 14)
            return 0;

        int i1 = ((data[7] & 0xFF) << 8) + (data[8] & 0xFF);
        int sign = 1;
        if ((i1 & 0x8000) != 0) {
            sign = -1;
            i1 = i1 & 0x7FFF;
        }
        return i1 * sign;
    }

    @Override
    public int getHiAccAltitude() {
        if ((this.getTypeOfShape() != TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid ||
                this.getTypeOfShape() !=TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid)
                || this.data == null || this.data.length != 18)
            return 0;

        int i1 = ((data[9] & 0xFF) << 16) + ((data[10] & 0xFF) << 8) + (data[11] & 0xFF);
        int sign = 1;
        if ((i1 & 0x800000) != 0) {
            sign = -1;
            i1 = i1 & 0x3FFFFF;
        }
        return i1 * sign;
    }

    @Override
    public double getUncertaintyAltitude() {
        if (this.getTypeOfShape() != TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid || this.data == null
                || this.data.length != 14)
            return 0;

        return GeographicalInformationImpl.decodeUncertaintyAltitude(this.data[12]);
    }

    @Override
    public double getHiAccUncertaintyAltitude() {
        if ((this.getTypeOfShape() != TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid &&
                this.getTypeOfShape() !=TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid)
                || this.data == null || this.data.length != 18)
            return 0;

        return GeographicalInformationImpl.decodeUncertaintyAltitude(this.data[16]);
    }

    @Override
    public int getInnerRadius() {
        if (this.getTypeOfShape() != TypeOfShape.EllipsoidArc || this.data == null || this.data.length != 13)
            return 0;

        int i1 = ((data[7] & 0xFF) << 8) + (data[8] & 0xFF);
        return i1;
    }

    @Override
    public double getUncertaintyRadius() {
        if (this.getTypeOfShape() != TypeOfShape.EllipsoidArc || this.data == null || this.data.length != 13)
            return 0;

        return GeographicalInformationImpl.decodeUncertainty(this.data[9]);
    }

    @Override
    public double getOffsetAngle() {
        if (this.getTypeOfShape() != TypeOfShape.EllipsoidArc || this.data == null || this.data.length != 13)
            return 0;

        return (data[10] & 0xFF);
    }

    @Override
    public double getIncludedAngle() {
        if (this.getTypeOfShape() != TypeOfShape.EllipsoidArc || this.data == null || this.data.length != 13)
            return 0;

        return (data[11] & 0xFF);
    }

    public static void encodeHiAccLatitude(byte[] data, int begin, double val) {
        long res = (long) (koefHAlat * val);

        if (res > 0x7FFFFFFF)
            res = 0x7FFFFFFF;

        if (val < 0)
            res |= 0x80000000L;

        data[begin] = (byte) ((res & 0xFF000000L) >> 24);
        data[begin + 1] = (byte) ((res & 0xFF0000) >> 16);
        data[begin + 2] = (byte) ((res & 0xFF00) >> 8);
        data[begin + 3] = (byte) (res & 0xFF);
    }

    public static void encodeHiAccLongitude(byte[] data, int begin, double val) {
        long res = (long) (koefHAlon * val);

        if (res > 0x7FFFFFFF)
            res = 0x7FFFFFFF;

        if (val < 0)
            res |= 0x80000000L;

        data[begin] = (byte) ((res & 0xFF000000L) >> 24);
        data[begin + 1] = (byte) ((res & 0xFF0000) >> 16);
        data[begin + 2] = (byte) ((res & 0xFF00) >> 8);
        data[begin + 3] = (byte) (res & 0xFF);
    }

    public static double decodeHiAccLatitude(byte[] data, int begin) {
        int i1 = ((data[begin] & 0xFF) << 24) | ((data[begin + 1] & 0xFF) << 16) | ((data[begin + 2] & 0xFF) << 8)
                | (data[begin + 3] & 0xFF);

        if ((i1 & 0x80000000) != 0) {
            i1 = i1 | 0xFF000000;
        }

        return i1 / koefHAlat;
    }

    public static double decodeHiAccLongitude(byte[] data, int begin) {
        int i1 = ((data[begin] & 0xFF) << 24) | ((data[begin + 1] & 0xFF) << 16) | ((data[begin + 2] & 0xFF) << 8)
                | (data[begin + 3] & 0xFF);

        if ((i1 & 0x80000000) != 0) {
            i1 = i1 | 0xFF000000;
        }

        return i1 / koefHAlon;
    }

    public static double decodeHiAccUncertainty(int data) {
        if (data < 0 || data > 255)
            data = 0;
        return hiAccUncertaintyTable[data];
    }

    public static int encodeHiAccUncertainty(double val) {
        for (int i = 0; i < 255; i++) {
            if (val < hiAccUncertaintyTable[i + 1]) {
                return i;
            }
        }
        return 255;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExtGeographicalInformation");
        sb.append(" [");

        sb.append("TypeOfShape=");
        sb.append(this.getTypeOfShape());

        if (this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithUncertaintyEllipse
                || this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid
                || this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithScalableUncertaintyEllipse
                || this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid) {

            sb.append(", HighAccuracyLatitude=");
            sb.append(this.getHiAccLatitude());

            sb.append(", HighAccuracyLongitude=");
            sb.append(this.getHiAccLongitude());
        } else {
            sb.append(", Latitude=");
            sb.append(this.getLatitude());

            sb.append(", Longitude=");
            sb.append(this.getLongitude());
        }


        if (this.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyCircle) {
            sb.append(", Uncertainty=");
            sb.append(this.getUncertainty());
        }

        if (this.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyEllipse
                || this.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {
            sb.append(", UncertaintySemiMajorAxis=");
            sb.append(this.getUncertaintySemiMajorAxis());

            sb.append(", UncertaintySemiMinorAxis=");
            sb.append(this.getUncertaintySemiMinorAxis());

            sb.append(", AngleOfMajorAxis=");
            sb.append(this.getAngleOfMajorAxis());

        } else if (this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithUncertaintyEllipse
                || this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid
                || this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithScalableUncertaintyEllipse
                || this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid) {

            sb.append(", HighAccuracyUncertaintySemiMajorAxis=");
            sb.append(this.getUncertaintySemiMajorAxis());

            sb.append(", HighAccuracyUncertaintySemiMinorAxis=");
            sb.append(this.getUncertaintySemiMinorAxis());

            sb.append(", AngleOfMajorAxis=");
            sb.append(this.getAngleOfMajorAxis());
        }

        if (this.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {

            sb.append(", Altitude=");
            sb.append(this.getAltitude());

            sb.append(", UncertaintyAltitude=");
            sb.append(this.getUncertaintyAltitude());

        } else if (this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid
                || this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid) {

            sb.append(", HighAccuracyAltitude=");
            sb.append(this.getHiAccAltitude());

            sb.append(", HighAccuracyUncertaintyAltitude=");
            sb.append(this.getHiAccUncertaintyAltitude());
        }

        if (this.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
            sb.append(", InnerRadius=");
            sb.append(this.getInnerRadius());

            sb.append(", UncertaintyRadius=");
            sb.append(this.getUncertaintyRadius());

            sb.append(", OffsetAngle=");
            sb.append(this.getOffsetAngle());

            sb.append(", IncludedAngle=");
            sb.append(this.getIncludedAngle());
        }

        if (this.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyEllipse
                || this.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid
                || this.getTypeOfShape() == TypeOfShape.EllipsoidArc
                || this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithUncertaintyEllipse
                || this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithScalableUncertaintyEllipse) {
            sb.append(", Confidence=");
            sb.append(this.getConfidence());
        } else if (this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid
                || this.getTypeOfShape() == TypeOfShape.HighAccuracyEllipsoidPointWithAltitudeAndScalableUncertaintyEllipsoid) {
            sb.append(", HorizontalConfidence=");
            sb.append(this.getHorizontalConfidence());

            sb.append(", VerticalConfidence=");
            sb.append(this.getVerticalConfidence());
        }

        sb.append("]");

        return sb.toString();
    }
}
