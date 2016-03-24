package com.lami.tuomatuo.model.crawler.vo;

import lombok.Data;

/**
 * Created by xujiankang on 2016/3/24.
 */
@Data
public class ChanYoujiDynamicVO {

    private Long id;
    private Long chanYouId;
    private String dynaWebURL;
    private String dynaCoverImgURL;
    private Long seeSum;
    private Long msgSum;
    private Long loveSum;
    private Long forwardSum;
    private String dynaTitle;
    private String dynamicMeta;

    public ChanYoujiDynamicVO() {
    }

    public ChanYoujiDynamicVO(Long chanYouId) {
        this.chanYouId = chanYouId;
    }
}
