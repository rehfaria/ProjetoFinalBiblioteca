package br.aplicacao;

import java.util.Scanner;

import br.biblioteca.modelo.Biblioteca;
import br.biblioteca.modelo.Endereco;
import br.biblioteca.modelo.Publicacao;
import br.biblioteca.modelo.Usuario;
import br.biblioteca.servico.BibliotecaService;

public class MainConsole {

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            var bib = new Biblioteca("Biblioteca", new Endereco("", "", ""));
            var service = new BibliotecaService(bib);

            // Carrega arquivos, se existirem
            try {
                service.carregar();
                System.out.println(">> Dados carregados de acervo.dat e usuarios.dat.");
            } catch (Exception e) {
                System.out.println(">> Nenhum arquivo de dados encontrado. Iniciando vazio.");
            }

            // Loop de menu
            while (true) {
                System.out.println("\n===== MENU =====");
                System.out.println("1) Listar acervo");
                System.out.println("2) Listar usuários");
                System.out.println("3) Cadastrar publicação");
                System.out.println("4) Cadastrar usuário");
                System.out.println("5) Remover publicação por ID");
                System.out.println("0) Salvar e sair");
                System.out.print("Escolha: ");
                String op = sc.nextLine().trim();

                switch (op) {
                    case "1" -> listarAcervo(service);
                    case "2" -> listarUsuarios(service);
                    case "3" -> cadastrarPublicacao(service, sc);
                    case "4" -> cadastrarUsuario(service, sc);
                    case "5" -> removerPublicacao(service, sc);
                    case "0" -> {
                        salvar(service);
                        System.out.println(">> Até mais!");
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            }
        }
    }

    /* ===== AÇÕES ===== */

    private static void listarAcervo(BibliotecaService service) {
        System.out.println("\n--- ACERVO ---");
        if (service.getAcervo().isEmpty()) {
            System.out.println("(vazio)");
            return;
        }
        for (Publicacao p : service.getAcervo()) {
            System.out.println(p.getId() + " | " + p.getTitulo() + " | " + p.getAutor());
        }
    }

    private static void listarUsuarios(BibliotecaService service) {
        System.out.println("\n--- USUÁRIOS ---");
        if (service.getUsuarios().isEmpty()) {
            System.out.println("(vazio)");
            return;
        }
        for (Usuario u : service.getUsuarios()) {
            var cartao = (u.getNumeroCartao() == null || u.getNumeroCartao().isBlank())
                    ? "(sem cartão)" : u.getNumeroCartao();
            System.out.println(u.getNome() + " | doc: " + u.getDocumento() + " | cartão: " + cartao);
        }
    }

    private static void cadastrarPublicacao(BibliotecaService service, Scanner sc) {
        System.out.print("Tipo (L=Livro, R=Revista, M=MidiaDigital): ");
        String tipo = sc.nextLine().trim().toUpperCase();

        System.out.print("ID: ");     String id = sc.nextLine().trim();
        System.out.print("Título: "); String titulo = sc.nextLine().trim();
        System.out.print("Autor: ");  String autor = sc.nextLine().trim();

        try {
            switch (tipo) {
                case "L" -> {
                    System.out.print("Páginas: ");
                    int pag = Integer.parseInt(sc.nextLine());
                    service.cadastrarLivro(id, titulo, autor, pag);
                }
                case "R" -> {
                    System.out.print("Edição: ");
                    int ed = Integer.parseInt(sc.nextLine());
                    service.cadastrarRevista(id, titulo, autor, ed);
                }
                case "M" -> {
                    System.out.print("Tamanho (MB): ");
                    double mb = Double.parseDouble(sc.nextLine());
                    service.cadastrarMidia(id, titulo, autor, mb);
                }
                default -> {
                    System.out.println("Tipo inválido. Use L, R ou M.");
                    return;
                }
            }
            System.out.println(">> Publicação cadastrada.");
            salvar(service); // salva a cada cadastro para não perder
        } catch (NumberFormatException nfe) {
            System.out.println("Valor numérico inválido.");
        } catch (Exception ex) {
            System.out.println("Erro no cadastro: " + ex.getMessage());
        }
    }

    private static void cadastrarUsuario(BibliotecaService service, Scanner sc) {
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();
        System.out.print("Documento: ");
        String doc = sc.nextLine().trim();

        try {
            service.cadastrarUsuario(nome, doc);
            System.out.println(">> Usuário cadastrado.");
            salvar(service); // salva a cada cadastro
        } catch (Exception ex) {
            System.out.println("Erro no cadastro: " + ex.getMessage());
        }
    }

    private static void removerPublicacao(BibliotecaService service, Scanner sc) {
        System.out.print("ID da publicação para remover: ");
        String id = sc.nextLine().trim();
        service.removerPublicacaoPorId(id);
        System.out.println(">> Se existia, foi removida.");
        salvar(service);
    }

    private static void salvar(BibliotecaService service) {
        try {
            service.salvar();
        } catch (Exception e) {
            System.out.println("Falha ao salvar: " + e.getMessage());
        }
    }
}
