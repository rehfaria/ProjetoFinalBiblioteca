package br.biblioteca.modelo;

import java.io.Serializable;

public class Usuario extends Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CartaoBiblioteca cartao; // cartão obrigatório no cadastro
    private boolean bloqueado = false;     // todo usuário nasce desbloqueado

    public Usuario(String nome, String doc, String numeroCartao) {
        super(nome, doc);
        if (numeroCartao == null || numeroCartao.isBlank()) {
            throw new IllegalArgumentException("O número do cartão é obrigatório.");
        }
        this.cartao = new CartaoBiblioteca(numeroCartao, this);
    }

    // cartão obrigatório → sempre existe
    public String getNumeroCartao() {
        return cartao.getNumero();
    }

    // BLOQUEIO
    public boolean isBloqueado() {
        return bloqueado;
    }

    public void bloquear() {
        bloqueado = true;
    }

    public void desbloquear() {
        bloqueado = false;
    }

    @Override
    public String toString() {
        return getNome() + " (" + getDocumento() + ") - Cartão: " + getNumeroCartao();
    }
}
