import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipFileSystem {

  public static void main(String[] args) throws IOException {
    Path fs = Paths.get("fs");
    //Files.createDirectory(fs);
    //FileSystem fileSystem = FileSystems.newFileSystem(Paths.get("file://fs"), Collections.emptyMap());
    FileSystem fileSystem = fs.getFileSystem();
    Path a = fileSystem.getPath("a.txt");
    Files.writeString(a, "hello");

  }

}
