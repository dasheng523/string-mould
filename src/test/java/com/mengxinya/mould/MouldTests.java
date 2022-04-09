package com.mengxinya.mould;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class MouldTests {

    @Test
    public void testLetter1() {
        String sourceStr = "YCLED720/720";
        SourceDetail detail = Mould.Letter.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals('Y', detail.getClay().value(Character.class));
    }

    @Test
    public void testLetter2() {
        String sourceStr = "720";
        SourceDetail detail = Mould.Letter.fill(sourceStr);
        Assertions.assertFalse(detail.isMatch());
    }

    @Test
    public void testNumber1() {
        String sourceStr = "720";
        SourceDetail detail = Mould.Digit.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(7, detail.getClay().value(Integer.class));
    }

    @Test
    public void testNumber2() {
        String sourceStr = "A1C";
        SourceDetail detail = Mould.Digit.fill(sourceStr);
        Assertions.assertFalse(detail.isMatch());
    }

    @Test
    public void testNumber3() {
        String sourceStr = "720";
        SourceDetail detail = Mould.Digits.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(720, detail.getClay().value(Integer.class));
    }

    @Test
    public void testEnWord1() {
        String sourceStr = "ABC";
        SourceDetail detail = Mould.EnWord.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals("ABC", detail.getClay().value(String.class));
    }

    @Test
    public void testEnWord2() {
        String sourceStr = "1AC";
        SourceDetail detail = Mould.EnWord.fill(sourceStr);
        Assertions.assertFalse(detail.isMatch());
    }
    @Test
    public void testEnWord3() {
        String sourceStr = "A1C";
        SourceDetail detail = Mould.EnWord.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals("A", detail.getClay().value(String.class));
    }

    @Test
    public void testCompose1() {
        String sourceStr = "A1C";
        Mould comp = Mould.compose(Mould.Letter, Mould.Digit, Mould.Letter);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals("[\"A\",1,\"C\"]", detail.getClay().toJsonString());
    }


    @Test
    public void testCompose2() {
        String sourceStr = "Pink720END";
        Mould comp = Mould.convert(Mould.compose(Mould.EnWord, Mould.Digits), ClayConverter.joining(""));
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(JSON.toJSONString("Pink720"), detail.getClay().toJsonString());
    }


    @Test
    public void testTheMould() {
        String sourceStr = "A1C";
        Mould comp = Mould.theMould("A1C");
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(JSON.toJSONString("A1C"), detail.getClay().toJsonString());
    }

    @Test
    public void testTheMould2() {
        String sourceStr = "123";
        Mould comp = Mould.theMould(123);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(JSON.toJSONString(123), detail.getClay().toJsonString());
    }

    @Test
    public void testJoin1() {
        String sourceStr = "1,2,3,4";
        Mould comp = Mould.join(Mould.theMould(","), Mould.Digit);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(
                JSON.toJSONString(Arrays.asList(1, 2, 3, 4)),
                detail.getClay().toJsonString()
        );
    }

    @Test
    public void testJoin2() {
        String sourceStr = "/1,2,3,4";
        Mould comp = Mould.join(Mould.theMould(","), Mould.Digit);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertFalse(detail.isMatch());
    }

    @Test
    public void testJoin3() {
        String sourceStr = "1";
        Mould comp = Mould.join(Mould.theMould(","), Mould.Digit);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
    }

    @Test
    public void testRepeat1() {
        String sourceStr = "Pink720";
        SourceDetail detail = Mould.repeat(Mould.Letter, 1).fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(JSON.toJSONString(Arrays.asList("P", "i", "n", "k")), detail.getClay().toJsonString());
    }

    @Test
    public void testRepeat2() {
        String sourceStr = "Pink720Red800Green600Yellow500END";
        Mould comp = Mould.convert(Mould.compose(Mould.EnWord, Mould.Digits), ClayConverter.joining(""));
        SourceDetail detail = Mould.repeat(comp, 1).fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(
                JSON.toJSONString(Arrays.asList("Pink720", "Red800", "Green600", "Yellow500")),
                detail.getClay().toJsonString()
        );
    }

    @Test
    public void testRepeat3() {
        String sourceStr = "Pink720Red800Green600Yellow500";
        Mould comp = Mould.convert(Mould.compose(Mould.EnWord, Mould.Digits), ClayConverter.joining(""));
        SourceDetail detail = Mould.repeat(comp, 1).fill(sourceStr);

        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(
                JSON.toJSONString(Arrays.asList("Pink720", "Red800", "Green600", "Yellow500")),
                detail.getClay().toJsonString()
        );
    }

    @Test
    public void testRepeat4() {
        String sourceStr = "A";
        Mould comp = Mould.repeat(Mould.zeroOrOne(Mould.Digit));
        SourceDetail detail = comp.fill(sourceStr);

        Assertions.assertTrue(detail.isMatch());
    }

    @Test
    public void testMaybe1() {
        String sourceStr = "A";
        Mould comp = Mould.maybe(Mould.Letter, Mould.Digit);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
    }

    @Test
    public void testMaybe2() {
        String sourceStr = "1";
        Mould comp = Mould.maybe(Mould.Letter, Mould.Digit);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
    }

    @Test
    public void testMaybe3() {
        String sourceStr = "好";
        Mould comp = Mould.maybe(Mould.Letter, Mould.Digit);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertFalse(detail.isMatch());
    }

    @Test
    public void testHan1() {
        String sourceStr = "好";
        SourceDetail detail = Mould.Han.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals("好", detail.getClay().value(String.class));
    }

    @Test
    public void testHan2() {
        String sourceStr = "A";
        SourceDetail detail = Mould.Han.fill(sourceStr);
        Assertions.assertFalse(detail.isMatch());
    }

    @Test
    public void testHan3() {
        String sourceStr = "你好hello";
        SourceDetail detail = Mould.Hans.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals("你好", detail.getClay().value(String.class));
    }

    @Test
    public void testZeroOrOne1() {
        String sourceStr = "hello";
        SourceDetail detail = Mould.composeJoining(Mould.zeroOrOne(Mould.Digits), Mould.EnWord).fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals("hello", detail.getClay().value(String.class));
    }

    @Test
    public void testZeroOrOne2() {
        String sourceStr = "8hello";
        SourceDetail detail = Mould.composeJoining(Mould.zeroOrOne(Mould.Digits), Mould.EnWord).fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals("8hello", detail.getClay().value(String.class));
    }

    @Test
    public void testBackRef() {
        String sourceStr = "aa#bb#cc";
        Mould.MouldContext context = Mould.makeContext();
        Mould mould = Mould.composeJoining(
                Mould.EnWord,
                context.ref(Mould.theMould("#"), "sep"),
                Mould.EnWord,
                context.backRef("sep"),
                Mould.EnWord
        );
        SourceDetail detail = mould.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(sourceStr, detail.getClay().value(String.class));
    }

    @Test
    public void testJoinRef1() {
        String sourceStr = "aa#bb#cc";
        Mould.MouldContext context = Mould.makeContext();
        Mould mould = Mould.join(context, Mould.maybe(Mould.theMould("#"), Mould.theMould(".")), Mould.EnWord);
        SourceDetail detail = mould.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(Arrays.asList("aa", "bb", "cc"), detail.getClay().value(List.class));
    }

    @Test
    public void testJoinRef2() {
        String sourceStr = "aa#bb.cc";
        Mould.MouldContext context = Mould.makeContext();
        Mould mould = Mould.join(context, Mould.maybe(Mould.theMould("#"), Mould.theMould(".")), Mould.EnWord);
        SourceDetail detail = mould.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(Arrays.asList("aa", "bb"), detail.getClay().value(List.class));
    }

    @Test
    public void testCons() {
        String sourceStr = "aa#bb#cc";
        Mould mould = Mould.cons(
                Mould.EnWord,
                Mould.repeat(
                        Mould.convert(
                                Mould.compose(Mould.theMould("#"), Mould.EnWord),
                                ClayConverter.deconstruct(1)
                        )
                )
        );
        SourceDetail detail = mould.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(Arrays.asList("aa", "bb", "cc"), detail.getClay().value(List.class));
    }


    @Test
    public void testDemo1() {
        String sourceStr = "YCLED720";
        Mould itemMould = Mould.composeJoining(
                Mould.EnWord,
                Mould.zeroOrOne(Mould.theMould("+")),
                Mould.Digits
        );
        SourceDetail detail = itemMould.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
    }

    @Test
    public void testDemo() {
        String sourceStr = "YCLED720/720、YCLED720/520、YCLED720、YCLED55、YCLED720L、YCLED5L";
        Mould itemMould = Mould.composeJoining(
                Mould.EnWord,
                Mould.Digits,
                Mould.zeroOrOne(
                        Mould.maybe(
                                Mould.Letter,
                                Mould.composeJoining(Mould.theMould("/"), Mould.Digits)
                        )
                )
        );
        Mould mould = Mould.join(Mould.theMould("、"), itemMould);
        SourceDetail detail = mould.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(
                JSON.toJSONString(Arrays.asList("YCLED720/720", "YCLED720/520", "YCLED720", "YCLED55", "YCLED720L", "YCLED5L")),
                detail.getClay().toJsonString()
        );
    }


    private SourceDetail superMould(String sourceStr) {
        Mould.MouldContext context = Mould.makeContext();
        Mould mould = Mould.join(
                context,
                Mould.maybe(
                        Mould.theMould("、"),
                        Mould.theMould("，"), Mould.theMould(","),
                        Mould.theMould("；"), Mould.theMould(";")
                ),
                Mould.convert(
                        Mould.repeat(Mould.maybe(
                                Mould.Letter,
                                Mould.Digit,
                                Mould.Han,
                                Mould.theMould("-"), Mould.theMould("+"),
                                Mould.theMould("("), Mould.theMould(")"),
                                Mould.theMould("（"), Mould.theMould("）")
                        )),
                        ClayConverter.joining("")
                )
        );
        return mould.fill(sourceStr);
    }


    @Test
    public void testInput1() {
        String sourceStr = "6Fr、8Fr、10Fr、12Fr、14Fr、16Fr、18Fr、20Fr、22Fr、24Fr、26Fr、28Fr、30Fr";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 5);
    }

    @Test
    public void testInput2() {
        String sourceStr = "H003-A、H003-B、H003-C、H003-D、H003-E、H003-F、H003-G、H003-H、H003-I";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 5);
    }

    @Test
    public void testInput3() {
        String sourceStr = "常规型、防逆流A型、防逆流B型";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(3, Clay.deconstruct(detail.getClay()).size());
    }

    @Test
    public void testInput4() {
        String sourceStr = "普通型（1000）、普通型（2000）、防逆流型（1500）、防逆流型（2000）、精密计量型（2200）、精密计量型（2500）、精密计量型（2700）、精密计量型（3000）、精密计量型（3100）、绑腿型（500）、绑腿型（600）、绑腿型（750）、绑腿型（900）、绑腿型（1000）。";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 5);
        Assertions.assertEquals("普通型（1000）", Clay.deconstruct(detail.getClay()).get(0).value(String.class));
    }

}
