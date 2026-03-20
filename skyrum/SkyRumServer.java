package skyrum;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import skyrum.combat.CombatSimulator;
import skyrum.decorators.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Servidor Web Simple usando com.sun.net.httpserver (incluido en el JDK de Java).
 * Muestra la interfaz gráfica de selección y orquesta la creación dinámica del héroe
 * a través del Patrón Decorator.
 */
public class SkyRumServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new SkyRumHandler());
        server.createContext("/images", new StaticImageHandler());
        server.setExecutor(null); // crea un executor por defecto
        server.start();
        System.out.println("SkyRum Server iniciado en http://localhost:" + port);
        System.out.println("Abre ese enlace en tu navegador para probar el simulador.");
    }

    static class StaticImageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String path = t.getRequestURI().getPath(); // Ej: /images/ragnar.png
            String fileName = path.substring(path.lastIndexOf("/") + 1);
            java.io.File file = new java.io.File("images/" + fileName);
            if (file.exists()) {
                t.getResponseHeaders().set("Content-Type", "image/png");
                t.sendResponseHeaders(200, file.length());
                OutputStream os = t.getResponseBody();
                java.nio.file.Files.copy(file.toPath(), os);
                os.close();
            } else {
                t.sendResponseHeaders(404, -1);
            }
        }
    }

    static class SkyRumHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String query = t.getRequestURI().getQuery();
            
            // Creamos nuestro héroe base original (Componente Concreto)
            Hero hero = new BaseHero();
            boolean showCombat = false;
            
            if (query != null && !query.isEmpty()) {
                showCombat = true;
                Map<String, String> params = parseQuery(query);
                
                // Aquí usamos el PATRÓN DECORATOR.
                // Envolvemos el objeto "hero" repetidas veces según la selección.
                // Cada vez, el nuevo decorador guarda la referencia del héroe anterior.
                // Esto permite apilar estadísticas y comportamiento de manera dinámica.
                
                if (params.containsKey("weapon")) {
                    String weapon = params.get("weapon");
                    if (weapon.equals("sword")) {
                        hero = new SwordDecorator(hero); // Envuelve al héroe con la Espada
                    } else if (weapon.equals("enchanted_sword")) {
                        hero = new EnchantedSwordDecorator(hero);
                    } else if (weapon.equals("bow")) {
                        hero = new BowDecorator(hero);
                    }
                }
                
                if (params.containsKey("armor")) {
                    String armor = params.get("armor");
                    if (armor.equals("iron")) {
                        hero = new IronArmorDecorator(hero); // Envuelve con Armadura de Hierro
                    } else if (armor.equals("dragon")) {
                        hero = new DragonArmorDecorator(hero);
                    }
                }
                
                // Estos funcionan como Checkboxes (múltiples selecciones posibles)
                if (params.containsKey("fire")) {
                    hero = new FireEnchantDecorator(hero);
                }
                if (params.containsKey("speed")) {
                    hero = new SpeedBuffDecorator(hero);
                }
            }

            // Generamos la respuesta HTML
            String html = generateHTML(hero, showCombat);
            byte[] bytesResponse = html.getBytes(StandardCharsets.UTF_8);
            
            t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            t.sendResponseHeaders(200, bytesResponse.length);
            OutputStream os = t.getResponseBody();
            os.write(bytesResponse);
            os.close();
        }

        private Map<String, String> parseQuery(String query) {
            Map<String, String> result = new HashMap<>();
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                } else {
                    result.put(entry[0], "");
                }
            }
            return result;
        }

        private String generateHTML(Hero hero, boolean showCombat) {
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>SkyRum - Decorator Pattern</title>");
            sb.append("<style>");
            sb.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #2c3e50; padding: 20px; color: #ecf0f1; margin: 0;}");
            sb.append("h1 { color: #f1c40f; text-align: center; font-size: 3em; margin-bottom: 5px;}");
            sb.append(".subtitle { text-align: center; color: #bdc3c7; max-width: 800px; margin: 0 auto 30px auto; line-height: 1.6;}");
            sb.append(".container { display: flex; gap: 20px; max-width: 900px; margin: auto; }");
            sb.append(".panel { background: #34495e; padding: 25px; border-radius: 12px; box-shadow: 0 8px 16px rgba(0,0,0,0.3); flex: 1; }");
            sb.append(".panel h2 { color: #e74c3c; margin-top: 0;}");
            sb.append(".narrative { text-align: center; margin-bottom: 30px; background: #1a252f; padding: 20px; border-radius: 10px; border: 2px solid #f39c12; }");
            sb.append(".narrative img { width: 150px; border-radius: 50%; border: 3px solid #f1c40f; box-shadow: 0 0 15px rgba(241, 196, 15, 0.5); }");
            sb.append(".narrative p { font-size: 1.2em; font-style: italic; color: #ecf0f1; margin-top: 15px;}");
            sb.append(".character-img { width: 180px; display: block; margin: 0 auto 20px auto; border-radius: 10px; border: 2px solid #7f8c8d; }");
            sb.append("h3 { color: #3498db; border-bottom: 1px solid #456; padding-bottom: 5px;}");
            sb.append("label { cursor: pointer; display: block; margin-bottom: 8px; font-size: 16px; transition: color 0.2s;}");
            sb.append("label:hover { color: #f39c12; }");
            sb.append("button { background: #e67e22; color: white; border: none; padding: 12px 20px; border-radius: 6px; cursor: pointer; font-size: 18px; font-weight: bold; width: 100%; margin-top: 25px; transition: background 0.3s;}");
            sb.append("button:hover { background: #d35400; }");
            sb.append(".result-panel { background: #ecf0f1; color: #2c3e50; }");
            sb.append(".result-panel h3 { color: #8e44ad; }");
            sb.append(".decorator-explanation { background: #1abc9c; color: white; padding: 15px; border-radius: 8px; margin-bottom: 25px; max-width: 870px; margin: 0 auto 20px auto; font-size: 15px;}");
            sb.append("</style></head><body>");
            
            sb.append("<h1>SkyRum 🐉</h1>");
            
            // Sección narrativa de Crowley
            sb.append("<div class='narrative'>");
            sb.append("<img src='/images/crowley.png' alt='Crowley'>");
            sb.append("<p>\"Ragnar, las bestias de SkyRum han despertado.<br>Equípate con estas armas y demuestra tu poder.\"</p>");
            sb.append("</div>");
            
            sb.append("<div class='container'>");
            
            // Left Panel: Form
            sb.append("<div class='panel'>");
            sb.append("<h2>Equipa a tu héroe</h2>");
            
            // Imagen de Ragnar
            sb.append("<img src='/images/ragnar.png' alt='Ragnar' class='character-img'>");
            
            sb.append("<form method='GET' action='/'>");
            
            sb.append("<h3>⚔️ Armas (Elige 1)</h3>");
            sb.append("<label><input type='radio' name='weapon' value='none' checked> Ninguna (Puños)</label>");
            sb.append("<label><input type='radio' name='weapon' value='sword'> Espada (+10 Ataque)</label>");
            sb.append("<label><input type='radio' name='weapon' value='enchanted_sword'> Espada Encantada (+20 Ataque)</label>");
            sb.append("<label><input type='radio' name='weapon' value='bow'> Arco (+8 Ataque, +5 Velocidad)</label>");
            
            sb.append("<h3>🛡️ Armaduras (Elige 1)</h3>");
            sb.append("<label><input type='radio' name='armor' value='none' checked> Ropa normal</label>");
            sb.append("<label><input type='radio' name='armor' value='iron'> Armadura de Hierro (+15 Defensa)</label>");
            sb.append("<label><input type='radio' name='armor' value='dragon'> Armadura de Dragón (+25 Defensa)</label>");
            
            sb.append("<h3>✨ Magia / Buffs (Combinables)</h3>");
            sb.append("<label><input type='checkbox' name='fire' value='on'> Encantamiento de Fuego (+10 Ataque)</label>");
            sb.append("<label><input type='checkbox' name='speed' value='on'> Bendición de Velocidad (+10 Velocidad)</label>");
            
            sb.append("<button type='submit'>⚔️ Crear héroe y combatir</button>");
            sb.append("</form>");
            sb.append("</div>");
            
            // Right Panel: Results
            if (showCombat) {
                sb.append("<div class='panel result-panel'>");
                sb.append(CombatSimulator.simulate(hero));
                sb.append("</div>");
            }
            
            sb.append("</div>"); // end container
            sb.append("</body></html>");
            
            return sb.toString();
        }
    }
}
