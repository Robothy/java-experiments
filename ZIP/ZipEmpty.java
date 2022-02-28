import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class ZipEmpty {
  public static void main(String[] args) throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(new File("a.zip"));
    ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
    zipOutputStream.close();
    fileOutputStream.close();
  }
}
