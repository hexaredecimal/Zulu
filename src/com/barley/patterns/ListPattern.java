package com.barley.patterns;

import com.barley.utils.AST;

import java.util.LinkedList;

public class ListPattern extends Pattern {

    private LinkedList<AST> arr;

    public ListPattern(LinkedList<AST> arr) {
        this.arr = arr;
    }

    public LinkedList<AST> getArr() {
        return arr;
    }

    @Override
    public String toString() {
        return arr.toString();
    }
}
