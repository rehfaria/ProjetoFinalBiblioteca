package br.biblioteca.ui;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import br.biblioteca.modelo.Usuario;

public class UsuariosTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    // Agora só 3 colunas: Nome, Documento e Cartão
    private final String[] cols = {"Nome", "Documento", "Cartão"};
    private List<Usuario> dados;

    public UsuariosTableModel(List<Usuario> dados) {
        this.dados = dados;
    }

    public void setDados(List<Usuario> d) {
        this.dados = d;
        fireTableDataChanged();
    }

    /** Retorna o Usuario na linha do MODEL (não da view). */
    public Usuario getAt(int modelRow) {
        return dados.get(modelRow);
    }

    @Override
    public int getRowCount() {
        return dados == null ? 0 : dados.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int c) {
        return cols[c];
    }

    @Override
    public Object getValueAt(int r, int c) {
        Usuario u = dados.get(r);
        return switch (c) {
            case 0 -> u.getNome();
            case 1 -> u.getDocumento();
            case 2 -> u.getNumeroCartao(); // Mostra o número do cartão, obrigatório
            default -> "";
        };
    }
}
