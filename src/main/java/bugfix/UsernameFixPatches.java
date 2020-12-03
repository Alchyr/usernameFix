package bugfix;


import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import javassist.CtBehavior;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class UsernameFixPatches
{
    private static SpireConfig config;
    static {
        Properties defaultLocation = new Properties();
        defaultLocation.setProperty("location", ".temp/");
        try {
            config = new SpireConfig("USERNAME_FIX", "config", defaultLocation);
            config.setString("location", config.getString("location"));
            config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SpirePatch(
            clz = SharedLibraryLoader.class,
            method = "getExtractedFile"
    )
    public static class ChangeLocation {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "idealFile" }
        )
        public static void change(SharedLibraryLoader __instance, String dirName, String fileName, @ByRef(type="java.io.File") Object[] idealFile)
        {
            String location;
            try {
                location = config.getString("location");
            }
            catch (Exception e)
            {
                location = ".temp/";
            }
            if (!(location.endsWith(File.separator) || location.endsWith("/")))
                location += "/";
            idealFile[0] = new File(location + dirName, fileName);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SharedLibraryLoader.class, "canWrite");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = SharedLibraryLoader.class,
            method = "loadFile",
            paramtypez = { String.class }
    )
    public static class ChangeGdxLocation {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "sourceCrc", "fileName", "file" }
        )
        public static void change(SharedLibraryLoader __instance, String sourcePath, String sourceCrc, String fileName, @ByRef(type="java.io.File") Object[] file)
        {
            String location;
            try {
                location = config.getString("location");
            }
            catch (Exception e)
            {
                location = ".temp/";
            }
            if (!(location.endsWith(File.separator) || location.endsWith("/")))
                location += "/";
            file[0] = new File(location + sourceCrc, fileName);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SharedLibraryLoader.class, "loadFile");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            cls = "com.codedisaster.steamworks.SteamSharedLibraryLoader",
            method = "discoverExtractLocation"
    )
    public static class ChangeSteamLocation {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "path" }
        )
        public static void change(String folderName, String fileName, @ByRef(type="java.io.File") Object[] path)
        {
            String location;
            try {
                location = config.getString("location");
            }
            catch (Exception e)
            {
                location = ".temp/";
            }
            if (!(location.endsWith(File.separator) || location.endsWith("/")))
                location += "/";
            path[0] = new File(location + folderName, fileName);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher("com.codedisaster.steamworks.SteamSharedLibraryLoader", "canWrite");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
