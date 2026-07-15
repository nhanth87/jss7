package org.restcomm.protocols.ss7.map.primitives;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.restcomm.protocols.ss7.map.api.primitives.Time;

/**
 *
 * @author Lasith Waruna Perera
 *
 */
public class TimeImpl extends OctetStringBase implements Time {

    private static final long msbZero = 2085978496000L;

    private static final long msbOne = -2208988800000L;

    public TimeImpl() {
        super(4, 4, "Time");
    }

    public TimeImpl(byte[] data) {
        super(4, 4, "Time", data);
    }

    public TimeImpl(int year, int month, int day, int hour, int minute, int second) {
        super(4, 4, "Time");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        switch (month) {
            case 1:
                month = Calendar.JANUARY;
                break;
            case 2:
                month = Calendar.FEBRUARY;
                break;
            case 3:
                month = Calendar.MARCH;
                break;
            case 4:
                month = Calendar.APRIL;
                break;
            case 5:
                month = Calendar.MAY;
                break;
            case 6:
                month = Calendar.JUNE;
                break;
            case 7:
                month = Calendar.JULY;
                break;
            case 8:
                month = Calendar.AUGUST;
                break;
            case 9:
                month = Calendar.SEPTEMBER;
                break;
            case 10:
                month = Calendar.OCTOBER;
                break;
            case 11:
                month = Calendar.NOVEMBER;
                break;
            case 12:
                month = Calendar.DECEMBER;
                break;
        }
        cal.set(year, month, day, hour, minute, second);
        long ntpTime = getNtpTime(cal.getTimeInMillis());
        this.data = new byte[4];
        System.arraycopy(longToBytes(ntpTime), 4, this.data, 0, 4);
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public int getYear() {
        long time = bytesToLong(this.data);
        time = getTime(time);
        Date d = new Date(time);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(d);
        return cal.get(Calendar.YEAR);
    }

    @Override
    public int getMonth() {
        long time = bytesToLong(this.data);
        time = getTime(time);
        Date d = new Date(time);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(d);
        return cal.get(Calendar.MONTH) + 1;
    }

    @Override
    public int getDay() {
        long time = bytesToLong(this.data);
        time = getTime(time);
        Date d = new Date(time);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(d);
        return cal.get(Calendar.DATE);
    }

    @Override
    public int getHour() {
        long time = bytesToLong(this.data);
        time = getTime(time);
        Date d = new Date(time);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(d);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    @Override
    public int getMinute() {
        long time = bytesToLong(this.data);
        time = getTime(time);
        Date d = new Date(time);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(d);
        return cal.get(Calendar.MINUTE);
    }

    @Override
    public int getSecond() {
        long time = bytesToLong(this.data);
        time = getTime(time);
        Date d = new Date(time);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(d);
        return cal.get(Calendar.SECOND);
    }

    private long getNtpTime(long time) {
        boolean isMSBSet = time < msbZero;
        long timeWithMills;
        if (isMSBSet) {
            timeWithMills = time - msbOne;
        } else {
            timeWithMills = time - msbZero;
        }
        long seconds = timeWithMills / 1000;
        if (isMSBSet) {
            seconds |= 0x80000000L;
        }
        return seconds;
    }

    public long getTime(long ntpTime) {
        long msb = ntpTime & 0x80000000L;
        if (msb == 0) {
            return msbZero + (ntpTime * 1000);
        } else {
            return msbOne + (ntpTime * 1000);
        }
    }

    public byte[] longToBytes(long x) {
        return new byte[] {
                (byte) (x >> 56), (byte) (x >> 48), (byte) (x >> 40), (byte) (x >> 32),
                (byte) (x >> 24), (byte) (x >> 16), (byte) (x >> 8), (byte) x
        };
    }

    public long bytesToLong(byte[] bytes) {
        // big-endian unsigned int from the first 4 bytes (matches the previous
        // ByteBuffer(8){0,0,0,0,b0,b1,b2,b3}.getLong() behaviour)
        return ((long) (bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");
        if (data != null) {
            sb.append("year=");
            sb.append(this.getYear());
            sb.append(", month=");
            sb.append(this.getMonth());
            sb.append(", day=");
            sb.append(this.getDay());
            sb.append(", hour=");
            sb.append(this.getHour());
            sb.append(", minute=");
            sb.append(this.getMinute());
            sb.append(", second=");
            sb.append(this.getSecond());
        }
        sb.append("]");

        return sb.toString();
    }
}
