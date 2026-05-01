/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates
 */
package org.restcomm.protocols.ss7.map;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.mobicents.protocols.asn.BitSetStrictLength;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Unit tests for MAPJacksonXMLHelper - tests XML serialization/deserialization
 * for MAP protocol classes
 * 
 * @author Matrix Agent
 */
public class MAPJacksonXMLHelperTest {

    private XmlMapper getXmlMapper() {
        return MAPJacksonXMLHelper.getXmlMapper();
    }
    
    /**
     * Test that XmlMapper is properly initialized
     */
    @Test
    public void testXmlMapperInitialization() {
        XmlMapper mapper = getXmlMapper();
        assertNotNull("XmlMapper should not be null", mapper);
    }
    
    /**
     * Test BitSetStrictLength serialization
     */
    @Test
    public void testBitSetStrictLengthSerialization() throws Exception {
        BitSetStrictLength bits = new BitSetStrictLength(8);
        bits.set(0); // 1
        bits.set(2); // 1
        bits.set(5); // 1
        // Binary: 00100101
        
        StringWriter writer = new StringWriter();
        getXmlMapper().writeValue(writer, bits);
        String xml = writer.toString();
        
        assertNotNull("XML should not be null", xml);
        System.out.println("BitSet XML:\n" + xml);
    }
    
    /**
     * Test BitSetStrictLength deserialization
     */
    @Test
    public void testBitSetStrictLengthDeserialization() throws Exception {
        String xml = "{\"strictLength\":8,\"bits\":\"00100101\"}";
        
        BitSetStrictLength bits = getXmlMapper().readValue(xml, BitSetStrictLength.class);
        
        assertNotNull("Deserialized BitSet should not be null", bits);
        assertEquals(8, bits.getStrictLength());
        assertTrue("Bit 0 should be set", bits.get(0));
        assertTrue("Bit 2 should be set", bits.get(2));
        assertTrue("Bit 5 should be set", bits.get(5));
        assertFalse("Bit 1 should not be set", bits.get(1));
        assertFalse("Bit 3 should not be set", bits.get(3));
    }
    
    /**
     * Test BitSetStrictLength round-trip
     */
    @Test
    public void testBitSetStrictLengthRoundTrip() throws Exception {
        BitSetStrictLength original = new BitSetStrictLength(16);
        original.set(1);
        original.set(3);
        original.set(7);
        original.set(15);
        
        // Serialize
        StringWriter writer = new StringWriter();
        getXmlMapper().writeValue(writer, original);
        String xml = writer.toString();
        
        // Deserialize
        BitSetStrictLength deserialized = getXmlMapper().readValue(xml, BitSetStrictLength.class);
        
        assertEquals(original.getStrictLength(), deserialized.getStrictLength());
        for (int i = 0; i < original.getStrictLength(); i++) {
            assertEquals("Bit " + i + " should match", original.get(i), deserialized.get(i));
        }
    }
    
    /**
     * Test with all bits set
     */
    @Test
    public void testBitSetAllBitsSet() throws Exception {
        BitSetStrictLength bits = new BitSetStrictLength(4);
        bits.set(0);
        bits.set(1);
        bits.set(2);
        bits.set(3);
        
        StringWriter writer = new StringWriter();
        getXmlMapper().writeValue(writer, bits);
        String xml = writer.toString();
        
        BitSetStrictLength deserialized = getXmlMapper().readValue(xml, BitSetStrictLength.class);
        
        for (int i = 0; i < 4; i++) {
            assertTrue("Bit " + i + " should be set", deserialized.get(i));
        }
    }
    
    /**
     * Test with no bits set
     */
    @Test
    public void testBitSetNoBitsSet() throws Exception {
        BitSetStrictLength bits = new BitSetStrictLength(4);
        // No bits set
        
        StringWriter writer = new StringWriter();
        getXmlMapper().writeValue(writer, bits);
        String xml = writer.toString();
        
        BitSetStrictLength deserialized = getXmlMapper().readValue(xml, BitSetStrictLength.class);
        
        for (int i = 0; i < 4; i++) {
            assertFalse("Bit " + i + " should not be set", deserialized.get(i));
        }
    }
    
    /**
     * Test XML with different strict lengths
     */
    @Test
    public void testBitSetDifferentLengths() throws Exception {
        int[] lengths = {1, 4, 8, 16, 32, 64};
        
        for (int len : lengths) {
            BitSetStrictLength bits = new BitSetStrictLength(len);
            if (len > 0) {
                bits.set(0);
                bits.set(len / 2);
                bits.set(len - 1);
            }
            
            StringWriter writer = new StringWriter();
            getXmlMapper().writeValue(writer, bits);
            String xml = writer.toString();
            
            BitSetStrictLength deserialized = getXmlMapper().readValue(xml, BitSetStrictLength.class);
            
            assertEquals("Length mismatch for " + len, len, deserialized.getStrictLength());
            assertTrue("First bit should be set", deserialized.get(0));
        }
    }
    
    /**
     * Test XML declaration handling
     */
    @Test
    public void testXmlDeclarationHandling() throws Exception {
        BitSetStrictLength bits = new BitSetStrictLength(4);
        bits.set(1);
        bits.set(2);
        
        String xml = getXmlMapper().writeValueAsString(bits);
        assertNotNull("XML should not be null", xml);
        
        // Should be able to read it back
        BitSetStrictLength deserialized = getXmlMapper().readValue(xml, BitSetStrictLength.class);
        assertEquals(4, deserialized.getStrictLength());
    }
    
    /**
     * Test using StringReader/StringWriter
     */
    @Test
    public void testReaderWriter() throws Exception {
        BitSetStrictLength bits = new BitSetStrictLength(8);
        bits.set(0);
        bits.set(7);
        
        // Serialize
        StringWriter writer = new StringWriter();
        getXmlMapper().writeValue(writer, bits);
        
        // Deserialize
        StringReader reader = new StringReader(writer.toString());
        BitSetStrictLength deserialized = getXmlMapper().readValue(reader, BitSetStrictLength.class);
        
        assertEquals(bits.getStrictLength(), deserialized.getStrictLength());
        assertEquals(bits.get(0), deserialized.get(0));
        assertEquals(bits.get(7), deserialized.get(7));
    }
}
