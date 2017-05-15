package ch.bildspur.postfx.builder;

import ch.bildspur.postfx.PostFXSupervisor;
import ch.bildspur.postfx.pass.*;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * PostFX builder pattern.
 */
public class PostFXBuilder {
    private PostFXSupervisor supervisor;
    private PostFX fx;

    private Map<String, Pass> passes;

    protected PostFXBuilder(PostFX fx, PostFXSupervisor supervisor) {
        this.fx = fx;
        this.supervisor = supervisor;

        passes = new HashMap<>();
    }

    private <T extends Pass> T getPass(Class<T> type) {
        if (passes.containsKey(type.getName()))
            return (T) passes.get(type.getName());

        // initialize class lazy and use constructor zero
        T pass = null;

        try {
            Constructor<?> constructor = type.getConstructor(PApplet.class);
            pass = (T) constructor.newInstance(fx.sketch);
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InstantiationException
                | InvocationTargetException e) {
            e.printStackTrace();
        }

        // add pass
        passes.put(type.getName(), pass);

        return pass;
    }

    /**
     * Compose and finalize rendering onto sketch texture.
     */
    public void compose() {
        supervisor.compose();
    }

    /**
     * Compose and finalize rendering.
     *
     * @param graphics Texture to render onto.
     */
    public void compose(PGraphics graphics) {
        supervisor.compose(graphics);
    }

    /**
     * Run a blur pass on the texture.
     *
     * @param blurSize Size of the blur.
     * @param sigma    Sigma of the blur.
     * @return Builder object.
     */
    public PostFXBuilder blur(int blurSize, float sigma) {
        blur(blurSize, sigma, false);
        blur(blurSize, sigma, true);
        return this;
    }

    /**
     * Run a blur pass on the texture.
     *
     * @param blurSize   Size of the blur.
     * @param sigma      Sigma of the blur.
     * @param horizontal Indicates if the pass runs horizontal or vertical.
     * @return Builder object.
     */
    public PostFXBuilder blur(int blurSize, float sigma, boolean horizontal) {
        BlurPass pass = getPass(BlurPass.class);

        pass.setBlurSize(blurSize);
        pass.setSigma(sigma);
        pass.setHorizontal(horizontal);

        supervisor.pass(pass);
        return this;
    }

    /**
     * Run a bright pass pass on the texture.
     *
     * @param threshold Threshold of the brightness.
     * @return Builder object.
     */
    public PostFXBuilder brightPass(float threshold) {
        BrightPass pass = getPass(BrightPass.class);

        pass.setThreshold(threshold);

        supervisor.pass(pass);
        return this;
    }

    /**
     * Run a sobel edge detection pass on the texture.
     *
     * @return Builder object.
     */
    public PostFXBuilder sobel() {
        SobelPass pass = getPass(SobelPass.class);
        supervisor.pass(pass);
        return this;
    }

    /**
     * Run a toon pass on the texture.
     *
     * @return Builder object.
     */
    public PostFXBuilder toon() {
        ToonPass pass = getPass(ToonPass.class);
        supervisor.pass(pass);
        return this;
    }

    /**
     * Run an invert pass on the texture.
     *
     * @return Builder object.
     */
    public PostFXBuilder invert() {
        InvertPass pass = getPass(InvertPass.class);
        supervisor.pass(pass);
        return this;
    }

    /**
     * Run a brightness and contrast correction pass on the texture.
     *
     * @param brightness Amount of brightness to add to the picture (default 0.0).
     * @param contrast   Contrast of the image (default 1.0).
     * @return Builder object.
     */
    public PostFXBuilder brightnessContrast(float brightness, float contrast) {
        BrightnessContrastPass pass = getPass(BrightnessContrastPass.class);

        pass.setBrightness(brightness);
        pass.setContrast(contrast);

        supervisor.pass(pass);
        return this;
    }

    /**
     * Run a pixel effect pass on the texture.
     *
     * @param amount Amount of the pixel effect.
     * @return Builder object.
     */
    public PostFXBuilder pixelate(float amount) {
        PixelatePass pass = getPass(PixelatePass.class);

        pass.setAmount(amount);

        supervisor.pass(pass);
        return this;
    }

    /**
     * Run a chromatic aberration effect pass on the texture.
     *
     * @return Builder object.
     */
    public PostFXBuilder chromaticAberration() {
        ChromaticAberrationPass pass = getPass(ChromaticAberrationPass.class);
        supervisor.pass(pass);
        return this;
    }

    /**
     * Run a grayscale pass on the texture.
     *
     * @return Builder object.
     */
    public PostFXBuilder grayScale() {
        GrayScalePass pass = getPass(GrayScalePass.class);
        supervisor.pass(pass);
        return this;
    }

    /**
     * Run a bloom effect on the texture.
     *
     * @param threshold Luminance threshold.
     * @param blurSize  Size of the blur.
     * @param sigma     Sigma of the blur.
     * @return Builder object.
     */
    public PostFXBuilder bloom(float threshold, int blurSize, float sigma) {
        BloomPass pass = getPass(BloomPass.class);

        pass.setThreshold(threshold);
        pass.setBlurSize(blurSize);
        pass.setSigma(sigma);

        supervisor.pass(pass);
        return this;
    }
}
