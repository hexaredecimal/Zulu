package com.barley;

import com.barley.runtime.Modules;
import com.barley.utils.Handler;

public class Main {

    public static void main(String[] args) {
        Modules.init();
        //Handler.tests();
        //Handler.calculator();
        //Handler.entry("program.barley");
        Handler.entry("program.barley");
    }
}
