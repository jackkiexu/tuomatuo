package com.lami.tuomatuo.model.crawler.vo;

import com.lami.tuomatuo.model.crawler.ChanYouJiAccount;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujiankang on 2016/3/24.
 */
@Data
public class ChanYouJiVO {
    private ChanYouJiAccountVO chanYouJiAccountVO = new ChanYouJiAccountVO();
    private List<ChanYoujiDynamicVO> chanYoujiDynamicVOList = new ArrayList<ChanYoujiDynamicVO>();
}
