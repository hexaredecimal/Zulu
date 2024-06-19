package com.erlava.utils;

import com.erlava.optimizations.Optimization;
import com.erlava.runtime.BarleyValue;

import java.io.Serializable;

public interface AST extends Serializable {

    BarleyValue execute();

    void visit(Optimization optimization);

}
