package br.aplicacao;

import br.biblioteca.modelo.*;
import br.biblioteca.servico.BibliotecaService;

import java.time.LocalDate;

public class MainConsole {
    public static void main(String[] args) throws Exception {
        Biblioteca biblioteca = new Biblioteca(
            "Biblioteca Municipal",
            new Endereco("Av. Central", "1000", "Uberlândia")
        );

        BibliotecaService service = new BibliotecaService(biblioteca);

        // Carregar dados salvos, se existirem
        service.carregar();

        // Cadastrar publicações, se necessário
        if (biblioteca.listarAcervo().isEmpty()) {
            service.cadastrarLivro("L1", "Clean Code", "Robert C. Martin", 464);
            service.cadastrarRevista("R1", "IEEE Software", "IEEE", 120);
            service.cadastrarMidia("M1", "Arquitetura Java", "Alura", 250);
        }

        // Cadastrar usuários, se necessário
        if (biblioteca.listarUsuarios().isEmpty()) {
            service.cadastrarUsuario("Renata Faria", "111");
            service.cadastrarUsuario("João Silva", "222");
        }

        // Emitir cartão para Renata, se ainda não tiver
        Usuario renata = biblioteca.buscarUsuarioPorDoc("111").orElseThrow();
        if (!renata.possuiCartao()) {
            renata.emitirCartao("0001");
        }

        // Criar um empréstimo
        Publicacao livro = biblioteca.buscarPublicacao("L1").orElseThrow();
        Emprestimo emp = new Emprestimo(renata, livro);
        emp.executar();
        System.out.println("Data prevista de devolução: " + emp.getDataPrevista());

        // Simular devolução atrasada
        Devolucao dev = new Devolucao(renata, livro);
        dev.executar();
        double multa = dev.calcularMulta(LocalDate.now().minusDays(5));
        System.out.println("Multa simulada: R$ " + multa);

        // Salvar alterações
        service.salvar();
        System.out.println("Dados salvos com sucesso!");
    }
}

