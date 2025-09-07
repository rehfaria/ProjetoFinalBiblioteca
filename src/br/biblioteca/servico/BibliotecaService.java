package br.biblioteca.servico;

import br.biblioteca.dao.ArquivoDAO;
import br.biblioteca.modelo.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BibliotecaService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Biblioteca biblioteca;
    private final ArquivoDAO<Publicacao> acervoDAO;
    private final ArquivoDAO<Usuario> usuariosDAO;

    public BibliotecaService(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.acervoDAO = new ArquivoDAO<>("acervo.dat");
        this.usuariosDAO = new ArquivoDAO<>("usuarios.dat");
    }

    public void carregar() throws IOException {
        acervoDAO.carregarLista().forEach(biblioteca::adicionarPublicacao);
        usuariosDAO.carregarLista().forEach(biblioteca::registrarUsuario);
    }

    public void salvar() throws IOException {
        acervoDAO.salvarLista(new ArrayList<>(biblioteca.listarAcervo()));
        usuariosDAO.salvarLista(new ArrayList<>(biblioteca.listarUsuarios()));
    }

    public void cadastrarLivro(String id, String titulo, String autor, int paginas) {
        biblioteca.adicionarPublicacao(new Livro(id, titulo, autor, paginas));
    }
    public void cadastrarRevista(String id, String titulo, String autor, int edicao) {
        biblioteca.adicionarPublicacao(new Revista(id, titulo, autor, edicao));
    }
    public void cadastrarMidia(String id, String titulo, String autor, double tamanhoMB) {
        biblioteca.adicionarPublicacao(new MidiaDigital(id, titulo, autor, tamanhoMB));
    }
    public void cadastrarUsuario(String nome, String doc) {
        biblioteca.registrarUsuario(new Usuario(nome, doc));
    }

    public List<Publicacao> getAcervo()   { return new ArrayList<>(biblioteca.listarAcervo()); }
    public List<Usuario> getUsuarios()    { return new ArrayList<>(biblioteca.listarUsuarios()); }
    public Biblioteca getBiblioteca()     { return biblioteca; }

    public boolean existePublicacaoId(String id) {
        return biblioteca.listarAcervo().stream().anyMatch(p -> p.getId().equals(id));
    }

    public void removerPublicacaoPorId(String id) {
        biblioteca.buscarPublicacao(id).ifPresent(biblioteca::removerPublicacao);
    }

}


