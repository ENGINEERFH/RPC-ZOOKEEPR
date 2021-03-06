package com.thrift.rpc.server;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.thrift.rpc.api.JiebaParticiple;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 *
 */
@Service
public class JiebaParticipleImpl implements JiebaParticiple.Iface {

    public JiebaParticipleImpl() {
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
