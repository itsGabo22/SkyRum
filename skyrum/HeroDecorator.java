package skyrum;

/**
 * Clase abstracta que implementa la interfaz Hero y contiene una referencia
 * a otro objeto Hero. Esta es la base del Patrón de Diseño Decorator.
 * Permite que los decoradores concretos "envuelvan" al héroe, delegando las llamadas
 * al componente subyacente y luego modificando el resultado (añadiendo stats).
 */
public abstract class HeroDecorator implements Hero {
    protected Hero hero;

    public HeroDecorator(Hero hero) {
        this.hero = hero;
    }

    @Override
    public String getDescription() {
        return hero.getDescription();
    }

    @Override
    public int getAttack() {
        return hero.getAttack();
    }

    @Override
    public int getDefense() {
        return hero.getDefense();
    }

    @Override
    public int getSpeed() {
        return hero.getSpeed();
    }
}
