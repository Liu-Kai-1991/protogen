package org.kai.protogen.compile;

import com.intellij.openapi.vfs.VirtualFile;

class BuildParameter {
  private String protoPath;
  private String language;
  private String outputPath;
  private String protoCompilerPath;
  private VirtualFile file;
  private String projectBasePath;

  String getProtoPath() {
    return protoPath;
  }

  String getProjectBasePath() {
    return projectBasePath;
  }

  String getLanguage() {
    return language;
  }

  String getOutputPath() {
    return outputPath;
  }

  String getProtoCompilerPath() {
    return protoCompilerPath;
  }

  VirtualFile getFile() {
    return file;
  }

  private BuildParameter() {
  }
  
  static BuildParameterBuilder builder(){
    return new BuildParameterBuilder();
  }

  static final class BuildParameterBuilder {

    private String protoPath;
    private String language;
    private String outputPath;
    private String protoCompilerPath;
    private VirtualFile file;
    private String projectBasePath;

    BuildParameterBuilder() {
    }

    BuildParameterBuilder addProtoPath(String protoPath) {
      this.protoPath = protoPath;
      return this;
    }

    BuildParameterBuilder addLanguage(String language) {
      this.language = language;
      return this;
    }

    BuildParameterBuilder addProjectBasePath(String projectBasePath) {
      this.projectBasePath = projectBasePath;
      return this;
    }

    BuildParameterBuilder addOutputPath(String outputPath) {
      this.outputPath = outputPath;
      return this;
    }

    BuildParameterBuilder addProtoCompilerPath(String protoCompilerPath) {
      this.protoCompilerPath = protoCompilerPath;
      return this;
    }

    BuildParameterBuilder addFile(VirtualFile file) {
      this.file = file;
      return this;
    }

    BuildParameter build() {
      BuildParameter buildParameter = new BuildParameter();
      buildParameter.outputPath = this.outputPath;
      buildParameter.protoPath = this.protoPath;
      buildParameter.language = this.language;
      buildParameter.file = this.file;
      buildParameter.protoCompilerPath = this.protoCompilerPath;
      buildParameter.projectBasePath = this.projectBasePath;
      return buildParameter;
    }
  }
}
