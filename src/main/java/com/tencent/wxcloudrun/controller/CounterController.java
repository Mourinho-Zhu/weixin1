package com.tencent.wxcloudrun.controller;

import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CounterRequest;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.model.ImageReplyBean;
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
        return getImageReplyExtends(from,to,content);
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
  private static final List<ImageReplyBean> mChanMaoMediaIdList = new ArrayList<>();
  private static final List<ImageReplyBean> mXiaoZhuZhuMediaIdList = new ArrayList<>();
  private static final List<ImageReplyBean> mPikaMaoMediaIdList = new ArrayList<>();
  private static final List<ImageReplyBean> mDaPiMaoMediaIdList = new ArrayList<>();
  private static final List<ImageReplyBean> mMaomaoMediaIdList = new ArrayList<>();
  private static final List<ImageReplyBean> mLanMaoMediaIdList = new ArrayList<>();
  private static final List<ImageReplyBean> mDaChanZhuMediaIdList = new ArrayList<>();
  private static final List<ImageReplyBean> mDaLanZhuMediaIdList = new ArrayList<>();
  private static final List<ImageReplyBean> mMomoMediaIdList = new ArrayList<>();
  private static final List<ImageReplyBean> mQinQinMediaIdList = new ArrayList<>();

  private static final String DA_PI_MAO = "大屁猫";
  private static final String CHAN_MAO = "馋猫";
  private static final String XIAO_CHAN_MAO = "小馋猫";
  private static final String PI_KA_MAO = "皮卡猫";
  private static final String XIAO_LAN_MAO = "小懒猫";
  private static final String LAN_MAO = "懒猫";
  private static final String XIAO_ZHU_ZHU = "小猪猪";
  private static final String ZHU_ZHU = "猪猪";
  private static final String MAO_MAO = "猫猫";
  private static final String DA_CHAN_ZHU = "大馋猪";
  private static final String CHAN_ZHU = "馋猪";
  private static final String DA_LAN_ZHU = "大懒猪";
  private static final String LAN_ZHU = "懒猪";
  private static final String MO_MO = "摸摸";
  private static final String QIN_QIN = "亲亲";

  static {
    mImageKeywordList.add(DA_PI_MAO);
    mImageKeywordList.add(CHAN_MAO);
    mImageKeywordList.add(XIAO_CHAN_MAO);
    mImageKeywordList.add(PI_KA_MAO);
    mImageKeywordList.add(XIAO_LAN_MAO);
    mImageKeywordList.add(LAN_MAO);    
    mImageKeywordList.add(XIAO_ZHU_ZHU); 
    mImageKeywordList.add(ZHU_ZHU);  
    mImageKeywordList.add(MAO_MAO);   
    mImageKeywordList.add(DA_CHAN_ZHU);  
    mImageKeywordList.add(CHAN_ZHU);  
    mImageKeywordList.add(DA_LAN_ZHU);  
    mImageKeywordList.add(LAN_ZHU); 
    mImageKeywordList.add(MO_MO); 
    mImageKeywordList.add(QIN_QIN);    

    //馋猫
    mChanMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMV7Yibh9z98yr7nkwaNKgzYS9xA6mmB4HnTADAJk0JDtGicprJHY4yxPQ/0?wx_fmt=gif","好好吃"));
    mChanMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMWdSFF0rbTnxWwRgP4HwmsW3hK3hkBvNayJibE6GGW2zbpcSAgMic319g/0?wx_fmt=gif","好好吃"));
    mChanMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMwVLoSOKB8QxSIsTIe3lQLFQxk3qNRNxKzN6sr7S1iaCBicggd2icHvmfA/0?wx_fmt=gif","好好吃"));
    mChanMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMROpOMkqibJBvtqq156D2nazUk4WFz74Zo64icwFq2BBjTCIHuTQONClQ/0?wx_fmt=gif","好好吃"));
    mChanMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMNG75HeVIqj9sbtTY5r340HusTBTCnLAGSkz7aak8N07kGqf7gtcgNw/0?wx_fmt=gif","好好吃"));

    //小猪猪
    mXiaoZhuZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMogKJ5AsTJDumicLib3ddRJSywhfuMlvDHEGlDk9YoU8qVXsf7RzH5ia5w/0?wx_fmt=gif","哼哼哼哼"));
    mXiaoZhuZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMLSoiaYia2ZfC6KlfIDZVicGk2QBrLfewj4NO7JWVkX9bP5oqcXIwe2SZA/0?wx_fmt=gif","哼哼哼哼"));
    mXiaoZhuZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMv3ravQWtls7iceHqibhb43wYzwiabbic6JZ7oyagTSKdhBxLMyWiczHoEJw/0?wx_fmt=gif","哼哼哼哼"));
    mXiaoZhuZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMhDg6hoXGnFvBOyHMZUJ0fIibuFSLvUqZAOxmLzokpLqVjRseYiaPsyqw/0?wx_fmt=gif","哼哼哼哼"));
    mXiaoZhuZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMMS4LqgddKbXiccFZPxyygAibGotGiafG3sx1ohGl9IR44S7ibvxc9XnsTw/0?wx_fmt=gif","哼哼哼哼"));


    //皮卡猫
    mPikaMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_png/t4K8ARPcgicUx3o11sjNzOGh18uQYgbQtYo3ibCVaSDUOcrRHK8uIiaGTElrqOcVQiarwnCApzbEMmSffaDm4yHUwQ/0?wx_fmt=png", "皮卡皮卡喵～"));


    //大屁猫
    mDaPiMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_png/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM4EDdeAibltYABobWcHWssUNSEfwCNkiaXqTBN8sicd14WZwyMNuY3rF0g/0?wx_fmt=png","大屁猫，猫屁大～"));
    mDaPiMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_png/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMiaMY6vEwTg2iaiaR1zicBoibQgVOBYicyXMjZCW5VehITFhxqeeFe81rK4SQ/0?wx_fmt=png","大屁猫，猫屁大～"));
    mDaPiMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_png/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMMWOqcb6PmkHdo82x3iaVM9uhPsyVWoh98ABpJY66ALvX1icibRzTRslrw/0?wx_fmt=png","大屁猫，猫屁大～"));


    //猫猫
    mMaomaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMlt4xs6ZYetAmExqx6nibbfs5ib5C1Kdibm0n1I0KnUq8Iic7L9noSGYiaUA/0?wx_fmt=gif","喵喵喵喵"));
    mMaomaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMNpEbZGStTSdnKF18wKnt3iatl7hhaH1u0jk8WGy2Djx6u0orPbtwvUA/0?wx_fmt=gif","喵喵喵喵"));
    mMaomaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM4thvT4KgcmlyALh12PNWY6jK8yXWWqlMib5OYax2Q9HH8JzVJzn4cqQ/0?wx_fmt=gif","喵喵喵喵"));
    mMaomaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMoTia0pY3yX3X1OcTzv5LLrFH8IJ1vrM7icCzCqfNJTgiaHgBeS2jibK5pg/0?wx_fmt=gif","喵喵喵喵"));
    mMaomaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM8w6xNdd789nhTMtDicd9kiaPKNKJFxmzCKUhD6BUhJNfwCKNZNVxNrzg/0?wx_fmt=gif","喵喵喵喵"));
    mMaomaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM7fqqeA6ysIkbogooPVKAgf0ibib5KhypSdfetPlVvmKlrK1ic6fj6ZgfA/0?wx_fmt=gif","喵喵喵喵"));

    

    //懒猫
    mLanMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMxa856ibdcQM3J1zvl8Qfiak59CPEV9ZMwbrfFuNnoMNnqrUjfHqOtibJQ/0?wx_fmt=gif","呼呼呼"));
    mLanMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMYYoWY3YJuwQb0xdR41txmITANrhK74EK5smDT2TEkRgEtBQBvXYQeg/0?wx_fmt=gif","呼呼呼"));
    mLanMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMiaibuRBnaQfSoCAkAlyrVNndGJVXt2ycqHA99y1EoeKOlLWgNvqZedOw/0?wx_fmt=gif","呼呼呼"));
    mLanMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMtwFPHlTprAWhFiaribnibbkLkJhldqo1y94E5oib1etVM4sAYt8EhoF6rQ/0?wx_fmt=gif","呼呼呼"));
    mLanMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM34oxgPiaphnxs8FCSwvcJ3Zv4GIfJGDCGtdHNq9frIR5aFCsPt0HjIg/0?wx_fmt=gif","呼呼呼"));
    mLanMaoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM8RBY8uJd3JUwp6Gn5yMfPnNJeSEEOFvbjO2icZRyIwMU52aH75zhh4g/0?wx_fmt=gif","呼呼呼"));


    //大馋猪
    mDaChanZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUx3o11sjNzOGh18uQYgbQtLy8CBPoufaNCcYCYnjNyNQLz5pboIxYribQAxgGloqvbGBP5ic4GFONQ/0?wx_fmt=gif","好好吃"));
    mDaChanZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMEh7NVYh1lw4pPmx34wGSaKLeiaJ4XRGjbKAL9xlwibghC75na0kHJ2gA/0?wx_fmt=gif","好好吃"));
    mDaChanZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMA9Xjln6rB1QL3QHMZibB12HDJSRgOJlmr2K9ZIeuIUQpVC6M0qmWEHQ/0?wx_fmt=gif","好好吃"));
    mDaChanZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMiaMg8NI8KHmWUxDiawpp7hy17G4lhpBmIw8utgnC23WVboI5s9ZCBAlA/0?wx_fmt=gif","好好吃"));
    mDaChanZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMkcIzYtWZHyXBBok4fCApeTNI8xTP42ome5iaPfGgAVVEUhTt0GAKNPQ/0?wx_fmt=gif","好好吃"));

    //懒猪
    mDaLanZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMkE17TObfoBOibP6Xd4vbQqJL3cmu1tS30RnD375Bxiavyibh3PCZuuBaQ/0?wx_fmt=gif", "呼呼呼"));
    mDaLanZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMhedS5ZCVnG6yZk5AZibyrq9uZcpicY3VNEGqibaUKOXnpFZu9RSI6HpbA/0?wx_fmt=gif", "呼呼呼"));
    mDaLanZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMgU8DS1zF6ibhMPLuBTibJMVgSAVCMBVibk2IlvkjYfu063icL5xBRTDXEA/0?wx_fmt=gif", "呼呼呼"));
    mDaLanZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMXF1C7cRneKRb89USm1fMRODveh7k77tSicOrf19P78ibH5UgKSp3Qa4w/0?wx_fmt=gif", "呼呼呼"));

    //摸摸
    mMomoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMiaD0XRKRPuicIBpzlpwEaic0915eTT5AG0PSTKFGibA7Fxhsee0ASibN1Qg/0?wx_fmt=gif", "好舒服啊"));
    mMomoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_png/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMCAeqUzysrNOQVTSOctTQl9dQF60LWJs8832CK2QQv1Mj88vpljNH2Q/0?wx_fmt=gif", "好舒服啊"));
    mMomoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_png/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMmibuGAIQhYvl7kNPpG1DNnlibt7CXeWmaUiaDkue1WPFIzqJuEwSn6ictg/0?wx_fmt=gif", "好舒服啊"));
    mMomoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMdyhatP9fh0Dn8fY9AXMXJer5UrLAJfN0axYRjL2Bn9r4OeZz77qX6g/0?wx_fmt=gif", "好舒服啊"));
    mMomoMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMIvor6emUXdqNtMJqyNCWeh0nibaicZdfKtDjichKFaXVPeI6XtRHJfZzg/0?wx_fmt=gif", "好舒服啊"));

    //亲亲
    mQinQinMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMf97ejwz1UtsQkj3QuJ0E45rX9FUTXJ9MsjoUKPxDV3pvPNQaBaaE3A/0?wx_fmt=gif", "mua~"));
    mQinQinMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMAxibhN0ibnXvtl1riamu46ZctnGQ5uWsGy7kEtPxWScP4UvP8F7jBf9JQ/0?wx_fmt=gif", "mua~"));
    mQinQinMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMHRduoYGRicqnkTW1iaibQbia0Wp1iaTVKKRsaG7kGcIcuUQfKPZIgPomYHw/0?wx_fmt=gif", "mua~"));
    mQinQinMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMq9alicoVN4ZnpFYxfF0ibfeQRicicQGTzJ26eY5XuiaMpntia5RQkRjL2SHA/0?wx_fmt=gif", "mua~"));


  }


  /**
   * 
   * {
      "ToUserName": "用户OPENID",
      "FromUserName": "公众号/小程序原始ID",
      "CreateTime": "发送时间", // 整型，例如：1648014186
      "MsgType": "news",
      "ArticleCount": 2, // 图文消息个数；当用户发送文本、图片、语音、视频、图文、地理位置这六种消息时，开发者只能回复1条图文消息；其余场景最多可回复8条图文消息
      "Articles": [{
        "Title": "图文标题",
        "Description": "图文描述",
        "PicUrl": "图片链接", // 支持JPG、PNG格式，较好的效果为大图360*200，小图200*200
        "Url":"点击图文消息跳转链接"
      },{
        "Title": "图文标题",
        "Description": "图文描述",
        "PicUrl": "图片链接", // 支持JPG、PNG格式，较好的效果为大图360*200，小图200*200
        "Url":"点击图文消息跳转链接"
      }]
    }
   */
  private String getImageReplyExtends(String from,String to,String content) {
    ImageReplyBean imageReplyBean = null;
    switch(content) {
      case DA_CHAN_ZHU:
      case CHAN_ZHU:
        imageReplyBean = getRandomImageReplyBean(mDaChanZhuMediaIdList);
        break;
      case DA_LAN_ZHU:
      case LAN_ZHU:
        imageReplyBean = getRandomImageReplyBean(mDaLanZhuMediaIdList);      
        break;
      case ZHU_ZHU:
      case XIAO_ZHU_ZHU:
        imageReplyBean = getRandomImageReplyBean(mXiaoZhuZhuMediaIdList);      
        break;     
      case PI_KA_MAO:
        imageReplyBean = getRandomImageReplyBean(mPikaMaoMediaIdList);      
        break;   
      case MAO_MAO:
        imageReplyBean = getRandomImageReplyBean(mMaomaoMediaIdList);      
        break; 
      case DA_PI_MAO:
        imageReplyBean = getRandomImageReplyBean(mDaPiMaoMediaIdList);      
        break;   
      case LAN_MAO:
      case XIAO_LAN_MAO:
        imageReplyBean = getRandomImageReplyBean(mLanMaoMediaIdList);      
        break;  
      case CHAN_MAO:
      case XIAO_CHAN_MAO:
          imageReplyBean = getRandomImageReplyBean(mChanMaoMediaIdList);      
          break;   
      case MO_MO:
          imageReplyBean = getRandomImageReplyBean(mMomoMediaIdList);      
          break;         
      case QIN_QIN:
          imageReplyBean = getRandomImageReplyBean(mQinQinMediaIdList);      
          break;                        
      default:
        return "";                  
    }
    if(null == imageReplyBean) return "";

    JSONObject reply = new JSONObject();
    reply.put(FROM_USER_NAME, to);
    reply.put(TO_USER_NAME, from);
    reply.put(TIME, System.currentTimeMillis() / 1000);
    reply.put(TYPE, "news");
    JSONArray imageArr = new JSONArray();
    JSONObject image = new JSONObject();
    image.put("Title", content);
    image.put("Description", imageReplyBean.text);
    image.put("PicUrl", imageReplyBean.url);
   
    image.put("Url", imageReplyBean.url);
    imageArr.put(image);
    reply.put("ArticleCount", 1);
    reply.put("Articles", imageArr);
    logger.info("reply " + reply);
    return reply.toString();
  }

  private ImageReplyBean getRandomImageReplyBean(List<ImageReplyBean> list) {
    Random random = new Random();
    int index = random.nextInt(list.size());
    return list.get(index);
  }
}