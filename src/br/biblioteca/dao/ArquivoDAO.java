package br.biblioteca.dao;

import java.io.*;
import java.util.*;

public class ArquivoDAO<T extends Serializable> {
    private final File arquivo;

    public ArquivoDAO(String caminho) {
        this.arquivo = new File(caminho);
    }

    // Salva uma lista no arquivo
    public synchronized void salvarLista(List<T> lista) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            out.writeObject(lista);
        }
    }

    // Carrega uma lista do arquivo
    @SuppressWarnings("unchecked")
    public synchronized List<T> carregarLista() throws IOException {
        if (!arquivo.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (List<T>) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Erro ao ler o arquivo.", e);
        }
    }
}
