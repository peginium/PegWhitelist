package peg.pegwhitelist;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import peg.pegwhitelist.other.Metrics;

import javax.security.auth.login.LoginException;
import java.util.Objects;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;
import static net.dv8tion.jda.api.JDABuilder.createDefault;

public class main extends JavaPlugin implements @NotNull Listener {
    private static JDA discordBot;
    public static String name;
    public static String titleDc;
    public static String descDc;
    public static String errorDc;
    public static String permDc;
    public static String statusDc;

    public static String remtitleDc;
    public static String remdescDc;

    public static boolean whPerm = false;
    public static boolean blPerm = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        //Metrics
        int pluginId = 9197;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));

        //Filemanager
        loadConfig();
        try {
            if (!getConfig().getString("version").equalsIgnoreCase("1.0")) {
                System.out.println("[PegWhitelist] Please delete config.yml to receive newest updates!");
            }
        } catch (NullPointerException e) {
            System.out.println("[PegWhitelist] Please delete config.yml to receive newest updates!");
        }

        name = getConfig().getString("discordkey");
        if (Objects.requireNonNull(name).equalsIgnoreCase("token")) {
            System.out.println("!!![PegWhitelist] Please enter a Discord bot ID in the config!!!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        String wh = getConfig().getString("whitelistperm");
        String bl = getConfig().getString("blacklistperm");
        if (Objects.requireNonNull(wh).equalsIgnoreCase("all")) {
            whPerm = false;
        } else if (wh.equalsIgnoreCase("admin")) {
            whPerm = true;
        } else {
            System.out.println("Unknown Parameter in Config at \"Whitelistperm\"! Using 'all' option");
            whPerm = false;
        }
        if (Objects.requireNonNull(bl).equalsIgnoreCase("all")) {
            blPerm = false;
        } else if (bl.equalsIgnoreCase("admin")) {
            blPerm = true;
        } else {
            System.out.println("Unknown Parameter in Config at \"blacklistperm\"! Using 'all' option");
            blPerm = false;
        }


        //Discordlogin
        try {
            discordBot = createDefault(name)
                    .addEventListeners(new DiscordListener())
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        //lang
        if (Objects.requireNonNull(getConfig().getString("lang")).equalsIgnoreCase("tr")) {
            titleDc = "whiteliste ekledim!";
            descDc = "İyi eğlenceler!";
            errorDc = "Hatalı kullanıcı adı!";
            remtitleDc = "silindi!";
            remdescDc = "Kullanıcı whitelistten silindi!";
            permDc = "Permission Eksik! (Lütfen bu permissionu bota ekletin 'Rolleri Yönet'!)";
            statusDc = "Oyuncu Çevirimiçi!";
        } else if (Objects.requireNonNull(getConfig().getString("lang")).equalsIgnoreCase("en")) {
            titleDc = "has been added!";
            descDc = "Have fun in our Server!";
            errorDc = "Your Minecraft-IGN is missing!";
            remtitleDc = "removed!";
            remdescDc = "Player can not join when Whitelist is active!";
            permDc = "Not enough Permissions! (You need 'Manage Roles')";
            statusDc = "online players!";
        } else if (Objects.requireNonNull(getConfig().getString("lang")).equalsIgnoreCase("custom")) {
            titleDc = getConfig().getString("titleDc");
            descDc = getConfig().getString("descDc");
            errorDc = getConfig().getString("errorDc");
            remtitleDc = getConfig().getString("removetitleDc");
            remdescDc = getConfig().getString("removedescDc");
            statusDc = getConfig().getString("statusDc");

            permDc = getConfig().getString("noPerms");
        } else {
            System.out.println("[PegWhitelist] Please provide a valid language (de,en,custom) in config.yml!");
        }
        System.out.println("Plugin started (discord)");
    }

    //Discord Status
    public static void DiscordStatus(){
        discordBot.getPresence().setActivity(Activity.watching(Bukkit.getServer().getOnlinePlayers().size()+ " "+ statusDc));
        System.out.println("[PegWhitelist]" + Bukkit.getServer().getOnlinePlayers().size()+ "Players Online!");
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(5);

        ScheduledFuture scheduledFuture =
                scheduledExecutorService.schedule(new Callable() {
                                                      public Object call() throws Exception {
                                                          DiscordStatus();
                                                          return "Called!";
                                                      }
                                                  },
                        10,
                        TimeUnit.SECONDS);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(5);

        ScheduledFuture scheduledFuture =
                scheduledExecutorService.schedule(new Callable() {
                                                      public Object call() throws Exception {
                                                          DiscordStatus();
                                                          return "Called!";
                                                      }
                                                  },
                        10,
                        TimeUnit.SECONDS);
    }


    /*
     */

    @Override
    public void onDisable() {
        System.out.println("Plugin shutdown [PegWhitelist])");
        discordBot.shutdownNow();
        titleDc = null;
        descDc = null;
        errorDc = null;
    }

    public void loadConfig() {
        this.saveDefaultConfig();
        saveConfig();
    }

}
