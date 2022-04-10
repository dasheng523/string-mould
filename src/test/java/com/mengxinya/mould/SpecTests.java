package com.mengxinya.mould;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpecTests {
    static String joinKey = "join";
    static Mould.MouldContext context = Mould.makeContext();
    public static Mould main = Mould.join(
            context,
            joinKey,
            Mould.composeJoining(
                    Mould.repeat(Mould.theMould(" "), 0),
                    Mould.maybe(
                            Mould.theMould("、"),
                            Mould.maybe(Mould.theMould("，"), Mould.theMould(",")),
                            Mould.maybe(Mould.theMould("；"), Mould.theMould(";"))
                    ),
                    Mould.repeat(Mould.theMould(" "), 0)
            ),
            Mould.convert(
                    Mould.repeat(Mould.maybe(
                            Mould.Letter,
                            Mould.Digit,
                            Mould.Han,
                            Mould.theMould("㎜"), Mould.theMould("*"),
                            Mould.theMould("-"), Mould.theMould("+"), Mould.theMould("."), Mould.theMould(" "),
                            Mould.theMould("φ"), Mould.theMould("#"), Mould.theMould("×"), Mould.theMould("/"),
                            Mould.theMould("("), Mould.theMould(")"),
                            Mould.theMould("（"), Mould.theMould("）")
                    )),
                    ClayConverter.joining("")
            )
    );

    private SourceDetail superMould(String source) {

        source = source.replaceAll(",", "，").replaceAll(";", "；");


        Mould mould = Mould.convert(
                Mould.compose(
                        main,
                        Mould.zeroOrOne(Mould.maybe(
                                Mould.theMould("。"),
                                context.backRef(joinKey)
                        )),
                        Mould.EOF),
                ClayConverter.deconstruct(0)
        );
        SourceDetail detail = mould.fill(source);
        if (detail.isFinish() && Clay.getValues(detail.getClay(), Object.class).size() > 1) {
            return detail;
        }
        return SourceDetail.notMatch(source);
    }


    @Test
    public void testInput1() {
        String sourceStr = "6Fr、8Fr、10Fr、12Fr、14Fr、16Fr、18Fr、20Fr、22Fr、24Fr、26Fr、28Fr、30Fr";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 5);
    }

    @Test
    public void testInput2() {
        String sourceStr = "H003-A、H003-B、H003-C、H003-D、H003-E、H003-F、H003-G、H003-H、H003-I";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 5);
    }

    @Test
    public void testInput3() {
        String sourceStr = "常规型、防逆流A型、防逆流B型";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertEquals(3, Clay.deconstruct(detail.getClay()).size());
    }

    @Test
    public void testInput4() {
        String sourceStr = "普通型（1000）、普通型（2000）、防逆流型（1500）、防逆流型（2000）、精密计量型（2200）、精密计量型（2500）、精密计量型（2700）、精密计量型（3000）、精密计量型（3100）、绑腿型（500）、绑腿型（600）、绑腿型（750）、绑腿型（900）、绑腿型（1000）。";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 5);
        Assertions.assertEquals("普通型（1000）", Clay.deconstruct(detail.getClay()).get(0).value(String.class));
    }

    @Test
    public void testInput5() {
        String sourceStr = "SG.Y01、SG.Y02、SG.Y03、SG.Y11、SG.Y12、SG.Y13、SG.Y21";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 5);
        Assertions.assertEquals("SG.Y01", Clay.deconstruct(detail.getClay()).get(0).value(String.class));
    }

    @Test
    public void testInput6() {
        String sourceStr = "G-1500ml、G-2000ml、G-2500ml、G-3000ml、G-3500ml；L-1500ml、L-2000ml、L-2500ml、L-3000ml、L-3500ml。";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertFalse(detail.isFinish());
    }

    @Test
    public void testInput7() {
        String sourceStr = "见附页";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertFalse(detail.isFinish());
    }

    @Test
    public void testInput8() {
        String sourceStr = "SD100、SD200、 SD300、SD400、SD200/200、SD300/300、SD400/400、SDY100、SDY200、SDY300、SDY400、SDK4、SDK5、SDK9、SDK12、SDK12/5、SDK9/4、SDK5/5、SDK4/4、SDYK4、SDYK5。";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 5);
        Assertions.assertEquals("SD100", Clay.deconstruct(detail.getClay()).get(0).value(String.class));
    }

    @Test
    public void testInput9() {
        String sourceStr = "HWGDJ-I-25、HWGDJ-I-50、HWGDJ-I-80、HWGDJ- I-150、HWGDJ-Ⅱ-25、HWGDJ-Ⅱ-50、HWGDJ-Ⅱ-80、HWGDJ-Ⅱ-150";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 5);
        Assertions.assertEquals("HWGDJ-I-25", Clay.deconstruct(detail.getClay()).get(0).value(String.class));
    }

    @Test
    public void testInput10() {
        String sourceStr = "直无钩140㎜、160㎜、180㎜、200㎜、220㎜。";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 3);
        Assertions.assertEquals("直无钩140㎜", Clay.deconstruct(detail.getClay()).get(0).value(String.class));
    }

    @Test
    public void testInput11() {
        String sourceStr = "YT-150,YT-200,YT-300,YT-400,YT-500,YT-600,YT-700,YT-800,YT-1000，YT-900";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 3);
        Assertions.assertEquals("YT-150", Clay.deconstruct(detail.getClay()).get(0).value(String.class));
    }

    @Test
    public void testInput12() {
        String sourceStr = "YT-150,YT-200,YT-300,YT-400,YT-500,YT-600,YT-700,YT-800,YT-1000，YT-900,";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 3);
        Assertions.assertEquals("YT-150", Clay.deconstruct(detail.getClay()).get(0).value(String.class));
    }

    @Test
    public void testInput13() {
        String sourceStr = "TN100/XN100/8*10in、10*12in、11*14in、14*17in、203*254mm、254*305mm、279*356mm、356*432mm、356*43m。";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 3);
        Assertions.assertEquals("TN100/XN100/8*10in", Clay.deconstruct(detail.getClay()).get(0).value(String.class));
    }


}


