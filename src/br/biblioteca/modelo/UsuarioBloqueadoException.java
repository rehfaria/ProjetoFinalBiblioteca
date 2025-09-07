package br.biblioteca.modelo;

public class UsuarioBloqueadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public UsuarioBloqueadoException() { super("Usuário bloqueado para empréstimos."); }
}
