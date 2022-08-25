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
  private static final List<String> mPikaMaoMediaIdList = new ArrayList<>();
  private static final List<String> mDaPiMaoMediaIdList = new ArrayList<>();
  private static final List<String> mXiaoMaomiMediaIdList = new ArrayList<>();
  private static final List<String> mLanMaoMediaIdList = new ArrayList<>();
  private static final List<ImageReplyBean> mDaChanZhuMediaIdList = new ArrayList<>();

  private static final String DA_PI_MAO = "大屁猫";
  private static final String CHAN_MAO = "馋猫";
  private static final String XIAO_CHAN_MAO = "小馋猫";
  private static final String PI_KA_MAO = "皮卡猫";
  private static final String XIAO_LAN_MAO = "小懒猫";
  private static final String LAN_MAO = "懒猫";
  private static final String XIAO_ZHU_ZHU = "小猪猪";
  private static final String XIAO_MAO_MI = "小猫咪";
  private static final String DA_CHAN_ZHU = "大馋猪";

  static {
    mImageKeywordList.add(DA_PI_MAO);
    mImageKeywordList.add(CHAN_MAO);
    mImageKeywordList.add(XIAO_CHAN_MAO);
    mImageKeywordList.add(PI_KA_MAO);
    mImageKeywordList.add(XIAO_LAN_MAO);
    mImageKeywordList.add(LAN_MAO);    
    mImageKeywordList.add(XIAO_ZHU_ZHU);  
    mImageKeywordList.add(XIAO_MAO_MI);    

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

    //皮卡猫
    mPikaMaoMediaIdList.add("Uqr_FN07KQF_j-eLL1GCno8Ifulw8wybn1ZFts0A-ofOuXirx-gon5F7izi-BywM");


    //大屁猫
    mDaPiMaoMediaIdList.add("og9uZrJ4VLOt3kLhu_AuIDgU4_HTAnlBVDmIJXiXtaiEDlLrt7ZkpXnxN6l432YF");

    //小猫咪
    mXiaoMaomiMediaIdList.add("Fqbf5z1cZh_UYBc_M1YfPnQe8yQo5lJ5ismgXy8OsQKa4sEWVpPo0NPFcQLF_J-E");
    mXiaoMaomiMediaIdList.add("odIJHwiJ_nC4Sfu-aEU_k6ROzq-WulBqxfXR8vswJILz3a8lHhj5y6tSAxqkC05M");
    mXiaoMaomiMediaIdList.add("odIJHwiJ_nC4Sfu-aEU_k7bCf9vMU-9D_gUPjLHL0sH19-PxVMoYRp8YC4jynrXo");
    mXiaoMaomiMediaIdList.add("odIJHwiJ_nC4Sfu-aEU_kx82fCOq9hIwogPS4-oRQboYzRYZu6z5JXqSJ_fYzCX");
    mXiaoMaomiMediaIdList.add("odIJHwiJ_nC4Sfu-aEU_k9uAGH2gA6MrGWyqXWjHlY6726k4rECZ01vtSeWet217");

    //懒猫
    mLanMaoMediaIdList.add("odIJHwiJ_nC4Sfu-aEU_k3Y24EQudy0AyxVIMaGAqJigDCAtGHBXCX48nIHKUtt_");
    mLanMaoMediaIdList.add("odIJHwiJ_nC4Sfu-aEU_k-cuLFmTAxAXMwAX57HVOcFXwOpRigqwesQVfvnQkMWC");
    mLanMaoMediaIdList.add("odIJHwiJ_nC4Sfu-aEU_kxROYlrxfMxEsbne9Udh4XcGSodXZhhI0AHSkxwG4hN_");

    //大馋猪
    mDaChanZhuMediaIdList.add(new ImageReplyBean("http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUx3o11sjNzOGh18uQYgbQtLy8CBPoufaNCcYCYnjNyNQLz5pboIxYribQAxgGloqvbGBP5ic4GFONQ/0?wx_fmt=gif","好好吃"));
  }

  private String getImageReply(String from,String to,String content) {
    String mediaId = "";
    switch(content) {
      case DA_PI_MAO:
      mediaId = getRandomMediaId(mDaPiMaoMediaIdList);
        break;
      case LAN_MAO:
      case XIAO_LAN_MAO:
        mediaId = getRandomMediaId(mLanMaoMediaIdList);
        break;
      case CHAN_MAO:
      case XIAO_CHAN_MAO:
        mediaId = getRandomMediaId(mChanMaoMediaIdList);
        break;
      case PI_KA_MAO:
        mediaId = getRandomMediaId(mPikaMaoMediaIdList);     
        break;
      case XIAO_ZHU_ZHU :
        mediaId = getRandomMediaId(mXiaoZhuZhuMediaIdList);
        break;
      case XIAO_MAO_MI :
        mediaId = getRandomMediaId(mXiaoMaomiMediaIdList);
        break;
      case DA_CHAN_ZHU:
        return getImageReplyExtends(from,to,content);
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
      imageReplyBean = getRandomImageReplyBean(mDaChanZhuMediaIdList);
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
    image.put("Url", "");
    imageArr.put(image);
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