package org.shootemup.config;

import java.awt.desktop.PrintFilesHandler;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import org.shootemup.engine.GameLevel;

/// Classe de configuração, carrega os arquivos de configuração de monta informações do jogo
public class Config {
    public int playerLife;
    public List<Path> phaseFiles;

    // Arquivo de configuração do jogo
    private static final Path configFilePath = Paths.get("src/main/resources/config.txt");

    /// Abre o arquivo de configuração e carrega dados iniciais e arquivo de fases
    public Config() {
        try (Scanner configFile = new Scanner(configFilePath)){
            playerLife = configFile.nextInt();
            int numPhases = configFile.nextInt();
            var _ = configFile.nextLine(); // Remove \n depois de ler o int
            phaseFiles = new ArrayList<>(numPhases);
            int i = 0;
            // Guarda os arquivos das fases
            while (i < numPhases) {
                phaseFiles.add(Paths.get(configFile.nextLine().trim()));
                i++;
            }
        } catch (Exception e) {
            System.err.println("Falha ao ler arquivo de configuração: " + e);
        }
    }

    /// Função que carrega as fases do jogo, retorna como uma fila de fases ordenada por ordem do arquivo
    public Queue<GameLevel> loadPhases() {
        var phases = new LinkedList<GameLevel>();

        for (var file: phaseFiles) {
            // Abre cada arquivo, lê linha por linha e cria as entidades necessesárias
            var phase = new GameLevel();

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;
                    String[] tokens = line.split("\\s+");

                    // Confere o tipo de entidade que temos e se os dados da linha estao no formato certo
                    // Senao retorna uma exceção
                    if (tokens[0].contains("CHEFE") && tokens.length == 6) {
                        String enemyType = tokens[1].trim();
                        int life = Integer.parseInt(tokens[2]);
                        long spawnTime = Long.parseLong(tokens[3]);
                        int x = Integer.parseInt(tokens[4]);
                        int y = Integer.parseInt(tokens[5]);
                        phase.addEnemyFromRawData(enemyType, spawnTime, life, x, y);
                    } else if (tokens[0].contains("INIMIGO") && tokens.length == 5) {
                        String enemyType = tokens[1].trim();
                        long spawnTime = Long.parseLong(tokens[2]);
                        int x = Integer.parseInt(tokens[3]);
                        int y = Integer.parseInt(tokens[4]);
                        phase.addEnemyFromRawData(enemyType, spawnTime, 1, x, y);
                    } else if (tokens[0].contains("POWERUP") && tokens.length == 5) {
                        String powerupType = tokens[1].trim();
                        long spawnTime = Long.parseLong(tokens[2]);
                        int x = Integer.parseInt(tokens[3]);
                        int y = Integer.parseInt(tokens[4]);
                        phase.addPowerUpFromRawParts(powerupType, spawnTime, x, y);
                    } else {
                        throw new IllegalArgumentException("Formato de dados da fase inválido");
                    }
                }
            } catch (IOException e) {
                System.err.printf("Falha ao acessar (%s): %s\n", file, e);
            } catch (IllegalArgumentException e) {
                System.err.printf("Falha ao ler dados do arquivo (%s): %s\n", file, e);
            } catch (IllegalStateException e) {
                System.err.printf("Falha ao criar uma entidade a partir dos dados do arquivo (%s): %s\n", file, e);
            }
            phases.addLast(phase);
        }

        return phases;
    }

}
