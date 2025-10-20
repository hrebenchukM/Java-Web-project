package learning.itstep.javaweb222.services.config;


public interface ConfigService {
    void addFile(String filename);
    String get(String path);
}