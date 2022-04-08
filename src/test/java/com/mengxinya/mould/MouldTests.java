package com.mengxinya.mould;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

    public void testCompose1() {
        String sourceStr = "A1C";
        Mould comp = Mould.compose(Mould.Letter, Mould.Digit, Mould.Letter);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(detail.getClay().toJsonString(), "[\"A\", 1, \"C\"]");
    }

    public void testTheMould() {
        String sourceStr = "A1C";
        Mould comp = Mould.theMould("A1C");
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(detail.getClay().toJsonString(), JSON.toJSONString("A1C"));
    }
    public void testTheMould2() {
        String sourceStr = "123";
        Mould comp = Mould.theMould(123);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(detail.getClay().toJsonString(), JSON.toJSONString(123));
    }

    public void testJoin() {
        String sourceStr = "1,2,3,4";
        Mould comp = Mould.join(Mould.theMould(","), Mould.Digit);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(detail.getClay().toJsonString(), "[1, 2, 3, 4]");
    }

    public void testRepeat() {
        String sourceStr = "Pink720Red800Green600Yellow500END";
        Mould comp = Mould.join(Mould.EnWord, Mould.Digit);
        SourceDetail detail = Mould.repeat(comp, 1).fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
        Assertions.assertEquals(detail.getClay().toJsonString(), "[\"Pink720\", \"Red800\", \"Green600\", \"Yellow500\"]");
    }

    public void testMaybe1() {
        String sourceStr = "A";
        Mould comp = Mould.maybe(Mould.Letter, Mould.Digit);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
    }

    public void testMaybe2() {
        String sourceStr = "1";
        Mould comp = Mould.maybe(Mould.Letter, Mould.Digit);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertTrue(detail.isMatch());
    }

    public void testMaybe3() {
        String sourceStr = "好";
        Mould comp = Mould.maybe(Mould.Letter, Mould.Digit);
        SourceDetail detail = comp.fill(sourceStr);
        Assertions.assertFalse(detail.isMatch());
    }


    public void testDemo() {
        String sourceStr = "YCLED720/720、YCLED720/520、YCLED520/520、YCLED700/700、YCLED700/500、YCLED500/500、YCLED720、YCLED700、YCLED520、YCLED500、YCLED5+5、YCLED5、YCLED720L、YCLED700L、YCLED520L、YCLED500L、YCLED5L";

    }
}
