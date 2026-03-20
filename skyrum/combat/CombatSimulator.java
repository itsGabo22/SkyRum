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
     * @return Una cadena en HTML con los resultados de combate con barras de stats y estilo RPG.
     */
    public static String simulate(Hero hero) {
        StringBuilder sb = new StringBuilder();
        
        // 1. Encontrar enemigo
        Random rand = new Random();
        Enemy enemy = ENEMIES[rand.nextInt(ENEMIES.length)];
        
        // 2. Mecánicas complejas
        SynergySystem.SynergyResult bonuses = SynergySystem.calculateBonuses(hero, enemy);
        
        int finalAttack = hero.getAttack() + bonuses.extraAttack;
        int finalDefense = hero.getDefense() + bonuses.extraDefense;
        int finalSpeed = hero.getSpeed() + bonuses.extraSpeed;
        
        String enemyImg = "bandit.png";
        if (enemy.name.equals("Dragón")) enemyImg = "dragon.png";
        else if (enemy.name.equals("Gigante")) enemyImg = "giant.png";
        
        sb.append("<h2>Battle Analysis</h2>");
        
        // Enemy Portrait
        sb.append("<div style='text-align: center;'>");
        sb.append("<img src='/images/").append(enemyImg).append("' alt='").append(enemy.name).append("' class='enemy-img'>");
        sb.append("<p class='enemy-stats'>").append(enemy.name).append(" (Level 50 Boss)</p>");
        sb.append("</div>");
        
        // Synergy/Weakness Badges
        if (!bonuses.synergyMessages.isEmpty() || !bonuses.weaknessMessages.isEmpty()) {
            String combined = (bonuses.synergyMessages + bonuses.weaknessMessages)
                .replace("<p class='synergy-text'>", "<div class='synergy-badge'>")
                .replace("<p class='weakness-text'>", "<div class='weakness-badge'>")
                .replace("</p>", "</div>");
            sb.append(combined);
        }
        
        sb.append("<h3>Final Attributes</h3>");
        sb.append("<p class='hero-desc' style='font-size: 0.9em;'>").append(hero.getDescription()).append("</p>");
        
        // Stat Bars
        addStatBar(sb, "ATTACK", finalAttack, 70, "atk-fill");
        addStatBar(sb, "DEFENSE", finalDefense, 70, "def-fill");
        addStatBar(sb, "SPEED", finalSpeed, 70, "spd-fill");
        
        // Combat logic
        int heroScore = finalAttack + finalDefense + finalSpeed;
        int enemyScore = enemy.attack + enemy.defense + enemy.speed;
        
        if (heroScore >= enemyScore) {
            sb.append("<div class='combat-msg msg-victory'>VICTORY: Ragnar has triumphed!</div>");
        } else {
            sb.append("<div class='combat-msg msg-defeat'>DEFEAT: Ragnar has fallen...</div>");
        }

        return sb.toString();
    }

    private static void addStatBar(StringBuilder sb, String label, int current, int max, String cssClass) {
        int percent = Math.min(100, (current * 100) / max);
        sb.append("<div class='stat-bar-container'>");
        sb.append("<div class='stat-label'><span>").append(label).append("</span><span>").append(current).append("</span></div>");
        sb.append("<div class='stat-bar'><div class='stat-fill ").append(cssClass).append("' style='width: ").append(percent).append("%;'></div></div>");
        sb.append("</div>");
    }
}
