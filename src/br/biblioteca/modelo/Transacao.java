package br.biblioteca.modelo;
import java.io.Serializable;
import java.time.LocalDate;

public abstract class Transacao implements Serializable {
    protected final LocalDate data = LocalDate.now();
    public abstract void executar() throws Exception;
}
