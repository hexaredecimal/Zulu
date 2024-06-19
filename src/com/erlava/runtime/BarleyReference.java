package com.erlava.runtime;

import com.erlava.memory.Storage;
import com.erlava.utils.BarleyException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BarleyReference implements BarleyValue, Serializable {

    private Object ref;

    public BarleyReference(Object ref) {
        this.ref = ref; Storage.segment(this);
    }

    @Override
    public BigInteger asInteger() {
        throw new BarleyException("BadArithmetic", "Cannot cast REFERENCE to a NUMBER");
    }

    @Override
    public BigDecimal asFloat() {
        throw new BarleyException("BadArithmetic", "Cannot cast REFERENCE to a NUMBER");
    }

    @Override
    public Object raw() {
        return ref;
    }

    public Object getRef() {
        return ref;
    }

    @Override
    public String toString() {
        return "#Reference<" + hashCode() + ">";
    }
}
