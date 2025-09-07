# Relatório — Sistema de Biblioteca (POO com Java)

## 1. Capa
**Sistema de Biblioteca**  
Trabalho Acadêmico — Programação Orientada a Objetos (Java)  
Autores: Renata Menezes de Faria; Thamela Cristina Rodrigues de Oliveira; Keila Almeida Santana; Samuel Victor Alventino Silva.

---

## 2. Introdução
Este relatório apresenta o desenvolvimento de um Sistema de Biblioteca utilizando Programação Orientada a Objetos (POO) em Java. O objetivo foi modelar um problema do mundo real aplicando herança, polimorfismo, composição, agregação, persistência e interface gráfica.

O sistema permite gerenciar o acervo (livros, revistas e mídias digitais), usuários (com cartão obrigatório), empréstimos e devoluções. Os dados são persistidos em arquivos binários (`*.dat`) e a interface gráfica foi construída com Java Swing. Também há um modo console para operações básicas.

---

## 3. Modelagem Conceitual (UML)
A seguir estão dois diagramas PlantUML para gerar as imagens no site PlantUML/PlantText/Kroki.

### 3.1. Diagrama de Domínio
> Foco nas entidades de negócio e seus relacionamentos.

```plantuml
@startuml
!pragma teoz true
' Tema e estilo
skinparam backgroundColor #FFFFFF
skinparam class {
  BackgroundColor<<abstract>> #FFF5E6
  BackgroundColor #F7FBFF
  BorderColor #4A90E2
  ArrowColor #4A90E2
}
skinparam package {
  BackgroundColor #EEF6FF
  BorderColor #4A90E2
}
skinparam shadowing false
left to right direction

title **Sistema de Biblioteca — Diagrama de Domínio**

package "Modelo (Domínio)" {
  abstract class Publicacao <<abstract>> {
    - id: String
    - titulo: String
    - autor: String
    + prazoDiasEmprestimo(): int
    + multaPorDia(): double
    + toString(): String
  }

  class Livro {
    - paginas: int
    + prazoDiasEmprestimo(): int
    + multaPorDia(): double
  }

  class Revista {
    - edicao: int
    + prazoDiasEmprestimo(): int
    + multaPorDia(): double
  }

  class MidiaDigital {
    - tamanhoMB: double
    + prazoDiasEmprestimo(): int
    + multaPorDia(): double
  }

  class Pessoa {
    - nome: String
    - documento: String
    + getNome(): String
    + getDocumento(): String
  }

  class Usuario {
    - cartao: CartaoBiblioteca
    + emitirCartao(numero: String): void
    + possuiCartao(): boolean
    + getNumeroCartao(): String
    + toString(): String
  }

  class CartaoBiblioteca {
    - numero: String
    + getNumero(): String
  }

  abstract class Transacao <<abstract>> {
    + executar(): void
  }

  class Emprestimo {
    - usuario: Usuario
    - item: Publicacao
    - dataPrevista: LocalDate
    + executar(): void
    + getDataPrevista(): LocalDate
  }

  class Devolucao {
    - usuario: Usuario
    - item: Publicacao
    - dataReal: LocalDate
    + executar(): void
    + diasAtraso(dataPrevista: LocalDate): long
    + calcularMulta(dataPrevista: LocalDate): double
  }

  class Biblioteca {
    - nome: String
    - endereco: Endereco
    - acervo: List<Publicacao>
    - usuarios: List<Usuario>
    + adicionarPublicacao(p: Publicacao): void
    + removerPublicacao(p: Publicacao): void
    + listarAcervo(): List<Publicacao>
    + registrarUsuario(u: Usuario): void
    + listarUsuarios(): List<Usuario>
    + buscarPublicacao(id: String): Optional<Publicacao>
    + buscarUsuarioPorDoc(doc: String): Optional<Usuario>
  }

  class Endereco {
    - rua: String
    - numero: String
    - cidade: String
  }
}

' --- Heranças (Polimorfismo) ---
Publicacao <|-- Livro
Publicacao <|-- Revista
Publicacao <|-- MidiaDigital
Pessoa <|-- Usuario

' --- Composição e Agregação ---
Usuario *-- CartaoBiblioteca : composição\n(cartão obrigatório)
Biblioteca o-- Publicacao : agregação
Biblioteca o-- Usuario : agregação

' --- Associações de Transações ---
Transacao <|-- Emprestimo
Transacao <|-- Devolucao
Emprestimo --> Usuario : associa
Emprestimo --> Publicacao : associa
Devolucao --> Emprestimo : referencia

' --- Notas explicativas ---
note right of Publicacao
**Polimorfismo**
Cada subclasse define seu
prazo de empréstimo e multa.
end note

note bottom of Usuario
**Composição**
Usuário possui um Cartão
emitido no cadastro.
end note

legend right
== Legenda ==
<|-- Herança / Generalização
o--  Agregação (coleção gerenciada)
*--  Composição (parte obrigatória)
-->  Associação (uso/referência)
<<abstract>> Classe Abstrata
endlegend

@enduml
```

### 3.2. Visão de Arquitetura
> Mostra as camadas (Domínio, Serviço, Persistência e UI) e as dependências.

```plantuml
@startuml
skinparam backgroundColor #FFFFFF
skinparam class {
  BackgroundColor<<abstract>> #FFF5E6
  BackgroundColor #F7FBFF
  BorderColor #4A90E2
  ArrowColor #4A90E2
}
skinparam package {
  BackgroundColor #EEF6FF
  BorderColor #4A90E2
}
left to right direction
title **Sistema de Biblioteca — Visão de Arquitetura**

package "Camada de Domínio" {
  abstract class Publicacao <<abstract>>
  class Livro
  class Revista
  class MidiaDigital
  class Pessoa
  class Usuario
  class CartaoBiblioteca
  abstract class Transacao <<abstract>>
  class Emprestimo
  class Devolucao
  class Biblioteca
  class Endereco
}

package "Serviço" {
  class BibliotecaService {
    - biblioteca: Biblioteca
    + carregar(): void
    + salvar(): void
    + getAcervo(): List<Publicacao>
    + getUsuarios(): List<Usuario>
    + getEmprestimos(): List<Emprestimo>
    + cadastrarLivro(id,titulo,autor,paginas): void
    + cadastrarRevista(id,titulo,autor,edicao): void
    + cadastrarMidia(id,titulo,autor,tamanhoMB): void
    + removerPublicacaoPorId(id): void
    + cadastrarUsuario(nome,doc): void
    + removerUsuarioPorDocumento(doc): boolean
    + emprestar(u,p): Emprestimo
    + devolver(e): Devolucao
  }
}

package "Persistência" {
  class ArquivoDAO<T> {
    - nomeArquivo: String
    + salvarLista(lista: List<T>): void
    + carregarLista(): List<T>
  }
}

package "UI (Swing)" {
  class MainFrame
  class PainelAcervo
  class PainelUsuarios
  class PainelEmprestimos
  class MainGUI
  class MainConsole
}

' Relacionamentos
Publicacao <|-- Livro
Publicacao <|-- Revista
Publicacao <|-- MidiaDigital
Pessoa <|-- Usuario
Usuario *-- CartaoBiblioteca
Biblioteca o-- Publicacao
Biblioteca o-- Usuario
Transacao <|-- Emprestimo
Transacao <|-- Devolucao
Emprestimo --> Usuario
Emprestimo --> Publicacao
Devolucao --> Emprestimo

BibliotecaService --> Biblioteca
BibliotecaService ..> ArquivoDAO : usa
MainFrame --> BibliotecaService
PainelAcervo --> BibliotecaService
PainelUsuarios --> BibliotecaService
PainelEmprestimos --> BibliotecaService
MainGUI --> MainFrame
MainConsole --> BibliotecaService

@enduml
```

---

## 4. Aplicação dos Conceitos de POO (exemplos do projeto)

### 4.1. Herança e Polimorfismo
`Publicacao` é abstrata; `Livro`, `Revista` e `MidiaDigital` implementam prazos e multas distintos:
```java
public abstract class Publicacao implements Serializable {
    private String id, titulo, autor;
    public abstract int prazoDiasEmprestimo();
    public abstract double multaPorDia();
}

public class Livro extends Publicacao {
    private int paginas;
    @Override public int prazoDiasEmprestimo() { return 14; }
    @Override public double multaPorDia() { return 1.50; }
}
```

### 4.2. Classe Abstrata e Polimorfismo de Comportamento
`Transacao` define `executar()`; `Emprestimo` e `Devolucao` implementam comportamentos diferentes:
```java
public abstract class Transacao implements Serializable {
    public abstract void executar() throws Exception;
}

public class Emprestimo extends Transacao {
    private Usuario usuario;
    private Publicacao item;
    private LocalDate dataPrevista;
    @Override
    public void executar() {
        dataPrevista = LocalDate.now().plusDays(item.prazoDiasEmprestimo());
    }
}
```

### 4.3. Composição
`Usuario` contém um `CartaoBiblioteca` (parte-obrigatória emitida no cadastro):
```java
public class Usuario extends Pessoa {
    private CartaoBiblioteca cartao;
    public void emitirCartao(String numero) {
        if (cartao != null) throw new IllegalStateException("Usuário já possui cartão.");
        cartao = new CartaoBiblioteca(numero, this);
    }
}
```

### 4.4. Agregação
`Biblioteca` agrega coleções de `Publicacao` e `Usuario`:
```java
public class Biblioteca implements Serializable {
    private List<Publicacao> acervo = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    public void adicionarPublicacao(Publicacao p) { acervo.add(p); }
    public void registrarUsuario(Usuario u) { usuarios.add(u); }
}
```

### 4.5. Persistência (DAO simples em arquivo)
`BibliotecaService` usa `ArquivoDAO<T>` para salvar/carregar listas:
```java
public class BibliotecaService implements Serializable {
    private final ArquivoDAO<Publicacao> acervoDAO = new ArquivoDAO<>("acervo.dat");
    private final ArquivoDAO<Usuario> usuariosDAO = new ArquivoDAO<>("usuarios.dat");
    private final ArquivoDAO<Emprestimo> emprestimosDAO = new ArquivoDAO<>("emprestimos.dat");

    public void carregar() throws IOException {
        biblioteca.carregarAcervo(acervoDAO.carregarLista());
        biblioteca.carregarUsuarios(usuariosDAO.carregarLista());
        biblioteca.carregarEmprestimos(emprestimosDAO.carregarLista());
    }
}
```

---

## 5. Uso de Bibliotecas
- **Java Swing** — construção da interface gráfica (JFrame, JPanel, JTable, etc.).  
- **Java IO** — serialização simples para persistência em arquivos `.dat`.  
- **Collections API** — uso de `List`, `ArrayList`, `Optional`, `Stream`.  
- **PlantUML** — criação dos diagramas UML.  

---

## 6. Manual do Usuário

### 6.1. Interface Gráfica (Swing)
- **Aba Acervo**: cadastrar (Livro, Revista, Mídia Digital), remover item selecionado.  
- **Aba Usuários**: cadastrar (nome, documento e **cartão obrigatório**), remover selecionado.  
- **Aba Empréstimos**: selecionar usuário e item para emprestar; tabela de empréstimos com opção de devolução.  

### 6.2. Modo Console
Ordem do menu:  
1) Listar acervo  
2) Cadastrar acervo  
3) Remover publicação por ID  
4) Listar usuários  
5) Cadastrar usuário (**cartão obrigatório**)  
6) Remover usuário  
7) Listar empréstimos  
8) Realizar empréstimo  
9) Devolver empréstimo  

*Em cadastros, digite `v` para **voltar** ao menu sem salvar.*

---

## 7. Conclusão
O projeto atende aos critérios do estudo de caso: diversidade de entidades; herança, polimorfismo, composição e agregação; persistência; e interação com o usuário por GUI e console. O sistema é extensível (novos tipos de publicação, relatórios, filtros) e serve como base sólida para trabalhos futuros.

---

## 8. Repositório e Arquivos Úteis
- **README.md** atualizado.
- **Diagramas PlantUML**: `uml_dominio.puml`, `uml_completo.puml` (este arquivo inclui os códigos abaixo).

> Gere as imagens colando o conteúdo dos blocos PlantUML acima em:  
> - https://www.planttext.com/ (recomendado)  
> - https://www.plantuml.com/plantuml  
> - https://kroki.io/