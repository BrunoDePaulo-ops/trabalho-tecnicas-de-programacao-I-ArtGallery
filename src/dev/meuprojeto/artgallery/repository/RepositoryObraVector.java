package dev.meuprojeto.artgallery.repository;

import java.util.Vector;
import dev.meuprojeto.artgallery.model.Obra;
import dev.meuprojeto.artgallery.exception.ObraJaCadastradaException;
import dev.meuprojeto.artgallery.exception.ObraNaoEncontradaException;

public class RepositoryObraVector implements IRepositoryObra {
    private Vector<Obra> obras;

    public RepositoryObraVector() {
        this.obras = new Vector<>();
    }

    @Override
    public void cadastrar(Obra obra) throws ObraJaCadastradaException {
        if (obra == null) {
            return;
        }
        
        // Verifica se já existe obra com mesmo título e autor (case insensitive)
        for (Obra o : obras) {
            if (o.getTitulo().equalsIgnoreCase(obra.getTitulo()) && 
                o.getAutor().equalsIgnoreCase(obra.getAutor())) {
                throw new ObraJaCadastradaException(
                    "Obra '" + obra.getTitulo() + "' de " + obra.getAutor() + " já está cadastrada."
                );
            }
        }
        obras.add(obra);
    }

    @Override
    public Obra buscar(String titulo) {
        if (titulo == null) {
            return null;
        }
        
        for (Obra obra : obras) {
            if (obra.getTitulo().equalsIgnoreCase(titulo)) {
                return obra;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Obra obra) throws ObraNaoEncontradaException {
        if (obra == null) {
            return;
        }
        
        for (int i = 0; i < obras.size(); i++) {
            Obra existente = obras.get(i);
            if (existente.getTitulo().equalsIgnoreCase(obra.getTitulo())) {
                obras.set(i, obra);
                return;
            }
        }
        throw new ObraNaoEncontradaException(
            "Obra com título '" + obra.getTitulo() + "' não foi encontrada para atualização."
        );
    }

    @Override
    public void remover(String titulo) {
        Obra obra = buscar(titulo);
        if (obra != null) {
            obra.setAtiva(false);
        }
    }

    @Override
    public Vector<Obra> listar() {
        return obras;
    }
}
