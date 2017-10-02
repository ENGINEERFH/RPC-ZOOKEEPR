package com.jieba.test;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

import java.util.Iterator;
import java.util.List;

public class JiebaTest {
    public void testDemo() {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        String sentence ="这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。";
        System.out.println(segmenter.process(sentence, SegMode.SEARCH).toString());
        List<SegToken> resultList = segmenter.process(sentence, SegMode.SEARCH);
        Iterator<SegToken> it = resultList.iterator();
        if (!it.hasNext())
            return ;

        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            SegToken s = it.next();
            if(!" ".equals(s.word)){
                sb.append(s.word).append('/');
            }
        }

        System.out.println(sb.toString());
    }

    public static void main(String[] args) {
        JiebaTest jt = new JiebaTest();
        jt.testDemo();
    }

}
