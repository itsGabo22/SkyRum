package skyrum.combat;

import skyrum.Hero;
import java.util.Random;

/**
 * Simulador de combate.
 * Genera un enemigo aleatorio y decide el resultado de la batalla
 * comparando de forma sencilla las estadísticas finales.
 */
public class CombatSimulator {
    
    // Clase interna simple para representar al enemigo
    public static class Enemy {
        public String name;
        public int attack;
        public int defense;
        public int speed;

        public Enemy(String name, int attack, int defense, int speed) {
            this.name = name;
            this.attack = attack;
            this.defense = defense;
            this.speed = speed;
        }
    }

    private static final Enemy[] ENEMIES = {
        new Enemy("Dragón", 40, 30, 15),
        new Enemy("Gigante", 35, 20, 10),
        new Enemy("Bandido", 15, 10, 20)
    };

    /**
     * @param hero El héroe ya decorado (creado con el patrón Decorator).
     * @return Una cadena en HTML con los resultados del combate.
     */
    public static String simulate(Hero hero) {
        StringBuilder sb = new StringBuilder();
        
        // 1. Mostrar las estadísticas finales de Ragnar
        sb.append("<h3>Estadísticas Finales de Ragnar</h3>");
        sb.append("<p><b>Descripción:</b> ").append(hero.getDescription()).append("</p>");
        sb.append("<p><b>Ataque:</b> ").append(hero.getAttack()).append("</p>");
        sb.append("<p><b>Defensa:</b> ").append(hero.getDefense()).append("</p>");
        sb.append("<p><b>Velocidad:</b> ").append(hero.getSpeed()).append("</p>");
        
        // 2. Generar enemigo aleatorio
        Random rand = new Random();
        Enemy enemy = ENEMIES[rand.nextInt(ENEMIES.length)];
        
        sb.append("<h3>Enemigo Encontrado</h3>");
        
        String enemyImg = "bandit.png";
        if (enemy.name.equals("Dragón")) enemyImg = "dragon.png";
        else if (enemy.name.equals("Gigante")) enemyImg = "giant.png";
        
        sb.append("<div style='text-align: center;'>");
        sb.append("<img src='/images/").append(enemyImg).append("' alt='").append(enemy.name).append("' style='width: 150px; height: 150px; object-fit: cover; border-radius: 10px; border: 2px solid #e74c3c; margin: 10px 0;'>");
        sb.append("</div>");
        
        sb.append("<p>¡Un <b>").append(enemy.name).append("</b> salvaje aparece!</p>");
        sb.append("<p>(Ataque: ").append(enemy.attack)
          .append(", Defensa: ").append(enemy.defense)
          .append(", Velocidad: ").append(enemy.speed).append(")</p>");
          
        sb.append("<h3>Resultado del Combate</h3>");
        
        // 3. Sistema súper simplificado de combate comparando la suma de stats
        int heroScore = hero.getAttack() + hero.getDefense() + hero.getSpeed();
        int enemyScore = enemy.attack + enemy.defense + enemy.speed;
        
        if (heroScore >= enemyScore) {
            sb.append("<p style='color: green; font-size: 18px; font-weight: bold;'>¡VICTORIA! Ragnar ha derrotado al ").append(enemy.name).append(".</p>");
        } else {
            sb.append("<p style='color: red; font-size: 18px; font-weight: bold;'>Ragnar ha sido derrotado por el ").append(enemy.name).append("...</p>");
        }

        return sb.toString();
    }
}
