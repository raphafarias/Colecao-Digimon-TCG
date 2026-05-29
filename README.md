# 🃏 Digimon TCG - Gerenciador de Coleção Pessoal

Uma aplicação Desktop robusta desenvolvida em **Java Swing** integrada a um banco de dados relacional **MySQL** para gerenciar coleções pessoais de cartas de Digimon Card Game. O sistema conecta-se diretamente à API pública oficial do Digimon Card.io para catalogar dados em lote e exibir imagens atualizadas em tempo real.

---

## 🚀 Funcionalidades Principais

* **Painel de Controle Estatístico (Dashboard):** Tela inicial moderna em estilo *Dark Mode* que exibe métricas dinâmicas do banco de dados (Total de cartas catalogadas, cartas únicas adquiridas e o volume total físico na estante).
* **Sincronização em Massa (API):** Download e processamento automático do catálogo de todas as cartas oficiais através do endpoint global da API.
* **Filtros Inteligentes e Busca:** Barra de pesquisa em tempo real por nome e filtro booleano (*Checkbox*) para visualizar exclusivamente as cartas que você já possui em sua coleção.
* **Previsualização Dinâmica:** Carregamento assíncrono em segundo plano (*Multithreading*) das artes das cartas ao selecionar uma linha da tabela, evitando travamentos na interface.
* **Smart Getter de URLs:** Algoritmo customizado para higienizar códigos e tratar strings de imagens da API, neutralizando inconsistências nos links fornecidos externamente.
* **Persistência Segura (MySQL):** Mecanismo de salvamento eficiente utilizando `ON DUPLICATE KEY UPDATE` para alimentar o catálogo sem corromper as quantidades coletadas pelo usuário.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 17+
* **Interface Gráfica:** Java Swing / AWT
* **Banco de Dados:** MySQL 8.0
* **Comunicação HTTP:** Java `HttpClient` (assíncrono/nativo)
* **Manipulação de JSON:** Google Gson
* **Ferramenta de Build:** Maven

---

## 📦 Estrutura do Projeto

O código foi arquitetado seguindo boas práticas de divisão de responsabilidades da orientação a objetos:

* `App.java`: Ponto de entrada do sistema que inicializa a thread visual com o `DashboardFrame`.
* `Carta.java`: Classe de modelo que encapsula as propriedades da carta e as anotações de desserialização múltipla do GSON (`@SerializedName`).
* `CartaDAO.java`: Camada de persistência responsável por gerenciar a conexão JDBC, consultas agregadas de estatísticas e manipulação no banco.
* `DigimonService.java`: Camada de infraestrutura responsável pelas requisições REST de busca e sincronização completa com a API externa.
* `DashboardFrame.java`: Janela de boas-vindas contendo indicadores numéricos e navegação do app.
* `GerenciadorCartasFrame.java`: Tela central de gerenciamento contendo a tabela estruturada, mecanismos de filtros e previsualizador.

---

## 🔧 Configuração e Instalação

### 1. Pré-requisitos
* Java JDK 17 ou superior instalado.
* MySQL Server ativo em sua máquina.
* IDE de sua preferência (VS Code, IntelliJ, Eclipse, etc.) com suporte a Maven.

### 2. Banco de Dados
Crie o banco de dados e a tabela necessária executando o seguinte script no seu terminal MySQL ou Workbench:

```sql
CREATE DATABASE IF NOT EXISTS digimon_tcg;
USE digimon_tcg;

CREATE TABLE IF NOT EXISTS cartas (
    codigo VARCHAR(50) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    cor VARCHAR(30),
    nivel INT,
    imagem_url TEXT,
    quantidade INT DEFAULT 0,
    PRIMARY KEY (codigo)
);
