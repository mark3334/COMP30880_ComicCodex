import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;

class FullSceneGenerator implements SceneGeneratorInterface {
    private static final boolean[] FEATURE_PATTERN = {true, true, true, true, true};

    @Override
    public boolean matches(VignetteSchema schema) {
        boolean[] actual = SceneGeneratorManager.getFeatureVector(schema);
        return SceneGeneratorManager.matchesPattern(actual, FEATURE_PATTERN);
    }

    @Override
    public Document generate(VignetteSchema schema) throws ParserConfigurationException {
        return SceneGeneratorManager.generateDom(schema, "full");
    }
}

class NoLeftTextSceneGenerator implements SceneGeneratorInterface {
    private static final boolean[] FEATURE_PATTERN = {true, false, true, true, true};

    @Override
    public boolean matches(VignetteSchema schema) {
        boolean[] actual = SceneGeneratorManager.getFeatureVector(schema);
        return SceneGeneratorManager.matchesPattern(actual, FEATURE_PATTERN);
    }

    @Override
    public Document generate(VignetteSchema schema) throws ParserConfigurationException {
        return SceneGeneratorManager.generateDom(schema, "no_left_text");
    }
}

class OnlyCombinedTextSceneGenerator implements SceneGeneratorInterface {
    private static final boolean[] FEATURE_PATTERN = {true, false, false, true, true};

    @Override
    public boolean matches(VignetteSchema schema) {
        boolean[] actual = SceneGeneratorManager.getFeatureVector(schema);
        return SceneGeneratorManager.matchesPattern(actual, FEATURE_PATTERN);
    }

    @Override
    public Document generate(VignetteSchema schema) throws ParserConfigurationException {
        return SceneGeneratorManager.generateDom(schema, "only_combined_text");
    }
}

class LeftTextOnlySceneGenerator implements SceneGeneratorInterface {
    private static final boolean[] FEATURE_PATTERN = {true, true, false, false, false};

    @Override
    public boolean matches(VignetteSchema schema) {
        boolean[] actual = SceneGeneratorManager.getFeatureVector(schema);
        return SceneGeneratorManager.matchesPattern(actual, FEATURE_PATTERN);
    }

    @Override
    public Document generate(VignetteSchema schema) throws ParserConfigurationException {
        return SceneGeneratorManager.generateDom(schema, "left_text_only");
    }
}
