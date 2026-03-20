package skyrum.decorators;

import skyrum.Hero;
import skyrum.HeroDecorator;

/**
 * Decorador concreto: Armadura de Dragón.
 * Aumenta la defensa en 25.
 */
public class DragonArmorDecorator extends HeroDecorator {
    public DragonArmorDecorator(Hero hero) {
        super(hero);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " y Armadura de Dragón";
    }

    @Override
    public int getDefense() {
        return super.getDefense() + 25;
    }
}
