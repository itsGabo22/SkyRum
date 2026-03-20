package skyrum.decorators;

import skyrum.Hero;
import skyrum.HeroDecorator;

/**
 * Decorador concreto: Bendición de Velocidad.
 * Aumenta la velocidad en 10.
 */
public class SpeedBuffDecorator extends HeroDecorator {
    public SpeedBuffDecorator(Hero hero) {
        super(hero);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " con Bendición de Velocidad";
    }

    @Override
    public int getSpeed() {
        return super.getSpeed() + 10;
    }
}
