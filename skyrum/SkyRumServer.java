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
        server.createContext("/images", new StaticFileHandler("images", "image/png"));
        server.createContext("/audio", new StaticFileHandler("audio", "audio/mpeg"));
        server.setExecutor(null);
        server.start();
        System.out.println("SkyRum Server iniciado en http://localhost:" + port);
    }

    static class StaticFileHandler implements HttpHandler {
        private String dir;
        private String contentType;
        public StaticFileHandler(String dir, String contentType) { this.dir = dir; this.contentType = contentType; }
        @Override
        public void handle(HttpExchange t) throws IOException {
            String path = t.getRequestURI().getPath();
            String fileName = path.substring(path.lastIndexOf("/") + 1);
            java.io.File file = new java.io.File(dir + "/" + fileName);
            if (file.exists()) {
                t.getResponseHeaders().set("Content-Type", contentType);
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
            
            Hero hero = new BaseHero();
            boolean showCombat = false;
            int level = 1;
            
            if (query != null && !query.isEmpty()) {
                Map<String, String> params = parseQuery(query);
                
                if (params.containsKey("weapon")) {
                    showCombat = true;
                    String weapon = params.get("weapon");
                    if (weapon.equals("sword")) hero = new SwordDecorator(hero); 
                    else if (weapon.equals("enchanted_sword")) hero = new EnchantedSwordDecorator(hero);
                    else if (weapon.equals("bow")) hero = new BowDecorator(hero);
                }
                
                if (params.containsKey("armor")) {
                    String armor = params.get("armor");
                    if (armor.equals("iron")) hero = new IronArmorDecorator(hero); 
                    else if (armor.equals("dragon")) hero = new DragonArmorDecorator(hero);
                }
                
                if (params.containsKey("fire")) hero = new FireEnchantDecorator(hero);
                if (params.containsKey("speed")) hero = new SpeedBuffDecorator(hero);
                
                if (params.containsKey("level")) {
                    try { level = Integer.parseInt(params.get("level")); } catch(Exception e) {}
                }
            }

            String html = generateHTML(hero, showCombat, level);
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

        private String generateHTML(Hero hero, boolean showCombat, int level) {
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
            
            sb.append("<audio id='bgm' loop><source src='/audio/ambient.mp3' type='audio/mpeg'></audio>");
            sb.append("<audio id='sfx-victory'><source src='/audio/victory.mp3' type='audio/mpeg'></audio>");
            sb.append("<audio id='sfx-defeat'><source src='/audio/defeat.mp3' type='audio/mpeg'></audio>");
            
            sb.append("<div class='audio-control' onclick='toggleAudio()' id='music-btn'>🔈 MÚSICA: OFF</div>");

            sb.append("<div class='header-container'>");
            sb.append("<h1 class='title-main'>SKYRUM</h1>");
            sb.append("<div class='title-line' id='t-line'></div>");
            sb.append("<div class='title-sub'>Crónicas del Norte</div>");
            sb.append("</div>");
            
            String narrative = "\"Ragnar, las bestias de SkyRum han despertado. Equípate sabiamente y demuestra tu poder.\"";
            if (level == 2) narrative = "\"Los gigantes han despertado… algo oscuro se mueve en las montañas de SkyRum.\"";
            else if (level == 3) narrative = "\"Los dragones han vuelto a los cielos de SkyRum. El fin se acerca, Ragnar.\"";
            else if (level >= 4) narrative = "\"El Señor Dragón gobierna los cielos. Esta batalla decidirá el destino del reino.\"";

            sb.append("<div class='quest-panel' id='q-panel'>");
            sb.append("<img src='/images/crowley.png' alt='Crowley'>");
            sb.append("<div class='quest-dialog' id='n-text'>").append(narrative).append("</div>");
            sb.append("</div>");
            
            sb.append("<div class='container'>");
            
            sb.append("<div class='panel' id='p-inv'>");
            sb.append("<h2>Equipa a Ragnar</h2>");
            sb.append("<div style='text-align: center;'><img src='/images/ragnar.png' style='width: 250px; border: 2px solid #5a4f32; margin-bottom: 25px;'></div>");
            
            sb.append("<form method='GET' action='/'>");
            sb.append("<input type='hidden' name='level' value='").append(showCombat ? level + 1 : level).append("'>");
            
            sb.append("<h3>⚔️ Armas</h3>");
            sb.append("<label class='inventory-label shadow'><span>Ninguna (Puños)</span><input type='radio' name='weapon' value='none' checked></label>");
            sb.append("<label class='inventory-label shadow'><span>Espada de Hierro (+10 Atk)</span><input type='radio' name='weapon' value='sword'></label>");
            sb.append("<label class='inventory-label shadow'><span>Espada Encantada (+20 Atk)</span><input type='radio' name='weapon' value='enchanted_sword'></label>");
            sb.append("<label class='inventory-label shadow'><span>Arco Élfico (+8 Atk, +5 Spd)</span><input type='radio' name='weapon' value='bow'></label>");
            
            sb.append("<h3>🛡️ Armadura</h3>");
            sb.append("<label class='inventory-label shadow'><span>Ropa de Aldeano</span><input type='radio' name='armor' value='none' checked></label>");
            sb.append("<label class='inventory-label shadow'><span>Armadura de Hierro (+15 Def)</span><input type='radio' name='armor' value='iron'></label>");
            sb.append("<label class='inventory-label shadow'><span>Escamas de Dragón (+25 Def)</span><input type='radio' name='armor' value='dragon'></label>");
            
            sb.append("<h3>✨ Magia / Bendiciones</h3>");
            sb.append("<label class='inventory-label shadow'><span>Encantamiento de Fuego (+10 Atk)</span><input type='checkbox' name='fire' value='on'></label>");
            sb.append("<label class='inventory-label shadow'><span>Bendición de Velocidad (+10 Spd)</span><input type='checkbox' name='speed' value='on'></label>");
            
            sb.append("<button type='submit'>Forjar héroe y entrar en batalla</button>");
            sb.append("</form>");
            sb.append("</div>");
            
            if (showCombat) {
                sb.append("<div class='panel' id='p-combat'>");
                sb.append(CombatSimulator.simulate(hero, level)); // Pass level to CombatSimulator
                sb.append("</div>");
            }
            sb.append("</div>");

            sb.append("<script>");
            sb.append("function toggleAudio() {");
            sb.append("  const bgm = document.getElementById('bgm');");
            sb.append("  const btn = document.getElementById('music-btn');");
            sb.append("  if (bgm.paused) { bgm.play(); btn.innerText = '🔊 MÚSICA: ON'; }");
            sb.append("  else { bgm.pause(); btn.innerText = '🔈 MÚSICA: OFF'; }");
            sb.append("}");
            sb.append("function playVictory() { document.getElementById('sfx-victory').play(); }");
            sb.append("function playDefeat() { document.getElementById('sfx-defeat').play(); }");

            sb.append("window.onload = () => {");
            sb.append("  const tl = gsap.timeline();");
            sb.append("  tl.to('.title-main', { opacity: 1, y: 0, duration: 1.2, ease: 'power3.out' })");
            sb.append("    .to('#t-line', { width: '80%', duration: 1 }, '-=0.8')");
            sb.append("    .to('.title-sub', { opacity: 1, duration: 1 }, '-=0.6')");
            sb.append("    .to('#q-panel', { autoAlpha: 1, y: 0, duration: 1 }, '-=0.5')");
            sb.append("    .to('#p-inv', { autoAlpha: 1, duration: 1 }, '-=0.6');");
            
            if (showCombat) {
                sb.append("  tl.to('#p-combat', { autoAlpha: 1, duration: 1 }, '-=0.7')");
                sb.append("    .to('.enemy-img', { scale: 1, duration: 1, ease: 'back.out(1.7)' })");
                sb.append("    .to('.synergy-badge, .weakness-badge', { opacity: 1, x: 0, stagger: 0.2 })");
                sb.append("    .to('.stat-fill', { width: (i, el) => el.style.width, duration: 1.5, stagger: 0.3, ease: 'power2.out' })");
                sb.append("    .to('.combat-msg', { opacity: 1, scale: 1, duration: 0.8, ease: 'elastic.out(1, 0.5)' });");
            }

            sb.append("  document.querySelectorAll('.inventory-label').forEach(label => {");
            sb.append("    label.addEventListener('click', () => {");
            sb.append("      const group = label.querySelector('input').name;");
            sb.append("      if(label.querySelector('input').type === 'radio') {");
            sb.append("        document.querySelectorAll('input[name='+group+']').forEach(i => i.parentNode.classList.remove('active'));");
            sb.append("      }");
            sb.append("      label.classList.toggle('active');");
            sb.append("      gsap.fromTo(label, { scale: 1 }, { scale: 1.04, duration: 0.1, yoyo: true, repeat: 1 });");
            sb.append("    });");
            sb.append("  });");
            sb.append("};");
            sb.append("</script>");
            
            sb.append("</body></html>");
            return sb.toString();
        }
    }
}
