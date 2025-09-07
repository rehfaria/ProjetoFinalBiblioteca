package br.biblioteca.ui;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import br.biblioteca.modelo.Publicacao;

public class AcervoTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final String[] cols = {"ID","Título","Autor","Tipo"};
    private List<Publicacao> dados;

    public AcervoTableModel(List<Publicacao> dados) { this.dados = dados; }

    public void setDados(List<Publicacao> dados) { this.dados = dados; fireTableDataChanged(); }

    @Override public int getRowCount() { return dados == null ? 0 : dados.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }

    @Override public Object getValueAt(int r, int c) {
        Publicacao p = dados.get(r);
        return switch (c) {
            case 0 -> p.getId();
            case 1 -> p.getTitulo();
            case 2 -> p.getAutor();
            case 3 -> p.getClass().getSimpleName();
            default -> "";
        };
    }
}
