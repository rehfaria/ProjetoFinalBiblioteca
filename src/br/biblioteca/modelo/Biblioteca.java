package br.biblioteca.modelo;
import java.io.Serializable;
import java.util.*;

public class Biblioteca implements Serializable {
    private final String nome;
    private final Endereco endereco;
    private final List<Publicacao> acervo = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();

    public Biblioteca(String nome, Endereco endereco) {
        this.nome = nome;
        this.endereco = endereco;
    }

    public void adicionarPublicacao(Publicacao p) { acervo.add(p); }
    
    public void registrarUsuario(Usuario u) { usuarios.add(u); }
    public List<Publicacao> listarAcervo() { return List.copyOf(acervo); }
    public List<Usuario> listarUsuarios() { return List.copyOf(usuarios); }

    public Optional<Publicacao> buscarPublicacao(String id) {
        return acervo.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Optional<Usuario> buscarUsuarioPorDoc(String doc) {
        return usuarios.stream().filter(u -> u.getDocumento().equals(doc)).findFirst();
    }
    public void removerPublicacao(Publicacao p) {
        acervo.remove(p);
    }

}
