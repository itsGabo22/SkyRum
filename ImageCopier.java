import java.nio.file.*;
import java.io.IOException;

public class ImageCopier {
    public static void main(String[] args) throws IOException {
        Path sourceDir = Paths.get("C:\\Users\\Gabriel\\.gemini\\antigravity\\brain\\c532fa54-afa8-41f0-9b69-0b965495236f");
        Path destDir = Paths.get("images");
        Files.createDirectories(destDir);
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir, "*.png")) {
            for (Path entry: stream) {
                String fileName = entry.getFileName().toString();
                String newName = "";
                if(fileName.startsWith("ragnar_")) newName = "ragnar.png";
                if(fileName.startsWith("crowley_")) newName = "crowley.png";
                if(fileName.startsWith("dragon_")) newName = "dragon.png";
                if(fileName.startsWith("giant_")) newName = "giant.png";
                if(fileName.startsWith("bandit_")) newName = "bandit.png";
                
                if(!newName.isEmpty()) {
                    Files.copy(entry, destDir.resolve(newName), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Copied " + newName);
                }
            }
        }
    }
}
