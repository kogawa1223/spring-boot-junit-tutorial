# 実装ノート

Java / Spring Boot 4.1 / JUnit 5 を1リポジトリで学んだ内容と、詰まった点の記録。

## 進捗

| トピック | 内容 | 状態 |
|---|---|---|
| Spring ドメイン | record / Service / Controller / 例外ハンドラ | 完了 |
| Bean Validation | `@NotBlank`/`@Size` + `@Valid` → 400 | 完了 |
| 例外処理 | `@RestControllerAdvice` + ProblemDetail | 完了 |
| JUnit assertions | `assertAll`, `assertThrows` | 完了 |
| JUnit lifecycle | `@BeforeEach`/`@AfterAll`, `@Nested` | 完了 |
| JUnit parametrized | `@ValueSource`/`@CsvSource`/`@MethodSource` | 完了 |
| JUnit assumptions | `assumeTrue`, `@Disabled`, `@EnabledOnOs` | 完了 |
| スライステスト | `@WebMvcTest` + `@MockitoBean` + MockMvc | 完了 |
| 統合テスト | `@SpringBootTest` + `@AutoConfigureMockMvc` | 完了 |

**33 tests / 1 skipped(@Disabled) / 0 failures.** 実サーバ起動で全エンドポイントも手動検証済み。

## 要点

### Java
- **record** は不変データに最適。`equals`/`hashCode`/`toString` が自動生成される。状態変更は copy-on-write（`toggle()` が新インスタンスを返す）。
- **コンストラクタインジェクション**: フィールド注入より推奨。`final` にでき、テストで普通に new できる。

### Spring Boot
- `@RestController` = `@Controller` + `@ResponseBody`。戻り値が自動で JSON 化。
- **Bean Validation**: DTO に `@NotBlank` 等 → Controller で `@Valid`。違反は `MethodArgumentNotValidException`。
- **ProblemDetail（RFC 9457）**: `ProblemDetail.forStatusAndDetail(...)` で機械可読なエラー。`setProperty` で追加フィールド。
- Actuator は依存追加だけで `/actuator/health` が立つ。

### JUnit 5
- `assertAll` は**最初の失敗で止まらず**全アサーションを評価。複数の独立検証に有効。
- `assertThrows` は例外オブジェクトを返すので、型だけでなくメッセージも検証できる。
- `@Nested` で「ある文脈において」のグルーピング。外側の `@BeforeEach` も継承される。
- パラメータ化: 静的に書くなら `@CsvSource`、動的・複雑な引数なら `@MethodSource`（`Stream<Arguments>` を返す static メソッド）。
- `assumeTrue` は前提が崩れたら**失敗ではなくスキップ**。環境依存テストを安全に分岐できる。

### テスト戦略
- **スライス `@WebMvcTest`**: Web 層だけロード。Service は `@MockitoBean` で差し替え → 速くて対象が絞れる。
- **統合 `@SpringBootTest`**: 全コンテキストを本物で配線。実際のフローを端から端まで。
- 両方書くと「どこをモックし、どこを通すか」の判断が身につく。

## 詰まった点

### JDK が未インストール
**症状**: `java`/`javac` 実行で `Unable to locate a Java Runtime`。Maven/Gradle も未導入。
**原因**: macOS に JDK が入っていなかった。
**解決**: `brew install openjdk@21`。ただし keg-only で PATH に通らないため、`JAVA_HOME=/opt/homebrew/opt/openjdk@21` を明示指定して `./gradlew` を実行。Gradle は wrapper 同梱なので別途インストール不要。
**教訓**: Spring Boot は Gradle/Maven wrapper を同梱するので、用意すべきは JDK 本体だけ。keg-only パッケージは `JAVA_HOME` 指定が要る（[[java-toolchain]] にメモ）。

### `@WebMvcTest` / `@AutoConfigureMockMvc` が見つからない
**症状**: `package org.springframework.boot.test.autoconfigure.web.servlet does not exist` でテストがコンパイル不能。
**原因**: **Spring Boot 4 でパッケージが移動**した。これらは旧パッケージから `org.springframework.boot.webmvc.test.autoconfigure` へ移った。
**解決**: 依存 jar の中身を `unzip -l <jar> | grep WebMvcTest.class` で総当たり検索し、新パッケージ（`spring-boot-webmvc-test-4.1.0.jar` 内）を特定して import を修正。
**教訓**: メジャーバージョンアップでは import パスが動く。記事を鵜呑みにせず、`unzip -l | grep` で実 jar のクラス位置を確認するのが最短の一次情報。

### Boot 4 のテストスターターはモジュール分割されている
**症状**: 従来の `spring-boot-starter-test` 一括スターターが見当たらない。
**原因**: Spring Boot 4 でテストスターターが機能別に分割された（`spring-boot-starter-webmvc-test`、`-validation-test`、`-actuator-test` 等）。
**解決**: Spring Initializr が依存に応じて必要な `-test` スターターを自動付与していたので、それをそのまま使用。
**教訓**: 構成は手書きせず Initializr 生成を起点にする。バージョン間の構造変化を取りこぼさずに済む。

### モック注入の API が非推奨
**症状**: Bean をモックに差し替えたいが、記事にある `@MockBean` が非推奨警告。
**原因**: Spring 6.2 で `@MockBean` は非推奨化され、`@MockitoBean`（`org.springframework.test.context.bean.override.mockito`）に置き換わった。
**解決**: スライステストのモック注入を `@MockitoBean` で記述。
**教訓**: テスト系アノテーションも世代交代する。非推奨を踏んだら現行の置換 API を確認して新しい方に寄せる。
