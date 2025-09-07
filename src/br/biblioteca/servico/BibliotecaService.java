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
    private final ArquivoDAO<Emprestimo> emprestimosDAO;

    public BibliotecaService(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.acervoDAO = new ArquivoDAO<>("acervo.dat");
        this.usuariosDAO = new ArquivoDAO<>("usuarios.dat");
        this.emprestimosDAO = new ArquivoDAO<>("emprestimos.dat");
    }

    // ------------ Persistência ------------
    public void carregar() throws IOException {
        biblioteca.carregarAcervo(acervoDAO.carregarLista());
        biblioteca.carregarUsuarios(usuariosDAO.carregarLista());
        biblioteca.carregarEmprestimos(emprestimosDAO.carregarLista());
    }

    public void salvar() throws IOException {
        acervoDAO.salvarLista(new ArrayList<>(biblioteca.listarAcervo()));
        usuariosDAO.salvarLista(new ArrayList<>(biblioteca.listarUsuarios()));
        emprestimosDAO.salvarLista(new ArrayList<>(biblioteca.listarEmprestimos()));
    }

    private void salvarEmprestimosSilencioso() {
        try { emprestimosDAO.salvarLista(new ArrayList<>(biblioteca.listarEmprestimos())); }
        catch (IOException e) { throw new RuntimeException("Falha ao salvar emprestimos.dat", e); }
    }

    // ------------ Consultas ------------
    public List<Publicacao> getAcervo()   { return new ArrayList<>(biblioteca.listarAcervo()); }
    public List<Usuario> getUsuarios()    { return new ArrayList<>(biblioteca.listarUsuarios()); }
    public List<Emprestimo> getEmprestimos() { return new ArrayList<>(biblioteca.listarEmprestimos()); }
    public Biblioteca getBiblioteca()     { return biblioteca; }

    // ------------ Acervo ------------
    public boolean existePublicacaoId(String id) {
        return biblioteca.listarAcervo().stream().anyMatch(p -> p.getId().equals(id));
    }

    public void removerPublicacaoPorId(String id) {
        biblioteca.buscarPublicacao(id).ifPresent(biblioteca::removerPublicacao);
        try { salvar(); } catch (IOException ignore) {}
    }

    // ------------ Usuários ------------
    public void cadastrarUsuario(String nome, String doc) {
        biblioteca.registrarUsuario(new Usuario(nome, doc));
        try { salvar(); } catch (IOException ignore) {}
    }

    public boolean removerUsuarioPorDocumento(String doc) {
        boolean removed = biblioteca.removerUsuarioPorDocumento(doc);
        if (removed) {
            try { salvar(); } catch (IOException ignore) {}
        }
        return removed;
    }

    // ------------ Empréstimos ------------
    public Emprestimo emprestar(Usuario u, Publicacao p) throws Exception {
        Emprestimo e = new Emprestimo(u, p);
        e.executar(); // valida bloqueio e define dataPrevista
        biblioteca.registrarEmprestimo(e);
        salvarEmprestimosSilencioso();
        return e;
    }

    /** Processa devolução e remove o empréstimo da lista. Retorna a Devolucao para consulta de multa/atraso. */
    public Devolucao devolver(Emprestimo e) {
        Devolucao d = new Devolucao(e.getUsuario(), e.getItem());
        try {
            d.executar(); // dataReal = hoje
        } catch (Exception ex) {
            // Transacao.executar() não lança checked aqui, mas mantemos por simetria
        }
        biblioteca.removerEmprestimo(e);
        salvarEmprestimosSilencioso();
        return d;
    }
    
 // --- Cadastro de publicações específicas ---
    public void cadastrarLivro(String id, String titulo, String autor, int paginas) {
        biblioteca.adicionarPublicacao(new Livro(id, titulo, autor, paginas));
        try { salvar(); } catch (Exception ignored) {}
    }

    public void cadastrarRevista(String id, String titulo, String autor, int edicao) {
        biblioteca.adicionarPublicacao(new Revista(id, titulo, autor, edicao));
        try { salvar(); } catch (Exception ignored) {}
    }

    public void cadastrarMidia(String id, String titulo, String autor, double tamanhoMB) {
        biblioteca.adicionarPublicacao(new MidiaDigital(id, titulo, autor, tamanhoMB));
        try { salvar(); } catch (Exception ignored) {}
    }
    

}


