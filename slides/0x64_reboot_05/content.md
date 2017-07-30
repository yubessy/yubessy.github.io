class: center, middle

# 僕とchatbotとサーバーレス

#### @yubessy

#### 0x64物語 Reboot #05 "ポストRails"

---

class: center, middle

## 本日のお題: "ポストRails"

---

## 僕とRails

#### Railsのエッセンス

* Ruby
* MVC

---

class: center, middle

# Railsのはなし

# END

---

## webアプリケーションの多様化

#### REST API, SPA

* ビュー(一枚のHTML)がない

#### log gateway

* モデル(RDB)がない

#### -> MVCの限界

---

## chatbot

TODO: 写真

---

## よくあるmessaging platform

* LINE Messaging API
* Facebook Messenger Platform

---

## 忙しいwebアプリケーション

#### webhook

* レスポンスタイム制限

#### 重いビジネスロジック

* 外部APIとの通信 (ChatOps)
* 自然言語処理・機械学習

#### bot起点の通信

* 通知
* 一斉配信

---

## ワーカーによる非同期処理

TODO: 図

---

## ワーカーによる非同期処理

#### webhook receiver

* workerに処理を投げる
* webhookに即レス

#### worker

* やっていく
* 終わったら返信する

---

## chatbotでの難点

#### サーバーの負荷管理

* 負荷傾向の異なる処理
    * receiver: 大量のリクエストを高速にさばく
    * worker: 重い処理
* -> 分散型ジョブキュー等でサーバを別にしたり

#### デバッグのやりにくさ

* 非同期処理
* webhookを受け取るパブリックエンドポイント
    * ローカルデバッグができない

---

## そこで: サーバーレス

TODO: 図

（すでに誰か説明してるはず）

* Lambdaがwebhookを受けてSQSに積む
* workerがSQSをデキューして処理

---

## サーバーレスの利点

#### 負荷分散

* 大量のWebhookも楽にさばける

#### デバッグ

* LambdaはSQSに積んで200返すだけ
    * テスト頑張らなくていい
* workerはサーバーレスでなくてもよい
    * DBコネクションプール等が要るなら普通のサーバーにする
    * SQSを挟むのでパブリックエンドポイントが不要
    * -> **ローカルデバッグができる！！**

---

## つくってみた

* 諸般の事情によりFacebook Messenger
* 諸般の事情によりAzure Functions
* 諸般の事情によりAzure Service Bus

---

class: center, middle

# demo

---

## まとめ

* chatbotとサーバーレスは相性が良さそう
* Azure意外と使える
