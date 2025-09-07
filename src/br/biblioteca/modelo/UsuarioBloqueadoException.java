package br.biblioteca.modelo;

public class UsuarioBloqueadoException extends Exception {
    public UsuarioBloqueadoException() {
        super("Usuário bloqueado para empréstimos.");
    }
}
