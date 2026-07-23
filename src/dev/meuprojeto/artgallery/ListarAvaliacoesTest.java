package dev.meuprojeto.artgallery;
import dev.meuprojeto.artgallery.repository.RepositoryObraDatabase;
import dev.meuprojeto.artgallery.model.Avaliacao;

import java.util.Vector;

public class ListarAvaliacoesTest {
    
    public static void main(String[] args) {
        RepositoryObraDatabase repo = new RepositoryObraDatabase();
        
        String titulo = "minsalt";
        
        Vector<Avaliacao> avaliacoes = repo.listarAvaliacoes(titulo);
        
        if (avaliacoes.isEmpty()) {
            System.out.println("⚠️ Nenhuma avaliação para '" + titulo + "'");
        } else {
            System.out.println("✅ " + avaliacoes.size() + " avaliações:");
            for (Avaliacao a : avaliacoes) {
                System.out.println("   " + a.getUsuario() + " - " + a.getNota() + " - " + a.getComentario());
            }
        }
        
        
        repo.buscar("Ciber Dragão");
        
        
    }
}
