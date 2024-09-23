package com.zulu.utils;


import java.io.Serializable;
import com.zulu.runtime.ZuluValue;

public interface Function extends Serializable {

	ZuluValue execute(ZuluValue... args);
}
