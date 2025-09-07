package br.biblioteca.modelo;
import java.io.Serializable;
import java.time.LocalDate;

public abstract class Transacao implements Serializable {
	private static final long serialVersionUID = 1L;
    protected final LocalDate data = LocalDate.now();
    public abstract void executar() throws Exception;
}
