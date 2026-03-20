package skyrum.decorators;

import skyrum.Hero;
import skyrum.HeroDecorator;

/**
 * Decorador concreto: Arco.
 * Aumenta el ataque en 8 y la velocidad en 5.
 */
public class BowDecorator extends HeroDecorator {
    public BowDecorator(Hero hero) {
        super(hero);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " con Arco";
    }

    @Override
    public int getAttack() {
        return super.getAttack() + 8;
    }

    @Override
    public int getSpeed() {
        return super.getSpeed() + 5;
    }
}
