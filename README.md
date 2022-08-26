# wxcloudrun-springboot
[![GitHub license](https://img.shields.io/github/license/WeixinCloud/wxcloudrun-express)](https://github.com/WeixinCloud/wxcloudrun-express)
![GitHub package.json dependency version (prod)](https://img.shields.io/badge/maven-3.6.0-green)
![GitHub package.json dependency version (prod)](https://img.shields.io/badge/jdk-11-green)

微信云托管 Java Springboot 框架模版，实现简单的计数器读写接口，使用云托管 MySQL 读写、记录计数值。

![](https://qcloudimg.tencent-cloud.cn/raw/be22992d297d1b9a1a5365e606276781.png)


## 快速开始
前往 [微信云托管快速开始页面](https://developers.weixin.qq.com/miniprogram/dev/wxcloudrun/src/basic/guide.html)，选择相应语言的模板，根据引导完成部署。

## 本地调试
下载代码在本地调试，请参考[微信云托管本地调试指南](https://developers.weixin.qq.com/miniprogram/dev/wxcloudrun/src/guide/debug/)。

## 实时开发
代码变动时，不需要重新构建和启动容器，即可查看变动后的效果。请参考[微信云托管实时开发指南](https://developers.weixin.qq.com/miniprogram/dev/wxcloudrun/src/guide/debug/dev.html)

## Dockerfile最佳实践
请参考[如何提高项目构建效率](https://developers.weixin.qq.com/miniprogram/dev/wxcloudrun/src/scene/build/speed.html)

## 目录结构说明
~~~
.
├── Dockerfile                      Dockerfile 文件
├── LICENSE                         LICENSE 文件
├── README.md                       README 文件
├── container.config.json           模板部署「服务设置」初始化配置（二开请忽略）
├── mvnw                            mvnw 文件，处理mevan版本兼容问题
├── mvnw.cmd                        mvnw.cmd 文件，处理mevan版本兼容问题
├── pom.xml                         pom.xml文件
├── settings.xml                    maven 配置文件
├── springboot-cloudbaserun.iml     项目配置文件
└── src                             源码目录
    └── main                        源码主目录
        ├── java                    业务逻辑目录
        └── resources               资源文件目录
~~~


## 服务 API 文档

### `GET /api/count`

获取当前计数

#### 请求参数

无

#### 响应结果

- `code`：错误码
- `data`：当前计数值

##### 响应结果示例

```json
{
  "code": 0,
  "data": 42
}
```

#### 调用示例

```
curl https://<云托管服务域名>/api/count
```



### `POST /api/count`

更新计数，自增或者清零

#### 请求参数

- `action`：`string` 类型，枚举值
  - 等于 `"inc"` 时，表示计数加一
  - 等于 `"clear"` 时，表示计数重置（清零）

##### 请求参数示例

```
{
  "action": "inc"
}
```

#### 响应结果

- `code`：错误码
- `data`：当前计数值

##### 响应结果示例

```json
{
  "code": 0,
  "data": 42
}
```

#### 调用示例

```
curl -X POST -H 'content-type: application/json' -d '{"action": "inc"}' https://<云托管服务域名>/api/count
```

## 使用注意
如果不是通过微信云托管控制台部署模板代码，而是自行复制/下载模板代码后，手动新建一个服务并部署，需要在「服务设置」中补全以下环境变量，才可正常使用，否则会引发无法连接数据库，进而导致部署失败。
- MYSQL_ADDRESS
- MYSQL_PASSWORD
- MYSQL_USERNAME
以上三个变量的值请按实际情况填写。如果使用云托管内MySQL，可以在控制台MySQL页面获取相关信息。


## License

[MIT](./LICENSE)

git commit -m 'update' && git push -u origin master

猫猫id:oTggI6ImplfCvRqh_xod4Z89QRJg


wx1857831b5e6dc0ce

424b911bb57330e3b7c41e81761e176e


素材库
皮卡猫
nuHjraOZCtB07SLGydqa-Y8wQTmgaA4KCBxl00fiwk_W-oP-Qh9nA0cu5uEMaclb http://mmbiz.qpic.cn/sz_mmbiz_png/t4K8ARPcgicUx3o11sjNzOGh18uQYgbQtYo3ibCVaSDUOcrRHK8uIiaGTElrqOcVQiarwnCApzbEMmSffaDm4yHUwQ/0?wx_fmt=png

小猪猪
nuHjraOZCtB07SLGydqa-ZE8OepeeUPokTbm76jZTX-oc6WunG0ortPaoAEvPsKk　http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMogKJ5AsTJDumicLib3ddRJSywhfuMlvDHEGlDk9YoU8qVXsf7RzH5ia5w/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-d7yC4FY2Jf-DwZkF01WY3cTZTa1hhYS4jA259CsqOlA　http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMLSoiaYia2ZfC6KlfIDZVicGk2QBrLfewj4NO7JWVkX9bP5oqcXIwe2SZA/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-eb99a_kOvzqHhgE2YmoBb6c3FKRVrCt6Rib5JBJfAtT　http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMv3ravQWtls7iceHqibhb43wYzwiabbic6JZ7oyagTSKdhBxLMyWiczHoEJw/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-YnqowgQnuZMAnwIgzxdKUA5KAJAz4ixh3ZAjgzp7t8B　http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMhDg6hoXGnFvBOyHMZUJ0fIibuFSLvUqZAOxmLzokpLqVjRseYiaPsyqw/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-S0P842Zben7D5A4RBPRpgotGyqq3ArtTlh5lF8ayOjd　http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMMS4LqgddKbXiccFZPxyygAibGotGiafG3sx1ohGl9IR44S7ibvxc9XnsTw/0?wx_fmt=gif

大屁猫　
nuHjraOZCtB07SLGydqa-VeYjdbTlO16-5aq64HqEIJFkfXYeXiE8aWppRtXeJL8 http://mmbiz.qpic.cn/sz_mmbiz_png/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM4EDdeAibltYABobWcHWssUNSEfwCNkiaXqTBN8sicd14WZwyMNuY3rF0g/0?wx_fmt=png
nuHjraOZCtB07SLGydqa-TlWjl-rbELazIedG2LgHndL_9TIsHX_SOdN_GGFtvTk http://mmbiz.qpic.cn/sz_mmbiz_png/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMiaMY6vEwTg2iaiaR1zicBoibQgVOBYicyXMjZCW5VehITFhxqeeFe81rK4SQ/0?wx_fmt=png
nuHjraOZCtB07SLGydqa-cc7x0yTjZKtalveWpIi3NLw3y5xJP4rIo4sObqFMRKe http://mmbiz.qpic.cn/sz_mmbiz_png/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMMWOqcb6PmkHdo82x3iaVM9uhPsyVWoh98ABpJY66ALvX1icibRzTRslrw/0?wx_fmt=png

馋猫　
nuHjraOZCtB07SLGydqa-ZxPkgqek-CgvwjSrK00aYPo-lPcdi_DggT91XmglG2B http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMV7Yibh9z98yr7nkwaNKgzYS9xA6mmB4HnTADAJk0JDtGicprJHY4yxPQ/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-UAQEStOts8dDsWD7w8vMrM0cUb1zh5Jp9wOlzrRUXrZ http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMWdSFF0rbTnxWwRgP4HwmsW3hK3hkBvNayJibE6GGW2zbpcSAgMic319g/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-XVYKasM8V7k4Rmu9MJYuLjC8cTX4NHh66WPcTct8BEB http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMwVLoSOKB8QxSIsTIe3lQLFQxk3qNRNxKzN6sr7S1iaCBicggd2icHvmfA/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-SCN1f5JRUhaLhuLTnnXptFCZg2bIaWb6bOJbqnReZbq http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMROpOMkqibJBvtqq156D2nazUk4WFz74Zo64icwFq2BBjTCIHuTQONClQ/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-WQ8Jr3-mwyDJaYILXgnjN6P_lFIQuOOzJ_P4p0dGbA5 http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMNG75HeVIqj9sbtTY5r340HusTBTCnLAGSkz7aak8N07kGqf7gtcgNw/0?wx_fmt=gif

猫猫
nuHjraOZCtB07SLGydqa-UV-_VZ5raTb0dZxplbkI_rH3BWA19OLiE8CsWMa-uZa　http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMlt4xs6ZYetAmExqx6nibbfs5ib5C1Kdibm0n1I0KnUq8Iic7L9noSGYiaUA/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-SV3i-8KwPvktjRvneaG36ooD41BL1DCCKK_D8qhQlZ2　http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMNpEbZGStTSdnKF18wKnt3iatl7hhaH1u0jk8WGy2Djx6u0orPbtwvUA/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-QIKrhbVB20UFU4bPvXVW9_9DU7JtvpPFngZ7lly6QRM　http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM4thvT4KgcmlyALh12PNWY6jK8yXWWqlMib5OYax2Q9HH8JzVJzn4cqQ/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-QfxJ57vTVVKE97NTmkq4Fr4B6L80SeXLbxe8818O4p4　http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMoTia0pY3yX3X1OcTzv5LLrFH8IJ1vrM7icCzCqfNJTgiaHgBeS2jibK5pg/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-WM71TO_OFiQ_2mZGhQ0sEOjrLDLNoAiiD9jiEoCiysf　http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM8w6xNdd789nhTMtDicd9kiaPKNKJFxmzCKUhD6BUhJNfwCKNZNVxNrzg/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-S8ipcIygKYrWy06kcsi4q3YdQbzu1DU_ZWb2783Qg7s  http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM7fqqeA6ysIkbogooPVKAgf0ibib5KhypSdfetPlVvmKlrK1ic6fj6ZgfA/0?wx_fmt=gif

懒猫
nuHjraOZCtB07SLGydqa-RnYc0bv__qsLwhHCYUja29AqsTyRmDUyL6QOR8uzRMs http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMxa856ibdcQM3J1zvl8Qfiak59CPEV9ZMwbrfFuNnoMNnqrUjfHqOtibJQ/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-Va478y29Rcfi4CdtCxEWv8jz_cuzQtG02PGp_cU22c_ http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMYYoWY3YJuwQb0xdR41txmITANrhK74EK5smDT2TEkRgEtBQBvXYQeg/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-ZU5GOpScqpTjbmlhh0aLVRk0CB4p_J47NGAyX4-mGjm http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMiaibuRBnaQfSoCAkAlyrVNndGJVXt2ycqHA99y1EoeKOlLWgNvqZedOw/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-Sn5JBiO2klorpbzIOwl4uTY0J0a_uPwUUeaavdyCkLt http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMtwFPHlTprAWhFiaribnibbkLkJhldqo1y94E5oib1etVM4sAYt8EhoF6rQ/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-c_PDs2CAGGAtbXPgi_MCGrFGLEB9KY29Vy2_8TFR8XJ http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM34oxgPiaphnxs8FCSwvcJ3Zv4GIfJGDCGtdHNq9frIR5aFCsPt0HjIg/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-XncqCj1-xvDumYoAvlGDs52wPvwb85ZAocILge6arag http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuM8RBY8uJd3JUwp6Gn5yMfPnNJeSEEOFvbjO2icZRyIwMU52aH75zhh4g/0?wx_fmt=gif


大馋猪
nuHjraOZCtB07SLGydqa-b9xjvTA7VIdgY7uu6XOKbtlZ37hiZDZrpAR7r1UzUgs http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUx3o11sjNzOGh18uQYgbQtLy8CBPoufaNCcYCYnjNyNQLz5pboIxYribQAxgGloqvbGBP5ic4GFONQ/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-YSI3MJucnT5GOmmQzz6EHBB1gU354e4u10P-7XgLJom http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMEh7NVYh1lw4pPmx34wGSaKLeiaJ4XRGjbKAL9xlwibghC75na0kHJ2gA/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-WU8SZ5pp1pBOZBZlRvx7yA2JGiuRQSbopWtSj7fQHe6 http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMA9Xjln6rB1QL3QHMZibB12HDJSRgOJlmr2K9ZIeuIUQpVC6M0qmWEHQ/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-QccOVSePqAvhABwwd1iDS7ri8wzIvfc4jHeRVaTLn-_ http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMiaMg8NI8KHmWUxDiawpp7hy17G4lhpBmIw8utgnC23WVboI5s9ZCBAlA/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-Tfyg9sRXRQ-325y6W-v07wpKgE9IUh7knXkGK2B4mvh http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMkcIzYtWZHyXBBok4fCApeTNI8xTP42ome5iaPfGgAVVEUhTt0GAKNPQ/0?wx_fmt=gif

懒猪
nuHjraOZCtB07SLGydqa-ezS05JJrBlMNLEMWT7lQNjCuzW-WRu-Iy8ZtRoxtcUz　http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMkE17TObfoBOibP6Xd4vbQqJL3cmu1tS30RnD375Bxiavyibh3PCZuuBaQ/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-c3-6mtW2yOl034tr1c_GfpPfzFYF7F-ORDtA_GMYVDu http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMhedS5ZCVnG6yZk5AZibyrq9uZcpicY3VNEGqibaUKOXnpFZu9RSI6HpbA/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-RPfDGPl8ix2zsgxTiz9edd0VGsz_FChVKtSZ_eR88pr http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMgU8DS1zF6ibhMPLuBTibJMVgSAVCMBVibk2IlvkjYfu063icL5xBRTDXEA/0?wx_fmt=gif
nuHjraOZCtB07SLGydqa-aye3x2MRmHE3Ivxfl8hEnxvaE3FJhWMVXqVHj4aGWju http://mmbiz.qpic.cn/sz_mmbiz_gif/t4K8ARPcgicUtbQ8mHFhM5uqyPjv0PZuMXF1C7cRneKRb89USm1fMRODveh7k77tSicOrf19P78ibH5UgKSp3Qa4w/0?wx_fmt=gif
