# Desafio Android - Solução Moderna com Jetpack Compose

Este projeto apresenta uma solução moderna para o desafio Android proposto pela PicPay. A aplicação original, que utilizava XML e possuía problemas de arquitetura e ciclo de vida, foi completamente refatorada para utilizar as tecnologias mais recentes do ecossistema Android, incluindo Jetpack Compose para a UI e uma arquitetura MVVM robusta.

<img src="https://github.com/mobilepicpay/desafio-android/blob/master/desafio-picpay.gif" width="300"/>

## ✨ Funcionalidades

- **Listagem de Contatos:** Exibe uma lista de usuários consumida a partir de uma API REST.
- **Acesso Offline:** Utiliza um banco de dados local (Room) para cachear os dados dos usuários, permitindo o funcionamento do app mesmo sem conexão.
- **Interface Reativa:** A UI, construída com Jetpack Compose, reage automaticamente a mudanças de estado (carregando, erro, dados carregados).
- **Manutenção de Estado:** O estado da tela é mantido durante mudanças de configuração (como rotação do dispositivo), graças ao uso de `ViewModel`.

## 🛠️ Arquitetura e Tecnologias

O projeto foi reestruturado para seguir as melhores práticas de desenvolvimento Android, resolvendo os problemas propostos no desafio original.

### Arquitetura: **MVVM (Model-View-ViewModel)**

-   **View (UI Layer):** Totalmente construída com **Jetpack Compose**. As telas (`@Composable`) são responsáveis apenas por exibir o estado fornecido pelo `ViewModel` e notificar eventos de usuário.
-   **ViewModel:** Contém a lógica de apresentação e gerencia o estado da UI. Utiliza `LiveData` para expor os dados à camada de UI, sobrevivendo a mudanças de configuração.
-   **Model (Data Layer):** Utiliza o **Repository Pattern** para abstrair as fontes de dados (rede e banco de dados local). O `Repository` decide se deve buscar dados da API ou do cache local.

### Bibliotecas Principais

-   **Interface Gráfica:**
    -   **Jetpack Compose:** Framework de UI declarativo e moderno do Google, utilizado para construir 100% da interface do aplicativo.
    -   **Coil:** Biblioteca de carregamento de imagens otimizada para coroutines e Jetpack Compose.
-   **Arquitetura e Ciclo de Vida:**
    -   **ViewModel:** Gerencia o estado da UI de forma consciente ao ciclo de vida.
    -   **LiveData:** Cria fluxos de dados observáveis que notificam a UI sobre mudanças de estado.
-   **Injeção de Dependência:**
    -   **Koin:** Framework leve para injeção de dependência em Kotlin, utilizado para fornecer `ViewModel`, `Repository` e outras classes.
-   **Rede (Networking):**
    -   **Retrofit:** Cliente HTTP para realizar chamadas à API REST de forma segura.
    -   **OkHttp:** Utilizado como base do Retrofit, inclui um `logging-interceptor` para facilitar o debug das chamadas de rede.
-   **Persistência de Dados (Cache):**
    -   **Room:** Biblioteca de persistência que fornece uma camada de abstração sobre o SQLite, facilitando o armazenamento local dos dados.
-   **Testes:**
    -   **JUnit 4:** Framework padrão para testes unitários.
    -   **Mockito & MockK:** Bibliotecas para criar "mocks" e simular comportamentos em testes, garantindo o isolamento da lógica de negócio.
    -   **Turbine:** Utilitário para testar `Flows` e `LiveData` de forma simples e eficaz.

## ✅ Requisitos do Desafio Atendidos

1.  **Manutenção de Estado:** Resolvido com o uso do `ViewModel`, que retém o estado da tela durante mudanças de configuração.
2.  **Correção de Crashes (Nullability & Lifecycle):** Resolvido com o uso de Kotlin (que promove Null-Safety) e componentes do Jetpack Architecture (ViewModel, LiveData), que são conscientes do ciclo de vida.
3.  **Cache de Dados:** Implementado com o `Room`. O `Repository` primeiro tenta buscar dados da API e, em caso de falha ou offline, utiliza os dados cacheados no banco de dados.
4.  **Flexibilidade para Mudanças:** A arquitetura MVVM com separação de camadas permite que a lógica de negócios (`ViewModel`, `Repository`) e a de apresentação (`Compose UI`) sejam modificadas de forma independente.
5.  **Testes Automatizados:** A estrutura permite testes unitários isolados para cada camada:
    -   **Data Layer:** Testes para o `Repository`.
    -   **Business Logic:** Testes para o `ViewModel`, verificando a emissão de estados.
    -   **UI Layer:** Testes de UI podem ser criados com o `ComposeTestRule` para verificar se os composables reagem corretamente aos diferentes estados.