package com.recipeessentials.config;

import com.cupboard.config.ICommonConfig;
import com.google.gson.JsonObject;

public class CommonConfiguration implements ICommonConfig
{
    public boolean disableRecipebook             = false;
    public boolean fastItemComparisons           = true;
    public boolean enableBetterRecipebookSorting = true;
    public boolean smallerRecipePacket           = false;
    public boolean cacheRecipes                  = true;
    public boolean logCachingErrors              = false;
    public boolean recipebookShowAll             = true;

    public CommonConfiguration()
    {
    }

    public JsonObject serialize()
    {
        final JsonObject root = new JsonObject();

        final JsonObject entry = new JsonObject();
        entry.addProperty("desc:", "Disables the recipe book entirely, removes the button on client side and removes synced data on server side: default:false");
        entry.addProperty("disableRecipebook", disableRecipebook);
        root.add("disableRecipebook", entry);

        final JsonObject entry3 = new JsonObject();
        entry3.addProperty("desc:", "Enables better sorting for the list of displayed recipes in the recipebook, craftables and recently used recipes are priotized: default:true");
        entry3.addProperty("enableBetterRecipebookSorting", enableBetterRecipebookSorting);
        root.add("enableBetterRecipebookSorting", entry3);

        final JsonObject entry8 = new JsonObject();
        entry8.addProperty("desc:",
          "Enables the recipe book to show all recipes from the get go, also enables modded recipes to show and disables network/login overhead: default:true");
        entry8.addProperty("recipebookShowAll", recipebookShowAll);
        root.add("recipebookShowAll", entry8);

        final JsonObject entry2 = new JsonObject();
        entry2.addProperty("desc:", "Enables faster item comparison for better performance, disable on mod conflicts: default:true");
        entry2.addProperty("fastItemComparisons", fastItemComparisons);
        root.add("fastItemComparisons", entry2);

        final JsonObject entry4 = new JsonObject();
        entry4.addProperty("desc:",
          "Reduces the size of the recipe packet, to prevent errors on too large packets and helps bad connections, requires the mod to be present on both client and server, disable on mod conflicts: default:false");
        entry4.addProperty("smallerRecipePacket", smallerRecipePacket);
        root.add("smallerRecipePacket", entry4);

        final JsonObject entry5 = new JsonObject();
        entry5.addProperty("desc:", "Caches recipe lookups to greatly improve lookup speed: default:true");
        entry5.addProperty("cacheRecipes", cacheRecipes);
        root.add("cacheRecipes", entry5);

        final JsonObject entry6 = new JsonObject();
        entry6.addProperty("desc:", "Enables error logging for recipe caching: default:false");
        entry6.addProperty("logCachingErrors", logCachingErrors);
        root.add("logCachingErrors", entry6);

        return root;
    }

    public void deserialize(JsonObject data)
    {
        disableRecipebook = data.get("disableRecipebook").getAsJsonObject().get("disableRecipebook").getAsBoolean();
        fastItemComparisons = data.get("fastItemComparisons").getAsJsonObject().get("fastItemComparisons").getAsBoolean();
        enableBetterRecipebookSorting = data.get("enableBetterRecipebookSorting").getAsJsonObject().get("enableBetterRecipebookSorting").getAsBoolean();
        smallerRecipePacket = data.get("smallerRecipePacket").getAsJsonObject().get("smallerRecipePacket").getAsBoolean();
        cacheRecipes = data.get("cacheRecipes").getAsJsonObject().get("cacheRecipes").getAsBoolean();
        recipebookShowAll = data.get("recipebookShowAll").getAsJsonObject().get("recipebookShowAll").getAsBoolean();
        logCachingErrors = data.get("logCachingErrors").getAsJsonObject().get("logCachingErrors").getAsBoolean();
    }
}
