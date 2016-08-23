package com.lami.tuomatuo.controller.dict;

/**
 * Created by xujiankang on 2016/8/23.
 */
import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.dict.DictUser;
import com.lami.tuomatuo.model.po.BaseParam;
import com.lami.tuomatuo.service.UserService;
import com.lami.tuomatuo.service.dict.DictUserService;
import com.lami.tuomatuo.utils.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by xjk on 10/21/15.
 */
public abstract class DictBaseController {

    @Autowired
    private DictUserService dictUserService;

    protected  final Logger logger = Logger.getLogger(getClass());
    protected abstract boolean checkAuth();

    protected  Result execute(BaseParam baseParam){
        logger.info("baseParam:"+baseParam);
        if (!checkAuth()) return new Result(Result.SUCCESS);
        if(!StringUtil.isMobile(baseParam.getMobile()) || StringUtil.isEmpty(baseParam.getSign())) return new Result(Result.PARAMCHECKERROR);
        DictUser user = dictUserService.getDictUserByMobile(baseParam.getMobile());
        if (user == null || !user.getPasswd().equals(baseParam.getSign())){
            return new Result(Result.ACCOUNT_ILLEGAL);
        }
        return new Result(Result.SUCCESS).setValue(user);
    }

    protected Map<String, Object> getParam(HttpServletRequest request){
        Map<String, Object> map = request.getParameterMap();

        Map<String, Object> param = new HashMap<String,Object>();
        Map.Entry entry;
        String key = "";
        String value = "";
        if (map != null && map.size() > 0) {
            Iterator entries = map.entrySet().iterator();
            while (entries.hasNext()) {
                entry = (Map.Entry) entries.next();
                key = (String) entry.getKey();
                Object valueObj = entry.getValue();
                if (null == valueObj) {
                    value = "";
                } else if (valueObj instanceof String[]) {
                    String[] values = (String[]) valueObj;
                    for (int i = 0; i < values.length; i++) {
                        value = values[i] + ",";
                    }
                    value = value.substring(0, value.length() - 1);
                } else {
                    value = valueObj.toString();
                }
                param.put(key, value);
            }
        }
        return param;
    }

    public String getBody(HttpServletRequest request){
        String brStr = "";
        try {
            BufferedReader br = request.getReader();
            while (br.read() != -1) {
                brStr+="<"+br.readLine();
            }
            logger.info("[brStr]=" + brStr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return brStr;
    }
}