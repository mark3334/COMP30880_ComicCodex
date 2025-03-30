import java.util.List;

public class SceneGeneratorRegistry {
    private static final List<SceneGeneratorInterface> generators = List.of(
            new FullSceneGenerator(),
            new NoLeftTextSceneGenerator(),
            new OnlyCombinedTextSceneGenerator(),
            new LeftTextOnlySceneGenerator()
    );

    public static String generateScene(VignetteSchema schema) {
        for (SceneGeneratorInterface gen : generators) {
            if (gen.matches(schema)) {
                return gen.generate(schema);
            }
        }
        return "<!-- Skipped scene: No suitable generator -->";
    }
}
