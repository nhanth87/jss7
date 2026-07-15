package org.mobicents.protocols.asn;

/**
 * Generic interface cho CHOICE decoder.
 *
 * <p>Pattern:
 * <ol>
 *   <li>Caller đọc tag (cursor.readTag())</li>
 *   <li>Gọi decode(cursor) → T</li>
 *   <li>Decoder dispatch theo tag, decode content, trả về T</li>
 * </ol>
 *
 * @param <T> result type (thường là enum + value holder)
 */
@FunctionalInterface
public interface ChoiceDecoder<T> {
    T decode(BerCursor cursor) throws AsnException;
}
