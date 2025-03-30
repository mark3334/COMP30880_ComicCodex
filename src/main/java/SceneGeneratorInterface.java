public interface SceneGeneratorInterface {
    boolean matches(VignetteSchema schema);
    String generate(VignetteSchema schema);
}
