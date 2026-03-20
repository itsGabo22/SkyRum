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
            sb.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><title>SkyRum - RPG Experience</title>");
            sb.append("<style>");
            sb.append("@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;700&family=MedievalSharp&display=swap');");
            sb.append("body { font-family: 'Cinzel', serif; background-color: #0b0c10; color: #c5c6c7; margin: 0; padding: 20px; background-image: radial-gradient(circle at center, #1f2833 0%, #0b0c10 100%); min-height: 100vh; }");
            sb.append("h1, h2, h3 { color: #d4af37; text-shadow: 1px 1px 3px #000; margin-top: 0; }");
            sb.append("h1 { text-align: center; font-size: 3em; margin-bottom: 25px; letter-spacing: 2px;}");
            sb.append(".quest-panel { background: linear-gradient(135deg, rgba(26,26,29,0.9), rgba(44,62,80,0.9)); border: 2px solid #d4af37; border-radius: 8px; padding: 25px; max-width: 900px; margin: 0 auto 30px auto; display: flex; align-items: center; gap: 30px; box-shadow: 0 0 25px rgba(212, 175, 55, 0.2); }");
            sb.append(".quest-panel img { width: 130px; height: 130px; border-radius: 50%; border: 3px solid #d4af37; box-shadow: 0 0 20px rgba(212, 175, 55, 0.6); object-fit: cover; }");
            sb.append(".quest-dialog { font-family: 'MedievalSharp', cursive; font-size: 1.5em; color: #f1e5ac; line-height: 1.5; text-shadow: 2px 2px 4px #000;}");
            sb.append(".container { display: flex; gap: 30px; max-width: 1000px; margin: auto; }");
            sb.append(".panel { background: rgba(20, 24, 28, 0.85); border: 2px solid #5a4f32; padding: 30px; border-radius: 12px; flex: 1; box-shadow: inset 0 0 30px rgba(0,0,0,0.9), 0 0 20px rgba(0,0,0,0.7); }");
            sb.append(".character-img { width: 100%; max-width: 260px; display: block; margin: 0 auto 25px auto; border: 3px solid #5a4f32; border-radius: 8px; box-shadow: 0 0 20px rgba(255, 255, 255, 0.05); }");
            sb.append(".enemy-img { width: 100%; max-width: 220px; border: 2px solid #a93226; border-radius: 8px; box-shadow: 0 0 20px rgba(169, 50, 38, 0.4); }");
            sb.append("label { display: block; margin-bottom: 12px; font-size: 1.15em; cursor: pointer; transition: all 0.3s ease; padding: 10px; border-radius: 6px; border: 1px solid transparent; background: rgba(44,62,80,0.2);}");
            sb.append("label:hover { background: rgba(212, 175, 55, 0.15); border-color: #8c7324; text-shadow: 0 0 8px #d4af37; transform: translateX(5px);}");
            sb.append("input[type='radio'], input[type='checkbox'] { margin-right: 12px; transform: scale(1.2); accent-color: #d4af37; }");
            sb.append("button { background: linear-gradient(to bottom, #d4af37, #8c7324); color: #0a0a0a; font-family: 'Cinzel', serif; font-weight: bold; font-size: 1.3em; border: 2px solid #fff; padding: 18px; width: 100%; margin-top: 30px; border-radius: 6px; cursor: pointer; box-shadow: 0 0 15px rgba(212, 175, 55, 0.4); transition: all 0.3s ease; text-transform: uppercase; letter-spacing: 1px;}");
            sb.append("button:hover { background: linear-gradient(to bottom, #f1e5ac, #d4af37); box-shadow: 0 0 25px rgba(212, 175, 55, 0.8); transform: translateY(-2px); }");
            sb.append(".result-panel h3 { border-bottom: 2px solid #5a4f32; padding-bottom: 12px; margin-bottom: 20px;}");
            sb.append(".synergy-text { color: #f1c40f; font-weight: bold; text-shadow: 0 0 8px #f39c12; margin: 8px 0; font-size: 1.1em;}");
            sb.append(".weakness-text { color: #e74c3c; font-weight: bold; text-shadow: 0 0 8px #c0392b; margin: 8px 0; font-size: 1.1em;}");
            sb.append(".bonuses-box { background: rgba(0,0,0,0.6); padding: 15px; border: 1px solid #8c7324; border-radius: 8px; margin-bottom: 25px;}");
            sb.append(".victory { color: #2ecc71; font-size: 1.5em; font-weight: bold; text-align: center; text-shadow: 0 0 15px #27ae60; margin-top: 25px; background: rgba(46, 204, 113, 0.1); padding: 15px; border-radius: 8px; border: 1px solid #2ecc71;}");
            sb.append(".defeat { color: #e74c3c; font-size: 1.5em; font-weight: bold; text-align: center; text-shadow: 0 0 15px #c0392b; margin-top: 25px; background: rgba(231, 76, 60, 0.1); padding: 15px; border-radius: 8px; border: 1px solid #e74c3c;}");
            sb.append(".enemy-stats { text-align: center; color: #bdc3c7; font-size: 1em; margin-top: 15px; font-family: 'MedievalSharp', cursive;}");
            sb.append(".hero-desc { font-family: 'MedievalSharp', cursive; color: #ecf0f1; font-size: 1.2em; line-height: 1.4; margin-bottom: 15px;}");
            sb.append("</style></head><body>");
            
            sb.append("<h1>SkyRum 🐉</h1>");
            
            // Top Panel: Story
            sb.append("<div class='quest-panel'>");
            sb.append("<img src='/images/crowley.png' alt='Crowley'>");
            sb.append("<div class='quest-dialog'>\"Ragnar, the beasts of SkyRum have awakened.<br>Choose your equipment wisely and prove your strength.\"</div>");
            sb.append("</div>");
            
            sb.append("<div class='container'>");
            
            // Left Panel: Form
            sb.append("<div class='panel'>");
            sb.append("<h2>Equip Ragnar</h2>");
            
            // Imagen de Ragnar
            sb.append("<img src='/images/ragnar.png' alt='Ragnar' class='character-img'>");
            
            sb.append("<form method='GET' action='/'>");
            
            sb.append("<h3>⚔️ Weapons</h3>");
            sb.append("<label><input type='radio' name='weapon' value='none' checked> None (Fists)</label>");
            sb.append("<label><input type='radio' name='weapon' value='sword'> Sword (+10 Attack)</label>");
            sb.append("<label><input type='radio' name='weapon' value='enchanted_sword'> Enchanted Sword (+20 Attack)</label>");
            sb.append("<label><input type='radio' name='weapon' value='bow'> Bow (+8 Attack, +5 Speed)</label>");
            
            sb.append("<h3>🛡️ Armor</h3>");
            sb.append("<label><input type='radio' name='armor' value='none' checked> Normal Clothes</label>");
            sb.append("<label><input type='radio' name='armor' value='iron'> Iron Armor (+15 Defense)</label>");
            sb.append("<label><input type='radio' name='armor' value='dragon'> Dragon Armor (+25 Defense)</label>");
            
            sb.append("<h3>✨ Magic / Buffs</h3>");
            sb.append("<label><input type='checkbox' name='fire' value='on'> Fire Enchantment (+10 Attack)</label>");
            sb.append("<label><input type='checkbox' name='speed' value='on'> Speed Blessing (+10 Speed)</label>");
            
            sb.append("<button type='submit'>Forge Hero and Enter Battle</button>");
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
