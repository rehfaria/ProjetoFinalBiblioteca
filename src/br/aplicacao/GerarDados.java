package br.aplicacao;

import br.biblioteca.modelo.*;
import br.biblioteca.servico.BibliotecaService;

public class GerarDados {
    public static void main(String[] args) {
        try {
            // Biblioteca base
            Endereco end = new Endereco("Av. Central", "1000", "Uberlândia");
            Biblioteca bib = new Biblioteca("Biblioteca Municipal", end);
            BibliotecaService service = new BibliotecaService(bib);

            // ===== ACERVO (vários itens simulados) =====
            // Livros
            service.cadastrarLivro("L001", "Padrões de Projeto", "Gamma et al.", 395);
            service.cadastrarLivro("L002", "Clean Code", "Robert C. Martin", 464);
            service.cadastrarLivro("L003", "Java Efetivo", "Joshua Bloch", 416);
            service.cadastrarLivro("L004", "Estruturas de Dados em Java", "Goodrich", 720);
            service.cadastrarLivro("L005", "Algoritmos", "Sedgewick & Wayne", 992);

            // Revistas (usei “autor” como editora/entidade)
            service.cadastrarRevista("R001", "Revista Ciência Hoje", "SBPC", 321);
            service.cadastrarRevista("R002", "ACM Communications", "ACM", 68);
            service.cadastrarRevista("R003", "IEEE Software", "IEEE", 40);

            // Mídias Digitais
            service.cadastrarMidia("M001", "Introdução a POO (vídeo)", "N/A", 650.0);
            service.cadastrarMidia("M002", "Banco de Dados – Aula 1", "N/A", 480.0);
            service.cadastrarMidia("M003", "Estruturas – Playlist", "N/A", 1024.0);

            // ===== USUÁRIOS (vários simulados) =====
            service.cadastrarUsuario("Renata Faria", "111");
            service.cadastrarUsuario("Thamela Oliveira", "222");
            service.cadastrarUsuario("Keila Almeida", "333");
            service.cadastrarUsuario("Carlos Silva", "444");
            service.cadastrarUsuario("Mariana Souza", "555");
            service.cadastrarUsuario("João Pedro Lima", "666");

            // Grava os dois arquivos .dat na raiz do projeto
            service.salvar();

            System.out.println("✅ Arquivos gerados com sucesso: acervo.dat e usuarios.dat (na raiz do projeto).");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Falha ao gerar dados: " + e.getMessage());
        }
    }
}
