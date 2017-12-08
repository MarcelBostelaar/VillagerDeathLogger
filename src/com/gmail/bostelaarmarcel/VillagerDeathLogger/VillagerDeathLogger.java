package com.gmail.bostelaarmarcel.VillagerDeathLogger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.logging.Level;

public class VillagerDeathLogger extends JavaPlugin implements Listener {
    FileWriter logFile;
    @Override
    public void onEnable() {
        getLogger().log(Level.ALL, "Loading VillagerDeathLogger");
        try{
        logFile = new FileWriter("VillagerDeathLog.txt", true);
        }
        catch (IOException e){
            getLogger().log(Level.ALL, "Error opening VillagerDeathLog.txt. Canceling plugin loading.");
        }
        getServer().getPluginManager().registerEvents(this, this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Listener)this);
        try{
            logFile.close();
        }catch (IOException e){

        }
        super.onDisable();
    }

    @EventHandler
    public void onVillagerDeath(EntityDeathEvent event){
        LivingEntity entity = event.getEntity();
        EntityType type = entity.getType();
        if(type == EntityType.VILLAGER){
            Location location = entity.getLocation();
            String name = entity.getCustomName();
            Instant time = Instant.now();
            Collection players = Bukkit.getOnlinePlayers();
            EntityDamageEvent deathCause = entity.getLastDamageCause();
            Player killer = entity.getKiller();
            try{
                logFile.write("Villager died on: " + time.toString() + " UTC time\n");
                if(killer == null){
                    logFile.write("Villager was killed by non-player cause: " + deathCause.getCause().name() + "\n");
                }
                else{
                    logFile.write("Villager was killed by PLAYER: " + killer.getDisplayName() + "\n");
                }
                String format_location = "X: " + location.getBlockX() + " Y: " + location.getBlockY() +" Z: " + location.getBlockZ();
                logFile.write("Villager died at: " + format_location + "\n");
                logFile.write("Players online in same dimention, Distance to villager, location:" + "\n");
                for (Object somePlayer : players) {
                    Player player = (Player)somePlayer;
                    String p_name = player.getDisplayName();
                    Location p_location = player.getLocation();
                    String format_p_location = "X: " + p_location.getBlockX() + " Y: " + p_location.getBlockY() +" Z: " + p_location.getBlockZ();
                    double distance = p_location.distance(location);
                    if(p_location.getWorld() == entity.getWorld()) {
                        logFile.write(p_name + "\t\t\t" + distance + "\t\t\t" + format_p_location + "\n\n");
                    }
                }
            }
            catch (IOException e){}
        }
    }
}
