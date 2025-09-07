package br.biblioteca.servico;

import br.biblioteca.dao.ArquivoDAO;
import br.biblioteca.modelo.Biblioteca;
import br.biblioteca.modelo.Devolucao;
import br.biblioteca.modelo.Emprestimo;
import br.biblioteca.modelo.Livro;
import br.biblioteca.modelo.MidiaDigital;
import br.biblioteca.modelo.Publicacao;
import br.biblioteca.modelo.Revista;
import br.biblioteca.modelo.Usuario;
import br.biblioteca.modelo.UsuarioBloqueadoException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Camada de serviço da Biblioteca.
 * Persiste dados via ArquivoDAO (*.dat) e centraliza regras simples.
 */
public class BibliotecaService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Biblioteca biblioteca;

    // DAOs (serialização em arquivos .dat)
    private final ArquivoDAO<Publicacao> acervoDAO      = new ArquivoDAO<>("acervo.dat");
    private final ArquivoDAO<Usuario>    usuariosDAO    = new ArquivoDAO<>("usuarios.dat");
    private final ArquivoDAO<Emprestimo> emprestimosDAO = new ArquivoDAO<>("emprestimos.dat");

    // empréstimos ativos em memória
    private final List<Emprestimo> emprestimos = new ArrayList<>();

    public BibliotecaService(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    // =========================================================
    // Persistência
    // =========================================================

    /** Carrega listas dos arquivos .dat e injeta no domínio. */
    public void carregar() throws IOException {
        for (Publicacao p : acervoDAO.carregarLista()) {
            biblioteca.adicionarPublicacao(p);
        }
        for (Usuario u : usuariosDAO.carregarLista()) {
            biblioteca.registrarUsuario(u);
        }
        emprestimos.clear();
        emprestimos.addAll(emprestimosDAO.carregarLista());
    }

    /** Salva acervo, usuários e empréstimos nos arquivos .dat. */
    public void salvar() throws IOException {
        acervoDAO.salvarLista(new ArrayList<>(biblioteca.listarAcervo()));
        usuariosDAO.salvarLista(new ArrayList<>(biblioteca.listarUsuarios()));
        emprestimosDAO.salvarLista(new ArrayList<>(emprestimos));
    }

    // =========================================================
    // Consultas
    // =========================================================
    public List<Publicacao> getAcervo()      { return biblioteca.listarAcervo(); }
    public List<Usuario>    getUsuarios()    { return biblioteca.listarUsuarios(); }
    public List<Emprestimo> getEmprestimos() { return List.copyOf(emprestimos); }

    // =========================================================
    // Acervo
    // =========================================================
    public void cadastrarLivro(String id, String titulo, String autor, int paginas) {
        validarTexto("ID", id);
        validarTexto("Título", titulo);
        validarTexto("Autor", autor);
        if (paginas <= 0) throw new IllegalArgumentException("Páginas deve ser > 0.");
        impedirIdDuplicado(id);

        biblioteca.adicionarPublicacao(new Livro(id, titulo, autor, paginas));
        tentarSalvarSilencioso();
    }

    public void cadastrarRevista(String id, String titulo, String autor, int edicao) {
        validarTexto("ID", id);
        validarTexto("Título", titulo);
        validarTexto("Autor", autor);
        if (edicao <= 0) throw new IllegalArgumentException("Edição deve ser > 0.");
        impedirIdDuplicado(id);

        biblioteca.adicionarPublicacao(new Revista(id, titulo, autor, edicao));
        tentarSalvarSilencioso();
    }

    public void cadastrarMidia(String id, String titulo, String autor, double tamanhoMB) {
        validarTexto("ID", id);
        validarTexto("Título", titulo);
        validarTexto("Autor", autor);
        if (tamanhoMB <= 0) throw new IllegalArgumentException("Tamanho (MB) deve ser > 0.");
        impedirIdDuplicado(id);

        biblioteca.adicionarPublicacao(new MidiaDigital(id, titulo, autor, tamanhoMB));
        tentarSalvarSilencioso();
    }

    public void removerPublicacaoPorId(String id) {
        var pub = biblioteca.buscarPublicacao(id)
                .orElseThrow(() -> new IllegalArgumentException("Publicação não encontrada."));

        // não remover se estiver emprestada
        boolean emprestada = emprestimos.stream()
                .anyMatch(e -> e.getItem().getId().equals(id));
        if (emprestada) throw new IllegalStateException("Publicação está emprestada e não pode ser removida.");

        biblioteca.removerPublicacao(pub);
        tentarSalvarSilencioso();
    }

    // =========================================================
    // Usuários
    // =========================================================

    /** Cadastro de usuário com cartão obrigatório. */
    public void cadastrarUsuario(String nome, String doc, String numeroCartao) {
        validarTexto("Nome", nome);
        validarTexto("Documento", doc);
        validarTexto("Cartão", numeroCartao);

        boolean docRepetido = biblioteca.listarUsuarios().stream()
                .anyMatch(u -> u.getDocumento().equals(doc));
        if (docRepetido) throw new IllegalArgumentException("Já existe usuário com este documento.");

        Usuario u = new Usuario(nome, doc, numeroCartao);
        biblioteca.registrarUsuario(u);
        tentarSalvarSilencioso();
    }

    /** Mantido apenas para compatibilidade — evita uso acidental. */
    @Deprecated
    public void cadastrarUsuario(String nome, String doc) {
        throw new UnsupportedOperationException("Use cadastrarUsuario(nome, doc, numeroCartao). O cartão é obrigatório.");
    }

    public boolean removerUsuarioPorDocumento(String doc) {
        var opt = biblioteca.buscarUsuarioPorDoc(doc);
        if (opt.isEmpty()) return false;

        // não remover se tiver empréstimo ativo
        boolean temEmprestimo = emprestimos.stream()
                .anyMatch(e -> e.getUsuario().getDocumento().equals(doc));
        if (temEmprestimo) throw new IllegalStateException("Usuário com empréstimo ativo não pode ser removido.");

        // A Biblioteca deve expor um método de remoção de usuário.
        // Caso ainda não exista, adicione em Biblioteca: public void removerUsuario(Usuario u) { usuarios.remove(u); }
        biblioteca.removerUsuario(opt.get());
        tentarSalvarSilencioso();
        return true;
    }

    // =========================================================
    // Empréstimos
    // =========================================================

    public Emprestimo emprestar(Usuario u, Publicacao p) {
        if (u == null || p == null) throw new IllegalArgumentException("Usuário e publicação são obrigatórios.");

        // Normaliza para os objetos oficiais do cadastro
        var usuario = biblioteca.buscarUsuarioPorDoc(u.getDocumento())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        var publicacao = biblioteca.buscarPublicacao(p.getId())
                .orElseThrow(() -> new IllegalArgumentException("Publicação não encontrada."));

        // Impede empréstimo duplicado do mesmo item
        boolean itemEmprestado = emprestimos.stream()
                .anyMatch(e -> e.getItem().getId().equals(publicacao.getId()));
        if (itemEmprestado) throw new IllegalStateException("Item indisponível (já emprestado).");

        var emp = new Emprestimo(usuario, publicacao);
        try {
            emp.executar(); // pode lançar UsuarioBloqueadoException
            emprestimos.add(emp);
            salvar();
            return emp;
        } catch (UsuarioBloqueadoException ub) {
            throw ub; // repassa mensagem clara
        } catch (Exception ex) {
            throw new RuntimeException("Falha ao emprestar: " + ex.getMessage(), ex);
        }
    }

    public Devolucao devolver(Emprestimo alvo) {
        if (alvo == null) throw new IllegalArgumentException("Empréstimo inválido.");

        int idx = -1;
        for (int i = 0; i < emprestimos.size(); i++) {
            var e = emprestimos.get(i);
            if (e.getUsuario().getDocumento().equals(alvo.getUsuario().getDocumento())
                    && e.getItem().getId().equals(alvo.getItem().getId())) {
                idx = i; break;
            }
        }
        if (idx < 0) throw new IllegalArgumentException("Empréstimo não encontrado.");

        var dev = new Devolucao(alvo.getUsuario(), alvo.getItem());
        try {
            dev.executar();
            emprestimos.remove(idx);
            salvar();
            return dev;
        } catch (Exception ex) {
            throw new RuntimeException("Falha ao devolver: " + ex.getMessage(), ex);
        }
    }

    // =========================================================
    // Helpers
    // =========================================================
    private void validarTexto(String campo, String valor) {
        if (valor == null || valor.isBlank())
            throw new IllegalArgumentException(campo + " inválido/obrigatório.");
    }

    private void impedirIdDuplicado(String id) {
        boolean idRepetido = biblioteca.listarAcervo().stream()
                .anyMatch(p -> p.getId().equals(id));
        if (idRepetido) throw new IllegalArgumentException("Já existe publicação com esse ID.");
    }

    private void tentarSalvarSilencioso() {
        try { salvar(); } catch (Exception ignored) {}
    }
    /** Verifica se já existe uma publicação com o ID informado. */
    public boolean existePublicacaoId(String id) {
        return biblioteca.listarAcervo().stream()
                .anyMatch(p -> p.getId().equalsIgnoreCase(id));
    }

}

