package br.biblioteca.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Biblioteca implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nome;
    private final Endereco endereco;
    private final List<Publicacao> acervo = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();

    public Biblioteca(String nome, Endereco endereco) {
        this.nome = nome;
        this.endereco = endereco;
    }

    // Getters adicionados para evitar warnings
    public String getNome() {
        return nome;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void adicionarPublicacao(Publicacao p) { acervo.add(p); }
    public void removerPublicacao(Publicacao p)   { acervo.remove(p); }

    public void registrarUsuario(Usuario u) { usuarios.add(u); }
    public void removerUsuario(Usuario u)   { usuarios.remove(u); }

    public List<Publicacao> listarAcervo() { return List.copyOf(acervo); }
    public List<Usuario> listarUsuarios()   { return List.copyOf(usuarios); }

    public Optional<Publicacao> buscarPublicacao(String id) {
        return acervo.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Optional<Usuario> buscarUsuarioPorDoc(String doc) {
        return usuarios.stream().filter(u -> u.getDocumento().equals(doc)).findFirst();
    }
}


