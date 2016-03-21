package com.lami.tuomatuo.crawler.controller;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by xujiankang on 2016/3/21.
 */
public class BaseController {
    protected static final Logger logger = Logger.getLogger(BaseController.class);

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
