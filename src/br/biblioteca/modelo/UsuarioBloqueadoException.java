package br.biblioteca.modelo;

public class UsuarioBloqueadoException extends Exception {
	private static final long serialVersionUID = 1L;
    public UsuarioBloqueadoException() {
        super("Usuário bloqueado para empréstimos.");
    }
}
