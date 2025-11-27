# 🚗 SafeStop - Sistema de Gerenciamento de Estacionamento

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005F0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)

> **SafeStop** é uma solução web completa para digitalizar o fluxo operacional e financeiro de estacionamentos, substituindo controles manuais por uma gestão eficiente, segura e baseada em dados.

---

## 📋 Sobre o Projeto

Este projeto foi desenvolvido como parte do Projeto Integrador do curso de Análise e Desenvolvimento de Sistemas. O objetivo principal é oferecer aos gestores de estacionamento uma ferramenta para monitorar vagas em tempo real, automatizar o cálculo de tarifas e garantir a segurança dos dados através de controle de acesso rigoroso.

Diferente de sistemas tradicionais, o **SafeStop** possui uma arquitetura flexível onde as regras de negócio (preço e tolerância) são configuráveis, e conta com uma API interna inteligente para agilizar o atendimento.

---

## 🚀 Funcionalidades Principais

### 🏢 Painel Administrativo
* **Dashboard em Tempo Real:** Visualização imediata de Vagas Livres, Ocupadas, Taxa de Ocupação e **Faturamento do Dia**.
* **Configuração Dinâmica:** O administrador define o Valor por Hora e os Minutos de Tolerância (cortesia) diretamente pelo sistema, sem alterar código.
* **Relatórios Financeiros:** Geração de relatórios filtrados por período, exibindo KPIs como Ticket Médio e Total de Entradas.

### 🚗 Operação (Entrada e Saída)
* **Registro Inteligente:** Ao digitar a placa, o sistema verifica automaticamente se o veículo já está no pátio (evitando duplicidade) e busca dados de clientes recorrentes para auto-preenchimento.
* **Validação de Dados:** Garantia de integridade com validação de formato de Placa (7 dígitos) e Telefone (11 dígitos).
* **Checkout Flexível:** Cálculo automático do valor com arredondamento de hora cheia, permitindo a aplicação de **descontos (R$ ou %)** no momento do pagamento.

### 👥 Gestão
* **Controle de Vagas:** Criação de vagas (unitária ou em massa), edição de tipos (Comum, PCD, Moto) e desativação segura (impede desativar vaga em uso).
* **Controle de Funcionários:** CRUD completo de funcionários com ativação/desativação de acesso ao sistema.
* **Segurança:** Controle de acesso baseado em papéis (Role-Based Access Control) com Spring Security. Apenas Admins acessam áreas sensíveis.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 17
* **Framework:** Spring Boot 3.x
* **Segurança:** Spring Security
* **Banco de Dados:** H2 Database (Dev) / MySQL (Prod)
* **Persistência:** Spring Data JPA
* **Frontend:** Thymeleaf (Server-Side Rendering) com CSS customizado
* **Ferramentas:** Lombok, DevTools, Maven

---

## 📦 Como Rodar o Projeto

### Pré-requisitos
* Java JDK 17+ instalado.
* Maven instalado (ou usar o wrapper `mvnw` incluso).

### Passos
1.  Clone o repositório:
    ```bash
    git clone [https://github.com/seu-usuario/safestop.git](https://github.com/seu-usuario/safestop.git)
    cd safestop
    ```
2.  Execute a aplicação:
    ```bash
    ./mvnw spring-boot:run
    ```
3.  Acesse no navegador:
    ```
    http://localhost:8080
    ```

---

## 🔐 Acesso Padrão (Dados de Teste)

O sistema inicializa com os seguintes usuários para teste (definidos no `data.sql`):

| Perfil | Email | Senha | Permissões |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin@safestop.com` | `admin123` | Acesso total (Configurações, Relatórios, Gestão) |
| **Funcionário** | `ze@safestop.com` | `func123` | Acesso operacional (Entrada, Saída, Dashboard) |

---

## 📐 Estrutura do Banco de Dados

O sistema utiliza um modelo relacional robusto:
* **Usuario:** Funcionários e Admins.
* **Vaga:** As vagas físicas do estacionamento.
* **Cliente & Veiculo:** Cadastro de clientes e seus carros.
* **Ticket:** O registro central que liga um Veículo a uma Vaga, com horários e valores.
* **Configuracao:** Tabela de configuração global do sistema (Preços/Regras).

---
*Desenvolvido como Projeto Integrador IV - 2025*
