package skyrum.decorators;

import skyrum.Hero;
import skyrum.HeroDecorator;

/**
 * Decorador concreto: Encantamiento de Fuego.
 * Aumenta el ataque en 10.
 */
public class FireEnchantDecorator extends HeroDecorator {
    public FireEnchantDecorator(Hero hero) {
        super(hero);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " con aura de Fuego";
    }

    @Override
    public int getAttack() {
        return super.getAttack() + 10;
    }
}
