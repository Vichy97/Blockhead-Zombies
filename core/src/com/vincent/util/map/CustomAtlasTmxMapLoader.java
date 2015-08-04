package com.vincent.util.map;

import java.io.IOException;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.BaseTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Created by Vincent on 7/19/2015.
 *
 * identical to the normal AtlasMapLoader except it uses CustomMapObjects rather than TextureMapObjects to make depth sorting easier
 */
public class CustomAtlasTmxMapLoader extends BaseTmxMapLoader<CustomAtlasTmxMapLoader.CustomAtlasTiledMapLoaderParameters> {

    public static class CustomAtlasTiledMapLoaderParameters extends BaseTmxMapLoader.Parameters {
        /** force texture filters? **/
        public boolean forceTextureFilters = false;
    }

    protected Array<Texture> trackedTextures = new Array<Texture>();

    private interface AtlasResolver {

        public TextureAtlas getAtlas (String name);

        public static class DirectAtlasResolver implements AtlasResolver {

            private final ObjectMap<String, TextureAtlas> atlases;

            public DirectAtlasResolver (ObjectMap<String, TextureAtlas> atlases) {
                this.atlases = atlases;
            }

            @Override
            public TextureAtlas getAtlas (String name) {
                return atlases.get(name);
            }

        }

        public static class AssetManagerAtlasResolver implements AtlasResolver {
            private final AssetManager assetManager;

            public AssetManagerAtlasResolver (AssetManager assetManager) {
                this.assetManager = assetManager;
            }

            @Override
            public TextureAtlas getAtlas (String name) {
                return assetManager.get(name, TextureAtlas.class);
            }
        }
    }

    public CustomAtlasTmxMapLoader () {
        super(new InternalFileHandleResolver());
    }

    public CustomAtlasTmxMapLoader (FileHandleResolver resolver) {
        super(resolver);
    }

    public TiledMap load (String fileName) {
        return load(fileName, new CustomAtlasTiledMapLoaderParameters());
    }

    @Override
    public Array<AssetDescriptor> getDependencies (String fileName, FileHandle tmxFile, CustomAtlasTiledMapLoaderParameters parameter) {
        Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
        try {
            root = xml.parse(tmxFile);

            Element properties = root.getChildByName("properties");
            if (properties != null) {
                for (Element property : properties.getChildrenByName("property")) {
                    String name = property.getAttribute("name");
                    String value = property.getAttribute("value");
                    if (name.startsWith("atlas")) {
                        FileHandle atlasHandle = getRelativeFileHandle(tmxFile, value);
                        dependencies.add(new AssetDescriptor(atlasHandle, TextureAtlas.class));
                    }
                }
            }
        } catch (IOException e) {
            throw new GdxRuntimeException("Unable to parse .tmx file.");
        }
        return dependencies;
    }

    public TiledMap load (String fileName, CustomAtlasTiledMapLoaderParameters parameter) {
        try {
            if (parameter != null) {
                convertObjectToTileSpace = parameter.convertObjectToTileSpace;
                flipY = parameter.flipY;
            } else {
                convertObjectToTileSpace = false;
                flipY = true;
            }

            FileHandle tmxFile = resolve(fileName);
            root = xml.parse(tmxFile);
            ObjectMap<String, TextureAtlas> atlases = new ObjectMap<String, TextureAtlas>();
            FileHandle atlasFile = loadAtlas(root, tmxFile);
            if (atlasFile == null) {
                throw new GdxRuntimeException("Couldn't load atlas");
            }

            TextureAtlas atlas = new TextureAtlas(atlasFile);
            atlases.put(atlasFile.path(), atlas);

            AtlasResolver.DirectAtlasResolver atlasResolver = new AtlasResolver.DirectAtlasResolver(atlases);
            TiledMap map = loadMap(root, tmxFile, atlasResolver);
            map.setOwnedResources(atlases.values().toArray());
            setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
            return map;
        } catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }

    /** May return null. */
    protected FileHandle loadAtlas (Element root, FileHandle tmxFile) throws IOException {
        Element e = root.getChildByName("properties");

        if (e != null) {
            for (Element property : e.getChildrenByName("property")) {
                String name = property.getAttribute("name", null);
                String value = property.getAttribute("value", null);
                if (name.equals("atlas")) {
                    if (value == null) {
                        value = property.getText();
                    }

                    if (value == null || value.length() == 0) {
                        // keep trying until there are no more atlas properties
                        continue;
                    }

                    return getRelativeFileHandle(tmxFile, value);
                }
            }
        }
        FileHandle atlasFile = tmxFile.sibling(tmxFile.nameWithoutExtension() + ".atlas");
        return atlasFile.exists() ? atlasFile : null;
    }

    private void setTextureFilters (TextureFilter min, TextureFilter mag) {
        for (Texture texture : trackedTextures) {
            texture.setFilter(min, mag);
        }
        trackedTextures.clear();
    }

    @Override
    public void loadAsync (AssetManager manager, String fileName, FileHandle tmxFile, CustomAtlasTiledMapLoaderParameters parameter) {
        map = null;

        if (parameter != null) {
            convertObjectToTileSpace = parameter.convertObjectToTileSpace;
            flipY = parameter.flipY;
        } else {
            convertObjectToTileSpace = false;
            flipY = true;
        }

        try {
            map = loadMap(root, tmxFile, new AtlasResolver.AssetManagerAtlasResolver(manager));
        } catch (Exception e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }

    @Override
    public TiledMap loadSync (AssetManager manager, String fileName, FileHandle file, CustomAtlasTiledMapLoaderParameters parameter) {
        if (parameter != null) {
            setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
        }

        return map;
    }

    protected TiledMap loadMap (Element root, FileHandle tmxFile, AtlasResolver resolver) {
        TiledMap map = new TiledMap();

        String mapOrientation = root.getAttribute("orientation", null);
        int mapWidth = root.getIntAttribute("width", 0);
        int mapHeight = root.getIntAttribute("height", 0);
        int tileWidth = root.getIntAttribute("tilewidth", 0);
        int tileHeight = root.getIntAttribute("tileheight", 0);
        String mapBackgroundColor = root.getAttribute("backgroundcolor", null);

        MapProperties mapProperties = map.getProperties();
        if (mapOrientation != null) {
            mapProperties.put("orientation", mapOrientation);
        }
        mapProperties.put("width", mapWidth);
        mapProperties.put("height", mapHeight);
        mapProperties.put("tilewidth", tileWidth);
        mapProperties.put("tileheight", tileHeight);
        if (mapBackgroundColor != null) {
            mapProperties.put("backgroundcolor", mapBackgroundColor);
        }

        mapTileWidth = tileWidth;
        mapTileHeight = tileHeight;
        mapWidthInPixels = mapWidth * tileWidth;
        mapHeightInPixels = mapHeight * tileHeight;

        if (mapOrientation != null) {
            if ("staggered".equals(mapOrientation)) {
                if (mapHeight > 1) {
                    mapWidthInPixels += tileWidth / 2;
                    mapHeightInPixels = mapHeightInPixels / 2 + tileHeight / 2;
                }
            }
        }

        for (int i = 0, j = root.getChildCount(); i < j; i++) {
            Element element = root.getChild(i);
            String elementName = element.getName();
            if (elementName.equals("properties")) {
                loadProperties(map.getProperties(), element);
            } else if (elementName.equals("tileset")) {
                loadTileset(map, element, tmxFile, resolver);
            } else if (elementName.equals("layer")) {
                loadTileLayer(map, element);
            } else if (elementName.equals("objectgroup")) {
                loadObjectGroup(map, element);
            }
        }
        return map;
    }

    protected void loadTileset (TiledMap map, Element element, FileHandle tmxFile, AtlasResolver resolver) {
        if (element.getName().equals("tileset")) {
            String name = element.get("name", null);
            int firstgid = element.getIntAttribute("firstgid", 1);
            int tilewidth = element.getIntAttribute("tilewidth", 0);
            int tileheight = element.getIntAttribute("tileheight", 0);
            int spacing = element.getIntAttribute("spacing", 0);
            int margin = element.getIntAttribute("margin", 0);
            String source = element.getAttribute("source", null);

            int offsetX = 0;
            int offsetY = 0;

            String imageSource = "";
            int imageWidth = 0, imageHeight = 0;

            FileHandle image = null;
            if (source != null) {
                FileHandle tsx = getRelativeFileHandle(tmxFile, source);
                try {
                    element = xml.parse(tsx);
                    name = element.get("name", null);
                    tilewidth = element.getIntAttribute("tilewidth", 0);
                    tileheight = element.getIntAttribute("tileheight", 0);
                    spacing = element.getIntAttribute("spacing", 0);
                    margin = element.getIntAttribute("margin", 0);
                    Element offset = element.getChildByName("tileoffset");
                    if (offset != null) {
                        offsetX = offset.getIntAttribute("x", 0);
                        offsetY = offset.getIntAttribute("y", 0);
                    }
                    Element imageElement = element.getChildByName("image");
                    if (imageElement != null) {
                        imageSource = imageElement.getAttribute("source");
                        imageWidth = imageElement.getIntAttribute("width", 0);
                        imageHeight = imageElement.getIntAttribute("height", 0);
                        image = getRelativeFileHandle(tsx, imageSource);
                    }
                } catch (IOException e) {
                    throw new GdxRuntimeException("Error parsing external tileset.");
                }
            } else {
                Element offset = element.getChildByName("tileoffset");
                if (offset != null) {
                    offsetX = offset.getIntAttribute("x", 0);
                    offsetY = offset.getIntAttribute("y", 0);
                }
                Element imageElement = element.getChildByName("image");
                if (imageElement != null) {
                    imageSource = imageElement.getAttribute("source");
                    imageWidth = imageElement.getIntAttribute("width", 0);
                    imageHeight = imageElement.getIntAttribute("height", 0);
                    image = getRelativeFileHandle(tmxFile, imageSource);
                }
            }

            String atlasFilePath = map.getProperties().get("atlas", String.class);
            if (atlasFilePath == null) {
                FileHandle atlasFile = tmxFile.sibling(tmxFile.nameWithoutExtension() + ".atlas");
                if (atlasFile.exists()) atlasFilePath = atlasFile.name();
            }
            if (atlasFilePath == null) {
                throw new GdxRuntimeException("The map is missing the 'atlas' property");
            }

            // get the TextureAtlas for this tileset
            FileHandle atlasHandle = getRelativeFileHandle(tmxFile, atlasFilePath);
            atlasHandle = resolve(atlasHandle.path());
            TextureAtlas atlas = resolver.getAtlas(atlasHandle.path());
            String regionsName = atlasHandle.nameWithoutExtension();

            for (Texture texture : atlas.getTextures()) {
                trackedTextures.add(texture);
            }

            TiledMapTileSet tileset = new TiledMapTileSet();
            MapProperties props = tileset.getProperties();
            tileset.setName(name);
            props.put("firstgid", firstgid);
            props.put("imagesource", imageSource);
            props.put("imagewidth", imageWidth);
            props.put("imageheight", imageHeight);
            props.put("tilewidth", tilewidth);
            props.put("tileheight", tileheight);
            props.put("margin", margin);
            props.put("spacing", spacing);

            if (imageSource != null && imageSource.length() > 0) {
                int lastgid = firstgid + ((imageWidth / tilewidth) * (imageHeight / tileheight)) - 1;
                for (AtlasRegion region : atlas.findRegions(regionsName)) {
                    // handle unused tile ids
                    if (region != null) {
                        int tileid = region.index + 1;
                        if (tileid >= firstgid && tileid <= lastgid) {
                            StaticTiledMapTile tile = new StaticTiledMapTile(region);
                            tile.setId(tileid);
                            tile.setOffsetX(offsetX);
                            tile.setOffsetY(flipY ? -offsetY : offsetY);
                            tileset.putTile(tileid, tile);
                        }
                    }
                }
            }

            for (Element tileElement : element.getChildrenByName("tile")) {
                int tileid = firstgid + tileElement.getIntAttribute("id", 0);
                TiledMapTile tile = tileset.getTile(tileid);
                if (tile == null) {
                    Element imageElement = tileElement.getChildByName("image");
                    if (imageElement != null) {
                        // Is a tilemap with individual images.
                        String regionName = imageElement.getAttribute("source");
                        regionName = regionName.substring(0, regionName.lastIndexOf('.'));
                        AtlasRegion region = atlas.findRegion(regionName);
                        if (region == null) throw new GdxRuntimeException("Tileset region not found: " + regionName);
                        tile = new StaticTiledMapTile(region);
                        tile.setId(tileid);
                        tile.setOffsetX(offsetX);
                        tile.setOffsetY(flipY ? -offsetY : offsetY);
                        tileset.putTile(tileid, tile);
                    }
                }
                if (tile != null) {
                    String terrain = tileElement.getAttribute("terrain", null);
                    if (terrain != null) {
                        tile.getProperties().put("terrain", terrain);
                    }
                    String probability = tileElement.getAttribute("probability", null);
                    if (probability != null) {
                        tile.getProperties().put("probability", probability);
                    }
                    Element properties = tileElement.getChildByName("properties");
                    if (properties != null) {
                        loadProperties(tile.getProperties(), properties);
                    }
                }
            }

            Array<Element> tileElements = element.getChildrenByName("tile");

            Array<AnimatedTiledMapTile> animatedTiles = new Array<AnimatedTiledMapTile>();

            for (Element tileElement : tileElements) {
                int localtid = tileElement.getIntAttribute("id", 0);
                TiledMapTile tile = tileset.getTile(firstgid + localtid);
                if (tile != null) {
                    Element animationElement = tileElement.getChildByName("animation");
                    if (animationElement != null) {

                        Array<StaticTiledMapTile> staticTiles = new Array<StaticTiledMapTile>();
                        IntArray intervals = new IntArray();
                        for (Element frameElement: animationElement.getChildrenByName("frame")) {
                            staticTiles.add((StaticTiledMapTile) tileset.getTile(firstgid + frameElement.getIntAttribute("tileid")));
                            intervals.add(frameElement.getIntAttribute("duration"));
                        }

                        AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(intervals, staticTiles);
                        animatedTile.setId(tile.getId());
                        animatedTiles.add(animatedTile);
                        tile = animatedTile;
                    }

                    String terrain = tileElement.getAttribute("terrain", null);
                    if (terrain != null) {
                        tile.getProperties().put("terrain", terrain);
                    }
                    String probability = tileElement.getAttribute("probability", null);
                    if (probability != null) {
                        tile.getProperties().put("probability", probability);
                    }
                    Element properties = tileElement.getChildByName("properties");
                    if (properties != null) {
                        loadProperties(tile.getProperties(), properties);
                    }
                }
            }

            for (AnimatedTiledMapTile tile : animatedTiles) {
                tileset.putTile(tile.getId(), tile);
            }

            Element properties = element.getChildByName("properties");
            if (properties != null) {
                loadProperties(tileset.getProperties(), properties);
            }
            map.getTileSets().addTileSet(tileset);
        }
    }


    protected void loadObject (TiledMap map, MapLayer layer, Element element) {
        if (element.getName().equals("object")) {
            MapObject object = null;

            float scaleX = convertObjectToTileSpace ? 1.0f / mapTileWidth : 1.0f;
            float scaleY = convertObjectToTileSpace ? 1.0f / mapTileHeight : 1.0f;

            float x = element.getFloatAttribute("x", 0) * scaleX;
            float y = (mapHeightInPixels - element.getFloatAttribute("y", 0)) * scaleY;

            float width = element.getFloatAttribute("width", 0) * scaleX;
            float height = element.getFloatAttribute("height", 0) * scaleY;

            if (element.getChildCount() > 0) {
                Element child = null;
                if ((child = element.getChildByName("polygon")) != null) {
                    String[] points = child.getAttribute("points").split(" ");
                    float[] vertices = new float[points.length * 2];
                    for (int i = 0; i < points.length; i++) {
                        String[] point = points[i].split(",");
                        vertices[i * 2] = Float.parseFloat(point[0]) * scaleX;
                        vertices[i * 2 + 1] = -Float.parseFloat(point[1]) * scaleY;
                    }
                    Polygon polygon = new Polygon(vertices);
                    polygon.setPosition(x, y);
                    object = new PolygonMapObject(polygon);
                } else if ((child = element.getChildByName("polyline")) != null) {
                    String[] points = child.getAttribute("points").split(" ");
                    float[] vertices = new float[points.length * 2];
                    for (int i = 0; i < points.length; i++) {
                        String[] point = points[i].split(",");
                        vertices[i * 2] = Float.parseFloat(point[0]) * scaleX;
                        vertices[i * 2 + 1] = -Float.parseFloat(point[1]) * scaleY;
                    }
                    Polyline polyline = new Polyline(vertices);
                    polyline.setPosition(x, y);
                    object = new PolylineMapObject(polyline);
                } else if ((child = element.getChildByName("ellipse")) != null) {
                    object = new EllipseMapObject(x, y - height, width, height);
                }
            }
            if (object == null) {
                String gid = null;
                if ((gid = element.getAttribute("gid", null)) != null) {
                    int id = (int)Long.parseLong(gid);
                    boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
                    boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);
                    TiledMapTile tile = map.getTileSets().getTile(id & ~MASK_CLEAR);
                    TextureRegion textureRegion = new TextureRegion(tile.getTextureRegion());
                    textureRegion.flip(flipHorizontally, flipVertically);
                    CustomMapObject textureMapObject = new CustomMapObject(textureRegion, x, y - height, scaleX, scaleY);
                    textureMapObject.getProperties().put("gid", id);
                    textureMapObject.setRotation(element.getFloatAttribute("rotation", 0));
                    object = textureMapObject;
                } else {
                    object = new RectangleMapObject(x, y - height, width, height);
                }
            }
            object.setName(element.getAttribute("name", null));
            String rotation = element.getAttribute("rotation", null);
            if (rotation != null) {
                object.getProperties().put("rotation", Float.parseFloat(rotation));
            }
            String type = element.getAttribute("type", null);
            if (type != null) {
                object.getProperties().put("type", type);
            }
            object.getProperties().put("x", x * scaleX);
            object.getProperties().put("y", (y - height) * scaleY);
            object.getProperties().put("width", width);
            object.getProperties().put("height", height);
            object.setVisible(element.getIntAttribute("visible", 1) == 1);
            Element properties = element.getChildByName("properties");
            if (properties != null) {
                loadProperties(object.getProperties(), properties);
            }
            layer.getObjects().add(object);
        }
    }
}

