package com.tencent.wxcloudrun.controller;

import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CounterRequest;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * counter控制器
 */
@RestController

public class CounterController {

  final CounterService counterService;
  final Logger logger;

  public CounterController(@Autowired CounterService counterService) {
    this.counterService = counterService;
    this.logger = LoggerFactory.getLogger(CounterController.class);
  }


  /**
   * 获取当前计数
   * @return API response json
   */
  @GetMapping(value = "/api/count")
  ApiResponse get() {
    logger.info("/api/count get request");
    Optional<Counter> counter = counterService.getCounter(1);
    Integer count = 0;
    if (counter.isPresent()) {
      count = counter.get().getCount();
    }

    return ApiResponse.ok(count);
  }


  /**
   * 更新计数，自增或者清零
   * @param request {@link CounterRequest}
   * @return API response json
   */
  @PostMapping(value = "/api/count")
  ApiResponse create(@RequestBody CounterRequest request) {
    logger.info("/api/count post request, action: {}", request.getAction());

    Optional<Counter> curCounter = counterService.getCounter(1);
    if (request.getAction().equals("inc")) {
      Integer count = 1;
      if (curCounter.isPresent()) {
        count += curCounter.get().getCount();
      }
      Counter counter = new Counter();
      counter.setId(1);
      counter.setCount(count);
      counterService.upsertCount(counter);
      return ApiResponse.ok(count);
    } else if (request.getAction().equals("clear")) {
      if (!curCounter.isPresent()) {
        return ApiResponse.ok(0);
      }
      counterService.clearCount(1);
      return ApiResponse.ok(0);
    } else {
      return ApiResponse.error("参数action错误");
    }
  }



  private static final String FROM_USER_NAME = "FromUserName";
  private static final String TO_USER_NAME = "ToUserName";
  private static final String CONTENT = "Content";
  private static final String TYPE = "MsgType";
  private static final String TIME = "CreateTime";

  /**
   * 获取公众号信息
   * @return API response json
   */
  @PostMapping(value = "/api/wx")
  public String createWx(HttpServletRequest request) {
    try {
      String body = getBodytxt(request);
      logger.info("/api/wx post request, action: {}",body);
      JSONObject jsonObject = new JSONObject(body);
      //用户id
      String from = jsonObject.getString(FROM_USER_NAME);
      //公众号id
      String to = jsonObject.getString(TO_USER_NAME);
      //内容
      String content = jsonObject.getString(CONTENT);
      //type
      String type = jsonObject.getString(TYPE);
      

      logger.info("from: " + from + ",to: " + to + " ,content:" + content + ", type = " + type);
      

      JSONObject reply = new JSONObject();
      reply.put(FROM_USER_NAME, to);
      reply.put(TO_USER_NAME, from);
      reply.put(TIME, System.currentTimeMillis() / 1000);
      reply.put(TYPE, "text");
      reply.put(CONTENT, content + "123");
      logger.info("reply " + reply);
      return reply.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "";
  }


  String getBodytxt(HttpServletRequest request) {
    String str, wholeStr = "";
    try {
      BufferedReader br = request.getReader();
 
      while((str = br.readLine()) != null){
        wholeStr += str;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return wholeStr;
  }

}