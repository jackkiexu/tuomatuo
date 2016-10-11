package com.lami.tuomatuo.service.dict;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.dict.DictWordDaoInterface;
import com.lami.tuomatuo.model.dict.DictWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xjk on 2016/8/23.
 */
@Service("dictWordService")
public class DictWordService extends BaseService<DictWord, Long> {

    @Autowired
    private DictWordDaoInterface dictWordDaoInterface;

    public List<DictWord> getUnitWord(Long unitId){
        DictWord dictWord = new DictWord();
        dictWord.setUnit(unitId);
        List<DictWord> dictWordList = dictWordDaoInterface.search(dictWord);
        return dictWordList;
    }

    public List<String> getUnitWordStr(Long unitId){
        List<String> result = new LinkedList<String>();
        List<DictWord> dictWordList = getUnitWord(unitId);
        for(DictWord dictWord : dictWordList){
            result.add(dictWord.getWord());
        }
        return result;
    }

    public void addWordToUnit(String word, Long unitId){
        DictWord dictWord = new DictWord();
        dictWord.setUnit(unitId);
        dictWord.setWord(word);
        dictWord.setCreateTime(new Date());
        dictWord.setUpdateTime(new Date());
        dictWordDaoInterface.save(dictWord);
    }

}
