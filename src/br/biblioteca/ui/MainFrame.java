package br.biblioteca.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.biblioteca.servico.BibliotecaService;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public MainFrame(BibliotecaService service) {
        super("ProjetoFinalBiblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Acervo",      new PainelAcervo(service));   // <-- CONFIRA ESTA LINHA
        tabs.add("Usuários",    new PainelUsuarios(service));
        tabs.add("Empréstimos", new PainelEmprestimos(service));

        add(tabs, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                try { service.salvar(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }
}
