package com.thrift.service;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.apache.thrift.TException;
import com.thrift.api.JiebaParticiple;

import java.util.Iterator;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class UserServiceImpl implements JiebaParticiple.Iface {

    public UserServiceImpl() {
    }

    @Override
    public String participle(String bestring) throws TException {

        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> resultList = segmenter.process(bestring, JiebaSegmenter.SegMode.SEARCH);
        Iterator<SegToken> it = resultList.iterator();
        if (!it.hasNext())
            return null;

        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            SegToken s = it.next();
            if(!" ".equals(s.word)){
                sb.append(s.word).append('/');
            }
        }

        return  sb.toString();

    }
}
