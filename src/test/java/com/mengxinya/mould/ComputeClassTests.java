package com.mengxinya.mould;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class ComputeClassTests {

    @Test
    public void testSimilarDegree1() {
        String a1 = "15ml/瓶";
        String b1 = "30ml/瓶";
        Double rs = Cosine.getSimilarity(a1, b1);
        System.out.println(rs);
    }

    @Test
    public void testSimilarDegree2() {
        String a1 = "7CM×10CM";
        String b1 = "2贴/袋";
        Double rs = Cosine.getSimilarity(a1, b1);
        System.out.println(rs);
    }

    @Test
    public void testSimilarDegree3() {
        String a1 = "30mm×30mm";
        String b1 = "70mm×100mm";
        Double rs = Cosine.getSimilarity(a1, b1);
        System.out.println(rs);
    }


}
