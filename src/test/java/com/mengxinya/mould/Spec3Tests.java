package com.mengxinya.mould;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Spec3Tests {

    private SourceDetail superMould(String source) {
        source = source.trim();
        if (source.contains("规格：") || source.contains("型号：")) {
            return SourceDetail.notMatch(source);
        }
        Mould sep = Mould.maybe(
                Mould.theMould("、"),
                Mould.maybe(Mould.theMould("，"), Mould.theMould(",")),
                Mould.maybe(Mould.theMould("；"), Mould.theMould(";"))
        );
        Mould sepSub = Mould.maybe(
                Mould.theMould("×"), Mould.theMould("*"),
                Mould.theMould("/"), Mould.theMould("-")
        );
        Mould itemSub = Mould.convert(
                Mould.repeat(Mould.not(Mould.maybe(
                        Mould.theMould("("), Mould.theMould("（"),
                        Mould.theMould(":"), Mould.theMould("："),
                        Mould.theMould("规格"), Mould.theMould("型号"),
                        Mould.theMould("注"), Mould.theMould("。"),
                        sep, sepSub
                ))),
                ClayConverter.joining("")
        );

        Mould item = Mould.convert(Mould.cons(itemSub, Mould.repeat(Mould.composeJoining(sepSub, itemSub))), ClayConverter.joining(""));
        Mould mould = Mould.join(sep, item);

        SourceDetail detail = mould.fill(source);

        // 排除只有一项的情况
        if (detail.isFinish() && Clay.getValues(detail.getClay(), Object.class).size() == 1) {
            return SourceDetail.notMatch(source);
        }

        // 第一项和最后一项差距不能太大
        if (detail.isFinish()) {
            List<String> clayList = Clay.getValues(detail.getClay(), String.class);
            String first = clayList.get(0);
            String end = clayList.get(clayList.size() - 1);
            if (ComputeClass.SimilarDegree(first, end) < 0.2) {
                return SourceDetail.notMatch(source);
            }
        }

        // 如果尾部还存在字符串的话，不能出现规律模式。
        Mould gap = Mould.repeat(Mould.not(item));
        Mould lawMould = Mould.compose(gap, item);
        if (detail.isFinish() && detail.getLeftSource().length() > 0) {
            SourceDetail lawDetail = lawMould.fill(detail.getLeftSource());
            if (lawDetail.isFinish()) {
                return SourceDetail.notMatch(source);
            }
        }

        return detail;
    }


    @Test
    public void testInput1() {
        String sourceStr = "8㎝×13㎝、9㎝×12㎝、10㎝×13㎝、12㎝×18㎝、13㎝×16㎝、8㎝×12㎝、12㎝×12㎝、4㎝×4㎝、6㎝×12㎝、13㎝×13㎝、7㎝×10㎝、7㎝×7㎝、6cm×8cm、9cm×9cm（特殊规格根据客户合同要求生产）";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        System.out.println(detail.getClay().toJsonString());
    }

    @Test
    public void testInput2() {
        String sourceStr = "30mm×30mm、40mm×110mm、50mm×120mm、70mm×100mm；贴片通常为长方形，其它特殊尺寸、规格、形状见订货合同。";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        System.out.println(detail.getClay().toJsonString());
    }

    @Test
    public void testInput3() {
        String sourceStr = "2.5g/支、？3.0g/支、？4.0g/支、？5.0g/支、？6.0g/支、？10g/支、？15g/支、？20g/支、？25g/支、？30g/支。";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        System.out.println(detail.getClay().toJsonString());
    }

    @Test
    public void testInput4() {
        String sourceStr = "3g/支";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertFalse(detail.isFinish());
    }

    @Test
    public void testInput5() {
        String sourceStr = "见附页。";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertFalse(detail.isFinish());
    }

    @Test
    public void testInput6() {
        String sourceStr = "JZJ-CXQ-Ⅰ10、JZJ-CXQ-Ⅰ20、JZJ-CXQ-Ⅰ200；JZJ-CXQ-Ⅱ800";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
    }

    @Test
    public void testInput7() {
        String sourceStr = "连身式、分身式（号型：160、165、170、175、180、185）";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertFalse(detail.isFinish());
    }

    @Test
    public void testInput8() {
        String sourceStr = "普通型和变色型的主要规格有：40mm×80mm、40mm×110mm、50mm×80mm、50mm×120mm、100mm×140mm、20mm×20mm、25mm×25mm、30mm×30mm、40mm×40mm、40mm×45mm、40mm×50mm、40mm×60mm、40mm×70mm、40mm×100mm、50mm×100mm、50mm×110mm、50mm×130mm、55mm×120mm、70mm×100mm、70mm×120mm、80mm×120mm、80mm×130mm、80mm×140mm、85mm×210mm、100mm×100mm、100mm×110mm、100mm×120mm、100mm×130mm、120mm×300mm、φ20mm、φ25mm。尺寸允许误差±5mm。面膜型的主要规格有：20ml、25ml、30ml。允许误差±5ml。";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertFalse(detail.isFinish());
    }

    @Test
    public void testInput9() {
        String sourceStr = "MZ-01（300cm×190cm）、MZ-02（280cm×175cm）、 MZ-03（250cm×165cm）、 MZ-04（240cm×135cm）、 MZ-05（220cm×68cm），尺寸允差±1cm。";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertFalse(detail.isFinish());
    }

    @Test
    public void testInput10() {
        String sourceStr = "HJ-004上肢吊带、HJ-006锁骨矫正带、HJ-009肋骨固定带、HJ-015腹带、X-001保健护腰、X-002束腰带、X-3腰部保健带、X-4/5/6/7/8/9/10护腰带、HJ-016/016B/016C乳腺绷带 规格：可调/S/M/L/XL/XXL（附件链接地址:http://qxzc.nmpa.gov.cn/upload/ba/1553326221249.doc）";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertFalse(detail.isFinish());
    }

    @Test
    public void testInput11() {
        String sourceStr = "G-001 G-002 G-003 G-004 G-005 S-101 S-102 S-103 S-104 S-105 S-106 S-108 S-110 S-111等型式";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertFalse(detail.isFinish());
    }

    @Test
    public void testInput12() {
        String sourceStr = "7CM×10CM，2贴/袋、3贴/袋、4贴/袋、5贴/袋、6贴/袋、8贴/袋、10贴/袋";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertFalse(detail.isFinish());
    }

    @Test
    public void testInput13() {
        String sourceStr = "15ml/瓶，30ml/瓶，50ml/瓶，250ml/瓶，500ml/瓶，1000ml/瓶，2500ml/瓶，5000ml/瓶，10000ml/瓶，15000ml/瓶，20000ml/瓶, 8ml/瓶, 25ml/瓶、6ml/瓶";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        System.out.println(detail.getClay().toJsonString());
    }


}


