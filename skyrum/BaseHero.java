package skyrum;

/**
 * Clase base del héroe (Componente Concreto en el patrón Decorator).
 * Representa al jugador sin ningún equipamiento ni poderes adicionales.
 * Sus estadísticas iniciales son básicas.
 */
public class BaseHero implements Hero {
    @Override
    public String getDescription() {
        return "Héroe novato";
    }

    @Override
    public int getAttack() {
        return 10;
    }

    @Override
    public int getDefense() {
        return 10;
    }

    @Override
    public int getSpeed() {
        return 10;
    }
}
