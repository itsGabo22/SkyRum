package skyrum.decorators;

import skyrum.Hero;
import skyrum.HeroDecorator;

/**
 * Decorador concreto: Espada Encantada.
 * Aumenta el ataque en 20.
 */
public class EnchantedSwordDecorator extends HeroDecorator {
    public EnchantedSwordDecorator(Hero hero) {
        super(hero);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " con Espada Encantada";
    }

    @Override
    public int getAttack() {
        return super.getAttack() + 20;
    }
}
