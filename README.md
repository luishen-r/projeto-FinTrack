# FinTrack 💰

O **FinTrack** é uma aplicação de gestão de finanças pessoais desenvolvida em Java. Nesta etapa (Unidade 5 | Capítulo 1 — Desenvolvimento do Projeto Intermediário), o projeto evoluiu de uma aplicação apenas de console para uma aplicação com **interface gráfica (JavaFX)**, **persistência em banco de dados relacional (JDBC/MySQL)**, uso de **Generics** e **testes automatizados com JUnit 5**.

O projeto segue o padrão de diretórios do Maven (`src/main/java`), permitindo abrir/importar em qualquer IDE (IntelliJ, Eclipse, VS Code) como um projeto Maven, com as dependências gerenciadas via `pom.xml`.

---

## ✨ Funcionalidades

- **➕ Adicionar Transação:** registro de receitas e despesas (avulsas ou mensais/recorrentes), com descrição, valor, tipo e data.
- **🔍 Validar Entradas:** regras de validação centralizadas em `util.Validador`, reaproveitadas tanto pela versão console quanto pela versão gráfica.
- **📋 Listar Transações:** exibição do histórico completo de movimentações.
- **📊 Saldo e Relatório:** cálculo do saldo consolidado (Receitas − Despesas) e uma tela de relatório com totais.
- **💱 Formatação de Moeda:** valores exibidos no padrão brasileiro (`R$`) via `Locale`.
- **❌ Excluir Transação:** remoção de registros específicos.
- **🗄️ Persistência em Banco de Dados:** tudo salvo em MySQL via JDBC.

---

## 🧩 Novas Tecnologias e Conceitos Aplicados

### 1. Generics
A classe `repository.RepositorioGenerico<T>` é usada internamente pelo `controller.FinTracker` para armazenar as transações, demonstrando:
- Classe genérica (`RepositorioGenerico<T>`).
- Wildcard `? extends T` em `adicionarTodos(Collection<? extends T>)` — permite adicionar, por exemplo, uma `List<TransacaoMensal>` a um repositório de `Transacao`.
- Wildcard `? super T` em `copiarPara(Collection<? super T>)` — permite copiar os elementos para qualquer coleção compatível com um supertipo de `T`.

### 2. JavaFX — Interface Gráfica
- `app.FinApp` estende `Application` e inicia a aplicação pelo método `start(Stage)`.
- **Tela Principal** (`TelaPrincipal.fxml`): tabela com todas as transações, saldo atual e botões de ação.
- **Tela de Nova Transação** (`NovaTransacao.fxml`): formulário para cadastrar receitas/despesas (avulsas ou mensais), com validação.
- **Tela de Relatório** (`Relatorio.fxml`): totais de receitas, despesas e saldo.
- Componentes usados: `Label`, `TextField`, `Button`, `ComboBox`, `DatePicker`, `CheckBox`, `TableView`, `GridPane`, `HBox`, `VBox`, `BorderPane`.
- Eventos tratados com `onAction` (`setOnAction` via `#metodo` no FXML).

### 3. FXML + Scene Builder + CSS
- As telas são definidas em arquivos `.fxml` (em `src/main/resources/fxml/`), **editáveis diretamente no Scene Builder**.
- A lógica de cada tela fica separada em uma classe controller (pacote `ui`), vinculada via `fx:controller`.
- A estilização fica centralizada em `src/main/resources/css/style.css`, aplicada a todas as janelas.

### 4. Acesso a Banco de Dados com JDBC
- Tabela `transacoes` (colunas: `id`, `descricao`, `valor`, `tipo`, `data`, `mensal`, `mes`), criada automaticamente pelo `db.Conexao`.
- `db.TransacaoDAO` implementa o CRUD completo com `PreparedStatement` e `ResultSet`, evitando SQL Injection.
- Conexão centralizada em `db.Conexao` (host, porta, banco, usuário e senha).
- Tratamento de erros SQL com `try/catch` e logs (`java.util.logging.Logger`).
- Controle de transações com `commit()`/`rollback()` demonstrado em `TransacaoDAO.inserirEmLote(...)`.
- **Importante:** o `TransacaoDAO` recebe a `Connection` por injeção de dependência (via construtor) — em produção, essa conexão vem do MySQL (`db.Conexao`); nos testes, vem de um banco SQLite em memória (ver seção de testes abaixo). Isso é o que torna o DAO testável sem precisar de um servidor de banco de dados rodando.

### 5. Testes de Software com JUnit 5
- Testes unitários para `Transacao`, `TransacaoMensal`, `FinTracker`, `RepositorioGenerico`, `Validador` e `TransacaoDAO`.
- **`db.TransacaoDAOTest` usa SQLite em memória** (`jdbc:sqlite::memory:`), exatamente como pedido no enunciado — não é necessário ter o MySQL rodando para executar os testes.
- Uso das anotações `@Test`, `@BeforeEach`, `@AfterEach`.
- Uso de `Assertions.assertEquals()`, `assertTrue()`, `assertThrows()`, `assertDoesNotThrow()`, entre outras.
- Testes organizados em pacotes separados, espelhando a estrutura de `src/main/java`.

---

## 📁 Estrutura do Projeto (Maven)

```text
FinTrack/
├── pom.xml                                    # Configuração do Maven (dependências e plugins)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── app/
│   │   │   │   ├── Main.java                  # Ponto de entrada da versão console
│   │   │   │   └── FinApp.java                # Ponto de entrada da versão gráfica (JavaFX)
│   │   │   ├── controller/
│   │   │   │   └── FinTracker.java            # Lógica de negócio (usa RepositorioGenerico internamente)
│   │   │   ├── db/
│   │   │   │   ├── Conexao.java               # Conexão JDBC com o MySQL + criação da tabela
│   │   │   │   ├── TransacaoDAO.java          # CRUD via JDBC (recebe a Connection por injeção)
│   │   │   │   └── TransacaoRegistro.java     # Associa uma Transacao ao seu id no banco
│   │   │   ├── exceptions/
│   │   │   │   └── EntradaInvalidaException.java
│   │   │   ├── model/
│   │   │   │   ├── Transacao.java             # Classe base (agora com campo "data")
│   │   │   │   └── TransacaoMensal.java       # Extensão de Transacao com recorrência mensal
│   │   │   ├── repository/
│   │   │   │   └── RepositorioGenerico.java   # Classe genérica (Generics + wildcards)
│   │   │   ├── ui/
│   │   │   │   ├── TelaPrincipalController.java
│   │   │   │   ├── NovaTransacaoController.java
│   │   │   │   └── RelatorioController.java
│   │   │   └── util/
│   │   │       ├── Formatador.java            # Formatação de valores em moeda (pt-BR)
│   │   │       └── Validador.java             # Regras de validação reaproveitadas (console + GUI)
│   │   └── resources/
│   │       ├── fxml/
│   │       │   ├── TelaPrincipal.fxml
│   │       │   ├── NovaTransacao.fxml
│   │       │   └── Relatorio.fxml
│   │       └── css/
│   │           └── style.css
│   └── test/
│       └── java/
│           ├── controller/FinTrackerTest.java
│           ├── db/TransacaoDAOTest.java       # Usa SQLite em memória
│           ├── model/TransacaoTest.java
│           ├── model/TransacaoMensalTest.java
│           ├── repository/RepositorioGenericoTest.java
│           └── util/
│               ├── FormatadorTest.java
│               └── ValidadorTest.java         # Usa assertThrows()
```

---

## ⚙️ Dependências (`pom.xml`)

- **JavaFX** (`javafx-controls`, `javafx-fxml`) — interface gráfica e carregamento de FXML.
- **JDBC / MySQL** (`com.mysql:mysql-connector-j`) — persistência em produção. Requer um servidor MySQL instalado e em execução.
- **JUnit 5** (`org.junit.jupiter:junit-jupiter`, escopo `test`) — testes unitários.
- **SQLite JDBC** (`org.xerial:sqlite-jdbc`, escopo `test`) — usado **apenas nos testes** do `TransacaoDAO`, para não depender do MySQL ao rodar `mvn test`.

Plugins configurados:

| Plugin | Finalidade |
|---|---|
| `maven-compiler-plugin` | Compila o projeto usando Java 17 |
| `maven-surefire-plugin` | Executa os testes JUnit com `mvn test` |
| `javafx-maven-plugin` | Executa a versão gráfica com `mvn javafx:run` |
| `exec-maven-plugin` | Executa a versão console com `mvn compile exec:java` |
| `maven-shade-plugin` | Gera um `.jar` único e executável em `target/FinTrack.jar` |

## 🗄️ Configurando o MySQL

Antes de rodar a versão JavaFX, é necessário ter um servidor MySQL em execução e criar o banco de dados:

```sql
CREATE DATABASE fintrack CHARACTER SET utf8mb4;
```

Em seguida, ajuste as credenciais em `src/main/java/db/Conexao.java` conforme o seu ambiente:

```java
private static final String HOST = "localhost";
private static final String PORTA = "3306";
private static final String BANCO = "fintrack";
private static final String USUARIO = "root";
private static final String SENHA = "senha";
```

A tabela `transacoes` é criada automaticamente na primeira conexão — não é necessário criá-la manualmente.

> Os testes (`mvn test`) **não** precisam do MySQL: eles usam um banco SQLite em memória, criado e descartado automaticamente a cada execução.

## 🎨 Editando as telas no Scene Builder

Os arquivos em `src/main/resources/fxml/` podem ser abertos diretamente no [Scene Builder](https://gluonhq.com/products/scene-builder/). Basta apontar o Scene Builder para essa pasta e editar visualmente `TelaPrincipal.fxml`, `NovaTransacao.fxml` ou `Relatorio.fxml`. As classes controller correspondentes (pacote `ui`) já estão vinculadas via `fx:controller`.

## ▶️ Como executar

Após importar o projeto na IDE (ou via terminal, na raiz do projeto):

```bash
# Baixar as dependências e compilar
mvn clean compile

# Rodar os testes unitários (JUnit 5 — usa SQLite em memória, não precisa de MySQL)
mvn test

# Rodar a versão console (Main.java) - não usa banco de dados
mvn compile exec:java

# Rodar a versão gráfica em JavaFX (FinApp.java) - requer MySQL configurado
mvn javafx:run

# Gerar um jar executável (versão JavaFX) em target/FinTrack.jar
mvn clean package
java -jar target/FinTrack.jar
```
