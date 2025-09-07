package br.aplicacao;

import javax.swing.SwingUtilities;

import br.biblioteca.modelo.Biblioteca;
import br.biblioteca.modelo.Endereco;
import br.biblioteca.servico.BibliotecaService;
import br.biblioteca.ui.MainFrame;

public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var bib = new Biblioteca("Biblioteca Municipal", new Endereco("Av. Central","1000","Uberlândia"));
            var service = new BibliotecaService(bib);
            try { service.carregar(); } catch (Exception e) { e.printStackTrace(); }
            new MainFrame(service).setVisible(true);
        });
    }
}
