package net.ddns.gsgtemp.PriceEditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PriceEditor extends JavaPlugin implements Listener {
    
    HashMap<String, String> quantities = new HashMap<String, String>();
    HashMap<String, String> prices = new HashMap<String, String>();
    boolean active = false;
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("priceedit") && sender instanceof Player && ((Player)sender).getName().equals("buhiroshi0205")) {
            active = !active;
            if (active) {
                sender.sendMessage("PriceEditor activated!");
            } else {
                sender.sendMessage("PriceEditor deactivated!");
            }
        } else if (label.equalsIgnoreCase("pereload")) {
            quantities = new HashMap<String, String>();
            prices = new HashMap<String, String>();
            try {
                loadCSV(new File(getDataFolder() + File.separator + "data.csv"));
            } catch (IOException ex) {}
        }
        return false;
    }
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getDataFolder().mkdir();
        File data = new File(getDataFolder() + File.separator + "data.csv");
        try {
            if (!data.exists()) {
                getLogger().log(Level.WARNING, "CSV data non-existent! A template file has been created. Please edit that file.");
                data.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(data));
                bw.append("Name,Quantity,Sell,Buy,(Blank means NA)\n");
                bw.append("Diamond,1,75,400");
                bw.close();
            }
            loadCSV(data);
        } catch (IOException ex) {}
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (active && e.getPlayer().getName().equals("buhiroshi0205")) {
            Material itemType = e.getItem().getType();
            Material blockType = e.getClickedBlock().getType();
            if (itemType != null && itemType == Material.BEDROCK && blockType != null && blockType == Material.WALL_SIGN) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                String itemname = sign.getLine(3);
                if (sign.getLine(0).equals("Admin Shop") && quantities.containsKey(itemname)) {
                    sign.setLine(1, quantities.get(itemname));
                    sign.setLine(2, prices.get(itemname));
                    sign.update();
                    e.getPlayer().sendMessage("Price Updated!");
                } else {
                    e.getPlayer().sendMessage("Sign not an Admin Shop or data does not exist for this item!");
                }
            }
        }
    }
    
    private void loadCSV(File data) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(data));
        br.readLine();
        String line = br.readLine();
        while (line!= null) {
            String[] args = line.split(",");
            quantities.put(args[0], args[1]);
            prices.put(args[0], "S " + args[2] + ':' + args[3] + " B");
            line = br.readLine();
        }
    }
    
}
