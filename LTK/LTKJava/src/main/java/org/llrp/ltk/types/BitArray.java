/*
 * Copyright 2007 ETH Zurich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.llrp.ltk.types;

import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Namespace;


/**
 * Array of bits - when encoded length is also encoded!
 *
 * @author gasserb
 */
public class BitArray extends LLRPType {
    protected static final Integer LENGTH = 1;
    private Bit[] bits;

    /**
         * create a new BitArray.
         * When encoded, BitArray also encodes its length.
         * @param bits to be decoded
         */
    public BitArray(Bit[] bits) {
        this.bits = bits.clone();
    }

    /**
         * create a new BitArray.
         * When encoded, BitArray also encodes its length.
         * @param list to be decoded
         */
    public BitArray(LLRPBitList list) {
        decodeBinary(list);
    }

    /**
         * create a new BitArray.
         * When encoded, BitArray also encodes its length.
         * Initially all bits set to 0
         * @param length of array
         */
    public BitArray(Integer length) {
        bits = new Bit[length];

        for (Integer i = 0; i < length; i++) {
            bits[i] = new Bit(false);
        }
    }

    /**
         * empty bit array.
         */
    public BitArray() {
        bits = new Bit[1];
    }

    /**
     * @param element to be decoded
     */
    public BitArray(Element element) {
        decodeXML(element);
    }

    /**
     * set bit at provided positionto 0.
     *
     * @param i to be cleared
     */
    public void clear(Integer i) {
        if ((i < 0) || (i > bits.length)) {
            return;
        } else {
            bits[i] = new Bit(false);
        }
    }

    /**
     * encodes length before encoding containing values.
     *
     * @return LLRPBitList
     */
    public LLRPBitList encodeBinary() {
        // add bits so that length of result is multiple of 8
        LLRPBitList padding = null;

        if ((bits.length % 8) > 0) {
            padding = new LLRPBitList(8 - (bits.length % 8));
        } else {
            padding = new LLRPBitList();
        }

        // length before bits
        LLRPBitList result = new UnsignedShort(bits.length).encodeBinary();

        for (Integer i = 0; i < bits.length; i++) {
            result.add(bits[i].toBoolean());
        }

        result.append(padding);

        return result;
    }

    /**
     * length of BaseType - not the array - for array length call size().
     *
     * @return
     */
    public static Integer length() {
        return Bit.length();
    }

    /**
     * Create BitArray from BitList. Must provide length with first 16 bits.
     *
     * @param list to be decoded
     */
    @Override
    public void decodeBinary(LLRPBitList list) {
        Integer length = new SignedInteger(list.subList(0, SignedShort.length())).toInteger();
        bits = new Bit[length];

        for (Integer i = 1; i <= length; i++) {
            bits[i - 1] = new Bit(list.get(15 + i));
        }
    }

    /**
     * get bit at specified position.
     *
     * @param i to get
     *
     * @return Bit
     */
    public Bit get(Integer i) {
        return bits[i];
    }

    /**
     * set bit at provided position to 1.
     *
     * @param i to be set to 1
     */
    public void set(Integer i) {
        if ((i < 0) || (i > bits.length)) {
            return;
        } else {
            bits[i] = new Bit(true);
        }
    }

    /**
     * number of elements in array.
     *
     * @return
     */
    public Integer size() {
        return bits.length;
    }

    @Override
    public Content encodeXML(String name, Namespace ns) {
        String s = "";

        for (Bit b : bits) {
            s += " ";
            s += b.toInteger().toString();
        }

        s = s.replaceFirst(" ", "");

        Element element = new Element(name, ns);
        element.setText(s);

        return element;
    }

    @Override
    public void decodeXML(Element element) {
        String text = element.getText();
        String[] bitStrings = text.split(" ");
        bits = new Bit[bitStrings.length];

        for (int i = 0; i < bits.length; i++) {
            bits[i] = new Bit(bitStrings[i]);
        }
    }

    public void add(Bit aBit) {
        Bit[] newBits = new Bit[bits.length + 1];
        System.arraycopy(bits, 0, newBits, 0, bits.length);
        newBits[bits.length] = aBit;
        bits = newBits;
    }
}