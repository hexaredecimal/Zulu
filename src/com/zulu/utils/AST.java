package com.zulu.utils;

import com.zulu.optimizations.Optimization;

import java.io.Serializable;
import com.zulu.runtime.ZuluValue;

public interface AST extends Serializable {

	ZuluValue execute();

	void visit(Optimization optimization);

}
