package com.lami.sms.pipe.yupian;

import lombok.Data;

/**云片 发送短信的结果
 * Created by xujiankang on 2016/1/25.
 */
@Data
public class SendResult {
    private Integer code; // 0 成功,
    private String msg; // 返回信息


    @Data
    class result {
        private Integer count; // 成功发送的短信个数
        private Integer fee; //扣费条数，70个字一条，超出70个字时按每67字一条计
        private Integer sid; //短信id；多个号码时以该id+各手机号尾号后8位作为短信id,
    }

}


