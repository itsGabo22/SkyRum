package skyrum;

/**
 * Interfaz base para nuestro héroe.
 * Define los métodos que todos los héroes (y sus decoradores) deben implementar.
 */
public interface Hero {
    String getDescription();
    int getAttack();
    int getDefense();
    int getSpeed();
}
