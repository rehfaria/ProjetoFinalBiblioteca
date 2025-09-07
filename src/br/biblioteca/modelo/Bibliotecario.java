package br.biblioteca.modelo;
public class Bibliotecario extends Pessoa {
    public Bibliotecario(String nome, String doc) {
        super(nome, doc);
    }

    public void autorizarDesbloqueio(Usuario u) {
        u.desbloquear();
    }
}
