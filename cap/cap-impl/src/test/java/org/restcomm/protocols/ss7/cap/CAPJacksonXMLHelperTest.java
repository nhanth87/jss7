/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates
 */
package org.restcomm.protocols.ss7.cap;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.mobicents.protocols.asn.BitSetStrictLength;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Unit tests for CAPJacksonXMLHelper - tests XML serialization/deserialization
 * for CAP protocol classes
 * 
 * @author Matrix Agent
 */
public class CAPJacksonXMLHelperTest {

    private XmlMapper getXmlMapper() {
        return CAPJacksonXMLHelper.getXmlMapper();
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
        System.out.println("CAP BitSet XML:\n" + xml);
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
     * Test CAP uses FIELD visibility for serialization
     */
    @Test
    public void testFieldVisibility() throws Exception {
        BitSetStrictLength bits = new BitSetStrictLength(4);
        bits.set(0);
        bits.set(3);
        
        String xml = getXmlMapper().writeValueAsString(bits);
        assertNotNull("XML should not be null", xml);
        assertTrue("XML should contain strictLength", xml.contains("strictLength"));
        assertTrue("XML should contain bits", xml.contains("bits"));
    }
    
    /**
     * Test with various bit patterns
     */
    @Test
    public void testVariousBitPatterns() throws Exception {
        String[] patterns = {"00000000", "11111111", "10101010", "01010101", "10000001"};
        
        for (String pattern : patterns) {
            BitSetStrictLength original = new BitSetStrictLength(8);
            for (int i = 0; i < pattern.length(); i++) {
                if (pattern.charAt(i) == '1') {
                    original.set(i);
                }
            }
            
            String xml = getXmlMapper().writeValueAsString(original);
            BitSetStrictLength deserialized = getXmlMapper().readValue(xml, BitSetStrictLength.class);
            
            for (int i = 0; i < 8; i++) {
                assertEquals("Pattern " + pattern + " bit " + i, original.get(i), deserialized.get(i));
            }
        }
    }
    
    /**
     * Test with empty BitSet
     */
    @Test
    public void testEmptyBitSet() throws Exception {
        BitSetStrictLength bits = new BitSetStrictLength(0);
        
        String xml = getXmlMapper().writeValueAsString(bits);
        BitSetStrictLength deserialized = getXmlMapper().readValue(xml, BitSetStrictLength.class);
        
        assertEquals(0, deserialized.getStrictLength());
    }
    
    /**
     * Test using StringReader/StringWriter
     */
    @Test
    public void testReaderWriter() throws Exception {
        BitSetStrictLength bits = new BitSetStrictLength(8);
        bits.set(1);
        bits.set(6);
        
        // Serialize
        StringWriter writer = new StringWriter();
        getXmlMapper().writeValue(writer, bits);
        
        // Deserialize
        StringReader reader = new StringReader(writer.toString());
        BitSetStrictLength deserialized = getXmlMapper().readValue(reader, BitSetStrictLength.class);
        
        assertEquals(bits.getStrictLength(), deserialized.getStrictLength());
        assertEquals(bits.get(1), deserialized.get(1));
        assertEquals(bits.get(6), deserialized.get(6));
    }
    
    /**
     * Test with large BitSet
     */
    @Test
    public void testLargeBitSet() throws Exception {
        BitSetStrictLength bits = new BitSetStrictLength(128);
        bits.set(0);
        bits.set(64);
        bits.set(127);
        
        String xml = getXmlMapper().writeValueAsString(bits);
        BitSetStrictLength deserialized = getXmlMapper().readValue(xml, BitSetStrictLength.class);
        
        assertEquals(128, deserialized.getStrictLength());
        assertTrue("Bit 0 should be set", deserialized.get(0));
        assertTrue("Bit 64 should be set", deserialized.get(64));
        assertTrue("Bit 127 should be set", deserialized.get(127));
    }
}
