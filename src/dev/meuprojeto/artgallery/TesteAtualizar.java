package dev.meuprojeto.artgallery;

import dev.meuprojeto.artgallery.model.*;
import dev.meuprojeto.artgallery.repository.*;
import dev.meuprojeto.artgallery.exception.*;

public class TesteAtualizar {
    public static void main(String[] args) {
        RepositoryObraVector repo = new RepositoryObraVector();

        try {
            // 1. Cadastra uma obra
            PinturaDigital obra1 = new PinturaDigital("A Noite Estrelada", "Van Gogh", "4K", "Photoshop");
            repo.cadastrar(obra1);
            System.out.println("✅ Obra cadastrada!");

            // 2. Busca a obra
            Obra buscada = repo.buscar("A Noite Estrelada");
            System.out.println("📋 Antes: " + buscada.exibirDetalhes());

            // 3. Atualiza a obra (MANTENDO O MESMO TÍTULO)
            PinturaDigital obraAtualizada = new PinturaDigital(
                "A Noite Estrelada",        // ← MESMO TÍTULO
                "Vincent van Gogh",         // ← AUTOR ATUALIZADO
                "8K",                       // ← RESOLUÇÃO ATUALIZADA
                "Photoshop CC"              // ← SOFTWARE ATUALIZADO
            );
            repo.atualizar(obraAtualizada);
            System.out.println("✅ Obra atualizada!");

            // 4. Busca novamente
            Obra atualizada = repo.buscar("A Noite Estrelada");
            System.out.println("📋 Depois: " + atualizada.exibirDetalhes());

        } catch (ObraJaCadastradaException | ObraNaoEncontradaException e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
    }
}