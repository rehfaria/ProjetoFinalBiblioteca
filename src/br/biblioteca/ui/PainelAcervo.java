package br.biblioteca.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import br.biblioteca.servico.BibliotecaService;

public class PainelAcervo extends JPanel {
    private static final long serialVersionUID = 1L;

    private final BibliotecaService service;

    // Campos
    private final JTextField txtId     = new JTextField(8);
    private final JTextField txtTitulo = new JTextField(14);
    private final JTextField txtAutor  = new JTextField(12);
    private final JComboBox<String> cbTipo =
            new JComboBox<>(new String[]{"Livro","Revista","MidiaDigital"});
    private final JTextField txtExtra = new JTextField(6); // páginas/edição/MB
    private final JLabel lblExtra = new JLabel("Páginas:");

    // Botões
    private final JButton btnAdicionar = new JButton("Adicionar");
    private final JButton btnRemover   = new JButton("Remover selecionado");

    // Tabela
    private final AcervoTableModel model = new AcervoTableModel(new ArrayList<>());
    private final JTable tabela = new JTable(model);

    public PainelAcervo(BibliotecaService service) {
        this.service = service;
        setLayout(new BorderLayout());
        // opcional: borda de título para ajudar a identificar
        // setBorder(new TitledBorder("Acervo"));

        // --- Topo de cadastro ---
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.setBackground(new Color(245, 245, 245));
        topo.add(new JLabel("ID:"));     topo.add(txtId);
        topo.add(new JLabel("Título:")); topo.add(txtTitulo);
        topo.add(new JLabel("Autor:"));  topo.add(txtAutor);
        topo.add(new JLabel("Tipo:"));   topo.add(cbTipo);
        topo.add(lblExtra);              topo.add(txtExtra);
        topo.add(btnAdicionar);
        topo.add(btnRemover);

        add(topo, BorderLayout.NORTH);

        // --- Tabela no centro ---
        tabela.setAutoCreateRowSorter(true); // ordenação por coluna
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Troca label do campo extra conforme tipo
        cbTipo.addActionListener(e -> {
            String t = (String) cbTipo.getSelectedItem();
            lblExtra.setText("Livro".equals(t) ? "Páginas:" :
                             "Revista".equals(t) ? "Edição:" : "Tamanho (MB):");
        });

        // Atalho: pressionar Enter no último campo = clicar Adicionar
        txtExtra.addActionListener(e -> btnAdicionar.doClick());

        // Ação: Adicionar
        btnAdicionar.addActionListener(e -> {
            try {
                String id   = txtId.getText().trim();
                String ti   = txtTitulo.getText().trim();
                String au   = txtAutor.getText().trim();
                String tipo = (String) cbTipo.getSelectedItem();
                String ex   = txtExtra.getText().trim();

                if (id.isEmpty() || ti.isEmpty() || au.isEmpty() || ex.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                    return;
                }

                // valida ID único
                if (service.existePublicacaoId(id)) {
                    JOptionPane.showMessageDialog(this, "Já existe uma publicação com ID: " + id);
                    return;
                }

                switch (tipo) {
                    case "Livro" -> {
                        int paginas = parseIntPositivo(ex, "Páginas");
                        service.cadastrarLivro(id, ti, au, paginas);
                    }
                    case "Revista" -> {
                        int edicao = parseIntPositivo(ex, "Edição");
                        service.cadastrarRevista(id, ti, au, edicao);
                    }
                    case "MidiaDigital" -> {
                        double mb = parseDoublePositivo(ex, "Tamanho (MB)");
                        service.cadastrarMidia(id, ti, au, mb);
                    }
                    default -> throw new IllegalArgumentException("Tipo inválido.");
                }

                atualizarTabela();
                limparCampos();
                service.salvar();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Valor numérico inválido: " + nfe.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        // Ação: Remover selecionado
        btnRemover.addActionListener(e -> {
            int viewRow = tabela.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um item na tabela para remover.");
                return;
            }
            int modelRow = tabela.convertRowIndexToModel(viewRow);
            String id = (String) model.getValueAt(modelRow, 0);

            int opc = JOptionPane.showConfirmDialog(
                    this, "Remover o item ID " + id + "?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (opc != JOptionPane.YES_OPTION) return;

            try {
                service.removerPublicacaoPorId(id);
                atualizarTabela();
                service.salvar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        atualizarTabela();
        System.out.println("[PainelAcervo] pronto: Adicionar/Remover/Ordenar.");
    }

    private void atualizarTabela() {
        model.setDados(service.getAcervo());
    }

    private void limparCampos() {
        txtId.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtExtra.setText("");
        cbTipo.setSelectedIndex(0);
        lblExtra.setText("Páginas:");
        txtId.requestFocus();
    }

    // ---- helpers de validação numérica ----
    private int parseIntPositivo(String s, String campo) {
        int v = Integer.parseInt(s);
        if (v <= 0) throw new NumberFormatException(campo + " deve ser > 0");
        return v;
    }
    private double parseDoublePositivo(String s, String campo) {
        double v = Double.parseDouble(s);
        if (v <= 0) throw new NumberFormatException(campo + " deve ser > 0");
        return v;
    }
}
