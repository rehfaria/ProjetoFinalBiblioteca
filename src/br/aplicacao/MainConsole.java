package br.aplicacao;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import br.biblioteca.modelo.Biblioteca;
import br.biblioteca.modelo.Emprestimo;
import br.biblioteca.modelo.Endereco;
import br.biblioteca.modelo.Publicacao;
import br.biblioteca.modelo.Usuario;
import br.biblioteca.servico.BibliotecaService;

public class MainConsole {

    public static void main(String[] args) {
        // Locale recomendado e compatível
        Locale.setDefault(Locale.forLanguageTag("pt-BR"));

        try (Scanner sc = new Scanner(System.in)) {
            var bib = new Biblioteca("Biblioteca", new Endereco("", "", ""));
            var service = new BibliotecaService(bib);

            // carrega acervo.dat, usuarios.dat, emprestimos.dat (se existirem)
            try {
                service.carregar();
            } catch (Exception e) {
                System.out.println(">> Aviso: não foi possível carregar todos os dados. " + e.getMessage());
            }

            loopPrincipal(sc, service);

            // salva ao sair
            try {
                service.salvar();
                System.out.println(">> Dados salvos.");
            } catch (Exception e) {
                System.out.println(">> Falha ao salvar: " + e.getMessage());
            }
        }
    }

    // ================== LOOP PRINCIPAL ==================
    private static void loopPrincipal(Scanner sc, BibliotecaService service) {
        while (true) {
            System.out.println("\n===== SISTEMA DE BIBLIOTECA (Console) =====");
            System.out.println("1) Listar acervo");
            System.out.println("2) Cadastrar acervo");
            System.out.println("3) Remover publicação por ID");
            System.out.println("4) Listar usuários");
            System.out.println("5) Cadastrar usuário (com cartão obrigatório)");
            System.out.println("6) Remover usuário por documento");
            System.out.println("7) Listar empréstimos");
            System.out.println("8) Realizar empréstimo");
            System.out.println("9) Devolver empréstimo");
            System.out.println("S) Sair");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();

            switch (op.toUpperCase()) {
                case "1" -> listarAcervo(service);
                case "2" -> menuCadastrarPublicacao(sc, service);
                case "3" -> removerPublicacao(sc, service);
                case "4" -> listarUsuarios(service);
                case "5" -> cadastrarUsuario(sc, service); // <-- com cartão obrigatório
                case "6" -> removerUsuario(sc, service);
                case "7" -> listarEmprestimos(service);
                case "8" -> realizarEmprestimo(sc, service);
                case "9" -> devolverEmprestimo(sc, service);
                case "S" -> { return; }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    // ================== ACERVO ==================
    private static void listarAcervo(BibliotecaService service) {
        System.out.println("\n=== Acervo ===");
        List<Publicacao> lista = service.getAcervo();
        if (lista.isEmpty()) {
            System.out.println("(vazio)");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.printf("%d) %s%n", i, lista.get(i).toString());
        }
    }

    /** Cadastro com opção de voltar: digite 'v' em qualquer campo para retornar ao menu. */
    private static void menuCadastrarPublicacao(Scanner sc, BibliotecaService service) {
        System.out.println("\n=== Cadastrar Publicação ===");
        System.out.println("Tipos: (1) Livro  (2) Revista  (3) Mídia Digital   [v = voltar]");
        System.out.print("Escolha o tipo: ");
        String tipo = sc.nextLine().trim();
        if (ehVoltar(tipo)) return;

        String id = lerLinhaOuVoltar(sc, "ID");
        if (id == null) return;
        String ti = lerLinhaOuVoltar(sc, "Título");
        if (ti == null) return;
        String au = lerLinhaOuVoltar(sc, "Autor");
        if (au == null) return;

        try {
            switch (tipo) {
                case "1" -> {
                    Integer paginas = lerIntPositivoOuVoltar(sc, "Páginas");
                    if (paginas == null) return;
                    service.cadastrarLivro(id, ti, au, paginas);
                }
                case "2" -> {
                    Integer edicao = lerIntPositivoOuVoltar(sc, "Edição (número)");
                    if (edicao == null) return;
                    service.cadastrarRevista(id, ti, au, edicao);
                }
                case "3" -> {
                    Double mb = lerDoublePositivoOuVoltar(sc, "Tamanho (MB)");
                    if (mb == null) return;
                    // alias compatível com PainelAcervo
                    service.cadastrarMidia(id, ti, au, mb);
                }
                default -> {
                    System.out.println("Tipo inválido. Voltando ao menu.");
                    return;
                }
            }
            service.salvar();
            System.out.println(">> Publicação cadastrada e salva.");
        } catch (Exception e) {
            System.out.println(">> Erro ao cadastrar: " + e.getMessage());
        }
    }

    private static void removerPublicacao(Scanner sc, BibliotecaService service) {
        System.out.println("\n=== Remover Publicação ===   [v = voltar]");
        String id = lerLinhaOuVoltar(sc, "ID da publicação para remover");
        if (id == null) return;

        try {
            service.removerPublicacaoPorId(id);
            service.salvar();
            System.out.println(">> Se existia, foi removida e os dados foram salvos.");
        } catch (Exception e) {
            System.out.println(">> Erro ao remover: " + e.getMessage());
        }
    }

    // ================== USUÁRIOS ==================
    private static void listarUsuarios(BibliotecaService service) {
        System.out.println("\n=== Usuários ===");
        List<Usuario> us = service.getUsuarios();
        if (us.isEmpty()) {
            System.out.println("(vazio)");
            return;
        }
        for (int i = 0; i < us.size(); i++) {
            Usuario u = us.get(i);
            System.out.printf("%d) %s | doc: %s | cartão: %s%n",
                    i,
                    u.getNome(),
                    u.getDocumento(),
                    u.getNumeroCartao() == null ? "-" : u.getNumeroCartao());
        }
    }

    /** Cadastro com opção de voltar e cartão obrigatório. */
    private static void cadastrarUsuario(Scanner sc, BibliotecaService service) {
        System.out.println("\n=== Cadastrar Usuário ===   [v = voltar]");
        String nome = lerLinhaOuVoltar(sc, "Nome");
        if (nome == null) return;

        String doc = lerLinhaOuVoltar(sc, "Documento");
        if (doc == null) return;

        // Número do cartão OBRIGATÓRIO
        String numeroCartao;
        while (true) {
            numeroCartao = lerLinhaOuVoltar(sc, "Número do cartão (obrigatório)");
            if (numeroCartao == null) return; // voltar
            if (!numeroCartao.isBlank()) break;
            System.out.println(">> O número do cartão é obrigatório.");
        }

        try {
            // 1) cadastra o usuário
            service.cadastrarUsuario(nome, doc);

            // 2) emite o cartão no objeto real (lista retorna mesmas referências)
            Usuario recem = null;
            for (Usuario u : service.getUsuarios()) {
                if (u.getDocumento().equals(doc)) { recem = u; break; }
            }
            if (recem != null) {
                if (!recem.possuiCartao()) {
                    recem.emitirCartao(numeroCartao);
                } else {
                    // se por algum motivo já existir, sobrescrevemos? aqui vamos apenas informar
                    System.out.println(">> Aviso: usuário já tinha cartão (" + recem.getNumeroCartao() + ").");
                }
            }

            service.salvar();
            System.out.println(">> Usuário cadastrado, cartão emitido e dados salvos.");
        } catch (Exception e) {
            System.out.println(">> Erro: " + e.getMessage());
        }
    }

    private static void removerUsuario(Scanner sc, BibliotecaService service) {
        System.out.println("\n=== Remover Usuário ===   [v = voltar]");
        String doc = lerLinhaOuVoltar(sc, "Documento do usuário a remover");
        if (doc == null) return;

        try {
            boolean ok = service.removerUsuarioPorDocumento(doc);
            if (ok) {
                service.salvar();
                System.out.println(">> Usuário removido e dados salvos.");
            } else {
                System.out.println(">> Nenhum usuário encontrado com esse documento.");
            }
        } catch (Exception e) {
            System.out.println(">> Erro ao remover: " + e.getMessage());
        }
    }

    // ================== EMPRÉSTIMOS ==================
    private static void listarEmprestimos(BibliotecaService service) {
        System.out.println("\n=== Empréstimos Ativos ===");
        List<Emprestimo> emps = service.getEmprestimos();
        if (emps.isEmpty()) {
            System.out.println("(nenhum)");
            return;
        }
        for (int i = 0; i < emps.size(); i++) {
            var e = emps.get(i);
            System.out.printf("%d) %s (doc: %s) | %s | prev.: %s%n",
                    i,
                    e.getUsuario().getNome(),
                    e.getUsuario().getDocumento(),
                    e.getItem().toString(),
                    e.getDataPrevista());
        }
    }

    private static void realizarEmprestimo(Scanner sc, BibliotecaService service) {
        System.out.println("\n=== Realizar Empréstimo ===   [v = voltar]");

        // selecionar usuário
        List<Usuario> usuarios = service.getUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println(">> Não há usuários cadastrados.");
            return;
        }
        for (int i = 0; i < usuarios.size(); i++) {
            System.out.printf("%d) %s (doc: %s)%n", i, usuarios.get(i).getNome(), usuarios.get(i).getDocumento());
        }
        Integer idxU = lerIndiceOuVoltar(sc, "Escolha o índice do usuário", usuarios.size());
        if (idxU == null) return;
        Usuario u = usuarios.get(idxU);

        // selecionar item
        List<Publicacao> acervo = service.getAcervo();
        if (acervo.isEmpty()) {
            System.out.println(">> Não há itens no acervo.");
            return;
        }
        for (int i = 0; i < acervo.size(); i++) {
            System.out.printf("%d) %s%n", i, acervo.get(i).toString());
        }
        Integer idxP = lerIndiceOuVoltar(sc, "Escolha o índice do item", acervo.size());
        if (idxP == null) return;
        Publicacao p = acervo.get(idxP);

        try {
            var emp = service.emprestar(u, p);
            System.out.println(">> Empréstimo realizado. Data prevista: " + emp.getDataPrevista());
            service.salvar();
        } catch (Exception e) {
            System.out.println(">> Erro: " + e.getMessage());
        }
    }

    private static void devolverEmprestimo(Scanner sc, BibliotecaService service) {
        List<Emprestimo> emps = service.getEmprestimos();
        if (emps.isEmpty()) {
            System.out.println(">> Não há empréstimos ativos.");
            return;
        }
        listarEmprestimos(service);
        Integer idx = lerIndiceOuVoltar(sc, "Escolha o índice do empréstimo para devolver", emps.size());
        if (idx == null) return;

        Emprestimo alvo = emps.get(idx);
        System.out.printf("Confirmar devolução de \"%s\" por %s? (s/N, v=voltar): ",
                alvo.getItem().toString(), alvo.getUsuario().getNome());
        String conf = sc.nextLine().trim();
        if (ehVoltar(conf)) return;
        if (!conf.equalsIgnoreCase("s")) {
            System.out.println(">> Operação cancelada.");
            return;
        }

        try {
            var dev = service.devolver(alvo);
            long atraso = dev.diasAtraso(alvo.getDataPrevista());
            double multa = dev.calcularMulta(alvo.getDataPrevista());
            System.out.println(">> Devolução concluída.");
            if (atraso > 0) {
                System.out.printf(">> Atraso: %d dia(s) | Multa: R$ %.2f%n", atraso, multa);
            }
            service.salvar();
        } catch (Exception e) {
            System.out.println(">> Erro: " + e.getMessage());
        }
    }

    // ================== UTILS (com suporte a VOLTAR) ==================
    private static boolean ehVoltar(String s) {
        return s != null && s.equalsIgnoreCase("v");
    }

    /** Lê uma linha e permite voltar com 'v'. Retorna null se o usuário quiser voltar. */
    private static String lerLinhaOuVoltar(Scanner sc, String rotulo) {
        System.out.print(rotulo + " (v=voltar): ");
        String s = sc.nextLine().trim();
        if (ehVoltar(s)) return null;
        return s;
    }

    /** Lê inteiro > 0 e permite voltar com 'v'. Retorna null para voltar. */
    private static Integer lerIntPositivoOuVoltar(Scanner sc, String rotulo) {
        while (true) {
            System.out.print(rotulo + " (v=voltar): ");
            String s = sc.nextLine().trim();
            if (ehVoltar(s)) return null;
            try {
                int v = Integer.parseInt(s);
                if (v <= 0) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException ex) {
                System.out.println("Valor inválido. Informe um número inteiro > 0.");
            }
        }
    }

    /** Lê double > 0 e permite voltar com 'v'. Retorna null para voltar. */
    private static Double lerDoublePositivoOuVoltar(Scanner sc, String rotulo) {
        while (true) {
            System.out.print(rotulo + " (v=voltar): ");
            String s = sc.nextLine().trim().replace(",", ".");
            if (ehVoltar(s)) return null;
            try {
                double v = Double.parseDouble(s);
                if (v <= 0) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException ex) {
                System.out.println("Valor inválido. Informe um número > 0.");
            }
        }
    }

    /** Lê índice válido [0..tamanho-1] e permite voltar com 'v'. Retorna null para voltar. */
    private static Integer lerIndiceOuVoltar(Scanner sc, String rotulo, int tamanhoLista) {
        while (true) {
            System.out.print(rotulo + " (0.." + (tamanhoLista - 1) + ", v=voltar): ");
            String s = sc.nextLine().trim();
            if (ehVoltar(s)) return null;
            try {
                int idx = Integer.parseInt(s);
                if (idx < 0 || idx >= tamanhoLista) {
                    System.out.println("Índice fora do intervalo.");
                } else {
                    return idx;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Índice inválido.");
            }
        }
    }
}

