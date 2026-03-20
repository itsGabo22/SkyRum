package skyrum.decorators;

import skyrum.Hero;
import skyrum.HeroDecorator;

/**
 * Decorador concreto: Espada.
 * Aumenta el ataque en 10.
 */
public class SwordDecorator extends HeroDecorator {
    public SwordDecorator(Hero hero) {
        super(hero);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " con Espada";
    }

    @Override
    public int getAttack() {
        return super.getAttack() + 10;
    }
}
