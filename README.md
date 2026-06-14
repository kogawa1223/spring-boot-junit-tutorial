# Spring Boot + JUnit 5 Tutorial

[![CI](https://github.com/kogawa1223/spring-boot-junit-tutorial/actions/workflows/ci.yml/badge.svg)](https://github.com/kogawa1223/spring-boot-junit-tutorial/actions/workflows/ci.yml)

**Java / Spring Boot / JUnit 5** を1リポジトリで束ねて学ぶ学習リポジトリ。
公式の [Spring Boot Getting Started](https://spring.io/guides/gs/spring-boot) と [JUnit 5 User Guide](https://docs.junit.org/current/user-guide/) を素材に、**小さくても本物の REST API を作り、それをスライス／統合テストで検証する**構成にした。

- **Spring Boot 4.1** / **Java 21** / **Gradle 9.5**（wrapper 同梱、JDK さえあれば動く）
- テスト: **33 件 green**（うち 1 件は `@Disabled` のデモ）

```bash
./gradlew test       # 全テスト（JUnit 5 + Spring slice/integration）
./gradlew bootRun    # アプリ起動 → http://localhost:8080
curl localhost:8080/actuator/health        # {"status":"UP"}
curl localhost:8080/api/tasks              # Task 一覧
```

> JDK は Homebrew の `openjdk@21` を使用。`JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew test` のように指定して実行する。

## 作ったもの（題材）

インメモリの **Task API**（ToDo）。学習に集中するため DB は使わず `ConcurrentHashMap` で保持。

| メソッド | パス | 説明 |
|---|---|---|
| `GET` | `/api/tasks` | 一覧（作成順） |
| `GET` | `/api/tasks/{id}` | 1件取得（無ければ 404） |
| `POST` | `/api/tasks` | 作成（`title` を Bean Validation） |
| `PATCH` | `/api/tasks/{id}/toggle` | done を反転 |
| `DELETE` | `/api/tasks/{id}` | 削除 |

エラーは **ProblemDetail（RFC 9457）** で返す。バリデーション違反は 400 + フィールド別メッセージ。

## ディレクトリ構成

```
src/main/java/dev/kogawa/tutorial/
├── util/Calculator.java          # Spring 非依存の純 Java（JUnit 基礎の題材）
├── task/
│   ├── Task.java                 # record（不変ドメイン）
│   ├── CreateTaskRequest.java    # Bean Validation つき DTO
│   ├── TaskService.java          # @Service（インメモリ）
│   ├── TaskController.java       # @RestController（CRUD）
│   └── TaskNotFoundException.java
└── web/GlobalExceptionHandler.java   # @RestControllerAdvice（例外→HTTP）

src/test/java/dev/kogawa/tutorial/
├── junit/AssertionsTest.java          # JUnit: assertions, assertAll, assertThrows
├── junit/LifecycleNestedTest.java     # JUnit: @BeforeEach/@AfterAll, @Nested, @DisplayName
├── junit/ParameterizedTests.java      # JUnit: @ValueSource/@CsvSource/@MethodSource
├── junit/AssumptionsDisabledTest.java # JUnit: assumeTrue, @Disabled, @EnabledOnOs
├── task/TaskControllerWebMvcTest.java # Spring: @WebMvcTest スライス + @MockitoBean
└── task/TaskApiIntegrationTest.java   # Spring: @SpringBootTest 統合（実 Bean）
```

## 学習トピックの対応表

| 技術 | 学んだこと | 主なファイル |
|---|---|---|
| **Java** | record、コンストラクタ DI、Stream、例外 | `task/*`, `util/Calculator` |
| **Spring Boot** | REST、Bean Validation、`@RestControllerAdvice`、Actuator、DI | `task/TaskController`, `web/GlobalExceptionHandler` |
| **JUnit 5** | assertions / lifecycle / `@Nested` / parametrized / assumptions | `junit/*` |
| **テスト戦略** | スライス（`@WebMvcTest`）と統合（`@SpringBootTest`）の使い分け | `task/*Test` |

詳細な要点・詰まった点は [NOTES.md](NOTES.md)。

## 設計判断

- **スライス vs 統合を両方書いた**: `@WebMvcTest` は Web 層だけを高速に、`@SpringBootTest` は全配線を本物で。違いが分かるよう同じ API を2通りで検証。
- **Spring 4 の新 API を採用**: モックは非推奨の `@MockBean` ではなく `@MockitoBean`（Spring 6.2+）。エラーは `ProblemDetail`。
- **テスト分離**: 統合テストは `@BeforeEach` で状態をリセットし、順序に依存しない。
- **1トピック = 1ファイル = 1コミット**: Conventional Commits で学習の進行が追える。
