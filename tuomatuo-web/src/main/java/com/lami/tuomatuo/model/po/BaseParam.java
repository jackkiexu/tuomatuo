package com.lami.tuomatuo.model.po;

import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.utils.StringUtil;
import lombok.Data;

/**
 * Created by xujiankang on 2016/1/25.
 */
@Data
public abstract class BaseParam {
    protected Long userId; // 用户 id
    protected String mobile; // 用户手机号码
    protected String sign; // 用户的签名
    protected Integer pageIndex = 1;
    protected Integer pageSize = 10;

    public Integer getOffset(){
        return (pageIndex - 1)*pageSize;
    }
}
