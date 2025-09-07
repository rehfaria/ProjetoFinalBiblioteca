package br.biblioteca.modelo;

import java.io.Serializable;

class CartaoBiblioteca implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String numero;
    private final Usuario dono;

    CartaoBiblioteca(String numero, Usuario dono) {
        this.numero = numero;
        this.dono = dono;
    }

    public String getNumero() {
        return numero;
    }

    public Usuario getDono() {
        return dono;
    }

    @Override
    public String toString() {
        return "Cartão: " + numero + " | Usuário: " + dono.getNome();
    }
}
