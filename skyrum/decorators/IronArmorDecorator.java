package skyrum.decorators;

import skyrum.Hero;
import skyrum.HeroDecorator;

/**
 * Decorador concreto: Armadura de Hierro.
 * Aumenta la defensa en 15.
 */
public class IronArmorDecorator extends HeroDecorator {
    public IronArmorDecorator(Hero hero) {
        super(hero);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " y Armadura de Hierro";
    }

    @Override
    public int getDefense() {
        return super.getDefense() + 15;
    }
}
