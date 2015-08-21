package co.marcin.novaguilds;

import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YamlEnumTest {
    private String[] ignoreConfig;

    public YamlEnumTest() {
        ignoreConfig = new String[]{
            "aliases.",
            "gguicmd",
            "groups"
        };
    }

    @Test
    public void testConfig() throws Exception {
        System.out.println();
        System.out.println("Testing config enums...");
        File configFile = new File(YamlParseTest.resourcesDirectory, "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        List<String> configEnumNames = new ArrayList<>();
        for(Config v : Config.values()) {
            configEnumNames.add(v.name());
        }

        int missingCount = 0;
        for(String key : config.getKeys(true)) {
            boolean ig = config.isConfigurationSection(key);
            for(String ignore : ignoreConfig) {
                if(key.startsWith(ignore)) {
                    ig = true;
                    break;
                }
            }

            if(!ig) {
                String name = StringUtils.replace(key, ".", "_").toUpperCase();
                if(!configEnumNames.contains(name)) {
                    if(missingCount == 0) {
                        System.out.println("Missing keys:");
                    }

                    System.out.println(" - "+name);
                    missingCount++;
                }
            }
        }

        if(missingCount == 0) {
            System.out.println("All values are present in Config enum");
        }
        else {
            throw new Exception("Found "+missingCount+" missing Config enums");
        }
    }

    @Test
    public void testMessages() throws Exception {
        System.out.println();
        System.out.println("Testing message enums...");
        File motherFile = new File(YamlParseTest.resourcesDirectory, "lang/en-en.yml");
        YamlConfiguration motherConfiguration = YamlConfiguration.loadConfiguration(motherFile);
        List<String> messageEnumNames = new ArrayList<>();
        for(Message v : Message.values()) {
            messageEnumNames.add(v.name());
        }

        int missingCount = 0;
        for(String key : motherConfiguration.getKeys(true)) {
            boolean ig = motherConfiguration.isConfigurationSection(key);
            for(String ignore : ignoreConfig) {
                if(key.startsWith(ignore)) {
                    ig = true;
                    break;
                }
            }

            if(!ig) {
                String name = StringUtils.replace(key, ".", "_").toUpperCase();
                if(!messageEnumNames.contains(name)) {
                    if(missingCount == 0) {
                        System.out.println("Missing keys:");
                    }

                    System.out.println(" - "+name);
                    missingCount++;
                }
            }
        }

        if(missingCount == 0) {
            System.out.println("All values are present in Message enum");
        }
        else {
            throw new Exception("Found "+missingCount+" missing Message enums");
        }
    }
}
