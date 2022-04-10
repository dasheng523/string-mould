package com.mengxinya.mould;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CosineSimilarity
 *
 * @author sherry
 * @date 2020/9/25 13:00
 */
public class CosineSimilarity {

    public static double computeTxtSimilar(String txtLeft, String txtRight){
        //所有文档的总词库
        List<String> totalWordList = new ArrayList<String>();
        //计算文档的词频
        Map<String, Integer> leftWordCountMap = getWordCountMap(txtLeft, totalWordList);
        Map<String, Float> leftWordTfMap = calculateWordTf(leftWordCountMap);

        Map<String, Integer> rightWordCountMap = getWordCountMap(txtRight, totalWordList);
        Map<String, Float> rightWordTfMap = calculateWordTf(rightWordCountMap);


        //获取文档的特征值
        List<Float> leftFeature = getTxtFeature(totalWordList,leftWordTfMap);
        List<Float> rightFeature = getTxtFeature(totalWordList,rightWordTfMap);

        //计算文档对应特征值的平方和的平方根
        float leftVectorSqrt = calculateVectorSqrt(leftWordTfMap);
        float rightVectorSqrt = calculateVectorSqrt(rightWordTfMap);

        //根据余弦定理公式，计算余弦公式中的分子
        float fenzi = getCosValue(leftFeature,rightFeature);

        //根据余弦定理计算两个文档的余弦值
        double cosValue = 0;
        if (fenzi > 0) {
            cosValue = fenzi / (leftVectorSqrt * rightVectorSqrt);
        }
        cosValue = Double.parseDouble(String.format("%.4f",cosValue));
        return cosValue;

    }

    /**
     * @Author：sks
     * @Description：获取词及词频键值对，并将词保存到词库中
     * @Date：
     */
    public static  Map<String,Integer> getWordCountMap(String text,List<String> totalWordList){
        Map<String,Integer> wordCountMap = new HashMap<String,Integer>();
        List<Term> words= HanLP.segment(text);
        int count = 0;
        for(Term tm:words){
            //取字数为两个字或两个字以上名词或动名词作为关键词
            if(tm.word.length()>1 && (tm.nature== Nature.n||tm.nature== Nature.vn)){
                count = 1;
                if(wordCountMap.containsKey(tm.word))
                {
                    count = wordCountMap.get(tm.word) + 1;
                    wordCountMap.remove(tm.word);
                }
                wordCountMap.put(tm.word,count);
                if(!totalWordList.contains(tm.word)){
                    totalWordList.add(tm.word);
                }
            }
        }
        return wordCountMap;
    }



    //计算关键词词频
    private static Map<String, Float> calculateWordTf(Map<String, Integer> wordCountMap) {
        Map<String, Float> wordTfMap =new HashMap<String, Float>();
        int totalWordsCount = 0;
        Collection<Integer> cv = wordCountMap.values();
        for (Integer count : cv) {
            totalWordsCount += count;
        }

        wordTfMap = new HashMap<String, Float>();
        Set<String> keys = wordCountMap.keySet();
        for (String key : keys) {
            wordTfMap.put(key, wordCountMap.get(key) / (float) totalWordsCount);
        }
        return wordTfMap;
    }

    //计算文档对应特征值的平方和的平方根
    private static float calculateVectorSqrt(Map<String, Float> wordTfMap) {
        float result = 0;
        Collection<Float> cols =  wordTfMap.values();
        for(Float temp : cols){
            if (temp > 0) {
                result += temp * temp;
            }
        }
        return (float) Math.sqrt(result);
    }



    private static List<Float> getTxtFeature(List<String> totalWordList, Map<String, Float> wordCountMap){
        List<Float> list =new ArrayList<Float>();
        for(String word :totalWordList){
            float tf = 0;
            if(wordCountMap.containsKey(word)){
                tf = wordCountMap.get(word);
            }
            list.add(tf);
        }
        return list;
    }

    /**
     * @Author：sks
     * @Description：根据两个向量计算余弦值
     * @Date：
     */
    private static float getCosValue(List<Float> leftFeature, List<Float> rightFeature) {
        float fenzi = 0;
        float tempX = 0;
        float tempY = 0;
        for (int i = 0; i < leftFeature.size(); i++) {
            tempX = leftFeature.get(i);
            tempY = rightFeature.get(i);
            if (tempX > 0 && tempY > 0) {
                fenzi += tempX * tempY;
            }
        }
        return fenzi;
    }
}
