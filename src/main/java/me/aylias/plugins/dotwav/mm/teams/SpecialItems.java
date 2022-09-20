package me.aylias.plugins.dotwav.mm.teams;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class SpecialItems {

  public static final ItemStack murdererKnife;
  public static final ItemStack returnToKnife;
  public static final ItemStack mirrorMan;
  public static final ItemStack lightsOutAbility;
  public static final ItemStack sheriffGun;
  public static final ItemStack knockbackStick;
  public static final ItemStack speedPotion;
  public static final ItemStack invisibilityPotion;
  public static final ItemStack jumpPotion;
  public static final ItemStack flashlight;
  public static final ItemStack camera;

  static {
    var knife = new ItemStack(Material.IRON_SWORD);
    var meta = knife.getItemMeta();

    meta.setDisplayName(ChatColor.RED + "Murderer's Knife");
    meta.setCustomModelData(1);

//        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.movement_speed", 1.02, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND));

    meta.setLore(Arrays.asList(
            "While Holding:",
            "- Drop to dash",
            "- Right click to throw, ",
            "  and right click again to ",
            "  tp to the thrown knife"
                              ));

    knife.setItemMeta(meta);

    murdererKnife = knife;
  }

  static {
    var knife = new ItemStack(Material.ENDER_PEARL);
    var meta = knife.getItemMeta();

    meta.setDisplayName(ChatColor.RED + "TP To Knife");
    meta.setCustomModelData(2);

    meta.setLore(Arrays.asList("While holding:", "- Right click to tp", "  to the thrown knife"));

    knife.setItemMeta(meta);

    returnToKnife = knife;
  }

  static {
    var knife = new ItemStack(Material.BLACK_DYE);
    var meta = knife.getItemMeta();

    meta.setDisplayName(ChatColor.GRAY + "Shadow Strike (Right Click)");
    meta.setCustomModelData(3);

    meta.setLore(Arrays.asList("While holding:", "- Right click to activate Shadow Strike"));

    knife.setItemMeta(meta);

    mirrorMan = knife;
  }

  static {
    var lightsOut = new ItemStack(Material.CARROT_ON_A_STICK);
    var meta = lightsOut.getItemMeta();

    meta.setDisplayName(ChatColor.GRAY + "Lights Out");
    meta.setCustomModelData(2);

    lightsOut.setItemMeta(meta);

    lightsOutAbility = lightsOut;
  }

  static {
    var gun = new ItemStack(Material.CARROT_ON_A_STICK);
    var meta = gun.getItemMeta();

    meta.setDisplayName(ChatColor.BLUE + "Sheriff’s Gun");
    meta.setCustomModelData(1);

    meta.setLore(Arrays.asList("While holding:", "- Right click to shoot", "- Drop to reload (max 3 rounds)"));

    gun.setItemMeta(meta);

    sheriffGun = gun;
  }

  static {
    var stick = new ItemStack(Material.STICK);
    var meta = stick.getItemMeta();

    meta.setDisplayName(ChatColor.GREEN + "Knockback Stick");
    meta.setCustomModelData(1);

    stick.setItemMeta(meta);

    knockbackStick = stick;
  }

  static {
    var potion = new ItemStack(Material.POTION);
    var meta = (PotionMeta) potion.getItemMeta();

    meta.setDisplayName(ChatColor.AQUA + "Speed Potion");
    meta.setCustomModelData(1);

    meta.addCustomEffect(PotionEffectType.SPEED.createEffect(200, 0), true);
    meta.setColor(Color.AQUA);

    potion.setItemMeta(meta);

    speedPotion = potion;
  }

  static {
    var potion = new ItemStack(Material.POTION);
    var meta = (PotionMeta) potion.getItemMeta();

    meta.setDisplayName(ChatColor.GRAY + "Invisibility Potion");
    meta.setCustomModelData(1);

    meta.addCustomEffect(PotionEffectType.INVISIBILITY.createEffect(200, 0), true);
    meta.setColor(Color.GRAY);

    potion.setItemMeta(meta);

    invisibilityPotion = potion;
  }

  static {
    var potion = new ItemStack(Material.POTION);
    var meta = (PotionMeta) potion.getItemMeta();

    meta.setDisplayName(ChatColor.GREEN + "Jump Potion");
    meta.setCustomModelData(1);

    meta.addCustomEffect(PotionEffectType.JUMP.createEffect(200, 0), true);
    meta.setColor(Color.GREEN);

    potion.setItemMeta(meta);

    jumpPotion = potion;
  }

  static {
    var flashlightStack = new ItemStack(Material.BLAZE_ROD);
    var meta = flashlightStack.getItemMeta();

    meta.setDisplayName(ChatColor.YELLOW + "Lantern");
    meta.setCustomModelData(1);

    flashlightStack.setItemMeta(meta);

    flashlight = flashlightStack;
  }

  static {
    var cameraStack = new ItemStack(Material.DIAMOND);
    var meta = cameraStack.getItemMeta();

    meta.setDisplayName(ChatColor.YELLOW + "Camera");
    meta.setCustomModelData(1);

    cameraStack.setItemMeta(meta);

    camera = cameraStack;
  }
}
