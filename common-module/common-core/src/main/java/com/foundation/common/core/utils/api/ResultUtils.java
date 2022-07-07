package com.foundation.common.core.utils.api;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author
 * @date 2022-07-05 15:03
 */
public class ResultUtils {

    public static void responseJson(HttpServletResponse response, Object object) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control","no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(new Gson().toJson(object));
        response.getWriter().flush();
    }

}
