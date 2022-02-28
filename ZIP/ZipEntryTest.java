import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipEntryTest {
  public static void main(String[] args) throws IOException {
    printEntries("ZIP/test.zip");
    //printEntries("ZIP/test2.zip");
    //printEntries("ZIP/test-ZipUtils2926119595820296549.zip");
  }

  private static void printEntries(String filename) throws IOException {
    try (ZipFile zipFile = new ZipFile(new File(filename))) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        System.out.println(entries.nextElement());
      }
    }
  }

}
