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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.ArrayList;
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
  private static final String IMAGE = "Image";
  private static final String MEDIA_ID = "MediaId";
  


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
      if(content == null || "".equals(content)) return "";

      if(mImageKeywordList.contains(content)) {
        return getImageReply(from,to,content);
      } else {
        return getTextReply(from, to,content);
      }


    } catch (Exception e) {
      e.printStackTrace();
    }

    return "";
  }

  private String getTextReply(String from,String to,String content) {
    String replyContent = "";
    switch(content) {
      case "纪念日":
      case "1":
        replyContent = getCommemorationDayText();
        break;
      case "猪猪":
      case "2":
        replyContent = "猪猪哼哼哼哼";
        break;
      case "猫猫":
      case "3":
        replyContent = "猫猫喵喵喵喵";
        break;
      default:
        replyContent = "猪猪很笨的，还不会这个问题嗷";
        break;                    
    }
    JSONObject reply = new JSONObject();
    reply.put(FROM_USER_NAME, to);
    reply.put(TO_USER_NAME, from);
    reply.put(TIME, System.currentTimeMillis() / 1000);
    reply.put(TYPE, "text");
    reply.put(CONTENT, replyContent);
    logger.info("reply " + reply);
    return reply.toString();
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

  String getCommemorationDayText() {
    StringBuilder sb = new StringBuilder();
    sb.append("老婆好～");
    addLine(sb);
    addLine(sb);
    sb.append(getTodayText());
    addLine(sb);
    sb.append(getLianAiDayText());
    addLine(sb);
    sb.append(getWeddingDayText());
    addLine(sb);
    sb.append(getBirthDayText());
    addLine(sb);
    return sb.toString();
  }

  void addLine(StringBuilder sb) {
    sb.append("\n");
  }

  String getTodayText() {
    LocalDate today = LocalDate.now();
    int month = today.getMonthValue();
    int day = today.getDayOfMonth();
    String week = "";
    DayOfWeek dayOfWeek = today.getDayOfWeek();
    switch (dayOfWeek) {
        case MONDAY:
            week = "星期一";
            break;
        case TUESDAY:
            week = "星期二";
            break;
        case WEDNESDAY:
            week = "星期三";
            break;
        case THURSDAY:
            week = "星期四";
            break;
        case FRIDAY:
            week = "星期五";
            break;
        case SATURDAY:
            week = "星期六";
            break;
        case SUNDAY:
            week = "星期日";
            break;
    }
    return month + "月" + day + "日" + " " + week;
  }


  String getLianAiDayText() {
    LocalDate today = LocalDate.now();
    LocalDate weddingDay = LocalDate.of(2020,11,4);
    long diffDay = today.toEpochDay() - weddingDay.toEpochDay();
    return "今天是我们认识的第 " + diffDay + " 天";
  }

  String getWeddingDayText() {
    LocalDate today = LocalDate.now();
    LocalDate weddingDay = LocalDate.of(2022,6,8);
    long diffDay = today.toEpochDay() - weddingDay.toEpochDay();
    return "我们已经成为合法夫妇 " + diffDay + " 天了";
  }

  String getBirthDayText() {
    LocalDate today = LocalDate.now();
    int year = today.getYear();
    LocalDate birthDay = LocalDate.of(year,11,8);
    if(today.isAfter(birthDay)) {
      birthDay = LocalDate.of(year + 1,11,8);
    }
    long diffDay = birthDay.toEpochDay() - today.toEpochDay();
    if(diffDay == 0) return "今天是猫猫的生日，祝猫猫生日快乐!";
    return "距你的生日还有 " + diffDay + " 天";
  }


  private static final List<String> mImageKeywordList = new ArrayList<>();
  private static final List<String> mChanMaoMediaIdList = new ArrayList<>();
  private static final List<String> mXiaoZhuZhuMediaIdList = new ArrayList<>();

  private static final String DA_PI_MAO = "大屁猫";
  private static final String CHAN_MAO = "馋猫";
  private static final String XIAO_CHAN_MAO = "小馋猫";
  private static final String PI_KA_MAO = "皮卡猫";
  private static final String XIAO_LAN_MAO = "小懒猫";
  private static final String LAN_MAO = "懒猫";
  private static final String XIAO_ZHU_ZHU = "小猪猪";

  static {
    mImageKeywordList.add(DA_PI_MAO);
    mImageKeywordList.add(CHAN_MAO);
    mImageKeywordList.add(XIAO_CHAN_MAO);
    mImageKeywordList.add(PI_KA_MAO);
    mImageKeywordList.add(XIAO_LAN_MAO);
    mImageKeywordList.add(LAN_MAO);    
    mImageKeywordList.add(XIAO_ZHU_ZHU);    

    //馋猫
    mChanMaoMediaIdList.add("og9uZrJ4VLOt3kLhu_AuIF0FnayrYoQ5aF0UT1EL-YP3Bg2fPXVK9TQTyinnXeDc");
    mChanMaoMediaIdList.add("Fqbf5z1cZh_UYBc_M1YfPnHqnpPG75t3JD2GHHQvdat52rVgZJOa_AMb_UdLfo4Y");
    mChanMaoMediaIdList.add("Fqbf5z1cZh_UYBc_M1YfPtJ7fqM5EQxVlH2quhdIWUFJRN0J2Ka59kdky2LDRavA");
    mChanMaoMediaIdList.add("Fqbf5z1cZh_UYBc_M1YfPkq7-E0g04TMAguaEYNSkN-Pe0cNPJz6tBsrp5iO_OQS");
    mChanMaoMediaIdList.add("Fqbf5z1cZh_UYBc_M1YfPvrEkZK-9NM3IQt-AAK8-ppzbBYg12ma2_C44KIhAYfm");

    //小猪猪
    mXiaoZhuZhuMediaIdList.add("Fqbf5z1cZh_UYBc_M1YfPi7lyHJOutaX70fdRSd7UwMBgEvuEJaD6BQvE1Upq-tR");
    mXiaoZhuZhuMediaIdList.add("Fqbf5z1cZh_UYBc_M1YfPt6o_7YUhJzhIvX3qHKq-LFN_MnmZf_1rWMfULAL4ouY");
    mXiaoZhuZhuMediaIdList.add("Fqbf5z1cZh_UYBc_M1YfPlLjuJWmnRMDFLQEMsg4QklKsGfPYF2b9I7pQcs4ynLH");



  }

  private String getImageReply(String from,String to,String content) {
    String mediaId = "";
    switch(content) {
      case DA_PI_MAO:
        mediaId = "og9uZrJ4VLOt3kLhu_AuIDgU4_HTAnlBVDmIJXiXtaiEDlLrt7ZkpXnxN6l432YF";
        break;
      case CHAN_MAO:
      case XIAO_CHAN_MAO:
        mediaId = getRandomMediaId(mChanMaoMediaIdList);
        break;
      case PI_KA_MAO:
        mediaId = "Uqr_FN07KQF_j-eLL1GCno8Ifulw8wybn1ZFts0A-ofOuXirx-gon5F7izi-BywM";     
        break;
      case XIAO_ZHU_ZHU :
        mediaId = getRandomMediaId(mXiaoZhuZhuMediaIdList);
        break;
      default:
        return "";                  
    }
    JSONObject reply = new JSONObject();
    reply.put(FROM_USER_NAME, to);
    reply.put(TO_USER_NAME, from);
    reply.put(TIME, System.currentTimeMillis() / 1000);
    reply.put(TYPE, "image");
    JSONObject image = new JSONObject();
    image.put(MEDIA_ID, mediaId);
    reply.put(IMAGE, image);
    logger.info("reply " + reply);
    return reply.toString();
  }

  private String getRandomMediaId(List<String> list) {
    Random random = new Random();
    int index = random.nextInt(list.size());
    return list.get(index);
  }
}