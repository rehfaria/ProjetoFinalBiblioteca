package br.biblioteca.modelo;

import java.io.Serializable;
import java.util.*;

public class Biblioteca implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nome;
    private final Endereco endereco;

    private final List<Publicacao> acervo   = new ArrayList<>();
    private final List<Usuario>    usuarios = new ArrayList<>();
    private final List<Emprestimo> emprestimos = new ArrayList<>();

    public Biblioteca(String nome, Endereco endereco) {
        this.nome = nome;
        this.endereco = endereco;
    }

    // --- Acervo ---
    public void adicionarPublicacao(Publicacao p) { acervo.add(p); }
    public void removerPublicacao(Publicacao p)   { acervo.remove(p); }
    public List<Publicacao> listarAcervo()        { return List.copyOf(acervo); }

    public Optional<Publicacao> buscarPublicacao(String id) {
        return acervo.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    // --- Usuários ---
    public void registrarUsuario(Usuario u)       { usuarios.add(u); }
    public List<Usuario> listarUsuarios()         { return List.copyOf(usuarios); }

    public Optional<Usuario> buscarUsuarioPorDoc(String doc) {
        return usuarios.stream().filter(u -> u.getDocumento().equals(doc)).findFirst();
    }

    public boolean removerUsuarioPorDocumento(String doc) {
        return buscarUsuarioPorDoc(doc).map(usuarios::remove).orElse(false);
    }

    // --- Empréstimos ---
    public void registrarEmprestimo(Emprestimo e) { emprestimos.add(e); }
    public void removerEmprestimo(Emprestimo e)   { emprestimos.remove(e); }
    public List<Emprestimo> listarEmprestimos()   { return List.copyOf(emprestimos); }

    // --- Suporte a carga (limpa e recarrega as listas) ---
    public void carregarAcervo(List<Publicacao> dados) {
        acervo.clear();
        if (dados != null) acervo.addAll(dados);
    }
    public void carregarUsuarios(List<Usuario> dados) {
        usuarios.clear();
        if (dados != null) usuarios.addAll(dados);
    }
    public void carregarEmprestimos(List<Emprestimo> dados) {
        emprestimos.clear();
        if (dados != null) emprestimos.addAll(dados);
    }

    // Getters opcionais (podem ser úteis)
    public String getNome() { return nome; }
    public Endereco getEndereco() { return endereco; }
}
