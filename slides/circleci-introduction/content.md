autoscale: true
header: alignment(left)
text: text-scale(0.8), line-height(0.5)

[.header: alignment(center)]

# Introduction to CircleCI

---

# Agenda

- 基礎編
  - CI, CD とは
  - Circle CI の構成要素
  - 基本的な Job, Step の使い方
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
  - 基本的な Job, Step の使い方
- 発展編

---

# CI, CD とは？

ソフトウェア開発における、ビルド・テスト・リリースのような

一連の定型処理を自動化により継続的に繰り返せるようにすること

- CI = Continuous Integration = 継続的インテグレーション
  - リリースに至るまでのビルド・テストなどを主に扱う
- CD = Continuous Delivery = 継続的デリバリ
  - リリースそのもの（デプロイ・マイグレーションなど）を主に扱う

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

- Github の branch, tag の push を起点に任意の処理を実行
- 1.0 -> 2.0 で大幅なアップデートがありほとんど別物
  - 原則としてコンテナを利用 (VMも使えないことはない)
  - 処理は言語やフレームワークに依存せず自分で書く

---

# Agenda

- 基礎編
  - CI, CD とは
  - **Circle CI の構成要素**
  - 基本的な Job, Step の使い方
- 発展編

---

# Workflow, Job, Step: 処理の階層を成す要素

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
  - **基本的な Job, Step の使い方**
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

# Step を適切なまとまりで区切る

```yaml
# ひとまとまりの処理ごとに Step を作って名前をつける
- run:
    name: 環境構築
    command: |
      python -m venv .venv
      source .venv/bin/activate
      pip install -r requirements.txt
- run:
    name: コードの静的解析
    command: |
      source .venv/bin/activate
      pep8 .
      pyflakes .
- run:
    name: テスト
    command: |
      source .venv/bin/activate
      unittest .  # test
```

---

# Step を適切なまとまりで区切る

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

ライブラリのインストールパスなどをキャッシュしておくと便利 (発展編でも紹介)

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

---

[.header: alignment(center)]

## 発展編

---

# Agenda

- 基礎編
- 発展編
  - **高度な Job, Step の使い方**
  - 高度な Workflow の使い方

---

# Workflow, Job, Step についてのおさらい

- Circle CI の処理は Workflow > Job > Step に階層化される
- Workflow: 1つ以上の Job を直列・並列・選択・分岐して実行
- Job: 1つ以上の Step を直列実行
- Step: 処理の最も小さい単位
  - checkout: コードをレポジトリから取り出す
  - run, deploy: コマンド (シェルスクリプト) を実行
  - save_cache, restore_cache: ディレクトリをキャッシュとして保存・読込

---

# Job: 設定項目一覧

```yaml
jobs:
  build:
    docker: # 使用する Docker イメージ
      - image: circleci/ruby:2.4.1
    environment: # 環境変数
      FOO: bar
    parallelism: 2 # 並列度
    resource_class: medium # CPU, RAM のスペック
    working_directory: /my-app # ワーキングディレクトリ
    branches: # ブランチフィルタ
      only:
        - master
    steps: ...
```

---

# Job: `parallelism` による並列化

1. `parallelism` を `2` 以上にする
2. 並列実行したい処理を `circleci tests` コマンドで並列化する

```yaml
parallelism: 2
steps:
  - run:
      command: |
        circleci tests glob "spec/**/*_spec.rb" > list.txt
        circleci tests split list.txt | xargs bundle exec rspec
```

---

# Job: `circleci tests` の解説

基本的にはファイルのリストの収集と分割をするだけのコマンド

```
$ circleci tests glob "spec/**/*_spec.rb" > list.txt
$ cat list.txt
spec/test1_spec.rb
spec/test2_spec.rb
spec/test3_spec.rb
spec/test4_spec.rb

$ circleci tests split list.txt
spec/test1_spec.rb spec/test2_spec.rb
spec/test3_spec.rb spec/test4_spec.rb
```

---

# Step: `run` の設定項目

```yaml
run:
  name: rspec # Webコンソールに表示されるステップ名
  command: bundle exec rspec # コマンド内容
  shell: bash -lc # コマンドを実行するシェル
  environment: # 環境変数
    FOO: bar
  working_directory: /my-app # ワーキングディレクトリ
  background: false # バックグラウンド実行するか否か
  no_output_timeout: 1m # 一定時間何も出力がなければタイムアウト
  when: # (always, on_success, on_fail) どういう場合に実行するか
```

---

# Step: `save_cache`, `restore_cache`

(再掲) ライブラリのインストールなどの時間を短縮

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

# Step: `store_artifacts`

ビルドの成果物などを保存する (コンパイル言語等で使う)

```yaml
- store_artifacts:
    path: build/results
    destination: build-results
```

---

# Step: `persist_to_workspace`, `attach_workspace`

ある Job の成果物を後続の別の Job で利用する

```yaml
persist_to_workspace:
  root: dist
  paths: '*.tar.gz'
```

```yaml
attach_workspace:
  at: dist
```

**Workflow** で複数の Job を組み合わせる場合に使う

---

# Agenda

- 基礎編
- 発展編
  - 高度な Job, Step の使い方
  - **高度な Workflow の使い方**

---

# Workflow とは？

複数の Job を組み合わせて複雑な処理フローを実現する機能

- 直列実行・並列実行
- 前の Job の成果物を利用
- Git の branch, tag によるフィルタリング
- Manual Approval (人による承認)
- Nightly Scheduling (`git push` によらない定期実行)

---

# Workflow: 複数の Job の定義

```yaml
version: 2
jobs:
  build:
    docker: ...
    steps: ...
  test:
    docker: ...
    steps: ...
workflows:
  version: 2
  build-and-test:
    jobs: # このままだと build と test は並列実行される
      - build
      - test
```

---

# Workflow: Job の直列実行

`requires` で依存する Job を指定することで直列実行できる

```yaml
workflows:
  version: 2
  build-and-test:
    jobs:
      - build
      - test:
          requires: # build が終わってから test を実行
            - build
```

---

# Workflow: 前の Job の成果物を利用

`build` の成果物を `test` で使う = `persist_to_workspace`, `attach_workspace`

```yaml
jobs:
  build:
    steps:
      - persist_to_workspace: # dist/ 以下の *.tar.gz ファイルを保存
          root: dist
          paths: '*.tar.gz'
  test:
    steps:
      - attach_workspace: # 保存された dist/ 以下のファイルを読込
          at: dist
```

---

# Workflow: Git の branch, tag によるフィルタリング

特定の名前の branch や tag の場合だけ Job を実行したい場合

```yaml
workflows:
  version: 2
  test-and-deploy:
    jobs:
      - test
      - deploy:
          requires: # test が終わってから deploy を実行
            - test
          filters:
            branches: # master の場合のみ deploy を実行
              only: /^master$/ # 正規表現
```

---

# Workflow: Manual Approval

人による承認ステップを挟む

```yaml
workflows:
  version: 2
  lint-and-test:
    jobs:
      - lint # lint は毎回実行
      - approve-test: # test に進むことを手動で承認
          type: approval
          requires:
            - lint
      - test: # test は時間がかかるので承認を必要にする
          requires:
            - approve-test
```

---

# Workflow: Nightly Scheduling

特定の Workflow を定期的に実行

```yaml
workflows:
  version: 2
  health:
    triggers:
      - schedule: # 毎日 00:00 に実行
          cron: "0 0 * * *"
    jobs: # 脆弱性のチェックなど
      - check-vulnerability
```

---

# まとめ

Workflow を使うととにかく色々なことができるが...

Workflow 自体の動作は自動テストできないので、複雑なものを作りすぎないこと

参考になる Workflow を探してみよう

---

# 参考になる資料

- [CircleCI2.0のWorkflowを試してみる](https://qiita.com/sawadashota/items/ba89382d563bc90bb5cd)
- [CircleCI 2.0でのスローテスト（テスト遅い）問題対処法を思いつくだけ書き出す](https://qiita.com/terrierscript/items/80dede32cc7935193b70)
- [CircleCI 2.0 をlocalで動かす](https://qiita.com/selmertsx/items/45bd672c2c8ddab1981b)
