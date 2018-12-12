package org.kai.protogen.compile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.SystemUtils;

final class ProtoCompilerUtil {
  private static final String PROTOC_NAME = "protoc";

  static String getSystemProtoCompilerPath() {
    return Arrays.stream(System.getenv("PATH").split(SystemUtils.IS_OS_WINDOWS ? ";" : ":"))
        .map(ProtoCompilerUtil::findProtoCompilerInPath)
        .filter(path -> !path.isEmpty())
        .findFirst()
        .orElse("");
  }

  private static String findProtoCompilerInPath(String path) {
    try {
      return StreamSupport.stream(
              Files.newDirectoryStream(
                      Paths.get(path), ProtoCompilerUtil::checkProtoCompilerPathValid)
                  .spliterator(),
              false)
          .map(Path::toString)
          .findFirst()
          .orElse("");
    } catch (IOException e) {
      return "";
    }
  }

  static boolean checkProtoCompilerPathValid(String pathString) {
    return checkProtoCompilerPathValid(Paths.get(pathString));
  }

  private static boolean checkProtoCompilerPathValid(Path path) {
    return path.toFile().isFile() & path.getFileName().toString().startsWith(PROTOC_NAME);
  }
}
