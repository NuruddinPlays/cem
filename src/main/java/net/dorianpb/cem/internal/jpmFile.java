package net.dorianpb.cem.internal;

import com.google.gson.internal.LinkedTreeMap;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

class jpmFile {
    private final String id;
    private final String texture;
    private final ArrayList<Double> textureSize;
    private final Boolean[] invertAxis;
    private final ArrayList<Double> translate;
    private final ArrayList<Double> rotate;
    private final Boolean[] mirrorTexture;
    private final ArrayList<jpmBox> boxes;
    private final ArrayList<jpmSprite> sprites;
    private final ArrayList<jpmFile> submodels;


    @SuppressWarnings({"unchecked","rawtypes"})
    public jpmFile(LinkedTreeMap json) {
        this.id = (String) json.get("id");
        this.texture = (String) json.get("texture");
        this.textureSize = (ArrayList<Double>) json.get("textureSize");

        String axes = (String) json.getOrDefault("invertAxis","");
        this.invertAxis = new Boolean[]{axes.indexOf('x') > -1,axes.indexOf('y') > -1,axes.indexOf('z') > -1};

        this.translate = (ArrayList<Double>) json.getOrDefault("translate",new ArrayList<>(Arrays.asList(0D, 0D, 0D)));
        this.rotate = (ArrayList<Double>) json.getOrDefault("rotate",new ArrayList<>(Arrays.asList(0D, 0D, 0D)));
        for(int i=0;i<this.rotate.size();i++){
            this.rotate.set(i,-Math.toRadians(this.rotate.get(i)));
        }
        
        String mirror = (String) json.getOrDefault("mirrorTexture","");
        this.mirrorTexture = new Boolean[]{mirror.contains("u"),mirror.contains("v")};

        if(json.containsKey("boxes")) {
            this.boxes = new ArrayList<>();
            for (LinkedTreeMap cube : (ArrayList<LinkedTreeMap>) json.get("boxes")) {
                this.boxes.add(new jpmBox(cube));
            }
        } else {this.boxes = null;}
        if(json.containsKey("sprites")) {
            this.sprites = new ArrayList<>();
            for (LinkedTreeMap sprite : (ArrayList<LinkedTreeMap>) json.get("sprites")) {
                this.sprites.add(new jpmSprite(sprite));
            }
        } else {this.sprites = null;}
        if(json.containsKey("submodel")||json.containsKey("submodels")){
            this.submodels = new ArrayList<>();
            if(json.containsKey("submodel")){
                this.submodels.add(new jpmFile((LinkedTreeMap) json.get("submodel")));
            }
            if(json.containsKey("submodels")){
                for(LinkedTreeMap submodel : (ArrayList<LinkedTreeMap>) json.get("submodels")){
                    this.submodels.add(new jpmFile(submodel));
                }
            }
        } else {this.submodels = null;}
    }

    ArrayList<jpmBox> getBoxes() {
        return boxes;
    }

    ArrayList<Double> getTranslate() {
        return translate;
    }
    
    Boolean[] getInvertAxis(){
        return invertAxis;
    }
    
    ArrayList<jpmFile> getSubmodels(){
        return submodels;
    }
    
    ArrayList<Double> getRotate(){
        return this.rotate;
    }
    
    String getId(){
        return id;
    }
    
    Boolean[] getMirrorTexture(){
        return mirrorTexture;
    }
    
    static class jpmBox {
        private final ArrayList<Double> textureOffset;
        private final ArrayList<Double> uvUp;
        private final ArrayList<Double> uvDown;
        private final ArrayList<Double> uvFront;
        private final ArrayList<Double> uvBack;
        private final ArrayList<Double> uvLeft;
        private final ArrayList<Double> uvRight;
        private final ArrayList<Double> coordinates;
        private final Double sizeAdd;
    
        @SuppressWarnings({"unchecked","rawtypes"})
        jpmBox(LinkedTreeMap json) {
            ArrayList<Double> zeroes = new ArrayList<>(Arrays.asList(0D, 0D, 0D, 0D));
            this.textureOffset = (ArrayList<Double>) json.get("textureOffset");
            this.uvUp = (ArrayList<Double>) json.getOrDefault("uvUp",zeroes);
            this.uvDown = (ArrayList<Double>) json.getOrDefault("uvDown",zeroes);
            this.uvFront = (ArrayList<Double>) json.getOrDefault("uvFront",json.getOrDefault("uvNorth",zeroes));
            this.uvBack = (ArrayList<Double>) json.getOrDefault("uvBack",json.getOrDefault("uvSouth",zeroes));
            this.uvLeft = (ArrayList<Double>) json.getOrDefault("uvLeft",json.getOrDefault("uvWest",zeroes));
            this.uvRight = (ArrayList<Double>) json.getOrDefault("uvRight",json.getOrDefault("uvEast",zeroes));
            this.coordinates = (ArrayList<Double>) json.get("coordinates");
            this.sizeAdd = (Double) json.getOrDefault("sizeAdd",0D);
            this.validate();
        }
    
        private void validate(){
            if(this.textureOffset==null){
                String str = "Element \"textureOffset\" is required";
                throw new InvalidParameterException(
                        (this.uvUp!=null||this.uvDown!=null||this.uvFront!=null||this.uvBack!=null||this.uvLeft!=null||this.uvRight!=null)
                                ?str+"; Specifying texture using uv coordinates is not supported"
                                :str
                );
            }
            if(this.coordinates==null){
                throw new InvalidParameterException("Element \"coordinates\" is required");
            }
        }
    
        ArrayList<Double> getTextureOffset() {
            return textureOffset;
        }
    
        ArrayList<Double> getCoordinates() {
            return coordinates;
        }
    
        Double getSizeAdd() {
            return sizeAdd;
        }
        
    }
    
    static class jpmSprite {
        private final ArrayList<Integer> textureOffset;
        private final ArrayList<Integer> coordinates;
        private final Double sizeAdd;
    
        @SuppressWarnings({"unchecked","rawtypes"})
        jpmSprite(LinkedTreeMap json) {
            this.textureOffset = (ArrayList<Integer>) json.get("textureOffset");
            this.coordinates = (ArrayList<Integer>) json.get("coordinates");
            this.sizeAdd = (Double) json.get("sizeAdd");
        }
    }
}

