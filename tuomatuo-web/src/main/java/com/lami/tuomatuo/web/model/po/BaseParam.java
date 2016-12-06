package com.lami.tuomatuo.web.model.po;

import lombok.Data;

/**
 * Created by xjk on 2016/1/25.
 */
@Data
public abstract class BaseParam {
    protected Long userId; // 用户 id
    protected String mobile; // 用户手机号码
    protected String sign; // 用户的签名
    protected Integer pageIndex = 1;
    protected Integer pageSize = 20;

    public Integer getOffset(){
        return (pageIndex - 1)*pageSize;
    }
}
