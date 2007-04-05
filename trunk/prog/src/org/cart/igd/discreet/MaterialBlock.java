package org.cart.igd.discreet;

public class MaterialBlock
{
    /** The name of this material */
    public String name;

    /** The ambient colour of this block - transcribed to 3 value RGB [0,1] */
    public float[] ambientColor;

    /** The diffuse colour of this block - transcribed to 3 value RGB [0,1] */
    public float[] diffuseColor;

    /** The specular colour of this block - transcribed to 3 value RGB [0,1] */
    public float[] specularColor;

    /** The shininess ratio to a [0,1] range */
    public float shininessRatio;

    /**
     * The shininess converted to a [0,1] range. This is the typical shininess
     * that one expects of a material property.
     */
    public float shininessStrength;

    /** The transparency converted to a [0,1] range */
    public float transparency;

    /** Flag indicating if the object should be rendered as wireframe */
    public boolean wireframe;

    /** The wire size to use if wireframe */
    public float wireSize;

    /** The shading type. What each value means is not known right now */
    public int shadingType;

    /** Flag indicating the transparency blend is additive */
    public boolean additiveBlend;

    /** Flag indicating 2-sided lighting is set or not */
    public boolean twoSidedLighting;

    /** The first texture map to be associated with this material */
    public TextureBlock textureMap1;

    /** The first texture map to be associated with this material */
    public TextureBlock textureMask1;

    /** The second texture map to be associated with this material */
    public TextureBlock textureMap2;

    /** The second texture map to be associated with this material */
    public TextureBlock textureMask2;

    /** The specular light map to be associated with this material */
    public TextureBlock specularMap;

    /** The specular light map to be associated with this material */
    public TextureBlock specularMask;

    /** The opacity map to be associated with this material */
    public TextureBlock opacityMap;

    /** The opacity map to be associated with this material */
    public TextureBlock opacityMask;

    /** The bump map to be associated with this material */
    public TextureBlock bumpMap;

    /** The bump map to be associated with this material */
    public TextureBlock bumpMask;

    /** The reflection map (environment map) associated with this material */
    public TextureBlock reflectionMap;

    /** The reflection map (environment map) associated with this material */
    public TextureBlock reflectionMask;

    /** The shininess map associated with this material */
    public TextureBlock shininessMap;

    /** The shininess map associated with this material */
    public TextureBlock shininessMask;

    /**
     * Create a new instance of this material block with the defaults set.
     */
    public MaterialBlock()
    {
        twoSidedLighting = false;
        wireframe = false;
        additiveBlend = false;
        transparency = 1;
    }
}
