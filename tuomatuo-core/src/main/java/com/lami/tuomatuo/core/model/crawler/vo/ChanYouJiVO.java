package com.lami.tuomatuo.core.model.crawler.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 2016/3/24.
 */
@Data
public class ChanYouJiVO {
    private ChanYouJiAccountVO chanYouJiAccountVO = new ChanYouJiAccountVO();
    private List<ChanYoujiDynamicVO> chanYoujiDynamicVOList = new ArrayList<ChanYoujiDynamicVO>();
}
