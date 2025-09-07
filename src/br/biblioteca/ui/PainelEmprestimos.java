package br.biblioteca.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import br.biblioteca.modelo.Devolucao;
import br.biblioteca.modelo.Emprestimo;
import br.biblioteca.modelo.Publicacao;
import br.biblioteca.modelo.Usuario;
import br.biblioteca.servico.BibliotecaService;

public class PainelEmprestimos extends JPanel {
    private static final long serialVersionUID = 1L;

    private final BibliotecaService service;

    private final JComboBox<Usuario> cbUsuario = new JComboBox<>();
    private final JComboBox<Publicacao> cbItem = new JComboBox<>();

    private final DefaultTableModel tm =
        new DefaultTableModel(new Object[]{"Usuário", "Documento", "Item", "Data Prevista"}, 0) {
            private static final long serialVersionUID = 1L; // ✅ adicionada para a classe anônima

            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

    private final JTable tabela = new JTable(tm);

    // Mantemos a lista alinhada às linhas da tabela
    private List<Emprestimo> linhas;

    private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PainelEmprestimos(BibliotecaService service) {
        this.service = service;
        setLayout(new BorderLayout());

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Usuário:"));
        topo.add(cbUsuario);
        topo.add(new JLabel("Item:"));
        topo.add(cbItem);

        JButton btEmprestar = new JButton("Emprestar");
        JButton btDevolver = new JButton("Devolver selecionado");
        topo.add(btEmprestar);
        topo.add(btDevolver);
        add(topo, BorderLayout.NORTH);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoCreateRowSorter(true);
        tabela.setFillsViewportHeight(true);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        btEmprestar.addActionListener(e -> realizarEmprestimo());
        btDevolver.addActionListener(e -> devolverSelecionado());

        carregarCombos();
        recarregarTabela(); // carrega a partir do service (emprestimos.dat)
    }

    private void carregarCombos() {
        cbUsuario.removeAllItems();
        for (var u : service.getUsuarios()) cbUsuario.addItem(u);

        cbItem.removeAllItems();
        for (var p : service.getAcervo()) cbItem.addItem(p);
    }

    private void recarregarTabela() {
        // zera
        while (tm.getRowCount() > 0) tm.removeRow(0);
        // carrega
        linhas = service.getEmprestimos();
        for (var e : linhas) {
            tm.addRow(new Object[]{
                e.getUsuario().getNome(),
                e.getUsuario().getDocumento(),
                e.getItem().toString(),
                e.getDataPrevista() == null ? "" : e.getDataPrevista().format(FMT)
            });
        }
    }

    private void realizarEmprestimo() {
        Usuario u = (Usuario) cbUsuario.getSelectedItem();
        Publicacao p = (Publicacao) cbItem.getSelectedItem();
        if (u == null || p == null) {
            JOptionPane.showMessageDialog(this, "Selecione usuário e item.");
            return;
        }
        try {
            service.emprestar(u, p);
            JOptionPane.showMessageDialog(this, "Empréstimo realizado com sucesso!");
            recarregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void devolverSelecionado() {
        int viewRow = tabela.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um empréstimo na tabela.");
            return;
        }
        int modelRow = tabela.convertRowIndexToModel(viewRow);
        Emprestimo alvo = linhas.get(modelRow);

        int resp = JOptionPane.showConfirmDialog(this,
            "Confirmar devolução de:\n" + alvo.getItem() + "\npor " + alvo.getUsuario().getNome() + "?",
            "Confirmar devolução", JOptionPane.YES_NO_OPTION);

        if (resp != JOptionPane.YES_OPTION) return;

        try {
            Devolucao dev = service.devolver(alvo);
            long atraso = dev.diasAtraso(alvo.getDataPrevista());
            double multa = dev.calcularMulta(alvo.getDataPrevista());

            String msg = "Devolução concluída.";
            if (atraso > 0) {
                msg += "\nAtraso: " + atraso + " dia(s)\nMulta: R$ " + String.format("%.2f", multa);
            }
            JOptionPane.showMessageDialog(this, msg, "Devolução", JOptionPane.INFORMATION_MESSAGE);

            recarregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
