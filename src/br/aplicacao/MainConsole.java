package br.aplicacao;

import br.biblioteca.modelo.Biblioteca;
import br.biblioteca.modelo.Emprestimo;
import br.biblioteca.modelo.Endereco;
import br.biblioteca.modelo.Publicacao;
import br.biblioteca.modelo.Usuario;
import br.biblioteca.modelo.UsuarioBloqueadoException;
import br.biblioteca.servico.BibliotecaService;

import java.util.List;

import java.util.Scanner;

public class MainConsole {

    private static final Scanner SC = new Scanner(System.in);
    private static BibliotecaService service;

    public static void main(String[] args) {
        try {
            var bib = new Biblioteca("Biblioteca", new Endereco("Rua X", "100", "Uberlândia"));
            service = new BibliotecaService(bib);

            try { service.carregar(); } catch (Exception ignored) {}

            int op;
            do {
                mostrarMenu();
                op = lerInt("Escolha uma opção: ");
                switch (op) {
                    case 1 -> listarAcervo();
                    case 2 -> cadastrarAcervo();
                    case 3 -> removerPublicacaoPorId();
                    case 4 -> listarUsuarios();
                    case 5 -> cadastrarUsuarioComCartao();
                    case 6 -> removerUsuario();
                    case 7 -> bloquearOuDesbloquearUsuario(); 
                    case 8 -> listarEmprestimos();
                    case 9 -> realizarEmprestimo();
                    case 10 -> devolverEmprestimo();
                    case 0 -> System.out.println("Saindo...");
                    default -> System.out.println("Opção inválida.");
                }
            } while (op != 0);

            try { service.salvar(); } catch (Exception ignored) {}
        } catch (Throwable t) {
            System.out.println("Falha: " + t.getMessage());
        }
    }

    // ========================= MENU =========================

    private static void mostrarMenu() {
        System.out.println("\n===== SISTEMA DE BIBLIOTECA =====");
        System.out.println("1) Listar acervo");
        System.out.println("2) Cadastrar acervo");
        System.out.println("3) Remover publicação por ID");
        System.out.println("4) Listar usuários");
        System.out.println("5) Cadastrar usuário");
        System.out.println("6) Remover usuário");
        System.out.println("7) Bloquear / Desbloquear usuário");
        System.out.println("8) Listar empréstimos");
        System.out.println("9) Realizar empréstimo");
        System.out.println("10) Devolver empréstimo");
        System.out.println("0) Sair");
    }

    // ====================== OPÇÕES ==========================

    private static void listarAcervo() {
        System.out.println("\n--- Acervo ---");
        List<Publicacao> acervo = service.getAcervo();
        if (acervo.isEmpty()) {
            System.out.println("(vazio)");
            return;
        }
        acervo.forEach(System.out::println);
    }

    private static void cadastrarAcervo() {
        System.out.println("\n--- Cadastro de Publicações (v = voltar) ---");
        String id = lerLinha("ID: ");
        if (ehVoltar(id)) return;

        String titulo = lerLinha("Título: ");
        if (ehVoltar(titulo)) return;

        String autor = lerLinha("Autor: ");
        if (ehVoltar(autor)) return;

        System.out.println("Tipo: 1=Livro, 2=Revista, 3=Mídia Digital");
        int tipo = lerInt("Escolha: ");
        try {
            switch (tipo) {
                case 1 -> {
                    int paginas = lerInt("Páginas: ");
                    service.cadastrarLivro(id, titulo, autor, paginas);
                }
                case 2 -> {
                    int edicao = lerInt("Edição: ");
                    service.cadastrarRevista(id, titulo, autor, edicao);
                }
                case 3 -> {
                    double mb = lerDouble("Tamanho (MB): ");
                    // método do service foi padronizado como cadastrarMidia(...)
                    service.cadastrarMidia(id, titulo, autor, mb);
                }
                default -> {
                    System.out.println("Tipo inválido.");
                    return;
                }
            }
            System.out.println("Publicação cadastrada com sucesso!");
        } catch (Exception ex) {
            System.out.println("Erro: " + ex.getMessage());
        }
    }

    private static void removerPublicacaoPorId() {
        System.out.println("\n--- Remover Publicação ---");
        String id = lerLinha("ID da publicação: ");
        if (ehVoltar(id)) return;

        try {
            service.removerPublicacaoPorId(id);
            System.out.println("Publicação removida com sucesso!");
        } catch (Exception ex) {
            System.out.println("Erro: " + ex.getMessage());
        }
    }

    private static void listarUsuarios() {
        System.out.println("\n--- Usuários ---");
        var usuarios = service.getUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("(nenhum usuário cadastrado)");
            return;
        }
        usuarios.forEach(u -> System.out.println(
            u.getNome() + " (" + u.getDocumento() + ")"
            + " | Cartão: " + u.getNumeroCartao()
            + " | Bloqueado: " + (u.isBloqueado() ? "Sim" : "Não")
        ));
    }


    private static void cadastrarUsuarioComCartao() {
        System.out.println("\n--- Cadastrar Usuário (v = voltar) ---");
        String nome = lerLinha("Nome: ");
        if (ehVoltar(nome)) return;

        String doc = lerLinha("Documento: ");
        if (ehVoltar(doc)) return;

        String cartao = lerLinha("Número do cartão: ");
        if (ehVoltar(cartao)) return;

        try {
    
            service.cadastrarUsuario(nome, doc, cartao);
            System.out.println("Usuário cadastrado com sucesso!");
            
        } catch (Exception ex) {
            System.out.println("Erro: " + ex.getMessage());
        }
    }

    private static void removerUsuario() {
        System.out.println("\n--- Remover Usuário ---");
        String doc = lerLinha("Documento do usuário: ");
        if (ehVoltar(doc)) return;

        try {
            boolean ok = service.removerUsuarioPorDocumento(doc);
            if (ok) System.out.println("Usuário removido com sucesso!");
            else    System.out.println("Nenhum usuário removido (não encontrado).");
        } catch (Exception ex) {
            System.out.println("Erro: " + ex.getMessage());
        }
    }

    private static void bloquearOuDesbloquearUsuario() {
        System.out.println("\n--- Bloquear/Desbloquear Usuário ---");
        String doc = lerLinha("Documento do usuário: ");
        if (ehVoltar(doc)) return;

        try {
            Usuario u = service.getUsuarios().stream()
                    .filter(x -> x.getDocumento().equals(doc))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            if (u.isBloqueado()) {
                u.desbloquear();
                System.out.println("Usuário DESBLOQUEADO com sucesso!");
            } else {
                u.bloquear();
                System.out.println("Usuário BLOQUEADO com sucesso!");
            }
            service.salvar();
        } catch (Exception ex) {
            System.out.println("Erro: " + ex.getMessage());
        }
    }

    private static void listarEmprestimos() {
        System.out.println("\n--- Empréstimos Ativos ---");
        List<Emprestimo> lista = service.getEmprestimos();
        if (lista.isEmpty()) {
            System.out.println("(nenhum empréstimo ativo)");
            return;
        }
        lista.forEach(e ->
                System.out.println("Usuário: " + e.getUsuario().getNome()
                        + " | Doc: " + e.getUsuario().getDocumento()
                        + " | Item: " + e.getItem()
                        + " | Devolver até: " + e.getDataPrevista()));
    }

    private static void realizarEmprestimo() {
        System.out.println("\n--- Realizar Empréstimo (v = voltar) ---");
        String doc = lerLinha("Documento do usuário: ");
        if (ehVoltar(doc)) return;

        String id = lerLinha("ID da publicação: ");
        if (ehVoltar(id)) return;

        try {
            Usuario u = service.getUsuarios().stream()
                    .filter(x -> x.getDocumento().equals(doc))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            Publicacao p = service.getAcervo().stream()
                    .filter(x -> x.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Publicação não encontrada."));

            service.emprestar(u, p);
            System.out.println("Empréstimo realizado com sucesso!");
        } catch (UsuarioBloqueadoException ub) {
            System.out.println("Erro: " + ub.getMessage());
        } catch (Exception ex) {
            System.out.println("Erro: " + ex.getMessage());
        }
    }

    private static void devolverEmprestimo() {
        System.out.println("\n--- Devolver Empréstimo (v = voltar) ---");
        String doc = lerLinha("Documento do usuário: ");
        if (ehVoltar(doc)) return;

        String id = lerLinha("ID da publicação: ");
        if (ehVoltar(id)) return;

        try {
            Emprestimo emp = service.getEmprestimos().stream()
                    .filter(e -> e.getUsuario().getDocumento().equals(doc)
                            && e.getItem().getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado."));

            var dev = service.devolver(emp);
            System.out.println("Devolução registrada!");
            long atraso = dev.diasAtraso(emp.getDataPrevista());
            if (atraso > 0) {
                double multa = dev.calcularMulta(emp.getDataPrevista());
                System.out.printf("Atraso: %d dia(s) | Multa: R$ %.2f%n", atraso, multa);
            }
        } catch (Exception ex) {
            System.out.println("Erro: " + ex.getMessage());
        }
    }

    // =================== UTILITÁRIOS =======================

    private static boolean ehVoltar(String s) {
        return s != null && s.trim().equalsIgnoreCase("v");
    }

    private static String lerLinha(String rotulo) {
        System.out.print(rotulo);
        return SC.nextLine().trim();
    }

    private static int lerInt(String rotulo) {
        while (true) {
            try {
                System.out.print(rotulo);
                String s = SC.nextLine().trim();
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Tente novamente.");
            }
        }
    }

    private static double lerDouble(String rotulo) {
        while (true) {
            try {
                System.out.print(rotulo);
                String s = SC.nextLine().trim();
                return Double.parseDouble(s.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Tente novamente.");
            }
        }
    }
}

