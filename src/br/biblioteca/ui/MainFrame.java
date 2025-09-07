package br.biblioteca.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.biblioteca.servico.BibliotecaService;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    // 1) Guardar o tabbed pane como atributo para poder acessá-lo depois
    private final JTabbedPane tabs = new JTabbedPane();

    public MainFrame(BibliotecaService service) {
        super("ProjetoFinalBiblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // 2) Monta as abas
        tabs.addTab("Acervo",      new PainelAcervo(service));
        tabs.addTab("Usuários",    new PainelUsuarios(service));
        tabs.addTab("Empréstimos", new PainelEmprestimos(service));

        add(tabs, BorderLayout.CENTER);

        // 3) Salvar ao fechar
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                try { service.salvar(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    // === Métodos utilitários para o MainGUI ===

    /** Seleciona a aba pelo título (ignora maiúsc/minúsc). */
    public void selecionarAba(String titulo) {
        if (titulo == null) return;
        for (int i = 0; i < tabs.getTabCount(); i++) {
            if (titulo.equalsIgnoreCase(tabs.getTitleAt(i))) {
                tabs.setSelectedIndex(i);
                return;
            }
        }
    }

    /** Seleciona a aba pelo índice, se existir. */
    public void selecionarAba(int index) {
        if (index >= 0 && index < tabs.getTabCount()) {
            tabs.setSelectedIndex(index);
        }
    }
}
