package com.lami.tuomatuo.controller.app1;

import com.lami.tuomatuo.controller.BaseController;
import org.springframework.stereotype.Controller;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Controller
public class UserDynamicController extends BaseController {

    @Override
    protected boolean checkAuth() {
        return false;
    }


}