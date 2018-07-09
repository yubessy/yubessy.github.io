autoscale: true
header: alignment(left)
text: text-scale(0.8), line-height(0.5)

[.header: alignment(center)]

# CircleCI 2.0

---

# Agenda

- 基礎編
  - CI, CD とは
  - Circle CI の構成要素
  - 基本的な Job の使い方
- 発展編
  - 高度な Job, Step の使い方
  - 高度な Workflow の使い方

---

# 目的

目指すこと

- CI, CD と CircleCI の基本的な概念を理解する
- CircleCI 2.0 の Job, Step を使いこなせるようになる

扱わないこと

- CircleCI 1.0
- 個別の言語・フレームワーク特有の話

---

[.header: alignment(center)]

## 基礎編

---

# Agenda

- 基礎編
  - **CI, CD とは**
  - Circle CI の構成要素
  - 基本的な Job の使い方
- 発展編

---

# CI, CD とは？

ソフトウェア開発における、ビルド・テスト・リリースのような

一連の定型処理を自動化により継続的に繰り返せるようにすること

- CI = Continuous Integration = 継続的インテグレーション
  - リリースに至るまでのビルド・テストなどを主に扱う
- CD = Continuous Delivery = 継続的デリバリ
  - リリース（デプロイ・マイグレーションなど）を主に扱う

---

# CI, CD があると何が嬉しい？

CI, CD がなかった頃

- ビルド: ビルド職人に毎回依存関係の解決から依頼
- テスト: テスターが数百の手順と目視確認を手動で実施
- リリース: インフラエンジニアが本番環境で数十のコマンドを実行

CI, CD をうまくやれば・・・

- ビルド・テスト: コードを push すると全部自動で実行
- リリース: 準備ができたら承認ボタンを押すだけ

---

# CircleCI とは？

マネージドの CI, CD サービス

特徴:

- Github の branch, tag の push を起点に処理を実行
- 1.0 -> 2.0 で大幅なアップデートがありほとんど別物
  - 原則としてコンテナを利用 (VMも使えないことはない)
  - 処理は言語やフレームワークに依存せず自分で書く

---

# Agenda

- 基礎編
  - CI, CD とは
  - **Circle CI の構成要素**
  - 基本的な Job の使い方
- 発展編

---

# Workflow, Job, Step

一連の処理は Workflow, Job, Step の順に階層化される

```yaml
workflows:
  main:
    jobs:
      - test: ...
      - deploy: ...
jobs:
  test:
    docker:
      - image: circleci/python:3
    steps:
      - checkout
      - run: python -m unittest
      - ...
  deploy: ...
```

---

# Workflow

処理をまとめる最も大きな単位で、ひとつ以上の Job で構成される

```yaml
workflows:
  main:
    jobs:
      - test: ...
      - deploy: ...
```

多くの場合は 1 レポジトリにつき 1 Workflow & 1 Job となる

実は直列・並列・分岐などかなり柔軟な処理ができる (発展編で紹介)

---

# Job

順番に実行される処理をまとめる単位で、ひとつ以上の Step で構成される

```yaml
jobs:
  test:
    docker:
      - image: circleci/python:3
    steps:
      - checkout
      - run: python -m unittest
      - ...
```

処理の実行環境は基本的に Docker コンテナで定義する

---

# Step

処理の最も小さな単位で、次のようないくつかの種類がある (発展編でも紹介)

```yaml
# レポジトリからコードを取得
- checkout
# コマンドを実行
- run: <command>
# コマンドを実行 (このように書くこともできる)
- run:
    name: <name>
    command: <command>
# コマンドを実行 (run とほぼ同じだが並列実行されない)
- deploy: <command>
```

---

# Agenda

- 基礎編
  - CI, CD とは
  - Circle CI の構成要素
  - **基本的な Job の使い方**
- 発展編

---

# Job を書き始める前に

「何が」自動化に必要かを調べる

- 必要なライブラリはどうインストールする？
- DBマイグレーションが別で管理されているならテストDBをどう立てる？
- コード管理できないクレデンシャルはどうやって渡す？

「本当に」自動化すべきかを考える

- リリースまで自動化しないといけないのか？ビルド・テストで十分か？
- プルリクマージで自動リリースされて大丈夫か？失敗時に切り戻せるか？
- 本番環境にSSHする必要があるか？クラウド標準のデプロイサービスを使えないか？

---

# はじめの一歩

最初は 1 Job で Workflow は省略してもよい (これでも Job は実行される)

```yaml
version: 2
jobs:
  build:
    docker:
      - image: circleci/ruby:2.4.1-jessie
    steps:
      - checkout
      - run: echo "A first hello"
```

---

# Docker イメージを選ぶ

実行環境として使う Docker イメージはよく考えて選ぶ

各 Step のコマンドはひとつめのコンテナで実行されることに注意

```yaml
docker:
  # 公式の `circleci/ruby` や `circleci/mysql` などをなるべく使う
  - image: circleci/python:3.6.2 # 本番環境とバージョンを揃える
  - image: circleci/mysql:5.7    # (同上)
```

---

# Step は適切に分ける

Step は名前をつけやすいひとまとまりの処理

```yaml
- run:
    name: setup env
    command: |
      python -m venv .venv
      source .venv/bin/activate
      pip install -r requirements.txt

- run:
    name: check code statically
    command: |
      source .venv/bin/activate
      pep8 .
      pyflakes .

- run:
    name: test
    command: |
      source .venv/bin/activate
      unittest .  # test
```

---

# Step は適切に分ける

```yaml
# 良くない例: 全部まとめる
- run:
    name: CI
    command: |
      python -m venv .venv
      source .venv/bin/activate
      pip install -r requirements.txt
      pep8 .
      pyflakes .
      unittest .

# 良くない例: 1コマンドずつ全部分ける
- run: pip install -r requirements.txt
- run: pep8 .
- run: pyflakes .
- run: unittest .
```

---

# Cache を適宜利用する

ライブラリのインストールパスなどをキャッシュしておくと便利 (発展編で解説)

```yaml
- restore_cache: # キャッシュあればそこからディレクトリの内容を復元
    key: requirements-{{ checksum "requirements.txt" }}

- run: # 実行されるがディレクトリが復元されていれば実質的に何も起きない
    name: setup venv and pip install
    command: |
      python -m venv .venv
      source .venv/bin/activate
      pip install -r requirements.txt

- save_cache: # ディレクトリの内容をキャッシュとして保存
    key: requirements-{{ checksum "requirements.txt" }}
    paths:
      - .venv
```

---

# 基礎編はここまで

身近な `circle.yml` を読んでみよう
