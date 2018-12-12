package org.kai.protogen.compile;

import java.nio.file.Path;
import java.nio.file.Paths;

final class BuildProtoUtil {
  static boolean invalidDirectory(String projectPath, String filePath) {
    Path path = Paths.get(projectPath, filePath);
    return !path.toFile().isDirectory();
  }
}
