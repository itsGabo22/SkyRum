package skyrum.combat;

import skyrum.Hero;

/**
 * Sistema que verifica dinámicamente las combinaciones de equipamiento
 * para brindar bonificaciones extras al jugador, simulando mecánicas de un RPG real.
 * Funciona leyendo los resultados finales de las descripciones del héroe ya decorado,
 * manteniendo el patrón Decorator completamente intacto y extendiendo el juego.
 */
public class SynergySystem {
    
    public static class SynergyResult {
        public int extraAttack = 0;
        public int extraSpeed = 0;
        public int extraDefense = 0;
        public String synergyMessages = "";
        public String weaknessMessages = "";
    }

    public static SynergyResult calculateBonuses(Hero hero, CombatSimulator.Enemy enemy) {
        SynergyResult result = new SynergyResult();
        String desc = hero.getDescription();
        
        // Identificamos los elementos equipados buscando palabras clave en la descripción
        boolean hasSword = desc.contains("Espada") && !desc.contains("Encantada");
        boolean hasEnchantedSword = desc.contains("Espada Encantada");
        boolean hasBow = desc.contains("Arco");
        boolean hasDragonArmor = desc.contains("Armadura de Dragón");
        boolean hasFire = desc.contains("Fuego");
        boolean hasSpeed = desc.contains("Bendición de Velocidad");

        // --- SISTEMA DE SINERGIAS DE EQUIPAMIENTO ---
        
        if (hasSword && hasFire) {
            result.extraAttack += 20;
            result.synergyMessages += "<p class='synergy-text'>✨ SYNERGY ACTIVATED: Flaming Blade! (+20 Attack)</p>\n";
        }
        if (hasBow && hasSpeed) {
            result.extraSpeed += 20;
            result.synergyMessages += "<p class='synergy-text'>✨ SYNERGY ACTIVATED: Phantom Archer! (+20 Speed)</p>\n";
        }
        if (hasEnchantedSword && hasDragonArmor) {
            // El bono se activa solo contra dragones
            if (enemy.name.equals("Dragón")) {
                result.extraAttack += 30;
                result.synergyMessages += "<p class='synergy-text'>✨ SYNERGY ACTIVATED: Dragon Slayer! (+30 extra attack against dragons)</p>\n";
            }
        }

        // --- SISTEMA DE DEBILIDADES DE LOS ENEMIGOS ---
        
        if (enemy.name.equals("Dragón") && hasBow) {
            result.extraAttack += 20;
            result.weaknessMessages += "<p class='weakness-text'>🔥 Enemy weakness exploited (Bow vs Dragon)! (+20 Attack)</p>\n";
        }
        if (enemy.name.equals("Gigante") && hasSword) {
            result.extraAttack += 15;
            result.weaknessMessages += "<p class='weakness-text'>🔥 Enemy weakness exploited (Sword vs Giant)! (+15 Attack)</p>\n";
        }
        if (enemy.name.equals("Bandido") && hasFire) {
            result.extraAttack += 15;
            result.weaknessMessages += "<p class='weakness-text'>🔥 Enemy weakness exploited (Fire Enchantment vs Bandit)! (+15 Attack)</p>\n";
        }

        return result;
    }
}
