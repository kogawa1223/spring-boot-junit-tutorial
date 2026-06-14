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

## 詰まった点ログ

| 症状 | 原因・解決 |
|---|---|
| JDK 未インストール（`Unable to locate a Java Runtime`） | `brew install openjdk@21`（keg-only）。`JAVA_HOME=/opt/homebrew/opt/openjdk@21` を指定して gradle 実行 |
| `@WebMvcTest`/`@AutoConfigureMockMvc` が `package ... does not exist` | **Spring Boot 4 でパッケージ移動**。`org.springframework.boot.test.autoconfigure.web.servlet` → `org.springframework.boot.webmvc.test.autoconfigure`。jar 内を `unzip -l \| grep WebMvcTest.class` で特定して修正 |
| Boot 4 のテストスターター | 旧 `spring-boot-starter-test` 一括ではなく、`spring-boot-starter-webmvc-test` 等にモジュール分割されている（Initializr が自動付与） |
| モック注入の API | 非推奨の `@MockBean` ではなく `@MockitoBean`（`org.springframework.test.context.bean.override.mockito`、Spring 6.2+）を使用 |
