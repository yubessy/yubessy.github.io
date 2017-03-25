<!-- $theme: gaia -->

## Distributed Deep Newral Network

##### 0x64物語 第08夜 "Network"

##### Shotaro Tanaka (@yubessy)

---

### Excuse

* ニューラルネットとネットワークをかけた
  と思わせて
* 分散機械学習のネットワークアーキテクチャを
  真面目に語るつもりだったけど
* 書いてみたらあまりネットワークの話がなかった

~~なんかごめん~~

---

### Why Distributed?

* データ量の増加
  * テキスト < 画像 < 動画
* 計算量の増加
  * いわゆるディープラーニング
* 1つのコンピュータの処理の頭打ち

-> 時代は分散コンピューティング

---

### Why Network?

* 分散コンピューティング
  * = 複数のノードがひとつの目的の計算を行う
* たいていノード間の通信がボトルネック
* 何を・どう分散させるかを考える必要

-> たぶんネットワークアーキテクチャが重要

---

### 分散処理の捉え方

* **分散対象**: 何を分けるか
  * そもそも分割できるのか
  * 負荷を均等できるか
* **ノード間通信**: どうつなぐか
  * クライアント・サーバ型 / ワーカー間通信
  * 同期 / 非同期
  * スループット / レイテンシ
  * プロトコル

---

### 深層ニューラルネット (DNN)

* たくさんのニューロンからなるグラフ
* バック・プロパゲーション
  * 入力値に対する、グラフの出力値と正解値の
    差分を求める
  * 差分が小さくなるよう、出力側から順に
    各ニューロンのパラメータを調整
* SGD
  * データ点を１個与えるごとにパラメータを更新

---



---

### DNNと分散処理

* DNNは実は分散処理に向いている
  * モデル並列化 -> グラフを複数の部分に分割
  * データ並列化 -> SGDを並列化
* "Large Scale Distributed Deep Networks" 
  Dean, et al. 2012.
  * Google の論文
  * DNNのモデル / データの分散について解説
  * ~~またお前か~~

---

### DistBelief: モデル並列化

* 分散対象: ニューラルネットのグラフ
    * ニューラルネットを部分グラフに分割
    * 各部分グラフを別のマシンで動かす
* ノード間通信: ワーカー間の直接通信
    * ニューラルネットの結合部が通信

---

### DownpourSGD: データの分散

* 分散対象: 学習データの集合
  * データをチャンクに分割
  * 各チャンクを別のマシンに処理させる
* ノード間通信: パラメータ・サーバ方式
  * 各ニューロンのパラメータ保持するサーバ
  * ワーカーノードは一定量の学習を終えるごとに
    非同期通信によりパラメータを更新

---

### これG社以外扱えるの？

##### と思ったあなたへ

---

### Distributed TensorFlow

* ここまで説明した分散処理機能が
  実はすでに TensorFlow に組み込まれている
* 🔎 Distributed TensorFlow

TODO: 図

---

### クラスタ定義

```python
cluster = tf.train.ClusterSpec({
    "worker": [  # データ分散のためのワーカー
        "worker0.example.com:2222", 
        "worker1.example.com:2222",
        "worker2.example.com:2222"
    ],
    "ps": [  # モデル分散のためのパラメータサーバ
        "ps0.example.com:2222",
        "ps1.example.com:2222"
    ]})
```

---

### モデル並列化

* ニューラルネットの各層を複数のPSに分散

```python
with tf.device("/job:ps/task:0"):
  weights_1 = tf.Variable(...)
  biases_1 = tf.Variable(...)
 
with tf.device("/job:ps/task:1"):
  weights_2 = tf.Variable(...)
  biases_2 = tf.Variable(...)
```

---

### データ並列化

* 各ワーカーにデータを分散させ、モデルのグラフを定義

```python
dev = tf.train.replica_device_setter(
    worker_device="/job:worker/task:%d" % FLAGS.task_index,
    cluster=cluster)

with tf.device(dev):
    input, labels = ...
    layer_1 = tf.nn.relu(tf.matmul(input, weights_1) + biases_1)
    logits = tf.nn.relu(tf.matmul(layer_1, weights_2) + biases_2)
    train_op = ...
```

---

### 学習

* 各ワーカで学習を実行

```
with tf.train.MonitoredTrainingSession(
    master=server.target, is_chief=(FLAGS.task_index == 0)) as sess:
    while not sess.should_stop():
        mon_sess.run(train_op)
```

---   

### まとめ

---

### 参考

* [Large Scale Distributed Deep Networks](http://www.cs.toronto.edu/~ranzato/publications/DistBeliefNIPS2012_withAppendix.pdf)
* [Distributed TensorFlow](https://www.tensorflow.org/deploy/distributed)
* [Distributed TensorFlowを試してみる](http://qiita.com/ashitani/items/2e48729e78a9f77f9790)
* [Distributed TensorFlowの話](http://qiita.com/kazunori279/items/981a8a2a44f5d1172856)
